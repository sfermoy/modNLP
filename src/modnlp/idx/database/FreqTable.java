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

import modnlp.Constants;
import modnlp.idx.query.SubcorpusConstraints;
import modnlp.dstruct.FrequencyHash;

import java.io.PrintWriter;
import com.sleepycat.je.Environment;
import com.sleepycat.bind.tuple.StringBinding;
import com.sleepycat.bind.tuple.IntegerBinding;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.BtreeStats;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.DatabaseNotFoundException;
import com.sleepycat.je.Cursor;
import com.sleepycat.je.CursorConfig;
import com.sleepycat.je.OperationStatus;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.LockMode;
import com.sleepycat.je.SecondaryDatabase;
import com.sleepycat.je.SecondaryConfig;
import com.sleepycat.je.SecondaryCursor;
import com.sleepycat.je.StatsConfig;
import java.util.Iterator;


/**
 *  Store a (case-sensitive) word form and the frequency
 *  with which it occurs
 *
 *  <pre>  
 *       KEY        |  DATA
 *   ---------------|-------------------
 *      wordform    | no_of_occurrences
 *  </pre>
 *
 * @author  S Luz &#60;luzs@acm.org&#62;
 * @version <font size=-1>$Id: FreqTable.java,v 1.2 2006/05/22 17:26:02 amaral Exp $</font>
 * @see  
*/
public class FreqTable extends Table {

  SecondaryDatabase freqKeyDatabase = null;
  private int totalNoOfTokens = -1;

  public FreqTable (Environment env, String fn, boolean write)
    throws DatabaseNotFoundException{
    super(env,fn,write);
    try {     
      // use a secondary db to keep records sorted by frequency
      FreqKeyCreator fkc = new FreqKeyCreator();
      SecondaryConfig sc = new SecondaryConfig();
      sc.setKeyCreator(fkc);
      sc.setReadOnly(!write);
      sc.setAllowCreate(write);
      sc.setSortedDuplicates(true);
      // setting the comparator sometimes causes je to throw a null pointer exception.
      // Investigate why.
      sc.setBtreeComparator(new DescIntComparator());
      String scname = "secfqtable.db";
      freqKeyDatabase = env.openSecondaryDatabase(null, 
                                                  scname, 
                                                  database,
                                                  sc);
    }
    catch (DatabaseException e) {
      logf.logMsg("Error opening secondary FreqTable", e);
      try {
        if (freqKeyDatabase != null) 
          freqKeyDatabase.close();
        if (database != null)
          database.close();         
      }
      catch (DatabaseException se) {
        logf.logMsg("Error trying to close secondary FreqTable", se);
      } 
    } 
  }

 public int getFrequency(String sik) {
    DatabaseEntry key = new DatabaseEntry();
    DatabaseEntry data = new DatabaseEntry();
    int freq = 0;
    StringBinding.stringToEntry(sik, key);
    try {
      if (database.get(null, key, data, LockMode.DEFAULT) ==
          OperationStatus.SUCCESS) 
        freq = IntegerBinding.entryToInt(data);
    }
    catch (DatabaseException e) {
      logf.logMsg("Error reading FreqTable" , e);
    }
    return freq;
 }

  public int put(String sik, int noccur) {
    DatabaseEntry key = new DatabaseEntry();
    DatabaseEntry data = new DatabaseEntry();
    int freq = noccur;
    StringBinding.stringToEntry(sik, key);
    try {
      if (database.get(null, key, data, LockMode.DEFAULT) ==
          OperationStatus.SUCCESS) {
        freq = IntegerBinding.entryToInt(data)+noccur;
      }
      IntegerBinding.intToEntry(freq, data);
      put(key,data);
      totalNoOfTokens = getTotalNoOfTokens() + noccur;
    }
    catch (DatabaseException e) {
      logf.logMsg("Error updating FreqTable" , e);
    }
    return freq;
  }

  public int remove(String sik, int noccur) {
    DatabaseEntry key = new DatabaseEntry();
    DatabaseEntry data = new DatabaseEntry();
    int freq = 0;
    StringBinding.stringToEntry(sik, key);
    try {
      if (database.get(null, key, data, LockMode.DEFAULT) ==
          OperationStatus.SUCCESS) {
        freq = IntegerBinding.entryToInt(data)-noccur;
        if (freq == 0)
          remove(key);
        else {
          IntegerBinding.intToEntry(freq, data);
          put(key,data);
        }
        totalNoOfTokens = getTotalNoOfTokens() - noccur;
      }
    }
    catch (DatabaseException e) {
      logf.logMsg("Error reading FreqTable" , e);
    }
    return freq;
  }

  public int getTotalNoOfTokens(){
    int tnt = 0;
    if (totalNoOfTokens > -1)
      return totalNoOfTokens;

    try {
      Cursor c = database.openCursor(null, null);
      DatabaseEntry key = new DatabaseEntry();
      DatabaseEntry data = new DatabaseEntry();
      while (c.getNext(key, data, LockMode.DEFAULT) == 
             OperationStatus.SUCCESS) {
        tnt  += IntegerBinding.entryToInt(data);
      }
      c.close();
    }
    catch (DatabaseException e) {
      logf.logMsg("Error accessing FreqTable" , e);
      return -1;
    }
    totalNoOfTokens = tnt;
    return totalNoOfTokens;
  }

