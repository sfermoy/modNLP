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
package modnlp.dstruct;

import java.io.*;
/**
 *  General class for random, read-only access to corpus files
 *  (e.g. for accessing context, concordances etc)
 *
 * @author  S Luz &#60;luzs@acm.org&#62;
 * @version <font size=-1>$Id: CorpusFile.java,v 1.1 2006/05/22 17:26:02 amaral Exp $</font>
 * @see  
*/
public class CorpusFile {

  public static final char JPSPACE = '　';
  public static final char ENSPACE = ' ';
  public static final byte WHITESPACE = 32; // code for ws char


  InputStreamReader fileReader;
  //int static final MAX_FILE_SIZE = 10000000;

  char [] fileArray;
  int noChars;
  String encoding = "UTF8"; // default
  int language = modnlp.Constants.LANG_EN; 
  char spaceChar = ' ';  // default to Latin script white space.

  public void setLanguage(int la){
    switch (la) {
    case modnlp.Constants.LANG_EN:
      setLangEN();
      break;
    case modnlp.Constants.LANG_JP:
      setLangJP();
      break;
    default:
      setLangEN();
      break;
    }
  }

  public void setLangJP (){
    spaceChar = JPSPACE;
    language = modnlp.Constants.LANG_JP; 
  }

  public void setLangEN (){
    spaceChar = ENSPACE;
    language = modnlp.Constants.LANG_EN; 
  }

  /**
   * The <code>ignoreSGML</code> flag controls whether
   * (SGML/XML-style) markup will be output. If <code>true</code>
   * force character reading methods to skip tags of the form '<.*>'
   * @see setSGMLFlag
   * @see readNextChar
   * @see readPreviousCha
   */
  protected boolean ignoreSGML = true;



  public CorpusFile(String fname, String e) throws IOException{
    encoding = e;
    File f = new File(fname);
    // length() gives us length in bytes, so array will generally be overdimensioned
    fileArray = new char[(int)f.length()];
    fileReader = new InputStreamReader(new FileInputStream(f), encoding);
    int n = 0;
    noChars = fileReader.read(fileArray);
    //fileReader.close();
  }


  /** get a number (<code>ctx</code>) of characters surrounding 
   *  the word (<code>wrd</code>) strating at <code>position</code>
   *
   * @param wrd   the keyword
   * @param pos   a (randomly-accessible) position in this file
   * @param ctx   the number of characters before and after 
   *              <code>wrd</code> to be returned
   * @return a string of size <code>ctx + ctx + wrd.length()</code> 
   *         (Note: control characters found in the file will be 
   *          replaced by whitespaces and markup will be ignored
   *          if <code>ignoreSGML</code> is <code>true</code>) 
   * @see ignoreSGML
   * @see readPreviousChar
   * @see readBack
   * @see readNextChar
   * @see readByteNoControl
   */
  public String getWordInContext(Integer pos, String wrd, int ctx)
    throws IOException{ 
    
    char [] lc = new char[ctx];
    char [] rc = new char[ctx+wrd.length()];
    int j = pos.intValue();
    int k = j-1;
    int cp = ctx-1;
    for (int i = 0; i < ctx; i++) {
      k = findNextLeftIndex(k); // find next allowed index (possibly ignoring tags
      lc[cp-i] = k < 0 || Character.isISOControl(fileArray[k])? 
        spaceChar : fileArray[k];
      k--;
      j = findNextRightIndex(j); // find next allowed index (possibly ignoring tags)
      rc[i] = j < 0 || j >= fileArray.length || Character.isISOControl(fileArray[j])? 
        spaceChar : fileArray[j];
      j++;
    }
    for (int i = 0; i < wrd.length(); i++ ){
      j = findNextRightIndex(j); // find next allowed index (possibly ignoring tags)
      rc[ctx+i] = j < 0 || j >= fileArray.length ||  Character.isISOControl(fileArray[j])? 
        spaceChar : fileArray[j];
      j++;
    }
    return (new String(lc))+(new String(rc));
  }

