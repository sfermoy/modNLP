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
package modnlp.tc.tsr;

import modnlp.tc.dstruct.*;
import modnlp.tc.parser.*;
import modnlp.tc.util.IOUtil;

import modnlp.util.PrintUtil;
import modnlp.dstruct.*;

import java.io.*;
import java.util.Enumeration;
import java.util.Set;
import java.util.Arrays;

/**
 * Parse Reuters file, perform term filtering and print the reduced
 * term set with the score for each term.
 *
 * Usage:
 * <pre>
 MakeReducedTermSet corpus_list stopwdlist aggr tf_method categ parser

SYNOPSIS:
  Tokenise each file in corpus_list, remove words in stopwdlist
  and reduce the term set by a factor of aggr.

ARGUMENTS
 tf_method: term filtering method. One of: 
         'df': document frequency, local,
         'dfg': document frequency, global,
         'ig': information gain.
         'gss': GSS coefficient

categ: target category (e.g. 'acq'.) for local term filtering OR
         a method for combining local scores. One of:
            '_DFG' (global document frequency),
            '_MAX' (maximum local score),
            '_SUM' (sum of local scores),
            '_WAVG' (sum of local scores wbeighted by category generality.)
PARSER: parser to be used [default: 'news']
           'lingspam': Androutsopoulos' lingspam corpus
           'news':     REUTERS-21578 corpus, XML version.

  </pre>
 * @author  Saturnino Luz &#60;luzs@acm.org&#62;
 * @version <font size=-1>$Id: MakeReducedTermSet.java,v 1.1.1.1 2005/05/26 13:59:30 amaral Exp $</font>
 * @see  BVProbabilityModel
 * @see TermFilter
 * @see NewsParser
*/

public class MakeReducedTermSet
{
  private static CorpusList clist = null;
  private static StopWordList swlist = null;
  private static int aggressiveness =0;
  /** 
   *  Set up the main user interface items
   */
  public  MakeReducedTermSet(String cl, String sw, String aggr) {
    super();
    this.clist = new CorpusList(cl);
    this.swlist = new StopWordList(sw);
    this.aggressiveness = (new Integer(aggr)).intValue();
    return;
  }

  /** 
   * parseNews: Set up a REUTERS-21578 news XML parser object, perform
   * parsing, and return a ParsedCorpus
   */
  public ParsedCorpus parse (String filename, String plugin) 
    throws Exception 
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


  public WordScorePair[] rank(String method, BVProbabilityModel pm, String categ) 
    throws Exception
  {
    System.err.println("Starting filtering...");
    WordScorePair[] wsp =  null;
   if (method.equals("dfg")) {
      System.err.println("Filtering term set by document frequency (global)");
      DocumentFrequency tf = new DocumentFrequency();
      tf.initialise(pm);
      computeScores(tf, categ);
      wsp = tf.getSortedScores();
    }
    else {
      System.out.println("Reducing term set by "+method);    
      TermFilter tf = IOUtil.loadTSRPlugin(method);
      tf.initialise(pm);
      computeScores(tf, categ);
      wsp = tf.getSortedScores();
    }
    return wsp;
  }


  public static void main(String[] args) {
    try {
      MakeReducedTermSet f = new MakeReducedTermSet(args[0],args[1],args[2]);
      String termFilter = args[3];
      String category = args[4];
      String parser = args.length > 5 ? args[5] : "NewsParser";
      BVProbabilityModel pm = new BVProbabilityModel();
      if (args.length > 6)
        pm.setSmoothingType((new Byte(args[6])).byteValue());
      for (Enumeration e = f.clist.elements(); e.hasMoreElements() ;)
        {
          String fname = (String)e.nextElement();
          System.err.print("\n----- Processing: "+fname+" ------\n");
          pm.addParsedCorpus(f.parse(fname,parser), swlist);
        }
      System.err.println("Probability Model size "+pm.getTermSetSize());
      WordScorePair[] wsp = f.rank(termFilter, pm, category);
      int size = pm.getTermSetSize();
      int rsize = size / aggressiveness;
      WordFrequencyPair[] rwfp = new WordFrequencyPair[rsize];
      int j = 0;
      System.err.println("Reducing T from "+size+" to "+rsize);
      int stop = size-rsize-1;
      for(int i = size-1; i > stop ; i--)
        System.out.println(wsp[i].getWord()+" = "
                           +wsp[i].getScore());
    }
    catch (Exception e){
      System.err.println("\nUsage: MakeReducedTermSet CORPUS_LIST STOPWDLIST");
      System.err.println("                            AGGRESSIVENESS TF_METHOD CATEG [PARSER [SMOOTHING]]");
      System.err.println("       tokenise each file in CORPUS_LIST, remove words in STOPWDLIST");
      System.err.println("       and reduce the term set by a factor of AGGRESSIVENESS.\n");
      System.err.println(" TF_METHOD: term filtering method. One of:");
      System.err.println("            'dfg' (document frequency, global)");
      System.err.println("            'DocumentFrequency' (document frequency, local)");
      System.err.println("            'InfoGain' (information gain)");
      System.err.println("            'GSScoefficient' (GSS coefficient)");
      System.err.println("            'OddsRatio' (Odds ratio)");
      System.err.println("            ... (see modnlp.tc.tsr.* for more)");
      System.err.println(" CATEG: target category (e.g. 'acq'.) or");
      System.err.println("    a method for combining local scores. One of:");
      System.err.println("            '_DFG' (global document frequency),");
      System.err.println("            '_MAX' (maximum local score),");
      System.err.println("            '_SUM' (sum of local scores),");
      System.err.println("            '_WAVG'(sum of local scores weighted by category generality),");
      System.err.println(" PARSER: parser to be used [default: 'NewsParser']");
      System.err.println("  'LingspamEmailParser': Androutsopoulos' lingspam corpus,");
      System.err.println("  'NewsParser':  REUTERS-21578 corpus, XML version.");
      System.err.println(" SMOOTHING: 0: no smoothing, 1: Laplace, ...  [default: 0]");
      e.printStackTrace();
    } 
  }
}

