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
package org.codelibs.fess.crawler.builder;

import org.codelibs.fess.crawler.Constants;
import org.codelibs.fess.crawler.entity.RequestData;
import org.codelibs.fess.crawler.entity.RequestData.Method;

/**
 * Builder class for creating RequestData objects using a fluent interface.
 * This class provides a simple way to construct RequestData objects with method chaining.
 *
 * <p>Usage example:</p>
 * <pre>
 * RequestData request = RequestDataBuilder.newRequestData()
 *     .method("GET")
 *     .url("https://example.com")
 *     .weight(1.0f)
 *     .build();
 * </pre>
 *
 * <p>The builder is implemented using an inner class {@link RequestDataContext} that handles
 * the actual construction of the RequestData object.</p>
 *
 */
public final class RequestDataBuilder {
    private RequestDataBuilder() {
    }

    /**
     * Creates a new RequestDataContext for building RequestData instances.
     *
     * @return a new RequestDataContext instance
     */
    public static RequestDataContext newRequestData() {
        return new RequestDataContext();
    }

    /**
     * Context class for building RequestData instances using a fluent API.
     */
    public static class RequestDataContext {
        private final RequestData data;

        private RequestDataContext() {
            data = new RequestData();
        }

        /**
         * Sets the HTTP method for the request.
         *
         * @param method the HTTP method (GET, POST, HEAD)
         * @return this RequestDataContext for method chaining
         */
        public RequestDataContext method(final String method) {
            if (Constants.GET_METHOD.equalsIgnoreCase(method)) {
                return get();
            }
            if (Constants.HEAD_METHOD.equalsIgnoreCase(method)) {
                return head();
            }
            if (Constants.POST_METHOD.equalsIgnoreCase(method)) {
                return post();
            }
            return get();
        }

        /**
         * Sets the HTTP method for the request.
         * @param method The HTTP method.
         * @return The current RequestDataContext instance.
         */
        public RequestDataContext method(final Method method) {
            data.setMethod(method);
            return this;
        }

        /**
         * Sets the HTTP method to GET.
         * @return The current RequestDataContext instance.
         */
        public RequestDataContext get() {
            return method(Method.GET);
        }

        /**
         * Sets the HTTP method to HEAD.
         * @return The current RequestDataContext instance.
         */
        public RequestDataContext head() {
            return method(Method.HEAD);
        }

        /**
         * Sets the HTTP method to POST.
         * @return The current RequestDataContext instance.
         */
        public RequestDataContext post() {
            return method(Method.POST);
        }

        /**
         * Sets the URL for this request data.
         *
         * @param url the URL string to be set
         * @return the current RequestDataContext instance for method chaining
         */
        public RequestDataContext url(final String url) {
            data.setUrl(url);
            return this;
        }

        /**
         * Sets the weight for the request data.
         *
         * @param weight the weight to set
         * @return the current RequestDataContext instance
         */
        public RequestDataContext weight(final float weight) {
            data.setWeight(weight);
            return this;
        }

        /**
         * Builds and returns the constructed RequestData object.
         *
         * @return the constructed RequestData object
         */
        public RequestData build() {
            return data;
        }
    }

}
