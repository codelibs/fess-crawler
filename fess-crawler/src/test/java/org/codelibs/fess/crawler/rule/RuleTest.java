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
package org.codelibs.fess.crawler.rule;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.codelibs.fess.crawler.entity.ResponseData;
import org.codelibs.fess.crawler.processor.ResponseProcessor;
import org.junit.jupiter.api.Test;
import org.dbflute.utflute.core.PlainTestCase;

/**
 * Test class for Rule interface.
 * Tests the contract and behavior of Rule implementations.
 */
public class RuleTest extends PlainTestCase {

    /**
     * Test implementation of Rule interface for testing purposes
     */
    public static class TestRule implements Rule {
        private static final long serialVersionUID = 1L;

        private final String ruleId;
        private final ResponseProcessor responseProcessor;
        private final boolean matchResult;
        private int matchCallCount = 0;

        public TestRule(String ruleId, ResponseProcessor responseProcessor, boolean matchResult) {
            this.ruleId = ruleId;
            this.responseProcessor = responseProcessor;
            this.matchResult = matchResult;
        }

        @Override
        public boolean match(ResponseData responseData) {
            matchCallCount++;
            return matchResult;
        }

        @Override
        public String getRuleId() {
            return ruleId;
        }

        @Override
        public ResponseProcessor getResponseProcessor() {
            return responseProcessor;
        }

        public int getMatchCallCount() {
            return matchCallCount;
        }
    }

    /**
     * Configurable test rule implementation
     */
    public static class ConfigurableRule implements Rule {
        private static final long serialVersionUID = 1L;

        private String ruleId;
        private ResponseProcessor responseProcessor;
        private Map<String, String> conditions = new HashMap<>();

        @Override
        public boolean match(ResponseData responseData) {
            if (responseData == null) {
                return false;
            }

            // Check URL condition
            String urlCondition = conditions.get("url");
            if (urlCondition != null && responseData.getUrl() != null) {
                if (!responseData.getUrl().matches(urlCondition)) {
                    return false;
                }
            }

            // Check MIME type condition
            String mimeTypeCondition = conditions.get("mimeType");
            if (mimeTypeCondition != null && responseData.getMimeType() != null) {
                if (!responseData.getMimeType().matches(mimeTypeCondition)) {
                    return false;
                }
            }

            // Check status code condition
            String statusCodeCondition = conditions.get("statusCode");
            if (statusCodeCondition != null) {
                int expectedCode = Integer.parseInt(statusCodeCondition);
                if (responseData.getHttpStatusCode() != expectedCode) {
                    return false;
                }
            }

            return true;
        }

        @Override
        public String getRuleId() {
            return ruleId;
        }

        @Override
        public ResponseProcessor getResponseProcessor() {
            return responseProcessor;
        }

        public void setRuleId(String ruleId) {
            this.ruleId = ruleId;
        }

        public void setResponseProcessor(ResponseProcessor responseProcessor) {
            this.responseProcessor = responseProcessor;
        }

        public void addCondition(String key, String value) {
            conditions.put(key, value);
        }

        public void clearConditions() {
            conditions.clear();
        }
    }

    /**
     * Test response processor implementation
     */
    public static class TestResponseProcessor implements ResponseProcessor, Serializable {
        private static final long serialVersionUID = 1L;

        private final String name;
        private int processCount = 0;

        public TestResponseProcessor(String name) {
            this.name = name;
        }

        @Override
        public void process(ResponseData responseData) {
            processCount++;
        }

        public String getName() {
            return name;
        }

        public int getProcessCount() {
            return processCount;
        }
    }

    /**
     * Test basic Rule implementation
     */
    @Test
    public void test_basicRuleImplementation() {
        TestResponseProcessor processor = new TestResponseProcessor("testProcessor");
        TestRule rule = new TestRule("rule1", processor, true);

        // Test getRuleId
        assertEquals("rule1", rule.getRuleId());

        // Test getResponseProcessor
        assertTrue(processor == rule.getResponseProcessor());

        // Test match
        ResponseData responseData = new ResponseData();
        assertTrue(rule.match(responseData));
        assertEquals(1, rule.getMatchCallCount());
    }

