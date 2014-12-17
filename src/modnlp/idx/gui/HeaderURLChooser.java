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
package modnlp.idx.gui;

import java.awt.Frame;
import javax.swing.JOptionPane;
//import java.net.NetworkInterface;
/**
 *  Choose URL for headers
 *
 * @author  S Luz &#60;luzs@acm.org&#62;
 * @version <font size=-1>$Id: $</font>
 * @see  
*/

public class HeaderURLChooser {

  String url;
  Frame parent;

  public static final int ENTER_OPTION = 0;
  public static final int CANCEL_OPTION = 1;
  public static final int SAME_OPTION = 2;
  

  public HeaderURLChooser(Frame owner, String dcl) {
    parent = owner;
    if (dcl == null)
      try {
        dcl = "http://"+java.net.InetAddress.getLocalHost().getCanonicalHostName()+"/";
        /*        for (Enumeration e = NetworkInterface.getNetworkInterfaces(); 
                  e.hasMoreElements() ;)
                  for (Enumeration f = ((NetworkInterface)e.nextElement()).getInetAddresses()
                  f.hasMoreElements() ;) 
                  {
                  (String)e.nextElement();
                  }
                  }
        */
      }
      catch (java.net.UnknownHostException e){
        dcl = "localhost";
      }
    url = dcl;
  }

  public int showChooseURL () {
    String nip  = JOptionPane.
      showInputDialog(parent, 
                      "URL for public access to Headers (http://...)",
                      url);
    if (nip == null)
      return CANCEL_OPTION;
    else if (nip.equals(url))
      return SAME_OPTION;
    url = nip;
    return ENTER_OPTION;
  }

  public String getURL() {
    return url;
  }




}
