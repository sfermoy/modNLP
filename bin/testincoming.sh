#!/bin/sh
# testincoming.sh
# Created Thu Sep  8 2011 by S Luz luzs@cs.tcd.ie
# $Id$
# $Log$
XML=`ls *.xml`
HED=`ls *.hed`
ERR=0
if [ "$1" == "--nodos2unix" ]
   then 
    echo "Skipping dos2unix conversion"
   else
    echo 'Converting to unix format'
    echo dos2unix $XML
    dos2unix $XML
    echo dos2unix $HED
    dos2unix $HED
fi
CWD=`dirname $0`
. $CWD/config.sh

echo 'Testing encoding'
for i in $XML ; do echo $i ; file -bi $i; done;
for i in $HED ; do echo $i ; file -bi $i; done;

echo 'Validating XML'
if xmllint -noout -valid $XML $HED 
then
    >&2 echo 'All files are valid XML'
else
    echo 'XML validation error (see above)'
    ERR=1
    #exit
fi

for i in $HED ; 
do 
    echo testing filename attribute of $i ;
    if ./testfilenameattribute.pl $i
    then
        echo $i matches specified fileaname;
    else
        >&2 echo "ERROR: $i does note match '<title ... filename=...>' specification";
        ERR=1
        #exit
    fi
    if [ -f $BASE/headers/$i ] ;
    then
        >&2 echo "ERROR: $i already indexed; it exists at $BASE/headers";
        ERR=1
    fi
    match=`echo $i| sed  -e 's/\.hed/\.xml/g' -`
    #echo testing if $match exists 
    if [ ! -f $match ] ;
    then
       >&2 echo "ERROR: I couldn't find a matching $match for $i";
       ERR=1
    fi
done;

for i in $XML ; 
do 
    if [ -f $BASE/text/$i ] ;
    then
        >&2 echo "ERROR: $i already indexed; it exists at $BASE/text/";
        ERR=1
    fi
    match=`echo $i| sed  -e 's/\.xml/\.hed/g' -`
    #echo testing if $match exists 
    if [ ! -f $match ] ;
    then
       >&2 echo "ERROR: I couldn't find a matching $match for $i";
       ERR=1
    fi
done;



if [ "$ERR" = "0" ]
then
   #cp $XML $HED toonline/
   >&2 echo 'All files are OK'
   >&2 echo done
else
    >&2 echo "ERRORS found; see above." 
fi
