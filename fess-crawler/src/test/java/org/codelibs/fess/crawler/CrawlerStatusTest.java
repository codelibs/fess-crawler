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
package org.codelibs.fess.crawler;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

import org.dbflute.utflute.core.PlainTestCase;

/**
 * Test class for CrawlerStatus enum.
 * Tests all enum functionality including values, valueOf, ordinal, and serialization.
 */
public class CrawlerStatusTest extends PlainTestCase {

    /**
     * Test that all expected enum values exist
     */
    public void test_enumValues() {
        CrawlerStatus[] values = CrawlerStatus.values();

        assertNotNull(values);
        assertEquals(3, values.length);

        // Verify all expected values exist
        assertEquals(CrawlerStatus.INITIALIZING, values[0]);
        assertEquals(CrawlerStatus.RUNNING, values[1]);
        assertEquals(CrawlerStatus.DONE, values[2]);
    }

    /**
     * Test valueOf method with valid values
     */
    public void test_valueOf_valid() {
        assertEquals(CrawlerStatus.INITIALIZING, CrawlerStatus.valueOf("INITIALIZING"));
        assertEquals(CrawlerStatus.RUNNING, CrawlerStatus.valueOf("RUNNING"));
        assertEquals(CrawlerStatus.DONE, CrawlerStatus.valueOf("DONE"));
    }

    /**
     * Test valueOf method with invalid values
     */
    public void test_valueOf_invalid() {
        try {
            CrawlerStatus.valueOf("INVALID");
            fail("Should throw IllegalArgumentException for invalid value");
        } catch (IllegalArgumentException e) {
            // Expected
            assertTrue(e.getMessage().contains("INVALID"));
        }

        try {
            CrawlerStatus.valueOf("initializing"); // lowercase
            fail("Should throw IllegalArgumentException for lowercase value");
        } catch (IllegalArgumentException e) {
            // Expected
            assertTrue(e.getMessage().contains("initializing"));
        }

        try {
            CrawlerStatus.valueOf("");
            fail("Should throw IllegalArgumentException for empty string");
        } catch (IllegalArgumentException e) {
            // Expected
        }

        try {
            CrawlerStatus.valueOf(" RUNNING "); // with spaces
            fail("Should throw IllegalArgumentException for value with spaces");
        } catch (IllegalArgumentException e) {
            // Expected
        }
    }

    /**
     * Test valueOf method with null
     */
    public void test_valueOf_null() {
        try {
            CrawlerStatus.valueOf(null);
            fail("Should throw NullPointerException for null value");
        } catch (NullPointerException e) {
            // Expected
        }
    }

    /**
     * Test name method
     */
    public void test_name() {
        assertEquals("INITIALIZING", CrawlerStatus.INITIALIZING.name());
        assertEquals("RUNNING", CrawlerStatus.RUNNING.name());
        assertEquals("DONE", CrawlerStatus.DONE.name());
    }

    /**
     * Test ordinal method
     */
    public void test_ordinal() {
        assertEquals(0, CrawlerStatus.INITIALIZING.ordinal());
        assertEquals(1, CrawlerStatus.RUNNING.ordinal());
        assertEquals(2, CrawlerStatus.DONE.ordinal());
    }

    /**
     * Test toString method
     */
    public void test_toString() {
        assertEquals("INITIALIZING", CrawlerStatus.INITIALIZING.toString());
        assertEquals("RUNNING", CrawlerStatus.RUNNING.toString());
        assertEquals("DONE", CrawlerStatus.DONE.toString());
    }

