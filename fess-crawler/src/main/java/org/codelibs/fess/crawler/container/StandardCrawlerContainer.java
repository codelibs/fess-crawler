/*
 * Copyright 2012-2024 CodeLibs Project and the Others.
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

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.beans.BeanDesc;
import org.codelibs.core.beans.FieldDesc;
import org.codelibs.core.beans.MethodDesc;
import org.codelibs.core.beans.factory.BeanDescFactory;
import org.codelibs.core.lang.ClassUtil;
import org.codelibs.core.lang.FieldUtil;
import org.codelibs.core.lang.MethodUtil;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.annotation.Resource;

/**
 * A container implementation that manages the lifecycle and dependency injection of components
 * in a crawler application. This container supports both singleton and prototype component
 * instantiation patterns.
 *
 * <p>The container provides mechanisms for:
 * <ul>
 *   <li>Registering and retrieving components by name</li>
 *   <li>Managing singleton instances with lifecycle hooks</li>
 *   <li>Creating prototype instances on demand</li>
 *   <li>Dependency injection using {@code @Resource} annotation</li>
 *   <li>Lifecycle management using {@code @PostConstruct} and {@code @PreDestroy} annotations</li>
 * </ul>
 *
 * <p>Components can be registered in two ways:
 * <ul>
 *   <li>As singletons, where one instance is shared throughout the container's lifecycle</li>
 *   <li>As prototypes, where a new instance is created each time the component is requested</li>
 * </ul>
 *
 * <p>The container supports component initialization and destruction through consumer functions,
 * allowing custom setup and cleanup operations for components.
 *
 */
public class StandardCrawlerContainer implements CrawlerContainer {

    private static final Logger logger = LogManager.getLogger(StandardCrawlerContainer.class);

    private final Map<String, ComponentHolder<?>> singletonMap = new ConcurrentHashMap<>();

    private final Map<String, ComponentDef<?>> prototypeMap = new ConcurrentHashMap<>();

    private boolean available = true;

    public StandardCrawlerContainer() {
        initialize();
    }

    @Override
    public <T> T getComponent(final String name) {
        if ("crawlerContainer".equals(name)) {
            return (T) this;
        }

        final ComponentDef<?> componentDef = prototypeMap.get(name);
        if (componentDef != null) {
            return (T) componentDef.get();
        }
        final ComponentHolder<?> componentHolder = singletonMap.get(name);
        if (componentHolder != null) {
            return (T) componentHolder.get();
        }
        return null;
    }

    @Override
    public boolean available() {
        return available;
    }

    @Override
    public void destroy() {
        available = false;
        for (final ComponentHolder<?> componentHolder : singletonMap.values()) {
            try {
                componentHolder.destroy();
            } catch (final Exception e) {
                logger.warn("Failed to destroy " + componentHolder.get(), e);
            }
        }
    }

    public <T> StandardCrawlerContainer prototype(final String name, final Class<T> cls, final Consumer<T> component) {
        prototypeMap.put(name, new ComponentDef<>(cls, component, this));
        return this;
    }

    public <T> StandardCrawlerContainer prototype(final String name, final Class<T> cls) {
        return prototype(name, cls, null);
    }

    public <T> StandardCrawlerContainer singleton(final String name, final Class<T> cls, final Consumer<T> initializer,
            final Consumer<T> destroyer) {
        final ComponentDef<T> componentDef = new ComponentDef<>(cls, initializer, this);
        final T instance = componentDef.get();
        singletonMap.put(name, new ComponentHolder<>(instance, destroyer));
        return this;
    }

    public <T> StandardCrawlerContainer singleton(final String name, final Class<T> cls, final Consumer<T> initializer) {
        return singleton(name, cls, initializer, (Consumer<T>) null);
    }

    public <T> StandardCrawlerContainer singleton(final String name, final Class<T> cls) {
        return singleton(name, cls, (Consumer<T>) null, (Consumer<T>) null);
    }

    public <T> StandardCrawlerContainer singleton(final String name, final T instance, final Consumer<T> initializer,
            final Consumer<T> destroyer) {
        final ComponentDef<T> componentDef = new ComponentDef<>(instance, initializer, this);
        singletonMap.put(name, new ComponentHolder<>(componentDef.get(), destroyer));
        return this;
    }

    public <T> StandardCrawlerContainer singleton(final String name, final T instance, final Consumer<T> initializer) {
        return singleton(name, instance, initializer, null);
    }

    public <T> StandardCrawlerContainer singleton(final String name, final T instance) {
        return singleton(name, instance, null, null);
    }

    /**
     * A holder for a component instance and its associated destroyer function.
     * This class manages the lifecycle of a component, including its destruction.
     *
     * @param <T> the type of the component
     */
    protected static class ComponentHolder<T> {
        protected T instance;

        protected Consumer<T> destroyer;

        protected ComponentHolder(final T instance, final Consumer<T> destroyer) {
            this.instance = instance;
            this.destroyer = destroyer;
        }

        protected T get() {
            return instance;
        }

        protected void destroy() {
            final BeanDesc beanDesc = BeanDescFactory.getBeanDesc(instance.getClass());
            for (final String methodName : beanDesc.getMethodNames()) {
                final MethodDesc methodDesc = beanDesc.getMethodDescNoException(methodName);
                if (methodDesc != null) {
                    final Method method = methodDesc.getMethod();
                    final PreDestroy postConstruct = method.getAnnotation(PreDestroy.class);
                    if (postConstruct != null) {
                        MethodUtil.invoke(method, instance);
                    }
                }
            }

            if (destroyer != null) {
                destroyer.accept(instance);
            }
        }
    }

    protected static class ComponentDef<T> {
        protected Class<T> cls;

        protected Consumer<T> initializer;

        protected StandardCrawlerContainer container;

        private T instance;

        protected ComponentDef(final Class<T> cls, final Consumer<T> initializer, final StandardCrawlerContainer container) {
            this.cls = cls;
            this.initializer = initializer;
            this.container = container;
        }

        protected ComponentDef(final T instance, final Consumer<T> initializer, final StandardCrawlerContainer container) {
            this.instance = instance;
            this.initializer = initializer;
            this.container = container;
        }

        protected T get() {
            final T component = instance == null ? ClassUtil.newInstance(cls) : instance;
            final BeanDesc beanDesc = BeanDescFactory.getBeanDesc(component.getClass());
            for (final FieldDesc fieldDesc : beanDesc.getFieldDescs()) {
                final Resource annotation = fieldDesc.getField().getAnnotation(Resource.class);
                if (annotation != null) {
                    final Object injected = container.getComponent(fieldDesc.getFieldName());
                    if (injected != null) {
                        FieldUtil.set(fieldDesc.getField(), component, injected);
                    }
                }
            }

            for (final String methodName : beanDesc.getMethodNames()) {
                final MethodDesc methodDesc = beanDesc.getMethodDescNoException(methodName);
                if (methodDesc != null) {
                    final Method method = methodDesc.getMethod();
                    final PostConstruct postConstruct = method.getAnnotation(PostConstruct.class);
                    if (postConstruct != null) {
                        MethodUtil.invoke(method, component);
                    }
                }
            }
            if (initializer != null) {
                initializer.accept(component);
            }
            return component;
        }
    }
}
