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
package modnlp.idx.query;

import modnlp.dstruct.WordForms;
/**
 *  Store words and the max number of words to scan between each
 *  word and the keyword. Example:
 *  <pre>
 *  query = a+[2]be+see+Dee, keyword = see
 *         
 *  will set a left Horizon with:
 *
 *  horizonArray = [ 4 , 1 ], 
 *  wordArray =    [ a,  be ]
 *  maxSearchHorizon = 4 
 *  </pre>
 *  That is, one can look back up to 4 words to be able to match "a", 1
 *  word back to match "be". maxSearchHorizon is redundant.
 *
 * @author  S Luz &#60;luzs@acm.org&#62;
 * @version <font size=-1>$Id: $</font>
 * @see WordQuery.getLeftHorizon()
 * @see WordQuery.getRightHorizon()
*/

public class Horizon {

  
  int [] horizonArray;
  WordForms [] wordArray;
  int maxSearchHorizon;

  /**
   * Creates a new <code>Horizon</code> instance.
   *
   * @param ha an <code>int[]</code> value
   * @param wa a <code>String[]</code> value
   * @param sh an <code>int</code> value
   */
  public Horizon(int[] ha, WordForms[] wa, int sh){
    horizonArray = ha;
    wordArray = wa;
    maxSearchHorizon = sh;
  }

  /**
   * Describe <code>getHorizonArray</code> method here.
   *
   * @return an <code>int[]</code> value
   */
  public int[] getHorizonArray () {
    return horizonArray;
  }

  /**
   * Describe <code>setHorizonArray</code> method here.
   *
   * @param ia an <code>int[]</code> value
   */
  public void setHorizonArray (int[] ia) {
    horizonArray = ia;
  }

  /**
   * Describe <code>getWordArray</code> method here.
   *
   * @return a <code>String[]</code> value
   */
  public WordForms[] getWordArray () {
    return wordArray;
  }

  /**
   * Describe <code>setWordArray</code> method here.
   *
   * @param wa a <code>String[]</code> value
   */
  public void setWordArray (WordForms[] wa) {
    wordArray = wa;
  }

  /**
   * Describe <code>getMaxSearchHorizon</code> method here.
   *
   * @return an <code>int</code> value
   */
  public int getMaxSearchHorizon () {
    return maxSearchHorizon;
  }

  /**
   * Describe <code>setMaxSearchHorizon</code> method here.
   *
   * @param sh an <code>int</code> value
   */
  public void setMaxSearchHorizon (int sh) {
    maxSearchHorizon = sh;
  }


}
