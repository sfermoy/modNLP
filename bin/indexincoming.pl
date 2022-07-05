#!/usr/bin/perl
# indexincoming.pl
# Created Mon Jun 21 2010 by S Luz luzs@cs.tcd.ie
# $Id$
# $Log$
use Cwd;
use Getopt::Std;

BEGIN{ 
    $0 =~ /(.*)\/[^\/]/;
    $pgd = $1;
    unshift (@INC,("./",
	       "$pgd/",
	       "$pgd/Lib/"
		));
		$me   = $0;
		$me =~ s/.*\///;
}


$VISUALTOHEAD="$pgd/visualstoheaders.pl -v -b -f";
## set these variables to point to your corpus files

require "config.pl";

sub Usage {
die "Usage: indexincoming.pl [-h|-d|-s|-q|-o|-x]
             -h        display this message
             -i        skip image copying (assume they are already at destination 'headers/')
             -v        extract visual tags from xml, convert them to flat-format and add to hed files 
             -d        dry run (test but do not index)
             -s        do not try to kill server before indexing
             -q        do not display debug messages
             -o        print all STDERR messages to STDOUT
             -x        skip 'file already indexed' test
  Please set locations correctly in config.pl.\n"; 
}
getopts('hidsqovx');
my $dry_run = $opt_d;
my $server_kill = !$opt_s;
my $debug = !$opt_q;
Usage if $opt_h;
Usage()
    unless $IDX_BIN || $TEXT_DIR || $HEADERS_DIR || $HEADERS_URL || $INDEX_DIR;

my $STDERR =  $opt_o? *STDOUT : *STDERR;

my @date = localtime();
my $DATE = join('',($date[5]+1900),"-",sprintf("%02d",$date[4]+1),"-",sprintf("%02d",$date[3]),"_$date[2].$date[1].$date[0]");
## ($sec,$min,$hour,$mday,$mon,$year,$wday,$yday,$isdst)

~ tr/ +/_/;


my $INCOMING_DIR = cwd();
my $TEXT_LIST_FILE = "$INCOMING_DIR/$LOG_DIR/indexed_on_$DATE.lst";
#$TEXT_LOG_FILE = "$INCOMING_DIR/indexed_on_$DATE.log";
my @TEXT_LIST=sort(<*.xml>);
my @HEADERS_LIST=sort(<*.hed>);
my @IMAGES = ();
#    map {
#    my $i = $_;
#    $i =~ s/\.hed/*.{png,jpg,jpeg,gif,PNG,JPG,JPEG,GIF}/;
#    $i;
#} @HEADERS_LIST; 
my $error_detected = 0;
my $warn_detected = 0;
my $error_local = 0;

@haux = @HEADERS_LIST;
$htmp = '';
unless($dry_run){
    open(FL, ">$TEXT_LIST_FILE") 
        or die "Couldn't open file list: $!\n";
}
if ( scalar @TEXT_LIST == 0 ){
    die "No files to index.\n";
}
#open(LOG, ">$TEXT_LOG_FILE") 
#    or die "Couldn't open log file: $!\n";
my %set = map { $_ => 1 } @haux;
my @missing;
foreach(@TEXT_LIST){
    my $h = $_;
    $h =~ s/\.xml$/.hed/;
    push(@missing, $h)
        unless exists($set{$h});
}
%set = map { $_ => 1 } @TEXT_LIST;
foreach(@HEADERS_LIST){
    my $t = $_;
    $t =~ s/\.hed$/.xml/;
    push(@missing, $t)
        unless exists($set{$t});
}
if (scalar(@missing) > 0){
    PrepareToDie("ERROR: The following files are missing:\n".join("\n",@missing)."\n");
}

