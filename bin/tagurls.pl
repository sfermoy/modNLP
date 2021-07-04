#!/usr/bin/perl
#
# xmladdfilename.pl --- add the file name to an element (e.g. <header language='XX'> elements
#                       producing <header filename='fnwithoutextension' language='XX'>)

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
use Regexp::Common qw /URI/;

sub usage {
    die<<"END";

  $me version $version


  usage: $me  [-h] 

  Options:
      -f \t fname \t\t Input file
      -e \t element \t Element to have filename added to
      -n \t do NOT create backup copy 
      -h \t Help   \t just display this message and quit.

END
}

getopts('hf:e:n');
usage
    if ( $opt_h || !$opt_f ); #|| !$opt_e || !$opt_a);

my $counter = 0;
my $fnnoext = substr($opt_f,0,index($opt_f,'.'));
my $infname = "$opt_f.pretag";
#my $infname = "$opt_f";
rename($opt_f, $infname);
my $content = '';

open(FI, $infname) or die ("error opening $infname: $1");
while(<FI>){
  $content .= $_;
}
close FI;

my $pad = '';
$content =~ s|(?<!<url> *)($RE{URI})|print "replacing '$1'\n";"<url>$1</url>"|goei;
#$content =~ s/<$opt_e\b(.*?)>/<$opt_e $opt_a=\"$fnnoext\"$pad$1>/go;

open(FO, ">$opt_f") or die ("error opening $opt_f: $1");

print FO $content;

close FO;
unlink($infname)
    if $opt_n;


# end
