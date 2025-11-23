/*
 * Copyright 2012-2025 CodeLibs Project and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.codelibs.fess.crawler.client.git;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.io.CloseableUtil;
import org.codelibs.core.io.CopyUtil;
import org.codelibs.core.io.FileUtil;
import org.codelibs.core.io.InputStreamUtil;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.core.timer.TimeoutManager;
import org.codelibs.core.timer.TimeoutTask;
import org.codelibs.fess.crawler.Constants;
import org.codelibs.fess.crawler.builder.RequestDataBuilder;
import org.codelibs.fess.crawler.client.AbstractCrawlerClient;
import org.codelibs.fess.crawler.client.AccessTimeoutTarget;
import org.codelibs.fess.crawler.entity.RequestData;
import org.codelibs.fess.crawler.entity.ResponseData;
import org.codelibs.fess.crawler.exception.ChildUrlsException;
import org.codelibs.fess.crawler.exception.CrawlerSystemException;
import org.codelibs.fess.crawler.exception.CrawlingAccessException;
import org.codelibs.fess.crawler.exception.MaxLengthExceededException;
import org.codelibs.fess.crawler.helper.ContentLengthHelper;
import org.codelibs.fess.crawler.helper.MimeTypeHelper;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.eclipse.jgit.treewalk.TreeWalk;

import jakarta.annotation.Resource;

/**
 * GitClient is a crawler client implementation for accessing resources from Git repositories.
 * It extends {@link AbstractCrawlerClient} and provides methods to retrieve content and metadata
 * from Git repositories. The client supports various configurations, including authentication.
 *
 * <p>
 * The class uses JGit library for Git operations.
 * </p>
 *
 * <p>
 * URL format: git://repository-url/branch/path/to/file
 * Example: git://https://github.com/user/repo/master/src/Main.java
 * </p>
 *
 * @author shinsuke
 */
public class GitClient extends AbstractCrawlerClient {

    /** Logger instance for this class */
    private static final Logger logger = LogManager.getLogger(GitClient.class);

    /** Property name for Git authentications */
    public static final String GIT_AUTHENTICATIONS_PROPERTY = "gitAuthentications";

    /** Property name for local repository directory */
    public static final String LOCAL_REPO_DIR_PROPERTY = "localRepoDir";

    /** Character encoding for Git operations */
    protected String charset = Constants.UTF_8;

    /** Helper for managing content length limits */
    @Resource
    protected ContentLengthHelper contentLengthHelper;

    /** The Git authentication holder */
    protected volatile GitAuthenticationHolder gitAuthenticationHolder;

    /** Cache of opened Git repositories */
    protected final Map<String, Git> gitRepositoryCache = new ConcurrentHashMap<>();

    /** Local directory for storing cloned repositories */
    protected File localRepoDir;

    /**
     * Creates a new GitClient instance.
     */
    public GitClient() {
        // Default constructor
    }

