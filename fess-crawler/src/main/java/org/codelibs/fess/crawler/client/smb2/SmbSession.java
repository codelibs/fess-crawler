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
package org.codelibs.fess.crawler.client.smb2;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.codelibs.fess.crawler.client.smb.SmbAuthentication;

import com.hierynomus.smbj.SMBClient;
import com.hierynomus.smbj.SmbConfig;
import com.hierynomus.smbj.auth.AuthenticationContext;
import com.hierynomus.smbj.connection.Connection;
import com.hierynomus.smbj.session.Session;

public class SmbSession {

    protected Connection connection;

    protected Session session;

    protected SmbAuthentication smbAuthentication;

    public SmbSession(final SmbAuthentication smbAuthentication) {
        this.smbAuthentication = smbAuthentication;
    }

    public AuthenticationContext getAuthenticationContext() {
        return new AuthenticationContext(smbAuthentication.getUsername(), smbAuthentication.getPassword().toCharArray(),
                smbAuthentication.getDomain() == null ? "" : smbAuthentication.getDomain());
    }

    public Session getSession() throws IOException {
        if (connection == null || !connection.isConnected()) {
            final SmbConfig config = SmbConfig.builder().withTimeout(60, TimeUnit.SECONDS).withSoTimeout(180, TimeUnit.SECONDS).build();
            final SMBClient client = new SMBClient(config);
            connection = client.connect(smbAuthentication.getServer());
            session = connection.authenticate(getAuthenticationContext());
        } else {
            if (session == null) {
                session = connection.authenticate(getAuthenticationContext());
            }
        }
        return session;
    }

    public void disconnect() {
        if (session != null) {
            try {
                session.close();
            } catch (final Exception e) {
                // ignore
            }
            session = null;
        }
        if (connection != null) {
            try {
                connection.close();
            } catch (final Exception e) {
                // ignore
            }
            connection = null;
        }
    }
}
