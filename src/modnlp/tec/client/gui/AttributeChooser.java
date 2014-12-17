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

import modnlp.idx.headers.HeaderDBManager;
import java.awt.BorderLayout;
import javax.swing.BoxLayout;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 *  GUI component for choosing attributes out of a list
 *
 * @author  S Luz &#60;luzs@acm.org&#62;
 * @version <font size=-1>$Id: $</font>
 * @see  
*/

public class AttributeChooser extends JPanel implements ListSelectionListener{

  JList valueList = null;
  JCheckBox excludeValues = new JCheckBox("Exclude");
  DefaultComboBoxModel valueListModel = null;
  String attributePath;
  Vector actionListeners = new Vector();
  ActionEvent defaultActionEvent = null;

  public AttributeChooser(String title, String ap, String[] values) {
    defaultActionEvent = new ActionEvent(this,0,"");
    valueList = new JList();
    valueList.addListSelectionListener(this);
    attributePath = HeaderDBManager.XQVAR+"/"+ap;
    //valueList.setVisibleRowCount(7);
    setBorder(BorderFactory.
              createCompoundBorder(BorderFactory.createTitledBorder(title),
                                   BorderFactory.createEmptyBorder(5,5,5,5)));
    JScrollPane vlsp = new JScrollPane(valueList);
    this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    add(vlsp);
    excludeValues.setEnabled(false);
    excludeValues.addActionListener(new ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
          fireActionEvents();;
        }});
    add(excludeValues);
    setListData(values);
  }

  public void valueChanged(ListSelectionEvent evt){
    if (valueList.getSelectedIndex() < 0)
      excludeValues.setEnabled(false);
    else
      excludeValues.setEnabled(true);
    fireActionEvents();
  }

  private void fireActionEvents (){
    int s = actionListeners.size();
    for (int i = 0; i < s; i++) {
      ((ActionListener)actionListeners.get(i)).actionPerformed(defaultActionEvent);
    }
  }

  public void addActionListener(ActionListener al){
    actionListeners.add(al);
  }

  public void setListData(String [] val) {
    valueListModel = new DefaultComboBoxModel(val);
    valueList.setModel(valueListModel);
  }

  public void setEnabled(boolean b) {
    valueList.setEnabled(b);
    excludeValues.setEnabled(b);
  }

  public void reset(){
    int [] i = new int[0];
    valueList.setSelectedIndices(i);
    excludeValues.setSelected(false);
  }

  public final String getSelection(){
    return getBooleanExpression();
  }

  public final boolean hasSelection(){
    return valueList.getSelectedIndex() >= 0;
  }

  public String getBooleanExpression () {
    StringBuffer sb = new StringBuffer();
    Object[] sel = valueList.getSelectedValues();
    String empty = "";
    if (sel.length == 0) 
      return "";
    // handle anomaly of empty values as selections (the header files
    // shouldn't really contain any) empty strings mess up the layout
    // of JComboBox so they need to show as " " (see
    // modnlp/idx/headers/HeaderDBManager#getOptionSet)
    sel[0] = sel[0].equals(" ") ? empty : sel[0];
    sb.append(attributePath+"='"+sel[0]+"'");
    for (int i = 1; i < sel.length; i++) {
    // handle anomaly of empty values as selections (the header files shouldn't really contain any)
      sel[i] = sel[i].equals(" ") ? empty : sel[i];
      sb.append(" or "+attributePath+"='"+sel[i]+"'");
    }
    if (excludeValues.isSelected())
      return "not ("+sb+")";
    return ""+sb+"";
  }

  public static void main (String[] args){
    String[] o = {"ES", "EN", "EL", "DE", "FR"};
    final AttributeChooser ac = new AttributeChooser("Speech language", "speech/@language", o);
    javax.swing.JFrame f = new javax.swing.JFrame("TEST");
    javax.swing.JButton b = new javax.swing.JButton("Test");
    b.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent evt) {System.err.println(ac.getBooleanExpression());}});
    f.getContentPane().add(ac,BorderLayout.NORTH);
    f.getContentPane().add(b,BorderLayout.SOUTH);
    f.pack();
    f.setVisible(true);
  }

}
