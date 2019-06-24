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
import modnlp.idx.query.WordQuery;
import modnlp.idx.query.Horizon;
import modnlp.idx.query.PrepContextQuery;
import modnlp.idx.query.SubcorpusConstraints;

import modnlp.util.LogStream;
import modnlp.util.PrintUtil;
import modnlp.dstruct.WordForms;
import modnlp.dstruct.CorpusFile;
import modnlp.dstruct.IntegerSet;
import modnlp.dstruct.TokenMap;
import modnlp.dstruct.IntOffsetArray;
import modnlp.dstruct.FrequencyHash;


import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.DatabaseNotFoundException;
import com.sleepycat.je.Cursor;
import com.sleepycat.je.SecondaryCursor;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.je.LockMode;
import com.sleepycat.je.OperationStatus;
import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.bind.tuple.StringBinding;
import com.sleepycat.bind.tuple.IntegerBinding;


import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;
import java.util.Vector;
import modnlp.idx.headers.HeaderDBManager;

/**
 *  Mediate access to all databases (called Dictionary for
 *  'historical' reasons; see tec-server)
 *
 * @author  S Luz &#60;luzs@acm.org&#62;
 * @version <font size=-1>$Id: Dictionary.java,v 1.2 2006/05/22 17:26:02 amaral Exp $</font>
 * @see modnlp.idx.MakeTECIndex
 */
public class Dictionary {

  public static final String TTOKENS_LABEL = "No. of tokens";
  public static final String TTRATIO_LABEL = "Type-token ratio";
  public static final String NOITEMS_LABEL = "Number of items";

  DictProperties dictProps;
  LogStream logf;
  // main tables 
  // WordPositionTable wPosTable;          // word -> [pos1, pos2, ...]  
  // (one table per fileno; to appear as local variables)
  WordFileTable wFilTable;       // word -> [fileno1, fileno2, ...]
  CaseTable caseTable;           // canonicalform -> [form1, form2, ...]
  FreqTable freqTable;           // word -> noofoccurrences
  FileTable fileTable;           // fileno -> filenameOrUri
  TPosTable tposTable;           // fileno -> (offset) positions of each token in file
  Environment environment;

  protected boolean verbose = false;  

  /**
   * Open a new <code>Dictionary</code> in read-only mode with default
   * DictProperties ("dictionary.properties" in current directory or,
   * failing that, hardcoded defaults).
   *
   */
  public Dictionary(){
    dictProps = new DictProperties();
    init(false);
  }

  /**
   * Open a new <code>Dictionary</code> in read-only mode with 
   * DictProperties dp.
   *
   */
  public Dictionary(DictProperties dp){
    dictProps = dp;
    init(false);
  }

  /**
   * Open a new <code>Dictionary</code>.
   *
   * @param write a <code>boolean</code> value: false opens the
   * dictionary in read-only mode; true opens it for writing (enabling
   * creation of new tables). Use default DictProperties
   * ("dictionary.properties" in current directory or, failing that,
   * hardcoded defaults).
   */
  public Dictionary (boolean write){
    dictProps = new DictProperties();
    init(write);
  }

  /**
   * Open a new <code>Dictionary</code>.
   *
   * @param write a <code>boolean</code> value: false opens the
   * dictionary in read-only mode; true opens it for writing (enabling
   * creation of new tables). Use default DictProperties dp.
   */
  public Dictionary (boolean write, DictProperties dp){
    dictProps = dp;
    init(write);
  }


