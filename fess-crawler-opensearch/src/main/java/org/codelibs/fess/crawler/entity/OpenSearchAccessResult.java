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
package org.codelibs.fess.crawler.entity;

import java.io.IOException;

import org.codelibs.core.beans.util.BeanUtil;
import org.codelibs.fess.crawler.service.impl.OpenSearchDataService;
import org.lastaflute.di.core.SingletonLaContainer;
import org.opensearch.core.xcontent.ToXContent;
import org.opensearch.core.xcontent.XContentBuilder;

/**
 * OpenSearchAccessResult is an implementation of {@link AccessResult} for OpenSearch.
 *
 * @author shinsuke
 *
 */
public class OpenSearchAccessResult extends AccessResultImpl<String> implements ToXContent {

    /**
     * Creates a new instance of OpenSearchAccessResult.
     */
    public OpenSearchAccessResult() {
        // NOP
    }

    /**
     * Field name for ID.
     */
    public static final String ID = "id";

    /**
     * Field name for session ID.
     */
    public static final String SESSION_ID = "sessionId";

    /**
     * Field name for rule ID.
     */
    public static final String RULE_ID = "ruleId";

    /**
     * Field name for URL.
     */
    public static final String URL = "url";

    /**
     * Field name for parent URL.
     */
    public static final String PARENT_URL = "parentUrl";

    /**
     * Field name for status.
     */
    public static final String STATUS = "status";

    /**
     * Field name for HTTP status code.
     */
    public static final String HTTP_STATUS_CODE = "httpStatusCode";

    /**
     * Field name for method.
     */
    public static final String METHOD = "method";

    /**
     * Field name for MIME type.
     */
    public static final String MIME_TYPE = "mimeType";

    /**
     * Field name for creation time.
     */
    public static final String CREATE_TIME = "createTime";

    /**
     * Field name for execution time.
     */
    public static final String EXECUTION_TIME = "executionTime";

    /**
     * Field name for content length.
     */
    public static final String CONTENT_LENGTH = "contentLength";

    /**
     * Field name for last modified timestamp.
     */
    public static final String LAST_MODIFIED = "lastModified";

    /**
     * Field name for access result data.
     */
    public static final String ACCESS_RESULT_DATA = "accessResultData";

    /**
     * Flag indicating whether the access result data has been initialized.
     */
    private boolean initializedData = false;

    /**
     * Initializes the access result with response data and result data.
     *
     * @param responseData The response data from the crawl operation.
     * @param resultData The result data from content processing.
     */
    @Override
    public void init(final ResponseData responseData, final ResultData resultData) {

        setCreateTime(System.currentTimeMillis());
        if (responseData != null) {
            BeanUtil.copyBeanToBean(responseData, this);
        }

        final OpenSearchAccessResultData accessResultData = new OpenSearchAccessResultData();
        if (resultData != null) {
            BeanUtil.copyBeanToBean(resultData, accessResultData);
        }
        setAccessResultData(accessResultData);
    }

    /**
     * Gets the access result data, loading it from the data service if not already initialized.
     *
     * @return The access result data.
     */
    @Override
    public AccessResultData<String> getAccessResultData() {
        if (!initializedData) {
            final OpenSearchDataService dataService = SingletonLaContainer.getComponent(OpenSearchDataService.class);
            final OpenSearchAccessResult accessResult = dataService.getAccessResult(getSessionId(), getUrl());
            if (accessResult != null && accessResult.accessResultData != null) {
                setAccessResultData(accessResult.accessResultData);
            } else {
                setAccessResultData(null);
            }
        }
        return accessResultData;
    }

    /**
     * Sets the access result data and marks it as initialized.
     *
     * @param accessResultDataAsOne The access result data to set.
     */
    @Override
    public void setAccessResultData(final AccessResultData<String> accessResultDataAsOne) {
        accessResultData = accessResultDataAsOne;
        initializedData = true;
    }

    /**
     * Converts this access result to XContent format for OpenSearch indexing.
     *
     * @param builder The XContentBuilder to write to.
     * @param params Additional parameters for the conversion.
     * @return The XContentBuilder with the access result data.
     * @throws IOException if the conversion fails.
     */
    @Override
    public XContentBuilder toXContent(final XContentBuilder builder, final Params params) throws IOException {
        builder.startObject();
        if (sessionId != null) {
            builder.field(SESSION_ID, sessionId);
        }
        if (ruleId != null) {
            builder.field(RULE_ID, ruleId);
        }
        if (url != null) {
            builder.field(URL, url);
        }
        if (parentUrl != null) {
            builder.field(PARENT_URL, parentUrl);
        }
        if (status != null) {
            builder.field(STATUS, status);
        }
        if (httpStatusCode != null) {
            builder.field(HTTP_STATUS_CODE, httpStatusCode);
        }
        if (method != null) {
            builder.field(METHOD, method);
        }
        if (mimeType != null) {
            builder.field(MIME_TYPE, mimeType);
        }
        if (createTime != null) {
            builder.field(CREATE_TIME, createTime);
        }
        if (executionTime != null) {
            builder.field(EXECUTION_TIME, executionTime);
        }
        if (contentLength != null) {
            builder.field(CONTENT_LENGTH, contentLength);
        }
        if (lastModified != null) {
            builder.field(LAST_MODIFIED, lastModified);
        }
        if (accessResultData instanceof ToXContent) {
            builder.field(ACCESS_RESULT_DATA);
            ((ToXContent) accessResultData).toXContent(builder, params);
        }
        builder.endObject();
        return builder;
    }
}
