package org.seasar.robot.dbflute.twowaysql.node;

import org.seasar.robot.dbflute.cbean.coption.LikeSearchOption;

/**
 * @author jflute
 */
public class MockLikeSearchMemberPmb {

    protected Integer _memberId;
    protected String _memberName;
    protected LikeSearchOption _memberNameInternalLikeSearchOption;

    public Integer getMemberId() {
        return _memberId;
    }

    public void setMemberId(Integer memberId) {
        this._memberId = memberId;
    }

    public String getMemberName() {
        return _memberName;
    }

    public void setMemberName_PrefixSearch(String memberName) {
        this._memberName = memberName;
        this._memberNameInternalLikeSearchOption = new LikeSearchOption().likePrefix();
    }

    public LikeSearchOption getMemberNameInternalLikeSearchOption() {
        return _memberNameInternalLikeSearchOption;
    }
}