  public void init (boolean write){
    try {
      logf = new LogStream(System.err);
      EnvironmentConfig envConfig = new EnvironmentConfig();
      envConfig.setReadOnly(!write);
      envConfig.setAllowCreate(write);
      environment = new Environment(new File(dictProps.getEnvHome()),
                                    envConfig);
      wFilTable = new WordFileTable(environment, 
                                    dictProps.getWFilTableName(), 
                                    write);
      caseTable = new CaseTable(environment, 
                                dictProps.getCaseTableName(), 
                                write);
      freqTable = new FreqTable(environment, 
                                dictProps.getFreqTableName(), 
                                write);
      fileTable = new FileTable(environment, 
                                dictProps.getFileTableName(), 
                                write);
      tposTable = new TPosTable(environment, 
                                dictProps.getTPosTableName(), 
                                write);
      
    } catch (Exception e) {
      logf.logMsg("Error opening Dictionaries: "+e);
      e.printStackTrace(System.err);
    }
  }

  
  /**
   * Add each token in tm (extracted from fou) to the index
   *
   * N.B.: currently, addToDictionary operations aren't atomic; if the
   * program crashes the index could be left in an inconsistent
   * state. In future, implement it using JE transactions
   *
   * @param tm a <code>TokenMap</code>: multiset of tokens
   * @param fou a <code>String</code>: the file whose <code>TokenMap</code> is tm 
   * @return founo: the sequential number identiying the file added 
   * @exception AlreadyIndexedException if an error occurs
   */
  public int addToDictionary(TokenMap tm, String fou) 
    throws AlreadyIndexedException, EmptyFileException
  {
    if (tm == null || tm.size() == 0){
      logf.logMsg("Dictionary: file or URI already indexed "+fou);
      throw new EmptyFileException(fou);
    }
    // check if file already exists in corpus; if so, quit and warn user
    NumberFormat nf = NumberFormat.getInstance();
    nf.setMaximumFractionDigits(4);
    int founo = fileTable.getKey(fou);
    if (founo >= 0) { // file has already been indexed
      logf.logMsg("Dictionary: file or URI already indexed "+fou);
      throw new AlreadyIndexedException(fou);
    }
    else 
      founo = -1*founo;
    fileTable.put(founo,fou);
    WordPositionTable wPosTable = null;
    try {
      wPosTable = new WordPositionTable(environment, 
                                        ""+founo,
                                        true);
    }
    catch (DatabaseNotFoundException e) {
      logf.logMsg("Dictionary: Error updating WordPositionTable "+founo);
      logf.logMsg("Dictionary: Crashing out... ");
      System.exit(1);
    }
    // store sorted set of positions (worst-case for basic operations
    // O(ln(n)) which should be better than storing in a vector and
    // standard merge sorting, which is O(n^2 ln(n))) 
    // [SL: run tests to check that's really the case]
    TreeSet poss = new TreeSet();
    int ct = 1;
    for (Iterator e = tm.entrySet().iterator(); e.hasNext() ;)
      {
        if (verbose)
          PrintUtil.printNoMove("Indexing ...",ct++);
        Map.Entry kv = (Map.Entry) e.next();
        String word = (String)kv.getKey();
        caseTable.put(word);
        wFilTable.put(word,founo);
        //StringIntKey sik = new StringIntKey(word, founo);
        IntegerSet set = (IntegerSet) kv.getValue();
        wPosTable.put(word, set);
        freqTable.put(word,set.size());
        poss.addAll(set);
      }
    if (verbose)
      PrintUtil.donePrinting();
    wPosTable.close();
    //System.err.println(poss);
    int [] posa = new int[poss.size()];
    int i = 0;
    for (Iterator e = poss.iterator(); e.hasNext() ;)
      posa[i++] = ((Integer) e.next()).intValue();
    //System.out.println(PrintUtil.toString(posa));
    tposTable.put(founo,new IntOffsetArray(posa));
    System.out.println("Cumulative compression ratio = "+
                       nf.format(tposTable.getCompressionRatio())+
                       " (read "+nf.format(tposTable.getBytesReceived())+
                       " and wrote "+
                       nf.format(tposTable.getBytesWritten())+" bytes)");
    //tposTable.dump();
    return founo;
  }
  
