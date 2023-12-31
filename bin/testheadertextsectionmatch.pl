#!/usr/bin/perl
# 
## set these variables to point to your corpus files
require "./config.pl";

$headfile = $ARGV[0];
$textfile = $headfile;

$debug = $ARGV[1] eq '-v'; 

$textfile =~ s/\.hed//i;
$textfile .= '.xml';

print "Testing that section attributes match in $headfile and $textfile\n"
    if $debug;

if (sectionMismatch($textfile, $headfile))
{
    die("Sections in $textfile do not match $headfile\n");
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

