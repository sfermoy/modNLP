#!/usr/bin/perl
# 
# XMLSPLITTER.PL --- split xml file. That is: create a header file by
# removing the content of selected elements

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
      -f \t fname \t Input file (to be split)
      -e \t element \t Element for include files
      -h \t Help   \t just display this message and quit.
  Example:
      \t xmlsplitter -f EN20050512.xml -e 'speech|writing'
END
}

getopts('hf:e:');
usage
    if ( $opt_h || !$opt_f || !$opt_e );

my $content = '';
open(FI, $opt_f) or die ("error opening $infname: $1");
while(<FI>){
  $content .= $_;
}
close FI;

my $pad = '';

$content =~ s/<($opt_e)(.*?)>.*?<(\/\1)>/<$1$2><$3>/gs;

my $f2 = $opt_f;
$f2 =~ s/(.+?)\..*/$1.hed/;

open(FO, ">$f2") or die ("error opening $f2: $1");

print FO $content;

close FO;

# end 
