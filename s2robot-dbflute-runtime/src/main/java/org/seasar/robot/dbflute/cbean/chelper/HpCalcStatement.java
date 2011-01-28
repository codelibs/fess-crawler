package org.seasar.robot.dbflute.cbean.chelper;

/**
 * @author jflute
 */
public interface HpCalcStatement {

    /**
     * Build the calculation statement of the column as SQL name.
     * @return The statement that has calculation. (NullAllowed: if null, means the column is not specified)
     */
    String buildStatementAsSqlName();

    /**
     * Build the calculation statement of the column as real name.
     * @return The statement that has calculation. (NullAllowed: if null, means the column is not specified)
     */
    String buildStatementAsRealName();
}
