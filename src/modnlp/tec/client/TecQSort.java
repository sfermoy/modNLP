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
package modnlp.tec.client;

import java.util.StringTokenizer;
/**
 *  QuickSort an array of objects with comparison made through a Comparer object
 *
 * 
 * @author  Saturnino Luz &#60;luzs@acm.org&#62;
 * @version <font size=-1>$Id: TecQSort.java,v 1.1.1.1 2000/07/07 16:54:36 luz Exp $</font>
 * @see  Compare
 * @deprecated
 */
public class TecQSort {


  private Comparer comp;
  // Number of tokens to compare
  public int sortContextHorizon = 1;
  public String SEPTOKEN;  

  /** Create a TecQSort object.
      One way to use this would be with local class creation:
      <PRE>
      TecQSort sorter = new TecQSort(new Comparer() {
      public int compare(Object a, Object b) {
      if (a.key == b.key) return 0;
      else if (a.key < b.key) return -1;
      else return 1;
      }
      });
      sorter.sort(array);
      </PRE>
  */
  public TecQSort(Comparer c) {
    comp = c;
  }

  /** Create a TecQSort object with a buil-in Comparer.
   *  @param lor a string ranging over "left" and "right". If
   "left", compare the reverse of the concordance
   strings on the left the concordance position. Otherwise
   compare the string to the right of the concordance position.
  */
  public TecQSort(String lor, int ctx) {
    final int half = ctx;
    if ( lor.equals("left") )
      comp = 
        new Comparer() {
          public int compare(Object a, Object b) {
            if ( a == null ) {
              return -1;
            }
            if ( b == null ) {
              return 1;
            }
            //int half  = ((String) a).length() / 2;
            StringBuffer ab = new StringBuffer(((String) a).substring(0,half));
            StringBuffer bb = new StringBuffer(((String) b).substring(0,half));
            StringTokenizer at = new StringTokenizer(ab.reverse().toString(),
                                                     SEPTOKEN,
                                                     false);
            StringTokenizer bt = new StringTokenizer(bb.reverse().toString(),
                                                     SEPTOKEN,
                                                     false);
            StringBuffer ac = new StringBuffer("");
            StringBuffer bc = new StringBuffer("");
	    // Read up to horizon
            for (int i = 1; i < sortContextHorizon; i++) {
              StringBuffer t = new StringBuffer(at.nextToken());
              ac.append(t.reverse().toString()+" ");
              t = new StringBuffer(bt.nextToken());
              bc.append(t.reverse().toString()+" ");
            }
	    // Insert horizon word
	    StringBuffer t = new StringBuffer(at.nextToken());
	    ac.insert(0, t.reverse().toString()+" ");
	    t = new StringBuffer(bt.nextToken());
	    bc.insert(0, t.reverse().toString()+" ");

            return ac.toString().toLowerCase().compareTo(bc.toString().toLowerCase());

          }
        };
    else 
      comp =
        new Comparer() {
          public int compare(Object a, Object b) {
            if ( a == null ) {
              return -1;
            }
            if ( b == null ) {
              return 1;
            }
            // int half  = ((String) a).length() / 2;
            String as = ((String) a).substring(half);
            String bs = ((String) b).substring(half);
            StringTokenizer at = new StringTokenizer(as,
                                                     SEPTOKEN,
                                                     false);
            StringTokenizer bt = new StringTokenizer(bs,
                                                     SEPTOKEN,
                                                     false);
            // Discard half-keywords
            at.nextToken();            
            bt.nextToken();
            StringBuffer ac = new StringBuffer("");
            StringBuffer bc = new StringBuffer("");
            // Read up to horizon
            for (int i = 1; i < sortContextHorizon ; i++) {
              if ( at.hasMoreTokens() ) {
                ac.append(at.nextToken()+" ");
              }
              if ( bt.hasMoreTokens() ) {
                bc.append(bt.nextToken()+" ");
              }
            }
            // Insert horizon word if any
            if ( at.hasMoreTokens() ) 
              ac.insert(0, at.nextToken()+" ");
            if ( bt.hasMoreTokens() )  
              bc.insert(0, bt.nextToken()+" ");
							
            return ac.toString().toLowerCase().compareTo(bc.toString().toLowerCase());
          }
        };
  }
	

  /** Sorts the array, according to the Comparer. */
  public void sort(ConcordanceVector conc) {
    SEPTOKEN = conc.SEPTOKEN;
    int hi = 0; 
    while ( conc != null )
      hi++;
    hi--;
    ConcordanceObject[] c = new ConcordanceObject[hi];
    quicksort(conc.toArray(c), 0, hi);
  }

  /** sorts a subsequence of the array, according to the Comparer. */
  public void sort(ConcordanceObject[] conc, int start, int end) {
    quicksort(conc, start, end - 1);
  }

  private void quicksort(ConcordanceObject[] conc, int p, int r) {
    if (p < r) {
      int q = partition(conc,p,r);
      if (q == r) {
        q--;
      }
      quicksort(conc,p,q);
      quicksort(conc,q+1,r);
    }
  }

  private int partition (ConcordanceObject[] conc, int p, int r) {
    Object pivot = conc[p].concordance;
    int lo = p;
    int hi = r;
    while (true) {      
      while (comp.compare(conc[hi].concordance, pivot) >= 0 &&
             lo < hi) {
        hi--;
      }
      while (comp.compare(conc[lo].concordance, pivot) < 0 &&
             lo < hi) {
        lo++;
      }
      if (lo < hi) {
        // swap concordance lines
        String T = conc[lo].concordance;
        conc[lo].concordance = conc[hi].concordance;
        conc[hi].concordance = T;
        // swap filenames
        T = conc[lo].filename;
        conc[lo].filename = conc[hi].filename;
        conc[hi].filename = T;
        // swap file position flages
        int I = conc[lo].filepos;
        conc[lo].filepos = conc[hi].filepos;
        conc[hi].filepos = I;      
        // swap position on conc.concordances[] flags
        long L = conc[lo].bytepos;
        conc[lo].bytepos = conc[hi].bytepos;
        conc[hi].bytepos = L;
      }
      else return hi;
    }
  }

}

