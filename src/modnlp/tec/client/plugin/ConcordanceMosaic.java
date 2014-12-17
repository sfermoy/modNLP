/**
 *  (c) 2008 S Luz <luzs@acm.org>
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

import modnlp.idx.headers.HeaderDBManager;
import modnlp.tec.client.Plugin;
import modnlp.tec.client.ConcordanceBrowser;
import modnlp.tec.client.ConcordanceObject;
import modnlp.idx.database.Dictionary;
import modnlp.idx.inverted.StringSplitter;

import java.awt.Font;
import java.awt.Dimension;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.FocusListener;
import java.awt.event.FocusEvent;

import java.net.URL;
import java.net.HttpURLConnection;
import javax.swing.JFrame;
import java.io.BufferedReader;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.Timer;
import java.io.PrintWriter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.JTable;
import javax.swing.table.JTableHeader;
import javax.swing.JScrollPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JLabel;
import java.util.StringTokenizer;
import java.io.PipedWriter;
import java.io.PipedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import javax.swing.JOptionPane;
import java.util.Vector;
import java.util.Collections;
import java.awt.event.MouseAdapter;
import javax.swing.table.TableColumnModel;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JFileChooser;
import java.io.File;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Comparator;
import java.util.Vector;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.awt.event.MouseEvent;
import java.text.NumberFormat;
/**
 *  Basic concordance mosaic generator. A concordance mosaic shows an
 *  overview of patterns of left and right contexts for a given
 *  concordance. Unlike a ConcordanceTree, a ConcordanceMosaic does
 *  not attempt to encode hierarchical structure. That is, each column
 *  displays only information about frequency of words on that column. E.g.
<pre>
     ----------------------------------
     |        |         |
     |  rcw11 | rcw21   |
     |        |         |
   KW|        |         |
     |--------|         |   ...
     |        |         |
     |  rcw12 |---------|
     |--------|         |
     |  rcw13 | rcw22   |
     -----------------------------------
 </pre>
 *
 * @author  S Luz &#60;luzs@acm.org&#62;
 * @version <font size=-1>$Id: $</font>
 * @see  
*/

public class ConcordanceMosaic extends JFrame
  implements Plugin
{
  
  private Thread ftwThread;
  private JFrame thisFrame = null;

  JButton saveButton = new JButton("Save");

  private PrintWriter fqlout = null;

  private static String title = new String("MODNLP Plugin: ConcordanceMosaic 0.1"); 
  private ConcordanceBrowser parent = null;
  private boolean guiLayoutDone = false;

  public ConcordanceMosaic() {
    thisFrame = this;
  }

  // plugin interface method
  public void setParent(Object p){
    parent = (ConcordanceBrowser)p;
  }

  // plugin interface method
  public void activate() {
    if (guiLayoutDone){
      setVisible(true);
      return;
    }

    JButton dismissButton = new JButton("Quit");
    dismissButton.addActionListener(new QuitListener());
    saveButton.addActionListener(new SaveListener());

    JPanel pas = new JPanel();
    pas.add(saveButton);
    pas.add(dismissButton);

    //getContentPane().add(pan, BorderLayout.NORTH);
    //getContentPane().add(scrollPane, BorderLayout.CENTER);
    getContentPane().add(pas, BorderLayout.SOUTH);

    //addFocusListener(this);
    //textArea.setFont(new Font("Courier", Font.PLAIN, parent.getFontSize()));
    saveButton.setEnabled(true);
    pack();
    setVisible(true);
    guiLayoutDone = true;
  }



  class QuitListener implements ActionListener
  {
    public void actionPerformed(ActionEvent e)
    {
      thisFrame.setVisible(false);
      //dispose();
    }
  }

  class SaveListener implements ActionListener {
    public void actionPerformed(ActionEvent e)
    {
      try
        {
          JFileChooser filedial = new JFileChooser();
          int returnVal = filedial.showDialog(thisFrame, "Save CSV file to disk");
          if (returnVal == JFileChooser.APPROVE_OPTION)
            {
              File file = filedial.getSelectedFile();
              //System.out.println(file.getName());
              PrintWriter dlf =
                new PrintWriter(new BufferedWriter(new FileWriter(file)));

              //ConcordanceObject[] va =  parent.getConcArray().concArray;
              String tknregexp  = parent.getClientProperties().getProperty("tokeniser.regexp");
              Vector columns = new Vector();
              StringSplitter ss = new StringSplitter();
              for (Iterator<ConcordanceObject> p = parent.getConcordanceVector().iterator(); p.hasNext(); ){
                //for (int i = 0; i < va.length ; i++) {
                ConcordanceObject co = p.next();
                if (co == null)
                  break;
                Object[] tkns = (ss.split(co.getKeywordAndRightContext())).toArray();
                for (int j = 0; j < tkns.length; j++){
                  HashMap wt;
                  try {
                    wt = (HashMap)columns.get(j);
                  }
                  catch (IndexOutOfBoundsException ex){
                    wt = new HashMap();
                    columns.add(wt);
                  }
                  Integer c = (Integer)wt.get((String)tkns[j]);
                  int ct = c == null? 1 : c.intValue()+1; 
                  wt.put(tkns[j],new Integer(ct));
                }
              }
              dlf.println("<table>");
              for (Iterator ic = columns.iterator(); ic.hasNext();){
                HashMap hm = (HashMap)ic.next();
                dlf.println("<td valign='top'>");
                Vector entries = new Vector(hm.entrySet());
                dlf.println("<td valign='top'>");
                Collections.sort(entries, new FrequencySorter(false,true));
                int theight = 0;
                Vector aux = new Vector(); 
                for (Iterator iw = entries.iterator(); iw.hasNext();){
                  Map.Entry me = (Map.Entry) iw.next();
                  int nooc = ((Integer)me.getValue()).intValue();
                  theight = theight+nooc;
                  aux.add(me);
                }
                for (Iterator iw = aux.iterator(); iw.hasNext();){
                  Map.Entry me = (Map.Entry) iw.next();
                  int nooc = ((Integer)me.getValue()).intValue();
                  dlf.println(me.getKey()+": "+nooc+"/"+theight+" ("+((float)nooc/theight)+")<br>");
                }

                dlf.println("</td>");
              }
              dlf.println("<table>");
              dlf.close();
            }
        }
      catch (Exception ex) {
        ex.printStackTrace(System.err);
        JOptionPane.showMessageDialog(null, "Error writing concordance table" + ex,
                                      "Error!", JOptionPane.ERROR_MESSAGE);
      }
    }
  }


  class FrequencySorter implements Comparator {
    boolean csensitive = false;
    boolean ascending = true;

    public FrequencySorter (boolean c, boolean a){
      csensitive = c;
      ascending = a;
    }

    public int compare(Object a, Object b) {
      Map.Entry ea = (Map.Entry)a;
      Map.Entry eb = (Map.Entry)b;
      String w1 = (String)ea.getKey();
      String w2 = (String)eb.getKey();
      Integer i1 = (Integer)ea.getValue();
      Integer i2 = (Integer)eb.getValue();
      
      int cp = i1.compareTo(i2);
      if (cp == 0)
        if (csensitive)
          cp = w1.compareTo(w2);
        else
          cp = w1.toLowerCase().compareTo(w2.toLowerCase());
      if (ascending)
        return cp*-1;
      else
        return cp;
    }
  }

}

