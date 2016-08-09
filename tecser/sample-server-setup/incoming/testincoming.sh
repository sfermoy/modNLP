#!/bin/sh
# testincoming.sh
# Created Thu Sep  8 2011 by S Luz luzs@cs.tcd.ie
# $Id$
# $Log$
XML=`ls *.xml`
HED=`ls *.hed`

echo 'Converting to unix format'
echo dos2unix $XML
dos2unix $XML
echo dos2unix $HED
dos2unix $HED

echo 'Testing encoding'
for i in $XML ; do echo $i ; file -bi $i; done;
for i in $HED ; do echo $i ; file -bi $i; done;

echo 'Validating XML'
if xmllint -noout -valid $XML $HED 
then
    echo 'All files are valid XML'
else
    echo 'XML validation error (see above)'
    exit
fi

for i in $HED ; 
do 
    echo testing filename attribute of $i ;
    if ./testfilenameattribute.pl $i
    then
        echo $i matches specified fileaname;
    else
        echo $i does note match '<title ... filename=...>' specification;
        exit
    fi
done;


cp $XML $HED toonlinetec/
echo 'All files are OK and have been copied into toonlinetec/'

echo done