  /**
   * Retrive total number of types, where each (case-sensitive) form
   * of a word counts as a type. Possibly not what you are after. See
   * {@link modnlp.idx.database.CaseTable#getTotalNoOfTokens} for an
   * alternative that corresponds to the usual definition of number of
   * types.
   *
   * @return an <code>int</code> value
   */
  public int getTotalNoOfTypes(){
    try {
      StatsConfig stc = new StatsConfig();  // stc.setFast(true);
      BtreeStats dbs = (BtreeStats)database.getStats(stc);
      return (int)dbs.getLeafNodeCount();
    }
    catch (DatabaseException e) {
      logf.logMsg("Error accessing FileTable" + e);
      return 0;
    }
  }

  /**
   * Return the case-sensitive type token ratio. That's probably not a
   * very useful metric. See
   * {@link modnlp.idx.database.Dictionary#getTypeTokenRatio} for a better
   * alternative.
   *
   * @return a <code>double</code> value
   * @see  modnlp.idx.database.Dictionary#getTypeTokenRatio
   */
  public double getTypeTokenRatio() {
    return (double)getTotalNoOfTypes()/getTotalNoOfTokens();
  }


  public void  dump () {
    try {
      Cursor c = database.openCursor(null, null);
      DatabaseEntry key = new DatabaseEntry();
      DatabaseEntry data = new DatabaseEntry();
      while (c.getNext(key, data, LockMode.DEFAULT) == 
             OperationStatus.SUCCESS) {
        String sik = StringBinding.entryToString(key);
        int freq  = IntegerBinding.entryToInt(data);
        System.out.println(sik+" = "+freq);
      }
      c.close();
      System.out.println("General stats:\n============= ");
      System.out.println("Total number of tokens = "+getTotalNoOfTokens());
      System.out.println("Total number of types = "+getTotalNoOfTypes());
      System.out.println("Type-token ratio (case-sensitive) = "+getTypeTokenRatio());
    }
    catch (DatabaseException e) {
      logf.logMsg("Error accessing FreqTable" , e);
    }
  }

  /**
   * Describe <code>printSortedFreqList</code> method here.
   *
   * @param os a <code>PrintWriter</code> value
   */
  public void printSortedFreqList (PrintWriter os) {
    printSortedFreqList(os, 0);
  }

  public void printSortedFreqList (PrintWriter os, int max) {
    printSortedFreqList(os, 0, max ,true);
  }


  public void printSortedFreqList (PrintWriter os, int from, int max, boolean nocase) {
    try {
      SecondaryCursor c = freqKeyDatabase.openSecondaryCursor(null, null);
      DatabaseEntry key = new DatabaseEntry();
      DatabaseEntry skey = new DatabaseEntry();
      DatabaseEntry data = new DatabaseEntry();
      int i = 0;
      boolean totheend = (max == 0);
      FrequencyHash ft = new FrequencyHash();
      max += from; // 1 2 3 4 5 6 7
      while (c.getNext(skey, key, data, LockMode.READ_UNCOMMITTED) == 
             OperationStatus.SUCCESS && (totheend  || i <= max) ) {
        String sik = StringBinding.entryToString(key);
        int freq  = IntegerBinding.entryToInt(data);
        if (i++ < from)
          continue;
        if (sik.length() > 0 && freq > 0)
          if (nocase)
            ft.add(sik,freq,nocase);
          else {
            os.println(i+Constants.LINE_ITEM_SEP+sik+Constants.LINE_ITEM_SEP+freq);
            //System.out.println(i+";"+sik+";"+freq);
          }
      }
      if (nocase){
        Dictionary.printSubCorpusStats(os,ft.size(),ft.getTokenCount()); // send corpus stats
        i = 1;
        for (Iterator p = (ft.getKeysSortedByValue(false)).iterator(); p.hasNext(); ) {
          String w = (String)p.next();
          Integer f = (Integer)ft.get(w);
          os.println(i+++Constants.LINE_ITEM_SEP+w+Constants.LINE_ITEM_SEP+f);
        }
      }
      c.close();
      os.flush();
    }
    catch (DatabaseException e) {
      logf.logMsg("Error accessing secondary cursor for FreqTable" , e);
    }
  }

  public Cursor getCursor() throws DatabaseException{
    return database.openCursor(null, null);
  }

  public SecondaryCursor getSecondaryCursor() throws DatabaseException{
    return freqKeyDatabase.openSecondaryCursor(null, CursorConfig.READ_COMMITTED);
  }

  public void close () {
    // ignore operation status
    try {
      if (freqKeyDatabase != null){
        freqKeyDatabase.close();
        freqKeyDatabase = null;
      }
      super.close();
    } catch(DatabaseException e) {
      logf.logMsg("Error closing DB "+dbname,e);
      e.printStackTrace(System.err);
    }
  }

}
