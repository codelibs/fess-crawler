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

import org.seasar.robot.interval.IntervalController;

/**
 * @author shinsuke
 *
 */
public abstract class AbstractIntervalController implements IntervalController {

    /* (non-Javadoc)
     * @see org.seasar.robot.interval.IntervalController#delay(int)
     */
    public void delay(int type) {
        switch (type) {
        case PRE_PROCESSING:
            delayBeforeProcessing();
            break;
        case POST_PROCESSING:
            delayAfterProcessing();
            break;
        case NO_URL_IN_QUEUE:
            delayAtNoUrlInQueue();
            break;
        case WAIT_NEW_URL:
            delayForWaitingNewUrl();
            break;
        default:
            // NOP
            break;
        }
    }

    protected abstract void delayBeforeProcessing();

    protected abstract void delayAfterProcessing();

    protected abstract void delayAtNoUrlInQueue();

    protected abstract void delayForWaitingNewUrl();
}
