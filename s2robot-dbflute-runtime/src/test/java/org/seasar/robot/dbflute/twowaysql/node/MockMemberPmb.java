package org.seasar.robot.dbflute.twowaysql.node;

import java.util.List;

/**
 * @author jflute
 */
public class MockMemberPmb {

    protected Integer _memberId;
    protected String _memberName;
    protected List<Integer> _memberIdList;
    protected List<String> _memberNameList;
    protected String[] _memberNames;

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

    public List<Integer> getMemberIdList() {
        return _memberIdList;
    }

    public void setMemberIdList(List<Integer> memberIdList) {
        this._memberIdList = memberIdList;
    }

    public List<String> getMemberNameList() {
        return _memberNameList;
    }

    public void setMemberNameList(List<String> memberNameList) {
        this._memberNameList = memberNameList;
    }

    public String[] getMemberNames() {
        return _memberNames;
    }

    public void setMemberNames(String[] emberNames) {
        this._memberNames = emberNames;
    }
}
