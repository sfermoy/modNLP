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
package modnlp.tec.client.gui;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import java.awt.Frame;

/**
 *  Choose a new remote corpus index (this is just a placeholder
 *  class, until a proper remote corpus chooser can be implemented)
 *
 * @author  S Luz &#60;luzs@acm.org&#62;
 * @version <font size=-1>$Id: $</font>
 * @see  
*/

public class RemoteCorpusChooser {

  String ipaddress;
  Frame parent;

  public static final int ENTER_OPTION = 0;
  public static final int CANCEL_OPTION = 1;
  public static final int SAME_IP = 2;
  

  public RemoteCorpusChooser(Frame owner, String dcl) {
    parent = owner;
    ipaddress = dcl;
  }

  public int showChooseCorpus () {
    String nip  = JOptionPane.
      showInputDialog(parent, 
                      "Address of new corpus server (URL)",
                      ipaddress);
    if (nip == null)
      return CANCEL_OPTION;
    else if (nip.equals(ipaddress))
      return SAME_IP;
    ipaddress = nip;
    return ENTER_OPTION;
  }

  public String getFQDN() {
    return ipaddress;
  }

  
  public String getServer() {
    return ipaddress.substring(0,ipaddress.indexOf(':'));
  }

  public int getPort() {
    if (ipaddress.indexOf(':') < 1)
      return new Integer(0);
    else
      return new Integer(ipaddress.substring(ipaddress.indexOf(':')+1)).intValue();
  }
}
