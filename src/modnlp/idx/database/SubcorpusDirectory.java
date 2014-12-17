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

import modnlp.dstruct.SubcorpusDelimPair;
import modnlp.dstruct.SubcorpusMap;
import modnlp.util.LogStream;
import modnlp.util.PrintUtil;

import com.sleepycat.je.Environment;
import com.sleepycat.je.DatabaseNotFoundException;
import com.sleepycat.je.DatabaseException;

import java.util.Iterator;
import java.util.Map;

/**
 *  Manage sub-corpus indices
 *
 * @author  S Luz &#60;luzs@acm.org&#62;
 * @version <font size=-1>$Id: $</font>
 * @see  
*/

public class SubcorpusDirectory {

  Dictionary dictionary;
  LogStream logf;
  boolean allowReindexing = false;
  
  public SubcorpusDirectory(Dictionary d){
    dictionary = d;
    logf = d.getLogStream();
  }

  public void setAllowReindexing(boolean b){
    allowReindexing = b;
  }

  public boolean isReindexingAllowed(){
    return allowReindexing;
  }

  public void add(SubcorpusMap tm, String fou)
    throws NotIndexedException, AlreadyIndexedException, EmptyFileException, DatabaseNotFoundException
  {
    if (tm == null || tm.size() == 0){
      logf.logMsg("SubcorpusDirectory: file or URI has no sections: "+fou);
      throw new EmptyFileException(fou);
    }
    // check if file doesn't exist in corpus; if so, quit and warn user
    int founo = dictionary.getFileKey(fou);
    if (founo < 0) { // file not indexed in Dictionary
      logf.logMsg("SubcorpusDirectory: file or URI not indexed in Dictionary: "+fou);
      throw new NotIndexedException(fou);
    }

    if (!allowReindexing && SubcorpusTable.exists(dictionary.getEnvironment(), ""+founo)) {
      logf.logMsg("SubcorpusDirectory: file or URI already indexed in SubcorpusDirectory: "
                  +fou);
      throw new AlreadyIndexedException(fou);
    }

    SubcorpusTable sct = new SubcorpusTable(dictionary.getEnvironment(), 
                                            ""+founo,
                                            true);
    int ct = 1;
    for (Iterator e = tm.entrySet().iterator(); e.hasNext() ;)
      {
        if (dictionary.getVerbose())
          PrintUtil.printNoMove("Indexing subcorpora...",ct++);
        Map.Entry kv = (Map.Entry) e.next();
        String word = (String)kv.getKey();
        SubcorpusDelimPair set = (SubcorpusDelimPair) kv.getValue();
        sct.put(word, set);
      }
    if (dictionary.getVerbose())
      PrintUtil.donePrinting();
    sct.close();
  }

  public void remove(String fou)
  {
    try {
      int founo = dictionary.getFileKey(fou);
      System.err.println("=====Removing secondary subcorpus DB "+founo);
      dictionary.getEnvironment().removeDatabase(null,SubcorpusTable.makeSecondaryDBName(founo));
      System.err.println("=====Removing primary subcorpus DB"+founo);
      dictionary.getEnvironment().removeDatabase(null,SubcorpusTable.makeDBName(founo));
    }
    catch (DatabaseException e)
      {
        System.out.println("Warning: trouble removing "+fou+": "+e);
      }
  }

  public boolean isIndexed(String fou) {
    int founo = dictionary.getFileKey(fou);
    if (SubcorpusTable.exists(dictionary.getEnvironment(), ""+founo)) {
      return true;
    }
    return false;
  }
 
  public void dump() {
    FileTable fileTable = dictionary.getFileTable();
    int fnos [] = fileTable.getKeys();
    for (int i = 0 ; i < fnos.length; i++){
      System.out.println("===========\n SubcorpusTable for "+
                         fileTable.getFileName(fnos[i])+":\n=============");
      try {
        SubcorpusTable wPosTable = new SubcorpusTable(dictionary.getEnvironment(), 
                                                      ""+fnos[i],
                                                      false);
        wPosTable.dump();
        wPosTable.close();
      }
      catch (DatabaseNotFoundException e){
        System.out.println("===========\n No subcorpustable found =========== "+e);
      }
      catch (Exception e){
        System.out.println("===========\n No subcorpustable found =========== "+e);
      }
    }
  }

}
