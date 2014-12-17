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
package modnlp.dstruct;

/**
 *  Store begin and end offsets delimiting a sub-corpus (section)
 *
 * @author  S Luz &#60;luzs@acm.org&#62;
 * @version <font size=-1>$Id: $</font>
 * @see  
*/

public class SubcorpusDelimPair implements Comparable {
  int begin;
  int end;

  public SubcorpusDelimPair (int b, int e){
    begin = b;
    end = e;
  }

  public final int getBegin(){
    return begin;
  }

  public final int getEnd(){
    return end;
  }

  public final String toString (){
    return begin+", "+end;
  }

  public final int compareTo (Object scdp)
  {
    return compareTo((SubcorpusDelimPair) scdp);
  }

  public final int compareTo (SubcorpusDelimPair scdp)
  {
    if ( begin == scdp.getBegin() )
      return 0;
    if ( begin > scdp.getBegin() )
      return 1;
    return -1;
  }

}
