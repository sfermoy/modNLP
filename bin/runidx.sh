#!/bin/bash

usage() 
{
    echo "Usage:";
    echo "$0: [indexdir  filelist hdir hurl] [-v] [-d]\n";
    echo "   With no parameters, starts GUI for index maintainance,";
    echo "   otherwise run (de)indexer from the command line.\n";
    echo "  OPTIONS: ";
    echo "   indexdir: the directory where dictionary.properties lives";
    echo "             and the indices will be stored.";
    echo "   hdir: the directory where header files live";
    echo "   hurl: public URL for access to headers";
    echo "   filelist: list of files to be indexed/deindexed.";
    echo "   -v: verbose output.";
    echo "   -d: deindex files in filelist.";
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


cd ../lib
java -Xms400m -Xmx500m -cp .:idx.jar:antlr-2.7.6.jar:commons-pool-1.2.jar:exist-modules.jar:exist.jar:gnu-regexp.jar:idx.jar:je.jar:jgroups-all.jar:log4j-1.2.14.jar:resolver.jar:sunxacml.jar:xmldb.jar:xmlrpc-1.2-patched.jar modnlp.idx.IndexManager $@
