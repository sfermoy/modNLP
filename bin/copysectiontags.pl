#!/usr/bin/perl
# 
## set these variables to point to your corpus files
require "./config.pl";

$headfile = $ARGV[0];
$textfile = $headfile;

$debug = $ARGV[1] eq '-v'; 

$textfile =~ s/\.hed//i;
$textfile .= '.xml';

print "Copying section tags from $textfile to  $headfile\n"
    if $debug;

copySectionTags($textfile, $headfile);


sub copySectionTags{
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
 
    my $err = 0;
    my %section = {};
    open(HF, "$h") or die "Couldn't open $h: $!\n";
    open(HT, ">$h-tmp") or die "Couldn't open $h-tmp: $!\n";
    my $tmp = '';
    ## remove old sections from header
    while ($l = <HF>){
        if ($l =~ s/(<$indexelement .*?>)/__SECTIONS_GO_HERE__/gs){
            print STDERR "Removed $1 from header\n";
        }
        $tmp .= $l;
    }
    close HF;

    my $sec = '';
    my $txt = '';
    open(TF, "$t") or die "Couldn't open $t: $!\n";
    while (<TF>){
        $txt .= $_;
    }
    close TF;
    
    while ($txt =~ /<($indexelement) (.*?)>/g){
        $sec .= "<$1 $3/>\n";
    }
    
    $tmp =~ s/__SECTIONS_GO_HERE__/$sec/;
    $tmp =~ s/__SECTIONS_GO_HERE__//g;
    print HT $tmp;
    close HT;

    return $err;
}

