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

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import org.codelibs.jcifs.smb.impl.SmbFile;
import org.dbflute.utflute.core.PlainTestCase;
import org.junit.jupiter.api.Test;

/**
 * Tests the last modified time that {@link SmbClient} gives to a response: the later of the last write time and the
 * change time of the file.
 */
public class SmbClientLastModifiedTest extends PlainTestCase {

    private static final long WRITE = 1_700_000_000_000L;

    private final SmbClient smbClient = new SmbClient();

    private SmbFile file(final long lastModified, final long changeTime) throws Exception {
        final SmbFile file = mock(SmbFile.class);
        doReturn(lastModified).when(file).lastModified();
        doReturn(changeTime).when(file).changeTime();
        return file;
    }

    @Test
    public void test_changeTimeAfterLastWrite_isTheLastModified() throws Exception {
        // an ACL change on NTFS moves the change time only
        assertEquals(WRITE + 60_000L, smbClient.getLastModifiedOf(file(WRITE, WRITE + 60_000L)));
    }

    @Test
    public void test_sameChangeTime_isTheLastWrite() throws Exception {
        // Samba sends the last write time as the change time
        assertEquals(WRITE, smbClient.getLastModifiedOf(file(WRITE, WRITE)));
    }

    @Test
    public void test_noChangeTime_isTheLastWrite() throws Exception {
        // a response that carries no change time gives 0
        assertEquals(WRITE, smbClient.getLastModifiedOf(file(WRITE, 0L)));
    }

    @Test
    public void test_changeTimeBeforeLastWrite_isTheLastWrite() throws Exception {
        assertEquals(WRITE, smbClient.getLastModifiedOf(file(WRITE, WRITE - 1_000L)));
    }
}
