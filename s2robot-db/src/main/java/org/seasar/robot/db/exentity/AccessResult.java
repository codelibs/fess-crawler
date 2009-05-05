package org.seasar.robot.db.exentity;

import java.sql.Timestamp;
import java.util.Date;

import org.seasar.framework.beans.util.Beans;
import org.seasar.robot.Constants;
import org.seasar.robot.entity.ResponseData;
import org.seasar.robot.entity.ResultData;

/**
 * The entity of ACCESS_RESULT.
 * <p>
 * You can implement your original methods here.
 * This class remains when re-generating.
 * </p>
 * @author DBFlute(AutoGenerator)
 */
public class AccessResult extends org.seasar.robot.db.bsentity.BsAccessResult
        implements org.seasar.robot.entity.AccessResult {

    /** Serial version UID. (Default) */
    private static final long serialVersionUID = 1L;

    public AccessResult() {
        super();
    }

    public void init(ResponseData responseData, ResultData resultData) {

        setCreateTime(new Timestamp(new Date().getTime())); // TODO response time
        Beans.copy(responseData, this).execute();
        setStatus(Constants.OK_STATUS);

        AccessResultData accessResultData = new AccessResultData();
        Beans.copy(resultData, accessResultData).execute();
        setAccessResultDataAsOne(accessResultData);
    }

    /* (non-Javadoc)
     * @see org.seasar.robot.entity.AccessResult#getAccessResultData()
     */
    public AccessResultData getAccessResultData() {
        return getAccessResultDataAsOne();
    }

    /* (non-Javadoc)
     * @see org.seasar.robot.entity.AccessResult#setAccessResultData(org.seasar.robot.db.exentity.AccessResultData)
     */
    public void setAccessResultData(
            org.seasar.robot.entity.AccessResultData accessResultData) {
        setAccessResultDataAsOne((AccessResultData) accessResultData);
    }

}