    /**
     * Test equals method
     */
    public void test_equals() {
        // Same instance
        assertTrue(CrawlerStatus.INITIALIZING.equals(CrawlerStatus.INITIALIZING));
        assertTrue(CrawlerStatus.RUNNING.equals(CrawlerStatus.RUNNING));
        assertTrue(CrawlerStatus.DONE.equals(CrawlerStatus.DONE));

        // Different instances
        assertFalse(CrawlerStatus.INITIALIZING.equals(CrawlerStatus.RUNNING));
        assertFalse(CrawlerStatus.INITIALIZING.equals(CrawlerStatus.DONE));
        assertFalse(CrawlerStatus.RUNNING.equals(CrawlerStatus.DONE));

        // Null comparison
        assertFalse(CrawlerStatus.INITIALIZING.equals(null));

        // Different type comparison
        assertFalse(CrawlerStatus.INITIALIZING.equals("INITIALIZING"));
        assertFalse(CrawlerStatus.INITIALIZING.equals(0));
    }

    /**
     * Test hashCode method
     */
    public void test_hashCode() {
        // Same status should have same hashCode
        CrawlerStatus status1 = CrawlerStatus.INITIALIZING;
        CrawlerStatus status2 = CrawlerStatus.INITIALIZING;
        assertEquals(status1.hashCode(), status2.hashCode());

        // Different statuses likely have different hashCodes (not guaranteed but typical)
        Set<Integer> hashCodes = new HashSet<>();
        hashCodes.add(CrawlerStatus.INITIALIZING.hashCode());
        hashCodes.add(CrawlerStatus.RUNNING.hashCode());
        hashCodes.add(CrawlerStatus.DONE.hashCode());

        // While not guaranteed, typically enum hashCodes are different
        assertTrue(hashCodes.size() > 1);
    }

    /**
     * Test compareTo method
     */
    public void test_compareTo() {
        // Compare with self
        assertEquals(0, CrawlerStatus.INITIALIZING.compareTo(CrawlerStatus.INITIALIZING));
        assertEquals(0, CrawlerStatus.RUNNING.compareTo(CrawlerStatus.RUNNING));
        assertEquals(0, CrawlerStatus.DONE.compareTo(CrawlerStatus.DONE));

        // INITIALIZING < RUNNING < DONE (based on ordinal)
        assertTrue(CrawlerStatus.INITIALIZING.compareTo(CrawlerStatus.RUNNING) < 0);
        assertTrue(CrawlerStatus.INITIALIZING.compareTo(CrawlerStatus.DONE) < 0);
        assertTrue(CrawlerStatus.RUNNING.compareTo(CrawlerStatus.DONE) < 0);

        // Reverse comparisons
        assertTrue(CrawlerStatus.RUNNING.compareTo(CrawlerStatus.INITIALIZING) > 0);
        assertTrue(CrawlerStatus.DONE.compareTo(CrawlerStatus.INITIALIZING) > 0);
        assertTrue(CrawlerStatus.DONE.compareTo(CrawlerStatus.RUNNING) > 0);
    }

    /**
     * Test getDeclaringClass method
     */
    public void test_getDeclaringClass() {
        assertEquals(CrawlerStatus.class, CrawlerStatus.INITIALIZING.getDeclaringClass());
        assertEquals(CrawlerStatus.class, CrawlerStatus.RUNNING.getDeclaringClass());
        assertEquals(CrawlerStatus.class, CrawlerStatus.DONE.getDeclaringClass());
    }

    /**
     * Test serialization and deserialization
     */
    public void test_serialization() throws Exception {
        // Test each enum value
        CrawlerStatus[] statuses = CrawlerStatus.values();

        for (CrawlerStatus original : statuses) {
            // Serialize
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(original);
            oos.close();

            // Deserialize
            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            ObjectInputStream ois = new ObjectInputStream(bais);
            CrawlerStatus deserialized = (CrawlerStatus) ois.readObject();
            ois.close();

            // Verify - enum instances are singletons
            assertSame(original, deserialized);
            assertEquals(original.name(), deserialized.name());
            assertEquals(original.ordinal(), deserialized.ordinal());
        }
    }

