#!/usr/bin/perl -w

## Sentence splitter for ECPC files based on the preprocessor written
## by Philipp Koehn for the europarl corpus
## (http://www.statmt.org/europarl/).  Unlike Koehn's, this splitter
## preprocesses for the TCA alignment tool, adding sentence and
## paragraph id tags (e.g. <p id='p1'><s id='s1'>...</s><s
## id='s2'>...</s></p>)

use FindBin qw($Bin);
use strict;
use Getopt::Std;

my $version = '0.4-ecpc';
## identify script for backup purposes
my $me   = $0; $me =~ s/.*\///;

my $mydir = "$Bin/nonbreaking_prefixes";
my %NONBREAKING_PREFIX = ();
my $language = "en";
my $paragbreakstring = '\n';
my $QUIET = 0;
my $HELP = 0;
my $TARGETTAGS = 'speech|writing';

sub usage {
  die<<"END";
$me version $version

  USAGE

  $me [-h] [-l en|de|...] [-e elements] file1 file2 ...

  Files file1, file2, ... will have its sentences which occur between the
  chosen elements tagged. E.g. if you specify -e speech|writing, the 
  chunk

    '<speech ref='s1'>this is a sentence. this is another.</speech>'

  will become

    '<speech ref='s1'>
     <p id='p1'><s id='s1'>this is a sentence.</s> 
     <s id='s2'>this is another.</s></p>
     </speech>'

  The original files will be save with extension '.$me' (e.g.
  file1.$me etc and file1, ... will
  contain the paragraph/sentence-annotated version.

  OPTIONS:

      -e element Element within whose tags sentences will be split
                 DEFAULT: $TARGETTAGS

      -l lang    Splitting will consider non-breaking patterns for 
                 language lang. See nonbreaking_prefixes/ directory
                 for available patterns (e.g. nonbreaking_prefix.en,
                 for English language prefixes.
                 DEFAULT: $language

      -p par     paragraph identifier. The token that identifies a 
                 paragraph break. This could be a markup element, such
                 as <p> or <p/>, a line break '\n', a double line break
                 '\n\n' or anything else you fancy.
                 DEFAULT: $paragbreakstring

      -q         Execute quietly. I.e. do not print progress messages

      -h         Just display this message and quit.

  EXAMPLE:

     $me -f EN20050512.xml -e 'speech|writing'

END
}

getopts('hql:e:');


if ($Getopt::Std::opt_h || $#ARGV < 0) {
    $HELP = $Getopt::Std::opt_h;
    &usage();
    exit;
}
if ($Getopt::Std::opt_l) {
    $language = $Getopt::Std::opt_l;
}
if (!$Getopt::Std::opt_q) {
    $QUIET = $Getopt::Std::opt_q;
    print STDERR "Sentence Splitter v$version (ECPC-specific release)\n";
    print STDERR "Language: $language\n";
}

my $prefixfile = "$mydir/nonbreaking_prefix.$language";

#default back to English if we don't have a language-specific prefix file
if (!(-e $prefixfile)) {
    $prefixfile = "$mydir/nonbreaking_prefix.en";
    print STDERR "WARNING: No known abbreviations for language '$language', attempting fall-back to English version...\n";
    die ("ERROR: No abbreviations files found in $mydir\n") unless (-e $prefixfile);
}

if (-e "$prefixfile") {
    open(PREFIX, "<:utf8", "$prefixfile");
    while (<PREFIX>) {
        my $item = $_;
        chomp($item);
        if (($item) && (substr($item,0,1) ne "#")) {
            if ($item =~ /(.*)[\s]+(\#NUMERIC_ONLY\#)/) {
                $NONBREAKING_PREFIX{$1} = 2;
            } else {
                $NONBREAKING_PREFIX{$item} = 1;
            }
        }
    }
    close(PREFIX);
}

## global counters for sentence and paragraph ID attributes
my $sentenceid = 1;
my $paragid = 1;

## loop through command line arguments (names of files to be split)
foreach (@ARGV){
    print "===========Processing $_...\n" unless $QUIET;
    my $fname = $_;
    my $infname = "$fname.pre$me";
    die "Backup file $infname exists. Rename or delete it before proceeding"
      if -e $infname;
    rename($fname, $infname);
    open(FI, $infname) or die ("error opening $infname: $!");
    open(FO, ">$fname") or die ("error opening $fname: $!");
    binmode(FI,":utf8");
    binmode(FO,":utf8");
    ## this will store each file's contents
    my $text = "";
    ## loop text, add lines together until we get a blank line or a <p>
    while(<FI>) {
        $text .= $_;
    }
    ## process all blocks within target tags
    $text =~ s/<($TARGETTAGS)(.+?)>\s*(.+?)\s*<(\/\1)>/"<$1$2>".&addptags($3)."<$4>"/sgoe;

    print FO $text;
    close FI;
    close FO;
}

sub addptags {
    my $text = shift;
    ## add opening sentence tag
    return ''
      unless $text;
    ## add sentence tags
    $text =~ s/(.+?)($paragbreakstring|$)/"<p id='p".$paragid++."'>".addstags(preprocess($1))."<\/p>\n"/ges;
    return "\n$text";
}

sub addstags {
    my $text = shift;
    ## add opening sentence tag
    return ''
      unless $text;
    $text = "<s id='se".$sentenceid++."'>$text";
    ## add sentence tags
    $text =~ s/\n(?!$)/"<\/s> <s id='se".$sentenceid++."'>"/ges;
    $text =~ s/\n$/<\/s>/os;
    $text =~ s/(<s[^>]+>)(<omit.*?>)(.*?)(<\/omit>)(<\/s>)/$2$1$3$5$4/gos;
    return "$text";
}

sub preprocess {
    my $text = shift;

    # clean up spaces at head and tail of each line as well as any double-spacing
    $text =~ s/ +/ /g;
    $text =~ s/\n /\n/g;
    $text =~ s/ \n/\n/g;
    $text =~ s/^ //g;
    $text =~ s/ $//g;

    #this is one paragraph
    ##my($text) = @_;
    
    #####add sentence breaks as needed#####
    
    #non-period end of sentence markers (?!) followed by sentence starters.
    $text =~ s/([?!]) +([\'\"\(\[\¿\¡\p{IsPi}]*[\p{IsUpper}])/$1\n$2/g;
    
    #multi-dots followed by sentence starters
    $text =~ s/(\.[\.]+) +([\'\"\(\[\¿\¡\p{IsPi}]*[\p{IsUpper}])/$1\n$2/g;
    
    # add breaks for sentences that end with some sort of punctuation inside a quote or parenthetical and are followed by a possible sentence starter punctuation and upper case
    $text =~ s/([?!\.][\ ]*[\'\"\)\]\p{IsPf}]+) +([\'\"\(\[\¿\¡\p{IsPi}]*[\ ]*[\p{IsUpper}])/$1\n$2/g;
    
    # add breaks for sentences that end with some sort of punctuation are followed by a sentence starter punctuation and upper case
    $text =~ s/([?!\.]) +([\'\"\(\[\¿\¡\p{IsPi}]+[\ ]*[\p{IsUpper}])/$1\n$2/g;
    
    # special punctuation cases are covered. Check all remaining periods.
    my $word;
    my $i;
    my @words = split(/ /,$text);
    $text = "";
    for ($i=0;$i<(scalar(@words)-1);$i++) {
        if ($words[$i] =~ /([\p{IsAlnum}\.\-]*)([\'\"\)\]\%\p{IsPf}]*)(\.+)$/) {
            #check if $1 is a known honorific and $2 is empty, never break
            my $prefix = $1;
            my $starting_punct = $2;
            if($prefix && $NONBREAKING_PREFIX{$prefix} && $NONBREAKING_PREFIX{$prefix} == 1 && !$starting_punct) {
                #not breaking;
            } elsif ($words[$i] =~ /(\.)[\p{IsUpper}\-]+(\.+)$/) {
                #not breaking - upper case acronym	
            } elsif($words[$i+1] =~ /^([ ]*[\'\"\(\[\¿\¡\p{IsPi}]*[ ]*[\p{IsUpper}0-9])/) {
                #the next word has a bunch of initial quotes, maybe a space, then either upper case or a number
                $words[$i] = $words[$i]."\n" unless ($prefix && $NONBREAKING_PREFIX{$prefix} && $NONBREAKING_PREFIX{$prefix} == 2 && !$starting_punct && ($words[$i+1] =~ /^[0-9]+/));
                #we always add a return for these unless we have a numeric non-breaker and a number start
            }
            
        }
        $text = $text.$words[$i]." ";
    }
    
    #we stopped one token from the end to allow for easy look-ahead. Append it now.
    $text = $text.$words[$i];
    
    # clean up spaces at head and tail of each line as well as any double-spacing
    $text =~ s/ +/ /g;
    $text =~ s/\n /\n/g;
    $text =~ s/ \n/\n/g;
    $text =~ s/^ //g;
    $text =~ s/ $//g;
    
    #add trailing break
    $text .= "\n" unless $text =~ /\n$/;
    
    return $text;    
}
