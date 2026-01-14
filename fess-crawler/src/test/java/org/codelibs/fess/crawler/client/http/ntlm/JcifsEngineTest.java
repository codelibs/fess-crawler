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

import org.apache.http.impl.auth.NTLMEngineException;
import org.junit.jupiter.api.Test;
import org.dbflute.utflute.core.PlainTestCase;

public class JcifsEngineTest extends PlainTestCase {

    @Test
    public void test_constructor() {
        Properties props = new Properties();

        JcifsEngine engine = new JcifsEngine(props);

        assertNotNull(engine);
        assertNotNull(engine.cifsContext);
    }

    @Test
    public void test_generateType1Msg() throws NTLMEngineException {
        Properties props = new Properties();
        JcifsEngine engine = new JcifsEngine(props);

        String type1Msg = engine.generateType1Msg("DOMAIN", "WORKSTATION");

        assertNotNull(type1Msg);
        assertTrue(type1Msg.length() > 0);
    }

    @Test
    public void test_generateType1Msg_nullDomain() throws NTLMEngineException {
        Properties props = new Properties();
        JcifsEngine engine = new JcifsEngine(props);

        String type1Msg = engine.generateType1Msg(null, null);

        assertNotNull(type1Msg);
        assertTrue(type1Msg.length() > 0);
    }

    @Test
    public void test_generateType3Msg_invalidChallenge() {
        Properties props = new Properties();
        JcifsEngine engine = new JcifsEngine(props);

        try {
            // Valid base64 but invalid NTLM message should throw exception
            // Using "AAAA" which is valid base64 but not a valid NTLM Type2 message
            engine.generateType3Msg("user", "password", "DOMAIN", "WORKSTATION", "AAAA");
            fail();
        } catch (NTLMEngineException e) {
            // Expected
            assertTrue(e.getMessage().contains("Invalid NTLM type 2 message"));
        }
    }

    @Test
    public void test_type1Flags() {
        // Verify TYPE_1_FLAGS is properly defined
        assertTrue(JcifsEngine.TYPE_1_FLAGS != 0);
    }
}
