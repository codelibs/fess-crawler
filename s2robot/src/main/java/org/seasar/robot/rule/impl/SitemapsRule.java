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
package org.seasar.robot.rule.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.seasar.framework.container.SingletonS2Container;
import org.seasar.robot.RobotSystemException;
import org.seasar.robot.entity.ResponseData;
import org.seasar.robot.helper.SitemapsHelper;
import org.seasar.robot.util.ResponseDataUtil;
import org.seasar.robot.util.TemporaryFileInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author shinsuke
 *
 */
public class SitemapsRule extends RegexRule {
    private static final Logger logger = LoggerFactory
        .getLogger(SitemapsRule.class);

    @Override
    public boolean match(final ResponseData responseData) {
        if (super.match(responseData)) {
            final File tempFile =
                ResponseDataUtil.createResponseBodyFile(responseData);
            try {
                responseData.setResponseBody(new TemporaryFileInputStream(
                    tempFile));
            } catch (final FileNotFoundException e) {
                throw new RobotSystemException("File does not exists: "
                    + tempFile.getAbsolutePath(), e);
            }

            InputStream is = null;
            try {
                final SitemapsHelper sitemapsHelper =
                    SingletonS2Container.getComponent("sitemapsHelper");
                is = new FileInputStream(tempFile);
                return sitemapsHelper.isValid(is);
            } catch (final Exception e) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Failed a sitemap check: " + responseData, e);
                }
            } finally {
                IOUtils.closeQuietly(is);
            }
        }

        return false;
    }
}
