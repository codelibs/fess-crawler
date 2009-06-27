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
package org.seasar.robot.extractor;

import org.seasar.extension.unit.S2TestCase;
import org.seasar.robot.extractor.impl.MsExcelExtractor;
import org.seasar.robot.extractor.impl.MsPowerPointExtractor;
import org.seasar.robot.extractor.impl.MsPublisherExtractor;
import org.seasar.robot.extractor.impl.MsVisioExtractor;
import org.seasar.robot.extractor.impl.MsWordExtractor;
import org.seasar.robot.extractor.impl.PdfExtractor;

/**
 * @author shinsuke
 *
 */
public class ExtractorFactoryTest extends S2TestCase {
    public ExtractorFactory extractorFactory;

    @Override
    protected String getRootDicon() throws Throwable {
        return "app.dicon";
    }

    public void test_getExtractor() {
        String key;

        key = "application/msword";
        assertTrue(extractorFactory.getExtractor(key) instanceof MsWordExtractor);

        key = "application/vnd.ms-excel";
        assertTrue(extractorFactory.getExtractor(key) instanceof MsExcelExtractor);

        key = "application/vnd.ms-powerpoint";
        assertTrue(extractorFactory.getExtractor(key) instanceof MsPowerPointExtractor);

        key = "application/vnd.visio";
        assertTrue(extractorFactory.getExtractor(key) instanceof MsVisioExtractor);

        key = "application/vnd.ms-publisher";
        assertTrue(extractorFactory.getExtractor(key) instanceof MsPublisherExtractor);

        key = "application/pdf";
        assertTrue(extractorFactory.getExtractor(key) instanceof PdfExtractor);

    }
}