    /**
     * Test Rule with always true match
     */
    @Test
    public void test_ruleAlwaysMatch() {
        TestResponseProcessor processor = new TestResponseProcessor("alwaysMatch");
        TestRule rule = new TestRule("alwaysRule", processor, true);

        ResponseData responseData = new ResponseData();
        responseData.setUrl("http://example.com");

        assertTrue(rule.match(responseData));
        assertTrue(rule.match(responseData));
        assertTrue(rule.match(responseData));
        assertEquals(3, rule.getMatchCallCount());
    }

    /**
     * Test Rule with always false match
     */
    @Test
    public void test_ruleNeverMatch() {
        TestResponseProcessor processor = new TestResponseProcessor("neverMatch");
        TestRule rule = new TestRule("neverRule", processor, false);

        ResponseData responseData = new ResponseData();
        responseData.setUrl("http://example.com");

        assertFalse(rule.match(responseData));
        assertFalse(rule.match(responseData));
        assertFalse(rule.match(responseData));
        assertEquals(3, rule.getMatchCallCount());
    }

    /**
     * Test Rule with null ResponseData
     */
    @Test
    public void test_matchWithNullResponseData() {
        ConfigurableRule rule = new ConfigurableRule();
        rule.setRuleId("nullTest");
        rule.setResponseProcessor(new TestResponseProcessor("nullProcessor"));

        // Should handle null gracefully
        assertFalse(rule.match(null));
    }

    /**
     * Test Rule with null rule ID
     */
    @Test
    public void test_nullRuleId() {
        TestRule rule = new TestRule(null, new TestResponseProcessor("test"), true);

        assertNull(rule.getRuleId());

        ResponseData responseData = new ResponseData();
        assertTrue(rule.match(responseData));
    }

    /**
     * Test Rule with null ResponseProcessor
     */
    @Test
    public void test_nullResponseProcessor() {
        TestRule rule = new TestRule("rule1", null, true);

        assertEquals("rule1", rule.getRuleId());
        assertNull(rule.getResponseProcessor());

        ResponseData responseData = new ResponseData();
        assertTrue(rule.match(responseData));
    }

    /**
     * Test ConfigurableRule with URL condition
     */
    @Test
    public void test_configurableRule_urlCondition() {
        ConfigurableRule rule = new ConfigurableRule();
        rule.setRuleId("urlRule");
        rule.setResponseProcessor(new TestResponseProcessor("urlProcessor"));
        rule.addCondition("url", "https?://.*\\.example\\.com/.*");

        ResponseData responseData1 = new ResponseData();
        responseData1.setUrl("http://www.example.com/page");
        assertTrue(rule.match(responseData1));

        ResponseData responseData2 = new ResponseData();
        responseData2.setUrl("https://api.example.com/v1/users");
        assertTrue(rule.match(responseData2));

        ResponseData responseData3 = new ResponseData();
        responseData3.setUrl("http://other.com/page");
        assertFalse(rule.match(responseData3));
    }

    /**
     * Test ConfigurableRule with MIME type condition
     */
    @Test
    public void test_configurableRule_mimeTypeCondition() {
        ConfigurableRule rule = new ConfigurableRule();
        rule.setRuleId("mimeRule");
        rule.setResponseProcessor(new TestResponseProcessor("mimeProcessor"));
        rule.addCondition("mimeType", "text/.*");

        ResponseData responseData1 = new ResponseData();
        responseData1.setMimeType("text/html");
        assertTrue(rule.match(responseData1));

        ResponseData responseData2 = new ResponseData();
        responseData2.setMimeType("text/plain");
        assertTrue(rule.match(responseData2));

        ResponseData responseData3 = new ResponseData();
        responseData3.setMimeType("image/png");
        assertFalse(rule.match(responseData3));
    }

