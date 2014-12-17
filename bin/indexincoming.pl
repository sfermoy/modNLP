#!/usr/bin/perl
# indexincoming.pl
# Created Mon Jun 21 2010 by S Luz luzs@cs.tcd.ie
# $Id$
# $Log$
 use Cwd;

## set these variables to point to your corpus files

require "config.pl";

$dry_run = $ARGV[0];
die "Usage: indexincoming.pl  [-dry-run]\n  Error reading config.pl. Please set locations correctly."
    unless $IDX_BIN || $TEXT_DIR || $HEADERS_DIR || $HEADERS_URL || $INDEX_DIR;


$DATE = localtime();
$DATE =~ tr/ /_/;


$INCOMING_DIR = cwd();
$TEXT_LIST_FILE = "$INCOMING_DIR/indexed_on_$DATE.lst";
@TEXT_LIST=sort(<*.xml>);
@HEADERS_LIST=sort(<*.hed>);


@haux = @HEADERS_LIST;
open(FL, ">$TEXT_LIST_FILE") 
    or die "Couldn't open file list: $!\n";
foreach (@TEXT_LIST){
    my $h = $_;
    $h =~ s/\.xml$/.hed/;
    if ($DOS2UNIX){
        my $cmd = "$DOS2UNIX $_";
        Run($cmd) or
            die "Error converting to unix: '$cmd': $!\n"; 
        my $cmd = "$DOS2UNIX $h";
        Run($cmd) or
            die "Error converting to unix: '$cmd': $!\n"; 
    }
    if ($XMLLINT){
        my $cmd = "$XMLLINT $_";
        Run($cmd) or
            die "XML parsing error: '$cmd': $!\n"; 
        my $cmd = "$XMLLINT $h";
        Run($cmd) or
            die "XML parsing error: '$cmd': $!\n"; 
    }
    if ($FILETYPE){
        my $cmd = "$FILETYPE $_";
        AcceptableEncoding($cmd) or
            die "Error determining file type: '$cmd': $!\n"; 
        my $cmd = "$FILETYPE $h";
        AcceptableEncoding($cmd) or
            die "Error determining file type: '$cmd': $!\n"; 
    }
    (unlink($TEXT_LIST_FILE) &&
     die "Incoming headers list doesn't match incoming text file list: $_ != $h.\n")
        unless shift(@haux) eq $h;
        (unlink($TEXT_LIST_FILE) &&
         die "Incoming files exists at destination '$TEXT_DIR/$_'")
        if -e "$TEXT_DIR/$_";
    print  FL "$TEXT_DIR/$_\n";
}
close FL;
(unlink($TEXT_LIST_FILE) &&
 die "Size of incoming headers list doesn't match incoming text file list size.\n")
    if $#haux > 0;
    

my $cmd = 'cp '. join(' ', @TEXT_LIST)." $TEXT_DIR/";
Run($cmd)
    or unlink @TEXT_LIST &&
       die "error running: '$cmd': $!\n";
$cmd = 'cp '. join(' ', @HEADERS_LIST)." $HEADERS_DIR/";
if (! Run($cmd)){
    unlink @TEXT_LIST;
    unlink @HEADERS_LIST;
    unlink($TEXT_LIST_FILE);
    die "error running: '$cmd': $!\n";
}

if (!chdir($IDX_BIN)){
    unlink @TEXT_LIST;
    unlink @HEADERS_LIST;
    unlink($TEXT_LIST_FILE);
    die "error running: 'chdir($IDX_BIN)': $!\n";
}

print "Now at $IDX_BIN \n";

$cmd = "./runidx.sh $INDEX_DIR $TEXT_LIST_FILE $HEADERS_DIR $HEADERS_URL";
if (! Run($cmd)){
    unlink @TEXT_LIST;
    unlink @HEADERS_LIST;
    unlink($TEXT_LIST_FILE);
    die "error running: '$cmd': $!\n";
}

chdir($INCOMING_DIR);
unlink @TEXT_LIST
    unless $dry_run;
unlink @HEADERS_LIST
    unless $dry_run;

print 'Files '.join(', ',@TEXT_LIST)." + headers have been indexed and removed from incoming folder.\n";

sub Run {
    my $c = shift;

    print "$c \n";
    return 1
        if $dry_run;
    
    return system($c) == 0 ; 
}

sub AcceptableEncoding {
    my $c = shift;

    print "$c \n";
    return 1
        if $dry_run;
    
    my $enc = `$c`;
    return $enc =~ /(charset=us-ascii|charset=utf-8)/;
}



sub Remove {
    my @toremove = @_;

    foreach (@toremove){
        unlink $_;
    }

}
