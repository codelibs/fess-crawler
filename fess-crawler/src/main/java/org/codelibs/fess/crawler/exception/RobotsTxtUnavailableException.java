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
package org.codelibs.fess.crawler.exception;

/**
 * Thrown when the robots.txt of an origin could not be retrieved (429, 5xx or a network error)
 * and the URL should be retried later instead of being crawled or dropped.
 *
 * <p>When this is thrown by
 * {@link org.codelibs.fess.crawler.helper.RobotsTxtHelper#checkRobotsTxt(org.codelibs.fess.crawler.CrawlerContext, String, String,
 * org.codelibs.fess.crawler.helper.RobotsTxtFetcher, org.codelibs.fess.crawler.helper.RobotsTxtPolicy)}, the backoff of the
 * origin has already been recorded on its {@link org.codelibs.fess.crawler.entity.HostState} (including Retry-After), or the
 * exception was raised because that backoff has not ended yet, or because the fetch was interrupted. Callers must not call
 * {@link org.codelibs.fess.crawler.entity.HostState#recordFailure(long, long, long, long)} again for it; they only re-queue the URL.
 * {@link #getRetryAfterMillis()} is informational.</p>
 *
 * <p>{@link #isFetchAttempted()} tells whether robots.txt was requested for this URL. When it was not, because the backoff of
 * the origin has not ended yet, the URL has not been tried and the re-queue does not count as one of its retries.</p>
 */
public class RobotsTxtUnavailableException extends CrawlingAccessException {

    private static final long serialVersionUID = 1L;

    /** The wait requested by the server in milliseconds, 0 or less if none. */
    private final long retryAfterMillis;

    /** Whether robots.txt was requested for the URL. */
    private final boolean fetchAttempted;

    /**
     * Creates a new RobotsTxtUnavailableException for a failed or interrupted robots.txt request.
     *
     * @param url the URL whose robots.txt could not be retrieved
     * @param retryAfterMillis the wait requested by the server (Retry-After) in milliseconds, 0 or less if none
     * @param cause the cause, or null
     */
    public RobotsTxtUnavailableException(final String url, final long retryAfterMillis, final Throwable cause) {
        this(url, retryAfterMillis, cause, true);
    }

    /**
     * Creates a new RobotsTxtUnavailableException.
     *
     * @param url the URL whose robots.txt could not be retrieved
     * @param retryAfterMillis the wait requested by the server (Retry-After) in milliseconds, 0 or less if none
     * @param cause the cause, or null
     * @param fetchAttempted true if robots.txt was requested; false if it was not because the backoff of the origin has not ended
     */
    public RobotsTxtUnavailableException(final String url, final long retryAfterMillis, final Throwable cause,
            final boolean fetchAttempted) {
        super("robots.txt is unavailable for " + url, cause);
        this.retryAfterMillis = retryAfterMillis;
        this.fetchAttempted = fetchAttempted;
    }

    /**
     * Returns the wait requested by the server.
     *
     * @return the wait in milliseconds, 0 or less if none
     */
    public long getRetryAfterMillis() {
        return retryAfterMillis;
    }

    /**
     * Returns whether robots.txt was requested for the URL.
     *
     * @return true if the request failed or was interrupted; false if no request was made because the backoff of the origin
     *         has not ended, in which case the URL has not been tried
     */
    public boolean isFetchAttempted() {
        return fetchAttempted;
    }

}
