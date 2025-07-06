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
 * Robots.txt Specifications:
 * <ul>
 * <li><a href=
 * "https://developers.google.com/webmasters/control-crawl-index/docs/robots_txt"
 * >https://developers.google.com/webmasters/control-crawl-index/docs/robots_txt
 * </a></li>
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
     * @param stream the input stream to parse
     * @param charsetName the character encoding to use
     * @return the parsed RobotsTxt object, or null if disabled
     */
    public RobotsTxt parse(final InputStream stream, final String charsetName) {
        if (!enabled) {
            return null;
        }

        try {
            @SuppressWarnings("resource")
            final BufferedReader reader = new BufferedReader(new InputStreamReader(new BOMInputStream(stream), charsetName));

            String line;
            final RobotsTxt robotsTxt = new RobotsTxt();
            final List<Directive> currentDirectiveList = new ArrayList<>();
            boolean isGroupRecordStarted = false;
            while ((line = reader.readLine()) != null) {
                line = stripComment(line).trim();
                if (StringUtil.isEmpty(line)) {
                    continue;
                }

                String value = getValue(USER_AGENT_RECORD, line);
                if (value != null) {
                    if (isGroupRecordStarted) {
                        currentDirectiveList.clear();
                        isGroupRecordStarted = false;
                    }
                    final String userAgent = value.toLowerCase(Locale.ENGLISH);
                    Directive currentDirective = robotsTxt.getDirective(userAgent);
                    if (currentDirective == null) {
                        currentDirective = new Directive(userAgent);
                        robotsTxt.addDirective(currentDirective);
                        currentDirectiveList.add(currentDirective);
                    }
                } else {
                    isGroupRecordStarted = true;
                    value = getValue(DISALLOW_RECORD, line);
                    if (value != null) {
                        if (!currentDirectiveList.isEmpty() && value.length() > 0) {
                            for (final Directive directive : currentDirectiveList) {
                                directive.addDisallow(value);
                            }
                        }
                    } else if ((value = getValue(ALLOW_RECORD, line)) != null) {
                        if (!currentDirectiveList.isEmpty() && value.length() > 0) {
                            for (final Directive directive : currentDirectiveList) {
                                directive.addAllow(value);
                            }
                        }
                    } else if ((value = getValue(CRAWL_DELAY_RECORD, line)) != null) {
                        if (!currentDirectiveList.isEmpty()) {
                            try {
                                final int crawlDelay = Integer.parseInt(value);
                                for (final Directive directive : currentDirectiveList) {
                                    directive.setCrawlDelay(Math.max(0, crawlDelay));
                                }
                            } catch (final NumberFormatException e) {
                                // ignore invalid crawl-delay values
                            }
                        }
                    } else if ((value = getValue(SITEMAP_RECORD, line)) != null && value.length() > 0) {
                        robotsTxt.addSitemap(value);
                    }
                }
            }

            return robotsTxt;
        } catch (final Exception e) {
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
