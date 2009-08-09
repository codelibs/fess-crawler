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
package org.seasar.robot.helper;

import java.util.HashMap;
import java.util.Map;

import org.seasar.framework.util.StringUtil;
import org.seasar.robot.RobotSystemException;

/**
 * @author shinsuke
 *
 */
public class ContentLengthHelper {

    protected long defaultMaxLength = 10L * 1024L * 1024L;//10M

    protected Map<String, Long> maxLengthMap = new HashMap<String, Long>();

    public void addMaxLength(String mimeType, long maxLength) {
        if (StringUtil.isBlank(mimeType)) {
            throw new RobotSystemException("MIME type is a blank.");
        }
        if (maxLength < 0) {
            throw new RobotSystemException("The value of maxLength is invalid.");
        }
        maxLengthMap.put(mimeType, maxLength);
    }

    public long getMaxLength(String mimeType) {
        if (StringUtil.isBlank(mimeType)) {
            return defaultMaxLength;
        }
        Long maxLength = maxLengthMap.get(mimeType);
        if (maxLength != null && maxLength >= 0L) {
            return maxLength;
        }
        return defaultMaxLength;
    }
}
