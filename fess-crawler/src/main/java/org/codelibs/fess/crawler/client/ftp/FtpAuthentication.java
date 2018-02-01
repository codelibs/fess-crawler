/*
 * Copyright 2012-2018 CodeLibs Project and the Others.
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

import java.net.URL;

import org.codelibs.core.lang.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author shinsuke
 *
 */
public class FtpAuthentication {
    private static final Logger logger = LoggerFactory
            .getLogger(FtpAuthentication.class);

    private String server;

    private int port;

    private String username;

    private String password;

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

    boolean matches(final String path) {
        if (StringUtil.isBlank(path)) {
            return false;
        }

        try {
            final int pos = path.indexOf('/', 6);
            final URL uri = new URL(pos == -1 ? path : path.substring(0, pos));
            if (!"ftp".equals(uri.getProtocol())) {
                return false;
            }
            if (StringUtil.isNotBlank(server) && !server.equals(uri.getHost())) {
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
            logger.debug("Invalid URI: " + path, e);
        }
        return false;
    }

}
