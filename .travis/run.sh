#!/bin/bash

cd `dirname $0`
cd ..
BASE_DIR=`pwd`
LOG_FILE=$BASE_DIR/test.log

mvn test > $LOG_FILE 2>&1
ret=$?

if [ $ret != 0 ] ; then
  for f in `find $BASE_DIR -type f | grep surefire-reports | grep -v /TEST-` ; do
    cat $f
  done
fi

tail -n1000 $LOG_FILE
exit $ret
