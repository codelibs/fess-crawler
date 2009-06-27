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
package org.seasar.robot.extractor;

import java.util.HashMap;
import java.util.Map;

import org.seasar.framework.util.StringUtil;
import org.seasar.robot.RobotSystemException;

/**
 * @author shinsuke
 *
 */
public class ExtractorFactory {
    protected Map<String, Extractor> extractorMap = new HashMap<String, Extractor>();

    public void addExtractor(String key, Extractor extractor) {
        if (StringUtil.isBlank(key)) {
            throw new RobotSystemException("The key is null.");
        }
        if (extractor == null) {
            throw new RobotSystemException("The extractor is null.");
        }
        extractorMap.put(key, extractor);
    }

    public Extractor getExtractor(String key) {
        return extractorMap.get(key);
    }
}
