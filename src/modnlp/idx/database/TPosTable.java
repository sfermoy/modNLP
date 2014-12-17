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

import modnlp.dstruct.IntOffsetArray;

import com.sleepycat.je.Environment;
import com.sleepycat.bind.tuple.IntegerBinding;
import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.BtreeStats;
import com.sleepycat.je.StatsConfig;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.DatabaseNotFoundException;
import com.sleepycat.je.Cursor;
import com.sleepycat.je.OperationStatus;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.LockMode;

/**
 *  Store all files token offsets for a particular file (seq_integer)
 *
 *  <pre>  
 *       KEY           |  DATA
 *   ------------------|-------------------
 *      seq_integer    |  [pos0, pos1-pos0, pos2-pos1, ...]
 *  </pre>
 *
 * @author  S Luz &#60;luzs@acm.org&#62;
 * @version <font size=-1>$Id: TPosTable.java,v 1.2 2006/05/22 17:26:02 amaral Exp $</font>
 * @see  
*/
public class TPosTable extends Table {

  private int bytesReceived = 0; // no of bytes (in IntOffsetArray) passed to put() 
  private int bytesWritten = 0; // no of bytes actually written

  public TPosTable (Environment env, String fn, boolean write) 
    throws DatabaseNotFoundException{
    super(env,fn,write);
  }

  public void put(int sik, IntOffsetArray fou) {
    TupleBinding ioab = new IntOffsetArrayBinding();
    DatabaseEntry key = new DatabaseEntry();
    DatabaseEntry data = new DatabaseEntry();
    IntegerBinding.intToEntry(sik, key);
    ioab.objectToEntry(fou, data);

    System.out.println("Compression ratio for this file ("+sik+") = "+
                       ((IntOffsetArrayBinding)ioab).getCompressionRatio());

    bytesReceived += ((IntOffsetArrayBinding)ioab).getBytesReceived();
    bytesWritten += ((IntOffsetArrayBinding)ioab).getBytesWritten();
    put(key,data);
  }

  public void remove(int sik) {
    DatabaseEntry key = new DatabaseEntry();
    IntegerBinding.intToEntry(sik, key);
    //System.err.println("Removing "+sik);
    remove(key);
  }

  public int [] getPosArray(int sik) {
    TupleBinding ioab = new IntOffsetArrayBinding();
    DatabaseEntry key = new DatabaseEntry();
    DatabaseEntry data = new DatabaseEntry();
    IntegerBinding.intToEntry(sik, key);
    get(key,data);
    return ((IntOffsetArray)ioab.entryToObject(data)).getPosArray();
  }

  /**
   * <code>getCompressionRatio</code> return the ratio of compression
   * for the <code>IntOffsetArray</code>'s stored in this table: i.e.
   * bytesWritten/bytesReceived;
   *
   * @return the ratio of compression of the array stored or zero, if
   * put() hasn't been called
   */
  public double getCompressionRatio () {
    return bytesReceived == 0 ? 0 : 1 - (double)bytesWritten/bytesReceived;
  }

  /**
   * Get no. of bytes passed to objectToEntry() so far.
   *
   * @return an <code>int</code> value
   */
  public int getBytesReceived () {
    return bytesReceived;
  }

  /**
   * Get no of bytes actually written into the Object stream so far
   *
   * @return a <code>int</code> value
   */
  public int getBytesWritten () {
    return bytesWritten;
  }


  public void  dump () {
    try {
      Cursor c = database.openCursor(null, null);
      TupleBinding ioab = new IntOffsetArrayBinding();
      DatabaseEntry key = new DatabaseEntry();
      DatabaseEntry data = new DatabaseEntry();
      while (c.getNext(key, data, LockMode.DEFAULT) == 
             OperationStatus.SUCCESS) {
        int sik = IntegerBinding.entryToInt(key);
        IntOffsetArray iosa = (IntOffsetArray)ioab.entryToObject(data);
        System.out.println(sik+" = "+iosa.toPosString());
        System.out.println(sik+" (offset) = "+iosa.toString());
      }
      c.close();
    }
    catch (DatabaseException e) {
      logf.logMsg("Error accessing TPosTable" + e);
    }
  }

}