  public int addToDictionary(TokenMap tm, TokenMap tmNoFreq, String fou) 
    throws AlreadyIndexedException, EmptyFileException
  {
    if (tm == null || tm.size() == 0){
      logf.logMsg("Dictionary: file or URI already indexed "+fou);
      throw new EmptyFileException(fou);
    }
    // check if file already exists in corpus; if so, quit and warn user
    NumberFormat nf = NumberFormat.getInstance();
    nf.setMaximumFractionDigits(4);
    int founo = fileTable.getKey(fou);
    if (founo >= 0) { // file has already been indexed
      logf.logMsg("Dictionary: file or URI already indexed "+fou);
      throw new AlreadyIndexedException(fou);
    }
    else 
      founo = -1*founo;
    fileTable.put(founo,fou);
    WordPositionTable wPosTable = null;
    try {
      wPosTable = new WordPositionTable(environment, 
                                        ""+founo,
                                        true);
    }
    catch (DatabaseNotFoundException e) {
      logf.logMsg("Dictionary: Error updating WordPositionTable "+founo);
      logf.logMsg("Dictionary: Crashing out... ");
      System.exit(1);
    }
    // store sorted set of positions (worst-case for basic operations
    // O(ln(n)) which should be better than storing in a vector and
    // standard merge sorting, which is O(n^2 ln(n))) 
    // [SL: run tests to check that's really the case]
    TreeSet poss = new TreeSet();
    int ct = 1;
    for (Iterator e = tm.entrySet().iterator(); e.hasNext() ;)
      {
        if (verbose)
          PrintUtil.printNoMove("Indexing ...",ct++);
        Map.Entry kv = (Map.Entry) e.next();
        String word = (String)kv.getKey();
        caseTable.put(word);
        wFilTable.put(word,founo);
        //StringIntKey sik = new StringIntKey(word, founo);
        IntegerSet set = (IntegerSet) kv.getValue();
        wPosTable.put(word, set);
        freqTable.put(word,set.size());
        poss.addAll(set);
      }
    for (Iterator e = tmNoFreq.entrySet().iterator(); e.hasNext() ;)
      {
        if (verbose)
          PrintUtil.printNoMove("Indexing ...",ct++);
        Map.Entry kv = (Map.Entry) e.next();
        String word = (String)kv.getKey();
        IntegerSet set = (IntegerSet) kv.getValue();
        
        caseTable.put(word);
        wFilTable.put(word,founo);
        //StringIntKey sik = new StringIntKey(word, founo);
        IntegerSet posSet = wPosTable.fetch(word);
        if(posSet !=null){
         set.addAll(posSet);
        }

        wPosTable.put(word, set);
        //remove to hide from frequency list arabic roots
        freqTable.put(word,set.size());
        //hiddenFreqTable.put(word,set.size());
        poss.addAll(set);
      }
    if (verbose)
      PrintUtil.donePrinting();
    wPosTable.close();
    //System.err.println(poss);
    int [] posa = new int[poss.size()];
    int i = 0;
    for (Iterator e = poss.iterator(); e.hasNext() ;)
      posa[i++] = ((Integer) e.next()).intValue();
    //System.out.println(PrintUtil.toString(posa));
    tposTable.put(founo,new IntOffsetArray(posa));
    System.out.println("Cumulative compression ratio = "+
                       nf.format(tposTable.getCompressionRatio())+
                       " (read "+nf.format(tposTable.getBytesReceived())+
                       " and wrote "+
                       nf.format(tposTable.getBytesWritten())+" bytes)");
    //tposTable.dump();
    return founo;
  }
  
  

  /**
   * <code>removeFromDictionary</code> de-indexes file or URL
   * <code>fou</code>
   *
   * N.B.: currently, removeFromDictionary operations aren't atomic;
   * if the program crashes the index could be left in an inconsistent
   * state. In future, implement it using JE transactions
   *
   * @param fou a <code>String</code> value
   * @exception NotIndexedException if an error occurs
   */
  public void removeFromDictionary(String fou) 
    throws NotIndexedException 
  {
    // check if file already exists in corpus; if so, quit and warn user
    int founo = fileTable.getKey(fou);
    if (founo < 0) { // file was not indexed
      logf.logMsg("Dictionary: file or URI not indexed "+fou);
      throw new NotIndexedException(fou);
    }
    try {
      WordPositionTable wPosTable = new WordPositionTable(environment, 
                                                          ""+founo,
                                                          true);
      TokenMap tm = wPosTable.removeFile();
      wPosTable.close();
    for (Iterator e = tm.entrySet().iterator(); e.hasNext() ;)
        {
          Map.Entry kv = (Map.Entry) e.next();
          String word = (String)kv.getKey();
          tposTable.remove(founo);
          wFilTable.remove(word,founo);
          IntegerSet set = (IntegerSet) kv.getValue();
          if (freqTable.remove(word,set.size()) == 0)
            caseTable.remove(word);
        }
    }
    catch (DatabaseNotFoundException e){
      logf.logMsg("Dictionary: Error removing WordPositionTable "+founo+e);
      logf.logMsg("Dictionary: Ignoring error and continuing...");
    }
    fileTable.remove(founo);
  }

  /**
   * Check if file or URI <code>fou</code> is in the index. 
   *
   * @param fou a <code>String</code> value
   * @return a <code>boolean</code> value
   */
  public boolean isIndexed(String fou) {
    // check if file already exists in corpus; if so, quit and warn user
    int founo = fileTable.getKey(fou);
    if (founo >= 0)  // file has already been indexed
      return true;
    else 
      return false;
  }

