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
 *  Store <code>ParsedDocument</code>s.
 *
 * @author  S. Luz &#60;luzs@acm.org&#62;
 * @version <font size=-1>$Id: ParsedCorpus.java,v 1.1.1.1 2005/05/26 13:59:30 amaral Exp $</font>
 * @see ParsedDocument
*/
public class ParsedCorpus extends Vector
{

  public ParsedCorpus () 
  {
    super();
  }

  /**
   * Add a new categorised text to this corpus
   *
   * @param pni a <code>ParsedDocument</code> containing a
   * categorised, uniquely indentified text.
   * @return true if item succesfully added, false otherwise.
   * @deprecated This method was used (and named) in the original
   * REUTERS news classification system. Use addParsedDocument
   * instead.
   */
  public boolean addNewsItem (ParsedDocument pni){
    return add(pni);
  }

  /**
   * Add a new categorised text to this corpus
   *
   * @param pni a <code>ParsedDocument</code> containing a
   * categorised, uniquely indentified text.
   * @return true if item succesfully added, false otherwise.
   */
  public boolean addParsedDocument (ParsedDocument pni){
    return add(pni);
  }


  /**
   * Concatenate <code>pt</code> to this object.
   *
   * @param pt a <code>ParsedCorpus</code> containing categorised texts
   * to be appended to this object.
   */
  public void append(ParsedCorpus pt){
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
  public ParsedCorpus getCategSubCorpus(String cat) {
    ParsedCorpus pt = new ParsedCorpus();
    for (Enumeration e = this.elements() ; e.hasMoreElements() ;){
      ParsedDocument pni = (ParsedDocument)e.nextElement();
      if ( pni.isOfCategory(cat) )
        pt.addParsedDocument(pni);
    }
    System.err.println("Subcorpus for "+cat+
                       " contains "+pt.size()*100/this.size()+
                       "% of texts.");
    return pt;
  }
  
  /**
   * Get the overall probability of category cat classifying a
   * document in the corpus represented by this ParsedCorpus
   * (i.e. category generality)
   */
  public double getCategProbability(String cat) {
    int size = 0;
    for (Enumeration e = this.elements() ; e.hasMoreElements() ;){
      ParsedDocument pni = (ParsedDocument)e.nextElement();
      if ( pni.isOfCategory(cat) )
        size++;
    }
    return (double) size/this.size();
  }

  /** 
   * Get the joint probability of term 'term' occurring in a document
   * and category 'cat' classifying it.
   *
   * NB: this is very inneficient. Use only for one-off estimates. Use
   * BVProbabilityModel for global estimates.
   */
  public Probabilities getProbabilities (String term, String cat){
    int c = 0, t = 0, tc = 0, ntc = 0, tnc = 0, ntnc = 0;
    for (Enumeration e = this.elements() ; e.hasMoreElements() ;){
      ParsedDocument pni = (ParsedDocument)e.nextElement();
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
      ParsedDocument pni = (ParsedDocument)e.nextElement();
      sb.append(pni.toString());
    }
    return sb.toString();
  }


}
