/**
 *  (c) 2007 S Luz <luzs@acm.org>
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

import modnlp.dstruct.SubcorpusMap;
import modnlp.dstruct.SubcorpusDelimPair;

import com.sleepycat.je.Environment;
import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.bind.tuple.StringBinding;
import com.sleepycat.bind.tuple.IntegerBinding;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.DatabaseNotFoundException;
import com.sleepycat.je.DeadlockException;
import com.sleepycat.je.Cursor;
import com.sleepycat.je.OperationStatus;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.LockMode;
import com.sleepycat.je.SecondaryDatabase;
import com.sleepycat.je.SecondaryConfig;
import com.sleepycat.je.SecondaryCursor;
import com.sleepycat.je.EnvironmentConfig;

import java.io.File;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.Set;


/**
 *  Store the delimiters so sub-sections of corpus documents
 *  <pre>  
 *        KEY        |  DATA
 *    ---------------|-------------------
 *      section_name |  [start_pos, end_pos]
 *  </pre>
 *
 * @author  S Luz &#60;luzs@acm.org&#62;
 * @version <font size=-1>$Id: SubcorpusTable.java,v 1.2 2006/05/22 17:26:02 amaral Exp $</font>
 * @see  
*/
public class SubcorpusTable extends Table {
  SecondaryDatabase posKeyDatabase = null;
  SubcorpusDelimPairBinding scdpBinding = new SubcorpusDelimPairBinding();
  TupleBinding stringBinding = new StringBinding();  

  public SubcorpusTable (Environment env, String fn, boolean write, boolean opensecond) 
    throws DatabaseNotFoundException
  {
    super(env,makeDBName(fn),write);
    setupSCT(env,fn,write,opensecond);
  }

  public SubcorpusTable (Environment env, String fn, boolean write) 
    throws DatabaseNotFoundException
  {
    super(env,makeDBName(fn),write);
    setupSCT(env,fn,write,true);
  }


  private final void setupSCT (Environment env, String fn, boolean write, boolean opensecond) {
    try {     
      // use a secondary db to keep records sorted by frequency
      SubcorpusKeyCreator skc = new SubcorpusKeyCreator(new SubcorpusDelimPairBinding());
      SecondaryConfig sc = new SecondaryConfig();
      sc.setKeyCreator(skc);
      sc.setReadOnly(!write);
      sc.setAllowCreate(write);
      sc.setSortedDuplicates(true);
      String scname = makeSecondaryDBName(fn);
      if (write || opensecond)
        posKeyDatabase = env.openSecondaryDatabase(null, 
                                                   scname, 
                                                   database,
                                                   sc);
    }
    catch (DatabaseException e) {
      logf.logMsg("Error opening secondary FreqTable", e);
      try {
        if (posKeyDatabase != null) 
          posKeyDatabase.close();
        if (database != null)
          database.close();         
      }
      catch (DatabaseException se) {
        logf.logMsg("Error trying to close secondary FreqTable", se);
      } 
    } 
  }

  public int getFileIDNumber () {
    try {
      String id = database.getDatabaseName();
      return (new Integer(id.substring(0,id.indexOf('.')))).intValue();      
    } catch (DatabaseException e) {
      return -1;
    }
  }

  public void put(String sik, SubcorpusDelimPair tuple) {
    TupleBinding isb = new SubcorpusDelimPairBinding();
    DatabaseEntry key = new DatabaseEntry();
    DatabaseEntry data = new DatabaseEntry();
    StringBinding.stringToEntry(sik, key);
    isb.objectToEntry(tuple, data);
    put(key,data);
  }