  public int getFileKey(String fou) {
    return fileTable.getKey(fou);
  }

  public FileTable getFileTable() {
    return fileTable;
  }

  public String [] getIndexedFileNames () {
    return fileTable.getFileNames();
  }

  public int [] getIndexedFileKeys () {
    return fileTable.getKeys();
  }

  public String getIndexedFileName (int k) {
    return fileTable.getFileName(k);
  }

  public FrequencyHash getFileFrequencyTable(int fno, boolean nocase){
    WordPositionTable wpt = null;
    try{
      wpt = new WordPositionTable(environment,""+fno,false);
    }
    catch (DatabaseException e) {
      logf.logMsg("Error accessing WordPositionTable "+fno+" :" , e);
    }
    return wpt.getFrequencyTable(nocase);
  }

  public Environment getEnvironment () {
    return environment;
  }
 
  public int getFrequency (WordForms wforms)
  {
    int tf = 0;
    if (wforms == null)
      return 0;
    for (Iterator e = wforms.iterator(); e.hasNext() ;)
      {
        String key = (String)e.next();
        tf = tf + freqTable.getFrequency(key);
      }
    return tf;
  }
  
  public int getFrequency (WordForms wforms,  SubcorpusConstraints sbc)
  {
    int tokencount = 0; 
    if (wforms == null)
      return 0;
    for (Iterator e = wforms.iterator(); e.hasNext() ;) {
      String word = (String)e.next();
      try {
        if (word.length() == 0)
          continue;
        // get all files in which this word occurs
        IntegerSet files =  wFilTable.fetch(word);
        for (Iterator f = files.iterator(); f.hasNext(); ) {
          String fno = ((Integer)f.next()).toString();
          if (sbc != null && !sbc.acceptFile(fno) ){
            // word occurs in a file that doesn't meet constraints
            continue;
          }
          //System.err.println("-------------------Looking at file: "+getIndexedFileName(Integer.parseInt(fno))); 
          WordPositionTable wpt = new WordPositionTable(environment, 
                                                        fno,
                                                        false);
          // all positions on a file
          IntegerSet pos  = wpt.fetch(word);
          wpt.close();
          if (pos == null)
            continue;
          SubcorpusTable sbct = null;
          int nooc = 0;
          if (sbc != null){
            try {
              sbct = new SubcorpusTable(environment, fno, false, false);
              // get number of tokens that satisfy subcorpus constraints
              nooc = sbc.getTokenCount(fno,pos,sbct);
              sbct.close();
            } 
            catch(DatabaseNotFoundException ex ) { sbct = null;}
          }
          else {
            // no further subcorpus constraints, count all tokens in this file
            nooc = pos.size();
          }
          if (nooc == 0)
            continue;
          tokencount += nooc;
        } // close file iterator, counting done for this word form
      } // close try
      catch (DatabaseException ex) {
        logf.logMsg("Error accessing secondary cursor for FreqTable" , ex);
      }
    } // close word iterator, counting done
    return tokencount;
  }
  
  

  public CaseTable getCaseTable (){
    return caseTable;
  }

