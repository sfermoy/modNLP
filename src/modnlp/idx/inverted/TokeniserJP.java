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
package modnlp.idx.inverted;

import  modnlp.idx.inverted.jpsegmenter.*;
import modnlp.dstruct.Token;
import modnlp.dstruct.TokenIndex;
import modnlp.util.Tokeniser;
import modnlp.util.PrintUtil;

import java.net.URL;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.io.StringReader;

/**
 *  Tokenise a chunk of *Japanese laguage* text and record the
 *  position of each token
 *
 * @author  S Luz &#60;luzs@acm.org&#62;
 * @version <font size=-1>$Id: TokeniserRegex.java,v 1.2 2010/10/22 17:26:02 amaral Exp $</font>
 * @see  
*/
public class TokeniserJP extends Tokeniser {

  private String ignoredElements = "(omit|ignore)";

  //private String bigWordRegexp = "\\p{L}[\\p{L}-.]*'?s?"; // include dots for abbrev. (e.g. U.S.A.)
  //private String wordRegexp = "[\\p{L}.]+|'s?";


  public TokeniserJP(String t){
    super(t);
  }

  public TokeniserJP(File t, String e) throws IOException {
    super(t,e);
  }

  public TokeniserJP(URL t, String e) throws IOException {
    super(t,e);
  }

  /**
   * Get the <code>IgnoredElements</code> value.
   *
   * @return a <code>String</code> value
   */
  public final String getIgnoredElements() {
    return ignoredElements;
  }

  /**
   * Get the <code>bigWordRegexp</code> value.
   *
   * @return a <code>String</code> value
   */
  //public final String getBigWordRegexp() {
  //  return bigWordRegexp;
  //}

  /**
   * Set the <code>IgnoredElements</code> value.
   *
   * @param newIgnoredElements The new IgnoredElements value.
   */
  public final void setIgnoredElements(final String newIgnoredElements) {
    this.ignoredElements = newIgnoredElements;
  }

  public void tokenise () throws IOException {
      String ignregexp = "--+|\\.\\.+|\\.+\\p{Space}";  // delete full stops and dashes (typically not used).
      if (ignoredElements != null && ignoredElements.length() > 0)
        ignregexp = ignregexp+
          "|< *"+ignoredElements+"[^>]*?/>"+
          "|< *"+ignoredElements+".*?>.*?</"+ignoredElements+" *>";
      if (!tagIndexing)
        ignregexp = ignregexp+"|<.*?>";
      //ignregexp = ignregexp+"|\\W\\W+";
      
      Pattern p = Pattern.compile(ignregexp);
      Matcher igns = p.matcher(originalText);
      
      StringBuffer tx = new StringBuffer(originalText);
      int ct = 1;
      while (igns.find()) {
        int s = igns.start();
        int e = igns.end();
        if (verbose)
          PrintUtil.printNoMove("Processing exclusions ...",ct++);
        //System.err.println("replacing\n-----------"+originalText.substring(s,e)+"\n--------------");
        char sp[] = new char[e-s];
        for (int j = 0;  j < sp.length; j++) {
          sp[j] = ' ';
        }
        tx.replace(s,e,new String(sp));
      }
      if (verbose)
        PrintUtil.donePrinting(); ct = 1;
      //verbose = false;
      String text = new String(tx);
      //System.out.println("-->"+text+"<--");
      BasicCodePointReader cpr = new BasicCodePointReader(new StringReader(text));
      TinySegmenter sgtr = new TinySegmenter(cpr);
      Token token = null;
      while ((token = sgtr.next()) != null) {
        tokenMap.putPos(token.str,(int)token.start);
        //System.out.println(token.str+" \t\tS="+token.start+" E="+token.end);
      }
      if (verbose)
        PrintUtil.donePrinting(); ct = 1;
  }

 public List<String> split (String s){
   ArrayList<String> ret = new ArrayList<String>();
   BasicCodePointReader cpr = new BasicCodePointReader(new StringReader(s));
   TinySegmenter sgtr = new TinySegmenter(cpr);
   Token token = null;
   try {
     while ((token = sgtr.next()) != null) {
       ret.add(token.str);
       //System.out.println(token.str+" \t\tS="+token.start+" E="+token.end);
     }     
   } catch (java.io.IOException e) {
     System.err.println(e);
   }
    return ret;
 }

  public TokenIndex getTokenIndex (String str) {
    TokenIndex ret = new TokenIndex();
    BasicCodePointReader cpr = new BasicCodePointReader(new StringReader(str));
    TinySegmenter sgtr = new TinySegmenter(cpr);
    Token token = null;
    try {
      while ((token = sgtr.next()) != null) {
        ret.add((int)token.start,(int)token.end);
        //System.out.println(token.str+" \t\tS="+token.start+" E="+token.end);
      }     
    } catch (java.io.IOException e) {
      System.err.println(e);
    }
    return ret;
  }
}
