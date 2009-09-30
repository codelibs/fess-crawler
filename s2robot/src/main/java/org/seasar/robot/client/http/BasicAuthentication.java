/*
 * Copyright 2004-2009 the Seasar Foundation and the Others.
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
package org.seasar.robot.client.http;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.seasar.framework.util.StringUtil;
import org.seasar.robot.RobotSystemException;

/**
 * @author shinsuke
 *
 */
public class BasicAuthentication {
    private String host;

    private Integer port;

    private String realm;

    private String scheme;

    private String username;

    private String password;

    public BasicAuthentication(String host, Integer port, String realm,
            String scheme, String username, String password) {
        this.host = host;
        this.port = port;
        this.realm = realm;
        this.scheme = scheme;
        this.username = username;
        this.password = password;
    }

    public AuthScope getAuthScope() {
        if (StringUtil.isBlank(host)) {
            return AuthScope.ANY;
        }

        int p;
        if (port != null) {
            p = port.intValue();
        } else {
            p = AuthScope.ANY_PORT;
        }

        String r = realm;
        if (StringUtil.isBlank(r)) {
            r = AuthScope.ANY_REALM;
        }

        String s = scheme;
        if (StringUtil.isBlank(s)) {
            s = AuthScope.ANY_SCHEME;
        }

        return new AuthScope(host, p, r, s);
    }

    public Credentials getCredentials() {
        if (StringUtil.isEmpty(username)) {
            throw new RobotSystemException("username is empty.");
        }
        return new UsernamePasswordCredentials(username,
                password != null ? password : "");
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getRealm() {
        return realm;
    }

    public void setRealm(String realm) {
        this.realm = realm;
    }

    public String getScheme() {
        return scheme;
    }

    public void setScheme(String scheme) {
        this.scheme = scheme;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