  /**
   * <code>matchConcordance</code> match <code>cline</code> against
   * this query (represented after <code>parseQuery()</code> by
   * <code>queryArray</code> and <code>intervArray</code>)
   *
   * <p>
   * N.B.: used binary search in order to speed it up to logarithmic
   * levels (avoiding the current O(n) worst-case behaviour)
   * 
   * @param pcq the 'pre-processed' query object, containing the
   *  search horizons ({@link modnlp.idx.query.Horizon Horizon})
   *  objects, and a set of byte offsets per wordform for a specific
   *  file ({@link modnlp.idx.database.WordPositionTable
   *  WordPositionTable}).
   * @param pos the position (as byte offset) of the keyword on the file
   * @param posa the entire word position array for a specified file
   *             (obtained through {@link TPosTable#getPosArray(int)}).
   * @return <code>true</code> if cline matches, false otherwise.
   */
  public boolean matchConcordance(PrepContextQuery pcq, int pos, int[] posa){

    // lhisa will contain sets of positions of query words occurring
    // to the left of the main keyword
    IntegerSet [] lhisa = null;
    // lha is the array specifying how far (the horizon) to the left we
    // are allowed to match each keyword to the left of the main keyword
    int [] lha =  null; 
    // lpa will store the absolute position offsets on the searched
    // file to match against lhisa given the horizon constraints
    // specified in lha
    int [] lpa = null;
    Horizon lh = pcq.getLeftHorizon();
    if (lh !=null){
      lhisa =  pcq.getLeftHorizonIntegerSetArray();
      lha =  lh.getHorizonArray();
      lpa = new int[lh.getMaxSearchHorizon()];
    }
    // rhisa, rha and rpa are analogous to their left-hand side
    // counterparts explained above
    IntegerSet [] rhisa = null;
    int [] rha = null;
    int [] rpa = null;
    Horizon rh = pcq.getRightHorizon();
    if (rh !=null){
      rhisa = pcq.getRightHorizonIntegerSetArray();
      rha = rh.getHorizonArray();
      rpa = new int[rh.getMaxSearchHorizon()];
    }

    //long bt = System.currentTimeMillis();

    if (lh != null){// build left-hand side array
      int i = Arrays.binarySearch(posa,pos);
      i--;
      for (int j = 0; j < lpa.length; j++)
        lpa[j] = (i - j < 0) ? 0 : posa[i-j];
    }

    if (rh != null){// build right-hand side array
      int i = Arrays.binarySearch(posa,pos);
      int i2 = i+1; 
      int ml = posa.length-i2;
      for (int j = 0; j < rpa.length; j++)
        if (j < ml )
          rpa[j] = posa[j+i2];
        else
          rpa[j] = 0;
    }
    //long et = System.currentTimeMillis();
    //System.err.println("Time: "+et+"-"+bt+"="+(et-bt));

    // match left-hand side
    if (lh != null){
      int bi = 0;
      int ei = 0;
      for (int i = 0;  i < lhisa.length; i++) {
        boolean matched = false;
        if (lhisa[i] == null) 
          continue;
        bi = ei;
        ei = lha[i];
        for (int k = bi; k < ei; k++) {
          if (lhisa[i].contains(lpa[k])) { // lhisa[i] should contain a set with with pos for all  kw forms
            matched = true;
            break;
          }
        }
        if (!matched)  // no matches for this kw, search doesn't match
          return false; // otherwise, move on to the next kw
      }
    }
    // match right-hand side
    if (rh != null){
      int bi = 0;
      int ei = 0;
      for (int i = 0;  i < rhisa.length; i++) {
        boolean matched = false;
        if (rhisa[i] == null) 
          continue;
        bi = ei;
        ei = rha[i];
        for (int k = bi; k < ei; k++) {
          if (rhisa[i].contains(rpa[k])) { // rhisa[i] should contain a set with with pos for all  kw forms
            matched = true;
            break;
          }
        }
        if (!matched)  // no matches for this kw, search doesn't match
          return false; // otherwise, move on to the next kw
      }
    }
    return true;
  }

  /** Return a vector containing all filenames where KEY 
   *  occurs in the corpus.
   * @param  key   the keyword to search for 
   */
  public Vector getAllFileNames (String key){
    Vector filenames = new Vector();
    IntegerSet fset = wFilTable.fetch(key);
    for (Iterator f = fset.iterator(); f.hasNext() ;){
      int fno = ((Integer)f.next()).intValue();
      filenames.addElement(fileTable.getFileName(fno));
    }
    return filenames;
  }

  public void printCorpusStats (PrintWriter os) {
    printCorpusStats(os, true);
  }

  public void printCorpusStats (PrintWriter os, boolean nocase) {
    os.println(0+Constants.LINE_ITEM_SEP+TTOKENS_LABEL+Constants.LINE_ITEM_SEP+freqTable.getTotalNoOfTokens());
    os.println(0+Constants.LINE_ITEM_SEP+TTRATIO_LABEL+Constants.LINE_ITEM_SEP+getTypeTokenRatio(nocase));
  }

  public static final void printSubCorpusStats (PrintWriter os, int notypes , int notokens) {
    os.println(0+Constants.LINE_ITEM_SEP+TTOKENS_LABEL+Constants.LINE_ITEM_SEP+notokens);
    os.println(0+Constants.LINE_ITEM_SEP+TTRATIO_LABEL+Constants.LINE_ITEM_SEP+getTypeTokenRatio(notypes,notokens));
  }
  
