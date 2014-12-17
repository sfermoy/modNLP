/**
 *  (c) 2008 S Luz <luzs@acm.org>
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
package modnlp.idx.inverted;

import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.ArrayList;
import java.util.List;

/**
 *  Split a string into an array of tokens according to the
 *  tokenisation scheme defined in TokeniserRegex
 *
 * @author  S Luz &#60;luzs@acm.org&#62;
 * @version <font size=-1>$Id: $</font>
 * @see  
*/

public class StringSplitter {
  private String bigWordRegexp = "\\p{L}[\\p{L}-.]*'?s?"; // include dots for abbrev. (e.g. U.S.A.)
  Pattern pattern;

  public StringSplitter(){
    pattern = (Pattern.compile(bigWordRegexp));
  }

  public StringSplitter(String regxp){
    bigWordRegexp = regxp;
    pattern = (Pattern.compile(bigWordRegexp));
  }

  public List split (String s){
    Matcher bwre = pattern.matcher(s);
    ArrayList ret = new ArrayList();
    while (bwre.find()) {
      int pos = bwre.start();
      String word = bwre.group();
      int iofd = word.indexOf('.');  // index of first dot
      int iolc = word.length()-1;    // index of last char
      boolean hogc = word.indexOf('-')+word.indexOf('\'') == -2 ? false : true;
      if ( iofd >= 0) {      // word is an acronym, possibly with a missing dot or ...
        //System.out.println("-->"+word+"<-- iofd="+iofd+" iolc="+iolc+" hogc="+hogc);
        if (iofd == iolc)    // ... a normal word with a trailing dot
          ret.add(word.substring(0,iolc));
        else if (iofd == 0)    // ... a normal word with a leading dot
          ret.add(word.substring(1,iolc+1)); 
        else if (word.charAt(iolc) == '.' || hogc) // right, this is a complete acronym. hyphenated or genitive
          ret.add(word);      // ... so store as is.
        else if (!hogc)                     // incomplete acronym...
          ret.add(word+".");  // ... add missing dot and store acronym
      }
      else {
        ret.add(word);      // word, hyphenated or genitive containing no dots 
      }
    }
    return ret;
  }
  
}
