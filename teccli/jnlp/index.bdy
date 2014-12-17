<html>
<head>
<title>TEC: An Open-Source Concordancing Tool</title>
</head>
<body BGCOLOR="#FEFFFF" >

<h2>modnlp-tec: on-line Translational English Corpus</h2>
<center><h4>Start The TEC concordancing tool by clicking on the icon below</h3></center>

<br>
<p>

<center>
<a href="tec.jnlp"><img border=0 src="teclick.png"></a>

</center>

<br>
<b>N.B.:</b> The TEC corpus browser uses Java<sup><font SIZE="-2">TM</font></sup>
Web Start technology.  If the tool fails to start when you click on the icon above, 
you might have to download or upgrade Java on your machine. See
<a HREF="http://www.java.com/"> the Java Web Site</a></b> for details.

If the link above fails to start, try
downloading <a href="modnlp-teccli-0.6.1-bin-tec.tgz">the latest
  version of modnlp-teccli (this link)</a>, uncompressing it, and
running teccli.jar (by clicking on it, for instance).
Once the application starts, you will see a dialogue similar to this:

  <img src="chooseindex.png" alt="choose index">

Select 'Choose new remote corpus' and
enter <code>ronaldo.cs.tcd.ie:1240</code> into the window that will
pop up. After a few seconds, the concordancer should appear. 

<h4> Quick tutorial </h4>

In addition to the standard concordancer, the browser implements a few
other tools. It allows you to restrict your search to sub-corpora
defined according to certain features, to display a summary of the
files contained in the TEC corpus. and to display frequency lists for
the various selectable sub-corpora.

<h4>Selecting sub-corpora</h4>

The sub-corpora selection tool allows you to restrict the results of
concordancing queries and the contents of frequency tables to sections
of files matching certain selection criteria. These criteria can be,
for example, author, translator, translator gender, source language,
translator nationality, etc. In order to select a sub-corpus, choose 
"Options->Select sub-corpus...". 

<p>
A window similar to the one shown below should appear.

<img src="subcorpus.png" alt="sub-corpus selection">

The menu boxes allow you to select one or more items describing texts
to be include in the desired sub-corpus. The menu boxes can be
connected so as to form the logical expressions which ultimately
determine what gets included or excluded. The 'exclude' checkbox below
the menu boxes cause the items selected in the box above it to be
excluded. 

<p>
Clicking 'OK' activates the sub-corpus selection. In order to
de-activate it (that is, allow search on the full corpus), choose 
"Options" and de-select "Activate sub-corpus". 

<h4>Displaying a frequency list</h4>

Select "Plugins->Word Frequency List". The following window will
appear. 

<img src="fqbrowsernotes.png" alt="Frequency list">

Select the range of ranked items to display (default is display the
500 most common terms) and click on "Get List" to retrieve their
frequency table. This table can be saved to a CSV file which you can,
if you like, manipulate through a spreadsheet software.

<h4>Displaying general corpus information</h4>

Select "Plugins->Corpus Description Browser". A window will appear
which contains a list of each file in the corpus, the major sub-corpus
they belong to (i.e. fiction, newspapers, biography, and in-flight
magazines), the number of tokens they contain and their type-token
ratios. At the bottom of the window you will see the total number of
tokens in the corpus and the overall type-token ratio.  

</body>
