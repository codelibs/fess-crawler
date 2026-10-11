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
package org.codelibs.fess.crawler.client.smb1;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.codelibs.fess.crawler.entity.ResponseData;
import org.codelibs.jcifs.smb1.ACE;
import org.codelibs.jcifs.smb1.SID;
import org.codelibs.jcifs.smb1.SmbFile;
import org.dbflute.utflute.core.PlainTestCase;
import org.junit.jupiter.api.Test;

/**
 * Tests how {@link SmbClient#processAccessControlEntries} sorts ACEs into the allowed and denied SID sets.
 */
public class SmbClientAceTest extends PlainTestCase {

    private static final int READ_AND_EXECUTE = 0x001200A9;

    private static final int WRITE_ONLY = 0x00100116;

    private final SmbClient smbClient = new SmbClient();

    private SID sid(final String name) {
        return mock(SID.class, name);
    }

    private ACE ace(final SID sid, final boolean allow, final int mask) {
        final ACE ace = mock(ACE.class);
        when(ace.getSID()).thenReturn(sid);
        when(ace.isAllow()).thenReturn(allow);
        when(ace.getAccessMask()).thenReturn(mask);
        return ace;
    }

    private ResponseData process(final ACE... aces) throws IOException {
        final SmbFile file = mock(SmbFile.class);
        doReturn(aces).when(file).getSecurity(true);
        final ResponseData responseData = new ResponseData();
        smbClient.processAccessControlEntries(responseData, file);
        return responseData;
    }

    private SID[] allowed(final ResponseData responseData) {
        return (SID[]) responseData.getMetaDataMap().get(SmbClient.SMB_ALLOWED_SID_ENTRIES);
    }

    private SID[] denied(final ResponseData responseData) {
        return (SID[]) responseData.getMetaDataMap().get(SmbClient.SMB_DENIED_SID_ENTRIES);
    }

    @Test
    public void test_allowAceWithEmptyMask_isNotReadable() throws Exception {
        final SID everyone = sid("Everyone");
        final ResponseData responseData = process(ace(everyone, true, 0));
        assertEquals(0, allowed(responseData).length);
        assertEquals(0, denied(responseData).length);
    }

    @Test
    public void test_allowAceWithReadMask_isReadable() throws Exception {
        final SID everyone = sid("Everyone");
        final ResponseData responseData = process(ace(everyone, true, READ_AND_EXECUTE));
        assertEquals(1, allowed(responseData).length);
        assertTrue(everyone == allowed(responseData)[0]);
    }

    @Test
    public void test_allowAceWithGenericMask_isReadable() throws Exception {
        final SID reader = sid("reader");
        final SID owner = sid("owner");
        final ResponseData responseData = process(ace(reader, true, ACE.GENERIC_READ), ace(owner, true, ACE.GENERIC_ALL));
        assertEquals(2, allowed(responseData).length);
    }

    @Test
    public void test_allowAceWithoutReadBit_isNotReadable() throws Exception {
        final ResponseData responseData = process(ace(sid("writer"), true, WRITE_ONLY));
        assertEquals(0, allowed(responseData).length);
    }

    @Test
    public void test_emptyMaskDoesNotHideOtherAces() throws Exception {
        final SID everyone = sid("Everyone");
        final SID user = sid("user");
        final ResponseData responseData = process(ace(everyone, true, 0), ace(user, true, READ_AND_EXECUTE));
        assertEquals(1, allowed(responseData).length);
        assertTrue(user == allowed(responseData)[0]);
    }

    @Test
    public void test_denyAce_isRecordedRegardlessOfMask() throws Exception {
        final SID blocked = sid("blocked");
        final ResponseData responseData = process(ace(blocked, false, ACE.FILE_READ_DATA));
        assertEquals(0, allowed(responseData).length);
        assertEquals(1, denied(responseData).length);
        assertTrue(blocked == denied(responseData)[0]);
    }
}
