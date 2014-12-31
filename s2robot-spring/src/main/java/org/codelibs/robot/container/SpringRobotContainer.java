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
