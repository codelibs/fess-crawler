package org.codelibs.robot.container;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.function.Consumer;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.codelibs.core.beans.BeanDesc;
import org.codelibs.core.beans.FieldDesc;
import org.codelibs.core.beans.MethodDesc;
import org.codelibs.core.beans.factory.BeanDescFactory;
import org.codelibs.core.lang.ClassUtil;
import org.codelibs.core.lang.FieldUtil;
import org.codelibs.core.lang.MethodUtil;
import org.jboss.netty.util.internal.ConcurrentHashMap;

public class SimpleComponentContainer implements ComponentContainer {

    private final Map<String, Object> singletonNameMap = new ConcurrentHashMap<>();

    private final Map<String, ComponentDef<?>> prototypeNameMap = new ConcurrentHashMap<>();

    @Override
    public <T> T getComponent(final String name) {
        if ("componentContainer".equals(name)) {
            @SuppressWarnings("unchecked")
            final T t = (T) this;
            return t;
        }

        final ComponentDef<?> componentDef = prototypeNameMap.get(name);
        if (componentDef != null) {
            @SuppressWarnings("unchecked")
            final T instance = (T) componentDef.get();
            return instance;
        }
        final Object object = singletonNameMap.get(name);
        if (object != null) {
            @SuppressWarnings("unchecked")
            final T instance = (T) object;
            return instance;
        }
        return null;
    }

    @Override
    public boolean available() {
        return true;
    }

    public <T> SimpleComponentContainer prototype(final String name,
            final Class<T> cls, final Consumer<T> component) {
        prototypeNameMap.put(name, new ComponentDef<>(cls, component, this));
        return this;
    }

    public <T> SimpleComponentContainer prototype(final String name,
            final Class<T> cls) {
        return prototype(name, cls, null);
    }

    public <T> SimpleComponentContainer singleton(final String name,
            final Class<T> cls, final Consumer<T> component) {
        final ComponentDef<T> componentDef = new ComponentDef<>(cls, component,
                this);
        final T instance = componentDef.get();
        singletonNameMap.put(name, instance);
        return this;
    }

    public <T> SimpleComponentContainer singleton(final String name,
            final Class<T> cls) {
        return singleton(name, cls, (Consumer<T>) null);
    }

    public <T> SimpleComponentContainer singleton(final String name,
            final T instance, final Consumer<T> component) {
        final ComponentDef<T> componentDef = new ComponentDef<>(instance,
                component, this);
        singletonNameMap.put(name, componentDef.get());
        return this;
    }

    public <T> SimpleComponentContainer singleton(final String name,
            final T instance) {
        return singleton(name, instance, null);
    }

    protected static class ComponentDef<T> {
        protected Class<T> cls;

        protected Consumer<T> initializer;

        protected SimpleComponentContainer container;

        private T instance;

        protected ComponentDef(final Class<T> cls,
                final Consumer<T> initializer,
                final SimpleComponentContainer container) {
            this.cls = cls;
            this.initializer = initializer;
            this.container = container;
        }

        protected ComponentDef(final T instance, final Consumer<T> initializer,
                final SimpleComponentContainer container) {
            this.instance = instance;
            this.initializer = initializer;
            this.container = container;
        }

        T get() {
            final T component = this.instance == null ? ClassUtil
                    .newInstance(cls) : this.instance;
            final BeanDesc beanDesc = BeanDescFactory.getBeanDesc(component
                    .getClass());
            for (final FieldDesc fieldDesc : beanDesc.getFieldDescs()) {
                final Resource annotation = fieldDesc.getField().getAnnotation(
                        Resource.class);
                if (annotation != null) {
                    final Object injected = container.getComponent(fieldDesc
                            .getFieldName());
                    if (injected != null) {
                        FieldUtil
                                .set(fieldDesc.getField(), component, injected);
                    }
                }
            }

            for (final String methodName : beanDesc.getMethodNames()) {
                final MethodDesc methodDesc = beanDesc
                        .getMethodDescNoException(methodName, new Class[0]);
                if (methodDesc != null) {
                    final Method method = methodDesc.getMethod();
                    final PostConstruct postConstruct = method
                            .getAnnotation(PostConstruct.class);
                    if (postConstruct != null) {
                        MethodUtil.invoke(method, component, new Object[0]);
                    }
                }
            }
            if (initializer != null) {
                initializer.accept(component);
            }
            return component;
        }
    }
}
