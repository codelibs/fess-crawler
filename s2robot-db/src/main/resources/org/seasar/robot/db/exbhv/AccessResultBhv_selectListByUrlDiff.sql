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
    ar1.EXECUTION_TIME,
    ar1.CREATE_TIME
from
    ACCESS_RESULT ar1
inner join
    ACCESS_RESULT ar2
on
    ar1.URL = ar2.URL
/*BEGIN*/where
    /*IF pmb.newSessionId != null*/ar1.SESSION_ID = /*pmb.newSessionId*/'123'/*END*/
    /*IF pmb.oldSessionId != null*/and ar2.SESSION_ID = /*pmb.oldSessionId*/'456'/*END*/
    and ar2.URL is NULL
/*END*/
order by ar1.URL
