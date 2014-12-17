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

import java.util.Vector;
import java.util.Collection;

/**
 *  Store all forms of a keyword (or wildcard)
 *
 * 
 * @author  Saturnino Luz &#60;luzs@acm.org&#62;
 * @version <font size=-1>$Id: WordForms.java,v 1.1 2006/05/22 17:26:02 amaral Exp $</font>
 * @see  
*/
public class WordForms extends Vector {

  // keyword stores the canonical (unexpanded) form of this set of words
  String keyword;


  public WordForms (String key) {
    super();
    this.keyword = key;
  }

  public WordForms () {
    super();
    this.keyword = null;
  }

  public WordForms (String k, Collection c) {
    super(c);
    this.keyword = k;
  }

  /**
   * Get the value of keyword.
   * @return value of keyword.
   */
  public String getKeyword() {
    return keyword;
  }
  
  /**
   * Set the value of keyword.
   * @param v  Value to assign to keyword.
   */
  public void setKeyword(String  v) {
    this.keyword = v;
  }


}
