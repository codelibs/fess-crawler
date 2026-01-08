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

/**
 * POJO configuration for credentials that can be converted to
 * either HC4 Credentials or HC5 Credentials.
 *
 * <p>This class provides a library-independent way to configure
 * authentication credentials that work with both Apache HttpComponents 4.x
 * and 5.x clients.</p>
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * CredentialsConfig config = new CredentialsConfig();
 * config.setUsername("user");
 * config.setPassword("secret");
 *
 * // For NTLM authentication
 * config.setType(CredentialsType.NTLM);
 * config.setDomain("MYDOMAIN");
 * config.setWorkstation("MYWORKSTATION");
 * }</pre>
 */
public class CredentialsConfig {

    /**
     * Type of credentials.
     */
    public enum CredentialsType {
        /**
         * Username and password credentials.
         */
        USERNAME_PASSWORD,
        /**
         * NTLM credentials with domain and workstation.
         */
        NTLM
    }

    private CredentialsType type = CredentialsType.USERNAME_PASSWORD;
    private String username;
    private String password;

    // NTLM-specific fields
    private String domain;
    private String workstation;

    /**
     * Gets the credentials type.
     *
     * @return the credentials type
     */
    public CredentialsType getType() {
        return type;
    }

    /**
     * Sets the credentials type.
     *
     * @param type the credentials type
     */
    public void setType(final CredentialsType type) {
        this.type = type;
    }

    /**
     * Gets the username.
     *
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username.
     *
     * @param username the username
     */
    public void setUsername(final String username) {
        this.username = username;
    }

    /**
     * Gets the password.
     *
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the password.
     *
     * @param password the password
     */
    public void setPassword(final String password) {
        this.password = password;
    }

    /**
     * Gets the NTLM domain.
     *
     * @return the domain
     */
    public String getDomain() {
        return domain;
    }

    /**
     * Sets the NTLM domain.
     *
     * @param domain the domain
     */
    public void setDomain(final String domain) {
        this.domain = domain;
    }

    /**
     * Gets the NTLM workstation.
     *
     * @return the workstation
     */
    public String getWorkstation() {
        return workstation;
    }

    /**
     * Sets the NTLM workstation.
     *
     * @param workstation the workstation
     */
    public void setWorkstation(final String workstation) {
        this.workstation = workstation;
    }

    @Override
    public String toString() {
        return "CredentialsConfig [type=" + type + ", username=" + username + ", domain=" + domain + ", workstation=" + workstation + "]";
    }
}
