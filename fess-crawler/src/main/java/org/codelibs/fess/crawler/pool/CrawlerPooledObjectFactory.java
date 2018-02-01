/*
 * Copyright 2012-2018 CodeLibs Project and the Others.
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

import javax.annotation.Resource;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.codelibs.fess.crawler.container.CrawlerContainer;

/**
 *
 * @param <T> Pooled object
 *
 * @author shinsuke
 */
public class CrawlerPooledObjectFactory<T> extends BasePooledObjectFactory<T> {
    @Resource
    protected CrawlerContainer crawlerContainer;

    protected String componentName;

    protected OnDestroyListener<T> onDestroyListener;

    /*
     * (non-Javadoc)
     *
     * @see org.apache.commons.pool2.BasePooledObjectFactory#create()
     */
    @Override
    public T create() throws Exception {
        @SuppressWarnings("unchecked")
        final T component = (T) crawlerContainer.getComponent(componentName);
        return component;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.apache.commons.pool2.BasePooledObjectFactory#wrap(java.lang.Object)
     */
    @Override
    public PooledObject<T> wrap(final T obj) {
        return new DefaultPooledObject<>(obj);
    }

    @Override
    public void destroyObject(final PooledObject<T> p) throws Exception {
        if (onDestroyListener != null) {
            onDestroyListener.onDestroy(p);
        }
    }

    public interface OnDestroyListener<T> {
        public void onDestroy(PooledObject<T> p);
    }

    public String getComponentName() {
        return componentName;
    }

    public void setComponentName(final String componentName) {
        this.componentName = componentName;
    }

    public OnDestroyListener<T> getOnDestroyListener() {
        return onDestroyListener;
    }

    public void setOnDestroyListener(
            final OnDestroyListener<T> onDestroyListener) {
        this.onDestroyListener = onDestroyListener;
    }
}
