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
package org.codelibs.fess.crawler.log;

/**
 * LogType defines the different types of log messages that can be generated during the crawling process.
 * Each enum constant represents a specific event or state in the crawler's execution.
 */
public enum LogType {
    /** Indicates the start of a crawling process. */
    START_CRAWLING,
    /** Indicates the cleanup phase of crawling. */
    CLEANUP_CRAWLING,
    /** Indicates an unsupported URL was encountered when crawling started. */
    UNSUPPORTED_URL_AT_CRAWLING_STARTED,
    /** Indicates checking the last modified date of a resource. */
    CHECK_LAST_MODIFIED,
    /** Indicates the resource has not been modified. */
    NOT_MODIFIED,
    /** Indicates getting content from a resource. */
    GET_CONTENT,
    /** Indicates a redirect location was found. */
    REDIRECT_LOCATION,
    /** Indicates processing a response. */
    PROCESS_RESPONSE,
    /** Indicates the crawling process has finished. */
    FINISHED_CRAWLING,
    /** Indicates processing child URLs due to an exception. */
    PROCESS_CHILD_URLS_BY_EXCEPTION,
    /** Indicates processing a child URL due to an exception. */
    PROCESS_CHILD_URL_BY_EXCEPTION,
    /** Indicates an access exception during crawling. */
    CRAWLING_ACCESS_EXCEPTION,
    /** Indicates a general exception during crawling. */
    CRAWLING_EXCEPTION,
    /** Indicates no URL is available in the queue. */
    NO_URL_IN_QUEUE,
    /** Indicates the start of a crawler thread. */
    START_THREAD,
    /** Indicates the finish of a crawler thread. */
    FINISHED_THREAD,
    /** Indicates no response processor is available. */
    NO_RESPONSE_PROCESSOR,
    /** Indicates no rule is available for processing. */
    NO_RULE,
    /** Indicates a system error occurred. */
    SYSTEM_ERROR
}
