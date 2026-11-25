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
package org.codelibs.fess.crawler.client.smb;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import org.codelibs.jcifs.smb.CIFSContext;
import org.codelibs.jcifs.smb.SID;
import org.codelibs.jcifs.smb.SidResolver;
import org.codelibs.jcifs.smb.impl.SmbFile;
import org.dbflute.utflute.core.PlainTestCase;
import org.junit.jupiter.api.Test;

/**
 * Tests the server that {@link SmbClient} gives to the SID resolver to look up the members of a group.
 */
public class SmbClientGroupServerTest extends PlainTestCase {

    private final SmbClient smbClient = new SmbClient();

    private SmbFile file(final String server, final String url) throws Exception {
        final SmbFile file = mock(SmbFile.class);
        doReturn(server).when(file).getServer();
        doReturn(new URL(url)).when(file).getURL();
        return file;
    }

    @Test
    public void test_defaultPort_isTheHostOnly() throws Exception {
        assertEquals("fileserver", smbClient.getServerOf(file("fileserver", "http://fileserver/share/a.txt")));
    }

    @Test
    public void test_otherPort_followsTheHost() throws Exception {
        assertEquals("fileserver:1445", smbClient.getServerOf(file("fileserver", "http://fileserver:1445/share/a.txt")));
    }

    @Test
    public void test_port445_isTheHostOnly() throws Exception {
        // an explicit default port is looked up like no port, as before
        assertEquals("fileserver", smbClient.getServerOf(file("fileserver", "http://fileserver:445/share/a.txt")));
    }

    @Test
    public void test_groupMembers_areLookedUpOnTheServerAndPortOfTheFile() throws Exception {
        final SmbFile file = file("fileserver", "http://fileserver:1445/share/a.txt");
        final CIFSContext context = mock(CIFSContext.class);
        final SidResolver resolver = mock(SidResolver.class);
        doReturn(context).when(file).getContext();
        doReturn(resolver).when(context).getSIDResolver();
        final SID domainSid = mock(SID.class);
        final SID group = mock(SID.class);
        doReturn(SID.SID_TYPE_DOM_GRP).when(group).getType();
        doReturn(domainSid).when(group).getDomainSid();
        doReturn(1104).when(group).getRid();
        doReturn(new SID[0]).when(resolver).getGroupMemberSids(any(), any(), any(), anyInt(), anyInt());

        final Set<SID> sids = new HashSet<>();
        smbClient.processAllowedSIDs(file, group, sids);

        verify(resolver).getGroupMemberSids(eq(context), eq("fileserver:1445"), eq(domainSid), eq(1104), anyInt());
        assertTrue(sids.contains(group));
    }

    @Test
    public void test_serverWithColon_isLeftAsIs() throws Exception {
        assertEquals("::1", smbClient.getServerOf(file("::1", "http://[::1]:1445/share/a.txt")));
    }

    @Test
    public void test_noServer_isNull() throws Exception {
        assertNull(smbClient.getServerOf(file(null, "http://host/a.txt")));
    }
}
