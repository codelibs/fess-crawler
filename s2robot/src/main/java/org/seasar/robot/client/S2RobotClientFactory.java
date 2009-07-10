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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.seasar.framework.util.StringUtil;
import org.seasar.robot.RobotSystemException;

/**
 * @author shinsuke
 *
 */
public class S2RobotClientFactory {
    protected Map<Pattern, S2RobotClient> clientMap = new HashMap<Pattern, S2RobotClient>();

    public void addClient(String regex, S2RobotClient client) {
        if (StringUtil.isBlank(regex)) {
            throw new RobotSystemException("A regular expression is null.");
        }
        if (client == null) {
            throw new RobotSystemException("S2RobotClient is null.");
        }
        clientMap.put(Pattern.compile(regex), client);
    }

    public void addClient(List<String> regexList, S2RobotClient client) {
        if (regexList == null || regexList.isEmpty()) {
            throw new RobotSystemException(
                    "A regular expression list is null or empty.");
        }
        if (client == null) {
            throw new RobotSystemException("S2RobotClient is null.");
        }
        for (String regex : regexList) {
            if (StringUtil.isNotBlank(regex)) {
                clientMap.put(Pattern.compile(regex), client);
            }
        }
    }

    public S2RobotClient getClient(String url) {
        if (StringUtil.isBlank(url)) {
            return null;
        }

        for (Map.Entry<Pattern, S2RobotClient> entry : clientMap.entrySet()) {
            Matcher matcher = entry.getKey().matcher(url);
            if (matcher.matches()) {
                return entry.getValue();
            }
        }
        return null;
    }
}
