#!/usr/bin/perl
# 
# TESTFILES.PL --- Test files for indexing

# Author: luzs <s.luz@ed.ac.uk>
# Created: 2 May 2022
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
use Cwd;
use File::Copy;

my $q = CGI->new();
print $q->header;

my $indexincoming = "../indexincoming.pl -d -v ";
my $headerdtd = "/disk2/omc/oslomedicalcorpus-gitlab/dtd/omcheader.dtd";
my $textdtd = "/disk2/omc/oslomedicalcorpus-gitlab/dtd/omctext.dtd";

my $timestamp = localtime;
$timestamp =~ s/ /_/g;
my $upload_folder = "./data/$timestamp";
my $tardir = "archives";
my $log = "/tmp/$timestamp.log";
mkdir $upload_folder, 0755;
my $userdtd = defined($q->param('userdtd'))? $q->param('userdtd') : 0;
if ( $userdtd ne 'true') {
    copy($headerdtd, "$upload_folder/omcheader.dtd")
        || die "Error copying $headerdtd: $!";
    copy($textdtd, "$upload_folder/omctext.dtd")
        || die "Error copying $textdtd: $!";
}

my @fname = $q->multi_param('filename');
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


print "<br><h3>Testing corpus files</h3>\n";

my $dir = getcwd;
chdir $upload_folder;
my $x = system("/usr/bin/bash -c \"$indexincoming\" > $log 2>&1 ");
open(LOG, $log);
print "Log:<pre>";
while(<LOG>){
        s/</&lt;/g;
        s/>/&gt;/g;
        print;
}
close LOG;
print "</pre>";
print "Done!<br>";

#print "CWD: ".getcwd()."   target ../$tardir/*.tgz\n";
unlink(glob("../$tardir/*.tgz")) or warn "Error removing data/$tardir/*.tgz: $!\n";
my $allowdownload = defined($q->param('allowdownload')) ?
    $q->param('allowdownload') : 0;
if (  $allowdownload eq 'true') {
    my $archive = "$tardir/$timestamp.tgz";
    chdir '..';#$dir;
    #print "<p><b>tar cfvz $archive $timestamp</b></p>";
    system("tar cfvz $archive $timestamp >/dev/null");
    print "<br><a href='data/$archive'>Download clean, reformatted files (link valid for the next 2 minutes).</a>";
}
#print "rm -rf $timestamp\n";
print `\n rm -rf '$timestamp'`;

