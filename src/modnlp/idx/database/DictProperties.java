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
package modnlp.idx.database;

import modnlp.idx.IndexManagerProperties;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 *  Load dictionary defaults
 *
 * @author  S Luz &#60;luzs@acm.org&#62;
 * @version <font size=-1>$Id: DictProperties.java,v 1.2 2006/05/22 17:26:02 amaral Exp $</font>
 * @see  
*/
public class DictProperties extends java.util.Properties{

  public static String PROP_FNAME = "dictionary.properties";
  //public static String DEFAULT_PROP_FNAME =  "idxmgr.properties";
  private static String PS = java.io.File.separator;
  String envHome = "/tmp/tec/index";   // a very unsafe default;
  //String headDir = "/tmp/tec/headers/";   // a very unsafe default;
  //String headURL = "file:///tmp/tec/headers/";   //  this is really supposed to be a HTTP address
  String wFilTableName = "wfindex.db";  // word -> [fileno1, fileno2, ...]
  String caseTableName = "caindex.db";  // canonicalform -> [form1, form2, ...]
  String freqTableName = "fqtable.db";  // word -> noofoccurrences
  String fileTableName = "fitable.db";  // fileno -> fileuri
  String tPosTableName = "tptable.db";  // fileno -> [offset1, offset2, ...]
  String corpusDir = ""; // the default directory for relative fileTableNames 
  String encoding  = "UTF-8";
  String headerEXT = "hed";

  public DictProperties (String cd) {
    envHome = cd;
    init();
  }
  
  public DictProperties (){
    init();
  }
  
  private void init(){
    String pf = envHome+PS+PROP_FNAME;
    try {
      this.load(new FileInputStream(pf));
    }
    catch (Exception e) {
      System.err.println("Error reading property file "+pf+": "+e);
      System.err.println("Using defaults in DictProperties.java and "+IndexManagerProperties.PROP_FNAME);
      setProperty("dictionary.environment.home",envHome);
      //setProperty("headers.url",headURL);
      //setProperty("headers.home",headDir);
      setProperty("wfile.table.name", wFilTableName);
      setProperty("case.table.name", caseTableName);
      setProperty("frequency.table.name", freqTableName);
      setProperty("file.table.name", fileTableName);
      setProperty("tpos.table.name", tPosTableName);
      setProperty("corpus.data.directory", corpusDir);
      setProperty("file.encoding", encoding);
      setProperty("header.extension", headerEXT);
      //ClassLoader cl = this.getClass().getClassLoader();
      try{
        this.load(new FileInputStream(IndexManagerProperties.PROP_FNAME));
        //this.load(cl.getResourceAsStream(IndexManagerProperties.PROP_FNAME));
      }
      catch (IOException ioe){
        System.err.println("Error reading default property file "+IndexManagerProperties.PROP_FNAME+": "+e);
      }
      save();
    }
  }

  public void save () {
    try {
      store(new FileOutputStream(envHome+PS+PROP_FNAME), 
            "Dictionary's properties");
    }
    catch (Exception e){
      System.err.println("Error writing property file "+envHome+PS+PROP_FNAME+": "+e);
    }
  }

  protected void finalize () throws java.lang.Throwable {
    save();
    super.finalize();
  }

  public String getEnvHome () {
    return getProperty("dictionary.environment.home");
  }

  //public String getExistConf () {
  //  return getProperty("dictionary.environment.home")+EXIST_CONF_NAME;
  //}

  public String getHeadURL () {
    return getProperty("headers.url");
  }

  public int getLanguage () {
    
    String p = getProperty("language");
    if (p==null) {
      return modnlp.Constants.LANG_EN;  // English is default
    }
    else {
      p = p.toLowerCase();
    }


    if (p==null) 
      return modnlp.Constants.LANG_EN;  // English is default
    if (p.equals("jp"))
      return modnlp.Constants.LANG_JP;
    if (p.equals("ar"))
      return modnlp.Constants.LANG_AR;

    return modnlp.Constants.LANG_EN;  // English is default
  }

  public String getHeadDir () {
    return getProperty("headers.home");
  }

  public String getTPosTableName () {
    return getProperty("tpos.table.name");
  }

  public String getWFilTableName () {
    return getProperty("wfile.table.name");
  }

  public String getCaseTableName () {
    return getProperty("case.table.name");
  }

  public String getFreqTableName () {
    return getProperty("frequency.table.name");
  }

  public String getFileTableName () {
    return getProperty("file.table.name");
  }

  public String getCorpusDir () {
    return corpusDir;
  }

  public String[] getAttributeChooserSpecs(){
    return parseAttributeChooserSpecs(getProperty("xquery.attribute.chooser.specs"));
  }

  public static String[] parseAttributeChooserSpecs(String a){
    return a.split(";");
  }

  public void setCorpusDir (String c){
    if (c == null)
      return;
    corpusDir = c;
    setProperty("corpus.data.directory", corpusDir);
  }

  public String getFullCorpusFileName (String fn){
    if ( (new File(fn)).isAbsolute() )
      return fn;
    else
      return corpusDir+fn;
  }

  // TODO: extend method setProperty(String p, String v) so that local variables always get updated
  // eg if (p.equals("header.extension") {super.setProperty(p, v); headerEXT = v;} etc
  public String getHeaderFilename(String fname) {
    File f = new File(fname);
    String n = f.getName();
    return n.substring(0,n.lastIndexOf('.')+1)+getProperty("header.extension"); 
  }

  public String getHeaderAbsoluteFilename(String fname) {
    String base = getProperty("headers.home");
    if (base.charAt(base.length()-1) == File.separatorChar)
      base = base.substring(0,base.length()-1);
    return base+File.separatorChar+getHeaderFilename(fname);
  }



}
