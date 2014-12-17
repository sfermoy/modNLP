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
package modnlp.idx.database;

import com.sleepycat.bind.tuple.IntegerBinding;
import com.sleepycat.je.DatabaseEntry;
import java.util.Comparator;
/**
 *  Compare two integers in descending order
 *
 * @author  S Luz &#60;luzs@acm.org&#62;
 * @version <font size=-1>$Id: DescIntComparator.java,v 1.1 2006/05/22 17:26:02 amaral Exp $</font>
 * @see  
*/

public class DescIntComparator implements Comparator, java.io.Serializable//<byte[]> 
{

  /**
   * Compare two objects for sorting in descending order.  Should
   * return negative if d2 < d1, positive if d2 > d1, and 0
   * otherwise. Objects d1, d2 will be arrays of unsigned bytes.
   *
   * @param d1 an <code>Object</code> value
   * @param d2 an <code>Object</code> value
   * @return an <code>int</code> value
   */
  public int compare(Object d1, Object d2) {  
  //public int compare(byte[] key2, byte[] key1) {
    byte[] key2 = (byte[])d1;
    byte[] key1 = (byte[])d2;

    int a1Len = key1.length;
    int a2Len = key2.length;
    
    int limit = Math.min(a1Len, a2Len);

    for (int i = 0; i < limit; i++) {
	    byte b1 = key1[i];
	    byte b2 = key2[i];
	    if (b1 == b2) {
        continue;
	    } 
      else {
        /* Bytes are signed, so convert to shorts so that
           we effectively do an unsigned byte comparison. */
        return (b1 & 0xff) - (b2 & 0xff);
	    }
    }
    return (a1Len - a2Len);
  }
}
