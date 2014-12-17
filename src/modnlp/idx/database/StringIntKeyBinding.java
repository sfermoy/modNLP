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

import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;

/**
 *  Custom tuple binding for a string followed by and integer (as in
 *  the key for WordPositionTable)
 *
 * @author  S Luz &#60;luzs@acm.org&#62;
 * @version <font size=-1>$Id: StringIntKeyBinding.java,v 1.2 2006/05/22 17:26:02 amaral Exp $</font>
 * @see  
*/

public class StringIntKeyBinding extends TupleBinding {

  public void objectToEntry(Object object, TupleOutput to) {
    StringIntKey sik = (StringIntKey)object;
    to.writeString(sik.getString());
    to.writeInt(sik.getInt());
  }

  public Object entryToObject(TupleInput ti) {
    
    return new StringIntKey(ti.readString(), ti.readInt());

    }


}
