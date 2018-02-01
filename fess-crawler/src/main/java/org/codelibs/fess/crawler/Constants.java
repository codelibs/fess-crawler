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
package org.codelibs.fess.crawler;

import java.nio.charset.Charset;

/**
 * @author shinsuke
 *
 */
public final class Constants {
    public static final String GET_METHOD = "GET";

    public static final String HEAD_METHOD = "HEAD";

    public static final String POST_METHOD = "POST";

    public static final int OK_STATUS = 0;

    public static final int NOT_MODIFIED_STATUS = 304;

    public static final int OK_STATUS_CODE = 200;

    public static final int NOT_MODIFIED_STATUS_CODE = 304;

    public static final int BAD_REQUEST_STATUS_CODE = 400;

    public static final int FORBIDDEN_STATUS_CODE = 403;

    public static final int NOT_FOUND_STATUS_CODE = 404;

    public static final int SERVER_ERROR_STATUS_CODE = 500;

    public static final String NO_TRANSFORMER = "NONE";

    public static final String UTF_8 = "UTF-8";

    public static final Charset UTF_8_CHARSET = Charset.forName(UTF_8);

    public static final Charset DEFAULT_CHARSET = Charset.defaultCharset();

    public static final String ISO_DATETIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    private Constants() {
    }

}
