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
package org.codelibs.fess.crawler.client.ftp;

import java.util.ArrayList;
import java.util.List;

/**
 * Holds a list of FtpAuthentication objects and provides methods to add and retrieve them based on a given path.
 * This class is designed to manage FTP authentication details for different paths.
 */
public class FtpAuthenticationHolder {
    List<FtpAuthentication> authenticationList = new ArrayList<>();

    /**
     * Constructs a new FtpAuthenticationHolder.
     */
    public FtpAuthenticationHolder() {
        // Default constructor
    }

    /**
     * Adds an FtpAuthentication object to the holder.
     * @param auth The FtpAuthentication object to add.
     */
    public void add(final FtpAuthentication auth) {
        if (auth != null) {
            authenticationList.add(auth);
        }
    }

    /**
     * Retrieves an FtpAuthentication object that matches the given path.
     * @param path The path to match.
     * @return The matching FtpAuthentication object, or null if no match is found.
     */
    public FtpAuthentication get(final String path) {
        if (path == null) {
            return null;
        }

        for (final FtpAuthentication auth : authenticationList) {
            if (auth.matches(path)) {
                return auth;
            }
        }
        return null;
    }
}
