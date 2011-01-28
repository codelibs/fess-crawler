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
package org.seasar.robot.dbflute.exception.thrower;

import java.util.List;

import org.seasar.robot.dbflute.Entity;
import org.seasar.robot.dbflute.bhv.DeleteOption;
import org.seasar.robot.dbflute.bhv.UpdateOption;
import org.seasar.robot.dbflute.bhv.WritableOption;
import org.seasar.robot.dbflute.cbean.ConditionBean;
import org.seasar.robot.dbflute.cbean.PagingBean;
import org.seasar.robot.dbflute.cbean.chelper.HpInvalidQueryInfo;
import org.seasar.robot.dbflute.exception.DangerousResultSizeException;
import org.seasar.robot.dbflute.exception.EntityAlreadyDeletedException;
import org.seasar.robot.dbflute.exception.EntityDuplicatedException;
import org.seasar.robot.dbflute.exception.NonQueryDeleteNotAllowedException;
import org.seasar.robot.dbflute.exception.NonQueryUpdateNotAllowedException;
import org.seasar.robot.dbflute.exception.OptimisticLockColumnValueNullException;
import org.seasar.robot.dbflute.exception.PagingCountSelectNotCountException;
import org.seasar.robot.dbflute.exception.SelectEntityConditionNotFoundException;
import org.seasar.robot.dbflute.exception.factory.ExceptionMessageBuilder;
import org.seasar.robot.dbflute.jdbc.FetchBean;
import org.seasar.robot.dbflute.util.DfSystemUtil;
import org.seasar.robot.dbflute.util.Srl;

/**
 * @author jflute
 */
public class BehaviorExceptionThrower {

    // ===================================================================================
    //                                                                              Select
    //                                                                              ======
    public void throwSelectEntityAlreadyDeletedException(Object searchKey) {
        final ExceptionMessageBuilder br = createExceptionMessageBuilder();
        br.addNotice("The entity was NOT found! it has already been deleted.");
        br.addItem("Advice");
        br.addElement("Please confirm the existence of your target record on your database.");
        br.addElement("Does the target record really created before this operation?");
        br.addElement("Has the target record been deleted by other thread?");
        br.addElement("It is precondition that the record exists on your database.");
        setupSearchKeyElement(br, searchKey);
        final String msg = br.buildExceptionMessage();
        throw new EntityAlreadyDeletedException(msg);
    }

    public void throwSelectEntityDuplicatedException(String resultCountExp, Object searchKey, Throwable cause) {
        final ExceptionMessageBuilder br = createExceptionMessageBuilder();
        br.addNotice("The entity was duplicated! It should be the only one.");
        br.addItem("Advice");
        br.addElement("Confirm your search condition. Does it really select the only one?");
        br.addElement("And confirm your database. Does it really exist the only one?");
        br.addItem("Result Count");
        br.addElement(resultCountExp);
        setupSearchKeyElement(br, searchKey);
        final String msg = br.buildExceptionMessage();
        if (cause != null) {
            throw new EntityDuplicatedException(msg, cause);
        } else {
            throw new EntityDuplicatedException(msg);
        }
    }

    protected void setupSearchKeyElement(ExceptionMessageBuilder br, Object searchKey) {
        if (searchKey != null && searchKey instanceof ConditionBean) {
            final ConditionBean cb = (ConditionBean) searchKey;
            setupInvalidQueryElement(br, cb);
            setupDisplaySqlElement(br, cb);
        } else {
            br.addItem("Search Condition");
            br.addElement(searchKey);
        }
    }

    public void throwSelectEntityConditionNotFoundException(ConditionBean cb) {
        final ExceptionMessageBuilder br = createExceptionMessageBuilder();
        br.addNotice("The condition for selecting an entity was not found!");
        br.addItem("Advice");
        br.addElement("Confirm your search condition. Does it really select the only one?");
        br.addElement("You have to set a valid query or fetch-first as 1.");
        br.addElement("For example:");
        br.addElement("  (x):");
        br.addElement("    MemberCB cb = MemberCB();");
        br.addElement("    ... = memberBhv.selectEntity(cb); // exception");
        br.addElement("  (x):");
        br.addElement("    MemberCB cb = MemberCB();");
        br.addElement("    cb.query().setMemberId_Equal(null);");
        br.addElement("    ... = memberBhv.selectEntity(cb); // exception");
        br.addElement("  (o):");
        br.addElement("    MemberCB cb = MemberCB();");
        br.addElement("    cb.query().setMemberId_Equal(3);");
        br.addElement("    ... = memberBhv.selectEntity(cb);");
        br.addElement("  (o):");
        br.addElement("    MemberCB cb = MemberCB();");
        br.addElement("    cb.fetchFirst(1);");
        br.addElement("    ... = memberBhv.selectEntity(cb);");
        setupInvalidQueryElement(br, cb);
        setupFetchSizeElement(br, cb);
        setupDisplaySqlElement(br, cb);
        final String msg = br.buildExceptionMessage();
        throw new SelectEntityConditionNotFoundException(msg);
    }

