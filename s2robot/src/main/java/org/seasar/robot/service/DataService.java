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
package org.seasar.robot.service;

import java.util.List;

import org.seasar.robot.entity.AccessResult;
import org.seasar.robot.util.AccessResultCallback;

/**
 * @author shinsuke
 *
 */
public interface DataService {

    public abstract void store(AccessResult accessResult);

    public abstract int getCount(String sessionId);

    public abstract void delete(String sessionId);

    public abstract void deleteAll();

    public abstract AccessResult getAccessResult(String sessionId, String url);

    public abstract List<AccessResult> getAccessResultList(String url,
            boolean hasData);

    public abstract void iterate(String sessionId,
            final AccessResultCallback accessResultCallback);

}