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
package modnlp.tec.client.gui;

import modnlp.gui.WrapLayout;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JViewport;

/**
 *  Combine AttributeChoosers
 *
 * @author  S Luz &#60;luzs@acm.org&#62;
 * @version <font size=-1>$Id: $</font>
 * @see  
*/

public class MultipleAttributeSelection extends JPanel implements ActionListener{

  static final String[] CONNECTORS = {"and", "or"};
  Vector attChoosers = new Vector();
  Vector attConnectors= new Vector();
  JCheckBox useTextQueryCheckBox = new JCheckBox("Use textual query?");
  JTextArea textQuery = new JTextArea(6,50);
  JPanel guiPane;
  JPanel textPane;
  Vector actionListeners = new Vector();
  ActionEvent defaultActionEvent = null;

  public MultipleAttributeSelection (String ti) {
    // option lists pane
    guiPane = new JPanel();
    guiPane.setBorder(BorderFactory.
              createCompoundBorder(BorderFactory.createTitledBorder(ti),
                                   BorderFactory.createEmptyBorder(5,5,5,5)));
    guiPane.setLayout(new WrapLayout());
    // make it scrollable (since we don't know beforehand how many options there will be)
    final JScrollPane scrollPane = new JScrollPane(guiPane);
    scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    scrollPane.setPreferredSize( new Dimension(940, 450) );
    guiPane.setSize(scrollPane.getPreferredSize());
    scrollPane.addComponentListener( new java.awt.event.ComponentAdapter()
      {
        public void componentResized(java.awt.event.ComponentEvent e)
        {
          JScrollPane scr = (JScrollPane)e.getComponent();
          JViewport viewport = scr.getViewport();
          Dimension d = viewport.getSize();
          viewport.getView().setSize(d);
        }
      });
    // text pane
    textPane = new JPanel();
    textPane.setBorder(BorderFactory.
              createCompoundBorder(BorderFactory.createTitledBorder("Text query"),
                                   BorderFactory.createEmptyBorder(5,5,5,5)));
    textPane.setLayout(new FlowLayout());
    setLayout(new BorderLayout());
    textQuery.setLineWrap(true);
    textQuery.setWrapStyleWord(true);
    textPane.add(new JScrollPane(textQuery));
    useTextQueryCheckBox.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent evt) {
          fireActionEvents(useTextQueryCheckBox, "Text checkbox changed");
          int s = attConnectors.size();
          boolean status = !useTextQueryCheckBox.isSelected();
          for (int i = 0; i < s; i++) {
            ((AttributeChooser)attChoosers.get(i)).setEnabled(status);
            ((JComboBox)attConnectors.get(i)).setEnabled(status);
          }
          ((AttributeChooser)attChoosers.get(s)).setEnabled(status);
          if (status)
            setConnectors();
        }});
    textPane.add(useTextQueryCheckBox);
    add(scrollPane,BorderLayout.NORTH);
    //add(textPane,BorderLayout.SOUTH);
  }
  
  public JPanel getTextPane(){
      return textPane;
  }

  public void add(final AttributeChooser ac){
    //JPanel acp = new JPanel();
    if (attChoosers.size() > 0) {
      final JComboBox cb = new JComboBox(CONNECTORS);
      cb.setEnabled(false);
      attConnectors.add(cb);
      guiPane.add(cb);
      cb.addActionListener(this);
      ac.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent evt) {
          setConnectors();
          /*  //efficient, but unsatisfactory 
              (only left connector status is set; 
              breaks if user chooses from right to left)

            if (isConnectable(ac))
            cb.setEnabled(true);
          else
            cb.setEnabled(false);
          */
        }});
    }
    ac.addActionListener(this);
    ac.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent evt) { setConnectors(); }});
    attChoosers.add(ac);
    guiPane.add(ac);
  }

  private void fireActionEvents (Object o, String m){
    int s = actionListeners.size();
    for (int i = 0; i < s; i++) {
      ((ActionListener)actionListeners.get(i)).
        actionPerformed(new ActionEvent(o,
                                        ActionEvent.ACTION_PERFORMED,m));
    }
  }

  public void addActionListener(ActionListener al){
    actionListeners.add(al);
  }

  public void actionPerformed(ActionEvent e){
    textQuery.setText(getSelection());
    fireActionEvents(this, null);
  }

  public final String getSelection(){
    if (useTextQueryCheckBox.isSelected())
      return textQuery.getText();
    int s = attChoosers.size();
    boolean leftfill = false;
    StringBuffer sb = new StringBuffer();
    for (int i = 0; i < s; i++) {
      String be = ((AttributeChooser)(attChoosers.get(i))).getSelection();
      if (be.length() > 0) {
        if (!leftfill) {
          sb.append("("+be+")");
          leftfill = true;
        }
        else {
          String cn = (String)((JComboBox)attConnectors.get(i-1)).getSelectedItem();
          sb.append(" "+cn+" ("+be+")");
        }
      }
    }
    return sb.toString();
  }
  
  // activate left/right connector if is selected and is
  // preceeded/followed by a selected ac
  private void setConnectors(){
    int s = attChoosers.size();
    for (int i = 1; i < s; i++)
      setLHSConnectors((AttributeChooser)attChoosers.get(i));
  }

  private void setLHSConnectors(AttributeChooser ac){
    int s = attChoosers.size();
    boolean connect = false;
    // check left-hand side
    for (int i = 0; i < s; i++) {
      AttributeChooser a = (AttributeChooser)attChoosers.get(i);
      if (a == ac) {
        if (connect && ac.hasSelection())
          ((JComboBox)attConnectors.get(i-1)).setEnabled(true);
        else
          ((JComboBox)attConnectors.get(i-1)).setEnabled(false);
        return;
      }
      if (a.hasSelection())
        connect = true;
    }
  }

  public void reset() {
    int s = attConnectors.size();
    for (int i = 0; i < s; i++) {
      ((JComboBox)attConnectors.get(i)).setSelectedIndex(0);
      ((AttributeChooser)attChoosers.get(i)).reset();
    }
    ((AttributeChooser)attChoosers.get(s)).reset();
    textQuery.setText("");
  }


  public static void main (String[] args){
    String[] o = {"ES", "EN", "EL", "DE", "FR"};
    final AttributeChooser ac = new AttributeChooser("Speech language", "speech/@language", o);
    String[] o1 = {"one", "two", "three", "four", "five", "six", "seven", "eight", "nine"};
    final AttributeChooser ac1 = new AttributeChooser("Number", "speech/@number", o1);
    String[] o2 = {"aaaaa", "bbbbb", "cccc", "dddd"};
    final AttributeChooser ac2 = new AttributeChooser("Letter", "speech/@letter", o2);
    String[] o3 = {"1", "2", "3", "4", "5", "6", "7", "8", "9"};
    final AttributeChooser ac3 = new AttributeChooser("Digit", "speech/@digit", o3);
    final MultipleAttributeSelection mas = new MultipleAttributeSelection("Subcorpus selection:");
    mas.add(ac);
    mas.add(ac1);
    mas.add(ac2);
    mas.add(ac3);
    javax.swing.JFrame f = new javax.swing.JFrame("TEST");
    javax.swing.JButton b = new javax.swing.JButton("Test");
    javax.swing.JButton r = new javax.swing.JButton("Reset");
    b.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
          System.err.println("-->"+mas.getSelection());
        }});
    r.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
          mas.reset();
        }});
    f.getContentPane().add(mas, java.awt.BorderLayout.NORTH);
    f.getContentPane().add(r,java.awt.BorderLayout.CENTER);
   f.getContentPane().add(b,java.awt.BorderLayout.SOUTH);
    f.pack();
    f.setVisible(true);
  }



}
