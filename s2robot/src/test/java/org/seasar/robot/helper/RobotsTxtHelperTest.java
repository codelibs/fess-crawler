/*
 * Copyright 2004-2009 the Seasar Foundation and the Others.
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
package org.seasar.robot.helper;

import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.io.IOUtils;
import org.seasar.extension.unit.S2TestCase;
import org.seasar.robot.entity.RobotsTxt;

public class RobotsTxtHelperTest extends S2TestCase {
    public RobotsTxtHelper robotsTxtHelper;

    @Override
    protected String getRootDicon() throws Throwable {
        return "app.dicon";
    }

    public void testParse() {
        RobotsTxt robotsTxt;
        InputStream in = RobotsTxtHelperTest.class
                .getResourceAsStream("robots.txt");
        try {
            robotsTxt = robotsTxtHelper.parse(new InputStreamReader(in));
        } finally {
            IOUtils.closeQuietly(in);
        }

        assertTrue(robotsTxt.allows("/aaa", "S2Robot"));
        assertTrue(robotsTxt.allows("/private/", "S2Robot"));
        assertTrue(robotsTxt.allows("/private/index.html", "S2Robot"));
        assertTrue(robotsTxt.allows("/help/", "S2Robot"));
        assertTrue(robotsTxt.allows("/help.html", "S2Robot"));
        assertTrue(robotsTxt.allows("/help/faq.html", "S2Robot"));
        assertTrue(robotsTxt.allows("/foo/bar/", "S2Robot"));
        assertTrue(robotsTxt.allows("/foo/bar/index.html", "S2Robot"));
        assertEquals(0, robotsTxt.getCrawlDelay("S2Robot"));

        assertFalse(robotsTxt.allows("/aaa", "BruteBot"));
        assertFalse(robotsTxt.allows("/private/", "BruteBot"));
        assertFalse(robotsTxt.allows("/private/index.html", "BruteBot"));
        assertFalse(robotsTxt.allows("/help/", "BruteBot"));
        assertFalse(robotsTxt.allows("/help.html", "BruteBot"));
        assertFalse(robotsTxt.allows("/help/faq.html", "BruteBot"));
        assertTrue(robotsTxt.allows("/foo/bar/", "BruteBot"));
        assertTrue(robotsTxt.allows("/foo/bar/index.html", "BruteBot"));
        assertEquals(1314000, robotsTxt.getCrawlDelay("BruteBot"));

        assertTrue(robotsTxt.allows("/aaa", "GOOGLEBOT"));
        assertTrue(robotsTxt.allows("/private/", "GOOGLEBOT"));
        assertTrue(robotsTxt.allows("/private/index.html", "GOOGLEBOT"));
        assertTrue(robotsTxt.allows("/help/", "GOOGLEBOT"));
        assertTrue(robotsTxt.allows("/help.html", "GOOGLEBOT"));
        assertTrue(robotsTxt.allows("/help/faq.html", "GOOGLEBOT"));
        assertTrue(robotsTxt.allows("/foo/bar/", "GOOGLEBOT"));
        assertTrue(robotsTxt.allows("/foo/bar/index.html", "GOOGLEBOT"));
        assertEquals(1, robotsTxt.getCrawlDelay("GOOGLEBOT"));

        assertTrue(robotsTxt.allows("/aaa", "UnknownBot"));
        assertFalse(robotsTxt.allows("/private/", "UnknownBot"));
        assertFalse(robotsTxt.allows("/private/index.html", "UnknownBot"));
        assertFalse(robotsTxt.allows("/help/", "UnknownBot"));
        assertFalse(robotsTxt.allows("/help.html", "UnknownBot"));
        assertTrue(robotsTxt.allows("/help/faq.html", "UnknownBot"));
        assertTrue(robotsTxt.allows("/foo/bar/", "UnknownBot"));
        assertTrue(robotsTxt.allows("/foo/bar/index.html", "UnknownBot"));
        assertEquals(3, robotsTxt.getCrawlDelay("UnknownBot"));

    }

}
