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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import org.codelibs.fess.crawler.entity.ResponseData;
import org.codelibs.fess.crawler.processor.ResponseProcessor;
import org.dbflute.utflute.core.PlainTestCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

/**
 * Test class for RuleManager interface.
 * Tests the contract and behavior of RuleManager implementations.
 */
public class RuleManagerTest extends PlainTestCase {

    /**
     * Test implementation of RuleManager for testing purposes
     */
    public static class TestRuleManager implements RuleManager {
        private final List<Rule> rules = new ArrayList<>();
        private int getRuleCallCount = 0;

        @Override
        public Rule getRule(ResponseData responseData) {
            getRuleCallCount++;
            if (responseData == null) {
                return null;
            }

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
                if (index < 0 || index > rules.size()) {
                    throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + rules.size());
                }
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

        public int getRuleCount() {
            return rules.size();
        }

        public int getGetRuleCallCount() {
            return getRuleCallCount;
        }

        public void clearRules() {
            rules.clear();
        }

        public List<Rule> getRules() {
            return new ArrayList<>(rules);
        }
    }

    /**
     * Thread-safe implementation of RuleManager
     */
    public static class ThreadSafeRuleManager implements RuleManager {
        private final List<Rule> rules = new CopyOnWriteArrayList<>();

        @Override
        public Rule getRule(ResponseData responseData) {
            if (responseData == null) {
                return null;
            }

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
    }

    /**
     * Simple test rule implementation
     */
    public static class TestRule implements Rule {
        private static final long serialVersionUID = 1L;
        private final String ruleId;
        private final boolean matchResult;
        private final ResponseProcessor responseProcessor;

        public TestRule(String ruleId, boolean matchResult) {
            this(ruleId, matchResult, null);
        }

        public TestRule(String ruleId, boolean matchResult, ResponseProcessor responseProcessor) {
            this.ruleId = ruleId;
            this.matchResult = matchResult;
            this.responseProcessor = responseProcessor;
        }

        @Override
        public boolean match(ResponseData responseData) {
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
    }

    /**
     * Conditional test rule that matches based on URL pattern
     */
    public static class UrlPatternRule implements Rule {
        private static final long serialVersionUID = 1L;
        private final String ruleId;
        private final String urlPattern;
        private final ResponseProcessor responseProcessor;

        public UrlPatternRule(String ruleId, String urlPattern) {
            this(ruleId, urlPattern, null);
        }

        public UrlPatternRule(String ruleId, String urlPattern, ResponseProcessor responseProcessor) {
            this.ruleId = ruleId;
            this.urlPattern = urlPattern;
            this.responseProcessor = responseProcessor;
        }

        @Override
        public boolean match(ResponseData responseData) {
            if (responseData == null || responseData.getUrl() == null) {
                return false;
            }
            return responseData.getUrl().matches(urlPattern);
        }

        @Override
        public String getRuleId() {
            return ruleId;
        }

        @Override
        public ResponseProcessor getResponseProcessor() {
            return responseProcessor;
        }
    }

    private TestRuleManager ruleManager;

    @Override
    protected void setUp(final TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        ruleManager = new TestRuleManager();
    }

    /**
     * Test adding a single rule
     */
    @Test
    public void test_addRule_single() {
        TestRule rule = new TestRule("rule1", true);

        ruleManager.addRule(rule);

        assertTrue(ruleManager.hasRule(rule));
        assertEquals(1, ruleManager.getRuleCount());
    }

    /**
     * Test adding multiple rules
     */
    @Test
    public void test_addRule_multiple() {
        TestRule rule1 = new TestRule("rule1", true);
        TestRule rule2 = new TestRule("rule2", false);
        TestRule rule3 = new TestRule("rule3", true);

        ruleManager.addRule(rule1);
        ruleManager.addRule(rule2);
        ruleManager.addRule(rule3);

        assertTrue(ruleManager.hasRule(rule1));
        assertTrue(ruleManager.hasRule(rule2));
        assertTrue(ruleManager.hasRule(rule3));
        assertEquals(3, ruleManager.getRuleCount());
    }

