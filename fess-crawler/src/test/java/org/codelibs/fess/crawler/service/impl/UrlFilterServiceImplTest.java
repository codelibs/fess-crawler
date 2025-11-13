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
package org.codelibs.fess.crawler.service.impl;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.codelibs.fess.crawler.helper.MemoryDataHelper;
import org.dbflute.utflute.core.PlainTestCase;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Test class for {@link UrlFilterServiceImpl}.
 */
public class UrlFilterServiceImplTest extends PlainTestCase {

    @InjectMocks
    private UrlFilterServiceImpl service;

    @Mock
    private MemoryDataHelper dataHelper;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        MockitoAnnotations.openMocks(this);
    }

    public void test_addIncludeUrlFilterSingle() {
        // Setup
        String sessionId = "session123";
        String url = "https://example.com/.*";

        // Execute
        service.addIncludeUrlFilter(sessionId, url);

        // Verify
        verify(dataHelper).addIncludeUrlPattern(sessionId, url);
    }

    public void test_addIncludeUrlFilterList() {
        // Setup
        String sessionId = "session123";
        List<String> urlList = new ArrayList<>();
        urlList.add("https://example.com/.*");
        urlList.add("https://test.com/.*");
        urlList.add("https://sample.org/.*");

        // Execute
        service.addIncludeUrlFilter(sessionId, urlList);

        // Verify - each URL should be added once
        verify(dataHelper).addIncludeUrlPattern(sessionId, "https://example.com/.*");
        verify(dataHelper).addIncludeUrlPattern(sessionId, "https://test.com/.*");
        verify(dataHelper).addIncludeUrlPattern(sessionId, "https://sample.org/.*");
    }

    public void test_addIncludeUrlFilterEmptyList() {
        // Setup
        String sessionId = "session123";
        List<String> urlList = new ArrayList<>();

        // Execute
        service.addIncludeUrlFilter(sessionId, urlList);

        // Verify - should not call dataHelper at all
        verify(dataHelper, times(0)).addIncludeUrlPattern(sessionId, null);
    }

    public void test_addExcludeUrlFilterSingle() {
        // Setup
        String sessionId = "session123";
        String url = "https://example.com/admin/.*";

        // Execute
        service.addExcludeUrlFilter(sessionId, url);

        // Verify
        verify(dataHelper).addExcludeUrlPattern(sessionId, url);
    }

    public void test_addExcludeUrlFilterList() {
        // Setup
        String sessionId = "session123";
        List<String> urlList = new ArrayList<>();
        urlList.add("https://example.com/admin/.*");
        urlList.add("https://example.com/private/.*");
        urlList.add("https://example.com/secret/.*");

        // Execute
        service.addExcludeUrlFilter(sessionId, urlList);

        // Verify
        verify(dataHelper).addExcludeUrlPattern(sessionId, "https://example.com/admin/.*");
        verify(dataHelper).addExcludeUrlPattern(sessionId, "https://example.com/private/.*");
        verify(dataHelper).addExcludeUrlPattern(sessionId, "https://example.com/secret/.*");
    }

    public void test_addExcludeUrlFilterEmptyList() {
        // Setup
        String sessionId = "session123";
        List<String> urlList = new ArrayList<>();

        // Execute
        service.addExcludeUrlFilter(sessionId, urlList);

        // Verify - should not call dataHelper at all
        verify(dataHelper, times(0)).addExcludeUrlPattern(sessionId, null);
    }

    public void test_delete() {
        // Setup
        String sessionId = "session123";

        // Execute
        service.delete(sessionId);

        // Verify
        verify(dataHelper).clearUrlPattern(sessionId);
    }

    public void test_deleteAll() {
        // Execute
        service.deleteAll();

        // Verify
        verify(dataHelper).clearUrlPattern();
    }

    public void test_getIncludeUrlPatternList() {
        // Setup
        String sessionId = "session123";
        List<Pattern> mockPatterns = new ArrayList<>();
        mockPatterns.add(Pattern.compile("https://example.com/.*"));
        mockPatterns.add(Pattern.compile("https://test.com/.*"));

        when(dataHelper.getIncludeUrlPatternList(sessionId)).thenReturn(mockPatterns);

        // Execute
        List<Pattern> result = service.getIncludeUrlPatternList(sessionId);

        // Verify
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(mockPatterns, result);
        verify(dataHelper).getIncludeUrlPatternList(sessionId);
    }

    public void test_getIncludeUrlPatternListEmpty() {
        // Setup
        String sessionId = "session123";
        List<Pattern> mockPatterns = new ArrayList<>();

        when(dataHelper.getIncludeUrlPatternList(sessionId)).thenReturn(mockPatterns);

        // Execute
        List<Pattern> result = service.getIncludeUrlPatternList(sessionId);

        // Verify
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(dataHelper).getIncludeUrlPatternList(sessionId);
    }

    public void test_getExcludeUrlPatternList() {
        // Setup
        String sessionId = "session123";
        List<Pattern> mockPatterns = new ArrayList<>();
        mockPatterns.add(Pattern.compile("https://example.com/admin/.*"));
        mockPatterns.add(Pattern.compile("https://example.com/private/.*"));

        when(dataHelper.getExcludeUrlPatternList(sessionId)).thenReturn(mockPatterns);

        // Execute
        List<Pattern> result = service.getExcludeUrlPatternList(sessionId);

        // Verify
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(mockPatterns, result);
        verify(dataHelper).getExcludeUrlPatternList(sessionId);
    }

    public void test_getExcludeUrlPatternListEmpty() {
        // Setup
        String sessionId = "session123";
        List<Pattern> mockPatterns = new ArrayList<>();

        when(dataHelper.getExcludeUrlPatternList(sessionId)).thenReturn(mockPatterns);

        // Execute
        List<Pattern> result = service.getExcludeUrlPatternList(sessionId);

        // Verify
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(dataHelper).getExcludeUrlPatternList(sessionId);
    }

    public void test_complexScenario() {
        // Setup
        String sessionId = "crawl-session-456";

        // Add include filters
        List<String> includeList = new ArrayList<>();
        includeList.add("https://example.com/.*");
        includeList.add("https://test.com/.*");
        service.addIncludeUrlFilter(sessionId, includeList);

        // Add exclude filters
        List<String> excludeList = new ArrayList<>();
        excludeList.add("https://example.com/admin/.*");
        excludeList.add("https://example.com/private/.*");
        service.addExcludeUrlFilter(sessionId, excludeList);

        // Add individual filters
        service.addIncludeUrlFilter(sessionId, "https://sample.org/.*");
        service.addExcludeUrlFilter(sessionId, "https://example.com/secret/.*");

        // Setup mock returns
        List<Pattern> mockIncludePatterns = new ArrayList<>();
        mockIncludePatterns.add(Pattern.compile("https://example.com/.*"));
        mockIncludePatterns.add(Pattern.compile("https://test.com/.*"));
        mockIncludePatterns.add(Pattern.compile("https://sample.org/.*"));

        List<Pattern> mockExcludePatterns = new ArrayList<>();
        mockExcludePatterns.add(Pattern.compile("https://example.com/admin/.*"));
        mockExcludePatterns.add(Pattern.compile("https://example.com/private/.*"));
        mockExcludePatterns.add(Pattern.compile("https://example.com/secret/.*"));

        when(dataHelper.getIncludeUrlPatternList(sessionId)).thenReturn(mockIncludePatterns);
        when(dataHelper.getExcludeUrlPatternList(sessionId)).thenReturn(mockExcludePatterns);

        // Execute retrieval
        List<Pattern> includePatterns = service.getIncludeUrlPatternList(sessionId);
        List<Pattern> excludePatterns = service.getExcludeUrlPatternList(sessionId);

        // Verify
        assertEquals(3, includePatterns.size());
        assertEquals(3, excludePatterns.size());

        // Verify all calls were made
        verify(dataHelper).addIncludeUrlPattern(sessionId, "https://example.com/.*");
        verify(dataHelper).addIncludeUrlPattern(sessionId, "https://test.com/.*");
        verify(dataHelper).addIncludeUrlPattern(sessionId, "https://sample.org/.*");
        verify(dataHelper).addExcludeUrlPattern(sessionId, "https://example.com/admin/.*");
        verify(dataHelper).addExcludeUrlPattern(sessionId, "https://example.com/private/.*");
        verify(dataHelper).addExcludeUrlPattern(sessionId, "https://example.com/secret/.*");
    }

    public void test_multipleSessionsIndependence() {
        // Setup
        String session1 = "session1";
        String session2 = "session2";

        // Add different filters for different sessions
        service.addIncludeUrlFilter(session1, "https://session1.com/.*");
        service.addIncludeUrlFilter(session2, "https://session2.com/.*");

        service.addExcludeUrlFilter(session1, "https://session1.com/admin/.*");
        service.addExcludeUrlFilter(session2, "https://session2.com/private/.*");

        // Verify that dataHelper was called with correct session IDs
        verify(dataHelper).addIncludeUrlPattern(session1, "https://session1.com/.*");
        verify(dataHelper).addIncludeUrlPattern(session2, "https://session2.com/.*");
        verify(dataHelper).addExcludeUrlPattern(session1, "https://session1.com/admin/.*");
        verify(dataHelper).addExcludeUrlPattern(session2, "https://session2.com/private/.*");
    }

    public void test_deleteSpecificSession() {
        // Setup
        String session1 = "session1";
        String session2 = "session2";

        // Add filters for both sessions
        service.addIncludeUrlFilter(session1, "https://session1.com/.*");
        service.addIncludeUrlFilter(session2, "https://session2.com/.*");

        // Delete only session1
        service.delete(session1);

        // Verify only session1 was cleared
        verify(dataHelper).clearUrlPattern(session1);
        verify(dataHelper, times(0)).clearUrlPattern(session2);
    }

    public void test_deleteAllSessions() {
        // Setup
        service.addIncludeUrlFilter("session1", "https://example.com/.*");
        service.addIncludeUrlFilter("session2", "https://test.com/.*");

        // Execute
        service.deleteAll();

        // Verify
        verify(dataHelper).clearUrlPattern();
    }
}
