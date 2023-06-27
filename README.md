## MODNLP: Modular Suite of NLP Tools for Corpus Analysis
https://sourceforge.net/projects/modnlp/
modnlp aims to provide a modular architecture and tools for natural language processing written (mainly) in Java.
It provides an API and tools for (inverted) indexing, storage and retrieval of large amounts of text, with (XML-based) handling of meta-data,
tools for text categorisation, including, functionality for XML parsing, term set reduction (and basic keyword extraction), probabilistic classifier induction, sample classification tools, and evaluation modules, a suite of corpus management, curation and distributed access tools.

If you use the tool please consider referencing it using the following article:

Luz, S., & Sheehan, S. (2020). Methods and visualization tools for the analysis of medical, political and scientific concepts in Genealogies of Knowledge. Palgrave Communications, 6(1), 1-20.


The modnlp suite is distributed under the GNU General Public
License. See COPYING (in the distribution tar archives for details).

Access to the development version via GIT through sourceforge. 
E.g.:

git clone git://git.code.sf.net/p/modnlp/code modnlp-code

or 

git clone http://git.code.sf.net/p/modnlp/code modnlp

(see http://sourceforge.net/p/modnlp/code/ci/master/tree/ )

See top-level README for how to get the required external (3rd party)
libraries.

Packages:

* modnlp-teccli-VERSION-bin-gok: binaries (executable) concordance
browser and visualisation software with configuration files set for
access to the Genealogies of Knowledge (GoK) project.
 
     - 0.8.6-bin-gok:  bug fixes in idx files.
  
*  modnlp-idx-VERSION-bin-gok: text and metadata indexer
pre-configured for GoK corpus.

    - 0.8.5: bug-fixes (corrected discrepancy between freq lists and
      concordances).
      
* modnlp-tecser-VERSION-bin.tar.gz: corpus server software.

    - 0.1.4: bug fixes in idx files.
    
* modnlp-VERSION.tar.gz: full source code (tc, idx, tecser, teccli).
    
    - 0.2.1: stable teccli 0.8.6, idx 0.8.5, tecser 0.1.4 releases.
    

