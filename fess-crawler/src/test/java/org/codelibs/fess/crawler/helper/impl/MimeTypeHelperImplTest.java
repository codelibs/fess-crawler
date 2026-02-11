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
package org.codelibs.fess.crawler.helper.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.codelibs.core.io.ResourceUtil;
import org.codelibs.fess.crawler.container.StandardCrawlerContainer;
import org.codelibs.fess.crawler.exception.MimeTypeException;
import org.codelibs.fess.crawler.helper.MimeTypeHelper;
import org.dbflute.utflute.core.PlainTestCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

/**
 * @author shinsuke
 *
 */
public class MimeTypeHelperImplTest extends PlainTestCase {

    private StandardCrawlerContainer container;

    @Override
    protected void setUp(final TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        container = new StandardCrawlerContainer().singleton("mimeTypeHelper", MimeTypeHelperImpl.class);
    }

    @Test
    public void test_getContentType_filenameFirst() throws IOException {
        final MimeTypeHelperImpl mimeTypeHelper = container.getComponent("mimeTypeHelper");
        mimeTypeHelper.useFilename = true;
        assertContentType("text/plain", "test/text1.txt", "hoge.txt");
        assertContentType("text/plain", null, "hoge.txt");
        assertContentType("text/html", "html/test1.html", "hoge.html");
        assertContentType("text/html", "html/test1.html", "hoge.htm");
        assertContentType("text/html", "html/test1.shtml", "hoge.shtml");

        assertContentType("application/msword", "test/text1.txt", "hoge.doc");
        assertContentType("application/msword", "extractor/msoffice/test.doc", "hoge.doc");
        assertContentType("application/vnd.ms-excel", "test/text1.txt", "hoge.xls");
        assertContentType("application/vnd.ms-excel", "extractor/msoffice/test.xls", "hoge.xls");
        assertContentType("application/vnd.ms-powerpoint", "test/text1.txt", "hoge.ppt");
        assertContentType("application/vnd.ms-powerpoint", "extractor/msoffice/test.ppt", "hoge.ppt");

        assertContentType("application/vnd.openxmlformats-officedocument.wordprocessingml.document", "test/text1.txt", "hoge.docx");
        assertContentType("application/vnd.openxmlformats-officedocument.wordprocessingml.document", "extractor/msoffice/test.docx",
                "hoge.docx");
        assertContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "test/text1.txt", "hoge.xlsx");
        assertContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "extractor/msoffice/test.xlsx", "hoge.xlsx");
        assertContentType("application/vnd.openxmlformats-officedocument.presentationml.presentation", "test/text1.txt", "hoge.pptx");
        assertContentType("application/vnd.openxmlformats-officedocument.presentationml.presentation", "extractor/msoffice/test.pptx",
                "hoge.pptx");

        assertContentType("image/jpeg", null, "hoge.jpg");
        assertContentType("image/gif", null, "hoge.gif");

        assertContentType("application/pdf", "extractor/test.pdf", "hoge.pdf");

        assertContentType("application/gzip", "extractor/gz/test.tar.gz", "hoge.tar.gz");
        assertContentType("application/zip", "extractor/zip/test.zip", "hoge.zip");
        assertContentType("application/x-lharc", "extractor/lha/test.lzh", "hoge.lzh"); // TODO is it correct?

        assertContentType("application/xml", "extractor/test.mm", "hoge.mm");

        assertContentType("message/rfc822", "extractor/eml/sample1.eml", "sample1.eml");

        assertContentType("application/octet-stream", null, "hoge");

        assertContentType("application/vnd.ms-powerpoint", "extractor/msoffice/test.ppt", "h&oge.ppt");
        assertContentType("application/vnd.ms-powerpoint", "extractor/msoffice/test.ppt", "h?oge.ppt");
        assertContentType("application/vnd.ms-powerpoint", "extractor/msoffice/test.ppt", "h@oge.ppt");
        assertContentType("application/vnd.ms-powerpoint", "extractor/msoffice/test.ppt", "h:oge.ppt");
        assertContentType("application/vnd.ms-powerpoint", "extractor/msoffice/test.ppt", "h/oge.ppt");

        assertContentType("image/vnd.dwg", "extractor/dwg/autocad_2000.dwg", "autocad_2000.dwg");
        assertContentType("image/vnd.dwg", "extractor/dwg/autocad_2004.dwg", "autocad_2004.dwg");
        assertContentType("image/vnd.dwg", "extractor/dwg/autocad_2007.dwg", "autocad_2007.dwg");
        assertContentType("image/vnd.dwg", "extractor/dwg/autocad_2010.dwg", "autocad_2010.dwg");
        assertContentType("image/vnd.dwg", "extractor/dwg/autocad_2013.dwg", "autocad_2013.dwg");
        assertContentType("image/vnd.dwg", "extractor/dwg/autocad_97_98.dwg", "autocad_97_98.dwg");

        assertContentType("image/vnd.dxf", "extractor/dxf/autocad_2000.dxf", "autocad_2000.dxf");
        assertContentType("image/vnd.dxf", "extractor/dxf/autocad_2004.dxf", "autocad_2004.dxf");
        assertContentType("image/vnd.dxf", "extractor/dxf/autocad_2007.dxf", "autocad_2007.dxf");
        assertContentType("image/vnd.dxf", "extractor/dxf/autocad_2010.dxf", "autocad_2010.dxf");
        assertContentType("image/vnd.dxf", "extractor/dxf/autocad_2013.dxf", "autocad_2013.dxf");
        assertContentType("image/vnd.dxf", "extractor/dxf/autocad_R12_LT2.dxf", "autocad_R12_LT2.dxf");

        assertContentType("text/x-csrc", "extractor/program/test.c", "test.c");
        assertContentType("text/x-c++src", "extractor/program/test.cpp", "test.cpp");
        assertContentType("text/x-chdr", "extractor/program/test.h", "test.h");
        assertContentType("text/x-c++hdr", "extractor/program/test.hpp", "test.hpp");
        assertContentType("text/x-java-source", "extractor/program/test.java", "test.java");
        assertContentType("text/javascript", "extractor/program/test.js", "test.js");

        assertContentType("application/x-js-taro", "extractor/ichitaro/taro2016_basic.jtd", "taro2016_basic.jtd");
        assertContentType("application/x-js-taro", "extractor/ichitaro/taro2016_temp.jtt", "taro2016_temp.jtt");
        assertContentType("application/x-js-taro", "extractor/ichitaro/taro2016_basic_zip.jtdc", "taro2016_basic_zip.jtdc");
        assertContentType("application/x-js-taro", "extractor/ichitaro/taro2016_temp_zip.jttc", "taro2016_temp_zip.jttc");
        assertContentType("application/x-js-taro", "extractor/ichitaro/taro7_taro2016_basic.jfw", "taro7_taro2016_basic.jfw");
        assertContentType("application/x-js-taro", "extractor/ichitaro/taro7_taro2016_temp.jvw", "taro7_taro2016_temp.jvw");
        assertContentType("application/x-js-taro", "extractor/ichitaro/ver4_taro2016_basic.jsw", "ver4_taro2016_basic.jsw");
        assertContentType("application/x-js-taro", "extractor/ichitaro/ver5_taro2016_basic.jaw", "ver5_taro2016_basic.jaw");
        assertContentType("application/x-js-taro", "extractor/ichitaro/ver5_taro2016_temp.jtw", "ver5_taro2016_temp.jtw");
        assertContentType("application/x-js-taro", "extractor/ichitaro/ver6_taro2016_basic.jbw", "ver6_taro2016_basic.jbw");
        assertContentType("application/x-js-taro", "extractor/ichitaro/ver6_taro2016_temp.juw", "ver6_taro2016_temp.juw");
    }

    @Test
    public void test_getContentType_content() throws IOException {
        assertContentType("text/plain", "test/text1.txt", "hoge.txt");
        assertContentType("text/plain", null, "hoge.txt");
        assertContentType("text/html", "html/test1.html", "hoge.html");
        assertContentType("text/html", "html/test1.html", "hoge.htm");
        assertContentType("text/html", "html/test1.shtml", "hoge.shtml");

        assertContentType("text/plain", "test/text1.txt", "hoge.doc");
        assertContentType("application/msword", "extractor/msoffice/test.doc", "hoge.doc");
        assertContentType("text/plain", "test/text1.txt", "hoge.xls");
        assertContentType("application/vnd.ms-excel", "extractor/msoffice/test.xls", "hoge.xls");
        assertContentType("text/plain", "test/text1.txt", "hoge.ppt");
        assertContentType("application/vnd.ms-powerpoint", "extractor/msoffice/test.ppt", "hoge.ppt");

        assertContentType("text/plain", "test/text1.txt", "hoge.docx");
        assertContentType("application/vnd.openxmlformats-officedocument.wordprocessingml.document", "extractor/msoffice/test.docx",
                "hoge.docx");
        assertContentType("text/plain", "test/text1.txt", "hoge.xlsx");
        assertContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "extractor/msoffice/test.xlsx", "hoge.xlsx");
        assertContentType("text/plain", "test/text1.txt", "hoge.pptx");
        assertContentType("application/vnd.openxmlformats-officedocument.presentationml.presentation", "extractor/msoffice/test.pptx",
                "hoge.pptx");

        assertContentType("image/jpeg", null, "hoge.jpg");
        assertContentType("image/gif", null, "hoge.gif");

        assertContentType("application/pdf", "extractor/test.pdf", "hoge.pdf");

        assertContentType("application/gzip", "extractor/gz/test.tar.gz", "hoge.tar.gz");
        assertContentType("application/zip", "extractor/zip/test.zip", "hoge.zip");
        assertContentType("application/x-lharc", "extractor/lha/test.lzh", "hoge.lzh"); // TODO is it correct?

        assertContentType("application/xml", "extractor/test.mm", "hoge.mm");

        assertContentType("message/rfc822", "extractor/eml/sample1.eml", "sample1.eml");

        assertContentType("application/octet-stream", null, "hoge");

        assertContentType("application/vnd.ms-powerpoint", "extractor/msoffice/test.ppt", "h&oge.ppt");
        assertContentType("application/vnd.ms-powerpoint", "extractor/msoffice/test.ppt", "h?oge.ppt");
        assertContentType("application/vnd.ms-powerpoint", "extractor/msoffice/test.ppt", "h@oge.ppt");
        assertContentType("application/vnd.ms-powerpoint", "extractor/msoffice/test.ppt", "h:oge.ppt");
        assertContentType("application/vnd.ms-powerpoint", "extractor/msoffice/test.ppt", "h/oge.ppt");

        assertContentType("image/vnd.dwg", "extractor/dwg/autocad_2000.dwg", "autocad_2000.dwg");
        assertContentType("image/vnd.dwg", "extractor/dwg/autocad_2004.dwg", "autocad_2004.dwg");
        assertContentType("image/vnd.dwg", "extractor/dwg/autocad_2007.dwg", "autocad_2007.dwg");
        assertContentType("image/vnd.dwg", "extractor/dwg/autocad_2010.dwg", "autocad_2010.dwg");
        assertContentType("image/vnd.dwg", "extractor/dwg/autocad_2013.dwg", "autocad_2013.dwg");
        assertContentType("image/vnd.dwg", "extractor/dwg/autocad_97_98.dwg", "autocad_97_98.dwg");

        assertContentType("image/vnd.dxf", "extractor/dxf/autocad_2000.dxf", "autocad_2000.dxf");
        assertContentType("image/vnd.dxf", "extractor/dxf/autocad_2004.dxf", "autocad_2004.dxf");
        assertContentType("image/vnd.dxf", "extractor/dxf/autocad_2007.dxf", "autocad_2007.dxf");
        assertContentType("image/vnd.dxf", "extractor/dxf/autocad_2010.dxf", "autocad_2010.dxf");
        assertContentType("image/vnd.dxf", "extractor/dxf/autocad_2013.dxf", "autocad_2013.dxf");
        assertContentType("image/vnd.dxf", "extractor/dxf/autocad_R12_LT2.dxf", "autocad_R12_LT2.dxf");

        assertContentType("text/x-csrc", "extractor/program/test.c", "test.c");
        assertContentType("text/x-c++src", "extractor/program/test.cpp", "test.cpp");
        assertContentType("text/x-chdr", "extractor/program/test.h", "test.h");
        assertContentType("text/x-c++hdr", "extractor/program/test.hpp", "test.hpp");
        assertContentType("text/x-java-source", "extractor/program/test.java", "test.java");
        assertContentType("text/javascript", "extractor/program/test.js", "test.js");

        assertContentType("application/x-js-taro", "extractor/ichitaro/taro2016_basic.jtd", "taro2016_basic.jtd");
        assertContentType("application/x-js-taro", "extractor/ichitaro/taro2016_temp.jtt", "taro2016_temp.jtt");
        assertContentType("application/x-js-taro", "extractor/ichitaro/taro2016_basic_zip.jtdc", "taro2016_basic_zip.jtdc");
        assertContentType("application/x-js-taro", "extractor/ichitaro/taro2016_temp_zip.jttc", "taro2016_temp_zip.jttc");
        assertContentType("application/x-js-taro", "extractor/ichitaro/taro7_taro2016_basic.jfw", "taro7_taro2016_basic.jfw");
        assertContentType("application/x-js-taro", "extractor/ichitaro/taro7_taro2016_temp.jvw", "taro7_taro2016_temp.jvw");
        assertContentType("application/x-js-taro", "extractor/ichitaro/ver4_taro2016_basic.jsw", "ver4_taro2016_basic.jsw");
        assertContentType("application/x-js-taro", "extractor/ichitaro/ver5_taro2016_basic.jaw", "ver5_taro2016_basic.jaw");
        assertContentType("application/x-js-taro", "extractor/ichitaro/ver5_taro2016_temp.jtw", "ver5_taro2016_temp.jtw");
        assertContentType("application/x-js-taro", "extractor/ichitaro/ver6_taro2016_basic.jbw", "ver6_taro2016_basic.jbw");
        assertContentType("application/x-js-taro", "extractor/ichitaro/ver6_taro2016_temp.juw", "ver6_taro2016_temp.juw");
    }

    private void assertContentType(final String expect, final String path, final String name) throws IOException {
        final MimeTypeHelper mimeTypeHelper = container.getComponent("mimeTypeHelper");
        if (path != null) {
            try (final InputStream is = ResourceUtil.getResourceAsStream(path)) {
                assertEquals(expect, mimeTypeHelper.getContentType(is, name));
            }
        } else {
            assertEquals(expect, mimeTypeHelper.getContentType(null, name));
        }
    }

    @Test
    public void test_getContentType_null() {
        final MimeTypeHelper mimeTypeHelper = container.getComponent("mimeTypeHelper");
        final InputStream is = ResourceUtil.getResourceAsStream("test/text1.txt");

        try {
            mimeTypeHelper.getContentType(null, "");
            fail();
        } catch (final MimeTypeException e) {}

        assertEquals("text/plain", mimeTypeHelper.getContentType(is, ""));
        assertEquals("text/plain", mimeTypeHelper.getContentType(is, " "));
    }

    @Test
    public void test_getContentType_sqlWithRemComment_noOverride() throws IOException {
        // Without extension override, a SQL file starting with REM is detected as application/x-bat
        final MimeTypeHelperImpl mimeTypeHelper = container.getComponent("mimeTypeHelper");
        try (final InputStream is = ResourceUtil.getResourceAsStream("mimetype/oracle_rem.sql")) {
            final String contentType = mimeTypeHelper.getContentType(is, "oracle_rem.sql");
            assertEquals("application/x-bat", contentType);
        }
    }

    @Test
    public void test_getContentType_sqlWithRemComment_withOverride() throws IOException {
        // With extension override, the SQL file is correctly detected as text/x-sql
        final MimeTypeHelperImpl mimeTypeHelper = container.getComponent("mimeTypeHelper");
        final Map<String, String> overrideMap = new HashMap<>();
        overrideMap.put(".sql", "text/x-sql");
        mimeTypeHelper.setExtensionMimeTypeMap(overrideMap);
        try (final InputStream is = ResourceUtil.getResourceAsStream("mimetype/oracle_rem.sql")) {
            final String contentType = mimeTypeHelper.getContentType(is, "oracle_rem.sql");
            assertEquals("text/x-sql", contentType);
        }
    }

    @Test
    public void test_getContentType_extensionOverride_noFilename() throws IOException {
        // Extension override is not applied when filename is not provided
        final MimeTypeHelperImpl mimeTypeHelper = container.getComponent("mimeTypeHelper");
        final Map<String, String> overrideMap = new HashMap<>();
        overrideMap.put(".sql", "text/x-sql");
        mimeTypeHelper.setExtensionMimeTypeMap(overrideMap);
        try (final InputStream is = ResourceUtil.getResourceAsStream("mimetype/oracle_rem.sql")) {
            final String contentType = mimeTypeHelper.getContentType(is, "");
            // Falls back to content-based detection
            assertEquals("application/x-bat", contentType);
        }
    }

    @Test
    public void test_getContentType_extensionOverride_unmappedExtension() throws IOException {
        // Extension not in the map falls through to normal detection
        final MimeTypeHelperImpl mimeTypeHelper = container.getComponent("mimeTypeHelper");
        final Map<String, String> overrideMap = new HashMap<>();
        overrideMap.put(".sql", "text/x-sql");
        mimeTypeHelper.setExtensionMimeTypeMap(overrideMap);
        try (final InputStream is = ResourceUtil.getResourceAsStream("test/text1.txt")) {
            final String contentType = mimeTypeHelper.getContentType(is, "hoge.txt");
            assertEquals("text/plain", contentType);
        }
    }

    @Test
    public void test_getContentType_extensionOverride_caseInsensitive() throws IOException {
        // Extension matching is case-insensitive (.SQL is treated as .sql)
        final MimeTypeHelperImpl mimeTypeHelper = container.getComponent("mimeTypeHelper");
        final Map<String, String> overrideMap = new HashMap<>();
        overrideMap.put(".sql", "text/x-sql");
        mimeTypeHelper.setExtensionMimeTypeMap(overrideMap);
        try (final InputStream is = ResourceUtil.getResourceAsStream("mimetype/oracle_rem.sql")) {
            final String contentType = mimeTypeHelper.getContentType(is, "oracle_rem.SQL");
            assertEquals("text/x-sql", contentType);
        }
    }

    @Test
    public void test_getContentType_extensionOverride_emptyMap() throws IOException {
        // Empty override map does not affect existing behavior
        final MimeTypeHelperImpl mimeTypeHelper = container.getComponent("mimeTypeHelper");
        mimeTypeHelper.setExtensionMimeTypeMap(new HashMap<>());
        try (final InputStream is = ResourceUtil.getResourceAsStream("test/text1.txt")) {
            final String contentType = mimeTypeHelper.getContentType(is, "hoge.txt");
            assertEquals("text/plain", contentType);
        }
    }

    @Test
    public void test_getExtension() {
        final MimeTypeHelperImpl mimeTypeHelper = container.getComponent("mimeTypeHelper");
        assertEquals(".sql", mimeTypeHelper.getExtension("test.sql"));
        assertEquals(".sql", mimeTypeHelper.getExtension("test.SQL"));
        assertEquals(".sql", mimeTypeHelper.getExtension("path/to/test.sql"));
        assertEquals(".gz", mimeTypeHelper.getExtension("archive.tar.gz"));
        assertEquals(".txt", mimeTypeHelper.getExtension("file.txt"));
        assertEquals(".html", mimeTypeHelper.getExtension("index.html"));
        assertEquals(".java", mimeTypeHelper.getExtension("src/main/Test.java"));
        assertEquals(".xml", mimeTypeHelper.getExtension("deeply/nested/path/config.xml"));
        assertNull(mimeTypeHelper.getExtension(null));
        assertNull(mimeTypeHelper.getExtension(""));
        assertNull(mimeTypeHelper.getExtension("  "));
        assertNull(mimeTypeHelper.getExtension("noextension"));
        assertNull(mimeTypeHelper.getExtension("dotlast."));
        assertNull(mimeTypeHelper.getExtension("/path/to/noext"));
    }

    @Test
    public void test_getContentType_extensionOverride_withNullStream() {
        // Extension override works even without an input stream
        final MimeTypeHelperImpl mimeTypeHelper = container.getComponent("mimeTypeHelper");
        final Map<String, String> overrideMap = new HashMap<>();
        overrideMap.put(".sql", "text/x-sql");
        mimeTypeHelper.setExtensionMimeTypeMap(overrideMap);

        final String contentType = mimeTypeHelper.getContentType(null, "test.sql");
        assertEquals("text/x-sql", contentType);
    }

    @Test
    public void test_getContentType_extensionOverride_withPathFilename() throws IOException {
        // Extension override works with path-style filenames
        final MimeTypeHelperImpl mimeTypeHelper = container.getComponent("mimeTypeHelper");
        final Map<String, String> overrideMap = new HashMap<>();
        overrideMap.put(".sql", "text/x-sql");
        mimeTypeHelper.setExtensionMimeTypeMap(overrideMap);
        try (final InputStream is = ResourceUtil.getResourceAsStream("mimetype/oracle_rem.sql")) {
            final String contentType = mimeTypeHelper.getContentType(is, "/opt/oracle/scripts/init.sql");
            assertEquals("text/x-sql", contentType);
        }
    }

    @Test
    public void test_getContentType_extensionOverride_replacesMap() throws IOException {
        // Setting a new map replaces the previous one
        final MimeTypeHelperImpl mimeTypeHelper = container.getComponent("mimeTypeHelper");
        final Map<String, String> overrideMap1 = new HashMap<>();
        overrideMap1.put(".sql", "text/x-sql");
        mimeTypeHelper.setExtensionMimeTypeMap(overrideMap1);
        try (final InputStream is = ResourceUtil.getResourceAsStream("mimetype/oracle_rem.sql")) {
            assertEquals("text/x-sql", mimeTypeHelper.getContentType(is, "test.sql"));
        }

        // Replace with a different map that doesn't include .sql
        final Map<String, String> overrideMap2 = new HashMap<>();
        overrideMap2.put(".bat", "application/x-bat");
        mimeTypeHelper.setExtensionMimeTypeMap(overrideMap2);
        try (final InputStream is = ResourceUtil.getResourceAsStream("mimetype/oracle_rem.sql")) {
            // .sql is no longer in the override map, falls through to normal detection
            final String contentType = mimeTypeHelper.getContentType(is, "test.sql");
            assertNotNull(contentType);
            // Should not be text/x-sql since the map was replaced
            assertFalse("text/x-sql".equals(contentType));
        }
    }

    @Test
    public void test_getContentType_extensionOverride_multipleMappings() throws IOException {
        // Multiple extension mappings all work correctly
        final MimeTypeHelperImpl mimeTypeHelper = container.getComponent("mimeTypeHelper");
        final Map<String, String> overrideMap = new HashMap<>();
        overrideMap.put(".sql", "text/x-sql");
        overrideMap.put(".plsql", "text/x-plsql");
        overrideMap.put(".pls", "text/x-plsql");
        mimeTypeHelper.setExtensionMimeTypeMap(overrideMap);

        try (final InputStream is = ResourceUtil.getResourceAsStream("mimetype/oracle_rem.sql")) {
            assertEquals("text/x-sql", mimeTypeHelper.getContentType(is, "test.sql"));
        }
        try (final InputStream is = ResourceUtil.getResourceAsStream("mimetype/oracle_rem.sql")) {
            assertEquals("text/x-plsql", mimeTypeHelper.getContentType(is, "package.plsql"));
        }
        try (final InputStream is = ResourceUtil.getResourceAsStream("mimetype/oracle_rem.sql")) {
            assertEquals("text/x-plsql", mimeTypeHelper.getContentType(is, "body.pls"));
        }
    }

    @Test
    public void test_getContentType_extensionOverride_overridesContentDetection() throws IOException {
        // Extension override takes priority over content-based detection
        final MimeTypeHelperImpl mimeTypeHelper = container.getComponent("mimeTypeHelper");

        // First verify without override: text1.txt is detected as text/plain by content
        try (final InputStream is = ResourceUtil.getResourceAsStream("test/text1.txt")) {
            assertEquals("text/plain", mimeTypeHelper.getContentType(is, "test.txt"));
        }

        // Now set override: .txt -> application/custom
        final Map<String, String> overrideMap = new HashMap<>();
        overrideMap.put(".txt", "application/custom");
        mimeTypeHelper.setExtensionMimeTypeMap(overrideMap);
        try (final InputStream is = ResourceUtil.getResourceAsStream("test/text1.txt")) {
            assertEquals("application/custom", mimeTypeHelper.getContentType(is, "test.txt"));
        }
    }

    @Test
    public void test_getContentType_extensionOverride_filenameFirst_mode() throws IOException {
        // Extension override still takes priority even when useFilename=true
        final MimeTypeHelperImpl mimeTypeHelper = container.getComponent("mimeTypeHelper");
        mimeTypeHelper.useFilename = true;
        final Map<String, String> overrideMap = new HashMap<>();
        overrideMap.put(".sql", "text/x-sql");
        mimeTypeHelper.setExtensionMimeTypeMap(overrideMap);
        try (final InputStream is = ResourceUtil.getResourceAsStream("mimetype/oracle_rem.sql")) {
            assertEquals("text/x-sql", mimeTypeHelper.getContentType(is, "test.sql"));
        }
    }

    @Test
    public void test_getContentType_extensionOverride_mixedCaseExtensions() throws IOException {
        // Various case combinations for the extension
        final MimeTypeHelperImpl mimeTypeHelper = container.getComponent("mimeTypeHelper");
        final Map<String, String> overrideMap = new HashMap<>();
        overrideMap.put(".sql", "text/x-sql");
        mimeTypeHelper.setExtensionMimeTypeMap(overrideMap);

        try (final InputStream is = ResourceUtil.getResourceAsStream("mimetype/oracle_rem.sql")) {
            assertEquals("text/x-sql", mimeTypeHelper.getContentType(is, "test.Sql"));
        }
        try (final InputStream is = ResourceUtil.getResourceAsStream("mimetype/oracle_rem.sql")) {
            assertEquals("text/x-sql", mimeTypeHelper.getContentType(is, "test.sQl"));
        }
        try (final InputStream is = ResourceUtil.getResourceAsStream("mimetype/oracle_rem.sql")) {
            assertEquals("text/x-sql", mimeTypeHelper.getContentType(is, "test.SQL"));
        }
    }

}
