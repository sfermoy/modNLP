/**
 *  (c) 2008 Saturnino Luz <luzs@acm.org>
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

import java.util.HashMap;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;


/**
 *  Store frequency tables
 *
 * @author  Saturnino Luz &#60;luzs@acm.org&#62;
 * @version <font size=-1>$Id: $</font>
 * @see  
*/

public class FrequencyHash extends  HashMap<String, Integer> {

  int tokenCount = 0;

  public int getTokenCount(){
    return tokenCount;
  }
  public int getTypeCount(){
    return size();
  }

  public double getTypeTokenRatio(){
    return (double)size()/tokenCount;
  }

  public int add(String word, int count, boolean nocase){
    if (count == 0)
      return 0;
    if (nocase)
      word = word.toLowerCase();
    Integer fqi = (Integer)get(word);
    int fq = (fqi == null)? 0 : fqi.intValue();
    fq += count;
    put(word, new Integer(fq));
    tokenCount += count;
    return fq;
  }

  public List getKeysSortedByValue(boolean asc){
    List keys = new ArrayList(keySet());
    Collections.sort(keys,new ValueSorter(this, asc));
    return keys;
  }

  class ValueSorter implements Comparator {

    int ascending;
    Map map;
    ValueSorter(Map m, boolean asc) {
      this.ascending = asc ? 1 : -1;
      this.map = m;
    }
    
    public int compare(Object a, Object b) {
      if(!map.containsKey(a) || !map.containsKey(b)) {
        return 0;
      }
      int va = ((Integer)map.get(a)).intValue();
      int vb = ((Integer)map.get(b)).intValue();
      if( va < vb ) {
        return -1*ascending;
      } 
      else if( va == vb ) {
        return 0*ascending;
      } 
      else {
        return 1*ascending;
      }
    }
  }
}
