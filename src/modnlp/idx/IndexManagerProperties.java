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
package modnlp.idx;

import  modnlp.idx.gui.IndexConfigChooser;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 *  Properties for IndexManager
 *
 * @author  S Luz &#60;luzs@acm.org&#62;
 * @version <font size=-1>$Id: $</font>
 * @see  
*/

public class IndexManagerProperties extends java.util.Properties{

  public static final String PROP_FNAME = 
    System.getProperty("user.dir")+java.io.File.separator+"idxmgr.properties";
  public static final String EXIST_CONF_FNAME = "eXistConf.xml";
  String propertyFileName;

  public IndexManagerProperties () 
  {
    super();
    try {
      this.load(new FileInputStream(PROP_FNAME));
    }
    catch (Exception e) {
      System.err.println("Error reading property file "+propertyFileName+": "+e);    
      System.err.println("Creating new property file");
      setProperty("last.directory",System.getProperty("user.dir"));
    }
  }
  
  public IndexManagerProperties (String propfname) throws IOException 
  {
    super();
    this.load(new FileInputStream(propfname));
  }

  /**
   * Return the number of corpora currently maintained by the
   * IndexManager
   *
   * @return an <code>int</code> value
   */
  public int getNumberOfCorpora() {
    return new Integer(getProperty("number.of.corpora")).intValue();
  }

  public void save () {
    try {
      store(new FileOutputStream(PROP_FNAME), 
            "modnlp.idx.IndexManager's properties");
    }
    catch (Exception e){
      System.err.println("Error writing property file "+PROP_FNAME+": "+e);
    }
  }
  protected void finalize () throws java.lang.Throwable {
    save();
    super.finalize();
  }

}