    /**
     * Test ConfigurableRule with status code condition
     */
    @Test
    public void test_configurableRule_statusCodeCondition() {
        ConfigurableRule rule = new ConfigurableRule();
        rule.setRuleId("statusRule");
        rule.setResponseProcessor(new TestResponseProcessor("statusProcessor"));
        rule.addCondition("statusCode", "200");

        ResponseData responseData1 = new ResponseData();
        responseData1.setHttpStatusCode(200);
        assertTrue(rule.match(responseData1));

        ResponseData responseData2 = new ResponseData();
        responseData2.setHttpStatusCode(404);
        assertFalse(rule.match(responseData2));

        ResponseData responseData3 = new ResponseData();
        responseData3.setHttpStatusCode(500);
        assertFalse(rule.match(responseData3));
    }

    /**
     * Test ConfigurableRule with multiple conditions (AND logic)
     */
    @Test
    public void test_configurableRule_multipleConditions() {
        ConfigurableRule rule = new ConfigurableRule();
        rule.setRuleId("multiRule");
        rule.setResponseProcessor(new TestResponseProcessor("multiProcessor"));
        rule.addCondition("url", "https://.*\\.example\\.com/.*");
        rule.addCondition("mimeType", "text/html");
        rule.addCondition("statusCode", "200");

        // All conditions match
        ResponseData responseData1 = new ResponseData();
        responseData1.setUrl("https://www.example.com/page");
        responseData1.setMimeType("text/html");
        responseData1.setHttpStatusCode(200);
        assertTrue(rule.match(responseData1));

        // URL doesn't match
        ResponseData responseData2 = new ResponseData();
        responseData2.setUrl("https://other.com/page");
        responseData2.setMimeType("text/html");
        responseData2.setHttpStatusCode(200);
        assertFalse(rule.match(responseData2));

        // MIME type doesn't match
        ResponseData responseData3 = new ResponseData();
        responseData3.setUrl("https://www.example.com/page");
        responseData3.setMimeType("application/json");
        responseData3.setHttpStatusCode(200);
        assertFalse(rule.match(responseData3));

        // Status code doesn't match
        ResponseData responseData4 = new ResponseData();
        responseData4.setUrl("https://www.example.com/page");
        responseData4.setMimeType("text/html");
        responseData4.setHttpStatusCode(404);
        assertFalse(rule.match(responseData4));
    }

    /**
     * Test Rule serialization
     */
    @Test
    public void test_serialization() throws Exception {
        TestResponseProcessor processor = new TestResponseProcessor("serializeProcessor");
        TestRule originalRule = new TestRule("serializeRule", processor, true);

        // Serialize
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(originalRule);
        oos.close();

        // Deserialize
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        ObjectInputStream ois = new ObjectInputStream(bais);
        TestRule deserializedRule = (TestRule) ois.readObject();
        ois.close();

        // Verify
        assertEquals(originalRule.getRuleId(), deserializedRule.getRuleId());
        assertNotNull(deserializedRule.getResponseProcessor());

        ResponseData responseData = new ResponseData();
        assertTrue(deserializedRule.match(responseData));
    }

    /**
     * Test multiple rules with same ResponseProcessor
     */
    @Test
    public void test_multipleRulesWithSameProcessor() {
        TestResponseProcessor sharedProcessor = new TestResponseProcessor("shared");

        TestRule rule1 = new TestRule("rule1", sharedProcessor, true);
        TestRule rule2 = new TestRule("rule2", sharedProcessor, false);
        TestRule rule3 = new TestRule("rule3", sharedProcessor, true);

        // Same processor instance
        assertTrue(sharedProcessor == rule1.getResponseProcessor());
        assertTrue(sharedProcessor == rule2.getResponseProcessor());
        assertTrue(sharedProcessor == rule3.getResponseProcessor());

        // Different rule IDs
        assertEquals("rule1", rule1.getRuleId());
        assertEquals("rule2", rule2.getRuleId());
        assertEquals("rule3", rule3.getRuleId());

        // Different match results
        ResponseData responseData = new ResponseData();
        assertTrue(rule1.match(responseData));
        assertFalse(rule2.match(responseData));
        assertTrue(rule3.match(responseData));
    }

