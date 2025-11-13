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
package org.codelibs.fess.crawler.helper;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.input.BOMInputStream;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.crawler.Constants;
import org.codelibs.fess.crawler.entity.RobotsTxt;
import org.codelibs.fess.crawler.entity.RobotsTxt.Directive;
import org.codelibs.fess.crawler.exception.RobotsTxtException;

/**
 * Robots.txt Parser following RFC 9309 specification.
 *
 * <p>This implementation supports the following features:</p>
 * <ul>
 * <li>User-agent directive with wildcard (*) matching</li>
 * <li>Disallow and Allow directives with pattern matching</li>
 * <li>Wildcard (*) in paths - matches any sequence of characters</li>
 * <li>End-of-path ($) matching - matches the end of URL path</li>
 * <li>Crawl-delay directive</li>
 * <li>Sitemap directive</li>
 * <li>Comment support (#)</li>
 * <li>Priority-based matching (longest match wins, Allow beats Disallow at equal length)</li>
 * </ul>
 *
 * <p>References:</p>
 * <ul>
 * <li><a href="https://datatracker.ietf.org/doc/html/rfc9309">RFC 9309 - Robots Exclusion Protocol</a></li>
 * <li><a href="https://developers.google.com/search/docs/crawling-indexing/robots/robots_txt">
 * Google's robots.txt Specification</a></li>
 * </ul>
 *
 * @author bowez
 * @author shinsuke
 *
 */
public class RobotsTxtHelper {

    /** Pattern for parsing user-agent records. */
    protected static final Pattern USER_AGENT_RECORD =
            Pattern.compile("^user-agent:\\s*([^\\t\\n\\x0B\\f\\r]+)\\s*$", Pattern.CASE_INSENSITIVE);

    /** Pattern for parsing disallow records. */
    protected static final Pattern DISALLOW_RECORD = Pattern.compile("^disallow:\\s*([^\\s]*)\\s*$", Pattern.CASE_INSENSITIVE);

    /** Pattern for parsing allow records. */
    protected static final Pattern ALLOW_RECORD = Pattern.compile("^allow:\\s*([^\\s]*)\\s*$", Pattern.CASE_INSENSITIVE);

    /** Pattern for parsing crawl-delay records. */
    protected static final Pattern CRAWL_DELAY_RECORD = Pattern.compile("^crawl-delay:\\s*([^\\s]+)\\s*$", Pattern.CASE_INSENSITIVE);

    /**
     * Pattern for Sitemap record.
     */
    protected static final Pattern SITEMAP_RECORD = Pattern.compile("^sitemap:\\s*([^\\s]+)\\s*$", Pattern.CASE_INSENSITIVE);

    /** Whether robots.txt processing is enabled. */
    protected boolean enabled = true;

    /**
     * Creates a new RobotsTxtHelper instance.
     */
    public RobotsTxtHelper() {
        // Default constructor
    }

    /**
     * Parses a robots.txt file from the given input stream using UTF-8 encoding.
     * @param stream the input stream to parse
     * @return the parsed RobotsTxt object, or null if disabled
     */
    public RobotsTxt parse(final InputStream stream) {
        return parse(stream, Constants.UTF_8);
    }

    /**
     * Parses a robots.txt file from the given input stream using the specified character encoding.
     *
     * <p>This method is designed to be resilient to malformed robots.txt files.
     * It will parse valid directives and ignore invalid ones, ensuring that partial
     * content can be extracted even from poorly formatted files.</p>
     *
     * <p>The following errors are handled gracefully (line is skipped, parsing continues):</p>
     * <ul>
     * <li>Invalid directive formats</li>
     * <li>Unknown directives</li>
     * <li>Invalid crawl-delay values (non-numeric, negative)</li>
     * <li>Directives before any User-agent declaration (ignored)</li>
     * <li>Empty values for directives</li>
     * </ul>
     *
     * <p>Only fatal I/O errors will cause parsing to fail with an exception.</p>
     *
     * @param stream the input stream to parse
     * @param charsetName the character encoding to use
     * @return the parsed RobotsTxt object, or null if disabled
     * @throws RobotsTxtException if a fatal I/O error occurs
     */
    public RobotsTxt parse(final InputStream stream, final String charsetName) {
        if (!enabled) {
            return null;
        }

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(new BOMInputStream(stream), charsetName));

            String line;
            final RobotsTxt robotsTxt = new RobotsTxt();
            final List<Directive> currentDirectiveList = new ArrayList<>();
            boolean isGroupRecordStarted = false;

