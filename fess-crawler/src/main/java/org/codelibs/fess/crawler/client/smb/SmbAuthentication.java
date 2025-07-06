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
package org.codelibs.fess.crawler.client.smb;

/**
 * Represents SMB authentication information, including server details,
 * credentials, and domain. This class is used to encapsulate the necessary
 * information for authenticating with an SMB server.
 *
 * <p>
 * It provides methods to set and retrieve the server address, port, username,
 * password, and domain. Additionally, it offers a method to construct a path
 * prefix for SMB URLs based on the configured server and port.
 * </p>
 *
 * <p>
 * The path prefix is in the format "smb://server:port/", where the port is
 * included only if it's greater than 0. If the server is not set, the path
 * prefix will be "smb://".
 * </p>
 */
public class SmbAuthentication {
    private String server;

    private int port;

    private String username;

    private String password;

    private String domain;

    /**
     * Creates a new SmbAuthentication instance.
     */
    public SmbAuthentication() {
        super();
    }

    /**
     * Returns the path prefix for SMB URLs.
     * @return The path prefix.
     */
    public String getPathPrefix() {
        final StringBuilder buf = new StringBuilder(100);
        buf.append("smb://");
        if (server != null) {
            buf.append(server);
            if (port > 0) {
                buf.append(':');
                buf.append(port);
            }
            buf.append('/');
        }
        return buf.toString();
    }

    /**
     * Returns the SMB server address.
     * @return the server address
     */
    public String getServer() {
        return server;
    }

    /**
     * Sets the SMB server address.
     * @param server the server address to set
     */
    public void setServer(final String server) {
        this.server = server;
    }

    /**
     * Returns the SMB server port.
     * @return the server port
     */
    public int getPort() {
        return port;
    }

    /**
     * Sets the SMB server port.
     * @param port the server port to set
     */
    public void setPort(final int port) {
        this.port = port;
    }

    /**
     * Returns the username for SMB authentication.
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username for SMB authentication.
     * @param username the username to set
     */
    public void setUsername(final String username) {
        this.username = username;
    }

    /**
     * Returns the password for SMB authentication.
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the password for SMB authentication.
     * @param password the password to set
     */
    public void setPassword(final String password) {
        this.password = password;
    }

    /**
     * Returns the domain for SMB authentication.
     * @return the domain
     */
    public String getDomain() {
        return domain;
    }

    /**
     * Sets the domain for SMB authentication.
     * @param domain the domain to set
     */
    public void setDomain(final String domain) {
        this.domain = domain;
    }

    /**
     * Returns a string representation of this object.
     * @return A string representation.
     */
    @Override
    public String toString() {
        return "[" + domain + "] " + username + "@" + server + ":" + port;
    }
}
