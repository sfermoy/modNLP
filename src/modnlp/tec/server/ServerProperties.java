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
import java.io.*;
import java.net.*;
import java.util.Properties;
/**
 *  Encapsulate server-related defaults and handle  
 *
 * @author  Saturnino Luz &#60;luzs@acm.org&#62;
 * @version <font size=-1>$Id: ServerProperties.java,v 1.1.1.1 2000/07/09 12:00:30 luz Exp $</font>
 * @see  
*/
public class ServerProperties extends Properties{
  public String WCASE_FNAME, WFREQ_FNAME, WFILE_FNAME, WPOSI_FNAME;


	public ServerProperties () 
	{
    super();
    try {
      FileInputStream fis = new FileInputStream(new File("server.properties"));
      this.load(fis);
      WCASE_FNAME = this.getProperty("word.case.db");
      WFREQ_FNAME = this.getProperty("word.frequency.db");
      WFILE_FNAME = this.getProperty("word.file.db");
      WPOSI_FNAME = this.getProperty("word.position.db");
    }
    catch (Exception e) {
      System.err.println("Property ERROR: " + e);
      e.printStackTrace(System.out);
    }
	}
  
}
