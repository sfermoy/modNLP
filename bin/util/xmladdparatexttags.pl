#!/usr/bin/perl
# 
# Add paratext attributes to sections in header files (copying them from the corresponding XML files)

# Author: Saturnino Luz <luzs@cs.tcd.ie>
# Created: 11 May 2007
# $Revision$
# Keywords:


#     Permission  to  use,  copy, and distribute is hereby granted,
#     providing that the above copyright notice and this permission
#     appear in all copies and in supporting documentation.

# Commentary:

# Change log:
#

# Code:
my $rcsid =   "\$Id:  $>";
my $version = substr("\$Revision$>",11,-length($0));

use Getopt::Std;


sub usage {
    die<<"END";

  $me version $version


  usage: $me  [-h] 

  Options:
      -f \t fname \t\t Input file (to be split)
      -n \t do not create backup copy
      -h \t Help   \t just display this message and quit.

END
}

getopts('hf:n');
usage
    if ( $opt_h || !$opt_f );

my $counter = 0;
my $xmlfname = $opt_f;
$xmlfname =~ s/\.hed/.xml/;

my $content = '';
open(FI, $xmlfname) or die ("error opening $xmlfname: $1");
%sections = ();
while(<FI>){
  $content .= $_;
}
close FI;

while($content =~ /<section\s+id=["']([^"']+?)["']\s+(.*?)\s*>/g){
#    print "--$1: -$2-\n";
    $section{$1} = $2;
}

#foreach (keys %section){
#    print "$_ -> -$section{$_}-\n";
#}

my $infname = "$opt_f.preparatext";
#my $infname = "$opt_f";
rename($opt_f, $infname);
$content = '';
open(FI, $infname) or die ("error opening $infname: $1");
while(<FI>){
  $content .= $_;
}
close FI;

my $pad = '';
$content =~ s/<section(.*?) id=[\'\"](.+?)[\'\"](.*?)>/$pad = ' ' if $3;"<section$1 id='$2' $section{$2}$pad$3>"/goes;

open(FO, ">$opt_f") or die ("error opening $opt_f: $1");

print FO $content;

close FO;

unlink($infname)
    if $opt_n;
# end 
