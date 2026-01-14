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
package org.codelibs.fess.crawler.rule.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import org.codelibs.fess.crawler.container.StandardCrawlerContainer;
import org.codelibs.fess.crawler.entity.ResponseData;
import org.codelibs.fess.crawler.processor.ResponseProcessor;
import org.codelibs.fess.crawler.rule.Rule;
import org.codelibs.fess.crawler.rule.RuleManager;
import org.dbflute.utflute.core.PlainTestCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

/**
 * Test class for AbstractRule.
 * Tests the abstract rule implementation and its common functionality.
 */
public class AbstractRuleTest extends PlainTestCase {

    /**
     * Concrete implementation of AbstractRule for testing
     */
    public static class TestAbstractRule extends AbstractRule {
        private static final long serialVersionUID = 1L;

        private boolean matchResult = true;
        private int matchCallCount = 0;
        private ResponseData lastResponseData = null;

        @Override
        public boolean match(ResponseData responseData) {
            matchCallCount++;
            lastResponseData = responseData;
            return matchResult;
        }

        public void setMatchResult(boolean matchResult) {
            this.matchResult = matchResult;
        }

        public int getMatchCallCount() {
            return matchCallCount;
        }

        public ResponseData getLastResponseData() {
            return lastResponseData;
        }

        public void resetCounters() {
            matchCallCount = 0;
            lastResponseData = null;
        }
    }

    /**
     * Another concrete implementation for testing different scenarios
     */
    public static class ConditionalAbstractRule extends AbstractRule {
        private static final long serialVersionUID = 1L;

        private String urlPattern;
        private String mimeTypePattern;

        @Override
        public boolean match(ResponseData responseData) {
            if (responseData == null) {
                return false;
            }

            boolean urlMatch = true;
            if (urlPattern != null && responseData.getUrl() != null) {
                urlMatch = responseData.getUrl().matches(urlPattern);
            }

            boolean mimeMatch = true;
            if (mimeTypePattern != null && responseData.getMimeType() != null) {
                mimeMatch = responseData.getMimeType().matches(mimeTypePattern);
            }

            return urlMatch && mimeMatch;
        }

        public void setUrlPattern(String urlPattern) {
            this.urlPattern = urlPattern;
        }

        public void setMimeTypePattern(String mimeTypePattern) {
            this.mimeTypePattern = mimeTypePattern;
        }
    }

    /**
     * Test RuleManager implementation
     */
    public static class TestRuleManager implements RuleManager {
        private final List<Rule> rules = new ArrayList<>();

        @Override
        public Rule getRule(ResponseData responseData) {
            for (Rule rule : rules) {
                if (rule != null && rule.match(responseData)) {
                    return rule;
                }
            }
            return null;
        }

        @Override
        public void addRule(Rule rule) {
            if (rule != null) {
                rules.add(rule);
            }
        }

        @Override
        public void addRule(int index, Rule rule) {
            if (rule != null) {
                rules.add(index, rule);
            }
        }

        @Override
        public boolean removeRule(Rule rule) {
            return rules.remove(rule);
        }

        @Override
        public boolean hasRule(Rule rule) {
            return rules.contains(rule);
        }

        public List<Rule> getRules() {
            return new ArrayList<>(rules);
        }

        public void clearRules() {
            rules.clear();
        }
    }

    /**
     * Test ResponseProcessor implementation
     */
    public static class TestResponseProcessor implements ResponseProcessor, Serializable {
        private static final long serialVersionUID = 1L;
        private int processCount = 0;
        private ResponseData lastProcessedData = null;

        @Override
        public void process(ResponseData responseData) {
            processCount++;
            lastProcessedData = responseData;
        }

        public int getProcessCount() {
            return processCount;
        }

        public ResponseData getLastProcessedData() {
            return lastProcessedData;
        }

        public void reset() {
            processCount = 0;
            lastProcessedData = null;
        }
    }

    private StandardCrawlerContainer container;
    private TestRuleManager ruleManager;
    private TestAbstractRule testRule;

    @Override
    protected void setUp(final TestInfo testInfo) throws Exception {
        super.setUp(testInfo);

        ruleManager = new TestRuleManager();
        container = new StandardCrawlerContainer().singleton("ruleManager", ruleManager);

        testRule = new TestAbstractRule();
        testRule.crawlerContainer = container;
    }

    /**
     * Test getRuleId and setRuleId
     */
    @Test
    public void test_ruleId_getterSetter() {
        // Initial state
        assertNull(testRule.getRuleId());

        // Set rule ID
        testRule.setRuleId("testRule1");
        assertEquals("testRule1", testRule.getRuleId());

        // Change rule ID
        testRule.setRuleId("changedRule");
        assertEquals("changedRule", testRule.getRuleId());

        // Set null rule ID
        testRule.setRuleId(null);
        assertNull(testRule.getRuleId());

        // Set empty rule ID
        testRule.setRuleId("");
        assertEquals("", testRule.getRuleId());
    }

