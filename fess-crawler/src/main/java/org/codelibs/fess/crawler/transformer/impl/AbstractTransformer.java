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
package org.codelibs.fess.crawler.transformer.impl;

import org.codelibs.fess.crawler.transformer.Transformer;

/**
 * An abstract base class for transformers.
 * Provides basic implementation for managing the transformer's name.
 *
 */
public abstract class AbstractTransformer implements Transformer {

    /**
     * Creates a new instance of AbstractTransformer.
     */
    public AbstractTransformer() {
        // NOP
    }

    /**
     * The name of the transformer.
     */
    protected String name;

    /**
     * Sets the name of the transformer.
     *
     * @param name the name to set
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * Returns the name of the transformer.
     *
     * @return the name of the transformer
     */
    @Override
    public String getName() {
        return name;
    }

}
