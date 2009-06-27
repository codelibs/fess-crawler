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
package org.seasar.robot.extractor.impl;

import java.io.IOException;
import java.io.InputStream;

import org.seasar.robot.RobotSystemException;
import org.seasar.robot.extractor.ExtractException;
import org.seasar.robot.extractor.Extractor;

/**
 * Gets a text from .doc file.
 * 
 * @author shinsuke
 *
 */
public class MsWordExtractor implements Extractor {

    /* (non-Javadoc)
     * @see org.seasar.robot.extractor.Extractor#getText(java.io.InputStream)
     */
    public String getText(InputStream in) {
        if (in == null) {
            throw new RobotSystemException("The inputstream is null.");
        }
        try {
            return new org.apache.poi.hwpf.extractor.WordExtractor(in)
                    .getText();
        } catch (IOException e) {
            throw new ExtractException(e);
        }
    }

}
