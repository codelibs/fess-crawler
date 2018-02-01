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
package org.codelibs.fess.crawler.log;

/**
 * @author shinsuke
 *
 */
public enum LogType {
    START_CRAWLING, //
    UNSUPPORTED_URL_AT_CRAWLING_STARTED, //
    CHECK_LAST_MODIFIED, //
    NOT_MODIFIED, //
    GET_CONTENT, //
    REDIRECT_LOCATION, //
    PROCESS_RESPONSE, //
    FINISHED_CRAWLING, //
    PROCESS_CHILD_URLS_BY_EXCEPTION, //
    PROCESS_CHILD_URL_BY_EXCEPTION, //
    CRAWLING_ACCESS_EXCEPTION, //
    CRAWLING_EXCETPION, //
    NO_URL_IN_QUEUE, //
    START_THREAD, //
    FINISHED_THREAD, //
    NO_RESPONSE_PROCESSOR, //
    NO_RULE, //
    SYSTEM_ERROR
}
