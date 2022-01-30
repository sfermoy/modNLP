/**
 *  (c) 2018 s sheehan sheehas1@tcd.ie
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

import edu.stanford.nlp.international.arabic.process.ArabicSegmenter;
import edu.stanford.nlp.international.arabic.process.ArabicTokenizer;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.process.Tokenizer;
import edu.stanford.nlp.process.TokenizerFactory;
import modnlp.util.Tokeniser;
import modnlp.util.PrintUtil;


import java.net.URL;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import modnlp.dstruct.TokenMap;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.ar.ArabicAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;




public class TokeniserARLuceneWhitespace extends Tokeniser {

  TokenMap tm2 = new TokenMap();
  private ArabicSegmenter segmenter = new ArabicSegmenter(new Properties());
  
  private String ignoredElements = "(omit|ignore)";
  public static final String PUNCTUATIONWORDREGEXP = "\\p{L}[\\p{L}-.]*'?s?|[.?!;,](?:\\s)";
  public TokeniserARLuceneWhitespace(String t){
    super(t);
  }

  public TokeniserARLuceneWhitespace(File t, String e) throws IOException {
    super(t,e);
  }

  public TokeniserARLuceneWhitespace(URL t, String e) throws IOException {
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

   public void setSegmenterModel(String fileorurl){
    segmenter.loadSegmenter(fileorurl);
  }
   
  public TokenMap getWhiteSpaceTokenMap(){
      return tm2;
  }
  
  public void tokenise () throws IOException {
    //Stemmer stem = new Stemmer();
    
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

    // Whitespace based tokens
    final TokenizerFactory<CoreLabel> tf = ArabicTokenizer.factory();
    final String encoding = "UTF-8";
    Tokenizer<CoreLabel> tokenizer = tf.getTokenizer(new StringReader(text));
    while (tokenizer.hasNext()) {
        CoreLabel token = tokenizer.next();
        if(token.word().matches(PUNCTUATIONWORDREGEXP))
        {
            //whitespace tokens
            tokenMap.putPos(token.word(), token.beginPosition());            
            //roots and striped tokens
            List<CoreLabel> processed = segmenter.segmentStringToTokenList(token.word());
            if(processed.size() > 0){
                for (int j = 0; j < processed.size(); j++) {
                    CoreLabel get = processed.get(j);
                    tm2.putPos(get.value(),token.beginPosition());
                }
            }
        }
    }
    if (verbose)
      PrintUtil.donePrinting(); ct = 1;
  }
}
