package org.seasar.robot.dbflute.twowaysql.node;

import org.seasar.robot.dbflute.cbean.SimplePagingBean;

/**
 * @author jflute
 */
public class MockPagingMemberPmb extends SimplePagingBean {

    private static final long serialVersionUID = 1L;
    protected Integer _memberId;
    protected String _memberName;

    public Integer getMemberId() {
        return _memberId;
    }

    public void setMemberId(Integer memberId) {
        this._memberId = memberId;
    }

    public String getMemberName() {
        return _memberName;
    }

    public void setMemberName(String memberName) {
        this._memberName = memberName;
    }
}
