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

import modnlp.idx.IndexManager;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;



import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import java.io.File;
/**
 *  Main IndexManager frame, from which most of IndexManager is controlled
 *
 * @author  S Luz &#60;luzs@acm.org&#62;
 * @version <font size=-1>$Id: $</font>
 * @see  
*/

public class IndexManagerUI extends JFrame 
  implements ActionListener, IndexingReporter 
{
  IndexManager parent;

  private JTextArea textArea;
  private JButton dismissButton = new JButton("QUIT");
  private JButton loadButton = new JButton("Index new files");
  private JButton newCorpusButton = new JButton("New index");
  private JButton stopButton = new JButton("Stop processing");
  private JButton clearButton = new JButton("Clear screen");
  private JButton deindexButton = new JButton("De-index selected files");
  private DefaultComboBoxModel corpusListModel;
  private JList corpusList;
  private JScrollBar scrollBar;
  private String currentDir = null;
  boolean debug = false;

  public IndexManagerUI (IndexManager p) {
   super("Corpus and index manager");
   parent = p;
   textArea = new JTextArea(10,80);
   textArea.setLineWrap(true);
   textArea.setWrapStyleWord(true);
   JScrollPane scrollPane = new JScrollPane(textArea);
   scrollPane.setPreferredSize(new Dimension(600, 300));
   dismissButton.addActionListener(this);
   loadButton.setToolTipText("Select a set of files for indexing");
   loadButton.addActionListener(this);
   newCorpusButton.setToolTipText("Select or create a new location for a corpus index");
   newCorpusButton.addActionListener(this);
   clearButton.setToolTipText("Clear the log window");
   clearButton.addActionListener(this);
   deindexButton.setToolTipText("Remove selected files from index");
   deindexButton.addActionListener(this);
   stopButton.setToolTipText("Gracefully stop indexing/de-indexing process");
   stopButton.addActionListener(this);
   
   JPanel pa = new JPanel();
   pa.add(loadButton);
   pa.add(clearButton);
   pa.add(stopButton);
   pa.add(newCorpusButton);
   pa.add(dismissButton);

   enableChoice(true);

   getContentPane().add(pa, BorderLayout.NORTH);
   JPanel spa = new JPanel(new BorderLayout());
   spa.add(new JLabel(" Indexing log:"), BorderLayout.NORTH);
   spa.add(scrollPane, BorderLayout.CENTER);
   spa.add(new JLabel("      "), BorderLayout.SOUTH);
   scrollBar = scrollPane.getVerticalScrollBar();

   getContentPane().add(spa, BorderLayout.CENTER);

   JPanel lpa = new JPanel(new BorderLayout());
   lpa.add(new JLabel(" Currently indexed files"), BorderLayout.NORTH);
   corpusList = new JList();
   //corpusList.setVisibleRowCount(7);
   JScrollPane clsp = new JScrollPane(corpusList);
   
   clsp.setPreferredSize(new Dimension(600,150));
   lpa.add(clsp, BorderLayout.CENTER);
   lpa.add(deindexButton, BorderLayout.SOUTH);
   getContentPane().add(lpa, BorderLayout.SOUTH);
  }

  public void setCorpusListData(String [] ifn) {
    corpusListModel = new DefaultComboBoxModel(ifn);
    corpusList.setModel(corpusListModel);
  }


  public void addIndexedFile(String fn){
    corpusListModel.addElement(fn);
  }

 public void removeIndexedFile(String fn){
    corpusListModel.removeElement(fn);
  }

  public void actionPerformed(ActionEvent evt)
  {
    if(evt.getSource() == dismissButton){
      if (JOptionPane.showConfirmDialog(this,
                                        "Really quit?", 
                                        "Really quit?", 
                                        JOptionPane.YES_NO_OPTION) 
          == JOptionPane.YES_OPTION) 
        {
          parent.exit(0);
        }
    }
    if(evt.getSource() == stopButton){
      if (JOptionPane.showConfirmDialog(this,
                                        "Stop (de)indexing?",
                                        "Stop (de)indexing?",
                                        JOptionPane.YES_NO_OPTION) 
          == JOptionPane.YES_OPTION) 
        {
          parent.setStop(true);
        }
    }
    else if(evt.getSource() == clearButton)
      textArea.setText(null);
    else if(evt.getSource() == newCorpusButton)
      parent.chooseNewCorpus();
    else if(evt.getSource() == loadButton)
      {
        CorpusFilesChooser filedial = (currentDir == null ) ? 
          new CorpusFilesChooser(): new CorpusFilesChooser(currentDir);
        int returnVal = filedial.showOpenDialog(this);//filedial.showDialog(this, "Select files");
        if (returnVal == CorpusFilesChooser.APPROVE_OPTION)
          {
            File[] files = filedial.getSelectedFiles();
            currentDir = files[0].getParent();
            enableChoice(false);
            parent.indexSelectedFiles(files);
          }
      }
    else if(evt.getSource() == deindexButton)
      {
        Object[] sel = corpusList.getSelectedValues();
        String q = "Are you sure you want to de-index all "+
          sel.length+" selected files?";
        if (JOptionPane.showConfirmDialog(this, q, q,
                                          JOptionPane.YES_NO_OPTION) 
            == JOptionPane.YES_OPTION) 
        {
          enableChoice(false);
          parent.deindexSelectedFiles(sel);
        }
      }
  }

  public void enableStop(boolean e){
    stopButton.setEnabled(e);
  }

  public void enableDismiss(boolean e){
    dismissButton.setEnabled(e);
  }

  public void enableChoice(boolean e){
    newCorpusButton.setEnabled(e);
    loadButton.setEnabled(e);
    deindexButton.setEnabled(e);
    stopButton.setEnabled(!e);
  }

  public void setCurrentDir(String cd){
    currentDir = cd;
  }

  public void print (String s){
    if (isVisible()){
      textArea.append(s);
      this.scrollBar.setValue(this.scrollBar.getMaximum());
    }
    if (!isVisible() || debug)
      System.err.print(s);    
  }

}
