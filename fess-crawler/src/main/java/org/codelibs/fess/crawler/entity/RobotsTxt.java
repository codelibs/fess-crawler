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
package org.codelibs.fess.crawler.entity;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.codelibs.core.lang.StringUtil;

/**
 * Represents a robots.txt file parser and handler.
 * This class manages the rules defined in a robots.txt file, including user agent directives,
 * allowed/disallowed paths, crawl delays, and sitemap URLs.
 *
 * <p>The robots.txt protocol is implemented according to the standard specification,
 * supporting pattern matching for user agents, path-based access control, and crawl delay settings.</p>
 *
 * <p>Key features:</p>
 * <ul>
 *   <li>Supports multiple user-agent directives with pattern matching</li>
 *   <li>Handles Allow and Disallow rules for path-based access control</li>
 *   <li>Manages crawl delay settings per user agent</li>
 *   <li>Stores sitemap URLs listed in robots.txt</li>
 * </ul>
 *
 * <p>The class uses case-insensitive pattern matching for user agents and supports
 * wildcard characters (*) in user agent strings. When multiple directives match a user agent,
 * the most specific (longest) match is used.</p>
 *
 */
public class RobotsTxt {
    private static final String ALL_BOTS = "*";

    /** Map of user agent patterns to their corresponding directives. */
    protected final Map<Pattern, Directive> directiveMap = new LinkedHashMap<>();

    /** List of sitemap URLs found in the robots.txt file. */
    private final List<String> sitemapList = new ArrayList<>();

    /**
     * Creates a new RobotsTxt instance.
     */
    public RobotsTxt() {
        // Default constructor
    }

    /**
     * Checks if access to a given path is allowed for a specific user agent according to robots.txt rules.
     *
     * @param path The path to check for access permission
     * @param userAgent The user agent string to check against robots.txt directives
     * @return true if access is allowed, false if access is disallowed by robots.txt rules.
     *         Returns true if no matching directive is found for the user agent.
     */
    public boolean allows(final String path, final String userAgent) {
        final Directive directive = getMatchedDirective(userAgent);
        if (directive == null) {
            return true;
        }
        return directive.allows(path);
    }

    /**
     * Gets the crawl delay value for the specified user agent from robots.txt.
     * The crawl delay specifies the time (in seconds) to wait between successive requests.
     *
     * @param userAgent The user agent string to match against robots.txt directives
     * @return The crawl delay value in seconds. Returns 0 if no matching directive is found
     *         or no crawl delay is specified for the matching directive.
     */
    public int getCrawlDelay(final String userAgent) {
        final Directive directive = getMatchedDirective(userAgent);
        if (directive == null) {
            return 0;
        }
        return directive.getCrawlDelay();
    }

    /**
     * Returns the most specific directive matching the given user agent.
     * The method finds the longest matching user agent pattern in the directives,
     * excluding the general "*" pattern which matches all bots.
     *
     * @param userAgent the user agent string to match against directives,
     *                 can be null (treated as empty string)
     * @return the most specific matching directive, or null if no directive matches
     */
    public Directive getMatchedDirective(final String userAgent) {
        final String target;
        if (userAgent == null) {
            target = StringUtil.EMPTY;
        } else {
            target = userAgent;
        }

        int maxUaLength = -1;
        Directive matchedDirective = null;
        for (final Map.Entry<Pattern, Directive> entry : directiveMap.entrySet()) {
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

    /**
     * Retrieves the robots.txt directive for the specified user agent.
     *
     * @param userAgent The user agent string to look up in the directives
     * @return The Directive object matching the user agent, or null if no matching directive is found or if userAgent is null
     */
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

    /**
     * Adds a directive to the robots.txt rules.
     * The user-agent pattern in the directive is converted to a regular expression pattern,
     * where '*' is replaced with '.*' for pattern matching, and stored case-insensitively.
     *
     * @param directive The directive to add to the robots.txt rules
     */
    public void addDirective(final Directive directive) {
        directiveMap.put(Pattern.compile(directive.getUserAgent().replace("*", ".*"), Pattern.CASE_INSENSITIVE), directive);
    }

    /**
     * Adds a sitemap URL to the list of sitemaps.
     *
     * @param url The URL of the sitemap to be added
     */
    public void addSitemap(final String url) {
        if (!sitemapList.contains(url)) {
            sitemapList.add(url);
        }
    }

    /**
     * Returns an array of sitemap URLs.
     *
     * @return an array of sitemap URLs
     */
    public String[] getSitemaps() {
        return sitemapList.toArray(new String[sitemapList.size()]);
    }

    /**
     * Represents a directive in a robots.txt file.
     * A directive consists of a user agent, crawl delay, allowed paths, and disallowed paths.
     */
    public static class Directive {
        /** The user agent string this directive applies to. */
        private final String userAgent;

        /** The crawl delay in seconds for this directive. */
        private int crawlDelay;

        /** The list of allowed paths for this directive. */
        private final List<String> allowedPaths = new ArrayList<>();

        /** The list of disallowed paths for this directive. */
        private final List<String> disallowedPaths = new ArrayList<>();

        /**
         * Constructs a new Directive with the specified user agent.
         * @param userAgent the user agent string this directive applies to
         */
        public Directive(final String userAgent) {
            this.userAgent = userAgent;
        }

        /**
         * Sets the crawl delay for this directive.
         * @param crawlDelay the crawl delay in seconds
         */
        public void setCrawlDelay(final int crawlDelay) {
            this.crawlDelay = crawlDelay;
        }

        /**
         * Gets the crawl delay for this directive.
         * @return the crawl delay in seconds
         */
        public int getCrawlDelay() {
            return crawlDelay;
        }

        /**
         * Gets the user agent for this directive.
         * @return the user agent string
         */
        public String getUserAgent() {
            return userAgent;
        }

        /**
         * Checks if the given path is allowed according to this directive.
         * @param path the path to check
         * @return true if the path is allowed, false otherwise
         */
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

        /**
         * Adds an allowed path to this directive.
         * @param path the path to allow
         */
        public void addAllow(final String path) {
            if (!allowedPaths.contains(path)) {
                allowedPaths.add(path);
            }
        }

        /**
         * Adds a disallowed path to this directive.
         * @param path the path to disallow
         */
        public void addDisallow(final String path) {
            if (!disallowedPaths.contains(path)) {
                disallowedPaths.add(path);
            }
        }

        /**
         * Gets all allowed paths for this directive.
         * @return an array of allowed paths
         */
        public String[] getAllows() {
            return allowedPaths.toArray(new String[allowedPaths.size()]);
        }

        /**
         * Gets all disallowed paths for this directive.
         * @return an array of disallowed paths
         */
        public String[] getDisallows() {
            return disallowedPaths.toArray(new String[disallowedPaths.size()]);
        }
    }

    /**
     * Returns a string representation of this object.
     * @return A string representation.
     */
    @Override
    public String toString() {
        return "RobotsTxt [directiveMap=" + directiveMap + ", sitemapList=" + sitemapList + "]";
    }

}
