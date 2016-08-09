#!/usr/bin/perl
# 


$file = $ARGV[0];
print "Testing filename attribute in $file\n";

open(FI,$file) or die "Error opening $file: $!";
$file =~ s/\.hed//i;

while (<FI>) {
    chomp; 
    $x .= $_; 
} 
if ($x =~ /<title .*filename=["']$file["']/g) {
    exit 0;
}
else {
    print "filename attribute mismatch in $file\n";
    exit 1;
}