  /**
   * Remove all entries whose file ids match founo and return a
   * SubcorpusMap of the entries removed
   *
   * @param founo an <code>int</code> value
   */
  public SubcorpusMap removeFile () {
    SubcorpusMap tm = null;
    Cursor c = null;
    try {
      tm = new SubcorpusMap();
      c = database.openCursor(null, null);
      TupleBinding kb = new StringBinding();
      DatabaseEntry key = new DatabaseEntry();
      TupleBinding isb = new SubcorpusDelimPairBinding();
      DatabaseEntry data = new DatabaseEntry();
      while (c.getNext(key, data, LockMode.DEFAULT) 
             == OperationStatus.SUCCESS) {
        String sik = (String) kb.entryToObject(key);
        SubcorpusDelimPair set  = (SubcorpusDelimPair) isb.entryToObject(data);
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

  public SubcorpusDelimPair fetch (String sik) {
    TupleBinding isb = new SubcorpusDelimPairBinding();
    DatabaseEntry key = new DatabaseEntry();
    DatabaseEntry data = new DatabaseEntry();
    SubcorpusDelimPair dlm = null;
    StringBinding.stringToEntry(sik, key);
    try {
      if (database.get(null,key, data, LockMode.DEFAULT) == OperationStatus.SUCCESS){ 
        dlm = (SubcorpusDelimPair)isb.entryToObject(data);
        //System.out.println("SubcorpusTable---->"+dlm);
      }
    } catch(DeadlockException e) {
      logf.logMsg("Deadlock reading from dbname:"+e);
    }
    catch(DatabaseException e) {
      logf.logMsg("Error reading from DB "+dbname+": "+e);
    }
    return dlm;
  }

  public final SubcorpusDelimPair[] getSubcorpusDelimPairs(Set ids){
    SubcorpusDelimPair[] scdps = new SubcorpusDelimPair[ids.size()];
    int i = 0;
    for (Iterator p = ids.iterator(); p.hasNext(); ) {
      String id = (String)p.next();
      //System.out.println("SID---->"+id+"---"+i);
      scdps[i++] = fetch(id);
    }
    return scdps;
  }


  public void dump () {
    try {
      Cursor c = database.openCursor(null, null);
      TupleBinding kb = new StringBinding();
      SubcorpusDelimPairBinding isb = new SubcorpusDelimPairBinding();
      DatabaseEntry key = new DatabaseEntry();
      DatabaseEntry data = new DatabaseEntry();
      System.out.println("Word positions for fileno "+database.getDatabaseName()+":");
      while (c.getNext(key, data, LockMode.DEFAULT) == 
             OperationStatus.SUCCESS) {
        String sik = (String) kb.entryToObject(key);
        SubcorpusDelimPair set  = (SubcorpusDelimPair) isb.entryToObject(data);
        System.out.println(sik+" < "+set+">");
      }
      c.close();
    }
    catch (DatabaseException e) {
      logf.logMsg("Error accessing wordPositionTable" + e);
    }
  }

  public static boolean exists(Environment env, int fno){
    return Table.exists(env, makeDBName(fno));
  }

  public static boolean exists(Environment env, String fn){
    return Table.exists(env, makeDBName(fn));
  }

  /**
   * given a file offset position, retrieve a section enclosing that
   * position or null if none such section exists
   *
   * CAVEAT: This assumes no nested sections are indexed. 
   *
   * @param os an <code>int</code> an offset position for this file 
   * @return a <code>String</code> id of a section enclosing pos or
   * null if none such section exists
   */
  public String getSectionID(int pos) {
    try {
      SecondaryCursor c = posKeyDatabase.openSecondaryCursor(null, null);
      DatabaseEntry key = new DatabaseEntry();
      IntegerBinding.intToEntry(pos, key);
      DatabaseEntry pkey = new DatabaseEntry();
      DatabaseEntry data = new DatabaseEntry();
      SubcorpusDelimPair scdp = null;
      OperationStatus status = c.getSearchKeyRange(key, pkey, data,
                                                 LockMode.DEFAULT);
      if (status == OperationStatus.SUCCESS)
        if (c.getPrev(key, pkey, data, LockMode.DEFAULT) != OperationStatus.SUCCESS){
          c.close();
          return null;
        }
        else {
          c.close();
          scdp = (SubcorpusDelimPair) scdpBinding.entryToObject(data);
          if (pos >= scdp.getBegin() && pos <= scdp.getEnd())
            return (String)stringBinding.entryToObject(pkey);
          else
            return null;
        }
      else {
        if (c.getLast(key, pkey, data, LockMode.DEFAULT) == OperationStatus.SUCCESS) {
          c.close();
          scdp = (SubcorpusDelimPair) scdpBinding.entryToObject(data);
          if (pos >= scdp.getBegin() && pos <= scdp.getEnd())
            return (String)stringBinding.entryToObject(pkey);
          else
            return null;
        }
        c.close();
        return null;
      }
    }
    catch (DatabaseException e) {
      logf.logMsg("Error accessing secondary cursor for SubcorpusTable" , e);
      return null;
    }
  }

  public SecondaryCursor getSecondaryCursor() throws DatabaseException{
    return posKeyDatabase.openSecondaryCursor(null, null);
  }

  public static final String makeDBName(String fn){
    return fn+".sc";
  }

  public static final String makeDBName(int fn){
    return fn+".sc";
  }

  public static final String makeSecondaryDBName(int fn){
    return "sec"+makeDBName(fn);
  }

  public static final String makeSecondaryDBName(String fn){
    return "sec"+makeDBName(fn);
  }


  public void close () {
    // ignore operation status
    try {
      if (posKeyDatabase != null) {
        posKeyDatabase.close();
        super.close();
        posKeyDatabase = null;
      }
      super.close();
    } catch(DatabaseException e) {
      logf.logMsg("Error closing DB "+dbname,e);
      //e.printStackTrace(System.err);
    };
  }

  public void finalize () {
      close();
  }


  public static void main (String[] argv){
    try {
      com.sleepycat.je.EnvironmentConfig ec = new com.sleepycat.je.EnvironmentConfig();
      ec.setReadOnly(true);
      Environment e = new Environment(new java.io.File(argv[0]), ec);
      String f = argv[1];  // file id
      SubcorpusTable s = new SubcorpusTable(e, f, false);
      java.io.BufferedReader cline 
        = new java.io.BufferedReader(new java.io.InputStreamReader(System.in));
      System.out.print("Enter position or press <ENTER> to quit.\n> ");
      if (argv.length > 2) {
        int p = (new Integer(argv[2])).intValue(); // position
        System.err.println("Position="+p+" is in section "+s.getSectionID(p));
      }

      String req = cline.readLine();
      while ( ! req.equalsIgnoreCase("") ) {
        int p = (new Integer(req)).intValue(); // position
        System.err.println("Position="+p+" is in section "+s.getSectionID(p));
        req = cline.readLine();
      }      
    } catch (Exception e) {
      System.err.println("Error:");
      e.printStackTrace(System.err);
    }
  }


}
