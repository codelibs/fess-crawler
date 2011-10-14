@echo off

set ANT_OPTS=-Xmx256M

set MY_PROJECT_NAME=robot

set DBFLUTE_HOME=..\mydbflute\dbflute-0.9.7.9

if "%pause_at_end%"=="" set pause_at_end=y
