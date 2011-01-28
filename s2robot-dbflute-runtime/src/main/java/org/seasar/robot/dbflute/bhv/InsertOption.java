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
package org.seasar.robot.dbflute.bhv;

import org.seasar.robot.dbflute.cbean.ConditionBean;
import org.seasar.robot.dbflute.util.DfTypeUtil;

/**
 * The option of insert for varying-insert.
 * @author jflute
 * @since 0.9.7.8 (2010/12/16 Thursday)
 * @param <CB> The type of condition-bean for specification.
 */
public class InsertOption<CB extends ConditionBean> implements WritableOption<CB> {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    public static final Long VERSION_NO_FIRST_VALUE = 0L;

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected boolean _disableCommonColumnAutoSetup;
    protected boolean _disablePrimaryKeyIdentity;
    protected Integer _batchInsertLoggingLimit;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    /**
     * Constructor.
     * <pre>
     * Member member = new Member();
     * member.set...(value);
     * InsertOption&lt;MemberCB&gt; option = <span style="color: #FD4747">new InsertOption&lt;MemberCB&gt;()</span>;
     * 
     * <span style="color: #3F7E5E">// ex) you can insert by your values for common columns</span>
     * option.<span style="color: #FD4747">disableCommonColumnAutoSetup</span>();
     * 
     * <span style="color: #3F7E5E">// ex) you can insert by your values for primary key</span>
     * option.<span style="color: #FD4747">disablePrimaryKeyIdentity</span>();
     * 
     * memberBhv.<span style="color: #FD4747">varyingInsert</span>(member, option);
     * </pre>
     */
    public InsertOption() {
    }

    // ===================================================================================
    //                                                                       Common Column
    //                                                                       =============
    /**
     * Disable auto-setup for common columns. <br />
     * You can insert by your values for common columns.
     * <pre>
     * Member member = new Member();
     * member.setOthers...(value);
     * member.setRegisterDatetime(registerDatetime);
     * member.setRegisterUser(registerUser);
     * member.setUpdateDatetime(updateDatetime);
     * member.setUpdateUser(updateUser);
     * InsertOption&lt;MemberCB&gt; option = new InsertOption&lt;MemberCB&gt;();
     * option.<span style="color: #FD4747">disableCommonColumnAutoSetup</span>();
     * memberBhv.varyingInsert(member, option);
     * </pre>
     * @return The option of insert. (NotNull: returns this)
     */
    public InsertOption<CB> disableCommonColumnAutoSetup() {
        _disableCommonColumnAutoSetup = true;
        return this;
    }

    public boolean isCommonColumnAutoSetupDisabled() {
        return _disableCommonColumnAutoSetup;
    }

    // ===================================================================================
    //                                                                     Identity Insert
    //                                                                     ===============
    /**
     * Disable identity for primary key. <br />
     * you can insert by your value for primary key.
     * <pre>
     * Member member = new Member();
     * member.setMemberId(123); <span style="color: #3F7E5E">// instead of identity</span>
     * member.setOthers...(value);
     * InsertOption&lt;MemberCB&gt; option = new InsertOption&lt;MemberCB&gt;();
     * option.<span style="color: #FD4747">disablePrimaryKeyIdentity</span>();
     * memberBhv.varyingInsert(member, option);
     * </pre>
     * @return The option of insert. (NotNull: returns this)
     */
    public InsertOption<CB> disablePrimaryKeyIdentity() {
        _disablePrimaryKeyIdentity = true;
        return this;
    }

    public boolean isPrimaryKeyIdentityDisabled() {
        return _disablePrimaryKeyIdentity;
    }

    // ===================================================================================
    //                                                                       Batch Logging
    //                                                                       =============
    /**
     * Limit batch-insert logging by logging size. <br />
     * For example, if you set 3, only 3 records are logged. <br />
     * This also works to SqlLogHandler's call-back and SqlResultInfo's displaySql.
     * @param batchInsertLoggingLimit The limit size of batch-insert logging. (NullAllowed: if null and minus, means no limit)
     */
    public void limitBatchInsertLogging(Integer batchInsertLoggingLimit) {
        this._batchInsertLoggingLimit = batchInsertLoggingLimit;
    }

    public Integer getBatchInsertLoggingLimit() {
        return _batchInsertLoggingLimit;
    }

    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        if (_disableCommonColumnAutoSetup) {
            sb.append("CommonColumnDisabled");
        }
        if (_disablePrimaryKeyIdentity) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append("PKIdentityDisabled");
        }
        if (sb.length() == 0) {
            sb.append("default");
        }
        return DfTypeUtil.toClassTitle(this) + ":{" + sb.toString() + "}";
    }
}