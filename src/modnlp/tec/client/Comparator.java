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

public abstract class Comparator implements java.util.Comparator {
  public int sortContextHorizon = 1;
  public int halfConcordance = 65;
  public boolean punctuation = false;

  public Comparator(){
  }

  public Comparator(int ctx, int half){
    sortContextHorizon = ctx;
    halfConcordance = half;
  }
  public Comparator(int ctx, int half, boolean pton){
    sortContextHorizon = ctx;
    halfConcordance = half;
    punctuation = pton;
  }

  /** Compare two objects for sorting.
      Should return negative if A < B, positive if A > B, else 0.
   */
  public abstract int compare(Object a, Object b);
}

