#!/usr/bin/perl
# 

$headfile = $ARGV[0];
$textfile = $headfile;

$debug = $ARGV[1] eq '-v'; 

$textfile =~ s/\.hed//i;
$textfile .= '.xml';

print "Testing that section IDs match in $headfile and $textfile\n"
    if $debug;

$hs = join('_',getSectionIDs($headfile));
$ts = join('_',getSectionIDs($textfile));

#print "\n$hs\n  ~~~  \n$ts\n";

if ($hs eq $ts) {
    exit 0;
}
else {
    print "filename attribute mismatch in $headfile, $textfile:\nH: $hs\n  =!=  \nT: $ts\n";
    exit 1;
}

sub getSectionIDs {
    my $file = shift;

    open(FI,$file) or die "Error opening $file: $!";
    my $x = '';
    while (<FI>) {
        chomp;
        $x .= "$_ ";
    }
    my @sa = ();
    while ( $x =~ /<section +id=['"]([^'"]+)?['"]/ig ){
        push(@sa, $1);
    }
    close(FI);
    return @sa;
}



