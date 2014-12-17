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
package modnlp.dstruct;

/**
 *  Store an array of (integer) offsets and handle conversion to and
 *  from absolute (character offset) positions.
 *
 * @author  S Luz &#60;luzs@acm.org&#62;
 * @version <font size=-1>$Id: $</font>
 * @see  
*/

public class IntOffsetArray {

  private int [] intOSA;

  public IntOffsetArray (){
    
  }

  public IntOffsetArray (int [] poa){
    intOSA = new int[poa.length];
    intOSA[0] = poa[0];
    for (int i = 1; i < poa.length; i++)
      intOSA[i] = poa[i]-poa[i-1];
  }

  public int[] getArray () {
    return intOSA;
  }

  public void setArray (int[] a) {
    intOSA = a;
  }

  public int[] getPosArray () {
    int [] poa = new int[intOSA.length]; 
    poa[0] = intOSA[0];
    for (int i = 1; i < intOSA.length; i++)
      poa[i] = poa[i-1]+intOSA[i];
    return poa;
  }

  public String toString() {
    StringBuffer sb = new StringBuffer();
    for (int i = 0; i < intOSA.length; i++)
      sb.append(intOSA[i]+" ");
    return sb.toString();
  }

  public String toPosString() {
    StringBuffer sb = new StringBuffer();
    int [] poa = getPosArray();
    for (int i = 0; i < poa.length; i++)
      sb.append(poa[i]+" ");
    return sb.toString();
  }
}
