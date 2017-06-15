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
 * Foundation, 59 Temple Place - Sui
te 330, Boston, MA 02111-1307, USA.
*/
package modnlp.tec.client.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Event;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Arrays;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import java.util.Arrays;
import javax.swing.JViewport;
import javax.swing.event.ListSelectionEvent;

import modnlp.idx.query.WordQuery;
import modnlp.tec.client.ConcordanceVector;
import modnlp.tec.client.ConcordanceBrowser;
import modnlp.tec.client.gui.event.ConcordanceDisplayEvent;
import modnlp.tec.client.gui.event.ConcordanceDisplayListener;
import modnlp.tec.client.gui.event.ConcordanceListSizeEvent;
import modnlp.tec.client.ConcordanceObject;
import modnlp.tec.client.gui.event.DefaultChangeEvent;
import modnlp.tec.client.gui.event.DefaultChangeListener;
import modnlp.tec.client.Download;
import modnlp.tec.client.gui.event.FontSizeChangeEvent;
import modnlp.tec.client.Plugin;
import modnlp.tec.client.gui.event.SortHorizonChangeEvent;

/**
 *  This frame implements a 'concordance browser' that interacts with
 *  the <a href="../server/index.html">TEC Server</a> or works
 *  stand-alone accessing the corpus index through <a
 *  href="../../idx/database/Dictionary.html">Dictionary.java</a>
 *
 * @author  S. Luz &#60;luzs@acm.org&#62;
 * @version <font size=-1>$Id: Browser.java,v 1.9 2003/08/06 16:58:56 luzs Exp $</font>
 * @see  Dictionary
 * @see  Server
*/

public class BrowserFrame extends BrowserGUI 
{
  //******
  public String clicked= "";
  public int column= 0;
  // Strings for the GUI
  private static final String HEDBUT = "Metadata";
  private static final String EXTBUT = "Extract";
  private static final String DLDBUT = "Save";
  private static final String STLBUT = "Sort Left";
  private static final String STRBUT = "Sort Right";
  private static final String PREBUT = "Preferences";
  private static final String DOBUTT = "Search";
  private static final String ASCBUTT = "Subcorpus";
  private static final String QUITBUT = "QUIT";

  private final static int FRAME_WIDTH = 1000;
  private final static int FRAME_HEIGHT = 700;
  private final static int ONE_SECOND = 1000;
  private final static int SRTBARMAX = 6;

  private final static boolean debug = true;
  private JProgressBar progressBar;
  private Timer timer;
  private Timer srt_timer;
  private Timer ucrt_timer;
  private JComboBox leftSortCtx = new JComboBox();
  private JComboBox rightSortCtx = new JComboBox();
  private ListDisplay concListDisplay;
  private String concordances = null;
  private JTextField keyword;
  //private JComboBox case_select;
  private int currentIndex = 0;
  private int height;
  private JButton concButton;

  private JMenu  fileMenu = new JMenu("File");
  private JMenuItem nlcButton = new JMenuItem("New local corpus...");
  private JMenuItem nrcButton = new JMenuItem("New remote corpus...");    
  private JMenuItem dldButton = new JMenuItem("Save concordances...");  
  private JMenuItem quitButton = new JMenuItem(QUITBUT);

  private JMenu  prefMenu = new JMenu("Options");
  private JCheckBoxMenuItem caseCheckBox = 
    new JCheckBoxMenuItem("Case sensitive");
  private JCheckBoxMenuItem punctuationCheckBox = 
    new JCheckBoxMenuItem("Punctuation as tokens");
  private JMenuItem advConcButton = 
    new JMenuItem("Select Sub-corpus...");

  private JCheckBoxMenuItem advConcFlagItem = 
    new JCheckBoxMenuItem("Activate sub-corpus selection");
  private JMenuItem prefButton = new JMenuItem("Preferences...");

  private JMenu  pluginMenu = new JMenu("Plugins");

  private JMenu  helpMenu = new JMenu("Help");
  private JMenuItem helpButton = new JMenuItem("Contents");
  private JMenuItem aboutButton = new JMenuItem("About MODNLP...");    

  private JButton stlButton = new JButton(STLBUT);
  private JButton strButton = new JButton(STRBUT);
  private JButton extractButton = new JButton(EXTBUT);
  private JButton headerButton = new JButton(HEDBUT);

