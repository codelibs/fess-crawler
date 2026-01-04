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

import java.net.URI;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;

/**
 * FtpAuthentication class holds the authentication information for FTP connections.
 * It includes server address, port number, username, and password.
 * It also provides a method to check if the authentication matches a given FTP path.
 *
 * <p><b>Security Note:</b> This class stores credentials in memory. For security best practices:
 * <ul>
 *   <li>Call {@link #clearCredentials()} after authentication is complete to clear sensitive data from memory</li>
 *   <li>Avoid logging instances of this class as it may expose credentials</li>
 *   <li>Consider using external secret management systems for credential storage in production</li>
 * </ul>
 *
 * @author shinsuke
 */
public class FtpAuthentication {
    private static final Logger logger = LogManager.getLogger(FtpAuthentication.class);

    /**
     * Constructs a new FtpAuthentication.
     */
    public FtpAuthentication() {
        // Default constructor
    }

    private String server;

    private int port;

    private String username;

    private String password;

    /**
     * Returns the server address.
     * @return The server address.
     */
    public String getServer() {
        return server;
    }

    /**
     * Sets the server address.
     * @param server The server address.
     */
    public void setServer(final String server) {
        this.server = server;
    }

    /**
     * Returns the port number.
     * @return The port number.
     */
    public int getPort() {
        return port;
    }

    /**
     * Sets the port number.
     * @param port The port number.
     */
    public void setPort(final int port) {
        this.port = port;
    }

    /**
     * Returns the username.
     * @return The username.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username.
     * @param username The username.
     */
    public void setUsername(final String username) {
        this.username = username;
    }

    /**
     * Returns the password.
     * @return The password.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the password.
     * @param password The password.
     */
    public void setPassword(final String password) {
        this.password = password;
    }

    /**
     * Clears the credentials from memory for security purposes.
     * This method should be called after the authentication is no longer needed.
     */
    public void clearCredentials() {
        this.password = null;
        this.username = null;
    }

    /**
     * Returns a string representation of this object.
     * Note: Password is intentionally excluded from the output for security reasons.
     * @return A string representation without sensitive data.
     */
    @Override
    public String toString() {
        return "FtpAuthentication[server=" + server + ", port=" + port + ", username=" + username + "]";
    }

    /**
     * Checks if this authentication matches the given FTP path.
     * @param path The FTP path to check.
     * @return true if it matches, false otherwise.
     */
    boolean matches(final String path) {
        if (StringUtil.isBlank(path)) {
            return false;
        }

        try {
            final int pos = path.indexOf('/', 6);
            final URI uri = new URI(pos == -1 ? path : path.substring(0, pos));
            if (!"ftp".equals(uri.getScheme()) || (StringUtil.isNotBlank(server) && !server.equals(uri.getHost()))) {
                return false;
            }
            int p = uri.getPort();
            if (p == -1) {
                p = 21;
            }
            if (port > 0 && port != p) {
                return false;
            }
            return true;
        } catch (final Exception e) {
            // Log the exception at debug level because an invalid URI is not critical and can be ignored.
            logger.debug("Invalid URI: {}", path, e);
        }
        return false;
    }

}
