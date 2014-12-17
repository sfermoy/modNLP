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

import modnlp.dstruct.StringSet;

import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;
import java.util.Iterator;
/**
 *  Tuple binding for storing sets of Strings into a JE database
 *
 * @author  S Luz &#60;luzs@acm.org&#62;
 * @version <font size=-1>$Id: StringSetBinding.java,v 1.2 2006/05/22 17:26:02 amaral Exp $</font>
 * @see  CaseTable
*/
public class StringSetBinding extends TupleBinding {
  
  // Write a StringSet to a TupleOutput
  public void objectToEntry(Object object, TupleOutput to) {
    
    StringSet set = (StringSet)object;
    
    for (Iterator f = set.iterator(); f.hasNext() ;)
      to.writeString(((String)f.next()));
  }

  // Convert a TupleInput to a StringSet
  public Object entryToObject(TupleInput ti) {
    
    StringSet set = new StringSet();
    try {
      while (true)
        set.add(ti.readString());
    } 
    catch (IndexOutOfBoundsException e) {}

    return set;
    }
} 

