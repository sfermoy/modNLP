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
import java.util.LinkedHashSet;
import java.util.Iterator;


/**
 *  A set of integers. Currently used for storing word position offsets in <code>modnlp.idx</code>
 *
 * @author  S Luz &#60;luzs@acm.org&#62;
 * @version <font size=-1>$Id: IntegerSet.java,v 1.2 2006/05/22 17:26:02 amaral Exp $</font>
 * @see modnlp.idx.database.Dictionary
 * @see modnlp.idx.database.IntegerSetBinding 
*/

public class IntegerSet extends LinkedHashSet {

  public String toString() {
    StringBuffer sb = new StringBuffer();
    for (Iterator f = this.iterator(); f.hasNext() ;)
      sb.append(f.next()+" ");
    return sb.toString();
  }

  public boolean contains (int pos) {
    return contains(new Integer(pos));
  }

}
