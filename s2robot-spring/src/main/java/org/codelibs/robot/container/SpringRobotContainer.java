/*
 * Copyright 2012-2015 CodeLibs Project and the Others.
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
package org.codelibs.robot.container;

import javax.annotation.Resource;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;

public class SpringRobotContainer implements RobotContainer {

    @Resource
    protected ConfigurableApplicationContext context;

    public static RobotContainer create(String... resourceLocations) {
        @SuppressWarnings("resource")
        GenericXmlApplicationContext appContext = new GenericXmlApplicationContext(
                resourceLocations);
        return (RobotContainer) appContext.getBean("robotContainer");
    }

    @Override
    public <T> T getComponent(String name) {
        @SuppressWarnings("unchecked")
        T bean = (T) context.getBean(name);
        return bean;
    }

    @Override
    public boolean available() {
        return context.isActive();
    }

    @Override
    public void destroy() {
        context.close();
    }

    public ConfigurableApplicationContext getApplicationContext() {
        return context;
    }

}