    /**
     * Test EnumSet functionality
     */
    public void test_enumSet() {
        // Create EnumSet with all values
        EnumSet<CrawlerStatus> allStatuses = EnumSet.allOf(CrawlerStatus.class);
        assertEquals(3, allStatuses.size());
        assertTrue(allStatuses.contains(CrawlerStatus.INITIALIZING));
        assertTrue(allStatuses.contains(CrawlerStatus.RUNNING));
        assertTrue(allStatuses.contains(CrawlerStatus.DONE));

        // Create EnumSet with specific values
        EnumSet<CrawlerStatus> activeStatuses = EnumSet.of(CrawlerStatus.INITIALIZING, CrawlerStatus.RUNNING);
        assertEquals(2, activeStatuses.size());
        assertTrue(activeStatuses.contains(CrawlerStatus.INITIALIZING));
        assertTrue(activeStatuses.contains(CrawlerStatus.RUNNING));
        assertFalse(activeStatuses.contains(CrawlerStatus.DONE));

        // Create empty EnumSet
        EnumSet<CrawlerStatus> emptySet = EnumSet.noneOf(CrawlerStatus.class);
        assertEquals(0, emptySet.size());

        // Create EnumSet with range
        EnumSet<CrawlerStatus> rangeSet = EnumSet.range(CrawlerStatus.INITIALIZING, CrawlerStatus.RUNNING);
        assertEquals(2, rangeSet.size());
        assertTrue(rangeSet.contains(CrawlerStatus.INITIALIZING));
        assertTrue(rangeSet.contains(CrawlerStatus.RUNNING));
        assertFalse(rangeSet.contains(CrawlerStatus.DONE));
    }

    /**
     * Test switch statement usage
     */
    public void test_switchStatement() {
        for (CrawlerStatus status : CrawlerStatus.values()) {
            String description = getStatusDescription(status);
            assertNotNull(description);
            assertFalse(description.isEmpty());
        }
    }

    /**
     * Helper method to test switch statement
     */
    private String getStatusDescription(CrawlerStatus status) {
        switch (status) {
        case INITIALIZING:
            return "Crawler is initializing";
        case RUNNING:
            return "Crawler is running";
        case DONE:
            return "Crawler has completed";
        default:
            fail("Unexpected status: " + status);
            return null;
        }
    }

    /**
     * Test state transitions (typical workflow)
     */
    public void test_stateTransitions() {
        // Simulate typical crawler lifecycle
        CrawlerStatus currentStatus = CrawlerStatus.INITIALIZING;
        assertEquals(CrawlerStatus.INITIALIZING, currentStatus);

        // Transition to RUNNING
        currentStatus = CrawlerStatus.RUNNING;
        assertEquals(CrawlerStatus.RUNNING, currentStatus);

        // Transition to DONE
        currentStatus = CrawlerStatus.DONE;
        assertEquals(CrawlerStatus.DONE, currentStatus);

        // Verify ordinal progression
        assertTrue(CrawlerStatus.INITIALIZING.ordinal() < CrawlerStatus.RUNNING.ordinal());
        assertTrue(CrawlerStatus.RUNNING.ordinal() < CrawlerStatus.DONE.ordinal());
    }

    /**
     * Test usage in conditional statements
     */
    public void test_conditionalStatements() {
        CrawlerStatus status = CrawlerStatus.RUNNING;

        // Test with if-else
        if (status == CrawlerStatus.INITIALIZING) {
            fail("Should not be INITIALIZING");
        } else if (status == CrawlerStatus.RUNNING) {
            // Expected
            assertTrue(true);
        } else if (status == CrawlerStatus.DONE) {
            fail("Should not be DONE");
        }

        // Test with ternary operator
        String message = (status == CrawlerStatus.RUNNING) ? "Running" : "Not Running";
        assertEquals("Running", message);

        // Test with logical operators
        assertTrue(status == CrawlerStatus.RUNNING || status == CrawlerStatus.DONE);
        assertFalse(status == CrawlerStatus.INITIALIZING && status == CrawlerStatus.DONE);
    }

