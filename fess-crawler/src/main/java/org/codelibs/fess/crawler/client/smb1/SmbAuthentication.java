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

import org.codelibs.jcifs.smb1.NtlmPasswordAuthentication;

/**
 * Represents SMB1 authentication information, including server details,
 * credentials, and domain. This class is used to encapsulate the necessary
 * information for authenticating with an SMB1 server.
 *
 * <p>
 * It provides methods to set and retrieve the server address, port, username,
 * password, and domain. Additionally, it offers a method to construct a path
 * prefix for SMB1 URLs based on the configured server and port.
 * </p>
 *
 * <p>
 * The path prefix is in the format "smb1://server:port/", where the port is
 * included only if it's greater than 0. If the server is not set, the path
 * prefix will be "smb1://".
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
     * Returns the path prefix for SMB1 URLs.
     * @return The path prefix.
     */
    public String getPathPrefix() {
        final StringBuilder buf = new StringBuilder(100);
        buf.append("smb1://");
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
     * Returns the NTLM password authentication.
     * @return The NTLM password authentication.
     */
    public NtlmPasswordAuthentication getAuthentication() {
        return new NtlmPasswordAuthentication(domain == null ? "" : domain, username, password);
    }

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
     * Returns the domain.
     * @return The domain.
     */
    public String getDomain() {
        return domain;
    }

    /**
     * Sets the domain.
     * @param domain The domain to set.
     */
    public void setDomain(final String domain) {
        this.domain = domain;
    }
}
