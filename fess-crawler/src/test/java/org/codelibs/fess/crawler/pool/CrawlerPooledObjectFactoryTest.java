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
        CloseableTestComponent.resetCounters();

        // Initialize container with test components
        container = new StandardCrawlerContainer().prototype("testComponent", TestComponent.class)
                .singleton("singletonComponent", SingletonTestComponent.class)
                .prototype("closeableComponent", CloseableTestComponent.class);

        // Initialize factory with constructor
        factory = new CrawlerPooledObjectFactory<>(container, "testComponent");
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
     * Closeable test component for testing AutoCloseable resource handling
     */
    public static class CloseableTestComponent implements AutoCloseable {
        private static AtomicInteger instanceCounter = new AtomicInteger(0);
        private static AtomicInteger closeCounter = new AtomicInteger(0);
        private final int id;
        private boolean closed = false;

        public CloseableTestComponent() {
            this.id = instanceCounter.incrementAndGet();
        }

        public int getId() {
            return id;
        }

        public boolean isClosed() {
            return closed;
        }

        @Override
        public void close() throws Exception {
            if (!closed) {
                closed = true;
                closeCounter.incrementAndGet();
            }
        }

        public static int getCloseCount() {
            return closeCounter.get();
        }

        public static void resetCounters() {
            instanceCounter.set(0);
            closeCounter.set(0);
        }
    }

    /**
     * Test basic object creation
     */
    public void test_create_basic() throws Exception {
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
     * Test constructor with null container
     */
    public void test_constructor_nullContainer() {
        try {
            new CrawlerPooledObjectFactory<>(null, "testComponent");
            fail("Should throw IllegalArgumentException for null container");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("crawlerContainer"));
        }
    }

    /**
     * Test constructor with null component name
     */
    public void test_constructor_nullComponentName() {
        try {
            new CrawlerPooledObjectFactory<>(container, null);
            fail("Should throw IllegalArgumentException for null component name");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("componentName"));
        }
    }

    /**
     * Test constructor with empty component name
     */
    public void test_constructor_emptyComponentName() {
        try {
            new CrawlerPooledObjectFactory<>(container, "");
            fail("Should throw IllegalArgumentException for empty component name");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("componentName"));
        }
    }

    /**
     * Test creation with invalid component name throws exception
     */
    public void test_create_invalidComponentName() {
        CrawlerPooledObjectFactory<Object> invalidFactory = new CrawlerPooledObjectFactory<>(container, "nonExistentComponent");

        try {
            invalidFactory.create();
            fail("Should throw IllegalStateException for invalid component name");
        } catch (Exception e) {
            assertTrue(e instanceof IllegalStateException);
            assertTrue(e.getMessage().contains("nonExistentComponent"));
        }
    }

    /**
     * Test creation with singleton component
     */
    public void test_create_singletonComponent() throws Exception {
        CrawlerPooledObjectFactory<SingletonTestComponent> singletonFactory = new CrawlerPooledObjectFactory<>(container,
                "singletonComponent");

        SingletonTestComponent component1 = singletonFactory.create();
        SingletonTestComponent component2 = singletonFactory.create();

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
        AtomicBoolean listenerCalled = new AtomicBoolean(false);
        final PooledObject<TestComponent>[] capturedObject = new PooledObject[1];

        OnDestroyListener<TestComponent> listener = new OnDestroyListener<TestComponent>() {
            @Override
            public void onDestroy(PooledObject<TestComponent> p) {
                listenerCalled.set(true);
                capturedObject[0] = p;
                p.getObject().destroy();
            }
        };

        CrawlerPooledObjectFactory<TestComponent> factoryWithListener = new CrawlerPooledObjectFactory<>(container, "testComponent",
                listener);

        TestComponent component = new TestComponent();
        PooledObject<TestComponent> pooledObject = new DefaultPooledObject<>(component);

        factoryWithListener.destroyObject(pooledObject);

        assertTrue(listenerCalled.get());
        assertSame(pooledObject, capturedObject[0]);
        assertTrue(component.isDestroyed());
    }

    /**
     * Test destroyObject with listener exception - should not prevent resource cleanup
     */
    public void test_destroyObject_listenerException() throws Exception {
        CloseableTestComponent.resetCounters();

        OnDestroyListener<CloseableTestComponent> listener = new OnDestroyListener<CloseableTestComponent>() {
            @Override
            public void onDestroy(PooledObject<CloseableTestComponent> p) {
                throw new RuntimeException("Test exception in listener");
            }
        };

        CrawlerPooledObjectFactory<CloseableTestComponent> factoryWithListener = new CrawlerPooledObjectFactory<>(container,
                "closeableComponent", listener);

        CloseableTestComponent component = new CloseableTestComponent();
        PooledObject<CloseableTestComponent> pooledObject = new DefaultPooledObject<>(component);

        // destroyObject should continue and close the resource even if listener throws
        factoryWithListener.destroyObject(pooledObject);

        // Resource should still be closed despite listener exception
        assertTrue("Component should be closed even if listener throws", component.isClosed());
    }

    /**
     * Test destroyObject with AutoCloseable component
     */
    public void test_destroyObject_autoCloseable() throws Exception {
        CloseableTestComponent.resetCounters();

        CrawlerPooledObjectFactory<CloseableTestComponent> closeableFactory = new CrawlerPooledObjectFactory<>(container,
                "closeableComponent");

        CloseableTestComponent component = new CloseableTestComponent();
        PooledObject<CloseableTestComponent> pooledObject = new DefaultPooledObject<>(component);

        assertFalse(component.isClosed());
        assertEquals(0, CloseableTestComponent.getCloseCount());

        closeableFactory.destroyObject(pooledObject);

        assertTrue(component.isClosed());
        assertEquals(1, CloseableTestComponent.getCloseCount());
    }

    /**
     * Test destroyObject with AutoCloseable and listener
     */
    public void test_destroyObject_autoCloseableWithListener() throws Exception {
        CloseableTestComponent.resetCounters();
        AtomicBoolean listenerCalled = new AtomicBoolean(false);

        OnDestroyListener<CloseableTestComponent> listener = new OnDestroyListener<CloseableTestComponent>() {
            @Override
            public void onDestroy(PooledObject<CloseableTestComponent> p) {
                listenerCalled.set(true);
                assertFalse("Component should not be closed yet when listener is called", p.getObject().isClosed());
            }
        };

        CrawlerPooledObjectFactory<CloseableTestComponent> factoryWithListener = new CrawlerPooledObjectFactory<>(container,
                "closeableComponent", listener);

        CloseableTestComponent component = new CloseableTestComponent();
        PooledObject<CloseableTestComponent> pooledObject = new DefaultPooledObject<>(component);

        factoryWithListener.destroyObject(pooledObject);

        assertTrue("Listener should be called", listenerCalled.get());
        assertTrue("Component should be closed after listener", component.isClosed());
        assertEquals(1, CloseableTestComponent.getCloseCount());
    }

    /**
     * Test getters
     */
    public void test_getters() {
        assertEquals("testComponent", factory.getComponentName());
        assertSame(container, factory.getCrawlerContainer());
        assertNull(factory.getOnDestroyListener());

        OnDestroyListener<TestComponent> listener = new OnDestroyListener<TestComponent>() {
            @Override
            public void onDestroy(PooledObject<TestComponent> p) {
                // Empty implementation
            }
        };

        CrawlerPooledObjectFactory<TestComponent> factoryWithListener = new CrawlerPooledObjectFactory<>(container, "testComponent",
                listener);
        assertEquals("testComponent", factoryWithListener.getComponentName());
        assertSame(container, factoryWithListener.getCrawlerContainer());
        assertSame(listener, factoryWithListener.getOnDestroyListener());
    }

    /**
     * Test lifecycle: create, wrap, destroy
     */
    public void test_fullLifecycle() throws Exception {
        TestComponent.resetCounter();

        final List<TestComponent> destroyedComponents = new ArrayList<>();
        OnDestroyListener<TestComponent> listener = new OnDestroyListener<TestComponent>() {
            @Override
            public void onDestroy(PooledObject<TestComponent> p) {
                TestComponent component = p.getObject();
                component.destroy();
                destroyedComponents.add(component);
            }
        };

        CrawlerPooledObjectFactory<TestComponent> factoryWithListener = new CrawlerPooledObjectFactory<>(container, "testComponent",
                listener);

        // Create
        TestComponent component = factoryWithListener.create();
        assertNotNull(component);
        assertEquals(1, component.getId());
        assertFalse(component.isDestroyed());

        // Wrap
        PooledObject<TestComponent> pooledObject = factoryWithListener.wrap(component);
        assertNotNull(pooledObject);
        assertSame(component, pooledObject.getObject());

        // Destroy
        factoryWithListener.destroyObject(pooledObject);
        assertTrue(component.isDestroyed());
        assertEquals(1, destroyedComponents.size());
        assertSame(component, destroyedComponents.get(0));
    }

    /**
     * Test concurrent object creation
     */
    public void test_concurrentCreation() throws Exception {
        TestComponent.resetCounter();

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

        OnDestroyListener<TestComponent> listener = new OnDestroyListener<TestComponent>() {
            @Override
            public void onDestroy(PooledObject<TestComponent> p) {
                destroyCount.incrementAndGet();
                if (p != null && p.getObject() != null) {
                    p.getObject().destroy();
                }
            }
        };

        final CrawlerPooledObjectFactory<TestComponent> factoryWithListener = new CrawlerPooledObjectFactory<>(container,
                "testComponent", listener);

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
                            factoryWithListener.destroyObject(pooledObject);
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
        CrawlerPooledObjectFactory<String> stringFactory = new CrawlerPooledObjectFactory<>(container, "stringComponent");

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
     * Test immutability of factory configuration
     */
    public void test_immutability() {
        // Factory configuration is immutable after construction
        assertEquals("testComponent", factory.getComponentName());
        assertSame(container, factory.getCrawlerContainer());

        // Create another factory with different settings
        OnDestroyListener<TestComponent> listener = new OnDestroyListener<TestComponent>() {
            @Override
            public void onDestroy(PooledObject<TestComponent> p) {
                // Empty implementation
            }
        };

        CrawlerPooledObjectFactory<TestComponent> factory2 = new CrawlerPooledObjectFactory<>(container, "singletonComponent",
                listener);

        // Each factory retains its own configuration
        assertEquals("testComponent", factory.getComponentName());
        assertNull(factory.getOnDestroyListener());

        assertEquals("singletonComponent", factory2.getComponentName());
        assertSame(listener, factory2.getOnDestroyListener());
    }
}
