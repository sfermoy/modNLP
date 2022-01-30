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
package modnlp.util;

import modnlp.dstruct.TokenMap;
import modnlp.dstruct.TokenIndex;

import java.net.URL;
import java.util.StringTokenizer;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.File;
import java.io.FileInputStream;

/**
 *  Tokenise a chunk of text and record the position of each token
 *
 * @author  S Luz &#60;luzs@acm.org&#62;
 * @version <font size=-1>$Id: Tokeniser.java,v 1.2 2006/05/22 17:26:02 amaral Exp $</font>
 * @see  
*/
public class Tokeniser {

  protected boolean tagIndexing = false; 
  protected boolean verbose = false; 
  protected String originalText;
  protected TokenMap tokenMap;
  
  protected String encoding = "UTF-8";

  public static final char[] SEPTKARR = {' ',
                                         '|',
                                         '\'',
                                         '`',
                                         '"',
                                         '-',
                                         '_',
                                         (new String(",")).charAt(0),
                                         (new String("\n")).charAt(0),
                                         (new String("\t")).charAt(0),
                                         //(new String(".")).charAt(0),
                                         // we want to be able to get abbreviations (e.g. "U.S.A." => "USA")
                                         '?',
                                         '!',
                                         (new String(";")).charAt(0),
                                         ':',
                                         '<',
                                         '>',
                                         '{',
                                         '}',
                                         '[',
                                         ']',
                                         '=',
                                         '+',
                                         '/',
                                         '\\',
                                         '%',
                                         '$',
                                         '*',
                                         '&',
                                         '(',
                                         ')' };
  public static final String SEPTOKEN = new String(SEPTKARR);

  protected Boolean indexPuntuation = false;
  protected Boolean indexNumerals = false;



  public Tokeniser (String text) {
    originalText = text;
    tokenMap = new TokenMap();
  }

  public Tokeniser (URL url, String e) throws IOException {
    encoding = e;
    BufferedReader in = 
      new BufferedReader(new InputStreamReader(url.openStream(), encoding));
    StringBuffer sb = new StringBuffer(in.readLine()+" ");
    String line = null;
    while ((line = in.readLine()) != null) {
      sb.append(line);
      sb.append(" ");
    }
    originalText = sb.toString();
    tokenMap = new TokenMap();
  }

  public Tokeniser (File file,  String e) throws IOException {
    encoding = e;
    BufferedReader in = 
      new BufferedReader(new InputStreamReader(new FileInputStream(file), encoding));
    StringBuffer sb = new StringBuffer(in.readLine()+" ");
    String line = null;
    while ((line = in.readLine()) != null) {
      sb.append(line);
      sb.append(" ");
    }
    originalText = sb.toString();
    tokenMap = new TokenMap();
  }

  /**
   * Gets the value of indexPuntuation
   *
   * @return the value of indexPuntuation
   */
  public final Boolean getIndexPuntuation() {
    return this.indexPuntuation;
  }

  /**
   * Sets the value of indexPuntuation
   *
   * @param setIndexPuntuation Value to assign to this.indexPuntuation
   */
  public void setIndexPuntuation(final Boolean argIndexPuntuation) {
    this.indexPuntuation = argIndexPuntuation;
  }


  /**
   * Gets the value of indexNumerals
   *
   * @return the value of indexNumerals
   */
  public final Boolean getIndexNumerals() {
    return this.indexNumerals;
  }

  /**
   * Sets the value of indexNumerals
   *
   * @param setIndexNumerals Value to assign to this.indexNumerals
   */
  public void setIndexNumerals(final Boolean argIndexNumerals) {
    this.indexNumerals = argIndexNumerals;
  }

  
  public void setTokenMap(TokenMap t){
    tokenMap = t;
  }

  public boolean getTagIndexing() {
    return tagIndexing;
  }

  public void setTagIndexing(boolean v) {
    tagIndexing = v;
  }

  public boolean getVerbose() {
    return verbose;
  }

  public void setVerbose(boolean v) {
    verbose = v;
  }

  public void setIgnoredElements(String i) {
  }

  public String getEncoding() {
    return encoding;
  }

  public void setEncoding(String v) {
    encoding = v;
  }

  public TokenMap getTokenMap() {
    return tokenMap;
  }

  public String getOriginalText() {
    return originalText;
  }


  /**
   * <code>tokenise</code>: Very basic tokenisation; Serious tokenisers
   *  must override this method. Note that positions in the tokenMap
   *  here correspond to the ORDER in which the token appears in
   *  originalText not its actual OFFSET.
   *
   *  @see modnlp.idx.inverted.TokeniserRegex for a proper
   *  implementation.
   */
  public void tokenise () throws java.io.IOException
  {
    int ct = 0;
    StringTokenizer st = new StringTokenizer(originalText, SEPTOKEN, false);
    while (st.hasMoreTokens()){
      tokenMap.putPos(st.nextToken(), ct++);
      if (verbose)
        PrintUtil.printNoMove("Tokenising ...",ct);
    }
    if (verbose)
      PrintUtil.donePrinting();
  }

   public List<String> split (String str) {
    Matcher bwre = Pattern.compile("[\\p{L}.]+|'s?").matcher(str);
    ArrayList<String> ret = new ArrayList<String>();
    while (bwre.find()) {
      String word = bwre.group();
      ret.add(word);
    }
    return ret;
  }

  public List<String> splitWordOnly (String str) {
    Matcher bwre = Pattern.compile("[\\p{L}]+|'s?").matcher(str);
    ArrayList<String> ret = new ArrayList<String>();
    while (bwre.find()) {
      String word = bwre.group();
      ret.add(word);
    }
    return ret;
  }
  public TokenIndex getTokenIndex (String str) {
    Matcher bwre = Pattern.compile("[\\p{L}.]+|'s?").matcher(str);
    TokenIndex ret = new TokenIndex();
    while (bwre.find()) {
      int s = bwre.start();
      int e = bwre.end();
      ret.add(s,e);
    }
    return ret;
  }

  /**
   * Delete dots (e.g. "U.S.A" => "USA"), remove spaces, clean up any
   * remaining garbage left by Tokenizer
   * @return type: a 'clean' type
   */
  public static String fixType(String type)
  {
    char[] nt = new char[type.length()];
    int j = 0;
    for (int i = 0; i < type.length(); i++)
      {
        if ( type.charAt(i) != '.' )
          nt[j++] = type.charAt(i);
      }
      
    return new String(nt, 0, j);
  }

  /**
   * Check is token is a negated token (e.g '-c' in p(t|-c))
   */
  public static boolean isBar(String token){
    return token.charAt(0) == '-';
  }

  /**
   *  Disbar token
   */
  public static String disbar(String token){
    return isBar(token) ? token.substring(1) : token;
  }

}
