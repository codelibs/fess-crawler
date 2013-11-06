/*
 * Copyright 2004-2013 the Seasar Foundation and the Others.
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
package org.seasar.robot.entity;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class RobotsTxt {
    protected static final String ALL_BOTS = "*";

    protected final Map<String, Directives> agentsToDirectives =
        new LinkedHashMap<String, Directives>();

    public RobotsTxt() {
        final Directives defaultDirectives = new Directives();
        defaultDirectives.crawlDelay = 0;
        agentsToDirectives.put(ALL_BOTS, defaultDirectives);
    }

    public boolean allows(final String path, final String userAgent) {
        final Directives directives =
            getDirectives(userAgent.toLowerCase(Locale.ENGLISH));
        if (directives == null) {
            return true;
        }
        return directives.allows(path);
    }

    public int getCrawlDelay(final String userAgent) {
        final Directives directives =
            getDirectives(userAgent.toLowerCase(Locale.ENGLISH));
        if (directives == null) {
            return 0;
        }
        return directives.getCrawlDelay();
    }

    public Directives getDirectives(final String userAgent) {
        return getDirectives(userAgent, ALL_BOTS);
    }

    public Directives getDirectives(final String userAgent,
            final String defaultUserAgent) {
        Directives directives = agentsToDirectives.get(userAgent);
        if (directives == null && defaultUserAgent != null) {
            directives = agentsToDirectives.get(defaultUserAgent);
        }
        return directives;
    }

    public void addDirectives(final String userAgent,
            final Directives directives) {
        agentsToDirectives.put(userAgent, directives);
    }

    public String[] getUserAgents() {
        final Set<String> userAgentSet = agentsToDirectives.keySet();
        return userAgentSet.toArray(new String[userAgentSet.size()]);
    }

    public static class Directives {
        private int crawlDelay;

        private final List<String> allowedPaths = new ArrayList<String>();

        private final List<String> disallowedPaths = new ArrayList<String>();

        public void setCrawlDelay(final int crawlDelay) {
            this.crawlDelay = crawlDelay;
        }

        public int getCrawlDelay() {
            return crawlDelay;
        }

        public boolean allows(final String path) {
            for (final String allowedPath : allowedPaths) {
                if (path.startsWith(allowedPath)) {
                    return true;
                }
            }
            for (final String disallowedPath : disallowedPaths) {
                if (path.startsWith(disallowedPath)) {
                    return false;
                }
            }
            return true;
        }

        public void addAllow(final String path) {
            allowedPaths.add(path);
        }

        public void addDisallow(final String path) {
            disallowedPaths.add(path);
        }

        public String[] getAllows() {
            return allowedPaths.toArray(new String[allowedPaths.size()]);
        }

        public String[] getDisallows() {
            return disallowedPaths.toArray(new String[disallowedPaths.size()]);
        }
    }
}
