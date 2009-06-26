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
package org.seasar.robot.client.fs;

import java.util.Set;

import org.seasar.robot.RobotSystemException;

/**
 * @author shinsuke
 *
 */
public class ChildUrlsException extends RobotSystemException {
    private Set<String> childUrlList;

    public ChildUrlsException(Set<String> childUrlList) {
        super("Threw child urls(" + childUrlList.size() + ").");
        this.childUrlList = childUrlList;
    }

    public Set<String> getChildUrlList() {
        return childUrlList;
    }
}
