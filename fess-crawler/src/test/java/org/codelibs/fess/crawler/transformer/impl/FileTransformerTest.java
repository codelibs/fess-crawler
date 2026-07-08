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
package org.codelibs.fess.crawler.transformer.impl;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.codelibs.core.io.FileUtil;
import org.codelibs.fess.crawler.Constants;
import org.codelibs.fess.crawler.entity.AccessResultDataImpl;
import org.codelibs.fess.crawler.entity.ResponseData;
import org.codelibs.fess.crawler.entity.ResultData;
import org.codelibs.fess.crawler.exception.CrawlerSystemException;
import org.dbflute.utflute.core.PlainTestCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

/**
 * @author shinsuke
 *
 */
public class FileTransformerTest extends PlainTestCase {
    public FileTransformer fileTransformer;

    @Override
    protected void setUp(final TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        fileTransformer = new FileTransformer();
        fileTransformer.setName("fileTransformer");
        Map<String, String> featureMap = newHashMap();
        featureMap.put("http://xml.org/sax/features/namespaces", "false");
        fileTransformer.setFeatureMap(featureMap);
        Map<String, String> propertyMap = newHashMap();
        fileTransformer.setPropertyMap(propertyMap);
        Map<String, String> childUrlRuleMap = newHashMap();
        childUrlRuleMap.put("//A", "href");
        childUrlRuleMap.put("//AREA", "href");
        childUrlRuleMap.put("//FRAME", "src");
        childUrlRuleMap.put("//IFRAME", "src");
        childUrlRuleMap.put("//IMG", "src");
        childUrlRuleMap.put("//LINK", "href");
        childUrlRuleMap.put("//SCRIPT", "src");
        fileTransformer.setChildUrlRuleMap(childUrlRuleMap);
    }

    protected void setBaseDir() throws IOException {
        fileTransformer.baseDir = File.createTempFile("crawler-", "");
        fileTransformer.baseDir.delete();
        fileTransformer.baseDir.mkdirs();
        fileTransformer.baseDir.deleteOnExit();
    }

    @Test
    public void test_name() {
        assertEquals("fileTransformer", fileTransformer.getName());
    }

    @Test
    public void test_getFilePath() {
        String url;

        url = "http://www.example.com/";
        assertEquals("http_CLN_/www.example.com/index.html", fileTransformer.getFilePath(url));

        url = "http://www.example.com/action?a=1";
        assertEquals("http_CLN_/www.example.com/action_QUEST_a=1", fileTransformer.getFilePath(url));

        url = "http://www.example.com/action?a=1&b=2";
        assertEquals("http_CLN_/www.example.com/action_QUEST_a=1_AMP_b=2", fileTransformer.getFilePath(url));
    }

    @Test
    public void test_transform() throws Exception {
        final byte[] data = new String("xyz").getBytes();
        final ResponseData responseData = new ResponseData();
        responseData.setUrl("http://www.example.com/submit?a=1&b=2");
        responseData.setResponseBody(data);
        responseData.setCharSet("UTF-8");
        setBaseDir();
        final ResultData resultData = fileTransformer.transform(responseData);
        assertEquals("http_CLN_/www.example.com/submit_QUEST_a=1_AMP_b=2", new String(resultData.getData(), "UTF-8"));
        final File file = new File(fileTransformer.baseDir, new String(resultData.getData(), "UTF-8"));
        assertEquals("xyz", new String(FileUtil.readBytes(file)));
    }

