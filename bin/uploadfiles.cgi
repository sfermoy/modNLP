#!/usr/bin/perl
# 
# UPLOADFILES.PL --- Upload files for indexing

# Author: luzs <s.luz@ed.ac.uk>
# Created: 27 Jan 2022
# $Revision$
# Keywords:


#     Permission  to  use,  copy, and distribute is hereby granted,
#     providing that the above copyright notice and this permission
#     appear in all copies and in supporting documentation.

# Commentary:

# Change log:
#

# Code:
use strict;
use warnings;
use CGI;
use Data::Dumper;

my $q = CGI->new();
print $q->header;

my $timestamp = localtime;
$timestamp =~ s/ /_/g;
my $upload_folder = "./data"; #"./data/$timestamp";
#mkdir $upload_folder, 0755;

my @fname = $q->param('filename');
my @fhand = $q->upload('filename');
my $buffer;

for (my $i = 0; $i <= $#fname; $i++ ){
    print "Uploading ".$fname[$i]."...<br>";    
    #    if ( my $io_handle = $fhand[$i] ) {
    ##print "WARNING: /disk2/omc/text/$fname[$i]\n";
    if (-e "/disk2/omc/text/$fname[$i]" || -e "/disk2/omc/headers/$fname[$i]" ){
	print "WARNING: file already in the corpus<br>";
    }
    my $io_handle = $fhand[$i]->handle;
    open (UPLOADFILE ,">$upload_folder/$fname[$i]" );
    while ( my $bytesread = $io_handle->read($buffer,1024) ) {
        print UPLOADFILE $buffer;
    }
}


print "<br><h3>Testing incoming/OMC</h3>\n";
my $x = system("cd $upload_folder; ./indexincoming.pl -d -v &> /tmp/log_omc.txt");
open(LOG, "/tmp/log_omc.txt");
print "Log:<pre>";
while(<LOG>){
        s/</&lt;/g;
        s/>/&gt;/g;
        print;
}
print "</pre>";

print "Done!<br>";

# end 

    #        while ( my $bytesread = $io_handle->read($buffer,1024) ) {
    #            print $out_file $buffer;
    #        }
    #}
