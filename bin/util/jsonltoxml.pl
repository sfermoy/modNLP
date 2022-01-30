#!/usr/bin/perl
# 
# JSONLTOXML.PL --- 

# Author: luzs <s.luz@ed.ac.uk>
# Created: 22 Jul 2020
# $Revision$
# Keywords:


#     Permission  to  use,  copy, and distribute is hereby granted,
#     providing that the above copyright notice and this permission
#     appear in all copies and in supporting documentation.

# Commentary:

# Change log:
#

# Code:
use JSON::Tiny  qw(decode_json encode_json);
use XML::XML2JSON;
use XML::Simple;

my $jsonfname = $ARGV[0];
my $prefix = $ARGV[1];

open(JSON, $jsonfname) || die("Error opening  $jsonfname: $!");

#my $converter = JSON::XS->new();
my $i = 0;
my $XML2JSON = XML::XML2JSON->new();

while (<JSON>){
    #chomp;
    $i = $i + 1;
    my $xmlname = sprintf("$prefix-%07d.xml", $i);
    print STDERR "Writing $xmlname\n";
    #my $data = decode_json($_);
    print;
    my $data = $XML2JSON->json2xml($_);
    print $data;
    my $xml = XMLout($data);
    open(XML, ">xmlname");
    print XML $xml;
    close XML;
}


# end 
