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
package org.codelibs.fess.crawler.interval;

/**
 * The {@code IntervalController} interface defines methods for controlling
 * the delay intervals in a web crawler. It includes constants representing
 * different types of processing states and a method to introduce a delay
 * based on the type of processing.
 * <p>
 * Constants:
 * </p>
 * <ul>
 *   <li>{@code PRE_PROCESSING} - Represents the pre-processing state.</li>
 *   <li>{@code POST_PROCESSING} - Represents the post-processing state.</li>
 *   <li>{@code NO_URL_IN_QUEUE} - Indicates that there are no URLs in the queue.</li>
 *   <li>{@code WAIT_NEW_URL} - Indicates that the crawler is waiting for new URLs.</li>
 * </ul>
 */
public interface IntervalController {
    int PRE_PROCESSING = 1;

    int POST_PROCESSING = 2;

    int NO_URL_IN_QUEUE = 4;

    int WAIT_NEW_URL = 8;

    /**
     * Introduces a delay based on the specified type.
     *
     * @param type the type of delay to be introduced
     */
    void delay(int type);
}