    /**
     * Test rule with empty string ID
     */
    @Test
    public void test_emptyStringRuleId() {
        TestRule rule = new TestRule("", new TestResponseProcessor("empty"), true);

        assertEquals("", rule.getRuleId());
        assertNotNull(rule.getResponseProcessor());

        ResponseData responseData = new ResponseData();
        assertTrue(rule.match(responseData));
    }

    /**
     * Test ConfigurableRule condition clearing
     */
    @Test
    public void test_configurableRule_clearConditions() {
        ConfigurableRule rule = new ConfigurableRule();
        rule.setRuleId("clearRule");
        rule.setResponseProcessor(new TestResponseProcessor("clearProcessor"));

        // Add conditions
        rule.addCondition("url", "https://.*");
        rule.addCondition("mimeType", "text/.*");

        ResponseData responseData = new ResponseData();
        responseData.setUrl("http://example.com");
        responseData.setMimeType("image/png");

        // Should not match with conditions
        assertFalse(rule.match(responseData));

        // Clear conditions
        rule.clearConditions();

        // Should match after clearing conditions
        assertTrue(rule.match(responseData));
    }

    /**
     * Test rule matching with various ResponseData states
     */
    @Test
    public void test_matchWithVariousResponseDataStates() {
        ConfigurableRule rule = new ConfigurableRule();
        rule.setRuleId("stateRule");
        rule.setResponseProcessor(new TestResponseProcessor("stateProcessor"));

        // Empty ResponseData
        ResponseData emptyData = new ResponseData();
        assertTrue(rule.match(emptyData));

        // ResponseData with only URL
        ResponseData urlOnlyData = new ResponseData();
        urlOnlyData.setUrl("http://example.com");
        assertTrue(rule.match(urlOnlyData));

        // ResponseData with only MIME type
        ResponseData mimeOnlyData = new ResponseData();
        mimeOnlyData.setMimeType("text/html");
        assertTrue(rule.match(mimeOnlyData));

        // ResponseData with only status code
        ResponseData statusOnlyData = new ResponseData();
        statusOnlyData.setHttpStatusCode(200);
        assertTrue(rule.match(statusOnlyData));

        // Full ResponseData
        ResponseData fullData = new ResponseData();
        fullData.setUrl("http://example.com/page");
        fullData.setMimeType("text/html");
        fullData.setHttpStatusCode(200);
        fullData.setCharSet("UTF-8");
        fullData.setContentLength(1024L);
        assertTrue(rule.match(fullData));
    }

