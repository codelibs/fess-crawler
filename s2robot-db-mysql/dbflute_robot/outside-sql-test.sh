#!/bin/bash

cd `dirname $0`
. _project.sh

echo "/nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn"
echo "Specify the file path to be used as build-properties."
echo "nnnnnnnnnn/"
export MY_PROPERTIES_PATH=build.properties

echo "/nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn"
echo "Execute {Outsite-Sql-Test}."
echo "nnnnnnnnnn/"
sh $DBFLUTE_HOME/etc/cmd/_df-outside-sql-test.sh $MY_PROPERTIES_PATH $1


