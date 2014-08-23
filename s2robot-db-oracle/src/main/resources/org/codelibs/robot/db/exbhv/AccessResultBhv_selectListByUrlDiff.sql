-- #AccessResultDiff#
-- +cursor+

-- !AccessResultPmb!
-- !!String newSessionId!!
-- !!String oldSessionId!!

select
    ar1.ID,
    ar1.SESSION_ID,
    ar1.RULE_ID,
    ar1.URL,
    ar1.PARENT_URL,
    ar1.STATUS,
    ar1.HTTP_STATUS_CODE,
    ar1.METHOD,
    ar1.MIME_TYPE,
    ar1.CONTENT_LENGTH,
    ar1.EXECUTION_TIME,
    ar1.CREATE_TIME
from
    ACCESS_RESULT ar1
where 
	ar1.URL not in (select ar2.URL from ACCESS_RESULT ar2 where /*IF pmb.oldSessionId != null*/ar2.SESSION_ID = /*pmb.oldSessionId*/'456'/*END*/)
    /*IF pmb.newSessionId != null*/and ar1.SESSION_ID = /*pmb.newSessionId*/'123'/*END*/
order by ar1.URL
