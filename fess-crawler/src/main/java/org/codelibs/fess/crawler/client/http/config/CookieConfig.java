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
package org.codelibs.fess.crawler.client.http.config;

import java.util.Date;

/**
 * POJO configuration for HTTP cookies that can be converted to
 * either HC4 Cookie or HC5 Cookie.
 *
 * <p>This class provides a library-independent way to configure
 * cookies that work with both Apache HttpComponents 4.x
 * and 5.x clients.</p>
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * CookieConfig config = new CookieConfig();
 * config.setName("sessionId");
 * config.setValue("abc123");
 * config.setDomain("example.com");
 * config.setPath("/");
 * config.setSecure(true);
 * }</pre>
 */
public class CookieConfig {

    private String name;
    private String value;
    private String domain;
    private String path;
    private Date expiryDate;
    private boolean secure;
    private boolean httpOnly;

    /**
     * Gets the cookie name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the cookie name.
     *
     * @param name the name
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * Gets the cookie value.
     *
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets the cookie value.
     *
     * @param value the value
     */
    public void setValue(final String value) {
        this.value = value;
    }

    /**
     * Gets the cookie domain.
     *
     * @return the domain
     */
    public String getDomain() {
        return domain;
    }

    /**
     * Sets the cookie domain.
     *
     * @param domain the domain
     */
    public void setDomain(final String domain) {
        this.domain = domain;
    }

    /**
     * Gets the cookie path.
     *
     * @return the path
     */
    public String getPath() {
        return path;
    }

    /**
     * Sets the cookie path.
     *
     * @param path the path
     */
    public void setPath(final String path) {
        this.path = path;
    }

    /**
     * Gets the cookie expiry date.
     *
     * @return the expiry date
     */
    public Date getExpiryDate() {
        return expiryDate;
    }

    /**
     * Sets the cookie expiry date.
     *
     * @param expiryDate the expiry date
     */
    public void setExpiryDate(final Date expiryDate) {
        this.expiryDate = expiryDate;
    }

    /**
     * Checks if the cookie is secure.
     *
     * @return true if secure
     */
    public boolean isSecure() {
        return secure;
    }

    /**
     * Sets whether the cookie is secure.
     *
     * @param secure true for secure cookies
     */
    public void setSecure(final boolean secure) {
        this.secure = secure;
    }

    /**
     * Checks if the cookie is HTTP-only.
     *
     * @return true if HTTP-only
     */
    public boolean isHttpOnly() {
        return httpOnly;
    }

    /**
     * Sets whether the cookie is HTTP-only.
     *
     * @param httpOnly true for HTTP-only cookies
     */
    public void setHttpOnly(final boolean httpOnly) {
        this.httpOnly = httpOnly;
    }

    @Override
    public String toString() {
        return "CookieConfig [name=" + name + ", domain=" + domain + ", path=" + path + ", secure=" + secure + ", httpOnly=" + httpOnly
                + "]";
    }
}
