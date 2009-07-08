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
package org.seasar.robot.interval.impl;

import java.net.URL;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

import org.seasar.framework.util.StringUtil;
import org.seasar.robot.RobotSystemException;
import org.seasar.robot.entity.UrlQueue;
import org.seasar.robot.util.CrawlingParameterUtil;

public class HostIntervalController extends DefaultIntervalController {

    private final ConcurrentMap<String, AtomicLong> lastTimes = new ConcurrentHashMap<String, AtomicLong>();

    /* (non-Javadoc)
     * @see org.seasar.robot.interval.impl.AbstractIntervalController#delayBeforeProcessing()
     */
    @Override
    protected void delayBeforeProcessing() {
        UrlQueue urlQueue = CrawlingParameterUtil.getUrlQueue();
        if (urlQueue == null) {
            return;
        }

        String url = urlQueue.getUrl();
        if (StringUtil.isBlank(url) || url.startsWith("file:")) {
            // not target
            return;
        }

        try {
            URL u = new URL(url);
            String host = u.getHost();
            if (host == null)
                return;
            AtomicLong lastTime = lastTimes.putIfAbsent(host, new AtomicLong(
                    System.currentTimeMillis()));
            if (lastTime == null)
                return;
            synchronized (lastTime) {
                while (true) {
                    long currentTime = System.currentTimeMillis();
                    long delayTime = lastTime.get()
                            + delayMillisBeforeProcessing - currentTime;
                    if (delayTime <= 0) {
                        lastTime.set(currentTime);
                        break;
                    }
                    lastTime.wait(delayTime);
                }
            }
        } catch (Exception e) {
            throw new RobotSystemException(e);
        }
    }

}
