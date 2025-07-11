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
package org.codelibs.fess.crawler.client.http.form;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScheme;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.MalformedChallengeException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.message.BasicNameValuePair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.exception.IORuntimeException;
import org.codelibs.core.io.InputStreamUtil;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.core.misc.Pair;
import org.codelibs.core.stream.StreamUtil;
import org.codelibs.fess.crawler.Constants;

/**
 * The FormScheme class implements the AuthScheme interface to provide
 * form-based authentication for HTTP clients. It handles the process of
 * obtaining a token and logging in using the provided credentials.
 *
 * <p>This class supports both GET and POST methods for token and login
 * requests. It also allows for the replacement of placeholders in URLs and
 * parameters with actual credentials.
 *
 * <p>Usage example:
 * <pre>
 * {@code
 * Map<String, String> params = new HashMap<>();
 * params.put("token_url", "http://example.com/token");
 * params.put("login_url", "http://example.com/login");
 * FormScheme formScheme = new FormScheme(params);
 * formScheme.authenticate(credentials, executor);
 * }
 * </pre>
 *
 * <p>Parameters:
 * <ul>
 * <li>ENCODING - The character encoding to use for request parameters.</li>
 * <li>TOKEN_URL - The URL to request the token from.</li>
 * <li>TOKEN_PATTERN - The regex pattern to extract the token from the response.</li>
 * <li>TOKEN_NAME - The name of the token parameter.</li>
 * <li>TOKEN_METHOD - The HTTP method to use for the token request (GET or POST).</li>
 * <li>TOKEN_PARAMTERS - The parameters to include in the token request.</li>
 * <li>LOGIN_METHOD - The HTTP method to use for the login request (GET or POST).</li>
 * <li>LOGIN_URL - The URL to send the login request to.</li>
 * <li>LOGIN_PARAMETERS - The parameters to include in the login request.</li>
 * </ul>
 *
 */
public class FormScheme implements AuthScheme {

    private static final Logger logger = LogManager.getLogger(FormScheme.class);

    private static final String ENCODING = "encoding";

    private static final String TOKEN_URL = "token_url";

    private static final String TOKEN_PATTERN = "token_pattern";

    private static final String TOKEN_NAME = "token_name";

    private static final String TOKEN_METHOD = "token_method";

    private static final String TOKEN_PARAMTERS = "token_paramters";

    private static final String LOGIN_METHOD = "login_method";

    private static final String LOGIN_URL = "login_url";

    private static final String LOGIN_PARAMETERS = "login_parameters";

    private static final String PASSWORD = "${password}";

    private static final String USERNAME = "${username}";

    private final Map<String, String> parameterMap;

    /**
     * Constructs a FormScheme with the given parameter map.
     * @param parameterMap The map of parameters.
     */
    public FormScheme(final Map<String, String> parameterMap) {
        this.parameterMap = parameterMap;
    }

    @Override
    public void processChallenge(final Header header) throws MalformedChallengeException {
        // no-op
    }

    @Override
    public String getSchemeName() {
        return "form";
    }

    @Override
    public String getParameter(final String name) {
        return parameterMap.get(name);
    }

    @Override
    public String getRealm() {
        return null;
    }

    @Override
    public boolean isConnectionBased() {
        return false;
    }

    @Override
    public boolean isComplete() {
        return false;
    }

    @Override
    public Header authenticate(final Credentials credentials, final HttpRequest request) throws AuthenticationException {
        return null;
    }

    /**
     * Authenticates using the form scheme.
     * @param credentials The credentials.
     * @param executor The executor for HTTP requests.
     */
    public void authenticate(final Credentials credentials,
            final BiConsumer<HttpUriRequest, BiConsumer<HttpResponse, HttpEntity>> executor) {
        final String tokenUrl = getParameter(TOKEN_URL);
        final String tokenPattern = getParameter(TOKEN_PATTERN);
        final List<Pair<String, String>> responseParams = new ArrayList<>();
        final String encoding = getParameter(ENCODING);
        if (StringUtil.isNotBlank(tokenUrl) && StringUtil.isNotBlank(tokenPattern)) {
            final String tokenHttpMethod = getParameter(TOKEN_METHOD);
            final String tokenReqParams = getParameter(TOKEN_PARAMTERS);

            final HttpUriRequest httpRequest;
            if (Constants.POST_METHOD.equalsIgnoreCase(tokenHttpMethod)) {
                final HttpPost httpPost = new HttpPost(tokenUrl);
                if (StringUtil.isNotBlank(tokenReqParams)) {
                    final HttpEntity httpEntity = parseRequestParameters(tokenReqParams, null, encoding);
                    httpPost.setEntity(httpEntity);
                }
                httpRequest = httpPost;
            } else {
                final StringBuilder buf = new StringBuilder(100);
                buf.append(tokenUrl);
                if (StringUtil.isNotBlank(tokenReqParams)) {
                    if (tokenUrl.indexOf('?') >= 0) {
                        buf.append('&');
                    } else {
                        buf.append('?');
                    }
                    buf.append(tokenReqParams);
                }
                httpRequest = new HttpGet(buf.toString());
            }

            executor.accept(httpRequest, (response, entity) -> {
                final int httpStatusCode = response.getStatusLine().getStatusCode();
                if (httpStatusCode < 400 || httpStatusCode == 401) {
                    parseTokenPage(tokenPattern, responseParams, entity);
                } else {
                    String content;
                    try {
                        content = new String(InputStreamUtil.getBytes(entity.getContent()), Constants.UTF_8_CHARSET);
                    } catch (final IOException e) {
                        content = e.getMessage();
                    }
                    logger.warn("Failed to access to {}. The http status is {}.\n{}", tokenUrl, httpStatusCode, content);
                }
            });
        }

        final String loginHttpMethod = getParameter(LOGIN_METHOD);
        final String originalLoginUrl = getParameter(LOGIN_URL);
        final String loginUrl = replaceCredentials(credentials, originalLoginUrl);
        final String loginReqParams = replaceCredentials(credentials, getParameter(LOGIN_PARAMETERS));

        if (StringUtil.isBlank(loginUrl)) {
            if (logger.isDebugEnabled()) {
                logger.debug("No login.url");
            }
            return;
        }

        final HttpUriRequest httpRequest;
        if (Constants.POST_METHOD.equalsIgnoreCase(loginHttpMethod)) {
            final HttpPost httpPost = new HttpPost(loginUrl);
            if (loginReqParams.length() > 0) {
                httpPost.setEntity(parseRequestParameters(loginReqParams.toString(), responseParams, encoding));
            }
            httpRequest = httpPost;
        } else {
            final StringBuilder buf = new StringBuilder(100);
            buf.append(loginUrl);
            if (loginReqParams.length() > 0) {
                if (loginUrl.indexOf('?') >= 0) {
                    buf.append('&');
                } else {
                    buf.append('?');
                }
                buf.append(loginReqParams);
                if (!responseParams.isEmpty()) {
                    buf.append('&');
                    responseParams.stream().forEach(p -> {
                        try {
                            buf.append(URLEncoder.encode(p.getFirst(), encoding));
                            buf.append('=');
                            buf.append(URLEncoder.encode(p.getSecond(), encoding));
                        } catch (final UnsupportedEncodingException e) {
                            throw new IORuntimeException(e);
                        }
                    });
                }
            }
            httpRequest = new HttpGet(buf.toString());
        }

        executor.accept(httpRequest, (response, entity) -> {
            final int httpStatusCode = response.getStatusLine().getStatusCode();
            if (httpStatusCode >= 400) {
                String content;
                try {
                    content = new String(InputStreamUtil.getBytes(entity.getContent()), Constants.UTF_8_CHARSET);
                } catch (final IOException e) {
                    content = e.getMessage();
                }
                logger.warn("Failed to login on {}. The http status is {}.\n{}", originalLoginUrl, httpStatusCode, content);
            } else if (logger.isDebugEnabled()) {
                logger.debug("Logged in {}", originalLoginUrl);
            }
        });

    }

