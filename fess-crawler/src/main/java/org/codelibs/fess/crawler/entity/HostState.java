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

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Holds the politeness state of one origin ({@code scheme://host[:port]}): the resolved robots.txt rules,
 * the Crawl-delay, the last access time and the failure backoff. One instance is shared by all crawler
 * threads, so every accessor is synchronized.
 */
public class HostState {

    /**
     * The resolution status of the robots.txt of an origin.
     */
    public enum RobotsTxtStatus {
        /** robots.txt was fetched and parsed; the directive decides. */
        PARSED,
        /** robots.txt does not exist or must be ignored (including 401/403); everything is allowed. */
        ALLOW_ALL,
        /** robots.txt stayed unavailable and fetching it was given up after the retries; nothing is allowed. */
        DISALLOW_ALL,
        /**
         * robots.txt could not be retrieved yet (429, 5xx or a network error). URLs of the origin are still admitted to the
         * queue, but fetching them fails with {@code RobotsTxtUnavailableException} and they are re-queued until robots.txt
         * is retrieved or given up.
         */
        UNAVAILABLE
    }

    private static final Pattern URL_PATTERN = Pattern.compile("^([A-Za-z][A-Za-z0-9+.-]*)://([^/?#]*)([^?#]*)(\\?[^#]*)?(#.*)?$");

    private RobotsTxtStatus robotsTxtStatus;

    private RobotsTxt.Directive directive;

    private long crawlDelayMillis;

    private int robotsTxtFailureCount;

    private int robotsTxtLastFailureAttempts;

    private String robotsTxtLastFailureReason;

    private Throwable robotsTxtLastFailure;

    private long lastAccessTime;

    private long backoffUntil;

    private int consecutiveFailures;

    private boolean crawlDelayCapLogged;

    /**
     * Creates an empty state: robots.txt not resolved, no delay, no backoff.
     */
    public HostState() {
        // empty
    }

    /**
     * Returns the origin of a URL: {@code scheme://host[:port]} with the scheme and the host lower-cased.
     * An explicit port is kept as written and a default port is not normalised.
     *
     * @param url the URL
     * @return the origin, or null if the URL has no host or is not hierarchical (file:, mailto:, malformed)
     */
    public static String toOrigin(final String url) {
        if (url == null) {
            return null;
        }
        final Matcher matcher = URL_PATTERN.matcher(url);
        if (!matcher.matches()) {
            return null;
        }
        String authority = matcher.group(2);
        final int at = authority.lastIndexOf('@');
        if (at >= 0) {
            authority = authority.substring(at + 1);
        }
        if (authority.isEmpty() || authority.charAt(0) == ':' || authority.indexOf(' ') >= 0) {
            return null;
        }
        return matcher.group(1).toLowerCase(Locale.ROOT) + "://" + authority.toLowerCase(Locale.ROOT);
    }

    /**
     * Returns the raw path and query of a URL, which is what robots.txt rules are matched against.
     *
     * @param url the URL
     * @return the path ("/" if empty) followed by "?" and the query when present, or null on a parse error
     */
    public static String toPathAndQuery(final String url) {
        if (url == null) {
            return null;
        }
        final Matcher matcher = URL_PATTERN.matcher(url);
        if (!matcher.matches()) {
            return null;
        }
        final String path = matcher.group(3);
        final String query = matcher.group(4);
        final String base = path.isEmpty() ? "/" : path;
        return query == null ? base : base + query;
    }

    /**
     * Returns the robots.txt status.
     * @return the status, or null if robots.txt has not been resolved yet
     */
    public synchronized RobotsTxtStatus getRobotsTxtStatus() {
        return robotsTxtStatus;
    }

    /**
     * Stores the resolved robots.txt. Any status other than {@link RobotsTxtStatus#UNAVAILABLE}
     * resets the count of failed attempts. {@link RobotsTxtStatus#PARSED} and {@link RobotsTxtStatus#ALLOW_ALL}
     * also clear the last failure.
     *
     * @param status the status
     * @param directive the directive that applies to this crawler, or null
     * @param crawlDelayMillis the Crawl-delay in milliseconds, 0 if none
     */
    public synchronized void setRobotsTxt(final RobotsTxtStatus status, final RobotsTxt.Directive directive, final long crawlDelayMillis) {
        this.robotsTxtStatus = status;
        this.directive = directive;
        this.crawlDelayMillis = crawlDelayMillis;
        if (status != RobotsTxtStatus.UNAVAILABLE) {
            robotsTxtFailureCount = 0;
        }
        if (status != RobotsTxtStatus.UNAVAILABLE && status != RobotsTxtStatus.DISALLOW_ALL) {
            robotsTxtLastFailureAttempts = 0;
            robotsTxtLastFailureReason = null;
            robotsTxtLastFailure = null;
        }
    }

    /**
     * Records the last failed attempt to retrieve robots.txt, so that the URLs dropped after the origin is given up
     * ({@link RobotsTxtStatus#DISALLOW_ALL}) can be reported with it.
     *
     * @param attempts the number of failed attempts so far
     * @param reason the failure, such as "HTTP 503" or the exception
     * @param cause the exception, or null for a failure status
     */
    public synchronized void setRobotsTxtLastFailure(final int attempts, final String reason, final Throwable cause) {
        this.robotsTxtLastFailureAttempts = attempts;
        this.robotsTxtLastFailureReason = reason;
        this.robotsTxtLastFailure = cause;
    }

    /**
     * Returns the number of failed attempts to retrieve robots.txt when the last failure was recorded.
     * @return the number of attempts, 0 if no failure is recorded
     */
    public synchronized int getRobotsTxtLastFailureAttempts() {
        return robotsTxtLastFailureAttempts;
    }

    /**
     * Returns the last failure to retrieve robots.txt, such as "HTTP 503" or the exception.
     * @return the failure, or null if none is recorded
     */
    public synchronized String getRobotsTxtLastFailureReason() {
        return robotsTxtLastFailureReason;
    }

    /**
     * Returns the exception of the last failure to retrieve robots.txt.
     * @return the exception, or null if none is recorded or the last failure was a status such as 503
     */
    public synchronized Throwable getRobotsTxtLastFailure() {
        return robotsTxtLastFailure;
    }

    /**
     * Counts one failed attempt to retrieve robots.txt.
     * @return the number of failed attempts so far
     */
    public synchronized int incrementAndGetRobotsTxtFailureCount() {
        return ++robotsTxtFailureCount;
    }

    /**
     * Checks a URL against the resolved robots.txt.
     *
     * @param url the URL
     * @return true if the URL may be crawled; always true while robots.txt is unresolved, allow-all or unavailable
     */
    public synchronized boolean isAllowedByRobotsTxt(final String url) {
        if (robotsTxtStatus == null || robotsTxtStatus == RobotsTxtStatus.ALLOW_ALL || robotsTxtStatus == RobotsTxtStatus.UNAVAILABLE) {
            return true;
        }
        if (robotsTxtStatus == RobotsTxtStatus.DISALLOW_ALL) {
            return false;
        }
        if (directive == null) {
            return true;
        }
        final String pathAndQuery = toPathAndQuery(url);
        return pathAndQuery == null || directive.allows(pathAndQuery);
    }

    /**
     * Returns the Crawl-delay from robots.txt.
     * @return the Crawl-delay in milliseconds, 0 if none
     */
    public synchronized long getCrawlDelayMillis() {
        return crawlDelayMillis;
    }

    /**
     * Returns the time of the last access to this origin.
     * @return the time in milliseconds, 0 if never accessed
     */
    public synchronized long getLastAccessTime() {
        return lastAccessTime;
    }

    /**
     * Sets the time of the last access to this origin.
     * @param lastAccessTime the time in milliseconds
     */
    public synchronized void setLastAccessTime(final long lastAccessTime) {
        this.lastAccessTime = lastAccessTime;
    }

    /**
     * Returns the time before which this origin must not be accessed because of failures.
     * @return the time in milliseconds, 0 if there is no backoff
     */
    public synchronized long getBackoffUntil() {
        return backoffUntil;
    }

    /**
     * Records a failure (such as 429 or 503) and extends the backoff.
     * The wait is {@code retryAfterMillis} when it is positive, otherwise
     * {@code baseMillis * 2^(consecutive failures - 1)}; either way it is capped at {@code maxMillis}.
     * The backoff end never moves earlier than it already is.
     *
     * @param now the current time in milliseconds
     * @param retryAfterMillis the wait requested by the server, or 0 or less if none
     * @param baseMillis the first exponential wait
     * @param maxMillis the upper limit of a wait
     * @return the wait in milliseconds
     */
    public synchronized long recordFailure(final long now, final long retryAfterMillis, final long baseMillis, final long maxMillis) {
        if (consecutiveFailures < Integer.MAX_VALUE) {
            consecutiveFailures++;
        }
        final long wait;
        if (retryAfterMillis > 0) {
            wait = Math.min(retryAfterMillis, maxMillis);
        } else {
            final int shift = consecutiveFailures - 1;
            wait = shift >= 62 || baseMillis > (maxMillis >> shift) ? maxMillis : Math.min(baseMillis << shift, maxMillis);
        }
        backoffUntil = Math.max(backoffUntil, now + wait);
        return wait;
    }

    /**
     * Records a success, which resets the exponential backoff. The current backoff end is not touched.
     */
    public synchronized void recordSuccess() {
        consecutiveFailures = 0;
    }

    /**
     * Marks that the over-limit Crawl-delay of this origin has been reported, so that it is logged only once.
     *
     * @return true the first time it is called, false afterwards
     */
    public synchronized boolean markCrawlDelayCapLogged() {
        if (crawlDelayCapLogged) {
            return false;
        }
        crawlDelayCapLogged = true;
        return true;
    }
}
