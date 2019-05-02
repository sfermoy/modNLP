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
import java.io.InputStreamReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.*;
import java.util.Properties;
/**
 *  Encapsulate client-related defaults 
 * 
 * @author  Saturnino Luz &#60;luzs@acm.org&#62;
 * @version <font size=-1>$Id: ClientProperties.java,v 1.2 2001/07/31 16:18:53 luzs Exp $</font>
 * @see  
*/
public class ClientProperties extends Properties{

  static final String FNAME = "tecli.properties";
  static final String DEF_SERVER = "ronaldo.cs.tcd.ie";
  static final String DEF_PORT = "1240";
  static final String DEF_HDURL = "http://ronaldo.cs.tcd.ie/tec/headers";

  public ClientProperties () 
  {
    super();
    System.err.println("Starting to load tecli.properties... ");
    try {
      File file = new File(FNAME);
      FileInputStream fis = new FileInputStream(file);
      System.err.println("Loading tecli.properties from FILE: " + file.getAbsoluteFile());
      this.load(fis);
    }
    catch (IOException ex){
      ClassLoader cl = this.getClass().getClassLoader();
      InputStreamReader fis = (new InputStreamReader(cl.getResourceAsStream("tecli.properties")));
      System.err.println("Loading tecli.properties from JAR: " + fis);
      try {
        this.load(fis);
      }
      catch (Exception e) {
        System.err.println("Warning" + e);
        System.err.println("Creating new property file" + FNAME);
        System.err.println("Setting default to remote access to main TEC site");
        setProperty("tec.client.server", DEF_SERVER);
        setProperty("tec.client.port", DEF_PORT);
        setProperty("tec.client.headers", DEF_HDURL);
        setProperty("stand.alone", "no");
        save();
        //e.printStackTrace(System.out);
        //System.exit(1);
      }
    }
    
  }
  public void save () {
    try {
      store(new FileOutputStream(FNAME), 
            "Corpus Browser's properties");
    }
    catch (Exception e){
      System.err.println("Error writing property file "+FNAME+": "+e);
    }
  }
  
  protected void finalize () throws java.lang.Throwable {
    save();
    super.finalize();
  }
  
}
