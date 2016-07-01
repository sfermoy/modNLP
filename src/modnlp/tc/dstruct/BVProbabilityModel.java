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
package modnlp.tc.dstruct;

import modnlp.tc.tsr.*;
import modnlp.tc.parser.*;
import modnlp.tc.util.*;

import modnlp.dstruct.*;
import modnlp.util.PrintUtil;
import modnlp.util.Tokeniser;

import java.io.Serializable;
import java.util.Set;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.Iterator;
/**
 *  Store inverted indices of terms and categories (indexed to
 *  documents) which form the basis of a probability model. (See
 *  tTable and cTable vars below), and implement methods to estimate
 *  probabilities based on these indices. 
 *
 * NB: Implementing these two interfaces separately would have been a
 * better design choice, as the implementation of
 * <code>TCInvertedIndex</code> given here can also serve as basis for
 * other kinds of <code>TCProbabilityModel</code>s. (!!ADD THIS TO
 * TODO LIST!!)
 *
 * @author  Saturnino Luz &#60;luzs@acm.org&#62;
 * @version <font size=-1>$Id: BVProbabilityModel.java,v 1.1.1.1 2005/05/26 13:59:30 amaral Exp $</font>
 * @see TCProbabilityModel
 * @see TCInvertedIndex
*/
public class BVProbabilityModel extends TCProbabilityModel 
implements TCInvertedIndex 
{


  private boolean ignoreCase = true;

  /** term table:  ( term1 , [(doc_id1, No_of_occurrences_of_term1_in_id1),
   *                            ..., 
   *                           (doc_idn, No_of_occurrences_of_term1_in_idn)],
   *                   ...
   *                  termk , [(doc_idm, No_of_occurrences_of_termk_in_idm), 
   *                            ..., 
   *                           (doc_idz, No_of_occurrences_of_termk_in_idz)])
   *
   * where term is a String and the elements of its Map value are
   * No. of occurences, indexed by doc_id's.
   */
  private Map tTable = new HashMap();

  /** category table:  ( cat1 , [id1, ..., idn]
   *                            ...
   *                            catk , [id1, ..., idn] )
   * where cat (the key) is a String and the elements of its HashSet
   * values are Strings (the same type as IDs, above)
   */
  private Map cTable = new HashMap();
  private Set docSet = new HashSet();
  /**
   * <code>corpusSize</code> represents the number of documents
   * indexed by this TCInvertedIndex. It should be equal to
   * <code>docSet</code> at all times.
   */
  int corpusSize = 0;

  /**
   * Creates a new <code>BVProbabilityModel</code> instance and
   * <code>TCInvertedIndex</code>. (See above note re. separating
   * these two classes)
   *
   */
  public BVProbabilityModel(){
    invertedIndex = this;
  }

  /**
   * Creates a new <code>BVProbabilityModel</code> instance and
   * <code>TCInvertedIndex</code> (see above note re. separating these
   * two classes) and initialise them with pt, excluding the terms in
   * swlist.
   *
   * @param pt a <code>ParsedCorpus</code> value
   * @param swlist a <code>StopWordList</code> value
   */
  public BVProbabilityModel(ParsedCorpus pt, StopWordList swlist){
    addParsedCorpus(pt, swlist);
    invertedIndex = this;
  }

  /**
   * Index each term (type) of each <code>ParsedDocument</code> in
   * <code>ParsedCorpus</code>, except those in
   * <code>stopwdlist</code>, on this PM.
   *
   * @param pt a <code>ParsedCorpus</code> value
   * @param swlist a <code>StopWordList</code> value
   */
  public void addParsedCorpus (ParsedCorpus pt, StopWordList swlist)
  {
    for (Iterator i = pt.iterator(); i.hasNext(); ) {
      PrintUtil.printNoMove("Generating prob models ...",corpusSize);  
      addParsedDocument((ParsedDocument)i.next(), swlist);
    }
    PrintUtil.donePrinting();
  }

  public void addParsedDocument (ParsedDocument pni, StopWordList swlist){
    corpusSize++;
    String id = pni.getId();
    docSet.add(id);
    //System.err.println("Generating set of words for text ID "+id);
    WordFrequencyPair[] wfp = 
      (new BagOfWords(pni.getText(), swlist)).getWordFrequencyArray();
    //System.err.println("Updating terms index");
    for (int i = 0; i < wfp.length ; i++)
      putIntoTTable(wfp[i].getWord(), id, wfp[i].getIntegerCount());
    //System.err.println("Updating categories index");
    for (Iterator k = pni.getCategVector().iterator() ; k.hasNext() ;)
      putIntoCTable((String)k.next(), id);
  } 

  
  /**
   * Get all categories in this corpus.
   *
   * @return a <code>Set</code> containing all categories that occur
   * in the corpus
   */
  public Set getCategorySet(){
    return cTable.keySet();
  }


   /**
   * Return the set of documents used in the generation of this index
   *
   * @return a <code>Set</code> containing the IDs of all documents
   * indexed in this index
   */
  public Set getDocSet(){
    return docSet;
  }

  /**
   * Get the size of <code>docSet</code>
   *
   * @return a
   */
  public int getCorpusSize(){
    return corpusSize;
  }

  /** 
   * Add file (id) to the set of files categorised as cat
   * Return true if cat is new to cTable, false otherwise.
   */
  private Object putIntoTTable(String term, String id, Integer count){
    Map idmap = (Map)tTable.get(term);
    if (idmap == null)
      idmap = new HashMap();
    idmap.put(id, count);
    return tTable.put(term,idmap);
  }

  /** 
   * Add file (id) to the set of files categorised as cat
   * Return true if cat is new to cTable, false otherwise.
   */
  private Object putIntoCTable(String cat, String id){
    Set idset = (Set)cTable.get(cat);
    if (idset == null)
      idset = new HashSet();
    idset.add(id);
    return cTable.put(cat,idset);
  }

  public boolean containsTerm(String term){
    return tTable.containsKey(term);
  }

  /**
   * Calculate and return the generality of <code>cat</code> for this
   * model. Generality is given by 
   * <pre>
     G_cat = no_of_docs_classified_as_cat / no_of_docs_in_corpus 
     </pre>
     i.e. (<code>G_cat</code> = p(cat))
 
   * @param cat a <code>String</code> representing a category
   * @return a <code>double</code> value
   */
  public double getCatGenerality(String cat){
    boolean barcat = false;
    if ( Tokeniser.isBar(cat) ){
      cat = Tokeniser.disbar(cat);
      barcat = true;
    }    
    Set cs =  (Set)cTable.get(cat);
    int css = cs == null ? 0 : cs.size();
    double c = (double) css/corpusSize;  // p(c)
    return barcat? 1-c : c;
  }

  /**
   * Get a summary of probabilities associated with
   * <code>term</code> and <code>cat</code> 
   *
   * @param term
   * @param cat a <code>String</code> representing a category
   * @return a summary of <code>Probabilities</code> 
   */
 public Probabilities getProbabilities(String term, String cat){
    Set ts =  tTable.containsKey(term) ? ((Map)tTable.get(term)).keySet() : null;
    Set cs =  (Set)cTable.get(cat);
    int tss = ts == null ? 0 : ts.size();
    int css = cs == null ? 0 : cs.size();
    int iss = 0; // intersection of ts and cs (couldn't be done with ts.retainAll(cs) 
                 // since retainAll is destructive)
    if ( tss > 0 && css > 0 )
      for (Iterator i = ts.iterator(); i.hasNext(); ) 
        if ( cs.contains(i.next()) )
          iss++;

    double vsize = (double) getTermSetSize(); // size of vocab., used for smoothing
    double c = (double) css/corpusSize;  // p(c)

    // these will get different values depending on smoothing
    double t = 0;
    double tgc = 0;
    
    switch (getSmoothingType()) {
    case NOSMOOTHING: // no smoothing variant
      t = (double) tss/corpusSize;  // p(t)
      tgc = (double) iss/css; //  p(t|c)
      break;
    case LAPLACE:// Laplace [add-one] smoothing
      //t = (double) tss/corpusSize; 
      // need to correct for smoothing 5-4-3
      // [below], otherwise inconsistency might arise. Example: for
      // p(t) = 1 we'd get P(-t,-c) = 1 - P(t) - P(-t,c) < 0.
      t = (double) (tss+1)/(corpusSize+2);  // p(t)
      tgc = (double) (iss+1)/(css+2); // p(t|c) 
      break;
    default: // no smoothing
      t = (double) tss/corpusSize;  // p(t)
      tgc = (double) iss/css; //  p(t|c)
    }

    double tc = (double) tgc * c;         // p(t,c)
    double tnc = (double) t - tc;         // p(t,~c)
    double ntc = (double) (1 - tgc) * c;  // p(~t,c)
    double ntnc = (double) (1 - t) - ntc; // p(~t,~c)
    
    Probabilities p = new Probabilities(t,c,tc,tnc,ntc,ntnc);
    //System.out.println("-- ("+term+","+cat+") = "+p);
    return p;
  }

  /**
   * Get the number of terms (types) indexed by this PM
   *
   * @return an <code>int</code> value
   */
  public int getTermSetSize(){
    return tTable.size();
  }

  /**
   * Delete all entries for terms not in the reduced term set 
   */
  public void trimTermSet(Set rts){
    tTable.keySet().retainAll(rts);
  }

  public Set getTermSet(){
    return tTable.keySet();
  }

  /**
   * Delete all entries for terms not in the reduced term set 
   */
  public void trimTermSet(WordFrequencyPair[] rts){
    tTable.keySet().retainAll(BagOfWords.extractTermCollection(rts));
  }


  public int getCategSetSize(){
    return cTable.size();
  }

  // return the vector of categories to which document id belongs 
  // (shouldn't this return a set instead?)
  public Vector getCategVector(String id){
    Vector cv = new Vector();
    for (Iterator e = cTable.entrySet().iterator(); e.hasNext() ;)
			{
        Map.Entry kv = (Map.Entry) e.next();
        if ( ((HashSet)kv.getValue()).contains(id) )
          cv.add(kv.getKey());
      }
    return cv;
  }

  /**
   *  make a new <code>WordScorePair</code>, big enough to store all
   *  terms indexed by this PM, with scores initialised to zero
   *.
   * @return a <code>WordScorePair[]</code> value
   */
  public WordScorePair[] getBlankWordScoreArray(){
    WordScorePair [] wsp = new WordScorePair[tTable.size()];
    int i = 0;
    for (Iterator e = tTable.entrySet().iterator(); e.hasNext() ;)
			{
        Map.Entry kv = (Map.Entry) e.next();
        wsp[i++] = new WordScorePair((String) kv.getKey(), 
                                     0);
      }
    return wsp;
  }

  /**
   *  gets an initialised <code>WordScorePair</code> and populate it
   *  with global term frequency
   *
   * @param wsp a <code>WordScorePair[]</code> value
   * @return a <code>WordScorePair[]</code> value
   */
  public WordScorePair[] setFreqWordScoreArray(WordScorePair[] wsp){
    int i = 0;
    for (Iterator e = tTable.entrySet().iterator(); e.hasNext() ;)
			{
        Map.Entry kv = (Map.Entry) e.next();
        wsp[i++] = new WordScorePair((String) kv.getKey(), 
                                     (double) ((HashMap)kv.getValue()).size());
      }
    return wsp;
  }

  public WordScorePair[] getWordScoreArray(){
    WordScorePair [] wsp = new WordScorePair[tTable.size()];
    int i = 0;
    for (Iterator e = tTable.entrySet().iterator(); e.hasNext() ;)
			{
        Map.Entry kv = (Map.Entry) e.next();
        wsp[i++] = new WordScorePair((String) kv.getKey(), 
                                     (double) ((HashMap)kv.getValue()).size());
      }
    return wsp;
  }

  /**
   * Get the number of files a term occurs in.
   *
   * @param term word (type) to be looked up
   * @return an <code>int</code> value containing the number of files
   * that contain at least one token of type <code>term</>
   */
  public int getTermCount(String term){
    return ((Map)tTable.get(term)).size();
  }

  /**
   *   Return the number of occurrences of <code>term</code> in
   *   document <code>id</code>
   *
   * @param id a <code>String</code> representing a unique document id
   * @param term a <code>String</code> 
   * @return the number of occurrences
   */
  public int getCount(String id, String term){
    Integer count = (Integer) ((Map)tTable.get(term)).get(id);
    if (count == null)
      return 0;
    else
      return count.intValue();
  }

  /**
   *   Return the number of occurrences of <code>term</code> in the
   *   corpus
   *
   * @param term a <code>String</code> 
   * @return the number of occurrences
   */
  public int getCount(String term){
    Map idmap = (Map)tTable.get(term);
    int count = 0;
    for (Iterator e = idmap.entrySet().iterator(); e.hasNext() ;)
			{
        Map.Entry kv = (Map.Entry) e.next();
        count += ((Integer)kv.getValue()).intValue();
      }
    return count;
  }

  public int[] getCooccurrenceVector(String term, String[] terms){
    Set ts = getDocSet(term);
    int[] cov = new int[terms.length];
    for (int i = 0; i < terms.length; i++) {
      Set ts2 = new HashSet(getDocSet(terms[i]));
      ts2.retainAll(ts);
      cov[i] = ts2.size();
    }
    return cov;
  }


  private Set getDocSet(String term){
    return ((Map)tTable.get(term)).keySet();
  }

  public boolean occursInCategory(String term, String cat){
    Set ds = ((Map)tTable.get(term)).keySet();
    Set cs = (Set)cTable.get(cat);
    for (Iterator i = ds.iterator(); i.hasNext(); ) 
      if (cs.contains(i.next()))
        return true;
    return false;
  }



  /**
   * Get the value of ignoreCase.
   * @return value of ignoreCase.
   */
  public boolean isIgnoreCase() {
    return ignoreCase;
  }
  
  /**
   * Set the value of ignoreCase.
   * @param v  Value to assign to ignoreCase.
   */
  public void setIgnoreCase(boolean  v) {
    this.ignoreCase = v;
  }
}
