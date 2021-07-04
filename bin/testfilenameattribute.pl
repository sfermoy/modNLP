#!/usr/bin/perl
# 


$file = $ARGV[0];
print "Testing filename attribute in $file\n";

open(FI,$file) or die "Error opening $file: $!";
$file =~ s/\.hed//i;

while (<FI>) {
#    chomp;
    $x .= $_;
}
$x =~ /(<document .*filename=["'])(.+?)(["'])/;

#print $x;
if ($2 eq $file) {
    exit 0;
}
else {
    print STDERR "filename attribute mismatch in $file: $1$2$3\n";
    print STDERR "fixing it...\n";
    $x =~ s/(<document .*filename=["'])(.+?)(["'])/$1$file$3/;
    rename($file,"$file"."-old");
    open(FI, ">$file.hed") || die("Error opening $file: $!");
    print FI $x;
    close(FI);
    exit 0;
}