    public void throwDangerousResultSizeException(FetchBean fetchBean, Throwable cause) {
        final int safetyMaxResultSize = fetchBean.getSafetyMaxResultSize();
        final ExceptionMessageBuilder br = new ExceptionMessageBuilder();
        br.addNotice("You've already been in DANGER ZONE. (check is working)");
        br.addItem("Advice");
        br.addElement("The selected size is over the specified safety size.");
        br.addElement("Confirm your conditions and table records.");
        br.addItem("Safety Max Result Size");
        br.addElement(safetyMaxResultSize);
        if (fetchBean instanceof ConditionBean) {
            final ConditionBean cb = ((ConditionBean) fetchBean);
            setupInvalidQueryElement(br, cb);
            setupFetchSizeElement(br, cb);
            setupDisplaySqlElement(br, cb);
        } else {
            br.addItem("Fetch Bean");
            br.addElement(fetchBean);
        }
        final String msg = br.buildExceptionMessage();
        throw new DangerousResultSizeException(msg, cause, safetyMaxResultSize);
    }

    protected void setupInvalidQueryElement(ExceptionMessageBuilder br, ConditionBean cb) {
        br.addItem("Invalid Query");
        final List<HpInvalidQueryInfo> invalidQueryList = cb.getSqlClause().getInvalidQueryList();
        if (invalidQueryList != null && !invalidQueryList.isEmpty()) {
            for (HpInvalidQueryInfo invalidQueryInfo : invalidQueryList) {
                br.addElement(invalidQueryInfo.buildDisplay());
            }
        } else {
            br.addElement("*no invalid");
        }
    }

    protected void setupFetchSizeElement(ExceptionMessageBuilder br, ConditionBean cb) {
        br.addItem("Fetch Size");
        br.addElement(cb.getFetchSize());
    }

    protected void setupDisplaySqlElement(ExceptionMessageBuilder br, ConditionBean cb) {
        br.addItem("Display SQL");
        br.addElement(cb.toDisplaySql());
    }

    public <ENTITY> void throwPagingCountSelectNotCountException(String tableDbName, String path, PagingBean pmb,
            Class<ENTITY> entityType, EntityDuplicatedException e) { // for OutsideSql
        final ExceptionMessageBuilder br = new ExceptionMessageBuilder();
        br.addNotice("The count select for paging could not get a count.");
        br.addItem("Advice");
        br.addElement("A select clause of OutsideSql paging should be switchable like this:");
        br.addElement("For example:");
        br.addElement("  /*IF pmb.isPaging()*/");
        br.addElement("  select member.MEMBER_ID");
        br.addElement("       , member.MEMBER_NAME");
        br.addElement("       , ...");
        br.addElement("  -- ELSE select count(*)");
        br.addElement("  /*END*/");
        br.addElement("    from ...");
        br.addElement("   where ...");
        br.addElement("");
        br.addElement("This specificaton is for both ManualPaging and AutoPaging.");
        br.addElement("(AutoPaging is only allowed to omit a paging condition)");
        br.addItem("Table");
        br.addElement(tableDbName);
        br.addItem("OutsideSql");
        br.addElement(path);
        br.addItem("ParameterBean");
        br.addElement(pmb);
        br.addItem("Entity Type");
        br.addElement(entityType);
        final String msg = br.buildExceptionMessage();
        throw new PagingCountSelectNotCountException(msg, e);
    }

    // ===================================================================================
    //                                                                              Update
    //                                                                              ======
    public <ENTITY extends Entity> void throwUpdateEntityAlreadyDeletedException(ENTITY entity) {
        final ExceptionMessageBuilder br = createExceptionMessageBuilder();
        br.addNotice("The entity was not found! it has already been deleted.");
        setupEntityElement(br, entity);
        final String msg = br.buildExceptionMessage();
        throw new EntityAlreadyDeletedException(msg);
    }

