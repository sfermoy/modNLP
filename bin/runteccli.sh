#!/bin/bash

usage() 
{
    echo "Usage:";
    echo "$0: [ host:port | -h ]\n";
}

if [ "$1" = "-h" ]; then
  usage
  exit
fi

if [ "$1" = "--help" ]; then
  usage
  exit
fi



if [ "$#" -gt "0" ]; then
  if [ "$#" -lt "4" ]; then
  usage
  exit
  fi
fi
TEC_BIN=`dirname $0`
cd $TEC_BIN

java -Xms400m -Xmx500m -jar teccli.jar

STATUS="${?}"
echo "teccli exit status = $STATUS"
echo "Press [ENTER] to continue..."
exit $STATUS
