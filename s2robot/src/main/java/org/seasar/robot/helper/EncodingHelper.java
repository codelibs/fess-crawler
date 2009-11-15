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

/**
 * @author shinsuke
 *
 */
public class EncodingHelper {

    protected String defaultEncoding = null;

    protected Map<String, String> encodingMap = new HashMap<String, String>();

    public String normalize(String enc) {
        if (StringUtil.isBlank(enc)) {
            return defaultEncoding;
        } else {
            String newEnc = encodingMap.get(enc);
            if (StringUtil.isBlank(newEnc)) {
                return enc;
            } else {
                return newEnc;
            }
        }
    }
}
