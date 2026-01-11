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
package org.codelibs.fess.crawler.container;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.dbflute.utflute.core.PlainTestCase;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.annotation.Resource;

/**
 * Test class for StandardCrawlerContainer.
 * Tests singleton/prototype management, dependency injection, and lifecycle hooks.
 */
public class StandardCrawlerContainerTest extends PlainTestCase {

    /**
     * Test that new container is available
     */
    public void test_available_newContainer() {
        StandardCrawlerContainer container = new StandardCrawlerContainer();
        assertTrue(container.available());
    }

    /**
     * Test that destroyed container is not available
     */
    public void test_available_afterDestroy() {
        StandardCrawlerContainer container = new StandardCrawlerContainer();
        assertTrue(container.available());

        container.destroy();
        assertFalse(container.available());
    }

    /**
     * Test getComponent returns null for unknown component
     */
    public void test_getComponent_unknown() {
        StandardCrawlerContainer container = new StandardCrawlerContainer();
        Object result = container.getComponent("unknownComponent");
        assertNull(result);
    }

    /**
     * Test getComponent("crawlerContainer") returns self
     */
    public void test_getComponent_crawlerContainer() {
        StandardCrawlerContainer container = new StandardCrawlerContainer();
        Object result = container.getComponent("crawlerContainer");
        assertTrue(container == result);
    }

    /**
     * Test singleton registration and retrieval
     */
    public void test_singleton_basic() {
        StandardCrawlerContainer container = new StandardCrawlerContainer();
        container.singleton("testService", SimpleService.class);

        SimpleService service1 = container.getComponent("testService");
        SimpleService service2 = container.getComponent("testService");

        assertNotNull(service1);
        assertTrue(service1 == service2); // Same instance
    }

    /**
     * Test singleton with existing instance
     */
    public void test_singleton_withInstance() {
        StandardCrawlerContainer container = new StandardCrawlerContainer();
        SimpleService instance = new SimpleService();
        instance.setValue("preset");

        container.singleton("testService", instance);

        SimpleService retrieved = container.getComponent("testService");
        assertTrue(instance == retrieved);
        assertEquals("preset", retrieved.getValue());
    }

    /**
     * Test singleton with initializer
     */
    public void test_singleton_withInitializer() {
        StandardCrawlerContainer container = new StandardCrawlerContainer();
        container.<SimpleService>singleton("testService", SimpleService.class, service -> {
            service.setValue("initialized");
        });

        SimpleService service = container.getComponent("testService");
        assertEquals("initialized", service.getValue());
    }

    /**
     * Test singleton with initializer and destroyer
     */
    public void test_singleton_withInitializerAndDestroyer() {
        AtomicBoolean destroyed = new AtomicBoolean(false);
        StandardCrawlerContainer container = new StandardCrawlerContainer();
        container.<SimpleService>singleton("testService", SimpleService.class,
                service -> service.setValue("init"),
                service -> destroyed.set(true));

        SimpleService service = container.getComponent("testService");
        assertEquals("init", service.getValue());
        assertFalse(destroyed.get());

        container.destroy();
        assertTrue(destroyed.get());
    }

    /**
     * Test prototype registration and retrieval
     */
    public void test_prototype_basic() {
        StandardCrawlerContainer container = new StandardCrawlerContainer();
        container.prototype("testService", SimpleService.class);

        SimpleService service1 = container.getComponent("testService");
        SimpleService service2 = container.getComponent("testService");

        assertNotNull(service1);
        assertNotNull(service2);
        assertFalse(service1 == service2); // Different instances
    }

    /**
     * Test prototype with initializer
     */
    public void test_prototype_withInitializer() {
        AtomicInteger counter = new AtomicInteger(0);
        StandardCrawlerContainer container = new StandardCrawlerContainer();
        container.<SimpleService>prototype("testService", SimpleService.class, service -> {
            service.setValue("instance-" + counter.incrementAndGet());
        });

        SimpleService service1 = container.getComponent("testService");
        SimpleService service2 = container.getComponent("testService");

        assertEquals("instance-1", service1.getValue());
        assertEquals("instance-2", service2.getValue());
    }

    /**
     * Test method chaining
     */
    public void test_methodChaining() {
        StandardCrawlerContainer container = new StandardCrawlerContainer()
                .singleton("service1", SimpleService.class)
                .singleton("service2", SimpleService.class)
                .prototype("service3", SimpleService.class);

        assertNotNull(container.getComponent("service1"));
        assertNotNull(container.getComponent("service2"));
        assertNotNull(container.getComponent("service3"));
    }

    /**
     * Test @Resource dependency injection
     */
    public void test_resourceInjection() {
        StandardCrawlerContainer container = new StandardCrawlerContainer();
        container.<SimpleService>singleton("dependency", SimpleService.class, s -> s.setValue("injected"));
        container.singleton("consumer", ServiceWithDependency.class);

        ServiceWithDependency consumer = container.getComponent("consumer");
        assertNotNull(consumer);
        assertNotNull(consumer.getDependency());
        assertEquals("injected", consumer.getDependency().getValue());
    }

