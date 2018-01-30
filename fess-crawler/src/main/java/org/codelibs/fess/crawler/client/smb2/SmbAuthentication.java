/*
 * Copyright 2012-2017 CodeLibs Project and the Others.
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

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import com.hierynomus.smbj.SMBClient;
import com.hierynomus.smbj.SmbConfig;
import com.hierynomus.smbj.auth.AuthenticationContext;
import com.hierynomus.smbj.connection.Connection;
import com.hierynomus.smbj.session.Session;

/**
 * @author shinsuke
 * @author kaorufuzita
 *
 */
public class SmbAuthentication {
    private String server;

    private int port;

    private String username;

    private String password;

    private String domain;

    private Connection connection;

    private Session session;

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

    public AuthenticationContext getAuthentication() {
        return new AuthenticationContext(username, password.toCharArray(), domain == null ? "" : domain);
    }

    public Session getSession() throws IOException {
        if (connection == null || !connection.isConnected()) {
            final SmbConfig config = SmbConfig.builder()
                    .withTimeout(60, TimeUnit.SECONDS)
                    .withSoTimeout(180, TimeUnit.SECONDS)
                    .build();
            final SMBClient client = new SMBClient(config);
            connection = client.connect(getServer());
            session = connection.authenticate(getAuthentication());
        } else {
            if (session == null) {
                session = connection.authenticate(getAuthentication());
            }
        }
        return session;
    }

    public void disconnect() {
        if (session != null) {
            try {
                session.close();
            } catch (Exception e) {
            }
            session = null;
        }
        if (connection != null) {
            try {
                connection.close();
            } catch (Exception e) {
            }
            connection = null;
        }
    }

    public String getServer() {
        return server;
    }

    public void setServer(final String server) {
        this.server = server;
    }

    public int getPort() {
        return port;
    }

    public void setPort(final int port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(final String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(final String domain) {
        this.domain = domain;
    }
}
