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
package modnlp.tc.dstruct;

import modnlp.dstruct.*;

import java.io.*;
import java.util.Vector;
import java.util.Enumeration;

/**
 *  Store ParsedNewsItem's.
 *
 * @author  S. Luz &#60;luzs@acm.org&#62;
 * @version <font size=-1>$Id: ParsedText.java,v 1.1.1.1 2005/05/26 13:59:30 amaral Exp $</font>
 * @deprecated Used ParsedCorpus instead
 * @see  
*/
public class ParsedText extends Vector
{

  public ParsedText () 
  {
    super();
  }

  public boolean addNewsItem (ParsedNewsItem pni){
    return add(pni);
  }

  public void append(ParsedText pt){
    try {
      addAll(pt);
    }
    catch (Exception e)
      {
        System.err.println("Error appending parsed text");
        e.printStackTrace();
        System.exit(0);
      }
  }

  /**
   * Get the sub-corpus defined by documents belonging to category cat
   */
  public ParsedText getCategSubCorpus(String cat) {
    ParsedText pt = new ParsedText();
    for (Enumeration e = this.elements() ; e.hasMoreElements() ;){
      ParsedNewsItem pni = (ParsedNewsItem)e.nextElement();
      if ( pni.isOfCategory(cat) )
        pt.addNewsItem(pni);
    }
    System.err.println("Subcorpus for "+cat+
                       " contains "+pt.size()*100/this.size()+
                       "% of texts.");
    return pt;
  }
  
  /**
   * Get the overall probability of category cat classifying a
   * document in the corpus represented by this ParsedText
   * (i.e. category generality)
   */
  public double getCategProbability(String cat) {
    int size = 0;
    for (Enumeration e = this.elements() ; e.hasMoreElements() ;){
      ParsedNewsItem pni = (ParsedNewsItem)e.nextElement();
      if ( pni.isOfCategory(cat) )
        size++;
    }
    return (double) size/this.size();
  }

  /** 
   * Get the joint probability of term 'term' occurring in a document and category 'cat' classifying it.
   *
   * NB: this is very inneficient. Use only for one-off estimates. Use
   * BVProbabilityModel for global estimates.
   */
  public Probabilities getProbabilities (String term, String cat){
    int c = 0, t = 0, tc = 0, ntc = 0, tnc = 0, ntnc = 0;
    for (Enumeration e = this.elements() ; e.hasMoreElements() ;){
      ParsedNewsItem pni = (ParsedNewsItem)e.nextElement();
      boolean docContainsTerm = (new BagOfWords(pni.getText())).containsTerm(term);
      if ( pni.isOfCategory(cat) ) {
        c++; 
        if ( docContainsTerm ) {
          t++;
          tc++;
        }
        else
          ntc++;
      }
      else 
        if ( docContainsTerm ){ 
          t++;
          tnc++;
        }
        else
          ntnc++;
    }
    double size = this.size();
    //System.err.println("\n t    = "+t+"\n c    = "+c+"\n tc   = "+tc+"\n ntc  = "+tnc+"\n tnc  = "+tnc+"\n ntnc = "+ntnc); 
    return new Probabilities(t/size,
                             c/size,
                             tc/size,
                             tnc/size,
                             ntc/size,
                             ntnc/size);
  }


  public String toString(){
    StringBuffer sb = new StringBuffer();
    for (Enumeration e = this.elements() ; e.hasMoreElements() ;){
      ParsedNewsItem pni = (ParsedNewsItem)e.nextElement();
      sb.append(pni.toString());
    }
    return sb.toString();
  }


}
