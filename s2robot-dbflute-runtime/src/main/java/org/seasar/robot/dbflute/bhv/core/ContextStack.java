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
package org.seasar.robot.dbflute.bhv.core;

import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.Map.Entry;

import org.seasar.robot.dbflute.Entity;
import org.seasar.robot.dbflute.cbean.ConditionBean;
import org.seasar.robot.dbflute.cbean.ConditionBeanContext;
import org.seasar.robot.dbflute.cbean.EntityRowHandler;
import org.seasar.robot.dbflute.cbean.FetchAssistContext;
import org.seasar.robot.dbflute.jdbc.FetchBean;
import org.seasar.robot.dbflute.outsidesql.OutsideSqlContext;
import org.seasar.robot.dbflute.resource.InternalMapContext;
import org.seasar.robot.dbflute.resource.ResourceContext;

/**
 * The context stack for all context.
 * @author jflute
 * @since 0.9.6.5 (2010/02/05 Friday)
 */
public class ContextStack {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    private static ThreadLocal<Stack<ContextStack>> _threadLocal = new ThreadLocal<Stack<ContextStack>>();

    // ===================================================================================
    //                                                                       Context Stack
    //                                                                       =============
    /**
     * Get context-stack on thread.
     * @return The instance of context-stack. (NullAllowed)
     */
    public static Stack<ContextStack> getContextStackOnThread() {
        return _threadLocal.get();
    }

    /**
     * Is existing context-stack on thread?
     * @return Determination.
     */
    public static boolean isExistContextStackOnThread() {
        return (_threadLocal.get() != null);
    }

    /**
     * Clear context-stack on thread.
     */
    public static void clearContextStackOnThread() {
        _threadLocal.set(null);
    }

    // ===================================================================================
    //                                                                All Context Handling
    //                                                                ====================
    public static void saveAllContextOnThread() {
        if (!isExistContextStackOnThread()) {
            _threadLocal.set(new Stack<ContextStack>());
        }
        final ContextStack contextStack = new ContextStack();
        if (ConditionBeanContext.isExistConditionBeanOnThread()) {
            contextStack.setConditionBean(ConditionBeanContext.getConditionBeanOnThread());
        }
        if (ConditionBeanContext.isExistEntityRowHandlerOnThread()) {
            contextStack.setEntityRowHandler(ConditionBeanContext.getEntityRowHandlerOnThread());
        }
        if (OutsideSqlContext.isExistOutsideSqlContextOnThread()) {
            contextStack.setOutsideSqlContext(OutsideSqlContext.getOutsideSqlContextOnThread());
        }
        if (FetchAssistContext.isExistFetchBeanOnThread()) {
            contextStack.setFetchBean(FetchAssistContext.getFetchBeanOnThread());
        }
        if (InternalMapContext.isExistInternalMapContextOnThread()) {
            contextStack.setInternalMap(InternalMapContext.getInternalMap());
        }
        if (ResourceContext.isExistResourceContextOnThread()) {
            contextStack.setResourceContext(ResourceContext.getResourceContextOnThread());
        }
        getContextStackOnThread().push(contextStack);
    }

    public static void restoreAllContextOnThreadIfExists() {
        if (!isExistContextStackOnThread()) {
            return;
        }
        final Stack<ContextStack> stackOnThread = getContextStackOnThread();
        if (stackOnThread.isEmpty()) {
            clearContextStackOnThread();
            return;
        }
        final ContextStack contextStack = stackOnThread.pop();
        final ConditionBean cb = contextStack.getConditionBean();
        if (cb != null) {
            ConditionBeanContext.setConditionBeanOnThread(cb);
        }
        final EntityRowHandler<? extends Entity> entityRowHandler = contextStack.getEntityRowHandler();
        if (entityRowHandler != null) {
            ConditionBeanContext.setEntityRowHandlerOnThread(entityRowHandler);
        }
        final OutsideSqlContext outsideSqlContext = contextStack.getOutsideSqlContext();
        if (outsideSqlContext != null) {
            OutsideSqlContext.setOutsideSqlContextOnThread(outsideSqlContext);
        }
        final FetchBean fetchBean = contextStack.getFetchBean();
        if (fetchBean != null) {
            FetchAssistContext.setFetchBeanOnThread(fetchBean);
        }
        final Map<String, Object> internalMap = contextStack.getInternalMap();
        if (internalMap != null) {
            InternalMapContext.clearInternalMapContextOnThread();
            final Set<Entry<String, Object>> entrySet = internalMap.entrySet();
            for (Entry<String, Object> entry : entrySet) {
                InternalMapContext.setObject(entry.getKey(), entry.getValue());
            }
        }
        final ResourceContext resourceContext = contextStack.getResourceContext();
        if (resourceContext != null) {
            ResourceContext.setResourceContextOnThread(resourceContext);
        }
    }

    public static void clearAllCurrentContext() {
        if (ConditionBeanContext.isExistConditionBeanOnThread()) {
            ConditionBeanContext.clearConditionBeanOnThread();
        }
        if (ConditionBeanContext.isExistEntityRowHandlerOnThread()) {
            ConditionBeanContext.clearEntityRowHandlerOnThread();
        }
        if (OutsideSqlContext.isExistOutsideSqlContextOnThread()) {
            OutsideSqlContext.clearOutsideSqlContextOnThread();
        }
        if (FetchAssistContext.isExistFetchBeanOnThread()) {
            FetchAssistContext.clearFetchBeanOnThread();
        }
        if (InternalMapContext.isExistInternalMapContextOnThread()) {
            InternalMapContext.clearInternalMapContextOnThread();
        }
        if (ResourceContext.isExistResourceContextOnThread()) {
            ResourceContext.clearResourceContextOnThread();
        }
    }

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    private ConditionBean _conditionBean;
    private EntityRowHandler<? extends Entity> _entityRowHandler;
    private OutsideSqlContext _outsideSqlContext;
    private FetchBean _fetchBean;
    private Map<String, Object> _internalMap;
    private ResourceContext _resourceContext;

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public ConditionBean getConditionBean() {
        return _conditionBean;
    }

    public void setConditionBean(ConditionBean conditionBean) {
        this._conditionBean = conditionBean;
    }

    public EntityRowHandler<? extends Entity> getEntityRowHandler() {
        return _entityRowHandler;
    }

    public void setEntityRowHandler(EntityRowHandler<? extends Entity> entityRowHandler) {
        this._entityRowHandler = entityRowHandler;
    }

    public OutsideSqlContext getOutsideSqlContext() {
        return _outsideSqlContext;
    }

    public void setOutsideSqlContext(OutsideSqlContext outsideSqlContext) {
        this._outsideSqlContext = outsideSqlContext;
    }

    public FetchBean getFetchBean() {
        return _fetchBean;
    }

    public void setFetchBean(FetchBean fetchBean) {
        this._fetchBean = fetchBean;
    }

    public Map<String, Object> getInternalMap() {
        return _internalMap;
    }

    public void setInternalMap(Map<String, Object> internalMap) {
        this._internalMap = internalMap;
    }

    public ResourceContext getResourceContext() {
        return _resourceContext;
    }

    public void setResourceContext(ResourceContext resourceContext) {
        this._resourceContext = resourceContext;
    }
}
