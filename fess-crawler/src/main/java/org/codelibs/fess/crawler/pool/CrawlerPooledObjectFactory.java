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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.fess.crawler.container.CrawlerContainer;

/**
 * A thread-safe factory for creating and managing pooled crawler objects.
 * This class extends {@link BasePooledObjectFactory} and provides
 * methods for creating, wrapping, and destroying crawler components
 * obtained from a {@link CrawlerContainer}.
 *
 * <p>This implementation provides proper resource management for closeable objects
 * and supports destruction listeners.</p>
 *
 * @param <T> the type of the pooled object
 */
public class CrawlerPooledObjectFactory<T> extends BasePooledObjectFactory<T> {
    private static final Logger logger = LogManager.getLogger(CrawlerPooledObjectFactory.class);

    /**
     * The container that provides crawler components.
     */
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
     * Constructs a new CrawlerPooledObjectFactory.
     */
    public CrawlerPooledObjectFactory() {
        // Default constructor for DI
    }

    /**
     * Creates a new object instance from the crawler container.
     *
     * @return A new instance of the component specified by componentName
     * @throws Exception if the component cannot be created
     */
    @Override
    @SuppressWarnings("unchecked")
    public T create() throws Exception {
        if (crawlerContainer == null) {
            throw new IllegalStateException("crawlerContainer is not set. Please configure the container before creating pooled objects.");
        }
        if (componentName == null) {
            throw new IllegalStateException("componentName is not set. Please specify a valid component name for the pooled object factory.");
        }
        if (logger.isDebugEnabled()) {
            logger.debug("Creating new pooled object for component: {}", componentName);
        }
        final Object component = crawlerContainer.getComponent(componentName);
        if (component == null) {
            throw new IllegalStateException("Component '" + componentName + "' not found in crawler container. Please verify the component is registered.");
        }
        return (T) component;
    }

    /**
     * Wraps an object instance into a pooled object.
     *
     * @param obj The object to wrap
     * @return A PooledObject wrapping the given object
     */
    @Override
    public PooledObject<T> wrap(final T obj) {
        return new DefaultPooledObject<>(obj);
    }

    /**
     * Destroys a pooled object and notifies the destroy listener if set.
     * If the object implements {@link AutoCloseable}, it will be closed automatically.
     *
     * @param p The pooled object to destroy
     * @throws Exception if destruction fails
     */
    @Override
    public void destroyObject(final PooledObject<T> p) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("Destroying pooled object for component: {}", componentName);
        }

        // Notify the listener first
        if (onDestroyListener != null) {
            try {
                onDestroyListener.onDestroy(p);
            } catch (final Exception e) {
                logger.warn("Error occurred in onDestroy listener for component: {}", componentName, e);
                // Continue with destruction even if listener fails
            }
        }

        // Close the object if it implements AutoCloseable or Closeable
        final T obj = p.getObject();
        if (obj instanceof AutoCloseable) {
            try {
                ((AutoCloseable) obj).close();
            } catch (final Exception e) {
                logger.warn("Error closing pooled object for component: {}", componentName, e);
                throw e;
            }
        }
    }

    /**
     * Listener interface for handling object destruction events.
     *
     * @param <T> The type of the pooled object
     */
    @FunctionalInterface
    public interface OnDestroyListener<T> {
        /**
         * Called when a pooled object is being destroyed.
         * This method is invoked before the object is closed (if it implements AutoCloseable).
         *
         * @param p The pooled object being destroyed
         */
        void onDestroy(PooledObject<T> p);
    }

    /**
     * Gets the component name.
     *
     * @return The component name
     */
    public String getComponentName() {
        return componentName;
    }

    /**
     * Sets the component name.
     *
     * @param componentName The component name to set
     */
    public void setComponentName(final String componentName) {
        this.componentName = componentName;
    }

    /**
     * Gets the crawler container.
     *
     * @return The crawler container
     */
    public CrawlerContainer getCrawlerContainer() {
        return crawlerContainer;
    }

    /**
     * Sets the crawler container.
     *
     * @param crawlerContainer The crawler container to set
     */
    public void setCrawlerContainer(final CrawlerContainer crawlerContainer) {
        this.crawlerContainer = crawlerContainer;
    }

    /**
     * Gets the onDestroy listener.
     *
     * @return The onDestroy listener, or null if not set
     */
    public OnDestroyListener<T> getOnDestroyListener() {
        return onDestroyListener;
    }

    /**
     * Sets the onDestroy listener.
     *
     * @param onDestroyListener The onDestroy listener to set
     */
    public void setOnDestroyListener(final OnDestroyListener<T> onDestroyListener) {
        this.onDestroyListener = onDestroyListener;
    }
}
