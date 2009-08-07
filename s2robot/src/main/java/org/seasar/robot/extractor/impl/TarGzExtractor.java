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

import java.io.InputStream;

import javax.annotation.Resource;

import org.apache.commons.compress.compressors.CompressorInputStream;
import org.apache.commons.compress.compressors.CompressorStreamFactory;
import org.apache.commons.io.IOUtils;
import org.seasar.framework.container.SingletonS2Container;
import org.seasar.robot.RobotSystemException;
import org.seasar.robot.extractor.ExtractException;
import org.seasar.robot.extractor.ExtractorFactory;
import org.seasar.robot.helper.MimeTypeHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author shinsuke
 *
 */
public class TarGzExtractor extends TarExtractor {
    private static final Logger logger = LoggerFactory
            .getLogger(TarGzExtractor.class);

    @Resource
    protected CompressorStreamFactory compressorStreamFactory;

    /* (non-Javadoc)
     * @see org.seasar.robot.extractor.Extractor#getText(java.io.InputStream)
     */
    public String getText(InputStream in) {
        if (in == null) {
            throw new RobotSystemException("The inputstream is null.");
        }

        MimeTypeHelper mimeTypeHelper = SingletonS2Container
                .getComponent("mimeTypeHelper");
        if (mimeTypeHelper == null) {
            throw new RobotSystemException("MimeTypeHelper is unavailable.");
        }

        ExtractorFactory extractorFactory = SingletonS2Container
                .getComponent("extractorFactory");
        if (extractorFactory == null) {
            throw new RobotSystemException("ExtractorFactory is unavailable.");
        }

        CompressorInputStream cin = null;

        try {
            cin = compressorStreamFactory.createCompressorInputStream("gz", in);
            return getTextInternal(cin, mimeTypeHelper, extractorFactory);
        } catch (ExtractException e) {
            throw e;
        } catch (Exception e) {
            throw new ExtractException("Could not extract a content.", e);
        } finally {
            IOUtils.closeQuietly(cin);
        }
    }
}