    /**
     * Test @Resource injection with missing dependency returns null field
     */
    public void test_resourceInjection_missingDependency() {
        StandardCrawlerContainer container = new StandardCrawlerContainer();
        container.singleton("consumer", ServiceWithDependency.class);

        ServiceWithDependency consumer = container.getComponent("consumer");
        assertNotNull(consumer);
        assertNull(consumer.getDependency()); // Not injected
    }

    /**
     * Test @PostConstruct is called
     */
    public void test_postConstruct() {
        StandardCrawlerContainer container = new StandardCrawlerContainer();
        container.singleton("service", ServiceWithLifecycle.class);

        ServiceWithLifecycle service = container.getComponent("service");
        assertTrue(service.isInitialized());
        assertFalse(service.isDestroyed());
    }

    /**
     * Test @PreDestroy is called on destroy
     */
    public void test_preDestroy() {
        StandardCrawlerContainer container = new StandardCrawlerContainer();
        container.singleton("service", ServiceWithLifecycle.class);

        ServiceWithLifecycle service = container.getComponent("service");
        assertFalse(service.isDestroyed());

        container.destroy();
        assertTrue(service.isDestroyed());
    }

    /**
     * Test prototype with @PostConstruct
     */
    public void test_prototype_postConstruct() {
        StandardCrawlerContainer container = new StandardCrawlerContainer();
        container.prototype("service", ServiceWithLifecycle.class);

        ServiceWithLifecycle service1 = container.getComponent("service");
        ServiceWithLifecycle service2 = container.getComponent("service");

        assertTrue(service1.isInitialized());
        assertTrue(service2.isInitialized());
        assertFalse(service1 == service2);
    }

    /**
     * Test multiple singletons are all destroyed
     */
    public void test_destroy_multipleSingletons() {
        StandardCrawlerContainer container = new StandardCrawlerContainer();
        container.singleton("service1", ServiceWithLifecycle.class);
        container.singleton("service2", ServiceWithLifecycle.class);

        ServiceWithLifecycle service1 = container.getComponent("service1");
        ServiceWithLifecycle service2 = container.getComponent("service2");

        assertFalse(service1.isDestroyed());
        assertFalse(service2.isDestroyed());

        container.destroy();

        assertTrue(service1.isDestroyed());
        assertTrue(service2.isDestroyed());
    }

    /**
     * Test implements CrawlerContainer interface
     */
    public void test_implementsInterface() {
        StandardCrawlerContainer container = new StandardCrawlerContainer();
        assertTrue(container instanceof CrawlerContainer);
    }

    /**
     * Test protocol handler initialization
     */
    public void test_initialize_setsProtocolHandler() {
        new StandardCrawlerContainer(); // Constructor calls initialize()

        String protocolHandlers = System.getProperty("java.protocol.handler.pkgs");
        assertNotNull(protocolHandlers);
        assertTrue(protocolHandlers.contains("org.codelibs.fess.net.protocol"));
    }

    /**
     * Test singleton with instance and initializer
     */
    public void test_singleton_instanceWithInitializer() {
        StandardCrawlerContainer container = new StandardCrawlerContainer();
        SimpleService instance = new SimpleService();
        container.singleton("service", instance, s -> s.setValue("modified"));

        SimpleService retrieved = container.getComponent("service");
        assertTrue(instance == retrieved);
        assertEquals("modified", retrieved.getValue());
    }

    /**
     * Test destroying container doesn't throw on empty container
     */
    public void test_destroy_emptyContainer() {
        StandardCrawlerContainer container = new StandardCrawlerContainer();
        container.destroy(); // Should not throw
        assertFalse(container.available());
    }

    /**
     * Test prototype vs singleton independence
     */
    public void test_prototypeVsSingleton() {
        StandardCrawlerContainer container = new StandardCrawlerContainer();
        container.<SimpleService>singleton("singleton", SimpleService.class, s -> s.setValue("singleton"));
        container.<SimpleService>prototype("prototype", SimpleService.class, s -> s.setValue("prototype"));

        SimpleService s1 = container.getComponent("singleton");
        SimpleService s2 = container.getComponent("singleton");
        SimpleService p1 = container.getComponent("prototype");
        SimpleService p2 = container.getComponent("prototype");

        assertTrue(s1 == s2);
        assertFalse(p1 == p2);
        assertEquals("singleton", s1.getValue());
        assertEquals("prototype", p1.getValue());
        assertEquals("prototype", p2.getValue());
    }

    // ---- Test helper classes ----

    /**
     * Simple service class for testing
     */
    public static class SimpleService {
        private String value;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    /**
     * Service with @Resource dependency
     */
    public static class ServiceWithDependency {
        @Resource
        private SimpleService dependency;

        public SimpleService getDependency() {
            return dependency;
        }
    }

    /**
     * Service with lifecycle annotations
     */
    public static class ServiceWithLifecycle {
        private boolean initialized = false;
        private boolean destroyed = false;

        @PostConstruct
        public void init() {
            initialized = true;
        }

        @PreDestroy
        public void cleanup() {
            destroyed = true;
        }

        public boolean isInitialized() {
            return initialized;
        }

        public boolean isDestroyed() {
            return destroyed;
        }
    }
}