  private int findNextRightIndex (int i) {
    if (i >= fileArray.length) // index gone past end of string, signal ' ' should be entered
      return -1;
    if (!ignoreSGML)  // any char will do
      return i;
    boolean ignore = i < 0 || fileArray[i] == '<' ? true : false;
    while (ignore){
      while ( ++i < fileArray.length && fileArray[i] != '>' ) {}
      i++;
      if (fileArray[i] != '<')
        return i;
      else
        i++;
      if (i == fileArray.length)
        return -1;
    }
    return i;
  }

  private int findNextLeftIndex (int i) {
    if (i < 0) // index gone past begining of string, signal ' ' should be entered
      return -1;
    if (!ignoreSGML)  // any char will do
      return i;
    boolean ignore = fileArray[i] == '>' ? true : false;
    while (ignore){
      while ( i >= 0 && fileArray[i--] != '<' ) {}
      if (i < 0 )
        return -1;
      if (fileArray[i] != '>')
        return i;
    }
    return i;
  }


  public String getWordInContextWithTags(Integer pos, String wrd, int ctx)
    throws IOException{ 
    
    char [] lc = new char[ctx];
    char [] rc = new char[ctx+wrd.length()];
    int j = pos.intValue();
    int k = j-1;
    int cp = ctx-1;
    for (int i = 0; i < ctx; i++) {
      if (k < 0)
        lc[cp-i] = spaceChar;
      else
        lc[cp-i] = fileArray[k--];
      if (j == fileArray.length)
        rc[i] = spaceChar;
      else
        rc[i] = fileArray[j++];
    }
    for (int i = 0; i < wrd.length(); i++ )
      if (j == fileArray.length)
        rc[ctx+i] = spaceChar;
      else
        rc[ctx+i] = fileArray[j++];
    return (new String(lc))+(new String(rc));
  }

  /** get a number (<code>ctx</code>) of characters before
   *  a position
   *
   * @param pos   a (randomly-accessible) position in this file
   * @param ctx   the number of characters before  
   *              <code>position</code> to be returned
   * @return a string of size <code>ctx</code> 
   */
  public String getPreContext(Integer pos, int ctx)
    throws IOException{
    return getPreContext(pos.longValue(),ctx);
  }

  public String getPreContext(long offset, int ctx)
    throws IOException{ 
    char [] ca = new char[ctx];
    int j = (int)offset-1;
    for (int i = 1; i <= ctx; i++) {
      j = findNextLeftIndex(j); // find next allowed index (possibly ignoring tags
      ca[ctx-i] = (j < 0 || Character.isISOControl(fileArray[j])? 
                   spaceChar : fileArray[j]);
      j--;
    }
    return new String(ca);
  }

  public String getPosContext(Integer pos, int ctx) 
    throws IOException{
    return getPosContext(pos.longValue(),ctx);
  }

  public String getPosContext(long offset, int ctx)
    throws IOException{
    char [] ca = new char[ctx];
    int j = (int)offset;
    for (int i = 0; i < ctx; i++) {
      j = findNextRightIndex(j); // find next allowed index (possibly ignoring tags
      ca[i] = (j < 0 || Character.isISOControl(fileArray[j])? 
               spaceChar : fileArray[j]);
      j++;
    }
    return new String(ca);
  }

  /** Set <code>ignoreSGML</code>. Default is <code>false</code>
   * @see ignoreSGML
   */
  public void setSGMLFlag(String yn){
    if (yn.equalsIgnoreCase("no") )
      ignoreSGML = true;
    else
      ignoreSGML = false;
  }

  public void setIgnoreSGML (boolean v){
    ignoreSGML = v;
  }

  public boolean getIgnoreSGML (){
    return ignoreSGML;
  }

  public void close(){
   
  }

  public static void main (String[] a){
    try {
      CorpusFile c = new CorpusFile(a[0], a[1]);
      System.err.println("-->"+c.getWordInContext((new Integer(a[1])), "aa", (new Integer(a[2])).intValue())
                         +"<--");
    } catch (Exception e) {
      System.err.print(e);
    }
  }

}

