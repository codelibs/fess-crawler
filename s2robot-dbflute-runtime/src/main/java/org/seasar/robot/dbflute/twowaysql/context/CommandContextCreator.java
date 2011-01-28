/*
 * Copyright 2004-2011 the Seasar Foundation and the Others.
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
package org.seasar.robot.dbflute.twowaysql.context;

import org.seasar.robot.dbflute.twowaysql.context.impl.CommandContextImpl;

/**
 * @author jflute
 */
public class CommandContextCreator {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected String[] argNames;
    protected Class<?>[] argTypes;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public CommandContextCreator(String[] argNames, Class<?>[] argTypes) {
        this.argNames = (argNames != null ? argNames : new String[0]);
        this.argTypes = (argTypes != null ? argTypes : new Class[0]);
    }

    // ===================================================================================
    //                                                                              Create
    //                                                                              ======
    /**
     * Create the command context as root context.
     * @param args The array of arguments. (NullAllowed)
     * @return The command context as root context. (NotNull)
     */
    public CommandContext createCommandContext(Object[] args) {
        final CommandContext ctx = CommandContextImpl.createCommandContextImplAsRoot();
        if (args != null) {
            for (int i = 0; i < args.length; ++i) {
                Class<?> argType = null;
                if (args[i] != null) {
                    if (i < argTypes.length) {
                        argType = argTypes[i];
                    } else if (args[i] != null) {
                        argType = args[i].getClass();
                    }
                }
                if (i < argNames.length) {
                    ctx.addArg(argNames[i], args[i], argType);
                } else {
                    ctx.addArg("$" + (i + 1), args[i], argType);
                }
            }
        }
        return ctx;
    }
}