    @Override
    public synchronized void init() {
        if (gitAuthenticationHolder != null) {
            return;
        }

        if (logger.isDebugEnabled()) {
            logger.debug("Initializing GitClient...");
        }

        super.init();

        // Initialize local repository directory
        final String localRepoDirPath = getInitParameter(LOCAL_REPO_DIR_PROPERTY, null, String.class);
        if (StringUtil.isNotBlank(localRepoDirPath)) {
            localRepoDir = new File(localRepoDirPath);
        } else {
            try {
                localRepoDir = File.createTempFile("git-crawler-", "-repos");
                if (!localRepoDir.delete() || !localRepoDir.mkdirs()) {
                    throw new CrawlerSystemException("Failed to create local repository directory: " + localRepoDir.getAbsolutePath());
                }
            } catch (final Exception e) {
                throw new CrawlerSystemException("Failed to create temporary directory for Git repositories", e);
            }
        }

        // Initialize Git authentication holder
        final GitAuthenticationHolder holder = new GitAuthenticationHolder();
        final GitAuthentication[] gitAuthentications =
                getInitParameter(GIT_AUTHENTICATIONS_PROPERTY, new GitAuthentication[0], GitAuthentication[].class);
        if (gitAuthentications != null) {
            for (final GitAuthentication gitAuthentication : gitAuthentications) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Adding GitAuthentication: {}", gitAuthentication);
                }
                holder.add(gitAuthentication);
            }
        }
        gitAuthenticationHolder = holder;

        if (logger.isInfoEnabled()) {
            logger.info("Git client initialized successfully: localRepoDir={}", localRepoDir.getAbsolutePath());
        }
    }

    @Override
    public void close() {
        if (gitAuthenticationHolder == null) {
            return;
        }
        if (logger.isDebugEnabled()) {
            logger.debug("Closing GitClient...");
        }

        // Close all cached Git repositories
        for (final Git git : gitRepositoryCache.values()) {
            try {
                git.close();
            } catch (final Exception e) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Failed to close Git repository", e);
                }
            }
        }
        gitRepositoryCache.clear();

        gitAuthenticationHolder = null;

        if (logger.isDebugEnabled()) {
            logger.debug("Git client closed");
        }
    }

    @Override
    public ResponseData doGet(final String uri) {
        return processRequest(uri, true);
    }

    @Override
    public ResponseData doHead(final String url) {
        try {
            final ResponseData responseData = processRequest(url, false);
            responseData.setMethod(Constants.HEAD_METHOD);
            return responseData;
        } catch (final ChildUrlsException e) {
            return null;
        }
    }

    /**
     * Processes a Git request to retrieve data from the specified URI.
     *
     * @param uri The URI to retrieve data from
     * @param includeContent Whether to include the actual content in the response
     * @return The response data containing the retrieved information
     * @throws CrawlingAccessException If the Git request fails
     */
    protected ResponseData processRequest(final String uri, final boolean includeContent) {
        if (gitAuthenticationHolder == null) {
            init();
        }

        // start
        AccessTimeoutTarget accessTimeoutTarget = null;
        TimeoutTask accessTimeoutTask = null;
        if (accessTimeout != null) {
            accessTimeoutTarget = new AccessTimeoutTarget(Thread.currentThread());
            accessTimeoutTask = TimeoutManager.getInstance().addTimeoutTarget(accessTimeoutTarget, accessTimeout, false);
        }

        try {
            return getResponseData(uri, includeContent);
        } finally {
            if (accessTimeoutTarget != null) {
                accessTimeoutTarget.stop();
                if (accessTimeoutTask != null && !accessTimeoutTask.isCanceled()) {
                    accessTimeoutTask.cancel();
                }
            }
        }
    }

    /**
     * Retrieves response data from the Git repository for the specified URI.
     *
     * @param uri The URI to retrieve data from
     * @param includeContent Whether to include the actual content in the response
     * @return The response data containing the retrieved information
     * @throws CrawlingAccessException If the Git operation fails
     */
    protected ResponseData getResponseData(final String uri, final boolean includeContent) {
        if (logger.isDebugEnabled()) {
            logger.debug("Accessing Git resource: uri={}, includeContent={}", uri, includeContent);
        }

        final ResponseData responseData = new ResponseData();
        try {
            responseData.setMethod(includeContent ? Constants.GET_METHOD : Constants.HEAD_METHOD);
            responseData.setUrl(uri);

            final GitInfo gitInfo = parseGitUri(uri);
            final Git git = getOrCloneRepository(gitInfo);
            final Repository repository = git.getRepository();

            // Get the commit for the specified branch/ref
            final Ref ref = repository.exactRef("refs/heads/" + gitInfo.getBranch());
            if (ref == null) {
                responseData.setHttpStatusCode(Constants.NOT_FOUND_STATUS_CODE);
                responseData.setCharSet(charset);
                responseData.setContentLength(0);
                return responseData;
            }

            final RevWalk revWalk = new RevWalk(repository);
            final RevCommit commit = revWalk.parseCommit(ref.getObjectId());
            final RevTree tree = commit.getTree();

            if (StringUtil.isBlank(gitInfo.getPath()) || "/".equals(gitInfo.getPath())) {
                // Root directory
                return processDirectory(uri, includeContent, responseData, repository, tree, gitInfo, "");
            }

            final TreeWalk treeWalk = TreeWalk.forPath(repository, gitInfo.getPath(), tree);
            if (treeWalk == null) {
                responseData.setHttpStatusCode(Constants.NOT_FOUND_STATUS_CODE);
                responseData.setCharSet(charset);
                responseData.setContentLength(0);
                return responseData;
            }

            if (treeWalk.isSubtree()) {
                // Directory
                treeWalk.enterSubtree();
                return processDirectory(uri, includeContent, responseData, repository, tree, gitInfo, gitInfo.getPath());
            } else {
                // File
                return processFile(uri, includeContent, responseData, repository, treeWalk, gitInfo, commit);
            }

        } catch (final CrawlerSystemException e) {
            CloseableUtil.closeQuietly(responseData);
            throw e;
        } catch (final Exception e) {
            CloseableUtil.closeQuietly(responseData);
            throw new CrawlingAccessException("Could not access " + uri, e);
        }
    }

    /**
     * Processes a directory in the Git repository.
     */
    protected ResponseData processDirectory(final String uri, final boolean includeContent, final ResponseData responseData,
            final Repository repository, final RevTree tree, final GitInfo gitInfo, final String path) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("Processing Git directory: path={}", path);
        }

        final Set<RequestData> requestDataSet = new HashSet<>();
        if (includeContent) {
            final TreeWalk treeWalk = new TreeWalk(repository);
            treeWalk.addTree(tree);
            treeWalk.setRecursive(false);

            if (StringUtil.isNotBlank(path)) {
                treeWalk.setFilter(org.eclipse.jgit.treewalk.filter.PathFilter.create(path));
                if (treeWalk.next()) {
                    treeWalk.enterSubtree();
                }
            }

            while (treeWalk.next()) {
                final String childPath = treeWalk.getPathString();
                final String childUri = gitInfo.toChildUrl(childPath);
                requestDataSet.add(RequestDataBuilder.newRequestData().get().url(childUri).build());
            }
        }
        throw new ChildUrlsException(requestDataSet, this.getClass().getName() + "#getResponseData");
    }

    /**
     * Processes a file in the Git repository.
     */
    protected ResponseData processFile(final String uri, final boolean includeContent, final ResponseData responseData,
            final Repository repository, final TreeWalk treeWalk, final GitInfo gitInfo, final RevCommit commit) throws Exception {
        final ObjectId objectId = treeWalk.getObjectId(0);
        final ObjectLoader loader = repository.open(objectId);

        responseData.setHttpStatusCode(Constants.OK_STATUS_CODE);
        responseData.setCharSet(charset);
        responseData.setContentLength(loader.getSize());
        checkMaxContentLength(responseData);

        responseData.setLastModified(new Date(commit.getCommitTime() * 1000L));

        if (contentLengthHelper != null) {
            final String mimeType = getMimeType(gitInfo.getFilename());
            final long maxLength = contentLengthHelper.getMaxLength(mimeType);
            if (responseData.getContentLength() > maxLength) {
                throw new MaxLengthExceededException("The content length (" + responseData.getContentLength() + " byte) is over "
                        + maxLength + " byte. The url is " + uri);
            }
        }

        if (includeContent) {
            if (loader.getSize() < maxCachedContentSize) {
                try (InputStream in = loader.openStream()) {
                    responseData.setResponseBody(InputStreamUtil.getBytes(in));
                }
            } else {
                File outputFile = null;
                try {
                    outputFile = createTempFile("crawler-GitClient-", ".out", null);
                    try (InputStream in = loader.openStream()) {
                        CopyUtil.copy(in, outputFile);
                    }
                    responseData.setResponseBody(outputFile, true);
                } catch (final Exception e) {
                    logger.warn("Failed to write Git file content to temp file: uri={}, size={}, tempFile={}", uri, loader.getSize(),
                            outputFile != null ? outputFile.getAbsolutePath() : "null", e);
                    responseData.setHttpStatusCode(Constants.SERVER_ERROR_STATUS_CODE);
                    FileUtil.deleteInBackground(outputFile);
                }
            }

            final MimeTypeHelper mimeTypeHelper = crawlerContainer.getComponent("mimeTypeHelper");
            try (final InputStream is = responseData.getResponseBody()) {
                responseData.setMimeType(mimeTypeHelper.getContentType(is, gitInfo.getFilename()));
            } catch (final Exception e) {
                responseData.setMimeType(mimeTypeHelper.getContentType(null, gitInfo.getFilename()));
            }
        }

        return responseData;
    }

    /**
     * Gets or clones a Git repository.
     */
    protected Git getOrCloneRepository(final GitInfo gitInfo) throws GitAPIException {
        final String cacheKey = gitInfo.getRepositoryUrl();
        Git git = gitRepositoryCache.get(cacheKey);
        if (git != null) {
            return git;
        }

        synchronized (gitRepositoryCache) {
            git = gitRepositoryCache.get(cacheKey);
            if (git != null) {
                return git;
            }

            final File repoDir = new File(localRepoDir, gitInfo.getRepositoryName());
            final GitAuthentication auth = gitAuthenticationHolder.get(gitInfo.getRepositoryUrl());

            try {
                if (repoDir.exists()) {
                    // Open existing repository
                    final Repository repository =
                            new FileRepositoryBuilder().setGitDir(new File(repoDir, ".git")).readEnvironment().findGitDir().build();
                    git = new Git(repository);
                } else {
                    // Clone repository
                    if (logger.isInfoEnabled()) {
                        logger.info("Cloning Git repository: url={}, dir={}", gitInfo.getRepositoryUrl(), repoDir.getAbsolutePath());
                    }
                    git = Git.cloneRepository()
                            .setURI(gitInfo.getRepositoryUrl())
                            .setDirectory(repoDir)
                            .setCredentialsProvider(createCredentialsProvider(auth))
                            .call();
                }

                gitRepositoryCache.put(cacheKey, git);
                return git;
            } catch (final Exception e) {
                throw new CrawlingAccessException("Failed to clone or open Git repository: " + gitInfo.getRepositoryUrl(), e);
            }
        }
    }

    /**
     * Creates a credentials provider for Git authentication.
     */
    protected UsernamePasswordCredentialsProvider createCredentialsProvider(final GitAuthentication auth) {
        if (auth != null && StringUtil.isNotBlank(auth.getUsername())) {
            return new UsernamePasswordCredentialsProvider(auth.getUsername(), auth.getPassword());
        }
        return null;
    }

    /**
     * Parses a Git URI into components.
     */
    protected GitInfo parseGitUri(final String uri) {
        if (!uri.startsWith("git://")) {
            throw new CrawlingAccessException("Invalid Git URI: " + uri);
        }

        final String remainder = uri.substring(6); // Remove "git://"
        final String[] parts = remainder.split("/", 3);

        if (parts.length < 2) {
            throw new CrawlingAccessException("Invalid Git URI format. Expected: git://repository-url/branch[/path]: " + uri);
        }

        final String repositoryUrl = parts[0];
        final String branch = parts[1];
        final String path = parts.length > 2 ? parts[2] : "";

        return new GitInfo(repositoryUrl, branch, path);
    }

    /**
     * Gets the MIME type for a filename.
     */
    protected String getMimeType(final String filename) {
        final MimeTypeHelper mimeTypeHelper = crawlerContainer.getComponent("mimeTypeHelper");
        return mimeTypeHelper.getContentType(null, filename);
    }

    /**
     * Inner class to hold Git URI information.
     */
    public static class GitInfo {
        private final String repositoryUrl;
        private final String branch;
        private final String path;

        public GitInfo(final String repositoryUrl, final String branch, final String path) {
            this.repositoryUrl = repositoryUrl;
            this.branch = branch;
            this.path = path;
        }

        public String getRepositoryUrl() {
            return repositoryUrl;
        }

        public String getBranch() {
            return branch;
        }

        public String getPath() {
            return path;
        }

        public String getRepositoryName() {
            try {
                final URI uri = new URI(repositoryUrl);
                String name = uri.getPath();
                if (name.endsWith(".git")) {
                    name = name.substring(0, name.length() - 4);
                }
                if (name.startsWith("/")) {
                    name = name.substring(1);
                }
                return name.replace("/", "_");
            } catch (final Exception e) {
                return Integer.toHexString(repositoryUrl.hashCode());
            }
        }

        public String getFilename() {
            if (StringUtil.isBlank(path)) {
                return "";
            }
            final int index = path.lastIndexOf('/');
            if (index >= 0 && index < path.length() - 1) {
                return path.substring(index + 1);
            }
            return path;
        }

        public String toChildUrl(final String childPath) {
            return "git://" + repositoryUrl + "/" + branch + "/" + childPath;
        }
    }

    /**
     * Gets the character encoding used for Git operations.
     *
     * @return The character encoding
     */
    public String getCharset() {
        return charset;
    }

    /**
     * Sets the character encoding used for Git operations.
     *
     * @param charset The character encoding to set
     */
    public void setCharset(final String charset) {
        this.charset = charset;
    }

    /**
     * Sets the Git authentication holder.
     *
     * @param gitAuthenticationHolder The Git authentication holder to set
     */
    public void setGitAuthenticationHolder(final GitAuthenticationHolder gitAuthenticationHolder) {
        this.gitAuthenticationHolder = gitAuthenticationHolder;
    }

    /**
     * Gets the local repository directory.
     *
     * @return The local repository directory
     */
    public File getLocalRepoDir() {
        return localRepoDir;
    }

    /**
     * Sets the local repository directory.
     *
     * @param localRepoDir The local repository directory to set
     */
    public void setLocalRepoDir(final File localRepoDir) {
        this.localRepoDir = localRepoDir;
    }
}
