/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package modnlp.idx.inverted;

import edu.stanford.nlp.international.arabic.process.ArabicSegmenter;
import edu.stanford.nlp.ling.CoreLabel;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import modnlp.dstruct.TokenIndex;
import modnlp.util.PrintUtil;
import modnlp.util.Tokeniser;
import modnlp.idx.stemmer.Stemmer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.ar.ArabicAnalyzer;
import org.apache.lucene.analysis.ar.ArabicNormalizationFilter;
import java.util.Properties;


/**
 *
 * @author shane
 */
public class TokeniserARLucene extends Tokeniser{
    
  private String ignoredElements = "(omit|ignore)";
  private ArabicSegmenter segmenter = new ArabicSegmenter(new Properties());
  
  public TokeniserARLucene(String t){
    super(t);
  }
  
  public TokeniserARLucene(File t, String e) throws IOException {
    super(t,e);
  }
  
  public TokeniserARLucene(URL t, String e) throws IOException {
    super(t,e);
  }
  
  public final String getIgnoredElements() {
    return ignoredElements;
  }
  
  public void setSegmenterModel(String fileorurl){
    segmenter.loadSegmenter(fileorurl);
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
    //System.out.println("-->"+text+"<--");
    Analyzer ARtokeniser = new ArabicAnalyzer(org.apache.lucene.util.Version.LUCENE_36);
    TokenStream stream = ARtokeniser.reusableTokenStream("text", new StringReader(text));

    OffsetAttribute offsetAttribute = stream.addAttribute(OffsetAttribute.class);
    CharTermAttribute charTermAttribute = stream.addAttribute(CharTermAttribute.class);
    
    while (stream.incrementToken()) {
      int startOffset = offsetAttribute.startOffset();
      int endOffset = offsetAttribute.endOffset();
      String token = charTermAttribute.toString();
      //System.out.println(token);
      List<CoreLabel> processed = segmenter.segmentStringToTokenList(token);
      Stemmer stem = new Stemmer();
      String stemmed = stem.formatWord(""+token+"");
      //System.out.println(stemmed);
      //System.out.println("-----------\n");
      if(processed.size() > 1){
          for (int j = 0; j < processed.size(); j++) {
              CoreLabel get = processed.get(j);
              tokenMap.putPos(get.value(),startOffset);
          }
      }
      if (token!=stemmed)
          tokenMap.putPos(stemmed,startOffset);
      tokenMap.putPos(token,startOffset);
      
    }
    if (verbose)
      PrintUtil.donePrinting(); ct = 1;
  }
  
  public List<String> split (String s){
    ArrayList<String> ret = new ArrayList<String>();
    try {
      Analyzer ARtokeniser = new ArabicAnalyzer(org.apache.lucene.util.Version.LUCENE_36);
      TokenStream stream = ARtokeniser.reusableTokenStream("text", new StringReader(s));
      stream = new ArabicNormalizationFilter(stream);
      
      OffsetAttribute offsetAttribute = stream.addAttribute(OffsetAttribute.class);
      CharTermAttribute charTermAttribute = stream.addAttribute(CharTermAttribute.class);
      
      while (stream.incrementToken()) {
        int startOffset = offsetAttribute.startOffset();
        int endOffset = offsetAttribute.endOffset();
        String token = charTermAttribute.toString();
        ret.add(token);
        //System.out.println(token.str+" \t\tS="+token.start+" E="+token.end);
      }     
    } catch (java.io.IOException e) {
      System.err.println(e);
    }
    return ret;
  }
  
  public TokenIndex getTokenIndex (String str) {
    TokenIndex ret = new TokenIndex();
    try {
      Analyzer ARtokeniser = new ArabicAnalyzer(org.apache.lucene.util.Version.LUCENE_36);
      TokenStream stream = ARtokeniser.reusableTokenStream("text", new StringReader(str));
      stream = new ArabicNormalizationFilter(stream);
      
      OffsetAttribute offsetAttribute = stream.addAttribute(OffsetAttribute.class);
      CharTermAttribute charTermAttribute = stream.addAttribute(CharTermAttribute.class);
      
      while (stream.incrementToken()) {
        int startOffset = offsetAttribute.startOffset();
        int endOffset = offsetAttribute.endOffset();
        String token = charTermAttribute.toString();
        ret.add(startOffset,endOffset);
        //System.out.println(token.str+" \t\tS="+token.start+" E="+token.end);
      }
    }
    catch (java.io.IOException e) {
      System.err.println(e);
    }
    return ret;
  }
    
}
