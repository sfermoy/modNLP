#!/usr/bin/perl
# 
# xmlnumberer.pl --- number (or renumber) sequentially a selected attribute of a selected element

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
      -e \t element \t Element to be (re)numbered
      -a \t attribute \t Attribute for (re)numbering
      -p \t prefix \t  Prefix for (re)numbering, in case of ID-type attributes
      -n \t do not create backup copy
      -h \t Help   \t just display this message and quit.

END
}

getopts('hf:e:a:p:n');
usage
    if ( $opt_h || !$opt_f || !$opt_e || !$opt_a);

my $counter = 0;
my $infname = "$opt_f.prenumber";
#my $infname = "$opt_f";
rename($opt_f, $infname);

my $content = '';
open(FI, $infname) or die ("error opening $infname: $1");
while(<FI>){
  $content .= $_;
}
close FI;

my $pad = '';
$content =~ s/<$opt_e(.*?)( $opt_a=[\'\"].+?[\'\"]|\b)(.*?)>/$ats =  $1 || ' '; $pad = ' ' if $3; $counter++;"<$opt_e$ats$opt_a=\'$opt_p$counter\'$pad$3>"/goes;

open(FO, ">$opt_f") or die ("error opening $opt_f: $1");

print FO $content;

close FO;

unlink($infname)
    if $opt_n;
# end 
