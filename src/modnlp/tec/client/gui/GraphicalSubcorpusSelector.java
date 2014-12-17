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

import modnlp.idx.database.Dictionary;
import modnlp.idx.database.DictProperties;
import modnlp.idx.headers.HeaderDBManager;
import modnlp.tec.client.ConcordanceBrowser;
import modnlp.tec.client.RemoteSubcorpusOptionRequest;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.BorderLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JCheckBox;
import javax.swing.ProgressMonitor;
import javax.swing.Timer;

/**
 *  Handle sub-corpus selection
 *
 * @author  S Luz &#60;luzs@acm.org&#62;
 * @version <font size=-1>$Id: $</font>
 * @see  
*/

public class GraphicalSubcorpusSelector extends JFrame {

  HeaderDBManager hdbm;
  MultipleAttributeSelection mas = new MultipleAttributeSelection("Subcorpus selection:");
  boolean loadingDone = false;
  boolean guiLayoutDone = false;
  boolean networkError = false;
  String[] attChsrSpecs;
  JCheckBox activeChecked = new JCheckBox("Sub-corpus selection is on");
  ConcordanceBrowser parent = null;
  private JFrame thisFrame = null;
  int progress = 0;
  Thread thread;
  private Timer timer;
  private boolean remoteServer = false;
  private boolean interrupted = false;
  final JButton doneButton = new JButton("OK");
  final JButton applyButton = new JButton("Apply");
  final JButton clearButton = new JButton("Clear");


  public GraphicalSubcorpusSelector(ConcordanceBrowser p){
    super("Sub-corpus selector");
    thisFrame = this;
    parent = p;
    if (parent.isStandAlone() ){
      attChsrSpecs = parent.getDictionary().getDictProps().getAttributeChooserSpecs();
      hdbm = parent.getHeaderDBManager();
      remoteServer = false;
    }
    else {
      remoteServer = true;
    }
    start();
  }

  public void start() {
    thread = new LoadOptionsThread();
    thread.start();
  }
  
  public void dispose(){
    interrupted = true;
    while (!loadingDone) {
      try {
        Thread.sleep(500);
      } catch (InterruptedException e) {
      }
    }
    /* handled by parent only
    if (hdbm != null){
      hdbm.finalize();
      hdbm = null;
    }
    */
    if (thread != null)
      thread = null;
    super.dispose();
  }

  public boolean isLoadingDone(){
    return loadingDone;
  }

  public String getSelection(){
    return mas.getSelection();
  }

  public void activate() {
    if (!loadingDone) {
      //task = new LongTask();
      ProgressMonitor pm = 
        new ProgressMonitor(this,
                            "Loading subcorpus selector", 
                            "", 0, attChsrSpecs.length);      
      timer = new Timer(1000, new TimerListener(pm));
      timer.start();     
    }
    
    if (guiLayoutDone && loadingDone){
      thisFrame.setVisible(true);
      return;
    }
    JPanel pa0 = new JPanel();
    pa0.setLayout(new BorderLayout());
    pa0.add(mas, BorderLayout.CENTER);
    //pa0.add(new JLabel(" "), BorderLayout.EAST);
    //pa0.add(new JLabel(" "), BorderLayout.WEST);
    
    //JPanel pa1 = new JPanel();
    //pa1.add(activeChecked);
    JPanel pa2 = new JPanel();
    pa2.add(doneButton);
    pa2.add(applyButton);
    pa2.add(clearButton);
    pa2.add(activeChecked);

    thisFrame.add(pa0, BorderLayout.NORTH);
    //thisFrame.add(pa1, BorderLayout.CENTER);
    thisFrame.add(pa2, BorderLayout.SOUTH);

    doneButton.addActionListener(new DoneListener());
    applyButton.addActionListener(new ApplyListener());
    clearButton.addActionListener(new ClearListener());
    activeChecked.addActionListener(new activeCheckedListener());
    mas.addActionListener(new ActionListener(){
                              public void actionPerformed(ActionEvent e){
                                applyButton.setEnabled(true);
                              }});
    if (loadingDone){
      thisFrame.pack();
      thisFrame.setVisible(true);
      guiLayoutDone = true;
    }
  }

