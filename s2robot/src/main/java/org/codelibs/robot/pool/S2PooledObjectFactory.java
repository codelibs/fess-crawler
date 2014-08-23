/*
 * Copyright 2004-2014 the Seasar Foundation and the Others.
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
package org.codelibs.robot.pool;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.seasar.framework.container.SingletonS2Container;

/**
 * 
 * @param <T>
 * 
 * @author shinsuke
 *
 */
public class S2PooledObjectFactory<T> extends BasePooledObjectFactory<T> {

    public String componentName;

    public OnDestroyListener<T> onDestroyListener;

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.commons.pool2.BasePooledObjectFactory#create()
     */
    @Override
    public T create() throws Exception {
        @SuppressWarnings("unchecked")
        T component = (T) SingletonS2Container.getComponent(componentName);
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
        return new DefaultPooledObject<T>(obj);
    }

    @Override
    public void destroyObject(PooledObject<T> p) throws Exception {
        if (onDestroyListener != null) {
            onDestroyListener.onDestroy(p);
        }
    }

    public interface OnDestroyListener<T> {
        public void onDestroy(PooledObject<T> p);
    }
}
