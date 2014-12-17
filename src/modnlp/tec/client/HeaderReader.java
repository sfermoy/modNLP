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
import java.awt.*;
import java.net.*;
import java.io.*;
/**
 *  This is a Client to a standard httpd server 
 *  that requests an URL and stores its content 
 *  public class variable (<code>content</code>
 *
 * @author  S. Luz &#60;luzs@acm.org&#62;
 * @version <font size=-1>$Id: HeaderReader.java,v 1.1.1.1 2000/07/07 16:54:36 luz Exp $</font>
 * @see  ContextClient
 * @see  Browser
*/
public class HeaderReader {

  private static final int MAXLIN = 5000;
  private DataInputStream data;

  public String headerfile;  
  public String[] content = new String[MAXLIN];
  
  
  public HeaderReader(String url)
  {
    headerfile = url;
    openHeader();
    readLines();
  }

  public void openHeader()
  {
    URL headerURL = null;

    try {headerURL = new URL(headerfile); }
    catch ( MalformedURLException e)
      {
	System.err.println("Can't open URL");
      }
    
    try
      {
	InputStream in = headerURL.openStream();
	data = new DataInputStream(new BufferedInputStream(in));	
      }
    catch (IOException e)
      {
	System.err.println("IO Error: " + e.getMessage());
      }
    
    return;
  }
  
  
  
  public void readLines()
  {
        
    String line;
    int i = 0;
    try
      {
	while((line = data.readLine()) != null && i < MAXLIN)
	  {
	    //System.out.println(line);
	    content[i++] = line;
	  }
      }	
    catch (IOException e)
      {
	System.err.println("IO Error: " + e.getMessage());
      }
  }
}
