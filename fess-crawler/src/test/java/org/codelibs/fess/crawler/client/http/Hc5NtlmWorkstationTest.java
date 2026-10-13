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
package org.codelibs.fess.crawler.client.http;

import java.util.Base64;
import java.util.Properties;

import org.apache.hc.client5.http.auth.AuthChallenge;
import org.apache.hc.client5.http.auth.AuthScope;
import org.apache.hc.client5.http.auth.ChallengeType;
import org.apache.hc.client5.http.auth.Credentials;
import org.apache.hc.client5.http.auth.NTCredentials;
import org.apache.hc.client5.http.impl.auth.BasicCredentialsProvider;
import org.apache.hc.client5.http.impl.auth.NTLMScheme;
import org.apache.hc.client5.http.protocol.HttpClientContext;
import org.apache.hc.core5.http.HttpHost;
import org.codelibs.fess.crawler.client.http.config.CredentialsConfig;
import org.codelibs.fess.crawler.client.http.ntlm.Hc5JcifsEngine;
import org.codelibs.jcifs.smb.config.PropertyConfiguration;
import org.codelibs.jcifs.smb.context.BaseContext;
import org.codelibs.jcifs.smb.ntlmssp.Type2Message;
import org.codelibs.jcifs.smb.ntlmssp.Type3Message;
import org.dbflute.utflute.core.PlainTestCase;
import org.junit.jupiter.api.Test;

/**
 * Checks which workstation name the NTLM messages of the HTTP client carry. HttpClient 5 ignores the
 * workstation argument of its NTCredentials constructors and fills in the local host name, so the
 * workstation of a web authentication has to be put back by the client.
 */
public class Hc5NtlmWorkstationTest extends PlainTestCase {

    private static final HttpHost HOST = new HttpHost("http", "localhost", 8080);

    private Credentials createCredentials(final String workstation) {
        final CredentialsConfig config = new CredentialsConfig();
        config.setType(CredentialsConfig.CredentialsType.NTLM);
        config.setUsername("alice");
        config.setPassword("secret");
        config.setDomain("CORP");
        config.setWorkstation(workstation);
        return new Hc5HttpClient().convertCredentials(config);
    }

    /**
     * Runs the NTLM handshake of HttpClient 5 up to the Type 3 message and returns that message. The
     * Type 1 message of JCIFS carries neither a domain nor a workstation, so the Type 3 message is
     * where the workstation is sent.
     */
    private Type3Message handshake(final Credentials credentials) throws Exception {
        final Hc5JcifsEngine engine = new Hc5JcifsEngine(new Properties());
        final NTLMScheme scheme = new NTLMScheme(engine);
        final BasicCredentialsProvider provider = new BasicCredentialsProvider();
        provider.setCredentials(new AuthScope(null, null, -1, null, null), credentials);
        final HttpClientContext context = HttpClientContext.create();

        scheme.processChallenge(new AuthChallenge(ChallengeType.TARGET, "NTLM", null, null), context);
        assertTrue(scheme.isResponseReady(HOST, provider, context));
        assertTrue(scheme.generateAuthResponse(HOST, null, context).startsWith("NTLM "));

        final BaseContext cifsContext = new BaseContext(new PropertyConfiguration(new Properties()));
        final Type2Message type2Message =
                new Type2Message(cifsContext, Type2Message.getDefaultFlags(cifsContext), new byte[] { 1, 2, 3, 4, 5, 6, 7, 8 }, "CORP");
        scheme.processChallenge(
                new AuthChallenge(ChallengeType.TARGET, "NTLM", Base64.getEncoder().encodeToString(type2Message.toByteArray()), null),
                context);
        final String authResponse = scheme.generateAuthResponse(HOST, null, context);
        assertTrue(authResponse.startsWith("NTLM "));
        return new Type3Message(Base64.getDecoder().decode(authResponse.substring("NTLM ".length())));
    }

    @Test
    public void test_configuredWorkstationIsSentInType3() throws Exception {
        final Type3Message type3 = handshake(createCredentials("UKWS1"));

        assertEquals("UKWS1", type3.getWorkstation());
        assertEquals("alice", type3.getUser());
        assertEquals("CORP", type3.getDomain());
    }

    @Test
    public void test_blankWorkstationKeepsTheLocalHostName() throws Exception {
        // what HttpClient 5 fills in itself: the local host name
        final String localWorkstation = new NTCredentials("alice", "secret".toCharArray(), null, "CORP").getWorkstation();

        for (final String blank : new String[] { null, "", "  " }) {
            final Credentials credentials = createCredentials(blank);
            assertEquals(localWorkstation, ((NTCredentials) credentials).getWorkstation());

            assertEquals(localWorkstation, handshake(credentials).getWorkstation());
        }
    }

    @Test
    public void test_workstationOfHc4CredentialsIsKept() throws Exception {
        final Credentials credentials =
                new Hc5HttpClient().convertFromHc4Credentials(new org.apache.http.auth.NTCredentials("alice", "secret", "HC4WS", "CORP"));

        assertEquals("HC4WS", handshake(credentials).getWorkstation());
    }
}