  public static final void printSubCorpusStats (PrintWriter os, double avgTTratio, int notokens) {
    os.println(0+Constants.LINE_ITEM_SEP+TTOKENS_LABEL+Constants.LINE_ITEM_SEP+notokens);
    os.println(0+Constants.LINE_ITEM_SEP+TTRATIO_LABEL+Constants.LINE_ITEM_SEP+avgTTratio );
  }

  public void printNoItems (PrintWriter os, int noitems) {
    os.println(0+Constants.LINE_ITEM_SEP+NOITEMS_LABEL+Constants.LINE_ITEM_SEP+noitems);
  }

  // TODO: need to implement version of
  // getTotalNoOfTokens(SubcorpusConstraints) to count the number of
  // tokens in a sub-corpus
  public int getTotalNoOfTokens(){
    return freqTable.getTotalNoOfTokens();  
  }


  public double getTypeTokenRatio(boolean nocase){
    return nocase?
      getTypeTokenRatio(caseTable.getTotalNoOfTypes(),
                        freqTable.getTotalNoOfTokens()) :
      freqTable.getTypeTokenRatio();
  }

  // default: case insensitive
  public double getTypeTokenRatio(){
    return 
      getTypeTokenRatio(caseTable.getTotalNoOfTypes(),
                        freqTable.getTotalNoOfTokens());
  }

  public static final double getTypeTokenRatio(int notypes, int notokens){
    return 
      (double)notypes/notokens;
  }

  /**
   * Print the entire frequency list onto os
   *
   * @param os a <code>PrintWriter</code> value
   */
  public void printSortedFreqList (PrintWriter os) {
    printSortedFreqList(os, 0, 0, true);
  }

  /**
   * Print the max topmost frequent types onto os.
   *
   * @param os a <code>PrintWriter</code> value
   * @param max an <code>int</code> value
   */
  public void printSortedFreqList (PrintWriter os, int max) {
    printCorpusStats(os);
    freqTable.printSortedFreqList(os, 0, max, true);
  }

 /**
   * Print the max topmost frequent types onto os.
   *
   * @param os a <code>PrintWriter</code> value
   * @param max an <code>int</code> value
   */
  public void printSortedFreqList (PrintWriter os, int from, int max, 
                                   boolean nocase) {
    if (!nocase){
      printCorpusStats(os,nocase);
    }
    freqTable.printSortedFreqList(os, from, max, nocase);
  }


  /**
   * Print the max topmost frequent types occurring in the subcorpus
   * denoted by sbc onto os.
   *
   * @param os a <code>PrintWriter</code> value
   * @param max an <code>int</code> value
   * @param sbc a <code>SubcorpusConstraints</code> value
   */
  public void printSortedFreqList (PrintWriter os, int from, int max, 
                                   SubcorpusConstraints sbc, boolean nocase) 
  {
    int tokencount = 0;  // 2^32 should be enough (we're not quite competing with google yet) 
    int typecount = 0;
    FrequencyHash ft = new FrequencyHash();
    try {
      DatabaseEntry key = new DatabaseEntry();
      DatabaseEntry skey = new DatabaseEntry();
      DatabaseEntry data = new DatabaseEntry();
      System.err.println("Requested secondary cursor");
      SecondaryCursor c = freqTable.getSecondaryCursor();
      System.err.println("Got secondary cursor");
      int i = 0;
      boolean totheend = (max == 0);
      max += from+1;
      while (c.getNext(skey, key, data, LockMode.READ_UNCOMMITTED) == 
             OperationStatus.SUCCESS && ( totheend  || i <= max) ) {
        if (i++ < from)
          continue;
        String word = StringBinding.entryToString(key);
        if (word.length() == 0)
          continue;
        IntegerSet files =  wFilTable.fetch(word);
        //System.err.println("starting counting for:"+word); 
        for (Iterator f = files.iterator(); f.hasNext(); ) {
          String fno = ((Integer)f.next()).toString();
          if (sbc != null && !sbc.acceptFile(fno) ){
            //System.err.println("-------------------SKIP:"+word); 
            continue;
          }
          //System.err.println("-------------------Looking at file: "+getIndexedFileName(Integer.parseInt(fno))); 
          WordPositionTable wpt = new WordPositionTable(environment, 
                                                        fno,
                                                        false);
          // all positions on a file
          IntegerSet pos  = wpt.fetch(word);
          wpt.close();
          if (pos == null)
            continue;
          SubcorpusTable sbct = null;
          int nooc = 0;
          if (sbc != null){
            try {
              sbct = new SubcorpusTable(environment, fno, false, false);
              //System.err.println("-------------------Counting SBC freq of:"+word+" in "+fno); 
              nooc = sbc.getTokenCount(fno,pos,sbct);
              sbct.close();
            } catch(DatabaseNotFoundException e ) { sbct = null;}
          }
          else {
            nooc = pos.size();
          }
          //System.err.println("word ->"+word+" = "+nooc);
          if (nooc == 0)
            continue;
          tokencount += nooc;
          ft.add(word,nooc,nocase);
        }
        //System.err.println("Counting done"); 
      }
      c.close();
    }
    catch (DatabaseException e) {
      logf.logMsg("Error accessing secondary cursor for FreqTable" , e);
    }
    int i = 1;
    printSubCorpusStats(os,ft.size(),ft.getTokenCount()); // send corpus stats
    for (Iterator p = (ft.getKeysSortedByValue(false)).iterator(); p.hasNext(); ) {
      String w = (String)p.next();
      Integer f = (Integer)ft.get(w);
      os.println(i+++Constants.LINE_ITEM_SEP+w+Constants.LINE_ITEM_SEP+f);
    }
    os.close();
    os.flush();
  }
  
