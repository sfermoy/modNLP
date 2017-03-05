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

my $debug = 0;
my @date = localtime();
my $DATE = join('',($date[5]+1900),"-",sprintf("%02d",$date[4]+1),"-",sprintf("%02d",$date[3]),"_$date[2].$date[1].$date[0]");
## ($sec,$min,$hour,$mday,$mon,$year,$wday,$yday,$isdst)

~ tr/ +/_/;


my $INCOMING_DIR = cwd();
my $TEXT_LIST_FILE = "$INCOMING_DIR/$LOG_DIR/indexed_on_$DATE.lst";
#$TEXT_LOG_FILE = "$INCOMING_DIR/indexed_on_$DATE.log";
my @TEXT_LIST=sort(<*.xml>);
my @HEADERS_LIST=sort(<*.hed>);
my $error_detected = 0;
my $error_local = 0;

@haux = @HEADERS_LIST;
open(FL, ">$TEXT_LIST_FILE") 
    or die "Couldn't open file list: $!\n";
if ( scalar @TEXT_LIST == 0 ){
    die "No files to index.\n";
}
#open(LOG, ">$TEXT_LOG_FILE") 
#    or die "Couldn't open log file: $!\n";
foreach (@TEXT_LIST){
    $error_local = 0;
    my $h = $_;
    $h =~ s/\.xml$/.hed/;
    my $t = $_;
    print "=====Checking $t and $h for indexing:\n";
    if ($DOS2UNIX){
        my $cmd = "$DOS2UNIX $t";
        Run($cmd) or
            PrepareToDie( "Error converting to unix: '$cmd': $!\n"); 
        my $cmd = "$DOS2UNIX $h";
        Run($cmd) or
            PrepareToDie( "Error converting to unix: '$cmd': $!\n"); 
    }
    if ($XMLLINT){
        my $cmd = "$XMLLINT $t";
        Run($cmd) or
            PrepareToDie( "XML parsing error: '$cmd': $!\n"); 
        my $cmd = "$XMLLINT $h";
        Run($cmd) or
            PrepareToDie( "XML parsing error: '$cmd': $!\n"); 
    }
    if ($FILETYPE){
        my $cmd = "$FILETYPE $t";
        AcceptableEncoding($cmd) or
            PrepareToDie( "Error determining file type: '$cmd': $!\n"); 
        my $cmd = "$FILETYPE $h";
        AcceptableEncoding($cmd) or
            PrepareToDie( "Error determining file type: '$cmd': $!\n"); 
    }
    unless(shift(@haux) eq $h){
        unlink($TEXT_LIST_FILE);
        PrepareToDie("Incoming HEADERS list don't match incoming TEXT files: $t != $h.\n");
    }
    if (sectionMismatch($t, $h)){
        unlink($TEXT_LIST_FILE);
        PrepareToDie("Incoming HEADERS list don't match incoming TEXT files: $t != $h.\n");  
    }
    if (-e "$TEXT_DIR/$t"){
        unlink($TEXT_LIST_FILE);
        PrepareToDie("Incoming files exists at destination '$TEXT_DIR/$t'\n");
    }    
    if (!CheckFilenameAttribute($h)){
        unlink($TEXT_LIST_FILE) &&
         PrepareToDie( "Stopped due to above error in header file $h\n");
    }
    print  FL "$TEXT_DIR/$t\n";
    if ( $error_local > 0 ) {
        print STDERR "-----ERRORS FOUND in $t or $h (see above).\n";
    }
    else {
        print STDERR "-----Files $t and $h are OK for indexing.\n";
    }
}
close FL;
(unlink($TEXT_LIST_FILE) &&
 PrepareToDie("Incoming headers don't match incoming text files.\n"))
    if $#haux > 0;

if ( $error_detected > 0 ){
    die "Errors in incoming files. See list above, fix the errors and try again.\n";
}

## Passed all checks; now the action begins...
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

    print "$c \n"
        if $debug;
    #print LOG "$c \n";

    if ($debug){
        print `$c`;
    }
    else{
        `$c 2>&1 /dev/null`;
    }

    return 1
        if $dry_run;
    return $? != -1;
    #return system("$c") == 0 ;
}

sub AcceptableEncoding {
    my $c = shift;

    print "$c \n"
        if $debug;
    print LOG "$c \n";
    return 1
        if $dry_run;
    
    my $enc = `$c`;
    return $enc =~ /(charset=us-ascii|charset=utf-8)/;
}

sub CheckFilenameAttribute{
    my $f = shift;
    
    my $name = $f;
    $name =~ s/.hed//;


    open(HF, "<$f") or die "Couldn't open $f: $!\n";

    my $l;
    while ($l = <HF>){
        if ($l =~ /filename=['"]([^'"]+?)['"]/){
            if ($1 eq $name){
                close HF; 
                return 1;
            }
            else{
                close HF; 
                print STDERR "File name mismatch in header file $f;\nFound filename='$1' when it should be filename='$name'\n";
                return 0;
            }
        }
    }
    close HF; 
    return 1;
}

sub sectionMismatch{
    my $t = shift;
    my $h = shift;

    my $indexelement = 'section';
    my $indexattribute = 'id';
    open(HF, "$IDX_BIN/idxmgr.properties") or die "Couldn't open $IDX_BIN/idxmgr.properties: $!\n";
    my $l;
    
    while ($l = <HF>){
        if ($l =~ /subcorpusindexer.element=(.+)/){
            $indexelement = $1;
            unless ($indexelement =~ /\(.*\)/){
                $indexelement = "($indexelement)";
            }
        }
        if ($l =~ /subcorpusindexer.attribute=(.+)/){
            $indexattribute = $1;
        }
    }
 
    my %section = {};
    open(HF, "$h") or die "Couldn't open $h: $!\n";
    while ($l = <HF>){
        if ($l =~ /<$indexelement .*?$indexattribute=['"](.+?)['"]/){
            $section{$2} = 1;
        }
    }
    close HF;

    my $err = 0;
    open(HF, "$t") or die "Couldn't open $t: $!\n";
    while ($l = <HF>){
        if ($l =~ /<$indexelement .*?$indexattribute=['"](.+?)['"]/){
            if ($section{$2}){
                $section{$2} = 0;
            }
            else{
                print STDERR "ERROR: Mismatched section $2: only in $t, not in $h\n";
                $err++;
            }
        }
    }
    close HF;
    foreach (keys %section){
        if ($section{$_}){
            print STDERR "ERROR: Mismatched section $_: only in $h, not in $t\n";
            $err++;
        }
    }
    return $err;
}

sub Remove {
    my @toremove = @_;

    foreach (@toremove){
        unlink $_;
    }

}

sub PrepareToDie {
    my $msg = shift;
    print STDERR "ERROR: $msg";
    $error_local = 1;
    $error_detected = 1;

}
