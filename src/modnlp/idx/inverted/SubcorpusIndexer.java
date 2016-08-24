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

import modnlp.util.PrintUtil;
import modnlp.dstruct.SubcorpusMap;
import modnlp.dstruct.SubcorpusDelimPair;

import java.net.URL;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.File;
import java.io.FileInputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *  Tokenise a chunk of text and record the position of each token
 *
 * @author  S Luz &#60;luzs@acm.org&#62;
 * @version <font size=-1>$Id: SubcorpusIndexer.java,v 1.2 2006/05/22 17:26:02 amaral Exp $</font>
 * @see  
*/
public class SubcorpusIndexer {

  private String elementName = "(speech|writing)"; // name of sections to index
  private String attribtName = "ref";          // an attribute name to index (needs to be XML type ID)
  private String encoding; 
  private String originalText;
  private SubcorpusMap tokenMap;
  private boolean verbose;

  public SubcorpusIndexer (URL url, String c, String e, String a) throws IOException {
    encoding = c;
    elementName = e;
    attribtName = a;
    BufferedReader in = 
      new BufferedReader(new InputStreamReader(url.openStream(), encoding));
    StringBuffer sb = new StringBuffer(in.readLine()+" ");
    String line = null;
    while ((line = in.readLine()) != null) {
      sb.append(line);
      sb.append(" ");
    }
    originalText = sb.toString();
    tokenMap = new SubcorpusMap();
  }

  public SubcorpusIndexer (File file,  String c, String e, String a) throws IOException {
    encoding = c;
    elementName = e;
    attribtName = a;
    BufferedReader in = 
      new BufferedReader(new InputStreamReader(new FileInputStream(file), encoding));
    StringBuffer sb = new StringBuffer(in.readLine()+" ");
    String line = null;
    while ((line = in.readLine()) != null) {
      sb.append(line);
      sb.append(" ");
    }
    originalText = sb.toString();
    tokenMap = new SubcorpusMap();
  }

  public SubcorpusIndexer (String t, String e, String a) {
    originalText = t;
    elementName = e;
    attribtName = a;
    tokenMap = new SubcorpusMap();
  }

  /**
   * Get the <code>SectionElementName</code> value.
   *
   * @return a <code>String</code> value
   */
  public final String geElementName() {
    return elementName;
  }

  /**
   * Set the <code>SectionElementName</code> value.
   *
   * @param newSectionElementName The new SectionElementName value.
   */
  public final void setElementName(final String newElementName) {
    this.elementName = newElementName;
  }

  public boolean getVerbose() {
    return verbose;
  }

  public void setVerbose(boolean v) {
    verbose = v;
  }

  // (section v.): get section delimiters
  public void section ()  {
    //verbose = false;
    //System.out.println("-->"+text+"<--");
    Matcher bwre = (Pattern.compile("< *"+elementName+" .*?"
                                    +attribtName+"=['\"](.+?)['\"][^>]*>(.*?)</ *"
                                    +elementName+" *>", Pattern.DOTALL)).matcher(originalText);
    int ct = 1;
    while (bwre.find()) {
      if (verbose)
        PrintUtil.printNoMove("Tokenising ...",ct++);
      int st = bwre.start();
      int ed = bwre.end();
      String id = bwre.group(2);
      //System.err.println("st="+st+"ed="+ed);
      tokenMap.putDelimPair(id, new SubcorpusDelimPair(st, ed));
    }
    if (verbose)
      PrintUtil.donePrinting();
  }


  /**
   * A section index is simply a a token map in the following format
   *
   * <pre>
   *   id  -> (start_section_offset, end_section_offset)
   *   ...
   * </pre>
   * @return a <code>TokenMap</code> value
   */
  public SubcorpusMap getSectionIndex(){
    return tokenMap;
  }

}
