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

import modnlp.util.Tokeniser;

//import modnlp.tc.tsr.*;
//import modnlp.tc.parser.*;
import java.util.Vector;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
/**
 *  Store number of tokens indexed by types
 * @author  Saturnino Luz &#60;luzs@acm.org&#62;
 * @version <font size=-1>$Id: BagOfWords.java,v 1.1.1.1 2005/05/26 13:59:30 amaral Exp $</font>
*/
public class BagOfWords extends HashMap{


  private boolean ignoreCase = true;

  public BagOfWords ()
  {
    super(); 
  }

  public BagOfWords (String text) 
  {
    super();
      addTokens(text);
  }

  public BagOfWords (String text, StopWordList swlist) 
  {
    super();
      addTokens(text, swlist);
  }


  public void addTokens(String text)  {
    Tokeniser tkzr = new Tokeniser(text);
    try {
      tkzr.tokenise();
      TokenMap tm = tkzr.getTokenMap();
      for (Iterator e = tm.entrySet().iterator(); e.hasNext() ;)
        {
          Map.Entry kv = (Map.Entry) e.next();
          String t = (String) kv.getKey();
          IntegerSet set = (IntegerSet) kv.getValue();
          addToken(t,set.size());
        } 
    }
    catch (java.io.IOException e){
      System.err.println("Error adding tokens to BagOfWords:\n"+e);
    }
    //putAll(tkzr.getTokenMap());
  }

  public void addTokens(String text, StopWordList swlist) {
    Tokeniser tkzr = new Tokeniser(text);
       try {
      tkzr.tokenise();
      TokenMap tm = tkzr.getTokenMap();
      for (Iterator e = tm.entrySet().iterator(); e.hasNext() ;)
        {
          Map.Entry kv = (Map.Entry) e.next();
          String t = (String) kv.getKey();
          if (swlist.contains(t))
            continue;
          IntegerSet set = (IntegerSet) kv.getValue();
          addToken(t,set.size());
        } 
    }
    catch (java.io.IOException e){
      System.err.println("Error adding tokens to BagOfWords:\n"+e);
    }

  }



  /**
   * addToFileCount: tokenize text and add 1 for each type (not token) 
   *     to the frequency list (text is assumed to be a single file)
   */
  public void addTypesToFileCount (String text) throws java.io.IOException
  {
    Tokeniser tkzr = new Tokeniser(text);
    tkzr.tokenise();
    Vector added = new Vector();
    TokenMap tm = tkzr.getTokenMap();
    for (Iterator e = tm.entrySet().iterator(); e.hasNext() ;)
			{
        Map.Entry kv = (Map.Entry) e.next();
        String t = (String) kv.getKey();
        addType(t, added);
      }
  }

  public int addToken (String type)
  {
    //System.err.println("add token|"+type+"|");
    if (type.equals(""))
      return -1;
    return addToken(type, 1);
  }
 
  private int addToken (String type, int number)
  {
    String key = isIgnoreCase() ? 
      Tokeniser.fixType(type.toLowerCase()) : Tokeniser.fixType(type);
    if (  key.equals("") )
      return 0;
    int count = getCount(key);
    put(key,new Integer(count+number));
    return count;
  }

  private int addType (String type, Vector added)
  {
    String key = isIgnoreCase() ? 
      Tokeniser.fixType(type.toLowerCase()) : Tokeniser.fixType(type);
    if (  key.equals("") || added.contains(key) )
      return 0;
    added.add(key);
    int count = getCount(key);
    put(key,new Integer(count+1));
    return count;
  }
  
  public void removeStopWords (StopWordList swl)
  {
    for (Iterator e = swl.iterator(); e.hasNext() ;)
      this.remove(e.next());
  }

  public void removeLessThan (int noccur) {
    for (Iterator e = this.entrySet().iterator(); e.hasNext() ;)
			{
        Map.Entry kv = (Map.Entry) e.next();
        if ( getCount((Integer)kv.getValue()) < noccur)
          e.remove();
      }
  }

  public int getCount (String type) 
  {
    return  getCount((Integer) get(type));
  } 


  private int getCount (Integer ct)
  {
    return  ct == null ? 0 : ct.intValue(); 
  }


  public boolean containsTerm(String type){
    return getCount(type) > 0;
  }

  /**
   * Return an array of objects comparable by double-precision
   * floating point numbers
   */
  public WordScorePair[] getWordScoreArray()
  {
    WordScorePair[] wfp = new WordScorePair[this.size()];
    int i = 0;
    for (Iterator e = this.entrySet().iterator(); e.hasNext() ;)
			{
        Map.Entry kv = (Map.Entry) e.next();
        String type = (String)kv.getKey();
        double notokens = (double) getCount((Integer)kv.getValue());
        wfp[i++] = new WordScorePair(type,notokens);
			}
    return wfp;
  } 

  /**
   * Return an array of comparable objects (e.g. for sorting)
   */
  public WordFrequencyPair[] getWordFrequencyArray()
  {
    WordFrequencyPair[] wfp = new WordFrequencyPair[this.size()];
    int i = 0;
    for (Iterator e = this.entrySet().iterator(); e.hasNext() ;)
			{
        Map.Entry kv = (Map.Entry) e.next();
        String type = (String)kv.getKey();
        int notokens =  getCount((Integer)kv.getValue());
        wfp[i++] = new WordFrequencyPair(type,notokens);
			}
    return wfp;
  } 

  public String[] getTermSet()
  {
    WordFrequencyPair[] wfp = getWordFrequencyArray();
    return BagOfWords.extractTermSet(wfp);
  }

  public static Set extractTermCollection(WordFrequencyPair[] wfp)
  {
    Set tset = new HashSet(wfp.length);

    for (int i = 0; i < wfp.length ; i++)
      tset.add(wfp[i].getWord());
    return tset;
  }

  public static String[] extractTermSet(WordFrequencyPair[] wfp)
  {
    String[] tset = new String[wfp.length];

    for (int i = 0; i < wfp.length ; i++)
      tset[i] = wfp[i].getWord();
    return tset;
  }

  public static String[] extractTermSet(WordScorePair[] wfp)
  {
    String[] tset = new String[wfp.length];

    for (int i = 0; i < wfp.length ; i++)
      tset[i] = wfp[i].getWord();
    return tset;
  }


  public Set keySet() {
    return super.keySet();
  }

  /**
   * Get the value of ignoreCase.
   * @return value of ignoreCase.
   */
  public boolean isIgnoreCase() {
    return ignoreCase;
  }
  

  /**
   * Set the value of ignoreCase.
   * @param v  Value to assign to ignoreCase.
   */
  public void setIgnoreCase(boolean  v) {
    this.ignoreCase = v;
  }
  

}
