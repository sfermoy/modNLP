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
package modnlp.tec.client.plugin;

import javax.swing.JFrame;
import modnlp.tec.client.Plugin;
import javax.swing.JTextArea;
import javax.swing.JButton;
import modnlp.tec.client.ConcordanceBrowser;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JCheckBox;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.awt.BorderLayout;

/**
 *  Basic subcorpus selection plug-in
 *
 * @author  S Luz &#60;luzs@acm.org&#62;
 * @version <font size=-1>$Id: $</font>
 * @see  
*/
public class SubcorpusSelection extends JFrame
  implements Plugin
{
  
  private JTextArea queryArea = new JTextArea(4, 30);
  JCheckBox activeChecked = new JCheckBox("Enable sub-corpus constraint");
  private JFrame thisFrame = null;
  private static String title = new String("Plugin: SubcorpusSelection 0.1"); 
  private ConcordanceBrowser parent = null;
  private boolean guiLayoutDone = false;

  public SubcorpusSelection() {
    thisFrame = this;
  }

  public void setParent(Object p){
    parent = (ConcordanceBrowser)p;
  }

  public void activate() {
    if (guiLayoutDone){
      setVisible(true);
      return;
    }    
    JPanel pa0 = new JPanel();
    pa0.setLayout(new BorderLayout());
    pa0.add(new JLabel("Subcorpus selection query:"), BorderLayout.NORTH);
    pa0.add(queryArea, BorderLayout.CENTER);
    pa0.add(new JLabel(" "), BorderLayout.EAST);
    pa0.add(new JLabel(" "), BorderLayout.WEST);

    JPanel pa1 = new JPanel();
    pa1.add(activeChecked);
    JPanel pa2 = new JPanel();
    JButton doneButton = new JButton("Dismiss");
    JButton applyButton = new JButton("Apply");
    JButton clearButton = new JButton("Clear");
    pa2.add(doneButton);
    pa2.add(applyButton);
    pa2.add(clearButton);

    add(pa0, BorderLayout.NORTH);
    add(pa1, BorderLayout.CENTER);
    add(pa2, BorderLayout.SOUTH);

    doneButton.addActionListener(new DoneListener());
    applyButton.addActionListener(new ApplyListener());
    clearButton.addActionListener(new ClearListener());
    activeChecked.addActionListener(new activeCheckedListener());
    queryArea.addKeyListener(new QueryListener());
    pack();
    setVisible(true);
    guiLayoutDone = true;
  }


  class DoneListener implements ActionListener
  {
    public void actionPerformed(ActionEvent e)
    {
      String q = queryArea.getText().trim();
      if (q != null && q.length() > 0 ){
        parent.setXQueryWhere(q);
      }
      else {
        parent.setAdvConcFlag(false);
        activeChecked.setSelected(false);
        //activeChecked.doClick();
      }
      thisFrame.setVisible(false);
      //dispose();
    }
  }

  class ApplyListener implements ActionListener
  {
    public void actionPerformed(ActionEvent e)
    {
      String q = queryArea.getText().trim();
      if (q != null && q.length() > 0 ){
        parent.setXQueryWhere(q);
        parent.setAdvConcFlag(true);
        activeChecked.setSelected(true);
        //activeChecked.doClick();
      }
      else {
        parent.setAdvConcFlag(false);
        activeChecked.setSelected(false);
        //activeChecked.doClick();
      }
    }
  }

  class ClearListener implements ActionListener
  {
    public void actionPerformed(ActionEvent e)
    {
      queryArea.setText("");
      parent.setXQueryWhere(null);
      parent.setAdvConcFlag(false);
      activeChecked.setSelected(false);
      //activeChecked.doClick();
    }
  }

  class QueryListener implements KeyListener
  {
    public void keyTyped(KeyEvent e) {
      if (activeChecked.isSelected()) {
        activeChecked.setSelected(false);
      }
    }

    public void keyPressed(KeyEvent e) {
    }

    public void keyReleased(KeyEvent e) {
    }
  }


  class activeCheckedListener implements ActionListener
  {
    public void actionPerformed(ActionEvent e)
    {
      if (activeChecked.isSelected()){
        String q = queryArea.getText().trim();
        if (q != null && q.length() > 0 ){
          parent.setXQueryWhere(q);
          parent.setAdvConcFlag(true);
        }
        else {
          parent.setAdvConcFlag(false);
          activeChecked.setSelected(false);
        }
      }
      else {
        parent.setAdvConcFlag(false);
      }
    }
  }

}

