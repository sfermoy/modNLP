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

import modnlp.tc.dstruct.TCInvertedIndex;
import modnlp.tc.dstruct.TCProbabilityModel;

import modnlp.dstruct.BagOfWords;
import modnlp.dstruct.WordFrequencyPair;
import modnlp.dstruct.WordScorePair;
import modnlp.util.*;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;

/**
 *  Abstract class for term set reduction
 *
 * @author  S Luz &#60;luzs@acm.org&#62;
 * @version <font size=-1>$Id: TermFilter.java,v 1.1.1.1 2005/05/26 13:59:30 amaral Exp $</font>
 * @see MakeReducedTermSet
 * @see modnlp.tc.induction.MakeProbabilityModel
*/
public abstract class TermFilter {

  TCProbabilityModel pm;
  TCInvertedIndex ii;

  WordScorePair[] wsp = null;

  public  TermFilter(){
  }

  public void initialise (TCProbabilityModel pm) {
    this.pm = pm;
    this.ii = pm.invertedIndex;
    wsp = ii.getBlankWordScoreArray();
  }

  /**
   * All Term Filters must implement  computeLocalTermScore()
   */
  abstract double computeLocalTermScore(String term, String cat);

  /** 
   *  Return a  term set reduced by a afctor of aggr
   */ 
  public Set getReducedTermSet (int aggr)
  {
    return BagOfWords.extractTermCollection(getReducedFreqList(aggr));
  }

  /** 
   *  Sort score table, pick the first rsize terms and return their
   *  frequency list (i.e. a table containing terms and the number of
   *  times they occur in the entire corpus)
   */
  public WordFrequencyPair[] getReducedFreqList(int aggr)
  {
    Arrays.sort(wsp);
    int size = ii.getTermSetSize();
    int rsize = size / aggr;
    WordFrequencyPair[] rwfp = new WordFrequencyPair[rsize];
    int j = 0;
    System.err.println("Reducing T from "+size+" to "+rsize);
    int stop = size-rsize-1;
    for(int i = size-1; i > stop ; i--)
      rwfp[j++] = new WordFrequencyPair(wsp[i].getWord(), 
                                        ii.getCount(wsp[i].getWord()));
    return rwfp;
  }


  public WordScorePair[] getSortedScores() {
    Arrays.sort(wsp);
    return wsp;
  }

  /** 
     * Get the value of wsp.
     * @return value of wsp.
     */
  public WordScorePair[] getWsp() {
    return wsp;
  }
  
  /**
   * Set the value of wsp.
   * @param v  Value to assign to wsp.
   */
  public void setWsp(WordScorePair[]  v) {
    this.wsp = v;
  }
  

  public void computeLocalScores (String cat){
    System.err.println("Computing LOCAL TSR for "+wsp.length+" terms and category "+cat);
    // convert wsp, initially filled with frequencies, into an  IG score table 
    for (int i = 0; i < wsp.length; i++) {
      PrintUtil.printNoMove("Computing TSR  ...",i);  
      wsp[i].setScore(computeLocalTermScore(wsp[i].getWord(),cat));
    }
    PrintUtil.donePrinting();
  }
  
  public void computeGlobalScoresSUM (){
    System.err.println("Computing GLOBAL TSR for "+wsp.length+" using f_sum");
    // convert wsp, initially filled with frequencies, into a score table
    Set cs = ii.getCategorySet();
    for (int i = 0; i < wsp.length; i++) {
      PrintUtil.printNoMove("Computing TSR  ...",i);
      for (Iterator e = cs.iterator(); e.hasNext() ;)
        wsp[i].setScore(wsp[i].getScore() + 
                        computeLocalTermScore(wsp[i].getWord(), (String)e.next()));
    }
    PrintUtil.printNoMove("Computing TSR  ...",wsp.length);
    PrintUtil.donePrinting();
  }

  public void computeGlobalScoresMAX (){
    System.err.println("Computing GLOBAL TSR for "+wsp.length+" using f_max");
    // convert wsp, initially filled with frequencies, into a score table
    Set cs = ii.getCategorySet();
    for (int i = 0; i < wsp.length; i++) {
      PrintUtil.printNoMove("Computing TSR  ...",i);
      for (Iterator e = cs.iterator(); e.hasNext() ;){
        double s = computeLocalTermScore(wsp[i].getWord(), (String)e.next());
        wsp[i].setScore(wsp[i].getScore() > s ? wsp[i].getScore() : s);
      }
    }
    PrintUtil.printNoMove("Computing TSR  ...",wsp.length);
    PrintUtil.donePrinting();
  }

  public void computeGlobalScoresWAVG (){
    System.err.println("Computing GLOBAL TSR for "+wsp.length+" using f_wavg");
    // convert wsp, initially filled with frequencies, into a score table
    Set cs = ii.getCategorySet();
    for (int i = 0; i < wsp.length; i++) {
      PrintUtil.printNoMove("Computing TSR  ...",i);
      for (Iterator e = cs.iterator(); e.hasNext() ;){
        String cat = (String)e.next(); 
        wsp[i].setScore(wsp[i].getScore() + 
                        ii.getCatGenerality(cat) *
                        computeLocalTermScore(wsp[i].getWord(), cat));
      }
    }
    PrintUtil.donePrinting();
  }
}