    /**
     * Test getResponseProcessor and setResponseProcessor
     */
    @Test
    public void test_responseProcessor_getterSetter() {
        // Initial state
        assertNull(testRule.getResponseProcessor());

        // Set response processor
        TestResponseProcessor processor = new TestResponseProcessor();
        testRule.setResponseProcessor(processor);
        assertTrue(processor == testRule.getResponseProcessor());

        // Change response processor
        TestResponseProcessor newProcessor = new TestResponseProcessor();
        testRule.setResponseProcessor(newProcessor);
        assertTrue(newProcessor == testRule.getResponseProcessor());
        assertFalse(processor == testRule.getResponseProcessor());

        // Set null processor
        testRule.setResponseProcessor(null);
        assertNull(testRule.getResponseProcessor());
    }

    /**
     * Test register method with index 0
     */
    @Test
    public void test_register_indexZero() {
        testRule.setRuleId("rule1");

        // Pre-add some rules
        TestAbstractRule existingRule = new TestAbstractRule();
        existingRule.setRuleId("existing");
        ruleManager.addRule(existingRule);

        // Register at index 0
        testRule.register(0);

        List<Rule> rules = ruleManager.getRules();
        assertEquals(2, rules.size());
        assertEquals("rule1", rules.get(0).getRuleId());
        assertEquals("existing", rules.get(1).getRuleId());
    }

    /**
     * Test register method with middle index
     */
    @Test
    public void test_register_middleIndex() {
        // Pre-add some rules
        TestAbstractRule rule1 = new TestAbstractRule();
        rule1.setRuleId("rule1");
        ruleManager.addRule(rule1);

        TestAbstractRule rule2 = new TestAbstractRule();
        rule2.setRuleId("rule2");
        ruleManager.addRule(rule2);

        // Register at index 1
        testRule.setRuleId("middle");
        testRule.register(1);

        List<Rule> rules = ruleManager.getRules();
        assertEquals(3, rules.size());
        assertEquals("rule1", rules.get(0).getRuleId());
        assertEquals("middle", rules.get(1).getRuleId());
        assertEquals("rule2", rules.get(2).getRuleId());
    }

    /**
     * Test register method with last index
     */
    @Test
    public void test_register_lastIndex() {
        testRule.setRuleId("lastRule");

        // Pre-add some rules
        TestAbstractRule rule1 = new TestAbstractRule();
        rule1.setRuleId("rule1");
        ruleManager.addRule(rule1);

        TestAbstractRule rule2 = new TestAbstractRule();
        rule2.setRuleId("rule2");
        ruleManager.addRule(rule2);

        // Register at last index
        testRule.register(2);

        List<Rule> rules = ruleManager.getRules();
        assertEquals(3, rules.size());
        assertEquals("lastRule", rules.get(2).getRuleId());
    }

    /**
     * Test register method on empty RuleManager
     */
    @Test
    public void test_register_emptyRuleManager() {
        testRule.setRuleId("firstRule");

        // Register on empty manager
        testRule.register(0);

        List<Rule> rules = ruleManager.getRules();
        assertEquals(1, rules.size());
        assertEquals("firstRule", rules.get(0).getRuleId());
        assertTrue(ruleManager.hasRule(testRule));
    }

    /**
     * Test multiple properties configuration
     */
    @Test
    public void test_multiplePropertiesConfiguration() {
        TestResponseProcessor processor = new TestResponseProcessor();

        testRule.setRuleId("multiRule");
        testRule.setResponseProcessor(processor);

        assertEquals("multiRule", testRule.getRuleId());
        assertTrue(processor == testRule.getResponseProcessor());

        // Register and verify
        testRule.register(0);
        assertTrue(ruleManager.hasRule(testRule));

        // Verify registered rule maintains properties
        Rule registeredRule = ruleManager.getRules().get(0);
        assertEquals("multiRule", registeredRule.getRuleId());
        assertTrue(processor == registeredRule.getResponseProcessor());
    }

    /**
     * Test match method implementation
     */
    @Test
    public void test_match_implementation() {
        ResponseData responseData = new ResponseData();
        responseData.setUrl("http://example.com");

        // Test default match (true)
        assertTrue(testRule.match(responseData));
        assertEquals(1, testRule.getMatchCallCount());
        assertTrue(responseData == testRule.getLastResponseData());

        // Change match result
        testRule.setMatchResult(false);
        assertFalse(testRule.match(responseData));
        assertEquals(2, testRule.getMatchCallCount());

        // Test with null ResponseData
        testRule.setMatchResult(true);
        assertTrue(testRule.match(null));
        assertEquals(3, testRule.getMatchCallCount());
        assertNull(testRule.getLastResponseData());
    }

