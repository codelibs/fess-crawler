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
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.input.BOMInputStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.core.lang.SystemUtil;
import org.codelibs.fess.crawler.Constants;
import org.codelibs.fess.crawler.CrawlerContext;
import org.codelibs.fess.crawler.entity.HostState;
import org.codelibs.fess.crawler.entity.HostState.RobotsTxtStatus;
import org.codelibs.fess.crawler.entity.RobotsTxt;
import org.codelibs.fess.crawler.entity.RobotsTxt.Directive;
import org.codelibs.fess.crawler.exception.MaxLengthExceededException;
import org.codelibs.fess.crawler.exception.RobotsTxtDisallowedException;
import org.codelibs.fess.crawler.exception.RobotsTxtException;
import org.codelibs.fess.crawler.exception.RobotsTxtUnavailableException;

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
 * <p>{@link #checkRobotsTxt(CrawlerContext, String, String, RobotsTxtFetcher, RobotsTxtPolicy)} fetches and applies
 * robots.txt per origin for the crawler clients, following the RFC 9309 status code and redirect rules.</p>
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

    /** The maximum number of redirects followed when fetching robots.txt (RFC 9309 section 2.3.1.2). */
    public static final int MAX_REDIRECTS = 5;

    private static final Logger logger = LogManager.getLogger(RobotsTxtHelper.class);

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

                    // Try to parse as Disallow directive (group-member record)
                    value = getValue(DISALLOW_RECORD, line);
                    if (value != null) {
                        isGroupRecordStarted = true;
                        // Only process if we have a current user-agent and value is not empty
                        if (!currentDirectiveList.isEmpty() && value.length() > 0) {
                            for (final Directive directive : currentDirectiveList) {
                                directive.addDisallow(value);
                            }
                        }
                        continue;
                    }

                    // Try to parse as Allow directive (group-member record)
                    value = getValue(ALLOW_RECORD, line);
                    if (value != null) {
                        isGroupRecordStarted = true;
                        // Only process if we have a current user-agent and value is not empty
                        if (!currentDirectiveList.isEmpty() && value.length() > 0) {
                            for (final Directive directive : currentDirectiveList) {
                                directive.addAllow(value);
                            }
                        }
                        continue;
                    }

                    // Try to parse as Crawl-delay directive (group-member record)
                    value = getValue(CRAWL_DELAY_RECORD, line);
                    if (value != null) {
                        isGroupRecordStarted = true;
                        if (!currentDirectiveList.isEmpty() && !StringUtil.isEmpty(value)) {
                            try {
                                final double crawlDelay = Double.parseDouble(value);
                                if (!Double.isNaN(crawlDelay) && !Double.isInfinite(crawlDelay)) {
                                    final long crawlDelayMillis = Math.round(Math.max(0, crawlDelay) * 1000);
                                    for (final Directive directive : currentDirectiveList) {
                                        directive.setCrawlDelayMillis(crawlDelayMillis);
                                    }
                                }
                            } catch (final NumberFormatException e) {
                                // Ignore invalid crawl-delay values (non-numeric)
                                // This allows parsing to continue with other directives
                            }
                        }
                        continue;
                    }

                    // Try to parse as Sitemap directive (not a group-member record per RFC 9309)
                    value = getValue(SITEMAP_RECORD, line);
                    if (value != null && value.length() > 0) {
                        robotsTxt.addSitemap(value);
                        continue;
                    }

                    // If we reach here, the line didn't match any known directive
                    // Silently ignore it to allow parsing to continue
                    // Unknown directives are not group-member records per RFC 9309

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
     * Fetches, resolves and applies the robots.txt of the origin of a URL.
     *
     * <p>robots.txt is resolved once per origin and the result is kept on the {@link HostState} of the
     * {@link CrawlerContext}. Threads of the same origin wait for the thread that is fetching it.
     * The status code is interpreted as RFC 9309 section 2.3.1 describes:</p>
     * <ul>
     * <li>2xx: the rules are parsed and applied; Sitemap lines are passed to {@link CrawlerContext#addSitemaps(String[])}.</li>
     * <li>3xx: the Location is followed up to {@link #MAX_REDIRECTS} times; beyond that, or without a usable Location,
     * everything is allowed.</li>
     * <li>4xx other than 429: everything is allowed.</li>
     * <li>429, 5xx or a network error: robots.txt is unavailable. The backoff of the origin is recorded on the
     * {@link HostState} (honouring Retry-After) and the URL fails with {@link RobotsTxtUnavailableException} so that it
     * can be retried later; robots.txt is not fetched again before the backoff ends, and a URL checked before then fails
     * at once with {@link RobotsTxtUnavailableException#isFetchAttempted()} false. When the first fetch and
     * {@link RobotsTxtPolicy#maxRetries()} retries after it have all failed, nothing of the origin is allowed.
     * With {@link RobotsTxtPolicy#allowOnUnavailable()}, everything is allowed at once.</li>
     * </ul>
     * <p>A robots.txt that is too large or cannot be parsed allows everything. A body in an unknown charset is read as UTF-8.
     * A fetch that ends because the thread was interrupted (see {@link #isInterruptedFetch(Throwable)}) is not counted as a failed
     * fetch and records no backoff; the URL fails with {@link RobotsTxtUnavailableException}.</p>
     *
     * @param context the crawler context that holds the per-origin state
     * @param url the URL to check
     * @param userAgent the User-Agent used to select the group of rules
     * @param fetcher fetches robots.txt without following redirects
     * @param policy how the rules are applied
     * @throws RobotsTxtDisallowedException if robots.txt does not allow the URL
     * @throws RobotsTxtUnavailableException if robots.txt is unavailable and the URL should be retried later
     */
    public void checkRobotsTxt(final CrawlerContext context, final String url, final String userAgent, final RobotsTxtFetcher fetcher,
            final RobotsTxtPolicy policy) {
        if (!enabled) {
            return;
        }
        final HostState hostState = context.getHostState(url);
        if (hostState == null) {
            return;
        }

        synchronized (hostState) {
            final RobotsTxtStatus status = hostState.getRobotsTxtStatus();
            if (status == RobotsTxtStatus.UNAVAILABLE && SystemUtil.currentTimeMillis() < hostState.getBackoffUntil()) {
                // robots.txt is not requested, so the URL has not been tried
                throw new RobotsTxtUnavailableException(url, 0L, null, false);
            }
            if (status == null || status == RobotsTxtStatus.UNAVAILABLE) {
                final Unavailable unavailable = resolveRobotsTxt(context, HostState.toOrigin(url), userAgent, fetcher, policy, hostState);
                if (unavailable != null) {
                    if (isInterruptedFetch(unavailable.cause())) {
                        // not a failure of the site: keep the state as it was and let the URL be re-queued
                        if (unavailable.cause() instanceof InterruptedException) {
                            Thread.currentThread().interrupt();
                        }
                        throw new RobotsTxtUnavailableException(url, 0L, unavailable.cause());
                    }
                    hostState.setRobotsTxt(RobotsTxtStatus.UNAVAILABLE, null, 0L);
                    final int failureCount = hostState.incrementAndGetRobotsTxtFailureCount();
                    if (policy.allowOnUnavailable()) {
                        if (logger.isInfoEnabled()) {
                            logger.info("{} is unavailable ({}); all URLs of the site are allowed.", unavailable.robotsTxtUrl(),
                                    unavailable.reason());
                        }
                        hostState.setRobotsTxt(RobotsTxtStatus.ALLOW_ALL, null, 0L);
                    } else if (failureCount > policy.maxRetries()) {
                        logger.warn("Gave up fetching {} after {} attempts (last failure: {}); no URL of the site is crawled.",
                                unavailable.robotsTxtUrl(), failureCount, unavailable.reason());
                        hostState.setRobotsTxt(RobotsTxtStatus.DISALLOW_ALL, null, 0L);
                    } else {
                        final long now = SystemUtil.currentTimeMillis();
                        final long retryAfterMillis = parseRetryAfter(unavailable.retryAfter(), now);
                        // stamp the backoff here so that the next attempt waits, whoever calls next
                        final long wait = hostState.recordFailure(now, retryAfterMillis, context.getBackoffBaseMillis(),
                                context.getMaxBackoffMillis());
                        if (logger.isDebugEnabled()) {
                            logger.debug("{} is unavailable ({}, attempt {}); retrying in {} ms.", unavailable.robotsTxtUrl(),
                                    unavailable.reason(), failureCount, wait, unavailable.cause());
                        }
                        throw new RobotsTxtUnavailableException(url, retryAfterMillis, unavailable.cause());
                    }
                }
            }
        }

        if (!hostState.isAllowedByRobotsTxt(url)) {
            throw new RobotsTxtDisallowedException(url);
        }
    }

    /**
     * Returns whether a robots.txt request ended because the crawler thread was interrupted rather than because the site
     * failed: the thread is interrupted, or the cause is an {@link InterruptedException} or an {@link InterruptedIOException}
     * that is not a timeout. A timeout ({@link java.net.SocketTimeoutException}, or an {@link InterruptedIOException} named
     * {@code *TimeoutException} such as a connect or connection-pool timeout of HttpClient) is a failure of the site.
     *
     * @param cause the exception of the request, or null
     * @return true if the request was interrupted
     */
    protected static boolean isInterruptedFetch(final Throwable cause) {
        if (Thread.currentThread().isInterrupted() || cause instanceof InterruptedException) {
            return true;
        }
        if (!(cause instanceof InterruptedIOException)) {
            return false;
        }
        for (Class<?> clazz = cause.getClass(); clazz != null; clazz = clazz.getSuperclass()) {
            if (clazz.getSimpleName().endsWith("TimeoutException")) {
                return false;
            }
        }
        return true;
    }

    /**
     * Why robots.txt could not be retrieved.
     *
     * @param robotsTxtUrl the URL that failed
     * @param reason the failure for log messages, such as "HTTP 503" or the exception
     * @param retryAfter the Retry-After header, or null
     * @param cause the exception, or null
     */
    private record Unavailable(String robotsTxtUrl, String reason, String retryAfter, Throwable cause) {
    }

    /**
     * Fetches robots.txt, following redirects, and stores the outcome on the host state.
     *
     * @return null when the status was stored, or the reason when robots.txt is unavailable
     */
    private Unavailable resolveRobotsTxt(final CrawlerContext context, final String origin, final String userAgent,
            final RobotsTxtFetcher fetcher, final RobotsTxtPolicy policy, final HostState hostState) {
        String robotsTxtUrl = origin + "/robots.txt";
        for (int redirects = 0;; redirects++) {
            if (logger.isInfoEnabled()) {
                logger.info("Checking URL: {}", robotsTxtUrl);
            }
            final RobotsTxtResponse response;
            try {
                response = fetcher.fetch(robotsTxtUrl);
            } catch (final MaxLengthExceededException e) {
                if (logger.isInfoEnabled()) {
                    logger.info("{} is too large; all URLs of the site are allowed: {}", robotsTxtUrl, e.getMessage());
                }
                hostState.setRobotsTxt(RobotsTxtStatus.ALLOW_ALL, null, 0L);
                return null;
            } catch (final Exception e) {
                return new Unavailable(robotsTxtUrl, e.getClass().getSimpleName() + ": " + e.getMessage(), null, e);
            }
            if (response == null) {
                return new Unavailable(robotsTxtUrl, "no response", null, null);
            }

            final int statusCode = response.statusCode();
            if (statusCode >= 200 && statusCode < 300) {
                applyRobotsTxt(context, robotsTxtUrl, userAgent, response, policy, hostState);
                return null;
            }
            if (statusCode >= 300 && statusCode < 400) {
                final String location = resolveLocation(robotsTxtUrl, response.location());
                if (location == null) {
                    if (logger.isInfoEnabled()) {
                        logger.info("{} returned {} without a valid Location ({}); all URLs of the site are allowed.", robotsTxtUrl,
                                statusCode, response.location());
                    }
                    hostState.setRobotsTxt(RobotsTxtStatus.ALLOW_ALL, null, 0L);
                    return null;
                }
                if (redirects >= MAX_REDIRECTS) {
                    if (logger.isInfoEnabled()) {
                        logger.info("{} redirects more than {} times; all URLs of the site are allowed.", origin + "/robots.txt",
                                MAX_REDIRECTS);
                    }
                    hostState.setRobotsTxt(RobotsTxtStatus.ALLOW_ALL, null, 0L);
                    return null;
                }
                robotsTxtUrl = location;
                continue;
            }
            if (statusCode >= 400 && statusCode < 500 && statusCode != 429) {
                if (logger.isDebugEnabled()) {
                    logger.debug("{} returned {}; all URLs of the site are allowed.", robotsTxtUrl, statusCode);
                }
                hostState.setRobotsTxt(RobotsTxtStatus.ALLOW_ALL, null, 0L);
                return null;
            }
            return new Unavailable(robotsTxtUrl, "HTTP " + statusCode, response.retryAfter(), null);
        }
    }

    /**
     * Parses a successful robots.txt response and stores its rules on the host state.
     */
    private void applyRobotsTxt(final CrawlerContext context, final String robotsTxtUrl, final String userAgent,
            final RobotsTxtResponse response, final RobotsTxtPolicy policy, final HostState hostState) {
        final String charset = isSupportedCharset(response.charset()) ? response.charset() : Constants.UTF_8;
        final byte[] body = response.body() == null ? new byte[0] : response.body();
        final RobotsTxt robotsTxt;
        try {
            robotsTxt = parse(new ByteArrayInputStream(body), charset);
        } catch (final Exception e) {
            if (logger.isInfoEnabled()) {
                logger.info("Could not parse {}; all URLs of the site are allowed: {}", robotsTxtUrl, e.getMessage());
            }
            hostState.setRobotsTxt(RobotsTxtStatus.ALLOW_ALL, null, 0L);
            return;
        }
        if (robotsTxt == null) {
            hostState.setRobotsTxt(RobotsTxtStatus.ALLOW_ALL, null, 0L);
            return;
        }

        final String[] sitemaps = robotsTxt.getSitemaps();
        if (sitemaps.length > 0) {
            context.addSitemaps(sitemaps);
        }

        if (!policy.useDisallows()) {
            hostState.setRobotsTxt(RobotsTxtStatus.ALLOW_ALL, null, 0L);
            return;
        }
        Directive directive = robotsTxt.getMatchedDirective(userAgent);
        if (directive != null && !policy.useAllows()) {
            final Directive disallowsOnly = new Directive(directive.getUserAgent());
            for (final String disallow : directive.getDisallows()) {
                disallowsOnly.addDisallow(disallow);
            }
            disallowsOnly.setCrawlDelayMillis(directive.getCrawlDelayMillis());
            directive = disallowsOnly;
        }
        hostState.setRobotsTxt(RobotsTxtStatus.PARSED, directive, directive == null ? 0L : directive.getCrawlDelayMillis());
    }

    /**
     * Checks a charset name from Content-Type. robots.txt is UTF-8 (RFC 9309 section 2.3), which is used for anything else.
     */
    private static boolean isSupportedCharset(final String charset) {
        if (StringUtil.isBlank(charset)) {
            return false;
        }
        try {
            return Charset.isSupported(charset);
        } catch (final IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Resolves a Location header against the URL that returned it.
     *
     * @return the absolute http(s) URL, or null if the Location is missing or unusable
     */
    private static String resolveLocation(final String currentUrl, final String location) {
        if (StringUtil.isBlank(location)) {
            return null;
        }
        try {
            final URI resolved = new URI(currentUrl).resolve(location.trim());
            final String scheme = resolved.getScheme();
            if (resolved.getHost() == null || !("http".equalsIgnoreCase(scheme) || "https".equalsIgnoreCase(scheme))) {
                return null;
            }
            return resolved.toString();
        } catch (final Exception e) {
            return null;
        }
    }

    /**
     * Parses a Retry-After header value (RFC 9110 section 10.2.3).
     *
     * @param value delta-seconds (a non-negative integer) or an HTTP-date in the IMF-fixdate format
     * @param now the current time in milliseconds
     * @return the wait in milliseconds from {@code now}; 0 if the value is missing, unparsable, zero or in the past
     */
    public static long parseRetryAfter(final String value, final long now) {
        if (StringUtil.isBlank(value)) {
            return 0L;
        }
        final String trimmed = value.trim();
        if (trimmed.chars().allMatch(c -> c >= '0' && c <= '9')) {
            try {
                return Math.multiplyExact(Long.parseLong(trimmed), 1000L);
            } catch (final NumberFormatException | ArithmeticException e) {
                return Long.MAX_VALUE;
            }
        }
        try {
            final long time = ZonedDateTime.parse(trimmed, DateTimeFormatter.RFC_1123_DATE_TIME).toInstant().toEpochMilli();
            return Math.max(0L, time - now);
        } catch (final DateTimeParseException e) {
            return 0L;
        }
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
