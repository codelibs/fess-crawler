package org.seasar.robot.client.http.digest;

/*
 * Copyright 2004-2010 the Seasar Foundation and the Others.
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

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.http.HeaderElement;
import org.apache.http.auth.MalformedChallengeException;
import org.apache.http.impl.auth.AuthSchemeBase;
import org.apache.http.message.BasicHeaderValueParser;
import org.apache.http.message.HeaderValueParser;
import org.apache.http.message.ParserCursor;
import org.apache.http.util.CharArrayBuffer;

/**
 * MT-safe RFC2617Scheme implementation.
 * 
 * @author shinsuke
 * 
 */
public abstract class RFC2617Scheme extends AuthSchemeBase {

    private ThreadLocal<Map<String, String>> paramsLocal =
        new ThreadLocal<Map<String, String>>();

    private Map<String, String> sharedParams = new HashMap<String, String>();

    public RFC2617Scheme() {
        super();
    }

    @Override
    protected void parseChallenge(final CharArrayBuffer buffer, int pos, int len)
            throws MalformedChallengeException {
        HeaderValueParser parser = BasicHeaderValueParser.DEFAULT;
        ParserCursor cursor = new ParserCursor(pos, buffer.length());
        HeaderElement[] elements = parser.parseElements(buffer, cursor);
        if (elements.length == 0) {
            throw new MalformedChallengeException(
                "Authentication challenge is empty");
        }

        Map<String, String> params = getParams(true, elements.length);
        for (HeaderElement element : elements) {
            params.put(element.getName(), element.getValue());
        }
    }

    /**
     * Returns authentication parameters map. Keys in the map are lower-cased.
     * 
     * @return the map of authentication parameters
     */
    protected Map<String, String> getParameters() {
        return getParams(false, 0);
    }

    /**
     * Returns authentication parameter with the given name, if available.
     * 
     * @param name
     *            The name of the parameter to be returned
     * 
     * @return the parameter with the given name
     */
    public String getParameter(final String name) {
        if (name == null) {
            throw new IllegalArgumentException("Parameter name may not be null");
        }
        return getParams(false, 0).get(name.toLowerCase(Locale.ENGLISH));
    }

    private Map<String, String> getParams(boolean recreate, int size) {
        Map<String, String> params;
        if (recreate) {
            paramsLocal.remove();
            params = new HashMap<String, String>(size + sharedParams.size());
            params.putAll(sharedParams);
            paramsLocal.set(params);
            return params;
        }

        params = paramsLocal.get();
        if (params == null) {
            params = new HashMap<String, String>();
            params.putAll(sharedParams);
            paramsLocal.set(params);
        }
        return params;
    }

    public String getRealm() {
        return getParameter("realm");
    }

    public void overrideParamter(final String name, final String value) {
        sharedParams.put(name, value);
    }

}
