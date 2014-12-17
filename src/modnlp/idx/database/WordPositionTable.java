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

import modnlp.dstruct.FrequencyHash;
import modnlp.dstruct.IntegerSet;
import modnlp.dstruct.TokenMap;

import com.sleepycat.je.Environment;
import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.bind.tuple.StringBinding;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.DatabaseNotFoundException;
import com.sleepycat.je.DeadlockException;
import com.sleepycat.je.Cursor;
import com.sleepycat.je.OperationStatus;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.LockMode;

/**
 *  Store words and the positions in which they occur as follows:
 *  <pre>  
 *        KEY      |  DATA
 *    -------------|-------------------
 *       word      |  [pos1, pos2, ...]
 *  </pre>
 *
 * @author  S Luz &#60;luzs@acm.org&#62;
 * @version <font size=-1>$Id: WordPositionTable.java,v 1.2 2006/05/22 17:26:02 amaral Exp $</font>
 * @see  
*/
public class WordPositionTable extends Table {

  public WordPositionTable (Environment env, String fn, boolean write) 
    throws DatabaseNotFoundException {
    super(env,fn,write);
  }

  public int getFileIDNumber () {
    try {
      return (new Integer(database.getDatabaseName())).intValue();      
    } catch (DatabaseException e) {
      return -1;
    }
  }

  public void put(String sik, IntegerSet set) {
    TupleBinding isb = new IntegerSetBinding();
    DatabaseEntry key = new DatabaseEntry();
    DatabaseEntry data = new DatabaseEntry();
    StringBinding.stringToEntry(sik, key);
    isb.objectToEntry(set, data);
    put(key,data);
  }

  /**
   * Remove all entries whose file ids match founo and return a
   * TokenMap of the entries removed
   *
   * @param founo an <code>int</code> value
   */
  public TokenMap removeFile () {
    TokenMap tm = null;
    Cursor c = null;
    try {
      tm = new TokenMap();
      c = database.openCursor(null, null);
      TupleBinding kb = new StringBinding();
      DatabaseEntry key = new DatabaseEntry();
      TupleBinding isb = new IntegerSetBinding();
      DatabaseEntry data = new DatabaseEntry();
      while (c.getNext(key, data, LockMode.DEFAULT) 
             == OperationStatus.SUCCESS) {
        String sik = (String) kb.entryToObject(key);
        IntegerSet set  = (IntegerSet) isb.entryToObject(data);
        tm.put(sik, set);
        //c.delete();
      }
      c.close();
      String founo = database.getDatabaseName();
      close();
      environment.removeDatabase(null,founo);
    }
    catch (DeadlockException e) {
      logf.logMsg("Deadlock deleting record " + e);
    }
    catch (DatabaseNotFoundException e) {
      logf.logMsg("Error removing wordPositionTable" + e);
    }
    catch (DatabaseException e) {
      logf.logMsg("Error accessing wordPositionTable" + e);
    }
    finally {
      try{ c.close(); }catch(DatabaseException e){};
      //return tm;
    }
    return tm;
  }

  public IntegerSet fetch (String sik) {
    TupleBinding isb = new IntegerSetBinding();
    DatabaseEntry key = new DatabaseEntry();
    DatabaseEntry data = new DatabaseEntry();
    IntegerSet set = null;
    StringBinding.stringToEntry(sik, key);
    try {
      if (database.get(null,key, data, LockMode.DEFAULT) == OperationStatus.SUCCESS) 
        set = (IntegerSet)isb.entryToObject(data);
    } catch(DeadlockException e) {
      logf.logMsg("Deadlock reading from dbname:"+e);
    }
    catch(DatabaseException e) {
      logf.logMsg("Error reading from DB "+dbname+": "+e);
    }
    return set;
  }

  public FrequencyHash getFrequencyTable(boolean nocase){
    FrequencyHash fh = new FrequencyHash();
    try {
      Cursor c = database.openCursor(null, null);
      TupleBinding kb = new StringBinding();
      TupleBinding isb = new IntegerSetBinding();
      DatabaseEntry key = new DatabaseEntry();
      DatabaseEntry data = new DatabaseEntry();
      //System.out.println("Word positions for fileno "+database.getDatabaseName()+":");
      while (c.getNext(key, data, LockMode.DEFAULT) == 
             OperationStatus.SUCCESS) {
        String sik = (String) kb.entryToObject(key);
        IntegerSet set  = (IntegerSet) isb.entryToObject(data);
        fh.add(sik,set.size(),nocase);
      }
      c.close();
    }
    catch (DatabaseException e) {
      logf.logMsg("Error accessing wordPositionTable" + e);
    }
    return fh;
  }

  public void dump () {
    try {
      Cursor c = database.openCursor(null, null);
      TupleBinding kb = new StringBinding();
      TupleBinding isb = new IntegerSetBinding();
      DatabaseEntry key = new DatabaseEntry();
      DatabaseEntry data = new DatabaseEntry();
      System.out.println("Word positions for fileno "+database.getDatabaseName()+":");
      while (c.getNext(key, data, LockMode.DEFAULT) == 
             OperationStatus.SUCCESS) {
        String sik = (String) kb.entryToObject(key);
        IntegerSet set  = (IntegerSet) isb.entryToObject(data);
        System.out.println(sik+" < "+set+">");
      }
      c.close();
    }
    catch (DatabaseException e) {
      logf.logMsg("Error accessing wordPositionTable" + e);
    }
  }


}
