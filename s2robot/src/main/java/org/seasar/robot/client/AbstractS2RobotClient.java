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

/**
 * @author shinsuke
 *
 */
public abstract class AbstractS2RobotClient implements S2RobotClient {

    private Map<String, Object> initParamMap;

    protected <T> T getInitParameter(String key, T defaultValue) {
        if (initParamMap != null) {
            T value = (T) initParamMap.get(key);
            if (value != null) {
                return value;
            }
        }
        return defaultValue;
    }

    public void setInitParameterMap(Map<String, Object> params) {
        this.initParamMap = params;
    }

}