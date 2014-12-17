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
package modnlp.idx.query;

import modnlp.dstruct.IntegerSet;
import modnlp.dstruct.WordForms;
import modnlp.idx.database.WordPositionTable;

/**
 *  Build and store a 'pre-processed' query object. This will contain
 *  the search horizons ({@link modnlp.idx.query.Horizon Horizon}) objects, and a set of
 *  byte offsets per wordform for a specific file
 *  ({@link modnlp.idx.database.WordPositionTable
   *  WordPositionTable}). The main point of this class is
 *  to tabulate the results of wpt lookups for use in
 *  ({@link modnlp.idx.database.Dictionary#matchConcordance(PrepContextQuery, int, int[])})
 *
 * @author  S Luz &#60;luzs@acm.org&#62;
 * @version <font size=-1>$Id: $</font>
 * @see  
*/
public class PrepContextQuery {

  WordPositionTable wpt = null;
  Horizon lh = null;
  Horizon rh = null;
  IntegerSet[] lhisa = null; // one integer set per word (union of all wform integer sets) 
  IntegerSet[] rhisa = null;

  public PrepContextQuery (Horizon lh, Horizon rh, WordPositionTable wpt) {
    this.wpt = wpt;
    this.lh = lh;
    this.rh = rh;
    // pre-fetch offsets for left-horizon keywords
    if (lh != null) {
      WordForms[] lwa = lh.getWordArray();
      lhisa = new IntegerSet[lwa.length]; // one integer set per word (union of all wform integer sets) 
      for (int i = 0;  i < lwa.length; i++) {
        Object [] kwa = lwa[i].toArray();
        IntegerSet is = wpt.fetch((String)kwa[0]);
        lhisa[i] = is == null ? new IntegerSet() : is;
        for (int j = 1 ; j < kwa.length; j++)
          if ((is = wpt.fetch((String)kwa[j])) != null)
            lhisa[i].addAll(is);
      }
    }
    // pre-fetch offsets for right-horizon keywords
    if (rh != null) {
      WordForms[] rwa = rh.getWordArray();
      rhisa = new IntegerSet[rwa.length]; // one integer set per word (union of all wform integer sets) 
      for (int i = 0;  i < rwa.length; i++) {
        Object [] kwa = rwa[i].toArray();
        IntegerSet is = wpt.fetch((String)kwa[0]);
        rhisa[i] = is == null ? new IntegerSet() : is;
        for (int j = 1 ; j < kwa.length; j++)
          if ((is = wpt.fetch((String)kwa[j])) != null)
            rhisa[i].addAll(is);
      }
    }
  }

  public Horizon getLeftHorizon() {
    return lh;
  }
  
  public Horizon getRightHorizon() {
    return rh;
  }
  public IntegerSet[] getLeftHorizonIntegerSetArray() {
    return lhisa;
  }
  
  public IntegerSet[] getRightHorizonIntegerSetArray() {
    return rhisa;
  }



}
