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

import org.codelibs.fess.crawler.container.StandardCrawlerContainer;
import org.codelibs.fess.crawler.exception.CrawlingAccessException;
import org.dbflute.utflute.core.PlainTestCase;

/**
 * Test class for GitClient.
 *
 * @author shinsuke
 */
public class GitClientTest extends PlainTestCase {

    public GitClient gitClient;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        StandardCrawlerContainer container = new StandardCrawlerContainer();
        container.singleton("mimeTypeHelper", org.codelibs.fess.crawler.helper.impl.MimeTypeHelperImpl.class);
        gitClient = new GitClient();
        gitClient.crawlerContainer = container;
    }

    @Override
    protected void tearDown() throws Exception {
        gitClient.close();
        super.tearDown();
    }

    public void test_init() {
        gitClient.init();
        assertNotNull(gitClient);
        assertNotNull(gitClient.getLocalRepoDir());
    }

    public void test_GitInfo() {
        GitClient.GitInfo info = new GitClient.GitInfo("https://github.com/user/repo.git", "master", "src/Main.java");
        assertEquals("https://github.com/user/repo.git", info.getRepositoryUrl());
        assertEquals("master", info.getBranch());
        assertEquals("src/Main.java", info.getPath());
        assertEquals("Main.java", info.getFilename());
        assertNotNull(info.getRepositoryName());
    }

    public void test_parseGitUri() {
        gitClient.init();

        GitClient.GitInfo info = gitClient.parseGitUri("git://https://github.com/user/repo.git/master/src/Main.java");
        assertEquals("https://github.com/user/repo.git", info.getRepositoryUrl());
        assertEquals("master", info.getBranch());
        assertEquals("src/Main.java", info.getPath());

        info = gitClient.parseGitUri("git://https://github.com/user/repo.git/develop");
        assertEquals("https://github.com/user/repo.git", info.getRepositoryUrl());
        assertEquals("develop", info.getBranch());
        assertEquals("", info.getPath());
    }

    public void test_parseGitUri_invalid() {
        gitClient.init();

        try {
            gitClient.parseGitUri("http://example.com/file.txt");
            fail("Should throw CrawlingAccessException for invalid scheme");
        } catch (final CrawlingAccessException e) {
            assertTrue(e.getMessage().contains("Invalid Git URI"));
        }

        try {
            gitClient.parseGitUri("git://invalid");
            fail("Should throw CrawlingAccessException for invalid format");
        } catch (final CrawlingAccessException e) {
            assertTrue(e.getMessage().contains("Invalid Git URI format"));
        }
    }

    public void test_GitAuthentication() {
        final GitAuthentication auth = new GitAuthentication();
        auth.setServer("https://github\\.com/.*");
        auth.setUsername("testuser");
        auth.setPassword("testpass");

        assertEquals("testuser", auth.getUsername());
        assertEquals("testpass", auth.getPassword());
        assertNotNull(auth.getServerPattern());
    }

    public void test_GitAuthenticationHolder() {
        final GitAuthenticationHolder holder = new GitAuthenticationHolder();

        final GitAuthentication auth1 = new GitAuthentication();
        auth1.setServer("https://github\\.com/.*");
        auth1.setUsername("user1");

        final GitAuthentication auth2 = new GitAuthentication();
        auth2.setServer("https://gitlab\\.com/.*");
        auth2.setUsername("user2");

        holder.add(auth1);
        holder.add(auth2);

        GitAuthentication found = holder.get("https://github.com/user/repo.git");
        assertNotNull(found);
        assertEquals("user1", found.getUsername());

        found = holder.get("https://gitlab.com/user/repo.git");
        assertNotNull(found);
        assertEquals("user2", found.getUsername());

        found = holder.get("https://unknown.com/user/repo.git");
        assertNull(found);
    }

    public void test_charsetGetterSetter() {
        gitClient.setCharset("UTF-16");
        assertEquals("UTF-16", gitClient.getCharset());
    }

    public void test_getMimeType() {
        gitClient.init();

        assertEquals("text/plain", gitClient.getMimeType("file.txt"));
        assertEquals("text/x-java", gitClient.getMimeType("Main.java"));
        assertEquals("application/xml", gitClient.getMimeType("config.xml"));
        assertEquals("application/json", gitClient.getMimeType("data.json"));
        assertEquals("application/octet-stream", gitClient.getMimeType("file.bin"));
    }
}
