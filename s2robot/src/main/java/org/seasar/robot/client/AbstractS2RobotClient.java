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
package org.seasar.robot.client;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author shinsuke
 * 
 */
public abstract class AbstractS2RobotClient implements S2RobotClient {

    private static final Logger logger = LoggerFactory
        .getLogger(AbstractS2RobotClient.class); // NOPMD

    private Map<String, Object> initParamMap;

    protected <T> T getInitParameter(final String key, final T defaultValue) {
        if (initParamMap != null) {
            try {
                final T value = (T) initParamMap.get(key);
                if (value != null) {
                    return value;
                }
            } catch (Exception e) {
                logger.warn("Could not load init parameters: " + key + " from "
                    + initParamMap, e);
            }
        }
        return defaultValue;
    }

    public void setInitParameterMap(final Map<String, Object> params) {
        this.initParamMap = params;
    }

}
