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
package modnlp.dstruct;
/**
 *  Represent a term and the number of times it occurs
 *
 * @author  Saturnino Luz &#60;luzs@acm.org&#62;
 * @version <font size=-1>$Id: WordFrequencyPair.java,v 1.1.1.1 2005/05/26 13:59:30 amaral Exp $</font>
 * @see  
*/
public class WordFrequencyPair implements Comparable 
{

  private String word = null;
  private Integer count = null;

  public WordFrequencyPair(String s, int c)
  {
    setWord(s);
    setCount( new Integer(c) );
  }
  public WordFrequencyPair(String s, Integer c)
  {
    setWord(s);
    setCount(c);
  }

  /**
   * Get the value of word.
   * @return value of word.
   */
  public String getWord() {
    return word;
  }
  
  /**
   * Set the value of word.
   * @param v  Value to assign to word.
   */
  public void setWord(String  v) {
    this.word = v;
  }
  
  /**
   * Get the value of count.
   * @return value of count.
   */
  public Integer getIntegerCount() {
    return count;
  }


  public int getCount() {
    return count.intValue();
  }
  
  
  /**
   * Set the value of count.
   * @param v  Value to assign to count.
   */
  public void setCount(Integer  v) {
    this.count = v;
  }
  
  public int compareTo (Object wfp)
  {
    return compareTo((WordFrequencyPair) wfp);
  }
  public int compareTo (WordFrequencyPair wfp)
  {
    return count.compareTo(wfp.getIntegerCount());
  }

}
