/*
 [df:title]
 Delete UrlQueue by SessionID
 
 [df:description]
 Delete UrlQueue by SessionID
*/
-- #df:x#

-- !df:pmb!
-- !!AutoDetect!!

delete from URL_QUEUE
/*BEGIN*/where 
	/*IF pmb.sessionId != null*/SESSION_ID = /*pmb.sessionId*/'20090704161034370'/*END*/
/*END*/
