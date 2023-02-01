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
package modnlp.tec.client;
import java.lang.*;
import java.util.StringTokenizer;
import java.util.Hashtable;
import java.util.Enumeration;
import java.net.URLEncoder;
/**
 *  Object representing the client's request to the TEC Server
 *
 *
 * @author  Nino Luz &#60;luzs@acm.org&#62;
 * @version <font size=-1>$Id: TecClientRequest.java,v 1.3 2003/06/22 13:48:05 luzs Exp $</font>
 * @see  TecServer
*/
public class TecClientRequest extends Hashtable<String,String> {
  public static final int CONCORD = 1;
  public static final int EXTRACT = 2;
  public static final int SQLQUERY = 3;
  public static final int NOTREQ = 999;
  private String serverURL = null;
  private int serverPORT = 0;
  private String serverProgramPath = null;
  private String encoding = "UTF-8";

  
  public TecClientRequest() {
    super(5);
  }

  /** Parse client request and initialize a key-value table
   *  to store it
   *  @param rq   a <code>String</code> containing the
   *              unparsed, CGI-like request
   */
  public  TecClientRequest(String rq){
    super(5);  // typical number of settings
    StringTokenizer st = new StringTokenizer(rq,"&",false);
    while (st.hasMoreTokens()){
      String kv = st.nextToken();
      String k = kv.substring(0,kv.indexOf("="));
      String v = kv.substring(kv.indexOf("=")+1);
      put(k,v);
    }
  }

  public void setServerURL (String url) {
    serverURL = url;
  }
  public void setServerPORT (int port) {
    serverPORT = port;
  }
  public void setServerProgramPath (String prog) {
    serverProgramPath = prog;
  }
  public String getServerURL () {
    return serverURL;
  }
  public int getServerPORT () {
    return serverPORT;
  }
  public String getServerProgramPath () {
    return serverProgramPath;
  }

  public boolean isValidURL () {
    return (serverURL != null && serverProgramPath != null);
  }

  public String getURLQueryBase () {
    if (this.isValidURL())
      if (serverPORT > 0)
        return getServerURL().lastIndexOf(":") > 6 ?
          getServerURL()+getServerProgramPath()+"?" :
          getServerURL()+":"+getServerPORT()+getServerProgramPath()+"?";
      else
        return getServerURL()+getServerProgramPath()+"?";
    else
      return "";
  }

  // get fully qualified domain name (e.g. genealogies.mvm.ed.ac.uk)
  public static String getServerFQDN (String url) {
    String fqdn = getServerURLBase(url);
    return fqdn.substring(fqdn.lastIndexOf("/")+1);
  }

  
  
  public static String getServerURLBase (String url) {
      int ei = getServerURLBaseEnd(url);
      if (ei < 0)
        ei = url.length();
      return url.substring(0,ei);
  }

  public static int getServerURLPort (String url) {
    String port = getServerURLBase(url);
    int st = port.lastIndexOf(":");
    if (st < 0 || st+1 >= port.length())
      return -1;
    try {
      return (new Integer(port.substring(st+1))).intValue();
    }
    catch (Exception e){
      return -1;
    }
  }
    
  public static String getServerURLPath (String url) {
      int ei = getServerURLBaseEnd(url);
      if (ei < 0)
        return "";
      return url.substring(ei+1);
  }
    
  public static int getServerURLBaseEnd (String url) {
    return url.indexOf('/',10);
  }

  /** Test the type of request against a predefined table
   * @return the <code>int</code> code for a type of request
   * @see CONCORD
   * @see EXTRACT
   * @see NOTREQ
   */
  public int typeOfRequest(){
    String rq = (String) get("request");
    if (rq.equalsIgnoreCase("concord"))
      return CONCORD;
    if (rq.equalsIgnoreCase("extract"))
      return EXTRACT;
     if (rq.equalsIgnoreCase("headerbaseurl"))
      return SQLQUERY;
    // add more ifs here as your range of services grow
    return NOTREQ;
  }

  public int getContextSize ()
  {
    return (new Integer ((String) get("context"))).intValue();
  }
  
  public boolean containsComplexKeyword()
  {
    String kw = (String) get("keyword");
    if ( kw.indexOf("+") > -1  )
      return true;
    else
      return false;
  }
  
  public void put (String key, int value)
  {
    this.put(key, ""+value);
  }
  
  public void put (String key, long value)
  {
    this.put(key, ""+value);
  }
  
  public String toString ()
  {
    StringBuffer sb = new StringBuffer("");
    sb.append(getURLQueryBase());
    for (Enumeration e = keys() ; e.hasMoreElements() ;)
      {
        String key = (String) e.nextElement();
        String val = (String) get(key);
        try {
          sb.append(URLEncoder.encode(key,encoding)+"="+URLEncoder.encode(val, encoding)+"&");
        } catch (java.io.UnsupportedEncodingException ex) {
          System.err.println("Unsupported encoding "+encoding);
          ex.printStackTrace();
        }

      }
    // chop off trailing '&'
    sb.deleteCharAt(sb.length()-1);
    return sb.toString();
  }
  
  /** Method for test purposes only.
   */
  public void testKeyVal() {
    
    for (Enumeration e = keys() ; e.hasMoreElements() ;)
      {
        String key = (String) e.nextElement();
        String val = (String) get(key);
        System.out.println("!"+key+"!"+val+"!");
      }
    
  }
  
}

