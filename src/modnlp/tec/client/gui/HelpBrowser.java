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

import java.awt.FontMetrics;
import java.awt.Font;
import java.awt.Dimension;
import java.net.URL;
import javax.swing.JFrame;
import java.awt.event.ActionListener;
import javax.swing.JEditorPane;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import java.io.IOException;
import javax.swing.JScrollPane;
import java.awt.event.ActionEvent;

/**
 *  Display help
 *
 * @author  Saturnino Luz &#60;luzs@acm.org&#62;
 * @version <font size=-1>$Id: HelpBrowser.java,v 1.1 2003/06/22 13:48:05 luzs Exp $</font>
 * @see  
*/
public class HelpBrowser 
  extends JFrame 
  implements ActionListener
{

  private JEditorPane segmentPane;
  private JButton dismissButton = new JButton("Dismiss");
  private static String title = new String("TEC Help"); 
  private String fname = "modnlp/tec/client/help/help.html"; 
  private boolean ok = false;

  public HelpBrowser () {
    super(title);
    try {
      ClassLoader cl = this.getClass().getClassLoader();
      createHelpBrowser(cl.getResource(fname));
    }
    catch (Exception e) {
      JOptionPane.showMessageDialog(this, "Help file not found" + fname,
                                    "Help Error", JOptionPane.ERROR_MESSAGE);
    }
  }

  public HelpBrowser (String file, String ti) {
    super(ti);
    fname = file;
    try {
      ClassLoader cl = this.getClass().getClassLoader();
      createHelpBrowser(cl.getResource(fname));
    }
    catch (Exception e) {
      JOptionPane.showMessageDialog(this, "Help file not found" + fname,
                                    "Help Error", JOptionPane.ERROR_MESSAGE);
    }
  }

  public boolean ok () {
    return ok;
  }

  public void createHelpBrowser (URL text) throws java.io.IOException{
    segmentPane = new  JEditorPane(text);
    JScrollPane scrollPane = new JScrollPane(segmentPane);
    scrollPane.setPreferredSize(new Dimension(500, 300));
		dismissButton.addActionListener(this);
    getContentPane().add(scrollPane);
    //getContentPane().add(dismissButton);
    pack();
    setVisible(true);
  }

  public void actionPerformed(ActionEvent evt)
  {
    if(evt.getSource() == dismissButton)
			dispose();
  }




}
