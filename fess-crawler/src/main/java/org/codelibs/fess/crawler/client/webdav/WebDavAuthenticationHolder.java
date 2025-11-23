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
package org.codelibs.fess.crawler.client.webdav;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Holder for WebDAV authentication information.
 * This class manages multiple WebDAV authentication credentials and matches them to server URLs.
 *
 * @author shinsuke
 */
public class WebDavAuthenticationHolder {

    /** List of WebDAV authentications. */
    protected List<WebDavAuthentication> webDavAuthenticationList = new ArrayList<>();

    /**
     * Creates a new WebDavAuthenticationHolder instance.
     */
    public WebDavAuthenticationHolder() {
        // Default constructor
    }

    /**
     * Adds a WebDAV authentication to the holder.
     *
     * @param webDavAuthentication The WebDAV authentication to add.
     */
    public void add(final WebDavAuthentication webDavAuthentication) {
        webDavAuthenticationList.add(webDavAuthentication);
    }

    /**
     * Gets the WebDAV authentication that matches the given URL.
     *
     * @param url The URL to match.
     * @return The matching WebDAV authentication, or null if no match is found.
     */
    public WebDavAuthentication get(final String url) {
        if (url == null) {
            return null;
        }

        for (final WebDavAuthentication webDavAuthentication : webDavAuthenticationList) {
            final Pattern pattern = webDavAuthentication.getServerPattern();
            if (pattern != null) {
                final Matcher matcher = pattern.matcher(url);
                if (matcher.matches()) {
                    return webDavAuthentication;
                }
            }
        }
        return null;
    }
}
