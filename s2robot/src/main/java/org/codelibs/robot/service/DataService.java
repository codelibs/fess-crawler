/*
 * Copyright 2004-2014 the Seasar Foundation and the Others.
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
package org.codelibs.robot.service;

import java.util.List;

import org.codelibs.robot.entity.AccessResult;
import org.codelibs.robot.util.AccessResultCallback;

/**
 * @author shinsuke
 * 
 */
public interface DataService {

    void store(AccessResult accessResult);

    void update(AccessResult accessResult);

    void update(List<AccessResult> accessResult);

    int getCount(String sessionId);

    void delete(String sessionId);

    void deleteAll();

    AccessResult getAccessResult(String sessionId, String url);

    List<AccessResult> getAccessResultList(String url, boolean hasData);

    void iterate(String sessionId,
            final AccessResultCallback accessResultCallback);

    void iterateUrlDiff(String oldSessionId, String newSessionId,
            final AccessResultCallback accessResultCallback);

}
