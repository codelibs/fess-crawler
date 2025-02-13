/*
 * Copyright 2012-2024 CodeLibs Project and the Others.
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
package org.codelibs.fess.crawler.client.smb;

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
 */
public class SmbAuthenticationHolder {
    private final Map<String, SmbAuthentication> authMap = new HashMap<>();

    /**
     * Adds an SMB authentication configuration to the holder.
     *
     * @param auth the SMB authentication configuration to add
     */
    public void add(final SmbAuthentication auth) {
        authMap.put(auth.getPathPrefix(), auth);
    }

    /**
        * Retrieves the SmbAuthentication associated with the given path.
        *
        * <p>
        * The method iterates through the authentication map to find the entry
        * whose key is a prefix of the given path. If a matching entry is found,
        * the corresponding SmbAuthentication object is returned.
        * </p>
        *
        * @param path The path for which to retrieve the SmbAuthentication.
        *             If null, the method returns null.
        * @return The SmbAuthentication associated with the path, or null if no
        *         matching authentication is found or if the path is null.
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
