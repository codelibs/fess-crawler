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
package org.codelibs.fess.crawler.entity;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.codelibs.core.lang.StringUtil;

public class RobotsTxt {
    private static final String ALL_BOTS = "*";

    protected final Map<Pattern, Directive> directiveMap = new LinkedHashMap<>();

    private final List<String> sitemapList = new ArrayList<>();

    public boolean allows(final String path, final String userAgent) {
        final Directive directive = getMatchedDirective(userAgent);
        if (directive == null) {
            return true;
        }
        return directive.allows(path);
    }

    public int getCrawlDelay(final String userAgent) {
        final Directive directive = getMatchedDirective(userAgent);
        if (directive == null) {
            return 0;
        }
        return directive.getCrawlDelay();
    }

    public Directive getMatchedDirective(final String userAgent) {
        final String target;
        if (userAgent == null) {
            target = StringUtil.EMPTY;
        } else {
            target = userAgent;
        }

        int maxUaLength = -1;
        Directive matchedDirective = null;
        for (final Map.Entry<Pattern, Directive> entry : directiveMap
                .entrySet()) {
            if (entry.getKey().matcher(target).find()) {
                final Directive directive = entry.getValue();
                final String ua = directive.getUserAgent();
                int uaLength = 0;
                if (!ALL_BOTS.equals(ua)) {
                    uaLength = ua.length();
                }
                if (uaLength > maxUaLength) {
                    matchedDirective = directive;
                    maxUaLength = uaLength;
                }
            }
        }

        return matchedDirective;
    }

    public Directive getDirective(final String userAgent) {
        if (userAgent == null) {
            return null;
        }
        for (final Directive directive : directiveMap.values()) {
            if (userAgent.equals(directive.getUserAgent())) {
                return directive;
            }
        }
        return null;
    }

    public void addDirective(final Directive directive) {
        directiveMap.put(Pattern.compile(
                directive.getUserAgent().replace("*", ".*"),
                Pattern.CASE_INSENSITIVE), directive);
    }

    public void addSitemap(final String url) {
        sitemapList.add(url);
    }

    public String[] getSitemaps() {
        return sitemapList.toArray(new String[sitemapList.size()]);
    }

    public static class Directive {
        private final String userAgent;

        private int crawlDelay;

        private final List<String> allowedPaths = new ArrayList<>();

        private final List<String> disallowedPaths = new ArrayList<>();

        public Directive(final String userAgent) {
            this.userAgent = userAgent;
        }

        public void setCrawlDelay(final int crawlDelay) {
            this.crawlDelay = crawlDelay;
        }

        public int getCrawlDelay() {
            return crawlDelay;
        }

        public String getUserAgent() {
            return userAgent;
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

    @Override
    public String toString() {
        return "RobotsTxt [directiveMap=" + directiveMap + ", sitemapList="
                + sitemapList + "]";
    }

}