    /**
     * Test ConditionalAbstractRule implementation
     */
    @Test
    public void test_conditionalRule_implementation() {
        ConditionalAbstractRule conditionalRule = new ConditionalAbstractRule();
        conditionalRule.crawlerContainer = container;
        conditionalRule.setRuleId("conditionalRule");

        // Set patterns
        conditionalRule.setUrlPattern("https?://.*\\.example\\.com/.*");
        conditionalRule.setMimeTypePattern("text/.*");

        // Test matching
        ResponseData responseData1 = new ResponseData();
        responseData1.setUrl("http://www.example.com/page");
        responseData1.setMimeType("text/html");
        assertTrue(conditionalRule.match(responseData1));

        // Test non-matching URL
        ResponseData responseData2 = new ResponseData();
        responseData2.setUrl("http://other.com/page");
        responseData2.setMimeType("text/html");
        assertFalse(conditionalRule.match(responseData2));

        // Test non-matching MIME type
        ResponseData responseData3 = new ResponseData();
        responseData3.setUrl("http://www.example.com/page");
        responseData3.setMimeType("image/png");
        assertFalse(conditionalRule.match(responseData3));

        // Test null ResponseData
        assertFalse(conditionalRule.match(null));
    }

    /**
     * Test serialization of AbstractRule
     */
    @Test
    public void test_serialization() throws Exception {
        TestResponseProcessor processor = new TestResponseProcessor();
        testRule.setRuleId("serializeRule");
        testRule.setResponseProcessor(processor);
        testRule.setMatchResult(false);

        // Serialize
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(testRule);
        oos.close();

        // Deserialize
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        ObjectInputStream ois = new ObjectInputStream(bais);
        TestAbstractRule deserializedRule = (TestAbstractRule) ois.readObject();
        ois.close();

        // Verify
        assertEquals("serializeRule", deserializedRule.getRuleId());
        assertNotNull(deserializedRule.getResponseProcessor());

        // Note: crawlerContainer is transient (marked with @Resource)
        // so it won't be serialized
        assertNull(deserializedRule.crawlerContainer);
    }

    /**
     * Test with null CrawlerContainer
     */
    @Test
    public void test_nullCrawlerContainer() {
        TestAbstractRule ruleWithoutContainer = new TestAbstractRule();
        ruleWithoutContainer.setRuleId("noContainer");

        // Properties should work without container
        assertEquals("noContainer", ruleWithoutContainer.getRuleId());

        TestResponseProcessor processor = new TestResponseProcessor();
        ruleWithoutContainer.setResponseProcessor(processor);
        assertTrue(processor == ruleWithoutContainer.getResponseProcessor());

        // Match should work
        ResponseData responseData = new ResponseData();
        assertTrue(ruleWithoutContainer.match(responseData));

        // Register should fail with null container
        try {
            ruleWithoutContainer.register(0);
            fail();
        } catch (NullPointerException e) {
            // Expected
        }
    }