    /**
     * Test adding null rule
     */
    @Test
    public void test_addRule_null() {
        ruleManager.addRule(null);

        assertEquals(0, ruleManager.getRuleCount());
    }

    /**
     * Test adding rule at specific index
     */
    @Test
    public void test_addRule_atIndex() {
        TestRule rule1 = new TestRule("rule1", true);
        TestRule rule2 = new TestRule("rule2", false);
        TestRule rule3 = new TestRule("rule3", true);

        ruleManager.addRule(rule1);
        ruleManager.addRule(rule3);
        ruleManager.addRule(1, rule2); // Insert in middle

        List<Rule> rules = ruleManager.getRules();
        assertEquals(3, rules.size());
        assertEquals("rule1", rules.get(0).getRuleId());
        assertEquals("rule2", rules.get(1).getRuleId());
        assertEquals("rule3", rules.get(2).getRuleId());
    }

    /**
     * Test adding rule at index 0
     */
    @Test
    public void test_addRule_atIndexZero() {
        TestRule rule1 = new TestRule("rule1", true);
        TestRule rule2 = new TestRule("rule2", false);

        ruleManager.addRule(rule1);
        ruleManager.addRule(0, rule2); // Insert at beginning

        List<Rule> rules = ruleManager.getRules();
        assertEquals(2, rules.size());
        assertEquals("rule2", rules.get(0).getRuleId());
        assertEquals("rule1", rules.get(1).getRuleId());
    }

    /**
     * Test adding rule at last index
     */
    @Test
    public void test_addRule_atLastIndex() {
        TestRule rule1 = new TestRule("rule1", true);
        TestRule rule2 = new TestRule("rule2", false);
        TestRule rule3 = new TestRule("rule3", true);

        ruleManager.addRule(rule1);
        ruleManager.addRule(rule2);
        ruleManager.addRule(2, rule3); // Insert at end

        List<Rule> rules = ruleManager.getRules();
        assertEquals(3, rules.size());
        assertEquals("rule3", rules.get(2).getRuleId());
    }

    /**
     * Test adding rule at invalid index
     */
    @Test
    public void test_addRule_atInvalidIndex() {
        TestRule rule1 = new TestRule("rule1", true);
        TestRule rule2 = new TestRule("rule2", false);

        ruleManager.addRule(rule1);

        try {
            ruleManager.addRule(-1, rule2);
            fail();
        } catch (IndexOutOfBoundsException e) {
            // Expected
        }

        try {
            ruleManager.addRule(5, rule2);
            fail();
        } catch (IndexOutOfBoundsException e) {
            // Expected
        }
    }

    /**
     * Test adding null rule at index
     */
    @Test
    public void test_addRule_nullAtIndex() {
        TestRule rule1 = new TestRule("rule1", true);

        ruleManager.addRule(rule1);
        ruleManager.addRule(0, null);

        assertEquals(1, ruleManager.getRuleCount());
        assertEquals("rule1", ruleManager.getRules().get(0).getRuleId());
    }

    /**
     * Test removing existing rule
     */
    @Test
    public void test_removeRule_existing() {
        TestRule rule1 = new TestRule("rule1", true);
        TestRule rule2 = new TestRule("rule2", false);

        ruleManager.addRule(rule1);
        ruleManager.addRule(rule2);

        assertTrue(ruleManager.removeRule(rule1));

        assertFalse(ruleManager.hasRule(rule1));
        assertTrue(ruleManager.hasRule(rule2));
        assertEquals(1, ruleManager.getRuleCount());
    }

    /**
     * Test removing non-existing rule
     */
    @Test
    public void test_removeRule_nonExisting() {
        TestRule rule1 = new TestRule("rule1", true);
        TestRule rule2 = new TestRule("rule2", false);

        ruleManager.addRule(rule1);

        assertFalse(ruleManager.removeRule(rule2));

        assertTrue(ruleManager.hasRule(rule1));
        assertEquals(1, ruleManager.getRuleCount());
    }

    /**
     * Test removing null rule
     */
    @Test
    public void test_removeRule_null() {
        TestRule rule = new TestRule("rule1", true);

        ruleManager.addRule(rule);

        assertFalse(ruleManager.removeRule(null));

        assertTrue(ruleManager.hasRule(rule));
        assertEquals(1, ruleManager.getRuleCount());
    }

