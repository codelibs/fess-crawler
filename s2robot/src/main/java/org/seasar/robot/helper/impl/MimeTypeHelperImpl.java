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
package org.seasar.robot.helper.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.apache.tika.mime.MimeTypes;
import org.apache.tika.mime.MimeTypesFactory;
import org.seasar.framework.util.StringUtil;
import org.seasar.robot.RobotSystemException;
import org.seasar.robot.entity.ExtractData;
import org.seasar.robot.helper.MimeTypeException;
import org.seasar.robot.helper.MimeTypeHelper;

/**
 * @author shinsuke
 *
 */
public class MimeTypeHelperImpl implements MimeTypeHelper {
    private static final String MIME_TYPES_RESOURCE_NAME = "/org/seasar/robot/mime/tika-mimetypes.xml";

    private MimeTypes mimeTypes;

    public MimeTypeHelperImpl() {
        try {
            mimeTypes = MimeTypesFactory.create(MIME_TYPES_RESOURCE_NAME);
        } catch (Exception e) {
            throw new RobotSystemException(
                    "Could not initialize MimeTypeHelper.", e);
        }
    }

    public String getContentType(InputStream is, String filename) {
        if (StringUtil.isEmpty(filename)) {
            throw new MimeTypeException("The filename is empty.");
        }
        Map<String, String> params = new HashMap<String, String>();
        params.put(ExtractData.RESOURCE_NAME_KEY, filename);
        return getContentType(is, params);
    }

    public String getContentType(InputStream is, Map<String, String> params) {
        String filename = params.get(ExtractData.RESOURCE_NAME_KEY);
        if (StringUtil.isEmpty(filename) && is == null) {
            throw new MimeTypeException(
                    "The filename or input stream is empty.");
        }

        Metadata metadata = new Metadata();
        metadata.add(Metadata.RESOURCE_NAME_KEY, filename);

        try {
            MediaType mediaType = mimeTypes.detect(is, metadata);
            return mediaType.getType() + "/" + mediaType.getSubtype();
        } catch (IOException e) {
            throw new MimeTypeException("Could not detect a content type.", e);
        }
    }
}