    /**
     * Test concurrent property access
     */
    @Test
    public void test_concurrentPropertyAccess() throws Exception {
        final int threadCount = 10;
        final int iterationsPerThread = 100;
        final CountDownLatch startLatch = new CountDownLatch(1);
        final CountDownLatch endLatch = new CountDownLatch(threadCount);
        final AtomicInteger errorCount = new AtomicInteger(0);

        // Set initial values
        testRule.setRuleId("concurrentRule");
        TestResponseProcessor processor = new TestResponseProcessor();
        testRule.setResponseProcessor(processor);

        for (int i = 0; i < threadCount; i++) {
            final int threadId = i;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        startLatch.await();
                        for (int j = 0; j < iterationsPerThread; j++) {
                            // Read properties
                            String ruleId = testRule.getRuleId();
                            ResponseProcessor proc = testRule.getResponseProcessor();

                            // Verify consistency
                            if (!"concurrentRule".equals(ruleId)) {
                                errorCount.incrementAndGet();
                            }
                            if (proc != processor) {
                                errorCount.incrementAndGet();
                            }

                            // Occasionally update (half the threads)
                            if (threadId % 2 == 0 && j % 10 == 0) {
                                testRule.setRuleId("concurrentRule");
                                testRule.setResponseProcessor(processor);
                            }
                        }
                    } catch (Exception e) {
                        errorCount.incrementAndGet();
                    } finally {
                        endLatch.countDown();
                    }
                }
            }).start();
        }

        startLatch.countDown();
        endLatch.await();

        assertEquals(0, errorCount.get());
        assertEquals("concurrentRule", testRule.getRuleId());
        assertTrue(processor == testRule.getResponseProcessor());
    }

    /**
     * Test rule chain with AbstractRule
     */
    @Test
    public void test_ruleChain() {
        // Create chain of rules
        TestAbstractRule rule1 = new TestAbstractRule();
        rule1.crawlerContainer = container;
        rule1.setRuleId("rule1");
        rule1.setMatchResult(false);

        TestAbstractRule rule2 = new TestAbstractRule();
        rule2.crawlerContainer = container;
        rule2.setRuleId("rule2");
        rule2.setMatchResult(true);

        TestAbstractRule rule3 = new TestAbstractRule();
        rule3.crawlerContainer = container;
        rule3.setRuleId("rule3");
        rule3.setMatchResult(false);

        // Register in order
        rule1.register(0);
        rule2.register(1);
        rule3.register(2);

        // Get matching rule
        ResponseData responseData = new ResponseData();
        Rule matchedRule = ruleManager.getRule(responseData);

        assertNotNull(matchedRule);
        assertEquals("rule2", matchedRule.getRuleId());
    }

    /**
     * Test edge cases for rule ID
     */
    @Test
    public void test_ruleId_edgeCases() {
        // Very long rule ID
        StringBuilder longId = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            longId.append("verylongruleid");
        }
        testRule.setRuleId(longId.toString());
        assertEquals(longId.toString(), testRule.getRuleId());

        // Special characters in rule ID
        testRule.setRuleId("rule-id_with.special@chars#123");
        assertEquals("rule-id_with.special@chars#123", testRule.getRuleId());

        // Unicode characters
        testRule.setRuleId("ルール日本語");
        assertEquals("ルール日本語", testRule.getRuleId());

        // Whitespace
        testRule.setRuleId("  rule with spaces  ");
        assertEquals("  rule with spaces  ", testRule.getRuleId());
    }

    /**
     * Test multiple registrations of same rule
     */
    @Test
    public void test_multipleRegistrations() {
        testRule.setRuleId("multiRegRule");

        // Register multiple times
        testRule.register(0);
        testRule.register(0);
        testRule.register(1);

        List<Rule> rules = ruleManager.getRules();
        assertEquals(3, rules.size());

        // All should be the same instance
        assertTrue(testRule == rules.get(0));
        assertTrue(testRule == rules.get(1));
        assertTrue(testRule == rules.get(2));
    }

    /**
     * Test property changes after registration
     */
    @Test
    public void test_propertyChangesAfterRegistration() {
        TestResponseProcessor processor1 = new TestResponseProcessor();
        testRule.setRuleId("originalId");
        testRule.setResponseProcessor(processor1);

        // Register
        testRule.register(0);

        // Verify initial state
        Rule registeredRule = ruleManager.getRules().get(0);
        assertEquals("originalId", registeredRule.getRuleId());
        assertTrue(processor1 == registeredRule.getResponseProcessor());

        // Change properties after registration
        TestResponseProcessor processor2 = new TestResponseProcessor();
        testRule.setRuleId("changedId");
        testRule.setResponseProcessor(processor2);

        // Verify changes are reflected (same object reference)
        assertEquals("changedId", registeredRule.getRuleId());
        assertTrue(processor2 == registeredRule.getResponseProcessor());
    }

    /**
     * Test inheritance behavior
     */
    @Test
    public void test_inheritanceBehavior() {
        // Test that subclass can override match method
        ConditionalAbstractRule conditionalRule = new ConditionalAbstractRule();
        TestAbstractRule testRule = new TestAbstractRule();

        ResponseData responseData = new ResponseData();
        responseData.setUrl("http://example.com");

        // Different implementations should behave differently
        conditionalRule.setUrlPattern("https://.*");
        assertFalse(conditionalRule.match(responseData)); // Doesn't match pattern

        testRule.setMatchResult(true);
        assertTrue(testRule.match(responseData)); // Always matches when set to true

        // Both should inherit same property behavior
        conditionalRule.setRuleId("conditional");
        testRule.setRuleId("test");

        assertEquals("conditional", conditionalRule.getRuleId());
        assertEquals("test", testRule.getRuleId());
    }

    /**
     * Test protected field access
     */
    @Test
    public void test_protectedFieldAccess() {
        // Test direct field access (protected fields)
        TestAbstractRule rule = new TestAbstractRule();

        // Direct field assignment
        rule.ruleId = "directFieldAccess";
        TestResponseProcessor processor = new TestResponseProcessor();
        rule.responseProcessor = processor;
        rule.crawlerContainer = container;

        // Verify through getters
        assertEquals("directFieldAccess", rule.getRuleId());
        assertTrue(processor == rule.getResponseProcessor());
        assertTrue(container == rule.crawlerContainer);

        // Register should work with direct field access
        rule.register(0);
        assertTrue(ruleManager.hasRule(rule));
    }
}