            while ((line = reader.readLine()) != null) {
                try {
                    // Strip comments and trim whitespace
                    line = stripComment(line).trim();
                    if (StringUtil.isEmpty(line)) {
                        continue;
                    }

                    // Try to parse as User-agent directive
                    String value = getValue(USER_AGENT_RECORD, line);
                    if (value != null) {
                        // If we've seen group-member records (Disallow, Allow, etc.),
                        // this starts a new group, so clear the current directive list
                        if (isGroupRecordStarted) {
                            currentDirectiveList.clear();
                            isGroupRecordStarted = false;
                        }
                        // Normalize user-agent to lowercase
                        final String userAgent = value.toLowerCase(Locale.ENGLISH);
                        Directive currentDirective = robotsTxt.getDirective(userAgent);
                        if (currentDirective == null) {
                            currentDirective = new Directive(userAgent);
                            robotsTxt.addDirective(currentDirective);
                        }
                        // Add to current list - multiple consecutive User-agent lines
                        // form a group and subsequent rules apply to all of them
                        currentDirectiveList.add(currentDirective);
                        continue;
                    }

                    // Mark that we've seen group-member records
                    isGroupRecordStarted = true;

                    // Try to parse as Disallow directive
                    value = getValue(DISALLOW_RECORD, line);
                    if (value != null) {
                        // Only process if we have a current user-agent and value is not empty
                        if (!currentDirectiveList.isEmpty() && value.length() > 0) {
                            for (final Directive directive : currentDirectiveList) {
                                directive.addDisallow(value);
                            }
                        }
                        continue;
                    }

                    // Try to parse as Allow directive
                    value = getValue(ALLOW_RECORD, line);
                    if (value != null) {
                        // Only process if we have a current user-agent and value is not empty
                        if (!currentDirectiveList.isEmpty() && value.length() > 0) {
                            for (final Directive directive : currentDirectiveList) {
                                directive.addAllow(value);
                            }
                        }
                        continue;
                    }

                    // Try to parse as Crawl-delay directive
                    value = getValue(CRAWL_DELAY_RECORD, line);
                    if (value != null) {
                        if (!currentDirectiveList.isEmpty() && !StringUtil.isEmpty(value)) {
                            try {
                                final int crawlDelay = Integer.parseInt(value);
                                for (final Directive directive : currentDirectiveList) {
                                    directive.setCrawlDelay(Math.max(0, crawlDelay));
                                }
                            } catch (final NumberFormatException e) {
                                // Ignore invalid crawl-delay values (non-numeric)
                                // This allows parsing to continue with other directives
                            }
                        }
                        continue;
                    }

                    // Try to parse as Sitemap directive
                    value = getValue(SITEMAP_RECORD, line);
                    if (value != null && value.length() > 0) {
                        robotsTxt.addSitemap(value);
                        continue;
                    }

                    // If we reach here, the line didn't match any known directive
                    // Silently ignore it to allow parsing to continue
                    // This handles unknown directives, malformed lines, etc.

                } catch (final Exception e) {
                    // Catch any unexpected errors during line processing
                    // Log if logger is available, but continue parsing
                    // This ensures that one bad line doesn't break the entire parse
                    continue;
                }
            }

            return robotsTxt;
        } catch (final java.io.IOException e) {
            // Only throw exception for fatal I/O errors
            throw new RobotsTxtException("Failed to read robots.txt due to I/O error.", e);
        } catch (final Exception e) {
            // Catch any other fatal errors (e.g., encoding issues)
            throw new RobotsTxtException("Failed to parse robots.txt.", e);
        }
    }

    /**
     * Extracts the value from a line using the given pattern.
     * @param pattern the pattern to match against
     * @param line the line to extract the value from
     * @return the extracted value, or null if no match
     */
    protected String getValue(final Pattern pattern, final String line) {
        final Matcher m = pattern.matcher(line);
        if (m.matches() && m.groupCount() > 0) {
            return m.group(1);
        }
        return null;
    }

    /**
     * Strips comments from a line (everything after '#' character).
     * @param line the line to strip comments from
     * @return the line without comments
     */
    protected String stripComment(final String line) {
        final int commentIndex = line.indexOf('#');
        if (commentIndex != -1) {
            return line.substring(0, commentIndex);
        }
        return line;
    }

    /**
     * Checks if robots.txt processing is enabled.
     * @return true if enabled, false otherwise
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Sets whether robots.txt processing is enabled.
     * @param enabled true to enable, false to disable
     */
    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }

}
