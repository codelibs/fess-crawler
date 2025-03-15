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

import org.codelibs.core.lang.StringUtil;

/**
 * The CrawlerContainer interface provides methods for managing components
 * within a crawler container. It includes methods to retrieve components,
 * check availability, and destroy the container. Additionally, it provides
 * a default method to initialize the container with specific protocol handlers.
 *
 */
public interface CrawlerContainer {

    /**
     * Retrieves a component by its name.
     *
     * @param <T> the type of the component
     * @param name the name of the component to retrieve
     * @return the component instance of the specified type
     */
    <T> T getComponent(String name);

    /**
     * Checks if the crawler container is available.
     *
     * @return true if the crawler container is available, false otherwise.
     */
    boolean available();

    /**
     * Cleans up resources and performs any necessary finalization tasks
     * before the object is destroyed. This method should be called to
     * ensure that all resources are properly released.
     */
    void destroy();

    /**
     * Initializes the CrawlerContainer by setting the system property
     * "java.protocol.handler.pkgs" to include the package "org.codelibs.fess.net.protocol".
     * If the property is not already set, it will be initialized with this package.
     * If the property is set but does not contain this package, the package will be appended.
     */
    default void initialize() {
        final StringBuilder buf = new StringBuilder(100);
        final String value = System.getProperty("java.protocol.handler.pkgs");
        if (StringUtil.isEmpty(value)) {
            buf.append("org.codelibs.fess.net.protocol");
        } else if (!value.contains("org.codelibs.fess.net.protocol")) {
            buf.append("|org.codelibs.fess.net.protocol");
        }
        if (buf.length() > 0) {
            System.setProperty("java.protocol.handler.pkgs", buf.toString());
        }
    }
}
