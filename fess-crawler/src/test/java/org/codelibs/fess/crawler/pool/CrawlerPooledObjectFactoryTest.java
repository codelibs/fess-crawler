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
package org.codelibs.fess.crawler.pool;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.codelibs.fess.crawler.container.StandardCrawlerContainer;
import org.codelibs.fess.crawler.pool.CrawlerPooledObjectFactory.OnDestroyListener;
import org.dbflute.utflute.core.PlainTestCase;

/**
 * Test class for CrawlerPooledObjectFactory.
 * Tests the pooled object factory functionality including object creation,
 * wrapping, destruction, and listener mechanisms.
 */
public class CrawlerPooledObjectFactoryTest extends PlainTestCase {

    private CrawlerPooledObjectFactory<TestComponent> factory;
    private StandardCrawlerContainer container;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        // Reset counters before each test
        TestComponent.resetCounter();
        SingletonTestComponent.resetInstanceCount();

        // Initialize container with test components
        container = new StandardCrawlerContainer().prototype("testComponent", TestComponent.class)
                .singleton("singletonComponent", SingletonTestComponent.class)
                .singleton("factory", CrawlerPooledObjectFactory.class);

        factory = new CrawlerPooledObjectFactory<>();
        factory.crawlerContainer = container;
    }

    /**
     * Test component class for testing factory operations
     */
    public static class TestComponent {
        private static AtomicInteger instanceCounter = new AtomicInteger(0);
        private final int id;
        private boolean destroyed = false;

        public TestComponent() {
            this.id = instanceCounter.incrementAndGet();
        }

        public int getId() {
            return id;
        }

        public boolean isDestroyed() {
            return destroyed;
        }

        public void destroy() {
            this.destroyed = true;
        }

        public static void resetCounter() {
            instanceCounter.set(0);
        }
    }

    /**
     * Singleton test component for testing singleton behavior
     */
    public static class SingletonTestComponent {
        private static int instanceCount = 0;

        public SingletonTestComponent() {
            instanceCount++;
        }

        public static int getInstanceCount() {
            return instanceCount;
        }

        public static void resetInstanceCount() {
            instanceCount = 0;
        }
    }

    /**
     * Test basic object creation
     */
    public void test_create_basic() throws Exception {
        factory.setComponentName("testComponent");

        TestComponent component = factory.create();
        assertNotNull(component);
        assertEquals(1, component.getId());

        TestComponent component2 = factory.create();
        assertNotNull(component2);
        assertEquals(2, component2.getId());

        // Different instances for prototype
        assertNotSame(component, component2);
    }

    /**
     * Test creation with null component name
     */
    public void test_create_nullComponentName() {
        factory.setComponentName(null);

        try {
            factory.create();
            fail("Should throw exception for null component name");
        } catch (Exception e) {
            // Expected behavior
            assertTrue(true);
        }
    }

    /**
     * Test creation with invalid component name
     */
    public void test_create_invalidComponentName() {
        factory.setComponentName("nonExistentComponent");

        try {
            Object result = factory.create();
            // The container may return null for non-existent components
            assertNull("Should return null for invalid component name", result);
        } catch (Exception e) {
            // Exception is also acceptable behavior
            assertTrue(true);
        }
    }

    /**
     * Test creation with singleton component
     */
    public void test_create_singletonComponent() throws Exception {
        factory.setComponentName("singletonComponent");

        Object component1 = factory.create();
        Object component2 = factory.create();

        assertNotNull(component1);
        assertNotNull(component2);

        // Should be same instance for singleton
        assertSame(component1, component2);
        // The first creation should increment the counter
        assertTrue("Instance count should be at least 1", SingletonTestComponent.getInstanceCount() >= 1);
    }

    /**
     * Test wrap method
     */
    public void test_wrap_basic() {
        TestComponent component = new TestComponent();

        PooledObject<TestComponent> pooledObject = factory.wrap(component);

        assertNotNull(pooledObject);
        assertTrue(pooledObject instanceof DefaultPooledObject);
        assertSame(component, pooledObject.getObject());
    }

    /**
     * Test wrap with null object
     */
    public void test_wrap_nullObject() {
        PooledObject<TestComponent> pooledObject = factory.wrap(null);

        assertNotNull(pooledObject);
        assertNull(pooledObject.getObject());
    }

    /**
     * Test destroyObject without listener
     */
    public void test_destroyObject_noListener() throws Exception {
        TestComponent component = new TestComponent();
        PooledObject<TestComponent> pooledObject = new DefaultPooledObject<>(component);

        // Should not throw exception even without listener
        factory.destroyObject(pooledObject);

        // Component should remain unchanged without listener
        assertFalse(component.isDestroyed());
    }

    /**
     * Test destroyObject with listener
     */
    public void test_destroyObject_withListener() throws Exception {
        TestComponent component = new TestComponent();
        PooledObject<TestComponent> pooledObject = new DefaultPooledObject<>(component);

        AtomicBoolean listenerCalled = new AtomicBoolean(false);
        final PooledObject<TestComponent>[] capturedObject = new PooledObject[1];

        factory.setOnDestroyListener(new OnDestroyListener<TestComponent>() {
            @Override
            public void onDestroy(PooledObject<TestComponent> p) {
                listenerCalled.set(true);
                capturedObject[0] = p;
                p.getObject().destroy();
            }
        });

        factory.destroyObject(pooledObject);

        assertTrue(listenerCalled.get());
        assertSame(pooledObject, capturedObject[0]);
        assertTrue(component.isDestroyed());
    }

    /**
     * Test destroyObject with null pooled object
     */
    public void test_destroyObject_nullPooledObject() throws Exception {
        AtomicBoolean listenerCalled = new AtomicBoolean(false);

        factory.setOnDestroyListener(new OnDestroyListener<TestComponent>() {
            @Override
            public void onDestroy(PooledObject<TestComponent> p) {
                listenerCalled.set(true);
            }
        });

        factory.destroyObject(null);

        // Listener should be called even with null
        assertTrue(listenerCalled.get());
    }

    /**
     * Test destroyObject with exception in listener
     */
    public void test_destroyObject_listenerException() {
        TestComponent component = new TestComponent();
        PooledObject<TestComponent> pooledObject = new DefaultPooledObject<>(component);

        factory.setOnDestroyListener(new OnDestroyListener<TestComponent>() {
            @Override
            public void onDestroy(PooledObject<TestComponent> p) {
                throw new RuntimeException("Test exception in listener");
            }
        });

        try {
            factory.destroyObject(pooledObject);
            fail("Should propagate exception from listener");
        } catch (Exception e) {
            assertTrue(e.getMessage().contains("Test exception"));
        }
    }

    /**
     * Test getter and setter for componentName
     */
    public void test_componentName_getterSetter() {
        assertNull(factory.getComponentName());

        factory.setComponentName("testName");
        assertEquals("testName", factory.getComponentName());

        factory.setComponentName(null);
        assertNull(factory.getComponentName());

        factory.setComponentName("");
        assertEquals("", factory.getComponentName());
    }

    /**
     * Test getter and setter for onDestroyListener
     */
    public void test_onDestroyListener_getterSetter() {
        assertNull(factory.getOnDestroyListener());

        OnDestroyListener<TestComponent> listener = new OnDestroyListener<TestComponent>() {
            @Override
            public void onDestroy(PooledObject<TestComponent> p) {
                // Empty implementation
            }
        };

        factory.setOnDestroyListener(listener);
        assertSame(listener, factory.getOnDestroyListener());

        factory.setOnDestroyListener(null);
        assertNull(factory.getOnDestroyListener());
    }

    /**
     * Test multiple listener changes
     */
    public void test_multipleListenerChanges() throws Exception {
        TestComponent component = new TestComponent();
        PooledObject<TestComponent> pooledObject = new DefaultPooledObject<>(component);

        List<String> callOrder = new ArrayList<>();

        // First listener
        factory.setOnDestroyListener(new OnDestroyListener<TestComponent>() {
            @Override
            public void onDestroy(PooledObject<TestComponent> p) {
                callOrder.add("listener1");
            }
        });

        factory.destroyObject(pooledObject);
        assertEquals(1, callOrder.size());
        assertEquals("listener1", callOrder.get(0));

        // Change listener
        factory.setOnDestroyListener(new OnDestroyListener<TestComponent>() {
            @Override
            public void onDestroy(PooledObject<TestComponent> p) {
                callOrder.add("listener2");
            }
        });

        factory.destroyObject(pooledObject);
        assertEquals(2, callOrder.size());
        assertEquals("listener2", callOrder.get(1));

        // Remove listener
        factory.setOnDestroyListener(null);
        factory.destroyObject(pooledObject);
        assertEquals(2, callOrder.size()); // Should not add more
    }

    /**
     * Test lifecycle: create, wrap, destroy
     */
    public void test_fullLifecycle() throws Exception {
        TestComponent.resetCounter();
        factory.setComponentName("testComponent");

        final List<TestComponent> destroyedComponents = new ArrayList<>();
        factory.setOnDestroyListener(new OnDestroyListener<TestComponent>() {
            @Override
            public void onDestroy(PooledObject<TestComponent> p) {
                TestComponent component = p.getObject();
                component.destroy();
                destroyedComponents.add(component);
            }
        });

        // Create
        TestComponent component = factory.create();
        assertNotNull(component);
        assertEquals(1, component.getId());
        assertFalse(component.isDestroyed());

        // Wrap
        PooledObject<TestComponent> pooledObject = factory.wrap(component);
        assertNotNull(pooledObject);
        assertSame(component, pooledObject.getObject());

        // Destroy
        factory.destroyObject(pooledObject);
        assertTrue(component.isDestroyed());
        assertEquals(1, destroyedComponents.size());
        assertSame(component, destroyedComponents.get(0));
    }

    /**
     * Test concurrent object creation
     */
    public void test_concurrentCreation() throws Exception {
        TestComponent.resetCounter();
        factory.setComponentName("testComponent");

        final int threadCount = 10;
        final int objectsPerThread = 10;
        final List<TestComponent> createdComponents = new ArrayList<>();
        final List<Exception> exceptions = new ArrayList<>();

        Thread[] threads = new Thread[threadCount];
        for (int i = 0; i < threadCount; i++) {
            threads[i] = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        for (int j = 0; j < objectsPerThread; j++) {
                            TestComponent component = factory.create();
                            synchronized (createdComponents) {
                                createdComponents.add(component);
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

        // Wait for all threads to complete
        for (Thread thread : threads) {
            thread.join();
        }

        // Verify results
        assertTrue(exceptions.isEmpty());
        assertEquals(threadCount * objectsPerThread, createdComponents.size());

        // Check all components are unique (for prototype)
        for (int i = 0; i < createdComponents.size(); i++) {
            for (int j = i + 1; j < createdComponents.size(); j++) {
                assertNotSame(createdComponents.get(i), createdComponents.get(j));
            }
        }
    }

    /**
     * Test concurrent destroy operations
     */
    public void test_concurrentDestroy() throws Exception {
        final AtomicInteger destroyCount = new AtomicInteger(0);

        factory.setOnDestroyListener(new OnDestroyListener<TestComponent>() {
            @Override
            public void onDestroy(PooledObject<TestComponent> p) {
                destroyCount.incrementAndGet();
                if (p != null && p.getObject() != null) {
                    p.getObject().destroy();
                }
            }
        });

        final int threadCount = 10;
        final int destroysPerThread = 10;
        final List<Exception> exceptions = new ArrayList<>();

        Thread[] threads = new Thread[threadCount];
        for (int i = 0; i < threadCount; i++) {
            threads[i] = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        for (int j = 0; j < destroysPerThread; j++) {
                            TestComponent component = new TestComponent();
                            PooledObject<TestComponent> pooledObject = new DefaultPooledObject<>(component);
                            factory.destroyObject(pooledObject);
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

        // Verify results
        assertTrue(exceptions.isEmpty());
        assertEquals(threadCount * destroysPerThread, destroyCount.get());
    }

    /**
     * Test with different component types
     */
    public void test_differentComponentTypes() throws Exception {
        // Test with String component
        container.singleton("stringComponent", "TestString");
        CrawlerPooledObjectFactory<String> stringFactory = new CrawlerPooledObjectFactory<>();
        stringFactory.crawlerContainer = container;
        stringFactory.setComponentName("stringComponent");

        String stringComponent = stringFactory.create();
        assertEquals("TestString", stringComponent);

        PooledObject<String> pooledString = stringFactory.wrap(stringComponent);
        assertNotNull(pooledString);
        assertEquals("TestString", pooledString.getObject());
    }

    /**
     * Test edge cases for wrap method
     */
    public void test_wrap_edgeCases() {
        // Test with various object types
        TestComponent component = new TestComponent();
        PooledObject<TestComponent> pooled1 = factory.wrap(component);
        assertNotNull(pooled1);

        // Test wrapping same object multiple times
        PooledObject<TestComponent> pooled2 = factory.wrap(component);
        assertNotNull(pooled2);
        assertNotSame(pooled1, pooled2); // Different PooledObject instances
        assertSame(pooled1.getObject(), pooled2.getObject()); // Same wrapped object
    }

    /**
     * Test factory reusability
     */
    public void test_factoryReusability() throws Exception {
        TestComponent.resetCounter();

        // Use factory with first component type
        factory.setComponentName("testComponent");
        TestComponent comp1 = factory.create();
        assertEquals(1, comp1.getId());

        // Change component name and reuse factory
        factory.setComponentName("testComponent");
        TestComponent comp2 = factory.create();
        assertEquals(2, comp2.getId());

        // Components should be different instances
        assertNotSame(comp1, comp2);
    }

    /**
     * Test listener state consistency
     */
    public void test_listenerStateConsistency() throws Exception {
        final List<String> events = new ArrayList<>();

        OnDestroyListener<TestComponent> listener1 = new OnDestroyListener<TestComponent>() {
            @Override
            public void onDestroy(PooledObject<TestComponent> p) {
                events.add("listener1");
            }
        };

        OnDestroyListener<TestComponent> listener2 = new OnDestroyListener<TestComponent>() {
            @Override
            public void onDestroy(PooledObject<TestComponent> p) {
                events.add("listener2");
            }
        };

        TestComponent component = new TestComponent();
        PooledObject<TestComponent> pooledObject = new DefaultPooledObject<>(component);

        // Test with listener1
        factory.setOnDestroyListener(listener1);
        assertEquals(listener1, factory.getOnDestroyListener());
        factory.destroyObject(pooledObject);
        assertEquals(1, events.size());
        assertEquals("listener1", events.get(0));

        // Switch to listener2
        factory.setOnDestroyListener(listener2);
        assertEquals(listener2, factory.getOnDestroyListener());
        factory.destroyObject(pooledObject);
        assertEquals(2, events.size());
        assertEquals("listener2", events.get(1));

        // Clear listener
        factory.setOnDestroyListener(null);
        assertNull(factory.getOnDestroyListener());
        factory.destroyObject(pooledObject);
        assertEquals(2, events.size()); // No new events
    }
}
