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

import static org.mockito.Mockito.mock;

import org.apache.hc.client5.http.auth.AuthScheme;
import org.apache.hc.client5.http.impl.auth.NTLMEngine;
import org.apache.hc.client5.http.impl.auth.NTLMScheme;
import org.dbflute.utflute.core.PlainTestCase;

public class Hc5NTLMSchemeFactoryTest extends PlainTestCase {

    public void test_constructor_default() {
        Hc5NTLMSchemeFactory factory = new Hc5NTLMSchemeFactory();
        assertNotNull(factory);
    }

    public void test_constructor_withNtlmEngine() {
        NTLMEngine mockEngine = mock(NTLMEngine.class);

        Hc5NTLMSchemeFactory factory = new Hc5NTLMSchemeFactory(mockEngine);

        assertNotNull(factory);
    }

    public void test_create_withoutNtlmEngine() {
        Hc5NTLMSchemeFactory factory = new Hc5NTLMSchemeFactory();

        AuthScheme authScheme = factory.create(null);

        assertNotNull(authScheme);
        assertTrue(authScheme instanceof NTLMScheme);
    }

    public void test_create_withNtlmEngine() {
        NTLMEngine mockEngine = mock(NTLMEngine.class);
        Hc5NTLMSchemeFactory factory = new Hc5NTLMSchemeFactory(mockEngine);

        AuthScheme authScheme = factory.create(null);

        assertNotNull(authScheme);
        assertTrue(authScheme instanceof NTLMScheme);
    }

    public void test_create_withNullContext() {
        Hc5NTLMSchemeFactory factory = new Hc5NTLMSchemeFactory();

        // Should not throw even with null context
        AuthScheme authScheme = factory.create(null);
        assertNotNull(authScheme);
    }
}
