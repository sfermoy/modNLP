/**
 *  (c) 2007 S Luz <luzs@acm.org>
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

import modnlp.Constants;
import modnlp.idx.database.DictProperties;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
/**
 *  
 *
 * @author  S Luz &#60;luzs@acm.org&#62;
 * @version <font size=-1>$Id: $</font>
 * @see  
*/

public class RemoteSubcorpusOptionRequest {

  private String server;
  private int portnum;
  private String webcli;
  String encoding = "UTF-8";

  public void setEncoding (String e){
    encoding = e;
  }

  public String getEncoding () {
    return encoding;
  }

  public RemoteSubcorpusOptionRequest(String s, int p){
    server = s;
    portnum = p;
  }

  public RemoteSubcorpusOptionRequest(String w){
    webcli = w;
  }

  
  public String[] getAttributeChooserSpecs() throws Exception{
    TecClientRequest request = new TecClientRequest();
    request.put("request","attchooserspecs");
    if (webcli != null){
      request.setServerURL(webcli);
      request.setServerPORT(-1);
    }
    else {
      request.setServerURL("http://"+server);
      request.setServerPORT(portnum);
    }
    request.setServerProgramPath("/attchooser");
    URL exturl = new URL(request.toString());
    
    HttpURLConnection exturlConnection = (HttpURLConnection) exturl.openConnection();
    //exturlConnection.setUseCaches(false);
    exturlConnection.setRequestMethod("GET");
    BufferedReader input = new
      BufferedReader(new
                     InputStreamReader(exturlConnection.getInputStream(), encoding));
    String al = input.readLine();
    return DictProperties.parseAttributeChooserSpecs(al);
  }

  public String[] getOptionSet (String xqatts) throws Exception{
      TecClientRequest request = new TecClientRequest();
      request.put("request","attoptions");
      request.put("xqueryattribs",xqatts);
      if (webcli != null){
        request.setServerURL(webcli);
        request.setServerPORT(-1);
      }
      else {
        request.setServerURL("http://"+server);
        request.setServerPORT(portnum);
      }
      request.setServerProgramPath("/attoptions");
      URL exturl = new URL(request.toString());
      
      HttpURLConnection exturlConnection = (HttpURLConnection) exturl.openConnection();
      //exturlConnection.setUseCaches(false);
      exturlConnection.setRequestMethod("GET");
      BufferedReader input = 
        new BufferedReader(new
                           InputStreamReader(exturlConnection.getInputStream(), encoding ));
      String s = input.readLine();
      return s.split(Constants.ATTRIBUTE_OPTION_SEP);
    }

}