    public <ENTITY extends Entity> void throwUpdateEntityDuplicatedException(ENTITY entity, int count) {
        final ExceptionMessageBuilder br = createExceptionMessageBuilder();
        br.addNotice("The entity was duplicated. It should be the only one!");
        br.addItem("Count");
        br.addElement(count);
        setupEntityElement(br, entity);
        final String msg = br.buildExceptionMessage();
        throw new EntityDuplicatedException(msg);
    }

    public void throwVersionNoValueNullException(Entity entity) {
        final ExceptionMessageBuilder br = createExceptionMessageBuilder();
        br.addNotice("Not found the value of 'version no' on the entity!");
        br.addItem("Advice");
        br.addElement("Please confirm the existence of the value of 'version no' on the entity.");
        br.addElement("You called the method in which the check for optimistic lock is indispensable.");
        br.addElement("So 'version no' is required on the entity. In addition, please confirm");
        br.addElement("the necessity of optimistic lock. It might possibly be unnecessary.");
        setupEntityElement(br, entity);
        final String msg = br.buildExceptionMessage();
        throw new OptimisticLockColumnValueNullException(msg);
    }

    public void throwUpdateDateValueNullException(Entity entity) {
        final ExceptionMessageBuilder br = createExceptionMessageBuilder();
        br.addNotice("Not found the value of 'update date' on the entity!");
        br.addItem("Advice");
        br.addElement("Please confirm the existence of the value of 'update date' on the entity.");
        br.addElement("You called the method in which the check for optimistic lock is indispensable.");
        br.addElement("So 'update date' is required on the entity. In addition, please confirm");
        br.addElement("the necessity of optimistic lock. It might possibly be unnecessary.");
        setupEntityElement(br, entity);
        final String msg = br.buildExceptionMessage();
        throw new OptimisticLockColumnValueNullException(msg);
    }

    public <ENTITY extends Entity> void throwNonQueryUpdateNotAllowedException(ENTITY entity, ConditionBean cb,
            UpdateOption<? extends ConditionBean> option) {
        final ExceptionMessageBuilder br = new ExceptionMessageBuilder();
        br.addNotice("The query-update without a query condition is not allowed.");
        br.addItem("Advice");
        br.addElement("Confirm your condition values for queryUpdate().");
        br.addElement("If you want to update all records, use varyingQueryUpdate().");
        setupEntityElement(br, entity);
        setupOptionElement(br, option);
        setupInvalidQueryElement(br, cb);
        final String msg = br.buildExceptionMessage();
        throw new NonQueryUpdateNotAllowedException(msg);
    }

    public <ENTITY extends Entity> void throwNonQueryDeleteNotAllowedException(ConditionBean cb,
            DeleteOption<? extends ConditionBean> option) {
        final ExceptionMessageBuilder br = new ExceptionMessageBuilder();
        br.addNotice("The query-delete without a query condition is not allowed.");
        br.addItem("Advice");
        br.addElement("Confirm your condition values for queryDelete().");
        br.addElement("If you want to delete all records, use varyingQueryDelete().");
        setupOptionElement(br, option);
        setupInvalidQueryElement(br, cb);
        final String msg = br.buildExceptionMessage();
        throw new NonQueryDeleteNotAllowedException(msg);
    }

    protected void setupEntityElement(ExceptionMessageBuilder br, Entity entity) {
        br.addItem("Entity");
        br.addElement(entity);
    }

    protected void setupOptionElement(ExceptionMessageBuilder br, WritableOption<? extends ConditionBean> option) {
        br.addItem("Option");
        br.addElement(option);
    }

    // ===================================================================================
    //                                                                      General Helper
    //                                                                      ==============
    /**
     * Get the value of line separator.
     * @return The value of line separator. (NotNull)
     */
    protected String ln() {
        return DfSystemUtil.getLineSeparator();
    }

    protected String initCap(String str) {
        return Srl.initCap(str);
    }

    protected String initUncap(String str) {
        return Srl.initUncap(str);
    }

    // ===================================================================================
    //                                                                    Exception Helper
    //                                                                    ================
    protected ExceptionMessageBuilder createExceptionMessageBuilder() {
        return new ExceptionMessageBuilder();
    }
}
