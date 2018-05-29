/*
 * Copyright 2012-2018 CodeLibs Project and the Others.
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
package org.codelibs.fess.crawler.client.smb2;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.codelibs.fess.crawler.client.smb.SmbAuthentication;

/**
 * @author shinsuke
 * @author kaorufuzita
 *
 */
public class SmbSessionHolder implements Iterable<SmbSession> {
    private final Map<String, SmbSession> sessionMap = new HashMap<>();

    @Override
    public Iterator<SmbSession> iterator() {
        return sessionMap.values().iterator();
    }

    public void add(final SmbAuthentication smbAuthentication) {
        sessionMap.put(smbAuthentication.getPathPrefix(), new SmbSession(smbAuthentication));
    }

    public SmbSession get(final String path) {
        if (path == null) {
            return null;
        }

        for (final Map.Entry<String, SmbSession> entry : sessionMap
                .entrySet()) {
            if (path.startsWith(entry.getKey())) {
                return entry.getValue();
            }
        }
        return null;
    }
}