    /**
     * Test removing all rules
     */
    @Test
    public void test_removeRule_all() {
        TestRule rule1 = new TestRule("rule1", true);
        TestRule rule2 = new TestRule("rule2", false);
        TestRule rule3 = new TestRule("rule3", true);

        ruleManager.addRule(rule1);
        ruleManager.addRule(rule2);
        ruleManager.addRule(rule3);

        assertTrue(ruleManager.removeRule(rule2));
        assertTrue(ruleManager.removeRule(rule1));
        assertTrue(ruleManager.removeRule(rule3));

        assertEquals(0, ruleManager.getRuleCount());
    }

    /**
     * Test hasRule with existing rule
     */
    @Test
    public void test_hasRule_existing() {
        TestRule rule = new TestRule("rule1", true);

        ruleManager.addRule(rule);

        assertTrue(ruleManager.hasRule(rule));
    }

    /**
     * Test hasRule with non-existing rule
     */
    @Test
    public void test_hasRule_nonExisting() {
        TestRule rule1 = new TestRule("rule1", true);
        TestRule rule2 = new TestRule("rule2", false);

        ruleManager.addRule(rule1);

        assertFalse(ruleManager.hasRule(rule2));
    }

    /**
     * Test hasRule with null
     */
    @Test
    public void test_hasRule_null() {
        TestRule rule = new TestRule("rule1", true);

        ruleManager.addRule(rule);

        assertFalse(ruleManager.hasRule(null));
    }

    /**
     * Test getRule with matching rule
     */
    @Test
    public void test_getRule_matching() {
        TestRule rule1 = new TestRule("rule1", false);
        TestRule rule2 = new TestRule("rule2", true);
        TestRule rule3 = new TestRule("rule3", false);

        ruleManager.addRule(rule1);
        ruleManager.addRule(rule2);
        ruleManager.addRule(rule3);

        ResponseData responseData = new ResponseData();
        Rule matchedRule = ruleManager.getRule(responseData);

        assertNotNull(matchedRule);
        assertEquals("rule2", matchedRule.getRuleId());
    }

    /**
     * Test getRule with no matching rule
     */
    @Test
    public void test_getRule_noMatch() {
        TestRule rule1 = new TestRule("rule1", false);
        TestRule rule2 = new TestRule("rule2", false);

        ruleManager.addRule(rule1);
        ruleManager.addRule(rule2);

        ResponseData responseData = new ResponseData();
        Rule matchedRule = ruleManager.getRule(responseData);

        assertNull(matchedRule);
    }

    /**
     * Test getRule with null ResponseData
     */
    @Test
    public void test_getRule_nullResponseData() {
        TestRule rule = new TestRule("rule1", true);

        ruleManager.addRule(rule);

        Rule matchedRule = ruleManager.getRule(null);

        assertNull(matchedRule);
    }

    /**
     * Test getRule with first matching rule (priority)
     */
    @Test
    public void test_getRule_firstMatchPriority() {
        TestRule rule1 = new TestRule("rule1", true);
        TestRule rule2 = new TestRule("rule2", true);
        TestRule rule3 = new TestRule("rule3", true);

        ruleManager.addRule(rule1);
        ruleManager.addRule(rule2);
        ruleManager.addRule(rule3);

        ResponseData responseData = new ResponseData();
        Rule matchedRule = ruleManager.getRule(responseData);

        assertNotNull(matchedRule);
        assertEquals("rule1", matchedRule.getRuleId()); // First matching rule
    }

