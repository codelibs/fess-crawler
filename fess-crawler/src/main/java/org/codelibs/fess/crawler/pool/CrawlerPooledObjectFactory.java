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

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.codelibs.fess.crawler.container.CrawlerContainer;

import jakarta.annotation.Resource;

/**
 * A factory for creating and managing pooled crawler objects.
 * This class extends {@link BasePooledObjectFactory} and provides
 * methods for creating, wrapping, and destroying crawler components
 * obtained from a {@link CrawlerContainer}.
 *
 * @param <T> the type of the pooled object
 */
public class CrawlerPooledObjectFactory<T> extends BasePooledObjectFactory<T> {
    /**
     * Constructs a new CrawlerPooledObjectFactory.
     */
    public CrawlerPooledObjectFactory() {
        // Default constructor
    }

    /**
     * The container that provides crawler components.
     */
    @Resource
    protected CrawlerContainer crawlerContainer;

    /**
     * The name of the component to be retrieved from the CrawlerContainer.
     */
    protected String componentName;

    /**
     * The listener that is called when a pooled object is destroyed.
     */
    protected OnDestroyListener<T> onDestroyListener;

    /**
     * Creates a new object instance from the crawler container.
     * @return A new instance of the component specified by componentName
     * @throws Exception if the component cannot be created
     */
    @Override
    public T create() throws Exception {
        return (T) crawlerContainer.getComponent(componentName);
    }

    /**
     * Wraps an object instance into a pooled object.
     * @param obj The object to wrap
     * @return A PooledObject wrapping the given object
     */
    @Override
    public PooledObject<T> wrap(final T obj) {
        return new DefaultPooledObject<>(obj);
    }

    /**
     * Destroys a pooled object and notifies the destroy listener if set.
     * @param p The pooled object to destroy
     * @throws Exception if destruction fails
     */
    @Override
    public void destroyObject(final PooledObject<T> p) throws Exception {
        if (onDestroyListener != null) {
            onDestroyListener.onDestroy(p);
        }
    }

    /**
     * Listener interface for handling object destruction events.
     * @param <T> The type of the pooled object
     */
    public interface OnDestroyListener<T> {
        /**
         * Called when a pooled object is being destroyed.
         * @param p The pooled object being destroyed
         */
        void onDestroy(PooledObject<T> p);
    }

    /**
     * Gets the component name.
     * @return The component name
     */
    public String getComponentName() {
        return componentName;
    }

    /**
     * Sets the component name.
     * @param componentName The component name to set
     */
    public void setComponentName(final String componentName) {
        this.componentName = componentName;
    }

    /**
     * Returns the onDestroy listener.
     * @return The onDestroy listener.
     */
    public OnDestroyListener<T> getOnDestroyListener() {
        return onDestroyListener;
    }

    /**
     * Sets the onDestroy listener.
     * @param onDestroyListener The OnDestroyListener to set.
     */
    public void setOnDestroyListener(final OnDestroyListener<T> onDestroyListener) {
        this.onDestroyListener = onDestroyListener;
    }
}
