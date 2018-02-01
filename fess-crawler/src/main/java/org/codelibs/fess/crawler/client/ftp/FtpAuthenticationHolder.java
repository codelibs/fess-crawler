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

import java.util.ArrayList;
import java.util.List;

/**
 * @author shinsuke
 *
 */
public class FtpAuthenticationHolder {
    List<FtpAuthentication> authList = new ArrayList<>();

    public void add(final FtpAuthentication auth) {
        authList.add(auth);
    }

    public FtpAuthentication get(final String path) {
        if (path == null) {
            return null;
        }

        for (final FtpAuthentication auth : authList) {
            if (auth.matches(path)) {
                return auth;
            }
        }
        return null;
    }
}
