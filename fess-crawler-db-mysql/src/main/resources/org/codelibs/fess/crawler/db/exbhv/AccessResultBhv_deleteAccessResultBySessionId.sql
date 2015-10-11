/*
 [df:title]
 Delete AccessResult by SessionID
 
 [df:description]
 Delete AccessResult by SessionID
*/
-- #df:x#

-- !df:pmb!
-- !!AutoDetect!!
delete from ACCESS_RESULT
/*BEGIN*/where 
	/*IF pmb.sessionId != null*/SESSION_ID = /*pmb.sessionId*/'20090704161034370'/*END*/
/*END*/
