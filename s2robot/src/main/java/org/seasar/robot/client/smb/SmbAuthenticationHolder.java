/*
 * Copyright 2004-2010 the Seasar Foundation and the Others.
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
package org.seasar.robot.client.smb;

import java.util.HashMap;
import java.util.Map;

/**
 * @author shinsuke
 * 
 */
public class SmbAuthenticationHolder {
    private Map<String, SmbAuthentication> authMap =
        new HashMap<String, SmbAuthentication>();

    public void add(SmbAuthentication auth) {
        authMap.put(auth.getPathPrefix(), auth);
    }

    public SmbAuthentication get(String path) {
        if (path == null) {
            return null;
        }

        for (Map.Entry<String, SmbAuthentication> entry : authMap.entrySet()) {
            if (path.startsWith(entry.getKey())) {
                return entry.getValue();
            }
        }
        return null;
    }
}
