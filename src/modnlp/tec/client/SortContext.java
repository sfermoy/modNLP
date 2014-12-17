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

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
/**
 *  Keep track of contexts (words to the left of right of keyword) and their positions (offsst)
 *
 * 
 * @author  Saturnino Luz &#60;luzs@acm.org&#62;
 * @version <font size=-1>$Id: $</font>
 * @see  
*/
public class SortContext {

    ArrayList<String> wordList = new ArrayList();
    ArrayList<Integer> offsetList = new ArrayList();

    public void add(String w, int i){
      wordList.add(w);
      offsetList.add(new Integer(i));
    }

    public void remove(int idx){
      wordList.remove(idx);
      offsetList.remove(idx);
    }

    public void reverse(){
      Collections.reverse(wordList);
      Collections.reverse(offsetList);
    }

   public int getOffset(int idx){
     return offsetList.size() > 0? offsetList.get(idx).intValue() : 0;
    }

   public String getWord(int idx){
     return offsetList.size() > 0? wordList.get(idx): "";
    }


    public List getWordList(){
      return wordList;
    }

    public String[] getWordArray(){
      String[] sa = new String[wordList.size()];
      sa = wordList.toArray(sa);
      return sa;
    }


    public List getOffsetList(){
      return offsetList;
    }
}
