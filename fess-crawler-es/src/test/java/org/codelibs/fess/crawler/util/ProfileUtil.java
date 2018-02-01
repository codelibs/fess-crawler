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
package org.codelibs.fess.crawler.util;

import java.util.ArrayList;
import java.util.List;

import org.codelibs.core.io.ResourceUtil;

public class ProfileUtil {
    private static final String SPRING_PROFILES_ACTIVE = "spring.profiles.active";

    public static void setup() {
        final List<String> list = new ArrayList<String>();

        final String values = System.getProperty(SPRING_PROFILES_ACTIVE);
        if (values != null) {
            for (final String value : values.split(",")) {
                list.add(value);
            }
        }

        if (ResourceUtil.getResourceNoException("org/h2/Driver.class") != null) {
            list.add("h2");
        }

        if (ResourceUtil.getResourceNoException("com/mysql/jdbc/Driver.class") != null) {
            list.add("mysql");
        }

        if (ResourceUtil.getResourceNoException("oracle/jdbc/driver/OracleDriver.class") != null) {
            list.add("oracle");
        }

        if (!list.isEmpty()) {
            final StringBuilder buf = new StringBuilder();
            for (final String value : list) {
                if (buf.length() > 0) {
                    buf.append(',');
                }
                buf.append(value);
            }
            System.setProperty(SPRING_PROFILES_ACTIVE, buf.toString());
        }
    }
}