    /**
     * Parses the token page and extracts token information.
     * @param tokenPattern The regex pattern to extract the token.
     * @param responseParams The list to store response parameters.
     * @param entity The HTTP entity containing the token page content.
     */
    protected void parseTokenPage(final String tokenPattern, final List<Pair<String, String>> responseParams, final HttpEntity entity) {
        try {
            final String tokenName = getParameter(TOKEN_NAME);
            final String content = new String(InputStreamUtil.getBytes(entity.getContent()), Constants.UTF_8_CHARSET);
            final String tokenValue = getTokenValue(tokenPattern, content);
            if (StringUtil.isNotBlank(tokenValue)) {
                responseParams.add(new Pair<>(tokenName, tokenValue));
                if (logger.isDebugEnabled()) {
                    logger.debug("Token: {}", tokenValue);
                }
            } else if (logger.isDebugEnabled()) {
                logger.debug("Token is not found.\n{}", content);
            }
        } catch (final IOException e) {
            throw new IORuntimeException(e);
        }
    }

    /**
     * Parses request parameters from a string.
     * @param params The parameter string.
     * @param paramList Additional parameters.
     * @param encoding The encoding.
     * @return The HttpEntity containing the parsed parameters.
     */
    protected HttpEntity parseRequestParameters(final String params, final List<Pair<String, String>> paramList, final String encoding) {
        try {
            final List<BasicNameValuePair> parameters =
                    StreamUtil.split(params, "&").get(stream -> stream.filter(StringUtil::isNotBlank).map(s -> {
                        final String name;
                        final String value;
                        final int pos = s.indexOf('=');
                        if (pos == -1 || pos == s.length() - 1) {
                            name = s;
                            value = StringUtil.EMPTY;
                        } else {
                            name = s.substring(0, pos);
                            value = s.substring(pos + 1);
                        }
                        return new BasicNameValuePair(name, value);
                    }).collect(Collectors.toList()));
            if (paramList != null) {
                parameters.addAll(
                        paramList.stream().map(p -> new BasicNameValuePair(p.getFirst(), p.getSecond())).collect(Collectors.toList()));
            }
            return new UrlEncodedFormEntity(parameters, encoding);
        } catch (final UnsupportedEncodingException e) {
            throw new IORuntimeException(e);
        }
    }

    /**
     * Extracts the token value from the content using the given pattern.
     * @param tokenPattern The regex pattern.
     * @param content The content to search.
     * @return The extracted token value.
     */
    protected String getTokenValue(final String tokenPattern, final String content) {
        final Matcher matcher = Pattern.compile(tokenPattern).matcher(content);
        if (matcher.find()) {
            if (logger.isDebugEnabled()) {
                logger.debug("Matched: {}", matcher.group());
            }
            if (matcher.groupCount() > 0) {
                return matcher.group(1);
            }
        }
        return null;
    }

    /**
     * Replaces credentials in the given value.
     * @param credentials The credentials.
     * @param value The value to replace.
     * @return The value with credentials replaced.
     */
    protected String replaceCredentials(final Credentials credentials, final String value) {
        if (StringUtil.isNotBlank(value)) {
            return value.replace(USERNAME, credentials.getUserPrincipal().getName()).replace(PASSWORD, credentials.getPassword());
        }
        return StringUtil.EMPTY;
    }

    /**
     * Returns a string representation of this object.
     * @return A string representation.
     */
    @Override
    public String toString() {
        return "FormScheme [parameterMap=" + parameterMap + "]";
    }
}
