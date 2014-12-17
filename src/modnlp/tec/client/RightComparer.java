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

/**
 *  Compare right hand side of concordance strings (for sorting) 
 *
 * 
 * @author  Saturnino Luz &#60;luzs@acm.org&#62;
 * @version <font size=-1>$Id: RightComparer.java,v 1.1.1.1 2000/07/07 16:54:36 luz Exp $</font>
 * @see  
*/
public class RightComparer 
  extends Comparator{

  public RightComparer (){
    super();
  }
  public RightComparer (int ctx, int half){
    super(ctx,half);
  }

  public RightComparer (int ctx, int half, boolean pton){
    super(ctx,half,pton);
  }

  // NB.: This could be made more effifient (space and timewise) if
  // instead of using getRightSortArray() we worked directly with
  // ConcordanceObject's TokenIndex, scanning substrings of
  // ConcordanceObject.getKeywordAndRightContext() as needed.
  public int compare(Object o1, Object o2) {
    ConcordanceObject coa = (ConcordanceObject) o1;
    ConcordanceObject cob = (ConcordanceObject) o2;

    String[] saa = coa.getRightSortArray(punctuation);
    String[] sab = cob.getRightSortArray(punctuation);

    int res;
    for (int i = 0; i < saa.length; i++) {
      if (i >= sab.length)
        return 1;
      if ((res = saa[i].compareToIgnoreCase(sab[i])) != 0) {
        return res;
      }
    }
    if (saa.length < sab.length)
      return -1;
    if (sab.length < saa.length)
      return 1;
    return 0;
 
  }
  
}