  public String getCorpusDir() {
    return dictProps.getCorpusDir();
  }

  public void printConcordances(WordQuery query, int ctx, boolean ignx, PrintWriter os){
    printConcordances(query, ctx, ignx, os, null);
  }

  public void printConcordances(WordQuery query, int ctx, boolean ignx, 
                                PrintWriter os, SubcorpusConstraints sbc) 
  {
    //System.err.println("dictio--->"+sbc);
    WordForms wforms = query.getKeyWordForms();
    if (wforms == null) {
      os.println(0);
      os.flush();
      return;
    }
    Horizon lh = null;
    Horizon rh = null;
    boolean jkw = query.isJustKeyword();
    if (!jkw) {
      lh = query.getLeftHorizon();
      rh = query.getRightHorizon();
    }
    String key =  query.getKeyword();
    int frequency = getFrequency(wforms);
    //String cdir = dictProps.getCorpusDir();
    // tell client the max no. of lines we may be sending
    os.println(frequency);
    os.flush();
    for (Iterator w = wforms.iterator(); w.hasNext(); ) {
      String word = (String)w.next();
      IntegerSet files =  wFilTable.fetch(word);
      int posl = 0;
      //int i = 1;
      try {
        for (Iterator f = files.iterator(); f.hasNext(); ) {
          Integer fno = (Integer)f.next();
          WordPositionTable wpt = new WordPositionTable(environment, 
                                                        ""+fno,
                                                        false);
          // all positions on a file
          int [] posa = tposTable.getPosArray(fno.intValue());
          IntegerSet pos  = wpt.fetch(word);
          if (pos == null)
            continue;
          PrepContextQuery pcq = new PrepContextQuery(lh, rh, wpt);
          String fn = fileTable.getFileName(fno.intValue());

          SubcorpusTable sbct = null;
          if (sbc != null){
            try {
              sbct = new SubcorpusTable(environment, fno.toString(), false);
            } catch(DatabaseNotFoundException e ) { sbct = null;}
          }

          CorpusFile fh = null;
          for (Iterator p = pos.iterator(); p.hasNext(); ) {
            Integer bp = (Integer)p.next();
            int bpi = bp.intValue();
            if ( (sbc == null || sbc.accept(fno.toString(),bpi,sbct)) 
                 && (jkw || matchConcordance(pcq,bpi,posa))) 
              {
                if (fh == null) {
                  fh = new CorpusFile(dictProps.getFullCorpusFileName(fn),
                                      dictProps.getProperty("file.encoding"));
                  fh.setLanguage(dictProps.getLanguage());
                  fh.setIgnoreSGML(ignx);
                } 
                String ot = fh.getWordInContext(bp, key, ctx);
                //              if (  query.matchConcordance(ot,ctx) )
                //{
                //System.err.println(fn+"|"+bp+"|"+ot);
                
                //Get the section id to which the keword belongs
                try {
                  sbct = new SubcorpusTable(environment, fno.toString(), false);
                } catch(DatabaseNotFoundException e ) { sbct = null;}     
                String sn = sbct.getSectionID(bpi);
                
                os.println(fn+"|"+bp+"|"+sn+"|"+ot);
                os.flush();
              }
            if (os.checkError()) {
              logf.logMsg("Dictionary.printConcordances: connection closed prematurely by client");
              if (fh != null)
                fh.close();
              return;
            }
          }
          if (fh != null)
            fh.close();
          wpt.close();
        }
      }
      catch (DatabaseNotFoundException e) {
        logf.logMsg("Error reading corpus file ", e);
      }
      catch (IOException e) {
        logf.logMsg("Error reading corpus file ", e);
      }
    }    
     os.close();
  }
  
public void printHeaders(PrintWriter os, HeaderDBManager hdbm){
    int [] fks = getIndexedFileKeys();
    os.println(hdbm.getFileHeaderAttributeHuman());
    for (int i = 0; i < fks.length; i++) {
            String fdesc = hdbm.getFileHeaderAttributes(fks[i]);  
            String line = fdesc ;
            // System.err.println("--");
            // System.out.println(line);
            os.println(line);     
      } 
    os.close();
    
}

