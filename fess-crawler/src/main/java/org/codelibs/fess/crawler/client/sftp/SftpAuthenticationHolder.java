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
package org.codelibs.fess.crawler.client.sftp;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Holder for SFTP authentication information.
 * This class manages multiple SFTP authentication credentials and matches them to server URLs.
 *
 * @author shinsuke
 */
public class SftpAuthenticationHolder {

    /** List of SFTP authentications. */
    protected List<SftpAuthentication> sftpAuthenticationList = new ArrayList<>();

    /**
     * Creates a new SftpAuthenticationHolder instance.
     */
    public SftpAuthenticationHolder() {
        // Default constructor
    }

    /**
     * Adds an SFTP authentication to the holder.
     *
     * @param sftpAuthentication The SFTP authentication to add.
     */
    public void add(final SftpAuthentication sftpAuthentication) {
        sftpAuthenticationList.add(sftpAuthentication);
    }

    /**
     * Gets the SFTP authentication that matches the given URL.
     *
     * @param url The URL to match.
     * @return The matching SFTP authentication, or null if no match is found.
     */
    public SftpAuthentication get(final String url) {
        if (url == null) {
            return null;
        }

        for (final SftpAuthentication sftpAuthentication : sftpAuthenticationList) {
            final Pattern pattern = sftpAuthentication.getServerPattern();
            if (pattern != null) {
                final Matcher matcher = pattern.matcher(url);
                if (matcher.matches()) {
                    return sftpAuthentication;
                }
            }
        }
        return null;
    }
}
