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

import modnlp.dstruct.TokenMap;
import modnlp.util.Tokeniser;

import java.net.URL;
import java.io.*;

import gnu.regexp.*;

/**
 *  Tokenise a chunk of text and record the position of each token
 *
 * @author  S Luz &#60;luzs@acm.org&#62;
 * @version <font size=-1>$Id: TokeniserGNU.java,v 1.2 2006/05/22 17:26:02 amaral Exp $</font>
 * @see  
*/
public class TokeniserGNU extends Tokeniser {

  private String bigWordRegexp = "[\\w-]+'?s?"; //"[\\w-]+'?s?";
  private String wordRegexp = "\\w+|'s?";
  private String ignoredElements = "(omit|ignore)";  


  public TokeniserGNU(String t){
    super(t);
  }

  public TokeniserGNU(File t, String e) throws IOException {
    super(t,e);
  }

  public TokeniserGNU(URL t, String e) throws IOException {
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
   * Set the <code>IgnoredElements</code> value.
   *
   * @param newIgnoredElements The new IgnoredElements value.
   */
  public final void setIgnoredElements(final String newIgnoredElements) {
    this.ignoredElements = newIgnoredElements;
  }

  public void tokenise () {

    try { 
      System.err.println("matching ingore tags");
      
      String ignregexp = "";
      if (ignoredElements != null && ignoredElements.length() > 0)
        ignregexp = "< *"+ignoredElements+".*?>.*?</"+ignoredElements+" *>";
      if (!tagIndexing)
        ignregexp = ignregexp+"|<.*?>";
      
      RE igns = new RE(ignregexp);
      
      REMatch[] igm = igns.getAllMatches(originalText);
      StringBuffer tx = new StringBuffer(originalText);
      System.err.println("replacing ignored text");
      for (int i = 0; i < igm.length ; i++) {
        int s = igm[i].getStartIndex();
        int e = igm[i].getEndIndex();
        char sp[] = new char[e-s];
        for (int j = 0;  j < sp.length; j++) {
          sp[j] = ' ';
        }
        tx.replace(s,e,new String(sp));
      }
      String text = new String(tx);
      System.out.println("tokenising \n-->"+text+"<--");
      RE bwre = new RE(bigWordRegexp,RESyntax.RE_CHAR_CLASSES);
      RE wre = new RE(wordRegexp,RESyntax.RE_CHAR_CLASSES);
      
      REMatch[] bwm = bwre.getAllMatches(text);
      
      System.err.println("indexing");
      for (int i = 0; i < bwm.length ; i++) {
        int pos = bwm[i].getStartIndex();
        tokenMap.putPos(bwm[i].toString(), pos);
        REMatch[] wm = wre.getAllMatches(bwm[i]);
        if (wm.length == 1)
          continue;
        for (int j = 0; j < wm.length ; j++) {
          int pos2 = pos+wm[j].getStartIndex();
          if (wm[j].toString().equals("'s"))
            tokenMap.putPos("'",pos2);
          else
            tokenMap.putPos(wm[j].toString(),pos2);
        }
      }
    }
    catch (REException e){
      System.err.println("TokeniserGNU: error tokenising text: "+e);
    }

  }

  public boolean getTagIndexing() {
    return tagIndexing;
  }

  public void setTagIndexing(boolean v) {
    tagIndexing = v;
  }

  public static void main(String[] args){
    try {
      BufferedReader in = 
        new BufferedReader(new InputStreamReader(new URL(args[0]).openStream()));
      StringBuffer sb = new StringBuffer(in.readLine()+" ");
      String line = null;
      while ((line = in.readLine()) != null) {
        sb.append(line);
        sb.append(" ");
      }
      TokeniserGNU t = new TokeniserGNU(sb.toString());
      t.setTagIndexing(true);
      t.tokenise();
      System.out.println(t.tokenMap);
    } catch (Exception e) {
      System.err.println("ERRPR!"+e);
      e.printStackTrace(System.err);
    }

  }
}
