/**
 *  (c) 2010 S Luz <luzs@acm.org>
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


import java.util.ArrayList;
import java.util.Collections;


/**
 *  Record start and end position of all tokens of a string (the
 *  string itself will be stored elsewhere)
 *
 * @author  S Luz &#60;luzs@acm.org&#62;
 * @version <font size=-1>$Id: $</font>
 * @see  
*/

public class TokenIndex {

  ArrayList<Integer> startPosList = new ArrayList<Integer>();
  ArrayList<Integer> endPosList = new ArrayList<Integer>();

  public void add(Integer s, Integer e){
    startPosList.add(s);
    endPosList.add(e);
  }

  public void add(int index, Integer s, Integer e){
    startPosList.add(index, s);
    endPosList.add(index, e);
  }
  
  public void add(int s, int e){
    add(new Integer(s), new Integer(e));
  }

  public void add(int index, int s, int e){
    add(index, new Integer(s), new Integer(e));
  }
  
  public TokenCoordinates remove(int i){
    return new TokenCoordinates(startPosList.remove(i).intValue(),
                                endPosList.remove(i).intValue());
  }

  public TokenCoordinates getCoordinates(int i){
    return new TokenCoordinates(startPosList.get(i).intValue(),
                                endPosList.get(i).intValue());
  }

  
  public void reverse(){
    Collections.reverse(startPosList);
    Collections.reverse(endPosList);
  }

  public int size (){
    return startPosList.size();
  }

  public int getStartPos (int index){
    return startPosList.get(index).intValue();
  }

  public int getEndPos (int index){
    return endPosList.get(index).intValue();
  }

  public static class TokenCoordinates {
    public int start;
    public int end;

    public TokenCoordinates(int s, int e){
      start = s;
      end = e;
    }
  }

  public String toString() {
    return("Start: "+startPosList+"\nEnd:   "+endPosList);
    
  }
  

}