    /**
     * Test concurrent rule matching
     */
    @Test
    public void test_concurrentRuleMatching() throws Exception {
        final ConfigurableRule rule = new ConfigurableRule();
        rule.setRuleId("concurrentRule");
        rule.setResponseProcessor(new TestResponseProcessor("concurrentProcessor"));
        rule.addCondition("url", "https://.*\\.example\\.com/.*");

        final int threadCount = 10;
        final int matchesPerThread = 100;
        final AtomicInteger matchCount = new AtomicInteger(0);
        final AtomicInteger noMatchCount = new AtomicInteger(0);
        final List<Exception> exceptions = new ArrayList<>();

        Thread[] threads = new Thread[threadCount];
        for (int i = 0; i < threadCount; i++) {
            final int threadIndex = i;
            threads[i] = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        for (int j = 0; j < matchesPerThread; j++) {
                            ResponseData responseData = new ResponseData();
                            if (threadIndex % 2 == 0) {
                                responseData.setUrl("https://www.example.com/page" + j);
                                if (rule.match(responseData)) {
                                    matchCount.incrementAndGet();
                                }
                            } else {
                                responseData.setUrl("https://other.com/page" + j);
                                if (!rule.match(responseData)) {
                                    noMatchCount.incrementAndGet();
                                }
                            }
                        }
                    } catch (Exception e) {
                        synchronized (exceptions) {
                            exceptions.add(e);
                        }
                    }
                }
            });
        }

        // Start all threads
        for (Thread thread : threads) {
            thread.start();
        }

        // Wait for all threads
        for (Thread thread : threads) {
            thread.join();
        }

        // Verify results
        assertTrue(exceptions.isEmpty());
        assertEquals(threadCount / 2 * matchesPerThread, matchCount.get());
        assertEquals(threadCount / 2 * matchesPerThread, noMatchCount.get());
    }

    /**
     * Test rule with special characters in conditions
     */
    @Test
    public void test_specialCharactersInConditions() {
        ConfigurableRule rule = new ConfigurableRule();
        rule.setRuleId("specialRule");
        rule.setResponseProcessor(new TestResponseProcessor("specialProcessor"));

        // Test with regex special characters
        rule.addCondition("url", "https://example\\.com/\\?param=.*");

        ResponseData responseData1 = new ResponseData();
        responseData1.setUrl("https://example.com/?param=value");
        assertTrue(rule.match(responseData1));

        ResponseData responseData2 = new ResponseData();
        responseData2.setUrl("https://exampleXcom/?param=value");
        assertFalse(rule.match(responseData2));
    }

    /**
     * Test rule ID uniqueness
     */
    @Test
    public void test_ruleIdUniqueness() {
        List<Rule> rules = new ArrayList<>();

        // Create rules with unique IDs
        for (int i = 0; i < 100; i++) {
            TestRule rule = new TestRule("rule_" + i, new TestResponseProcessor("processor_" + i), true);
            rules.add(rule);
        }

        // Verify all IDs are unique
        for (int i = 0; i < rules.size(); i++) {
            for (int j = i + 1; j < rules.size(); j++) {
                assertFalse(rules.get(i).getRuleId() == rules.get(j).getRuleId());
            }
        }
    }

    /**
     * Test rule processor chain behavior
     */
    @Test
    public void test_ruleProcessorChain() {
        final List<String> executionOrder = new ArrayList<>();

        // Create custom processor that tracks execution
        ResponseProcessor processor = new ResponseProcessor() {
            @Override
            public void process(ResponseData responseData) {
                executionOrder.add("processor_executed");
            }
        };

        TestRule rule = new TestRule("chainRule", processor, true);

        ResponseData responseData = new ResponseData();
        if (rule.match(responseData)) {
            executionOrder.add("match_true");
            rule.getResponseProcessor().process(responseData);
        }

        assertEquals(2, executionOrder.size());
        assertEquals("match_true", executionOrder.get(0));
        assertEquals("processor_executed", executionOrder.get(1));
    }

    /**
     * Test rule with very long rule ID
     */
    @Test
    public void test_veryLongRuleId() {
        StringBuilder longId = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            longId.append("verylongruleid_");
        }

        TestRule rule = new TestRule(longId.toString(), new TestResponseProcessor("longProcessor"), true);

        assertEquals(longId.toString(), rule.getRuleId());

        ResponseData responseData = new ResponseData();
        assertTrue(rule.match(responseData));
    }

    /**
     * Test rule behavior consistency
     */
    @Test
    public void test_ruleBehaviorConsistency() {
        TestRule rule = new TestRule("consistentRule", new TestResponseProcessor("consistentProcessor"), true);

        ResponseData responseData = new ResponseData();
        responseData.setUrl("http://example.com");

        // Multiple calls should return same result
        boolean firstResult = rule.match(responseData);
        boolean secondResult = rule.match(responseData);
        boolean thirdResult = rule.match(responseData);

        assertEquals(firstResult, secondResult);
        assertEquals(secondResult, thirdResult);

        // Rule ID should remain constant
        String id1 = rule.getRuleId();
        String id2 = rule.getRuleId();
        String id3 = rule.getRuleId();

        assertEquals(id1, id2);
        assertEquals(id2, id3);

        // Processor should remain the same instance
        ResponseProcessor proc1 = rule.getResponseProcessor();
        ResponseProcessor proc2 = rule.getResponseProcessor();
        ResponseProcessor proc3 = rule.getResponseProcessor();

        assertTrue(proc1 == proc2);
        assertTrue(proc2 == proc3);
    }
}
