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

import org.codelibs.core.lang.StringUtil;

/**
 * @author shinsuke
 *
 */
public interface CrawlerContainer {

    <T> T getComponent(String name);

    boolean available();

    void destroy();

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
