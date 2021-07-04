#!/usr/bin/perl
# 
BEGIN{
  # Initialise CGI output
     print STDOUT "Content-type: text/html\n\n";
     }

print "<html>Testing incoming/English<br>\n";
$x = `cd /home/genealr0/public_ftp/incoming/English; /disk2/gok/incoming/testincoming.sh &> /tmp/log_en.txt`;
open(LOG, "/tmp/log_en.txt");
print "Log:<pre>";
while(<LOG>){
	s/</&lt;/g;
	s/>/&gt;/g;
	print;
}
print "</pre>";
    