    @Test
    public void test_storeData_concurrent_sameUrl() throws Exception {
        setBaseDir();

        // All threads crawl the SAME url/path, so createFile(...) must hand each of them a
        // distinct target file (via "_0", "_1", ... suffixes) instead of letting two threads
        // race for, and overwrite, the same file.
        final String url = "http://www.example.com/concurrent";
        final String nominalPath = fileTransformer.getFilePath(url);
        final int threadCount = 20;
        final ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        final CountDownLatch readyLatch = new CountDownLatch(threadCount);
        final CountDownLatch startLatch = new CountDownLatch(1);
        final CountDownLatch doneLatch = new CountDownLatch(threadCount);
        final List<Throwable> failures = new CopyOnWriteArrayList<>();

        try {
            for (int i = 0; i < threadCount; i++) {
                final String content = "content-" + i;
                executorService.execute(() -> {
                    try {
                        readyLatch.countDown();
                        startLatch.await();

                        final ResponseData responseData = new ResponseData();
                        responseData.setUrl(url);
                        responseData.setResponseBody(content.getBytes("UTF-8"));
                        responseData.setCharSet("UTF-8");
                        final ResultData resultData = new ResultData();
                        fileTransformer.storeData(responseData, resultData);
                    } catch (final Throwable t) {
                        failures.add(t);
                    } finally {
                        doneLatch.countDown();
                    }
                });
            }

            // wait until all threads are ready, then release them all at once
            readyLatch.await();
            startLatch.countDown();
            assertTrue(doneLatch.await(30, TimeUnit.SECONDS));
        } finally {
            executorService.shutdown();
        }

        assertTrue(failures.isEmpty());

        // the reserved target files are the nominal path plus "_0".."_(threadCount-2)" suffixes;
        // every one of them must exist and hold content produced by exactly one thread.
        final File nominalFile = new File(fileTransformer.baseDir, nominalPath);
        final List<File> candidateFiles = new ArrayList<>();
        candidateFiles.add(nominalFile);
        for (int i = 0; i < threadCount - 1; i++) {
            candidateFiles.add(new File(nominalFile.getParentFile(), nominalFile.getName() + "_" + i));
        }

        final Set<String> seenContents = new HashSet<>();
        int existingCount = 0;
        for (final File file : candidateFiles) {
            if (file.exists()) {
                existingCount++;
                final String content = new String(FileUtil.readBytes(file));
                assertTrue(content.startsWith("content-"));
                // fails if two threads wrote to the same file (one thread's content clobbered another's)
                assertTrue(seenContents.add(content));
            }
        }
        assertEquals(threadCount, existingCount);
        assertEquals(threadCount, seenContents.size());

        // no unexpected extra file (e.g. "_<threadCount-1>") was created beyond what threadCount threads need
        assertFalse(new File(nominalFile.getParentFile(), nominalFile.getName() + "_" + (threadCount - 1)).exists());
    }

    @Test
    public void test_storeData_concurrent_copyRunsOutsideLock() throws Exception {
        setBaseDir();

        // Proves the content copy is no longer serialized through the transformer-wide lock:
        // each thread's InputStream blocks on a CyclicBarrier the first time it is read. If
        // storeData still ran CopyUtil.copy() inside the synchronized section (as before this
        // fix), only one thread at a time could ever reach that read() call, so the barrier
        // (which needs all threadCount parties) would never trip and every call would fail
        // with a timeout/broken-barrier error. With the copy running outside the lock, all
        // threads reach the barrier together and it trips normally.
        final int threadCount = 6;
        final CyclicBarrier barrier = new CyclicBarrier(threadCount);
        final ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        final List<Throwable> failures = new CopyOnWriteArrayList<>();
        final CountDownLatch doneLatch = new CountDownLatch(threadCount);

        try {
            for (int i = 0; i < threadCount; i++) {
                final int idx = i;
                executorService.execute(() -> {
                    try {
                        final byte[] data = ("data-" + idx).getBytes("UTF-8");
                        final ResponseData responseData = new ResponseData() {
                            @Override
                            public InputStream getResponseBody() {
                                return new BarrierInputStream(new ByteArrayInputStream(data), barrier);
                            }
                        };
                        responseData.setUrl("http://www.example.com/barrier" + idx);
                        responseData.setCharSet("UTF-8");
                        final ResultData resultData = new ResultData();
                        fileTransformer.storeData(responseData, resultData);
                    } catch (final Throwable t) {
                        failures.add(t);
                    } finally {
                        doneLatch.countDown();
                    }
                });
            }

            assertTrue(doneLatch.await(20, TimeUnit.SECONDS));
        } finally {
            executorService.shutdown();
        }

        assertTrue(failures.isEmpty());
    }

    /**
     * Test-only InputStream that blocks on a {@link CyclicBarrier} the first time it is read,
     * used to detect whether concurrent readers are able to be "in flight" at the same time.
     */
    private static final class BarrierInputStream extends InputStream {
        private final InputStream delegate;
        private final CyclicBarrier barrier;
        private boolean awaited;

        BarrierInputStream(final InputStream delegate, final CyclicBarrier barrier) {
            this.delegate = delegate;
            this.barrier = barrier;
        }

        @Override
        public int read() throws IOException {
            awaitOnce();
            return delegate.read();
        }

        @Override
        public int read(final byte[] b, final int off, final int len) throws IOException {
            awaitOnce();
            return delegate.read(b, off, len);
        }

