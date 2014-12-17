/**
 *  (c) 2006 S Luz <luzs@acm.org>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2,
 * or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
*/
package modnlp.tc.util;

import modnlp.tc.tsr.*;
import modnlp.tc.parser.*;
import modnlp.tc.dstruct.*;

import modnlp.dstruct.*;

import java.io.*;
import java.util.Enumeration;
import java.util.Arrays;

/**
 * Parse Reuters file, perform term filtering and generate ARFF file
 * for use with the WEKA datamining toolkit.
 *  
 * Usage:
 * <pre>
MakeARFF corpus_list stopwdlist aggr tf_method categ repr

SYNOPSIS:
  Tokenise each file in corpus_list, remove words in stopwdlist
  and reduce the term set by a factor of aggr.

ARGUMENTS
 tf_method: term filtering method. One of: 
         'df': document frequency, local,
         'dfg': document frequency, global,
         'ig': information gain.

 categ: target category (e.g. 'acq'.) This will be written into 
        the ARFF file as the last attribute of an instance).

        If the categ argument denotes a global TSR method (_MAX, _SUM,
        _WAVG, or _DFG), a document will (possibly) be represented as
        several lines in the ARFF file: one for each category the
        document belongs to.

 repr: document representation. One of 
        'occur': a vector of integers representing the number of 
             time a term occurs in the document,
        'boolean': a vector of Boolean values indicating whether 
             a term occurs in the document of not
        'pweight': a vector of real values indicating a term's 
             proportional weight, computed as 

          pweight = round ( 10 x (1+ log #_occurs_term_i_in_j 
                                  / 1 + log #_terms_in_j))


        'tfidf': Term frequency inverse document frequency:

          tfidf = no_of_occurrences_of_t_in_d * 
                  log ( size_of_corpus / 
                        size_of_subcorpus_in_which_t_occurs) 

parser: parser to be used [default: 'NewsParser']
         'LingspamEmailParser': Androutsopoulos' lingspam corpus,
         'NewsParser':  REUTERS-21578 corpus, XML version.
         (add your own 'parser' by subclassing modnlp.tc.parser.Parser)
  </pre>
 * @author  Saturnino Luz &#60;luzs@acm.org&#62;
 * @version <font size=-1>$Id: MakeARFF.java,v 1.1.1.1 2005/05/26 13:59:30 amaral Exp $</font>
 * @see  TCInvertedIndex TCProbabilityModel
 * @see TermFilter
 * @see Parser
 * @see ARFFUtil
 * @see  http://www.cs.waikato.ac.nz/~ml/weka/index.html the weka toolkit for data mining.

*/

public class MakeARFF
{
  private static CorpusList clist = null;
  private static StopWordList swlist = null;
  private static int aggressiveness =0;
  /** 
   *  Set up the main user interface items
   */
  public  MakeARFF(String clist, String swlist, String aggr) {
    super();
    this.clist = new CorpusList(clist);
    this.swlist = new StopWordList(swlist);
    this.aggressiveness = (new Integer(aggr)).intValue();
    return;
  }

  /** 
   * parseNews: Set up parser object, perform parsing, and print
   *     indented contents onto stdout (for test purposes only)
   */
  public ParsedCorpus parse(String filename, String plugin) throws Exception
  {
    Parser np = IOUtil.loadParserPlugin(plugin);
    np.setFilename(filename);
    return np.getParsedCorpus();
  }


   private void computeScores (TermFilter tf, String methodOrCat) {
    if ( methodOrCat.equals("_MAX") )
      tf.computeGlobalScoresMAX();
    else if ( methodOrCat.equals("_WAVG") )
      tf.computeGlobalScoresWAVG();
    else if ( methodOrCat.equals("_SUM") )
      tf.computeGlobalScoresSUM();
    else if ( methodOrCat.equals("_DFG") )
      ((DocumentFrequency)tf).computeGlobalDocFrequency();
    else
      tf.computeLocalScores(methodOrCat);
  }