  public boolean hasNetworkError(){
    return networkError;
  }

  class LoadOptionsThread extends Thread {

    public void run() {
      loadingDone = false;
      networkError = false;
      if (remoteServer) {
        RemoteSubcorpusOptionRequest ror = 
          new RemoteSubcorpusOptionRequest(parent.getRemoteServer(), parent.getRemotePort());
        try{
          attChsrSpecs = ror.getAttributeChooserSpecs();
        }
        catch(java.net.ConnectException e){
          //JOptionPane.showMessageDialog(null, e, "ERROR", JOptionPane.ERROR_MESSAGE);
          //          parent.showErrorMessage("Could not connect to remote server "+
          //                        parent.getRemoteServer()+":"+parent.getRemotePort()+": "+e);
          e.printStackTrace(System.err);
          loadingDone = true;
          guiLayoutDone = true;
          networkError = true;
          return;
        }
        catch(Exception e){
          thisFrame.add(new JLabel("Sub-corpus selection not supported by "+parent.getRemoteServer()));
          e.printStackTrace(System.err);
          final JButton doneButton = new JButton("OK");
          doneButton.addActionListener(new ActionListener(){
              public void actionPerformed(ActionEvent e){
                thisFrame.setVisible(false);
              }});
          loadingDone = true;
          guiLayoutDone = true;
          return;
        }
      }
      
      for (int i = 0; i < attChsrSpecs.length && !interrupted; i++) {
        progress = i;
        String[] o = {""};
        if (remoteServer) {
          RemoteSubcorpusOptionRequest ror = 
            new RemoteSubcorpusOptionRequest(parent.getRemoteServer(), parent.getRemotePort());
          try {
            System.err.println("Getting "+attChsrSpecs[i+1]);
            o = ror.getOptionSet(attChsrSpecs[i+1]);
          }
          catch(Exception e){
            System.err.println("Error getting remote subcorpus options for "+attChsrSpecs[i+1]+": "+e);
          }
        }
        else {
          o = hdbm.getOptionSet(attChsrSpecs[i+1]);
        }
        //System.err.println(java.util.Arrays.toString(o));
        mas.add(new AttributeChooser(attChsrSpecs[i], attChsrSpecs[++i],o));
      }
      loadingDone = true;
      //hdbm.finalize();
      //hdbm = null;
    }
  }

  class DoneListener implements ActionListener
  {
    public void actionPerformed(ActionEvent e)
    {
      String q = mas.getSelection();
      if (q != null && q.length() > 0 ){
        parent.setXQueryWhere(q);
        parent.setAdvConcFlag(true);
        activeChecked.setSelected(true);
      }
      else {
        parent.setAdvConcFlag(false);
        activeChecked.setSelected(false);
        //activeChecked.doClick();
      }
      applyButton.setEnabled(false);
      thisFrame.setVisible(false);
      //dispose();
    }
  }

  class ApplyListener implements ActionListener
  {
    public void actionPerformed(ActionEvent e)
    {
      String q = mas.getSelection();
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
      ((JButton)e.getSource()).setEnabled(false);
    }
  }

  class ClearListener implements ActionListener
  {
    public void actionPerformed(ActionEvent e)
    {
      mas.reset();
      parent.setXQueryWhere(null);
      parent.setAdvConcFlag(false);
      activeChecked.setSelected(false);
      //activeChecked.doClick();
    }
  }

  class activeCheckedListener implements ActionListener
  {
    public void actionPerformed(ActionEvent e)
    {
      if (activeChecked.isSelected()){
        String q = mas.getSelection();
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

  class TimerListener implements ActionListener {
    ProgressMonitor progressMonitor;

    public TimerListener(ProgressMonitor p){
      progressMonitor = p;
    }

    public void actionPerformed(ActionEvent evt) {
      if (progressMonitor.isCanceled() || loadingDone) {
        progressMonitor.close();
        if (! guiLayoutDone){
          thisFrame.pack();
          thisFrame.setVisible(true);
          guiLayoutDone = true;
        }
        timer.stop();
      } else {
        progressMonitor.setProgress(progress);

      }
    }
  }


}
