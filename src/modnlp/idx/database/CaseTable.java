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
import modnlp.dstruct.WordForms;
import modnlp.idx.query.WordQuery;

import com.sleepycat.je.Environment;
import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.bind.tuple.StringBinding;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.DatabaseNotFoundException;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.DeadlockException;
import com.sleepycat.je.Cursor;
import com.sleepycat.je.OperationStatus;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.LockMode;
import com.sleepycat.je.BtreeStats;
import com.sleepycat.je.StatsConfig;

import java.util.Iterator;
import java.util.Collections;
import java.util.regex.Pattern;

/**
 *  Store a canonical (lowercase) form of a word and all other forms
 *   in which it occur, as follows:
 *
 *  <pre>  
 *       KEY        |  DATA
 *   ---------------|-------------------
 *    canonicalform | [form1, form2, ...]
 * e.g.:
 *      rose        | [Rose, rose, ROSE]
 *  </pre>
 *
 * @author  S Luz &#60;luzs@acm.org&#62;
 * @version <font size=-1>$Id: CaseTable.java,v 1.2 2006/05/22 17:26:02 amaral Exp $</font>
 * @see  
*/
public class CaseTable extends Table {

  public CaseTable (Environment env, String fn, boolean write) 
    throws DatabaseNotFoundException {
    super(env,fn,write);
  }

  public StringSet fetch (String sik) {
    TupleBinding isb = new StringSetBinding();
    DatabaseEntry key = new DatabaseEntry();
    DatabaseEntry data = new DatabaseEntry();
    StringSet set = null;
    StringBinding.stringToEntry(normaliseString(sik), key);
    try {
      if (database.get(null,key, data, LockMode.DEFAULT) == OperationStatus.SUCCESS) 
        set = (StringSet)isb.entryToObject(data);
    } catch(DeadlockException e) {
      logf.logMsg("Deadlock reading from dbname:"+e);
    }
    catch(DatabaseException e) {
      logf.logMsg("Error reading from DB "+dbname+": "+e);
    }
    return set;
  }

  public StringSet put(String sik, StringSet set) {
    TupleBinding isb = new StringSetBinding();
    DatabaseEntry key = new DatabaseEntry();
    DatabaseEntry data = new DatabaseEntry();
    StringBinding.stringToEntry(normaliseString(sik), key);
    isb.objectToEntry(set, data);
    put(key,data);
    return set;
  }

  public StringSet put(String sik, String wform) {
    TupleBinding isb = new StringSetBinding();
    DatabaseEntry key = new DatabaseEntry();
    DatabaseEntry data = new DatabaseEntry();
    StringSet set = null;
    StringBinding.stringToEntry(normaliseString(sik), key);
    try {
      if (database.get(null, key, data, LockMode.DEFAULT) ==
          OperationStatus.SUCCESS) {
        set = (StringSet)isb.entryToObject(data);
      }
      else 
        set = new StringSet();
      set.add(wform);
      isb.objectToEntry(set, data);
      put(key,data);
    }
    catch (DatabaseException e) {
      logf.logMsg("Error reading CaseTable" + e);
    }
    return set;
  }

  public StringSet put(String wform) {
    return put(normaliseString(wform), wform);
  }

  public StringSet remove(String wform) {
    TupleBinding isb = new StringSetBinding();
    DatabaseEntry key = new DatabaseEntry();
    DatabaseEntry data = new DatabaseEntry();
    StringSet set = null;
    StringBinding.stringToEntry(normaliseString(wform), key);
    try {
      if (database.get(null, key, data, LockMode.DEFAULT) ==
          OperationStatus.SUCCESS) {
        set = (StringSet)isb.entryToObject(data);
        set.remove(wform);
        if (set.size() == 0)
          remove(key);
        else {
          isb.objectToEntry(set, data);
          put(key,data);
        }
      }
    }
    catch (DatabaseException e) {
      logf.logMsg("Error reading CaseTable" + e);
    }
    return set;
  }

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
  
  public WordForms getAllPrefixMatches (String k, boolean csensitive ){
    WordForms wordform = new WordForms(k);
    String prefix = WordQuery.getWildcardsLHS(k);
    try {
      Cursor c = database.openCursor(null, null);
      TupleBinding kb = new StringBinding();
      TupleBinding isb = new StringSetBinding();
      DatabaseEntry key = new DatabaseEntry();
      DatabaseEntry data = new DatabaseEntry();
      while (c.getNext(key, data, LockMode.DEFAULT) == 
             OperationStatus.SUCCESS) {
        String sik = (String) kb.entryToObject(key);
        StringSet set  = (StringSet) isb.entryToObject(data);
        for (Iterator f = set.iterator(); f.hasNext() ;) {          
          String word = (String)f.next();
          if ( ! csensitive ) {
            if ( normaliseString(word).startsWith(normaliseString(prefix)) ) 
              wordform.addElement(word);
          }
          else
            if ( word.startsWith(prefix) ) 
              wordform.addElement(word);
        }
      }
      c.close();
    }
    catch (DatabaseException e) {
      logf.logMsg("Error accessing CaseTable" + e);
    }
    Collections.sort(wordform);
    //System.out.println(wordform);    
    return wordform;
  }
  