    /**
     * Test null safety in comparisons
     */
    public void test_nullSafety() {
        CrawlerStatus status = CrawlerStatus.INITIALIZING;
        CrawlerStatus nullStatus = null;

        // Enum == null should be false
        assertFalse(status == nullStatus);
        assertFalse(status.equals(nullStatus));

        // null == Enum should be false
        assertFalse(nullStatus == status);

        // Verify no NullPointerException when comparing with ==
        boolean result = (nullStatus == CrawlerStatus.INITIALIZING);
        assertFalse(result);
    }

    /**
     * Test thread safety of enum
     */
    public void test_threadSafety() throws Exception {
        final int threadCount = 100;
        final int iterationsPerThread = 1000;
        final Set<Exception> exceptions = new HashSet<>();

        Thread[] threads = new Thread[threadCount];
        for (int i = 0; i < threadCount; i++) {
            threads[i] = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        for (int j = 0; j < iterationsPerThread; j++) {
                            // Access enum values
                            CrawlerStatus[] values = CrawlerStatus.values();
                            assertEquals(3, values.length);

                            // Use valueOf
                            CrawlerStatus status = CrawlerStatus.valueOf("RUNNING");
                            assertEquals(CrawlerStatus.RUNNING, status);

                            // Compare values
                            assertTrue(CrawlerStatus.INITIALIZING.compareTo(CrawlerStatus.DONE) < 0);
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

        // Wait for all threads to complete
        for (Thread thread : threads) {
            thread.join();
        }

        // Verify no exceptions
        assertTrue(exceptions.isEmpty());
    }

    /**
     * Test memory reference equality
     */
    public void test_memoryReferenceEquality() {
        // Enum instances are singletons
        CrawlerStatus status1 = CrawlerStatus.INITIALIZING;
        CrawlerStatus status2 = CrawlerStatus.INITIALIZING;

        // Should be the same instance
        assertSame(status1, status2);
        assertTrue(status1 == status2);

        // valueOf should return the same instance
        CrawlerStatus status3 = CrawlerStatus.valueOf("INITIALIZING");
        assertSame(status1, status3);
        assertTrue(status1 == status3);

        // values() array contains the same instances
        CrawlerStatus status4 = CrawlerStatus.values()[0];
        assertSame(status1, status4);
        assertTrue(status1 == status4);
    }

    /**
     * Test enum as map key
     */
    public void test_enumAsMapKey() {
        java.util.Map<CrawlerStatus, String> statusMap = new java.util.HashMap<>();

        // Add entries
        statusMap.put(CrawlerStatus.INITIALIZING, "Starting up");
        statusMap.put(CrawlerStatus.RUNNING, "In progress");
        statusMap.put(CrawlerStatus.DONE, "Completed");

        // Verify entries
        assertEquals(3, statusMap.size());
        assertEquals("Starting up", statusMap.get(CrawlerStatus.INITIALIZING));
        assertEquals("In progress", statusMap.get(CrawlerStatus.RUNNING));
        assertEquals("Completed", statusMap.get(CrawlerStatus.DONE));

        // Test with EnumMap for better performance
        java.util.EnumMap<CrawlerStatus, String> enumMap = new java.util.EnumMap<>(CrawlerStatus.class);
        enumMap.put(CrawlerStatus.INITIALIZING, "Init");
        enumMap.put(CrawlerStatus.RUNNING, "Run");
        enumMap.put(CrawlerStatus.DONE, "Done");

        assertEquals(3, enumMap.size());
        assertTrue(enumMap.containsKey(CrawlerStatus.INITIALIZING));
        assertTrue(enumMap.containsKey(CrawlerStatus.RUNNING));
        assertTrue(enumMap.containsKey(CrawlerStatus.DONE));
    }
}
