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
package org.seasar.robot.dbflute.twowaysql.context.impl;

import java.util.ArrayList;
import java.util.List;

import org.seasar.robot.dbflute.helper.StringKeyMap;
import org.seasar.robot.dbflute.twowaysql.context.CommandContext;

/**
 * @author jflute
 */
public class CommandContextImpl implements CommandContext {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** The arguments. it should be allowed null value. */
    private StringKeyMap<Object> args = StringKeyMap.createAsCaseInsensitive();

    /** The types of argument. it should be allowed null value. */
    private StringKeyMap<Class<?>> argTypes = StringKeyMap.createAsCaseInsensitive();

    private StringBuilder sqlSb = new StringBuilder(100);
    private List<Object> bindVariables = new ArrayList<Object>();
    private List<Class<?>> bindVariableTypes = new ArrayList<Class<?>>();

    // /- - - - - - - - - - - - - - - - - -
    // When this is the root context,
    // these boolean values are immutable.
    // - - - - - - - - - -/  

    private boolean enabled = true;
    private boolean beginChild;
    private boolean alreadySkippedPrefix;

    private CommandContext parent;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    /**
     * Constructor for root context.
     */
    private CommandContextImpl() {
    }

    /**
     * Constructor for child context.
     * @param parent The parent context. (NotNull)
     */
    private CommandContextImpl(CommandContext parent) {
        this.parent = parent;
        enabled = false;
    }

    /**
     * Create the implementation of command context as root.
     * @return The implementation of command context as root. (NotNull)
     */
    public static CommandContextImpl createCommandContextImplAsRoot() {
        return new CommandContextImpl();
    }

    /**
     * Create the implementation of command context as begin-child.
     * @param parent The parent context. (NotNull)
     * @return The implementation of command context as begin-child. (NotNull)
     */
    public static CommandContextImpl createCommandContextImplAsBeginChild(CommandContext parent) {
        return new CommandContextImpl(parent).asBeginChild();
    }

    private CommandContextImpl asBeginChild() {
        beginChild = true;
        return this;
    }

    // ===================================================================================
    //                                                                    Context Handling
    //                                                                    ================
    public Object getArg(String name) {
        if (args.containsKey(name)) {
            return args.get(name);
        } else if (parent != null) {
            return parent.getArg(name);
        } else {
            if (args.size() == 1) {
                String firstKey = args.keySet().iterator().next();
                return args.get(firstKey);
            }
            return null;
        }
    }

    public Class<?> getArgType(String name) {
        if (argTypes.containsKey(name)) {
            return (Class<?>) argTypes.get(name);
        } else if (parent != null) {
            return parent.getArgType(name);
        } else {
            if (argTypes.size() == 1) {
                String firstKey = argTypes.keySet().iterator().next();
                return argTypes.get(firstKey);
            }
            return null;
        }
    }

    public void addArg(String name, Object arg, Class<?> argType) {
        args.put(name, arg);
        argTypes.put(name, argType);
    }

    public String getSql() {
        return sqlSb.toString();
    }

    public Object[] getBindVariables() {
        return bindVariables.toArray(new Object[bindVariables.size()]);
    }

    public Class<?>[] getBindVariableTypes() {
        return (Class<?>[]) bindVariableTypes.toArray(new Class[bindVariableTypes.size()]);
    }

    public CommandContext addSql(String sql) {
        sqlSb.append(sql);
        return this;
    }

    public CommandContext addSql(String sql, Object bindVariable, Class<?> bindVariableType) {
        sqlSb.append(sql);
        bindVariables.add(bindVariable);
        bindVariableTypes.add(bindVariableType);
        return this;
    }

    public CommandContext addSql(String sql, Object[] bindVariables, Class<?>[] bindVariableTypes) {
        sqlSb.append(sql);
        for (int i = 0; i < bindVariables.length; ++i) {
            this.bindVariables.add(bindVariables[i]);
            this.bindVariableTypes.add(bindVariableTypes[i]);
        }
        return this;
    }

    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append(sqlSb).append(", ");
        sb.append(enabled).append(", ");
        sb.append(alreadySkippedPrefix).append(", ");
        sb.append("parent=").append(parent);
        sb.append("}@").append(Integer.toHexString(hashCode()));
        return sb.toString();
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isBeginChildContext() {
        return beginChild;
    }

    public boolean isAlreadySkippedPrefix() {
        return alreadySkippedPrefix;
    }

    public void setAlreadySkippedPrefix(boolean alreadySkippedPrefix) {
        this.alreadySkippedPrefix = alreadySkippedPrefix;
    }
}