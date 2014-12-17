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

import modnlp.util.Maths;
import modnlp.dstruct.WordFrequencyPair;

import java.util.Vector;
import java.util.Enumeration;

/**
 *  Store text as an array of integers (each representing the number of times a term occurs in the text), 
 *  its id and categories (also as a vector).
 *  <b>Note that in order to retrieve the actual term you need to lookup the 
 * template (TSReducedText.reducedTermSet) </b>
 *
 * @author Saturnino Luz &#60;luzs@acm.org&#62;
 * @version <font size=-1>$Id: NewsItemAsOccurVector.java,v 1.1.1.1 2005/05/26 13:59:30 amaral Exp $</font>
 * @see  TSReducedText
*/
public class NewsItemAsOccurVector {

  private Vector categs = null;
  private String id = null;
  private int[] tvect;


  public NewsItemAsOccurVector (Vector categs, int[] tvect, String id)
  {
    super();
    this.id = id;
    this.categs = categs;
    this.tvect = tvect;
  }

  public void addCategory (String topic)
  {
    categs.add(topic);
  }

  public Enumeration getCategories ()
  {
    return categs.elements();
  }

  public Vector getCategVector ()
  {
    return categs;
  }

  public int[] getOccurrenceArray ()
  {
    return tvect;
  }

  // add up all occurrences of terms (reduced) 
  public int getNoOfTems() {
    int total = 0;
    for (int i = 0; i < tvect.length; i++)
      total = total + tvect[i];
    return total;
  }

  /**
   * Return rounded text-size-proportional weighting of term frequency:
   *
   <pre> 
                             1 + log no_occurs_term_i_in_j 
     pweight = round ( 10 x  ------------------------------ )
                                1 + log no_terms_in_j

   </pre> 
   * if <code>no_terms_in_j &gt; 0</code>. Otherwise <code>pweight = 0</code>. 
   *
   * See Manning & Scutze, p. 580 eq (16.1) (slightly modified here to
   * take only occurrences of terms in the reduced term set into
   * account, rather than ALL occurences)
   */
  public long[] getPWEIGHTVector(int length){
    long[] pwv = new long[length];
    int [] oca = this.getOccurrenceArray();
    double dw = 1+Maths.log2(this.getNoOfTems());
    for (int i = 0; i < oca.length ; i++ ){
      pwv[i] = oca[i] == 0 ? 0
        : Math.round(10 * (1+Maths.log2(oca[i]))/dw);
    }
    return pwv;
  }

  /**
   * Return the TFIDF for each vector position. TFIDF calculated as follows:
   *
   * w_ij = no_of_occurrences_of_t_in_d * log ( size_of_corpus / size_of_subcorpus_in_which_t_occurs) 
   *
   * @see <a href="http://www.cs.tcd.ie/courses/baict/baim/ss/part2/mlandtc-4up.pdf">my lecture notes on Machine Learning and Text Categorisation.</a>
   */
  public double[] getTFIDFVector(WordFrequencyPair[] wfp, int nofdocs){
    double[] pwv = new double[wfp.length];
    int [] oca = this.getOccurrenceArray();
    for (int i = 0; i < oca.length ; i++ ){
      pwv[i] = oca[i] * Maths.log2((double)nofdocs/wfp[i].getCount());
    }
    return pwv;
  }

  public boolean[] getBooleanTextArray ()
  {
    boolean[] ba = new boolean[tvect.length];
    for (int i = 0; i < tvect.length; i++)
      ba[i] = tvect[i] > 0 ? true : false;
    return  ba;
  }


  public boolean isOfCategory(String cat){
    for (Enumeration e = categs.elements() ; e.hasMoreElements() ;){
      if ( ((String)e.nextElement()).equals(cat) )
        return true;
    }
    return false;
  }

  /**
   * Get the value of id.
   * @return value of id.
   */
  public String getId() {
    return id;
  }
  
  /**
   * Set the value of id.
   * @param v  Value to assign to id.
   */
  public void setId(String  v) {
    this.id = v;
  }
}
