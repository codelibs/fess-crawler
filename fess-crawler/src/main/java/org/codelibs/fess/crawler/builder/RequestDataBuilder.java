/*
 * Copyright 2012-2018 CodeLibs Project and the Others.
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
package org.codelibs.fess.crawler.builder;

import org.codelibs.fess.crawler.Constants;
import org.codelibs.fess.crawler.entity.RequestData;
import org.codelibs.fess.crawler.entity.RequestData.Method;

/**
 * Builder class to create a request.
 *
 * @author shinsuke
 *
 */
public final class RequestDataBuilder {
    private RequestDataBuilder() {
    }

    public static RequestDataContext newRequestData() {
        return new RequestDataContext();
    }

    public static class RequestDataContext {
        private final RequestData data;

        private RequestDataContext() {
            data = new RequestData();
        }

        public RequestDataContext method(final String method) {
            if (Constants.GET_METHOD.equalsIgnoreCase(method)) {
                return get();
            } else if (Constants.HEAD_METHOD.equalsIgnoreCase(method)) {
                return head();
            } else if (Constants.POST_METHOD.equalsIgnoreCase(method)) {
                return post();
            }
            return get();
        }

        public RequestDataContext method(final Method method) {
            data.setMethod(method);
            return this;
        }

        public RequestDataContext get() {
            return method(Method.GET);
        }

        public RequestDataContext head() {
            return method(Method.HEAD);
        }

        public RequestDataContext post() {
            return method(Method.POST);
        }

        public RequestDataContext url(final String url) {
            data.setUrl(url);
            return this;
        }

        public RequestDataContext metaData(final String metaData) {
            data.setMetaData(metaData);
            return this;
        }

        public RequestData build() {
            return data;
        }
    }

}
