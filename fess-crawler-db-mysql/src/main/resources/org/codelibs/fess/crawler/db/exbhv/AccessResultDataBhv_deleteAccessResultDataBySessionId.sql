/*
 [df:title]
 Delete AccessResultData by SessionID
 
 [df:description]
 Delete AccessResultData by SessionID
*/
-- #df:x#

-- !df:pmb!
-- !!AutoDetect!!
delete ACCESS_RESULT_DATA from ACCESS_RESULT_DATA
	inner join ACCESS_RESULT
	on ACCESS_RESULT_DATA.ID = ACCESS_RESULT.ID
/*BEGIN*/where 
	/*IF pmb.sessionId != null*/ACCESS_RESULT.SESSION_ID = /*pmb.sessionId*/'20090704161034370'/*END*/
/*END*/
