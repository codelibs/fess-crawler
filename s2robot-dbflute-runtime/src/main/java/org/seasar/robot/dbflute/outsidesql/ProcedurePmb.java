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
package org.seasar.robot.dbflute.outsidesql;

import org.seasar.robot.dbflute.twowaysql.pmbean.ParameterBean;

/**
 * The parameter-bean for procedure.
 * @author jflute
 */
public interface ProcedurePmb extends ParameterBean {

    /**
     * Get the value of procedure name for calling.
     * @return The string expression of procedure name. (NotNull)
     */
    String getProcedureName();

    /**
     * Does it escape the procedure statement? <br />
     * If true, 'call SP_FOO()' to '{call = SP_FOO()}'. <br />
     * This default value should be true basically.
     * @return Determination.
     */
    boolean isEscapeStatement();

    /**
     * Does it call the procedure statement by select statement? <br />
     * If true, '{call SP_FOO()}' to 'select * from SP_FOO()'. <br />
     * This default value is resolved by generator automatically.
     * @return Determination.
     */
    boolean isCalledBySelect();
}