## flush after printing
$| = 1;
#print "~~~~~~~~~~~~~>".join(':',@missing)."\n";
foreach (@TEXT_LIST){
    $error_local = 0;
    my $h = $_;
    $h =~ s/\.xml$/.hed/;
    my $t = $_;
    print "=====Checking $t and $h for indexing:\n";
    if ($DOS2UNIX){
        my $cmd = "$DOS2UNIX $t";
        Run($cmd) or
            PrepareToDie( "ERROR converting to unix: '$cmd': $!\n"); 
        my $cmd = "$DOS2UNIX $h";
        Run($cmd) or
            PrepareToDie( "ERROR converting to unix: '$cmd': $!\n"); 
    }
    if ($XMLLINT){
        my $cmd = "$XMLLINT $t";
        Run($cmd) or
            PrepareToDie( "XML parsing ERROR: '$cmd': $!\n"); 
        my $cmd = "$XMLLINT $h";
        Run($cmd) or
            PrepareToDie( "XML parsing ERROR: '$cmd': $!\n"); 
    }
    if ($FILETYPE){
        my $cmd = "$FILETYPE $t";
        AcceptableEncoding($cmd) or
            PrepareToDie( "ERROR determining file type: '$cmd': $!\n"); 
        my $cmd = "$FILETYPE $h";
        AcceptableEncoding($cmd) or
            PrepareToDie( "ERROR determining file type: '$cmd': $!\n"); 
    }
    if ($VISUALTOHEAD && $opt_v){
        my $cmd = "$VISUALTOHEAD $h";
        Run($cmd) or
            PrepareToDie("ERROR extracting visuals from $t into $h ('$cmd'): $!\n");
    }
    #print "++++++++>".join(':',@haux)."\n";
    unless($htmp = shift(@haux) eq $h){
        unlink($TEXT_LIST_FILE);
        PrepareToDie("Incoming HEADERS list don't match incoming TEXT files: $t and $h ($htmp).\n");
    }
    if (sectionMismatch($t, $h)){
        unlink($TEXT_LIST_FILE);
        PrepareToDie("Section ID mismatch in $t and $h.\n");  
    }
    my $img = getImages($h);
    push(@IMAGES, @$img);
    
    if (!$opt_x && -e "$TEXT_DIR/$t"){
        unlink($TEXT_LIST_FILE);
        PrepareToDie("Incoming files exists at destination '$TEXT_DIR/$t'\n", ($dry_run ? 'WARNING' : 0));
    }
    if (!CheckFilenameAttribute($h)){
        unlink($TEXT_LIST_FILE) &&
         PrepareToDie( "Stopped due to above ERROR in header file $h\n");
    }
    print  FL "$TEXT_DIR/$t\n"
        unless $dry_run;
    if ( $error_local > 0 ) {
        my $S = $error_local > 1? "S" : '';
        print $STDERR "-----$error_local ERROR$S/WARNING$S in $t or $h (see above).\n";
    }
    else {
        print $STDERR "-----Files $t and $h are OK for indexing.\n";
    }
}
close FL
    unless $dry_run;
(unlink($TEXT_LIST_FILE) &&
 PrepareToDie("Incoming headers don't match incoming text files.\n"))
    if $#haux > 0;

if ( $warn_detected > 0 ){
    print $STDERR "Warnings found. Search for the word 'WARNING' to see them.\n";
}
if ( $error_detected > 0 ){
    print $STDERR "=========================================================================================\n";
    print $STDERR "Error(s)/warning(s) in incoming files. search for 'ERROR' and 'WARNING' above for details.\n";
    exit $error_detected;
}
else {
    print $STDERR "All files are OK\n";
}

exit $error_detected
    if $dry_run;

## Passed all checks; now the action begins...
if ($server_kill && -e $SERVER_PID){
    $pid = `cat $SERVER_PID`;
    Run("kill $pid") or  die "Couldn't stop server: $!\n";
}

my $cmd = 'cp '. join(' ', @TEXT_LIST)." $TEXT_DIR/";
Run($cmd)
    or unlink @TEXT_LIST &&
       die "error running: '$cmd': $!\n";