        private void awaitOnce() throws IOException {
            if (!awaited) {
                awaited = true;
                try {
                    barrier.await(10, TimeUnit.SECONDS);
                } catch (final Exception e) {
                    throw new IOException(e);
                }
            }
        }
    }

    @Test
    public void test_createFile() throws Exception {
        fileTransformer.baseDir = File.createTempFile("crawler-", "");
        fileTransformer.baseDir.delete();
        fileTransformer.baseDir.mkdirs();
        fileTransformer.baseDir.deleteOnExit();

        String path;
        File file;
        File resultFile;

        path = "hoge.html";
        file = fileTransformer.createFile(path);
        resultFile = new File(fileTransformer.baseDir, path);
        assertEquals(resultFile, file);
        FileUtil.writeBytes(file.getAbsolutePath(), "abc".getBytes());

        path = "foo1/hoge.html";
        file = fileTransformer.createFile(path);
        resultFile = new File(fileTransformer.baseDir, path);
        assertEquals(resultFile, file);
        FileUtil.writeBytes(file.getAbsolutePath(), "abc".getBytes());

        path = "foo1/foo2/hoge.html";
        file = fileTransformer.createFile(path);
        resultFile = new File(fileTransformer.baseDir, path);
        assertEquals(resultFile, file);
        FileUtil.writeBytes(file.getAbsolutePath(), "abc".getBytes());

        path = "hoge.html";
        file = fileTransformer.createFile(path);
        resultFile = new File(fileTransformer.baseDir, path + "_0");
        assertEquals(resultFile, file);
        FileUtil.writeBytes(file.getAbsolutePath(), "abc".getBytes());

        path = "hoge.html";
        file = fileTransformer.createFile(path);
        resultFile = new File(fileTransformer.baseDir, path + "_1");
        assertEquals(resultFile, file);
        FileUtil.writeBytes(file.getAbsolutePath(), "abc".getBytes());

        path = "hoge.html/hoge2.html";
        file = fileTransformer.createFile(path);
        resultFile = new File(fileTransformer.baseDir, "hoge.html_2" + File.separator + "hoge2.html");
        assertEquals(resultFile, file);
        FileUtil.writeBytes(file.getAbsolutePath(), "abc".getBytes());

        path = "hoge.html/hoge3.html";
        file = fileTransformer.createFile(path);
        resultFile = new File(fileTransformer.baseDir, "hoge.html_2" + File.separator + "hoge3.html");
        assertEquals(resultFile, file);
        FileUtil.writeBytes(file.getAbsolutePath(), "abc".getBytes());

        path = "hoge.html/hoge2.html";
        file = fileTransformer.createFile(path);
        resultFile = new File(fileTransformer.baseDir, "hoge.html_2" + File.separator + "hoge2.html_0");
        assertEquals(resultFile, file);
        FileUtil.writeBytes(file.getAbsolutePath(), "abc".getBytes());
    }

    @Test
    public void test_getData() throws Exception {
        final AccessResultDataImpl accessResultDataImpl = new AccessResultDataImpl();
        accessResultDataImpl.setData("hoge.txt".getBytes());
        accessResultDataImpl.setEncoding(Constants.UTF_8);
        accessResultDataImpl.setTransformerName("fileTransformer");

        setBaseDir();

        final Object obj = fileTransformer.getData(accessResultDataImpl);
        assertTrue(obj instanceof File);
        assertEquals(new File(fileTransformer.baseDir, "hoge.txt"), obj);
    }

    @Test
    public void test_getData_wrongName() throws Exception {
        final AccessResultDataImpl accessResultDataImpl = new AccessResultDataImpl();
        accessResultDataImpl.setData("hoge.txt".getBytes());
        accessResultDataImpl.setEncoding(Constants.UTF_8);
        accessResultDataImpl.setTransformerName("transformer");

        setBaseDir();

        try {
            final Object obj = fileTransformer.getData(accessResultDataImpl);
            fail();
        } catch (final CrawlerSystemException e) {}
    }

    @Test
    public void test_getData_nullData() throws Exception {
        final AccessResultDataImpl accessResultDataImpl = new AccessResultDataImpl();
        accessResultDataImpl.setData(null);
        accessResultDataImpl.setEncoding(Constants.UTF_8);
        accessResultDataImpl.setTransformerName("fileTransformer");

        setBaseDir();

        final Object obj = fileTransformer.getData(accessResultDataImpl);
        assertNull(obj);
    }
}