  public WordFrequencyPair[] filter(String method, TCProbabilityModel pm, String categ) 
  throws Exception{
    System.err.println("Starting filtering...");
    WordFrequencyPair[] wsp;
    if (method.equals("dfg")) {
      System.err.println("Filtering term set by document frequency (global)");
      DocumentFrequency tf = new DocumentFrequency();
      tf.initialise(pm);
      computeScores(tf, categ);
      wsp = tf.getReducedFreqList(aggressiveness);
    }
    else {
      System.err.println("Reducing term set by "+method);    
      TermFilter tf = IOUtil.loadTSRPlugin(method);
      tf.initialise(pm);
      computeScores(tf, categ);
      wsp = tf.getReducedFreqList(aggressiveness);
    }
    return wsp;
  }

  public static void main(String[] args) {
    try {
      MakeARFF f = new MakeARFF(args[0],args[1],args[2]);
      String termFilter = args[3];
      String category = args[4];
      String docRepresentation = args[5];
      String parser = args.length > 6 ? args[6] : "NewsParser";
      // print header line (arff comment)
      System.out.println("% File generated by \n%  modnlp.tc.util.MakeARFF "+
                         args[0]+" "+args[1]+" "+args[2]+
                         " "+args[3]+" "+args[4]+" "+args[5]+"\n%\n"+
                         "% Date: "+(new java.util.Date())+"\n");
      BVProbabilityModel pm = new BVProbabilityModel();
      for (Enumeration e = f.clist.elements(); e.hasMoreElements() ;)
        {
          String fname = (String)e.nextElement();
          System.err.print("\n----- Processing: "+fname+" ------\n");
          pm.addParsedCorpus(f.parse(fname,parser), swlist);
        }
      System.gc();
      WordFrequencyPair[] rts = f.filter(termFilter, pm, category);
      // pm.trimTermSet(rts);
      System.err.println("Creating ARFF file");
      if (category.charAt(0) == '_')
        category = null;
      if (docRepresentation.equalsIgnoreCase("occur"))
        ARFFUtil.printOccurARFF(pm, rts, category, System.out);
      else if (docRepresentation.equalsIgnoreCase("boolean"))
        ARFFUtil.printBooleanARFF(pm, rts, category, System.out);
      else if (docRepresentation.equalsIgnoreCase("pweight"))
        ARFFUtil.printPWeightARFF(pm, rts, category, System.out);
      else if (docRepresentation.equalsIgnoreCase("tfidf"))
        ARFFUtil.printTFIDFARFF(pm, rts, category, System.out);
      else if (docRepresentation.equalsIgnoreCase("debug"))
        ARFFUtil.printDebug(pm, rts, System.out);
      else
        System.err.print("\nInvalid representation scheme: "+docRepresentation);
    }
    catch (Exception e){
      System.err.println("\nUsage: MakeARFF CORPUS_LIST STOPWDLIST AGGRESSIVENESS TF_METHOD CATEG REPR");
      System.err.println("       tokenise each file in CORPUS_LIST, remove words in STOPWDLIST");
      System.err.println("       and reduce the term set by a factor of AGGRESSIVENESS.\n");
      System.err.println(" TF_METHOD: term filtering method. One of: 'df' (document frequency, local),");
      System.err.println("            'dfg' (document frequency, global),");
      System.err.println("            'ig' (information gain)");
      System.err.println("            'gss' (GSS coefficient)");
      System.err.println("            'or' (Odds ratio)");
      System.err.println(" CATEG: taget category (e.g. 'acq'. Last attribute of an instance) or");
      System.err.println("    a method for combining local scores. One of:");
      System.err.println("            '_DFG' (global document frequency),");
      System.err.println("            '_MAX' (maximum local score),");
      System.err.println("            '_SUM' (sum of local scores),");
      System.err.println("            '_WAVG' (sum of local scores wbeighted by category generality),");
      System.err.println(" REPR: document representation (one of {occur, boolean, pweight, tfidf}\n");
      System.err.println(" PARSER: parser to be used [default: 'NewsParser']");
      System.err.println("  'LingspamEmailParser': Androutsopoulos' lingspam corpus,");
      System.err.println("  'NewsParser':  REUTERS-21578 corpus, XML version.");
      e.printStackTrace();
    } 
  }
}