$cmd = 'cp '. join(' ', @HEADERS_LIST)." $HEADERS_DIR/";
if (! Run($cmd)){
    unlink @TEXT_LIST;
    unlink @HEADERS_LIST;
    unlink @IMAGES;
    unlink($TEXT_LIST_FILE);
    die "error running: '$cmd': $!\n";
}

unless (@IMAGES == 0 || $opt_i) { 
    $cmd = 'cp '. join(' ', @IMAGES)." $HEADERS_DIR/";
    if (! Run($cmd)){
        unlink @TEXT_LIST;
        unlink @HEADERS_LIST;
        unlink @IMAGES;
        unlink($TEXT_LIST_FILE);
        die "error running: '$cmd': $!\n";
    }
}

if (!chdir($IDX_BIN)){
    unlink @TEXT_LIST;
    unlink @HEADERS_LIST;
    unlink($TEXT_LIST_FILE);
    unlink @IMAGES;
    die "error running: 'chdir($IDX_BIN)': $!\n";
}


print "Now at $IDX_BIN \n";

$cmd = "./runidx.sh $INDEX_DIR $TEXT_LIST_FILE $HEADERS_DIR $HEADERS_URL";
if (! Run($cmd)){
    print "Cleaning up before death:\n";
    print "Now at $TEXT_DIR\n";
    chdir($TEXT_DIR);
    print "Removing @TEXT_LIST\n";
    unlink @TEXT_LIST
        unless $dry_run;
    print "Now at $HEADERS_DIR\n";
    chdir($HEADERS_DIR);
    print "Removing @HEADERS_LIST\n";
    unlink @HEADERS_LIST
        unless $dry_run;
    die "error running: '$cmd': $!\n";
}

chdir($INCOMING_DIR);
unlink @TEXT_LIST
    unless $dry_run;
unlink @HEADERS_LIST
    unless $dry_run;
unlink @IMAGES
    unless $dry_run;

print 'Files '.join(', ',@TEXT_LIST)." + headers have been indexed and removed from incoming folder.\n";

if ($server_kill && -e $SERVER_PID){
    $cmd = "daemonize -p $SERVER_PID -o $SYSLOGFILE -e $ERRLOG -c $TEC_BIN $TEC_SERVER $ARGS";
    Run($cmd) or  die "error re-starting server: '$cmd': $!\n";
}

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

    my $st = $? >> 8;
    print "Run exit status $st\n";
    $st == 0;
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
    my $c = '';
    my $fix = 0;
    while ($l = <HF>){
        $c .= $l;
        if ($l =~ /filename=['"]([^'"]+?)['"]/){
            if ($1 eq $name){
                close HF; 
                return 1;
            }
            else{
                ##                close HF; 
                print $STDERR "WARNING: File name mismatch in header file $f;\nFound filename='$1' when it should be filename='$name'\n";
                $fix = 1;
                $warn_detected = 1;
                #                return 0;
            }
        }
    }
    close HF;
    if ($fix){
        print $STDERR "fixing it...\n";
        $c =~ s/(<document .*filename=["'])(.+?)(["'])/$1$name$3/;
        rename($name,"$name"."-old");
        open(FI, ">$name.hed") || die("Error opening $name: $!");
        print FI $c;
        close(FI);
    }
    return 1;
}


sub getImages{
    my $h = shift;

    my @IMG = ();
    open(HF, "$h") or die "Couldn't open $h: $!\n";
    while ($l = <HF>){
        if ($l =~ /<img .*?src=['"](.+?)['"]/){
            if (! -e $1){
                PrepareToDie("Missing image from $h: $1\n");
            }
            else {
                push(@IMG, $1);
            }
        }
    }
    close HF;
    return(\@IMG);
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
                print $STDERR "ERROR: Mismatched section $2: only in $t, not in $h\n";
                $err++;
            }
        }
    }
    close HF;
    foreach (keys %section){
        if ($section{$_}){
            print $STDERR "ERROR: Mismatched section $_: only in $h, not in $t\n";
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
    my $warn = shift || 'ERROR';
    
    print $STDERR "$warn: $msg";
    $error_local += 1;
    $error_detected = 1;

}
