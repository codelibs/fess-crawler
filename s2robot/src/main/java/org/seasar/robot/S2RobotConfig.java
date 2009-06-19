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
package org.seasar.robot;

/**
 * @author shinsuke
 *
 */
public class S2RobotConfig {
    /** The number of a thread */
    protected int numOfThread = 10;

    protected int maxThreadCheckCount = 20;

    protected int threadCheckInterval = 500;

    /** a max depth for crawling. -1 is no depth check. */
    protected int maxDepth = -1;

    /** a max count to access urls. 0 is no limit to access it. */
    protected long maxAccessCount = 0;

    public int getNumOfThread() {
        return numOfThread;
    }

    public void setNumOfThread(int numOfThread) {
        this.numOfThread = numOfThread;
    }

    public int getMaxThreadCheckCount() {
        return maxThreadCheckCount;
    }

    public void setMaxThreadCheckCount(int maxThreadCheckCount) {
        this.maxThreadCheckCount = maxThreadCheckCount;
    }

    public int getThreadCheckInterval() {
        return threadCheckInterval;
    }

    public void setThreadCheckInterval(int threadCheckInterval) {
        this.threadCheckInterval = threadCheckInterval;
    }

    public int getMaxDepth() {
        return maxDepth;
    }

    public void setMaxDepth(int maxDepth) {
        this.maxDepth = maxDepth;
    }

    public long getMaxAccessCount() {
        return maxAccessCount;
    }

    public void setMaxAccessCount(long maxAccessCount) {
        this.maxAccessCount = maxAccessCount;
    }

}
