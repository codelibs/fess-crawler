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
package org.codelibs.fess.crawler.util;

import org.codelibs.fess.crawler.entity.AccessResult;

/**
 * A callback interface for processing access results.
 *
 * @param <RESULT> the type of access result that extends {@link AccessResult}
 */
public interface AccessResultCallback<RESULT extends AccessResult<?>> {
    /**
     * Processes the given access result.
     *
     * @param accessResult the result of the access operation to be processed
     */
    void iterate(RESULT accessResult);
}