  public WordForms getAllSuffixMatches (String k, boolean csensitive ){
    WordForms wordform = new WordForms(k);
    String suffix = WordQuery.getWildcardsRHS(k);
    try {
      Cursor c = database.openCursor(null, null);
      TupleBinding kb = new StringBinding();
      TupleBinding isb = new StringSetBinding();
      DatabaseEntry key = new DatabaseEntry();
      DatabaseEntry data = new DatabaseEntry();
      while (c.getNext(key, data, LockMode.DEFAULT) == 
             OperationStatus.SUCCESS) {
        String sik = (String) kb.entryToObject(key);
        StringSet set  = (StringSet) isb.entryToObject(data);
        for (Iterator f = set.iterator(); f.hasNext() ;) {          
          String word = (String)f.next();
          if ( ! csensitive ) {
            if ( normaliseString(word).endsWith(normaliseString(suffix)) ) 
              wordform.addElement(word);
          }
          else
            if ( word.endsWith(suffix) ) 
              wordform.addElement(word);
        }
      }
      c.close();
    }
    catch (DatabaseException e) {
      logf.logMsg("Error accessing CaseTable" + e);
    }
    Collections.sort(wordform);
    //System.out.println(wordform);    
    return wordform;
  }
  
  public WordForms getAllRegexMatches (String k, boolean csensitive ){
    WordForms wordform = new WordForms(k);
    try {
      Cursor c = database.openCursor(null, null);
      TupleBinding kb = new StringBinding();
      TupleBinding isb = new StringSetBinding();
      DatabaseEntry key = new DatabaseEntry();
      DatabaseEntry data = new DatabaseEntry();
      while (c.getNext(key, data, LockMode.DEFAULT) == 
             OperationStatus.SUCCESS) {
        String sik = (String) kb.entryToObject(key);
        StringSet set  = (StringSet) isb.entryToObject(data);
        for (Iterator f = set.iterator(); f.hasNext() ;) {       
          String word = (String)f.next();
          Pattern p = null;
          if ( ! csensitive ) {
            final int ciflag = Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE;
            p = Pattern.compile(k, ciflag);  //
          }
          else {
            p = Pattern.compile(k);  //
          } 
          if ( p.matcher(word).matches() ) 
            wordform.addElement(word);
        }
      }
      c.close();
    }
    catch (DatabaseException e) {
      logf.logMsg("Error accessing CaseTable" + e);
    }
    Collections.sort(wordform);
    //System.out.println(wordform);    
    return wordform;
  }
  
  
  /** Return a vector containing all word forms (all existing
   *  cases) found in the dictionary.
   * @param  key   the keyword to search for 
   * @return all word forms or null if none found.
   */
  public WordForms getAllCases (String sik){
    WordForms wordform = new WordForms(sik);
    StringSet ws = fetch(normaliseString(sik));
    return ws == null? null : new WordForms(sik, ws);
  } 
  
  

  // --- Utilities

  /** Return 'normalised' form of a sting; lowercase and with diacritics etc removed. 
   * @param  sik   a string 
   * @return a normalised form of sik
   */  
  public static String normaliseString (String sik){
    sik = java.text.Normalizer.normalize(sik, java.text.Normalizer.Form.NFD);
    sik = sik.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
    System.out.println("----"+sik);
    
    return sik.toLowerCase();
  }
  
  public void  dump () {
    try {
      Cursor c = database.openCursor(null, null);
      TupleBinding kb = new StringBinding();
      TupleBinding isb = new StringSetBinding();
      DatabaseEntry key = new DatabaseEntry();
      DatabaseEntry data = new DatabaseEntry();
      while (c.getNext(key, data, LockMode.DEFAULT) == 
             OperationStatus.SUCCESS) {
        String sik = (String) kb.entryToObject(key);
        StringSet set  = (StringSet) isb.entryToObject(data);
        System.out.println(sik+" < "+set+">");
      }
      c.close();
    }
    catch (DatabaseException e) {
      logf.logMsg("Error accessing CaseTable" + e);
    }
  }

  


}