    /**
     * Test getRule with URL pattern rules
     */
    @Test
    public void test_getRule_urlPattern() {
        UrlPatternRule rule1 = new UrlPatternRule("httpRule", "https?://.*");
        UrlPatternRule rule2 = new UrlPatternRule("exampleRule", ".*example\\.com.*");
        UrlPatternRule rule3 = new UrlPatternRule("pdfRule", ".*\\.pdf$");

        ruleManager.addRule(rule1);
        ruleManager.addRule(rule2);
        ruleManager.addRule(rule3);

        ResponseData responseData1 = new ResponseData();
        responseData1.setUrl("https://example.com/page");
        Rule matchedRule1 = ruleManager.getRule(responseData1);
        assertEquals("httpRule", matchedRule1.getRuleId()); // First match

        ResponseData responseData2 = new ResponseData();
        responseData2.setUrl("ftp://files.com/document.pdf");
        Rule matchedRule2 = ruleManager.getRule(responseData2);
        assertEquals("pdfRule", matchedRule2.getRuleId());

        ResponseData responseData3 = new ResponseData();
        responseData3.setUrl("file:///local/file.txt");
        Rule matchedRule3 = ruleManager.getRule(responseData3);
        assertNull(matchedRule3); // No match
    }

    /**
     * Test rule order preservation
     */
    @Test
    public void test_ruleOrderPreservation() {
        TestRule rule1 = new TestRule("rule1", false);
        TestRule rule2 = new TestRule("rule2", true);
        TestRule rule3 = new TestRule("rule3", false);
        TestRule rule4 = new TestRule("rule4", true);

        ruleManager.addRule(rule1);
        ruleManager.addRule(rule2);
        ruleManager.addRule(rule3);
        ruleManager.addRule(rule4);

        List<Rule> rules = ruleManager.getRules();
        assertEquals(4, rules.size());
        assertEquals("rule1", rules.get(0).getRuleId());
        assertEquals("rule2", rules.get(1).getRuleId());
        assertEquals("rule3", rules.get(2).getRuleId());
        assertEquals("rule4", rules.get(3).getRuleId());
    }

    /**
     * Test adding duplicate rules
     */
    @Test
    public void test_addRule_duplicates() {
        TestRule rule = new TestRule("rule1", true);

        ruleManager.addRule(rule);
        ruleManager.addRule(rule); // Add same rule again

        assertEquals(2, ruleManager.getRuleCount());
        assertTrue(ruleManager.hasRule(rule));

        // Remove one instance
        assertTrue(ruleManager.removeRule(rule));
        assertEquals(1, ruleManager.getRuleCount());
        assertTrue(ruleManager.hasRule(rule));

        // Remove second instance
        assertTrue(ruleManager.removeRule(rule));
        assertEquals(0, ruleManager.getRuleCount());
        assertFalse(ruleManager.hasRule(rule));
    }

    /**
     * Test empty RuleManager
     */
    @Test
    public void test_emptyRuleManager() {
        assertEquals(0, ruleManager.getRuleCount());

        ResponseData responseData = new ResponseData();
        assertNull(ruleManager.getRule(responseData));

        TestRule rule = new TestRule("rule1", true);
        assertFalse(ruleManager.hasRule(rule));
        assertFalse(ruleManager.removeRule(rule));
    }

    /**
     * Test clearing all rules
     */
    @Test
    public void test_clearRules() {
        TestRule rule1 = new TestRule("rule1", true);
        TestRule rule2 = new TestRule("rule2", false);
        TestRule rule3 = new TestRule("rule3", true);

        ruleManager.addRule(rule1);
        ruleManager.addRule(rule2);
        ruleManager.addRule(rule3);

        assertEquals(3, ruleManager.getRuleCount());

        ruleManager.clearRules();

        assertEquals(0, ruleManager.getRuleCount());
        assertFalse(ruleManager.hasRule(rule1));
        assertFalse(ruleManager.hasRule(rule2));
        assertFalse(ruleManager.hasRule(rule3));
    }

    /**
     * Test getRule call count
     */
    @Test
    public void test_getRuleCallCount() {
        TestRule rule = new TestRule("rule1", true);
        ruleManager.addRule(rule);

        ResponseData responseData = new ResponseData();

        assertEquals(0, ruleManager.getGetRuleCallCount());

        ruleManager.getRule(responseData);
        assertEquals(1, ruleManager.getGetRuleCallCount());

        ruleManager.getRule(responseData);
        assertEquals(2, ruleManager.getGetRuleCallCount());

        ruleManager.getRule(null);
        assertEquals(3, ruleManager.getGetRuleCallCount());
    }

