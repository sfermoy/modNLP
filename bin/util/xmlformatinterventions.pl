#!/usr/local/bin/perl
# 
# XMLFORMATINTERVENTIONS.PL --- add line breaks after <speech> and
# <writing> and before the respective end tags (serve as a
# pre-processor for sentence splitters

# Author: S Luz <luzs@cs.tcd.ie>
# Created: 19 Jul 2008
# $Revision$
# Keywords:


#     Permission  to  use,  copy, and distribute is hereby granted,
#     providing that the above copyright notice and this permission
#     appear in all copies and in supporting documentation.

# Commentary:

# Change log:
#

# Code:
my $me   = $0;
$me =~ s/.*\///;

my $targettags = '(speech|writing)';

foreach (@ARGV){
#while (<>) {
#  chomp;
  print "===========Processing $_...\n";
  my $fname = $_;
  my $infname = "$fname.pre$me";
  #my $infname = "$opt_f";
  rename($fname, $infname);
  open(FI, $infname) or die ("error opening $infname: $!");
  open(FO, ">$fname") or die ("error opening $fname: $!");
  while(<FI>){
      s/(<\s*)$targettags(.*?>)(.+)/$1$2$3\n$4/;
      s/(.+)(<\/)$targettags(>)/$1\n$2$3$4/;
      print FO;
  }
  close FI;
  close FO;
}
