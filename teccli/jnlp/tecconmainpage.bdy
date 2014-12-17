<!-- TITLE:  teCCon HOME Page  -->
<!-- HDLMTS: <SCRIPT LANGUAGE="Javascript">  var javawsInstalled = 0;  isIE = "false";  if (navigator.mimeTypes && navigator.mimeTypes.length) {     x = navigator.mimeTypes['application/x-java-jnlp-file'];     if (x) javawsInstalled = 1  }   else {     isIE = "true";  }  function insertLink(url, name) {    if (javawsInstalled) {       document.write("<a href=" + url + ">"  + name + "</a>");    } else {      document.write("Need to install Java Web Start");    }  }</SCRIPT><SCRIPT LANGUAGE="VBScript">  on error resume next  If isIE = "true" Then     If Not(IsObject(CreateObject("JavaWebStart.IsInstalled"))) Then        javawsInstalled = 0     Else        javawsInstalled = 1     End If  End If</SCRIPT> -->
<!-- Document created by ../UPDATEwww1.12/html2bdy.pl v1.1  from /usr/contrib/www/tec/update/new/tecconmainpage.bdy -->
<body BGCOLOR="#FEFFFF" >
<BR>
<CENTER>
<h3><CODE>teCCon</CODE>: the Translational English Corpus CONcordancing tool</h3>
<h5>Centre for Translation Studies, UMIST</h5>
<h4>A Resource For The Research Community</h4>
</CENTER>
<BR><BR>
<P>

There are two ways of using the TEC Concordance Tool.  The recommended
method is to <img src="$figDirectory/new.gif"><a href="jnlp/"><b>start it directly
from your web browser</b> by following this link</a>.
Alternatively, you can access the corpus by 
<a  href="download">downloading the "stand-alone" version
of the TEC Browser</a>  and installing it on your machine. 

In addition to the functionality provided by the TEC browser, 
the following lists are available:
<UL>
<LI> frequency list: i.e. all <a target=freq
href="http://tec.ccl.umist.ac.uk/cgi-bin/showfq.cgi"> words in the
corpus, ordered by frequency</a>.
<BR>
<LI> and a list of all words in the corpus, <a target=freq href="http://tec.umist.ac.uk/cgi-bin/showfq.cgi?sort=alphabetically&init=a&end=b">ordered
alphabetically</a>.

<LI> If you need instruction on how to use the concordancing tool, 
 <a target=freq href="Doc/userguide.html">have a look at the TEC User Guide</a>


</UL> 


<!--
<p><BR><BR><BR><BR><BR><BR><BR>
<HR>
<FONT size=-1>More questions about TEC? Please e-mail
<a href="mailto:mona@ccl.umist.ac.uk">mona@ccl.umist.ac.uk</a></FONT>
-->