  public String getExtract(String fn, int ctx, long offset, boolean ignx)
  {
    String pre = null;
    String kw = null;
    String pos = null;
    try {
      CorpusFile fh = new CorpusFile(dictProps.getFullCorpusFileName(fn),
                                     dictProps.getProperty("file.encoding"));
      fh.setIgnoreSGML(ignx);
      pre = fh.getPreContext(offset,ctx);
      pos = fh.getPosContext(offset,ctx);
      int se = pos.length();
      for (int i = 0; i < se; i++)
        if (Character.isWhitespace(pos.charAt(i))) {
          se = i;
          break;
        }
      kw = "<font color='red'><b>"+pos.substring(0,se)+"</b></font>";
      pos = pos.substring(se);
    }
    catch (IOException e) {
      logf.logMsg("Error reading corpus file ", e);
    }
    return pre+kw+pos;
  }    

  public DictProperties getDictProps() {
    return dictProps;
  }

  /**
   * 
   *
   * @param word a <code>String</code> the query word (type)
   * @return an <code>IntegerSet</code> the set of keys to files in
   * which word occurs
   */
  public IntegerSet getOccurringFiles(String word) {
    return  wFilTable.fetch(word);
  }
 
  public void  dump () {
    System.out.println("===========\n FileTable:\n===========");
    fileTable.dump();
    System.out.println("===========\n Word-File Table:\n===========");
    wFilTable.dump();
    System.out.println("===========\n CaseTable:\n===========");
    caseTable.dump();
    System.out.println("===========\n FileTable:\n===========");
    fileTable.dump();
    System.out.println("===========\n TPosTable:\n===========");
    tposTable.dump();
    int fnos [] = fileTable.getKeys();
    for (int i = 0 ; i < fnos.length; i++){
      System.out.println("===========\n WordPositionTable for "+
                         fileTable.getFileName(fnos[i])+":\n=============");
      try {
        WordPositionTable wPosTable = new WordPositionTable(environment, 
                                                            ""+fnos[i],
                                                            false);
        wPosTable.dump();
        wPosTable.close();
      }
      catch(DatabaseNotFoundException e){
        logf.logMsg("Dictionary: Error reading WordPositionTable: "+fnos[i]+e);
      }
    }
    System.out.println("===========\n FreqTable:\n===========");
    freqTable.dump();
  }

  public void sync () {
    try {
      environment.sync();
    } catch(Exception e) {
      logf.logMsg("Error synchronising environment: "+e);
    }
  }

  public void compress () {
    try {
      System.err.println("compressing db....");
      environment.compress();
    } catch(Exception e) {
      logf.logMsg("Error compressing environment: "+e);
    }
  }

  public void cleanLog () {
    try {
      System.err.println("cleanning Log....");
      environment.cleanLog();
    } catch(Exception e) {
      logf.logMsg("Error cleanning environment log: "+e);
    }
  }

  public void close () {
    try {
      tposTable.close();
      freqTable.close();
      wFilTable.close();
      //wPosTable.close();
      caseTable.close();
      fileTable.close();
      environment.close();
    } catch(Exception e) {
      logf.logMsg("Error closing environment: "+e);
    }
  }

  public void finalize () {
    cleanLog();
    close();
  }

  public LogStream getLogStream(){
    return logf;
  }

  public boolean getVerbose() {
    return verbose;
  }

  public void setVerbose(boolean v) {
    verbose = v;
  }

}
