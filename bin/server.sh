#!/bin/sh
# tecser.sh
# Created Wed Apr 22 2009 by S Luz luzs@cs.tcd.ie
# $Id$
# $Log$
TEC_BIN=`dirname $0`
cd $TEC_BIN
cd ../lib
java -Xms400m -jar tecser.jar
