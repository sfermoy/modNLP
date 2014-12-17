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
import com.sleepycat.je.SecondaryKeyCreator; 
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.SecondaryDatabase;
import com.sleepycat.bind.tuple.IntegerBinding;

import java.io.IOException; 


/**
 *  Create key for secondary frequency database (key is frequency)
 *
 * @author  S Luz &#60;luzs@acm.org&#62;
 * @version <font size=-1>$Id: FreqKeyCreator.java,v 1.1 2006/05/22 17:26:02 amaral Exp $</font>
 * @see  
*/

public class FreqKeyCreator implements SecondaryKeyCreator {

  public boolean createSecondaryKey(SecondaryDatabase secDb,
                                    DatabaseEntry key,
                                    DatabaseEntry data,                    
                                    DatabaseEntry result) {
    int fq = IntegerBinding.entryToInt(data);
    IntegerBinding.intToEntry(fq,result);
    return true;
  }

}
