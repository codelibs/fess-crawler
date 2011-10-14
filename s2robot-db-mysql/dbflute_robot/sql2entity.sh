#!/bin/bash

cd `dirname $0`
. _project.sh

echo "/nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn"
echo "Specify the file path to be used as build-properties."
echo "nnnnnnnnnn/"
export MY_PROPERTIES_PATH=build.properties

echo "/nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn"
echo "Execute {Sql2Entity}."
echo "nnnnnnnnnn/"
sh $DBFLUTE_HOME/etc/cmd/_df-sql2entity.sh $MY_PROPERTIES_PATH $1


