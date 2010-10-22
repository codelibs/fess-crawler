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
package org.seasar.robot.client.http.ntlm;

import java.io.IOException;

import jcifs.ntlmssp.Type1Message;
import jcifs.ntlmssp.Type2Message;
import jcifs.ntlmssp.Type3Message;
import jcifs.util.Base64;

import org.apache.http.impl.auth.NTLMEngine;
import org.apache.http.impl.auth.NTLMEngineException;

/**
 * JcifsEngine is a NTLM Engine implementation based on JCIFS.
 * 
 * @author shinsuke
 * 
 */
public class JcifsEngine implements NTLMEngine {

    public String generateType1Msg(final String domain, final String workstation)
            throws NTLMEngineException {

        final Type1Message t1m =
            new Type1Message(
                Type1Message.getDefaultFlags(),
                domain,
                workstation);
        return Base64.encode(t1m.toByteArray());
    }

    public String generateType3Msg(final String username,
            final String password, final String domain,
            final String workstation, final String challenge)
            throws NTLMEngineException {
        Type2Message t2m;
        try {
            t2m = new Type2Message(Base64.decode(challenge));
        } catch (IOException ex) {
            throw new NTLMEngineException("Invalid Type2 message", ex);
        }
        final Type3Message t3m =
            new Type3Message(t2m, password, domain, username, workstation, 512);
        return Base64.encode(t3m.toByteArray());
    }

}
