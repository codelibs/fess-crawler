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
     * Represents a path pattern used in robots.txt allow/disallow rules.
     * Supports wildcard (*) and end-of-path ($) matching according to RFC 9309.
     */
    public static class PathPattern implements Comparable<PathPattern> {
        /** The original path pattern string. */
        private final String pattern;

        /** The compiled regular expression pattern for matching. */
        private final Pattern regexPattern;

        /** The priority length used for sorting (patterns without wildcards are prioritized by length). */
        private final int priorityLength;

        /**
         * Constructs a new PathPattern from the given robots.txt path pattern.
         * @param pattern the path pattern string from robots.txt (may contain * and $)
         */
        public PathPattern(final String pattern) {
            this.pattern = pattern;
            this.regexPattern = compilePattern(pattern);
            this.priorityLength = calculatePriorityLength(pattern);
        }

        /**
         * Compiles a robots.txt path pattern into a regular expression pattern.
         * According to RFC 9309:
         * - '*' matches any sequence of characters (including the empty sequence)
         * - '$' matches the end of the URL path
         * - All other characters are matched literally
         *
         * @param pattern the robots.txt path pattern
         * @return the compiled Pattern object
         */
        private static Pattern compilePattern(final String pattern) {
            final StringBuilder regex = new StringBuilder();
            regex.append("^"); // Match from the start

            for (int i = 0; i < pattern.length(); i++) {
                final char c = pattern.charAt(i);
                if (c == '*') {
                    // Wildcard: matches any sequence of characters
                    regex.append(".*");
                } else if (c == '$') {
                    // End-of-path: matches the end of the URL
                    if (i == pattern.length() - 1) {
                        regex.append("$");
                    } else {
                        // $ in the middle is treated as a literal character
                        regex.append("\\$");
                    }
                } else {
                    // Escape special regex characters
                    if ("\\[]{}()+?.^|".indexOf(c) != -1) {
                        regex.append('\\');
                    }
                    regex.append(c);
                }
            }

            // If pattern doesn't end with $, it implicitly matches anything after it
            if (pattern.isEmpty() || pattern.charAt(pattern.length() - 1) != '$') {
                // This makes "/fish" match "/fish", "/fishing", "/fish/", etc.
                // No need to add anything - the pattern naturally continues
            }

            return Pattern.compile(regex.toString());
        }

        /**
         * Calculates the priority length for this pattern.
         * According to RFC 9309, longer patterns have higher priority.
         * The priority length is the number of characters before any wildcard.
         *
         * @param pattern the path pattern
         * @return the priority length
         */
        private static int calculatePriorityLength(final String pattern) {
            // For priority, we count the pattern length, treating * as having length 0
            // and $ as having length 1
            int length = 0;
            for (int i = 0; i < pattern.length(); i++) {
                final char c = pattern.charAt(i);
                if (c == '*') {
                    // Wildcard doesn't contribute to priority length
                    continue;
                } else if (c == '$') {
                    // $ at the end adds to specificity
                    length++;
                } else {
                    length++;
                }
            }
            return length;
        }

        /**
         * Checks if the given path matches this pattern.
         * @param path the path to check
         * @return true if the path matches, false otherwise
         */
        public boolean matches(final String path) {
            return regexPattern.matcher(path).find();
        }

        /**
         * Gets the original pattern string.
         * @return the pattern string
         */
        public String getPattern() {
            return pattern;
        }

        /**
         * Gets the priority length of this pattern.
         * @return the priority length
         */
        public int getPriorityLength() {
            return priorityLength;
        }

        /**
         * Compares this pattern with another for priority ordering.
         * Longer patterns (higher priority length) come first.
         * @param other the other pattern to compare with
         * @return negative if this has higher priority, positive if other has higher priority
         */
        @Override
        public int compareTo(final PathPattern other) {
            // Higher priority length comes first (descending order)
            return Integer.compare(other.priorityLength, this.priorityLength);
        }

        @Override
        public String toString() {
            return "PathPattern{pattern='" + pattern + "', priorityLength=" + priorityLength + "}";
        }
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

        /** The list of allowed path patterns for this directive. */
        private final List<PathPattern> allowedPaths = new ArrayList<>();

        /** The list of disallowed path patterns for this directive. */
        private final List<PathPattern> disallowedPaths = new ArrayList<>();

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
         * According to RFC 9309:
         * 1. Find the longest matching pattern (by priority length)
         * 2. If both Allow and Disallow patterns match with the same length, Allow takes precedence
         * 3. If no pattern matches, the path is allowed by default
         *
         * @param path the path to check
         * @return true if the path is allowed, false otherwise
         */
        public boolean allows(final String path) {
            PathPattern longestAllowMatch = null;
            PathPattern longestDisallowMatch = null;

            // Find the longest matching Allow pattern
            for (final PathPattern allowPattern : allowedPaths) {
                if (allowPattern.matches(path)) {
                    if (longestAllowMatch == null || allowPattern.getPriorityLength() > longestAllowMatch.getPriorityLength()) {
                        longestAllowMatch = allowPattern;
                    }
                }
            }

            // Find the longest matching Disallow pattern
            for (final PathPattern disallowPattern : disallowedPaths) {
                if (disallowPattern.matches(path)) {
                    if (longestDisallowMatch == null
                            || disallowPattern.getPriorityLength() > longestDisallowMatch.getPriorityLength()) {
                        longestDisallowMatch = disallowPattern;
                    }
                }
            }

            // Apply RFC 9309 priority rules
            if (longestAllowMatch == null && longestDisallowMatch == null) {
                // No matching rules, allow by default
                return true;
            } else if (longestAllowMatch != null && longestDisallowMatch == null) {
                // Only Allow matches
                return true;
            } else if (longestAllowMatch == null && longestDisallowMatch != null) {
                // Only Disallow matches
                return false;
            } else {
                // Both match - compare lengths
                final int allowLength = longestAllowMatch.getPriorityLength();
                final int disallowLength = longestDisallowMatch.getPriorityLength();

                if (allowLength > disallowLength) {
                    // Allow is more specific
                    return true;
                } else if (disallowLength > allowLength) {
                    // Disallow is more specific
                    return false;
                } else {
                    // Same length - Allow takes precedence (RFC 9309)
                    return true;
                }
            }
        }

        /**
         * Adds an allowed path pattern to this directive.
         * Supports wildcards (*) and end-of-path ($) according to RFC 9309.
         * @param path the path pattern to allow
         */
        public void addAllow(final String path) {
            final PathPattern pattern = new PathPattern(path);
            // Check if pattern already exists
            boolean exists = false;
            for (final PathPattern existing : allowedPaths) {
                if (existing.getPattern().equals(path)) {
                    exists = true;
                    break;
                }
            }
            if (!exists) {
                allowedPaths.add(pattern);
            }
        }

        /**
         * Adds a disallowed path pattern to this directive.
         * Supports wildcards (*) and end-of-path ($) according to RFC 9309.
         * @param path the path pattern to disallow
         */
        public void addDisallow(final String path) {
            final PathPattern pattern = new PathPattern(path);
            // Check if pattern already exists
            boolean exists = false;
            for (final PathPattern existing : disallowedPaths) {
                if (existing.getPattern().equals(path)) {
                    exists = true;
                    break;
                }
            }
            if (!exists) {
                disallowedPaths.add(pattern);
            }
        }

        /**
         * Gets all allowed path patterns for this directive.
         * @return an array of allowed path pattern strings
         */
        public String[] getAllows() {
            final String[] result = new String[allowedPaths.size()];
            for (int i = 0; i < allowedPaths.size(); i++) {
                result[i] = allowedPaths.get(i).getPattern();
            }
            return result;
        }

        /**
         * Gets all disallowed path patterns for this directive.
         * @return an array of disallowed path pattern strings
         */
        public String[] getDisallows() {
            final String[] result = new String[disallowedPaths.size()];
            for (int i = 0; i < disallowedPaths.size(); i++) {
                result[i] = disallowedPaths.get(i).getPattern();
            }
            return result;
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
