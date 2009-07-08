/*
 * Copyright 2004-2009 the Seasar Foundation and the Others.
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

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RobotsTxt {
    protected static final String ALL_BOTS = "*";

    protected final Map<String, Directives> agentsToDirectives = new HashMap<String, Directives>();

    public RobotsTxt() {
        Directives defaultDirectives = new Directives();
        defaultDirectives.crawlDelay = 0;
        agentsToDirectives.put(ALL_BOTS, defaultDirectives);
    }

    public boolean allows(String path, String userAgent)
            throws MalformedURLException {
        Directives directives = getDirectives(userAgent.toLowerCase());
        if (directives == null)
            return true;
        return directives.allows(path);
    }

    public int getCrawlDelay(String userAgent) {
        Directives directives = getDirectives(userAgent.toLowerCase());
        if (directives == null)
            return 0;
        return directives.getCrawlDelay();
    }

    public Directives getDirectives(String userAgent) {
        Directives directives = agentsToDirectives.get(userAgent);
        if (directives == null)
            directives = agentsToDirectives.get(ALL_BOTS);
        return directives;
    }

    public void addDirectives(String userAgent, Directives directives) {
        agentsToDirectives.put(userAgent, directives);
    }

    public static class Directives {
        private int crawlDelay;

        private final List<String> allowedPaths = new ArrayList<String>();

        private final List<String> disallowedPaths = new ArrayList<String>();

        public void setCrawlDelay(int crawlDelay) {
            this.crawlDelay = crawlDelay;
        }

        public int getCrawlDelay() {
            return crawlDelay;
        }

        public boolean allows(String path) {
            for (String allowedPath : allowedPaths) {
                if (path.startsWith(allowedPath))
                    return true;
            }
            for (String disallowedPath : disallowedPaths) {
                if (path.startsWith(disallowedPath))
                    return false;
            }
            return true;
        }

        public void addAllow(String path) {
            this.allowedPaths.add(path);
        }

        public void addDisallow(String path) {
            this.disallowedPaths.add(path);
        }
    }
}
