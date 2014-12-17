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
package modnlp.idx.headers;

import modnlp.idx.query.SubcorpusConstraints;
import java.util.HashMap;
/**
 *  Manage caching of SubcorpusConstraints. (This is a very simplistic
 *  implementation which will be replaced by a more sensible one when
 *  time allows; i.e., probably never ;-)
 *
 * @author  S Luz &#60;luzs@acm.org&#62;
 * @version <font size=-1>$Id: $</font>
 * @see  
*/

public final class ConstraintCache extends HashMap {

  int MAXSIZE;
  /**
   *  next key to be deleted when MAXSIZE is reached
   */
  int nextToGo = 0;
  String[] keys;

  public ConstraintCache(int m){
    super();
    //super(11);
    MAXSIZE = m;
    keys = new String[MAXSIZE];
  }

  public void cache(String k, SubcorpusConstraints c){
    if (nextToGo == MAXSIZE) {
      nextToGo = 0;
    }
    //System.err.println("replacing ntg="+nextToGo+"="+keys[nextToGo]);
    remove(keys[nextToGo]);
    keys[nextToGo++] = k;
    put(k,c);
  }

  public SubcorpusConstraints get(String k) {
    return (SubcorpusConstraints)super.get(k);
  }
  

}
