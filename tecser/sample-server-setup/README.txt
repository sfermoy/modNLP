Thi directory is contain the general structure of a modnlp/tec
server. It includes 

dtd/  - a directory where the text and header dtds will be stored 
        (this general template contains gokheader.dtd and goktext.dtd
        as examples)

headers/ - where the header files will be stored once they've been 
           uploaded to the server and indexed; it should also contain a
           link to the appropriate dtd in dtd/

text/ - where the text files will be stored once they've been 
           uploaded to the server and indexed;  it should also contain a
           link to the appropriate dtd in dtd/

index/ 	- where the indices created by modnlp/idx will be kept

software/ - containing copies of the latest modnlp/idx modnlp/tecser
            and modnlp/teccli with symbolic links to general directory
            names to be used by the indexer scripts.

incoming/ - where the files to be indexed should be placed.  it should also contain
           links to the appropriate dtds (header and text) in dtd/. It
           contains the indexing scripts and a makefile that calls the
           appropriate scripts and logs their outputs in logs/indexing.log

The files to be indexed should be placed in incoming/. Then you run
make (or alternatively indexincoming.pl). The programme does a basic
sanity check of the files to be indexed and indexes them if there are
no errors (xml validation, formatting etc). If there are errors, the
processing stops with an error message. Otherwise, the files are
indexed and a list of the indexed files is placed in 
logs/indexed_on_YYYY-MM-DD_hh.mm.ss.lst (where YYYY is the year,
etc). 

Edit incoming/config.pl setting the variables to the configuration of
your server (location of files, etc).

Once this is done, open software/modnlp-tecser/, configure
server.properties, edit the appropriate startup script
(tecser-sysV-startup.sh for CentOS Unix System V style startu or
bin/tecser-init.d.sh for Debian/Ubuntu startup) and copy it into
/etc/init.d/, adding links from the appropriate runlevel directories
(e.g.  ln -s /etc/init.d/tecser-sysV-startup.sh /etc/rc3.d/K05tecser
and ln -s /etc/init.d/tecser-sysV-startup.sh /etc/rc3.d/S97tecser

Start the server as super user, and you're ready to go...
 

Short note on HOW TO CREATE FILES THAT CONFORM TO goktext.dtd and gokheader.dtd
===============================================================================

I suggest you:

 - use the header and text files in the 'tidier' version of
   the corpus (tec/corpus/ and tec/headers/) as 'templates' for the new
   files,

 - edit the new files with the jEdit tool (and forget about the
   software your computer people created for editing header files)

The jEdit editor can be downloaded for free from

http://www.jedit.org/

jEdit has an XML editing 'plugin' which should help you make sure the
files you send me in future at the very least conform to tectext.dtd
and techeader.dtd. Once you have downloaded and installed jEdit, you
will probably need to install the XML plugin (i.e. the component that
enables jedit to 'understand' XML, and out DTDs). Do the following:

 1 - start jEdit and click on 'Plugins' (on the menu bar) and select
     'Plugin manager...' on the drop-down menu.

 2 - When the manager window pops up choose the 'Install' tab,
     select 'XML' from the list of plugins and hit the 'install'
     button (if you are behind a firewall you might have to set
     the proxy server by selecting 'download options...', and
     'proxy servers').

Here's a rough 'protocol' for editing with jedit:
-------------------------------------------------

 0 - before you start, make sure that jedit

      - is set to use UTF-8 as the default character encoding and the
        Unix line separator (\n) as the default separator. You can do
        that by selecting (on the menu bar) 'Utilities->Global
        Options', then 'General' (on the options window that will
        appear), then 'Unix (\n)' for 'Default line separator' and
        'UTF-8' (careful not to select not UTF-8Y) for 'Default
        character encoding', click on 'Apply' and 'OK'.

 1 - copy goktext.dtd and gokheader.dtd into your work 'folder'

 2 - copy the 'templates' of the xml or hed file to be edited into
     the same folder.

 3 - start jedit and use it to open the (xml or hed) file to be
     edited

 4 - type in the content &c, making sure that:

     - if you are editing a TEXT (.xml) file, the file *always* starts
       with:

        <?xml version="1.0" encoding="UTF-8" standalone="no"?>
        <!DOCTYPE tectext SYSTEM "goktext.dtd">
        <tectext>

    -  if you are editing a HEADER (.hed) file, the file *always* starts
         with:

         <?xml version="1.0" encoding="UTF-8" standalone="no"?>
         <!DOCTYPE techeader SYSTEM "gokheader.dtd">
         <techeader>

     -  each text and header file contains at least one 'section' tag, and that
        the 'id' attribute of the section tag(s) in the text files
        actually match the respective 'id' attribute of the
        on the 'section' tag(s) in the header files. Each section id
        label should be unique, and start with a letter (e.g. 's1',
        's2', etc). It is *very important* that these section tags
        correspond to the sections described in the headers. If they
        don't, one will not be able to select the  sub-corpora
        (for concordancing, frequency lists etc) through the
        information contained within the section tags of the headers
        (e.g. translator, language, nationality, etc)

  5-  select Plugins->Error List->Error List (on the jedit menu bar).

       - a list of errors will typically appear, indicating where in
         your xml/hed file the errors are located, along with a brief
         description of the error. Clicking on the error on this list
         will take you to the location of the correspoding error in
         the xml/hed file. Correct all errors until the error list is
         empty.

       - N.B.: after you have corrected an error, save the file so
         that the error list gets update
