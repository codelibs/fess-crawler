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

import java.util.regex.Pattern;

import org.codelibs.core.lang.StringUtil;

/**
 * Authentication information for WebDAV connections.
 * This class holds credentials for WebDAV authentication.
 *
 * @author shinsuke
 */
public class WebDavAuthentication {

    /** The server URL pattern for matching. */
    protected Pattern serverPattern;

    /** The username for authentication. */
    protected String username;

    /** The password for authentication. */
    protected String password;

    /**
     * Creates a new WebDavAuthentication instance.
     */
    public WebDavAuthentication() {
        // Default constructor
    }

    /**
     * Gets the server pattern.
     *
     * @return The server pattern.
     */
    public Pattern getServerPattern() {
        return serverPattern;
    }

    /**
     * Sets the server pattern.
     *
     * @param serverPattern The server pattern to set.
     */
    public void setServerPattern(final Pattern serverPattern) {
        this.serverPattern = serverPattern;
    }

    /**
     * Sets the server pattern from a string.
     *
     * @param serverPattern The server pattern string.
     */
    public void setServer(final String serverPattern) {
        if (StringUtil.isNotBlank(serverPattern)) {
            this.serverPattern = Pattern.compile(serverPattern);
        }
    }

    /**
     * Gets the username.
     *
     * @return The username.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username.
     *
     * @param username The username to set.
     */
    public void setUsername(final String username) {
        this.username = username;
    }

    /**
     * Gets the password.
     *
     * @return The password.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the password.
     *
     * @param password The password to set.
     */
    public void setPassword(final String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "WebDavAuthentication [serverPattern=" + serverPattern + ", username=" + username + "]";
    }
}
