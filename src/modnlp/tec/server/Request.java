/** 
 * Project: MODNLP/TEC/SERVER, Based on software developed for the
 * Translated English Corpora (TEC) project, from the Dept of Language
 * Engineering - UMIST (DLE-UMIST)
 *

 * Copyright (c) 2006 S.Luz (TCD)
 *           (c) 1998 S.Luz (DLE-UMIST) 
 *           All Rights Reserved.
 *
 *   This program is free software; you can redistribute it and/or
 *   modify it under the terms of the GNU General Public License
 *   as published by the Free Software Foundation; either version 2
 *   of the License, or (at your option) any later version.
 *   
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
     
 *   You should have received a copy of the GNU General Public License
 *   along with this program; if not, write to the Free Software
 *   Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 *
 *     
 */ 
package modnlp.tec.server;

import java.util.StringTokenizer;
import java.util.Hashtable; 
import java.util.Enumeration; 
import java.net.URLDecoder;
/**
 *  Object representing the client's request to the TEC Server
 *
 * 
 * @author  S Luz &#60;luzs@acm.org&#62;
 * @version <font size=-1>$Id: TecClientRequest.java,v 1.7 2003/08/08 18:40:02 luzs Exp $</font>
 * @see  TecServer
*/
public class Request extends Hashtable {
  public static final int CONCORD = 1;
  public static final int EXTRACT = 2;
  public static final int SQLQUERY = 3;
  public static final int SQLMETAQ = 4;
  public static final int FREQLIST = 5;
  public static final int HEADERBASEURL = 6;
  public static final int ATTCHOPTSPECS = 7;
  public static final int ATTCHOSPTIONS = 8;
  public static final int CDESCRIPTION = 9;
  public static final int FREQWORD = 10;
  public static final int NOOFTOKENS = 11;
  public static final int COLUMNBATCH = 12;
  public static final int ALLHEADERS = 13;
  //public static final int SERVERINFO = 7;
  public static final int NOTREQ = 999;
  public String reqString = null;

  /** Parse client request and initialize a key-value table
   *  to store it
   *  @param rq   a <code>String</code> containing the 
   *              unparsed, CGI-like request
   */
  public  Request(String rq){
    super(5);  // typical number of settings
  
    reqString = rq;
    // deal with get requests:"GET /concordancer?sgml=yes&... HTTP/1.1"
    // trying to preserve backward compatibility with socket clients <= 0.2.0 

    // remove GET and filename (or do nothing if socket request) 
    rq = rq.substring(rq.indexOf("?")+1); 

    // remove ... HTTP/1.1 (or do nothing if socket request) 
    if (rq.indexOf(" ") > 0) 
      rq = rq.substring(0,rq.indexOf(" "));
    try {
      rq = URLDecoder.decode(rq, "UTF-8");
      //System.err.println(rq);
      StringTokenizer st = new StringTokenizer(rq,"&",false);
      while (st.hasMoreTokens()){
        String kv = st.nextToken();
        String k = kv.substring(0,kv.indexOf("="));
        //URLDecoder.decode(kv.substring(0,kv.indexOf("=")), "UTF-8");
        String v = kv.substring(kv.indexOf("=")+1);
        //URLDecoder.decode(kv.substring(kv.indexOf("=")+1), "UTF-8");
        put(k,v);
      }
    }
    catch (java.io.UnsupportedEncodingException ex) {
      System.err.println("Unsupported encoding UTF-8");
      ex.printStackTrace();
    }
  }
  
  /** Test the type of request against a predefined table
   * @return the <code>int</code> code for a type of request
   * @see CONCORD
   * @see EXTRACT
   * @see NOTREQ
   */
  public int typeOfRequest(){
    //System.out.println(this);
    String rq = (String) get("request");
    if (rq.equalsIgnoreCase("concord"))
      return CONCORD;
    if (rq.equalsIgnoreCase("extract"))
      return EXTRACT;
    if (rq.equalsIgnoreCase("sqlquery"))
      return SQLQUERY;
    if (rq.equalsIgnoreCase("sqlmetaquery"))
      return SQLMETAQ;
    if (rq.equalsIgnoreCase("freqlist"))
      return FREQLIST;
    if (rq.equalsIgnoreCase("corpusdesc"))
      return CDESCRIPTION;
    if (rq.equalsIgnoreCase("headerbaseurl"))
      return HEADERBASEURL;
    if (rq.equalsIgnoreCase("attchooserspecs"))
      return ATTCHOPTSPECS;
    if (rq.equalsIgnoreCase("attoptions"))
      return ATTCHOSPTIONS;
    if (rq.equalsIgnoreCase("freqword"))
      return FREQWORD;
    if (rq.equalsIgnoreCase("nooftokens"))
      return NOOFTOKENS;
    if (rq.equalsIgnoreCase("columnbatch"))
      return COLUMNBATCH;
    if (rq.equalsIgnoreCase("dldHeaders"))
      return ALLHEADERS;
    // add more ifs here as your range of services grow
    return NOTREQ;
  }
  

  public boolean hasSQLQuery(){
    return (get("sqlquery") != null); 
  }

  /** Method for test purposes only.
   */
  public void testKeyVal() {
		
    for (Enumeration e = keys() ; e.hasMoreElements() ;) 
      {
				String key = (String) e.nextElement();
				String val = (String) get(key);
				//System.out.println("!"+key+"!"+val+"!");
      }   
  }
	
}
