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
package org.codelibs.fess.crawler.interval.impl;

import java.net.URI;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

import org.codelibs.core.exception.InterruptedRuntimeException;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.core.lang.SystemUtil;
import org.codelibs.fess.crawler.entity.UrlQueue;
import org.codelibs.fess.crawler.exception.CrawlerSystemException;
import org.codelibs.fess.crawler.util.CrawlingParameterUtil;

/**
 * HostIntervalController is an implementation of {@link org.codelibs.fess.crawler.interval.IntervalController}
 * that controls the interval between requests to the same host.
 * It uses a ConcurrentMap to store the last access time for each host.
 * The delayBeforeProcessing method is overridden to introduce a delay before processing a URL,
 * ensuring that requests to the same host are not made too frequently.
 * The delay is calculated based on the configured delayMillisBeforeProcessing parameter.
 * If the time since the last request to the host is less than the configured delay,
 * the thread waits until the delay has elapsed.
 * This class is thread-safe.
 */
public class HostIntervalController extends DefaultIntervalController {

    private final ConcurrentMap<String, AtomicLong> lastTimes = new ConcurrentHashMap<>();

    public HostIntervalController() {
    }

    public HostIntervalController(final Map<String, Long> params) {
        super(params);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.codelibs.fess.crawler.interval.impl.AbstractIntervalController#
     * delayBeforeProcessing()
     */
    @Override
    protected void delayBeforeProcessing() {
        final UrlQueue<?> urlQueue = CrawlingParameterUtil.getUrlQueue();
        if (urlQueue == null) {
            return;
        }

        final String url = urlQueue.getUrl();
        if (StringUtil.isBlank(url) || url.startsWith("file:")) {
            // not target
            return;
        }

        try {
            final URI uri = new URI(url);
            final String host = uri.getHost();
            if (host == null) {
                return;
            }
            final AtomicLong lastTime = lastTimes.putIfAbsent(host, new AtomicLong(SystemUtil.currentTimeMillis()));
            if (lastTime == null) {
                return;
            }
            synchronized (lastTime) {
                while (true) {
                    final long currentTime = SystemUtil.currentTimeMillis();
                    final long delayTime = lastTime.get() + delayMillisBeforeProcessing - currentTime;
                    if (delayTime <= 0) {
                        lastTime.set(currentTime);
                        break;
                    }
                    lastTime.wait(delayTime);
                }
            }
        } catch (final InterruptedException e) {
            throw new InterruptedRuntimeException(e);
        } catch (final Exception e) {
            throw new CrawlerSystemException(e);
        }
    }

}
