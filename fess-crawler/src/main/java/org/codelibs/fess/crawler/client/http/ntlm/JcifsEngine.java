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

import java.io.IOException;
import java.util.Base64;
import java.util.Properties;

import org.apache.http.impl.auth.NTLMEngine;
import org.apache.http.impl.auth.NTLMEngineException;
import org.codelibs.fess.crawler.exception.CrawlingAccessException;

import org.codelibs.jcifs.smb.CIFSException;
import org.codelibs.jcifs.smb.config.PropertyConfiguration;
import org.codelibs.jcifs.smb.context.BaseContext;
import org.codelibs.jcifs.smb.ntlmssp.NtlmFlags;
import org.codelibs.jcifs.smb.ntlmssp.Type1Message;
import org.codelibs.jcifs.smb.ntlmssp.Type2Message;
import org.codelibs.jcifs.smb.ntlmssp.Type3Message;

/**
 * JcifsEngine is a NTLM Engine implementation based on JCIFS.
 *
 * @author shinsuke
 *
 */
public class JcifsEngine implements NTLMEngine {

    /** Flags for Type 1 NTLM message. */
    protected static final int TYPE_1_FLAGS = NtlmFlags.NTLMSSP_NEGOTIATE_56 | NtlmFlags.NTLMSSP_NEGOTIATE_128
            | NtlmFlags.NTLMSSP_NEGOTIATE_NTLM | NtlmFlags.NTLMSSP_NEGOTIATE_ALWAYS_SIGN | NtlmFlags.NTLMSSP_REQUEST_TARGET;

    /** The CIFS context for NTLM authentication. */
    protected BaseContext cifsContext;

    /**
     * Constructs a JcifsEngine with the specified properties.
     *
     * @param props the properties for configuring the CIFS context
     * @throws CrawlingAccessException if an error occurs during initialization
     */
    public JcifsEngine(final Properties props) {
        try {
            cifsContext = new BaseContext(new PropertyConfiguration(props));
        } catch (final CIFSException e) {
            throw new CrawlingAccessException(e);
        }
    }

    /**
     * Generates a Type 1 NTLM message.
     *
     * @param domain the domain name
     * @param workstation the workstation name
     * @return the Base64-encoded Type 1 message
     * @throws NTLMEngineException if an NTLM engine error occurs
     */
    @Override
    public String generateType1Msg(final String domain, final String workstation) throws NTLMEngineException {
        final Type1Message type1Message = new Type1Message(cifsContext, TYPE_1_FLAGS, domain, workstation);
        return Base64.getEncoder().encodeToString(type1Message.toByteArray());
    }

    /**
     * Generates a Type 3 NTLM message.
     *
     * @param username the username
     * @param password the password
     * @param domain the domain
     * @param workstation the workstation
     * @param challenge the Type 2 challenge message
     * @return the Base64-encoded Type 3 message
     * @throws NTLMEngineException if an NTLM engine error occurs
     * @throws CrawlingAccessException if an error occurs during message generation
     */
    @Override
    public String generateType3Msg(final String username, final String password, final String domain, final String workstation,
            final String challenge) throws NTLMEngineException {
        Type2Message type2Message;
        try {
            type2Message = new Type2Message(Base64.getDecoder().decode(challenge));
        } catch (final IOException exception) {
            throw new NTLMEngineException("Invalid NTLM type 2 message", exception);
        }
        final int type2Flags = type2Message.getFlags();
        final int type3Flags = type2Flags & (0xffffffff ^ (NtlmFlags.NTLMSSP_TARGET_TYPE_DOMAIN | NtlmFlags.NTLMSSP_TARGET_TYPE_SERVER));
        try {
            final Type3Message type3Message =
                    new Type3Message(cifsContext, type2Message, null, password, domain, username, workstation, type3Flags);
            return Base64.getEncoder().encodeToString(type3Message.toByteArray());
        } catch (final Exception e) {
            throw new CrawlingAccessException(e);
        }
    }
}
