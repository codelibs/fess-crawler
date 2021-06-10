/*
 * Copyright 2012-2021 CodeLibs Project and the Others.
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

import org.apache.http.impl.auth.NTLMEngine;
import org.apache.http.impl.auth.NTLMEngineException;

/**
 * SmbjEngine is a NTLM Engine implementation based on smbj.
 *
 * @author shinsuke
 *
 */
public class SmbjEngine implements NTLMEngine {

    @Override
    public String generateType1Msg(final String arg0, final String arg1) throws NTLMEngineException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String generateType3Msg(final String arg0, final String arg1, final String arg2, final String arg3, final String arg4)
            throws NTLMEngineException {
        // TODO Auto-generated method stub
        return null;
    }
}
