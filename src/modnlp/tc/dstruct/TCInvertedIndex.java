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

import modnlp.dstruct.*;
import java.util.Set;
import java.util.Vector;
/**
 *  Inverted indices for text categorisation must implement this
 *  interface. It defines methods for storage of and access to
 *  inverted indices of terms and categories with respect to
 *  documents. These indices are 'inverted' in the following sense:
 *  given a term or category, the index points to the documents that
 *  contain that term or category. In the case of terms, the index
 *  should also store the number of occurrences. 
 *
 * @author  S Luz &#60;luzs@acm.org&#62;
 * @version <font size=-1>$Id: TCInvertedIndex.java,v 1.1.1.1 2005/05/26 13:59:30 amaral Exp $</font>
 * @see  BVProbabilityModel
*/
public interface TCInvertedIndex {

  /**
   * Check if the index contains <code>term</code>
   *
   * @param term a term to be looked up.
   * @return true if this index contains <code>term</code>, fals otherwise
   */
  boolean containsTerm(String term);
  
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
  double getCatGenerality(String cat);

  /**
   * Return the set of documents used in the generation of this index
   *
   * @return a <code>Set</code> containing the IDs of all documents
   * indexed in this index
   */
  Set getDocSet ();

  /**
   * Find all categories under which document <code>id</code> has been
   * classified
   *
   * @return a <code>Vector</code> containing the the vector of
   * categories (of type <code>String</code>) to which document id
   * belongs
   */
  Vector getCategVector(String id);

  /**
   * Get all categories in this corpus.
   *
   * @return a <code>Set</code> containing all categories that occur in the corpus
   */
  Set getCategorySet ();

  /**
   * Index each term (type) of each <code>ParsedDocument</code> in
   * <code>ParsedCorpus</code>, except those in
   * <code>stopwdlist</code>, on this index.
   *
   * @param pt a <code>ParsedCorpus</code> value
   * @param swlist a <code>StopWordList</code> value
   */
  void addParsedCorpus (ParsedCorpus pt, StopWordList swlist);

  /**
   * Delete all entries for terms not in the reduced term set. (To be
   * used after TSR)
   */
  void trimTermSet(Set rts);

  /**
   * Get the number of terms (types) indexed by this index
   *
   * @return an <code>int</code> value
   */
  int getTermSetSize();

  /**
   * Get the set of terms (types) indexed by this index
   *
   * @return a <code>Set</code> containing all terms in the index
   */
  Set getTermSet();

  /**
   * Get the number of files a term occurs in.
   *
   * @param term word (type) to be looked up
   * @return an <code>int</code> value containing the number of files
   * that contain at least one token of type <code>term</>
   */
  int getTermCount(String term);

 /**
   *   Return the number of occurrences of <code>term</code> in
   *   document <code>id</code>
   *
   * @param id a <code>String</code> representing a unique document id
   * @param term a <code>String</code> 
   * @return the number of occurrences
   */
  public int getCount(String id, String term);

  /**
   *   Return the number of occurrences of <code>term</code> in the
   *   corpus
   *
   * @param term a <code>String</code> 
   * @return the number of occurrences
   */
  public int getCount(String term);


  /**
   * Return a vector containing the number of documents the word <code>term</code>
   * co-occurs with each term in  <code>terms</code>
   *
   * @param term a <code>String</code> value
   * @param terms a <code>String[]</code> value
   * @return an <code>int[]</code> value
   */
  public int[] getCooccurrenceVector(String term, String[] terms);

  /**
   * Size of the corpus on which this model is based. What this number
   * represents depends on the nature of he model. In Boolean-vector
   * models in which events are sets of documents,
   * <code>corpusSize</code> represents the number of documents.
   */
  public int getCorpusSize();

  /**
   *  make a new <code>WordScorePair</code>, big enough to store all
   *  terms indexed by this index, with scores initialised to zero
   *
   * @return a <code>WordScorePair[]</code> value
   */
  WordScorePair[] getBlankWordScoreArray();

  /**
   *  gets an initialised <code>WordScorePair</code> and populate it
   *  with global term frequency
   *
   * @param wsp a <code>WordScorePair[]</code> value
   * @return a <code>WordScorePair[]</code> value
   */
  public WordScorePair[] setFreqWordScoreArray(WordScorePair[] wsp);

}
