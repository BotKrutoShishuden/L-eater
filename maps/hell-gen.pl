#!/usr/bin/perl
use strict;
use warnings;

my $dim = int($ARGV[0]) || 100;

print "#L". "#" x ($dim - 2) . "\n";
print "#R" . "\\" x ($dim - 3) . "#" . "\n" ; 
foreach my $i (0 .. ($dim - 1) ) { print "#" . "\\" x ($dim - 2) . "#" . "\n" ; }
print "#" x $dim . "\n";