  private JPanel optArea = new JPanel();
  private JPanel outArea = new JPanel();
  private JPanel statusArea = new JPanel();
  private JLabel statusLabel = new JLabel();
  private JLabel statusLabelScroll = new JLabel();
  private JPanel li1 = new JPanel();
  private JPanel opt = new JPanel();
  private JPanel concLabel = new JPanel();
  private int srt_i = 0;
  private String encoding = null;
  private BrowserFrame myself; 
  private SubcorpusCaseStatusPanel sccsPanel;

  ConcordanceBrowser parent = null;

  /** Create a TEC Window Object
   * @param width   window width
   * @param height   window height
   */
  public BrowserFrame(int width, int height, ConcordanceBrowser parent){
    super();
    myself = this;
    concListDisplay = new ListDisplay(this, parent.getConcordanceVector());
    setListFontSize(parent.getPreferredFontSize());
    setTitle(parent.getBrowserName());
    width = width == 0? FRAME_WIDTH : width;
    height = height == 0? FRAME_HEIGHT : height;
    setSize(width,height);
    this.parent = parent;
  }

  // ok
  public void initGUI(){
    Container contentPane = getContentPane();
    setFont(new Font("Helvetica",Font.PLAIN, 12));
    parent.incProgress();
    
    // Lay out menu bar. 
    JMenuBar menuBar;
    
    menuBar = new JMenuBar();
    setJMenuBar(menuBar);
    
    fileMenu.add(nlcButton);
    fileMenu.add(nrcButton);
    fileMenu.add(dldButton);
    fileMenu.addSeparator();
    fileMenu.add(quitButton);
    
    prefMenu.add(caseCheckBox);
    prefMenu.add(punctuationCheckBox);
    prefMenu.add(advConcButton);
    prefMenu.add(advConcFlagItem);
    prefMenu.addSeparator();
    prefMenu.add(prefButton);

    helpMenu.add(helpButton);
    helpMenu.add(aboutButton);

    menuBar.add(fileMenu);
    menuBar.add(prefMenu);
    menuBar.add(pluginMenu);
    menuBar.add(Box.createHorizontalGlue());
    menuBar.add(helpMenu);

    parent.incProgress();

    /// create  sub-components

    // create keyword box
    JPanel kwd = new JPanel();
    kwd.add( new JLabel("Keyword"));
    keyword = new JTextField(15);
    kwd.add( keyword);
    keyword.setToolTipText("Syntax: word_1[+[[context]]word2...]. E.g. 'seen+before' will find '...never seen before...' etc; 'seen+[2]before' finds the '...seen her before...'");
    kwd.setBorder(BorderFactory.createEtchedBorder());
    concButton = new JButton(DOBUTT);
    kwd.add( concButton );

    // sort panels
    JPanel lsp = new JPanel();
    JPanel rsp = new JPanel();
    lsp.setBorder(BorderFactory.createEtchedBorder());
    rsp.setBorder(BorderFactory.createEtchedBorder());
    for (int i = 1 ; i <= PreferPanel.SCTXMAX ; i++){
      leftSortCtx.addItem(""+i);
      rightSortCtx.addItem(""+i);
    }
    lsp.add(leftSortCtx);
    lsp.add(stlButton);
    
    rsp.add(rightSortCtx);
    rsp.add(strButton);

    leftSortCtx.setEnabled(false);
    rightSortCtx.setEnabled(false);
    stlButton.setEnabled(false);
    strButton.setEnabled(false);
    extractButton.setEnabled(false);
    headerButton.setEnabled(false);
    dldButton.setEnabled(false);
    
    nlcButton.setToolTipText("Select a new corpus index");
    nrcButton.setToolTipText("Select a new corpus index server");
    dldButton.setToolTipText("Save the displayed concordances to disk");
    stlButton.setToolTipText("Sort with left context horizon indicated on the box");
    strButton.setToolTipText("Sort with right context horizon indicated on the box");
    extractButton.setToolTipText("Display text extract of the selected line");
    headerButton.setToolTipText("Display header file of the selected line");
    
    progressBar = new JProgressBar(0, 500);
    progressBar.setValue(0);
    progressBar.setStringPainted(true);
    progressBar.setString("");
    
    //Create a timer for monitoring dwld progress.
    timer = new Timer(ONE_SECOND, new ActionListener() {
        public void actionPerformed(ActionEvent evt) {
          progressBar.setValue(parent.getNoOfConcordancesReadSoFar());
          //progressBar.setString(perct+"% completed");
          if ( parent.getNoOfConcordancesReadSoFar() >= parent.getExpectedNoOfConcordances()
               || !parent.isReceivingFromServer() )
            {
              timer.stop();
              progressBar.setValue(progressBar.getMaximum());
            }
        }
      });
    ucrt_timer = new Timer(300, new ActionListener() {
        public void actionPerformed(ActionEvent evt) {
          progressBar.setValue(srt_i++ % SRTBARMAX);
          //progressBar.setString(perct+"% completed");
          if (parent.gotResponseFromServer() || !parent.isReceivingFromServer())
            {
              ucrt_timer.stop();
              progressBar.setString("Done");
              progressBar.setValue(progressBar.getMaximum());
            }
        }
      });
    srt_timer = new Timer(300, new ActionListener() {
        public void actionPerformed(ActionEvent evt) {
          //Int perct = (Int) (progressBar.getPercentComplete()*100);
          progressBar.setValue(srt_i++ % SRTBARMAX);
          if ( !parent.isSorting() )
            {
              srt_timer.stop();
              progressBar.setString("Done");
              progressBar.setValue(progressBar.getMaximum());
            }
        }
      });
    
    // create status line (bottom of the screen)
    statusArea.setLayout( new FlowLayout(FlowLayout.LEFT));
    statusArea.setLayout(new BorderLayout());
    JPanel statusLeft = new JPanel(new FlowLayout(FlowLayout.LEFT));
    JPanel statusRight = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    
    statusLeft.add(progressBar);    
    statusLeft.add(statusLabel);
    statusRight.add(statusLabelScroll);
    sccsPanel = new SubcorpusCaseStatusPanel(parent);
    statusRight.add(sccsPanel);

    statusArea.add(statusLeft,BorderLayout.WEST);
    statusArea.add(statusRight,BorderLayout.EAST);
    
    // -------- plugins partly disabled for the time being
    pluginMenu.setEnabled(true);
    
    ActionListener dcl = new ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
          if (!WordQuery.isValidQuery(keyword.getText())) {
            alertWindow("Invalid query syntax");
            return;
          }
          sccsPanel.updateStatus();
          parent.requestConcordance(keyword.getText());
        }};
    
    // set up even listening
    concButton.addActionListener(dcl);
    keyword.addActionListener(dcl);

    quitButton.addActionListener(new ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
          parent.quit();
        }}
      );
    prefButton.addActionListener(new ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
          parent.showPreferencesEditor();
        }}
      );
    advConcButton.addActionListener(new ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
          parent.showSubcorpusSelector();
        }}                         
      );
    
    //caseCheckBox.addActionListener();
    caseCheckBox.setState(false);
    punctuationCheckBox.setState(false);
    advConcFlagItem.setState(false);

    advConcFlagItem.addChangeListener(new ChangeListener(){
        public void stateChanged(ChangeEvent e)  {
          parent.setAdvConcFlag(advConcFlagItem.isSelected());
        }}
      );
    stlButton.addActionListener(new ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            //sort other context for arabic
            if(parent.getLanguage() == modnlp.Constants.LANG_AR){

                parent.startSorting(getSortLeftCtxHorizon(),false);

            }
            else{
                  parent.startSorting(getSortLeftCtxHorizon(),true);
            }
             
        
        }}
      );
    strButton.addActionListener(new ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
        
            //sort other context for arabic
            if(parent.getLanguage() == modnlp.Constants.LANG_AR){
                      parent.startSorting(getSortRightCtxHorizon(),true);
            }
            else{
                  parent.startSorting(getSortRightCtxHorizon(),false);
            }
         
        }}
      );
    extractButton.addActionListener(new ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
          ConcordanceObject sel = concListDisplay.getSelectedValue();
          if (sel == null) {
            alertWindow("Please select a concordance!");
          }
          else
            parent.showExtract(sel);
        }}
      );
    headerButton.addActionListener(new ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
          ConcordanceObject sel = concListDisplay.getSelectedValue();
          if (sel == null) {
            alertWindow("Please select a concordance!");
          }
          else
            parent.showHeader(sel);
        }}
      );
    nlcButton.addActionListener(new ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
          parent.chooseNewLocalCorpus();
        }}
      );
    nrcButton.addActionListener(new ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
          parent.chooseNewRemoteCorpus();
        }}
      );
    dldButton.addActionListener(new ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
          try
            {
              JFileChooser filedial = new JFileChooser(keyword.getText()
                                                       +".tec");
              int returnVal = filedial.showDialog(myself, "Save to disk");
              if (returnVal == JFileChooser.APPROVE_OPTION)
                {
                  File file = filedial.getSelectedFile();
                  //System.out.println(file.getName());
                  Download dlf =
                    new Download(file);
                  parent.downloadConcordance(dlf);
                }
            }
          catch (java.io.IOException e){
            alertWindow("Error downloading concordances\n!"+e);
          }}}
      );

    helpButton.addActionListener(new ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
          parent.showHelp();
        }}
      );

    aboutButton.addActionListener(new ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
          parent.showAbout();
        }}
      );
    
    // lay out top toolbox
    contentPane.setLayout(new BorderLayout());
    outArea.setLayout(new BorderLayout());
    concLabel.setLayout(new BoxLayout(concLabel, BoxLayout.X_AXIS));

    concLabel.setFont(new Font("Helvetica", Font.PLAIN, 12));
    concLabel.setForeground(Color.green.darker().darker());
    
    concLabel.add(Box.createGlue());
    concLabel.add( kwd );
    concLabel.add(Box.createGlue());
    concLabel.add(lsp);
    concLabel.add(rsp);
    concLabel.add(Box.createGlue());
    concLabel.add(extractButton);
    concLabel.add(Box.createGlue());
    concLabel.add(headerButton);
    concLabel.add(Box.createGlue());
    
    
    // lay out main interactive area (toolbox + list)
    outArea.add("North",concLabel);
    outArea.add("Center",concListDisplay);
    
    // add status area (status indicator + messages)
    contentPane.add("North",optArea);
    contentPane.add("Center",outArea);
    contentPane.add("South",statusArea);
    
    // no longer needed: ListDisplay will keep track of the number of
    //items it displays.  
    //addComponentListener(concListDisplay);
  }
  //*****
  
  public void resetShowDetailString () {
      concListDisplay.renderer.showDetailString = null;
  }
  public void setShowDetailString (int col, String str) {
      concListDisplay.renderer.showDetailString = str;
      concListDisplay.refresh();
      concListDisplay.setViewToIndex(concListDisplay.getIndexOfDetail( col, str));
  }

  // ok
  public void addPluginMenuItem (final Plugin plugin, String mentry) {
    JMenuItem pmi = new JMenuItem(mentry);
    pluginMenu.add(pmi);
    try {
      pmi.addActionListener(
                            new ActionListener(){
                              public void actionPerformed(ActionEvent event)
                              {
                                plugin.activate();
                              }
                            });
    }
    catch (Exception e) {
      System.err.println("Warning (BrowserFrame): error loading plugin: "+e);
      e.printStackTrace(System.err);
    }
  }

  public void addRemoteCorpusMenuItem (final String server, final int port, String mentry) {
    JMenuItem mi = new JMenuItem(mentry);
    fileMenu.add(mi, 3);
    try {
      mi.addActionListener(
                            new ActionListener(){
                              public void actionPerformed(ActionEvent event)
                              {
                                parent.setRemoteCorpus(server, port);
                              }
                            });
    }
    catch (Exception e) {
      System.err.println("Warning (BrowserFrame): error loading plugin: "+e);
      e.printStackTrace(System.err);
    }
  }
  
    
  // ok
  public void itemStateChanged(ItemEvent e) {
    JMenuItem source = (JMenuItem)(e.getSource());
    String s = "Item event detected.";
  }
  
  public void clickAdvConcFlag(boolean f) {
    if (advConcFlagItem.isSelected() != f)
      advConcFlagItem.doClick();
  }
  // ok
  //Listener method for list selection changes.
  public void valueChanged(ListSelectionEvent e) {
    if (e.getValueIsAdjusting() == false) {
      
      if (concListDisplay.getSelectedIndex() == -1) {
        //No selection: disable header and extract
        extractButton.setEnabled(false);
        headerButton.setEnabled(false);
      } else {
        extractButton.setEnabled(true);
        headerButton.setEnabled(true);
      }
    }
  }

  // ok
  public boolean interruptDownloading()
  {
    int stop =
      JOptionPane.
      showConfirmDialog(this,
                        "Concordance list is not completed. "+
                        "\nInterrupt transfer and continue sorting?",
                        "Interrupt transfer and continue sorting?",
                        JOptionPane.YES_NO_OPTION);
    return stop == 0 ? true : false;
  }
  
  // ok
  public void alertWindow (String msg)
  {
    JOptionPane.showMessageDialog(null,
                                  msg,
                                  "alert",
                                  JOptionPane.ERROR_MESSAGE);
  }  
  

  // ok
  /**
   * Implement ConcordanceDisplayListener method.
   */
  public void concordanceChanged(ConcordanceDisplayEvent e)
  {
    //concListDisplay.revalidate();
    if (e.getEventType() == ConcordanceDisplayEvent.FIRSTDISPLAY_EVT){
      concListDisplay.redisplayConc();
      //***************
        if (!clicked.equalsIgnoreCase("")) {
             setShowDetailString(column,clicked);
             clicked="";
        }
           
       
      
      //concListDisplay.setCellPrototype(parent.getConcordanceVector().get(0));
    }
    if (e.getEventType() == ConcordanceDisplayEvent.DOWNLOADSTATUS_EVT){
      updateStatusLabel(e.getMessage());
      return;
    }
    if (e.getEventType() == ConcordanceDisplayEvent.DOWNLOADCOMPLETE_EVT){
      updateStatusLabel(e.getMessage());
      updateStatusLabel(e.getMessage());
      return;
    }

    //System.out.println("Displaying "+e.getFirstIndex() );		
    try {
      updateStatusLabel(e.getMessage());
      if ( concListDisplay != null && concListDisplay.list != null)
        concListDisplay.list.clearSelection();
      timer.start();
      progressBar.setString(null);
      progressBar.setMaximum(parent.getExpectedNoOfConcordances());
      strButton.setEnabled(true);
      stlButton.setEnabled(true);
      leftSortCtx.setEnabled(true);
      rightSortCtx.setEnabled(true);
      extractButton.setEnabled(false);
      headerButton.setEnabled(false);
      dldButton.setEnabled(true);
    }
    catch (NumberFormatException ex){
      labelMessage("Number format exception: Server may be down. ");
      //concThread.stop();
    }
    catch (NullPointerException ex){
      labelMessage("Error caught: Server may be down. ");
      //concThread.stop();
    }
  }


  //ok
  /**
   * Implement ConcordanceDisplayListener method.
   */
  public void concordanceChanged(ConcordanceListSizeEvent e)
  {
    concListDisplay.redisplayConc();
    updateStatusLabelScroll("Showing "+e.getNoFound()+" lines. ");
  }

  public void progressBarUnknownStart(String msg){
    progressBar.setMaximum(SRTBARMAX-1);
    ucrt_timer.start();
    progressBar.setString(msg);
  }


  // ok
  public void updateStatusLabel (String msg){
    if (debug)
      System.err.println(msg);
    if (msg != null)
      statusLabel.setText(msg);
  }

  // ok
  public void updateStatusLabelScroll (String msg){
    statusLabelScroll.setText(msg);
  }

  // ok
  /** Display a message in this window's user message area.*/
  public void labelMessage (String msg)
  {
    updateStatusLabel(msg);
    concLabel.validate();
    concLabel.repaint();
  }

  // ok
  public void clearMessageArea() {
    updateStatusLabel("");
    updateStatusLabelScroll("");
  }

  // ok
  // The TEC default change interface
  public void defaultChanged(DefaultChangeEvent e)
  {
  }
  
  //ok
  public void defaultChanged(SortHorizonChangeEvent e)
  {
    leftSortCtx.setSelectedItem(""+e.getNewHorizon());
    rightSortCtx.setSelectedItem(""+e.getNewHorizon());
  }

  //  ok 
  public void defaultChanged(FontSizeChangeEvent e) {
    concListDisplay.defaultChanged(e);
  }
  
  // ok
  public int getListFontSize()  {
    return concListDisplay.getFontSize();
  }

  // ok
  public int getPreferredFontSize()  {
    return parent.getPreferredFontSize();
  }

  // ok
  public void setListFontSize(int s)  {
    concListDisplay.setFontSize(s);
  }
  
  // ok
  public int getSortRightCtxHorizon(){
    return (new Integer((String)rightSortCtx.getSelectedItem())).intValue();
  }

  // ok
  public int getSortLeftCtxHorizon(){
    return (new Integer((String)leftSortCtx.getSelectedItem())).intValue();
  }

  public boolean getCase(){
    return caseCheckBox.getState();
  }

  public boolean getPunctuation(){
    return punctuationCheckBox.getState();
  }

  public ConcordanceListModel getListModel(){
    return (ConcordanceListModel)concListDisplay.getListModel();
  }

    @Override
    public int getLanguage() {
        return parent.getLanguage();
    }

}

