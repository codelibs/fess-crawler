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
package org.codelibs.fess.crawler.client.http.ntlm;

import java.util.Properties;

import org.apache.hc.client5.http.auth.AuthScheme;
import org.apache.hc.client5.http.auth.AuthSchemeFactory;
import org.apache.hc.client5.http.impl.auth.NTLMEngine;
import org.apache.hc.client5.http.impl.auth.NTLMScheme;
import org.apache.hc.core5.http.protocol.HttpContext;

/**
 * Hc5NTLMSchemeFactory is an AuthSchemeFactory implementation for NTLM authentication
 * in Apache HttpComponents 5.x.
 *
 * <p>This factory creates NTLM authentication schemes using either a custom NTLMEngine
 * or the JCIFS-based engine implementation.</p>
 *
 * <p>Usage example:</p>
 * <pre>
 * {@code
 * // Using default JCIFS engine
 * Properties props = new Properties();
 * Hc5NTLMSchemeFactory factory = new Hc5NTLMSchemeFactory(props);
 *
 * // Register with HttpClient
 * Map<String, AuthSchemeFactory> authSchemeMap = new HashMap<>();
 * authSchemeMap.put("NTLM", factory);
 * httpClient.setAuthSchemeFactoryMap(authSchemeMap);
 * }
 * </pre>
 *
 * @author shinsuke
 */
public class Hc5NTLMSchemeFactory implements AuthSchemeFactory {

    /** The NTLM engine to use for authentication. */
    protected NTLMEngine ntlmEngine;

    /**
     * Creates a new Hc5NTLMSchemeFactory with default NTLM engine.
     * Uses the built-in NTLM implementation from HttpClient 5.
     */
    public Hc5NTLMSchemeFactory() {
        this.ntlmEngine = null;
    }

    /**
     * Creates a new Hc5NTLMSchemeFactory with JCIFS-based NTLM engine.
     *
     * @param props the properties for configuring the JCIFS context
     */
    public Hc5NTLMSchemeFactory(final Properties props) {
        this.ntlmEngine = new Hc5JcifsEngine(props);
    }

    /**
     * Creates a new Hc5NTLMSchemeFactory with a custom NTLM engine.
     *
     * @param ntlmEngine the NTLM engine to use
     */
    public Hc5NTLMSchemeFactory(final NTLMEngine ntlmEngine) {
        this.ntlmEngine = ntlmEngine;
    }

    /**
     * Creates a new NTLMScheme instance.
     *
     * @param context The HTTP context (not used in this implementation).
     * @return A new NTLMScheme instance.
     */
    @Override
    public AuthScheme create(final HttpContext context) {
        if (ntlmEngine != null) {
            return new NTLMScheme(ntlmEngine);
        }
        return new NTLMScheme();
    }
}