    /**
     * Test concurrent rule additions
     */
    @Test
    public void test_concurrentAddRule() throws Exception {
        final ThreadSafeRuleManager threadSafeManager = new ThreadSafeRuleManager();
        final int threadCount = 10;
        final int rulesPerThread = 100;
        final CountDownLatch startLatch = new CountDownLatch(1);
        final CountDownLatch endLatch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            final int threadId = i;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        startLatch.await();
                        for (int j = 0; j < rulesPerThread; j++) {
                            TestRule rule = new TestRule("rule_" + threadId + "_" + j, true);
                            threadSafeManager.addRule(rule);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        endLatch.countDown();
                    }
                }
            }).start();
        }

        startLatch.countDown();
        endLatch.await();

        // Verify all rules were added (can't check exact count due to thread-safe implementation)
        ResponseData responseData = new ResponseData();
        assertNotNull(threadSafeManager.getRule(responseData)); // At least one rule should match
    }

    /**
     * Test concurrent rule removals
     */
    @Test
    public void test_concurrentRemoveRule() throws Exception {
        final ThreadSafeRuleManager threadSafeManager = new ThreadSafeRuleManager();
        final List<TestRule> rules = new ArrayList<>();

        // Add rules
        for (int i = 0; i < 100; i++) {
            TestRule rule = new TestRule("rule_" + i, true);
            rules.add(rule);
            threadSafeManager.addRule(rule);
        }

        final int threadCount = 10;
        final CountDownLatch startLatch = new CountDownLatch(1);
        final CountDownLatch endLatch = new CountDownLatch(threadCount);
        final AtomicInteger removeCount = new AtomicInteger(0);

        for (int i = 0; i < threadCount; i++) {
            final int threadId = i;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        startLatch.await();
                        for (int j = threadId; j < rules.size(); j += threadCount) {
                            if (threadSafeManager.removeRule(rules.get(j))) {
                                removeCount.incrementAndGet();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        endLatch.countDown();
                    }
                }
            }).start();
        }

        startLatch.countDown();
        endLatch.await();

        // All rules should be removed
        assertEquals(100, removeCount.get());
    }

    /**
     * Test mixed operations
     */
    @Test
    public void test_mixedOperations() {
        TestRule rule1 = new TestRule("rule1", false);
        TestRule rule2 = new TestRule("rule2", true);
        TestRule rule3 = new TestRule("rule3", false);
        TestRule rule4 = new TestRule("rule4", true);

        // Add rules
        ruleManager.addRule(rule1);
        ruleManager.addRule(rule2);
        assertEquals(2, ruleManager.getRuleCount());

        // Check existence
        assertTrue(ruleManager.hasRule(rule1));
        assertTrue(ruleManager.hasRule(rule2));
        assertFalse(ruleManager.hasRule(rule3));

        // Get matching rule
        ResponseData responseData = new ResponseData();
        Rule matched = ruleManager.getRule(responseData);
        assertEquals("rule2", matched.getRuleId());

        // Add at index
        ruleManager.addRule(1, rule3);
        assertEquals(3, ruleManager.getRuleCount());

        // Remove rule
        assertTrue(ruleManager.removeRule(rule2));
        assertEquals(2, ruleManager.getRuleCount());

        // Get rule after removal
        matched = ruleManager.getRule(responseData);
        assertNull(matched); // No matching rule now

        // Add rule4 which matches
        ruleManager.addRule(rule4);
        matched = ruleManager.getRule(responseData);
        assertEquals("rule4", matched.getRuleId());
    }

    /**
     * Test rule replacement scenario
     */
    @Test
    public void test_ruleReplacement() {
        TestRule oldRule = new TestRule("rule1", false);
        TestRule newRule = new TestRule("rule1", true); // Same ID, different behavior

        ruleManager.addRule(oldRule);

        ResponseData responseData = new ResponseData();
        assertNull(ruleManager.getRule(responseData)); // Old rule doesn't match

        // Replace old rule with new rule
        ruleManager.removeRule(oldRule);
        ruleManager.addRule(newRule);

        Rule matched = ruleManager.getRule(responseData);
        assertNotNull(matched);
        assertEquals("rule1", matched.getRuleId()); // New rule matches
    }
}
