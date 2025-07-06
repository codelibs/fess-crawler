/*
 * Copyright 2012-2025 CodeLibs Project and the Others.
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
package org.codelibs.fess.crawler.client.smb1;

import java.util.HashMap;
import java.util.Map;

/**
 * Holds a map of SMB authentication configurations, allowing retrieval of the appropriate
 * authentication based on a given path.
 *
 * <p>This class manages a collection of {@link SmbAuthentication} objects, each associated
 * with a specific path prefix. When a path is provided, it iterates through the stored
 * authentications to find the one whose path prefix matches the beginning of the given path.
 * This allows for different SMB shares to use different authentication credentials.</p>
 *
 */
public class SmbAuthenticationHolder {
    private final Map<String, SmbAuthentication> authMap = new HashMap<>();

    /**
     * Creates a new SmbAuthenticationHolder instance.
     */
    public SmbAuthenticationHolder() {
        super();
    }

    /**
     * Adds an SMB authentication configuration to the holder.
     * @param auth The SMB authentication configuration to add.
     */
    public void add(final SmbAuthentication auth) {
        authMap.put(auth.getPathPrefix(), auth);
    }

    /**
     * Retrieves an SMB authentication configuration that matches the given path.
     * @param path The path to match.
     * @return The matching SmbAuthentication object, or null if no match is found.
     */
    public SmbAuthentication get(final String path) {
        if (path == null) {
            return null;
        }

        for (final Map.Entry<String, SmbAuthentication> entry : authMap.entrySet()) {
            if (path.startsWith(entry.getKey())) {
                return entry.getValue();
            }
        }
        return null;
    }
}
