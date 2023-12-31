/**
 *   Copyright (c) 2008-2019 S Luz <luzs@acm.org>. All Rights Reserved.
 *
 *   This program  is free software; you can redistribute it and/or
 *   modify it under the terms of the GNU General Public License
 *   as published by the Free Software Foundation; either version 2
 *   of the License, or (your option) any later version.
 *   
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
     
 *   You should have received a copy of the GNU General Public License
 *   along with this program; if not, write to the Free Software
 *   Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 **/ 
package modnlp.tec.client;

import modnlp.Constants;
import modnlp.tec.client.gui.RemoteCorpusChooser;
import modnlp.tec.client.gui.SplashScreen;
import modnlp.tec.client.gui.BrowserFrame;
import modnlp.tec.client.gui.BrowserGUI;
import modnlp.tec.client.gui.PreferPanel;
import modnlp.tec.client.gui.*;
import modnlp.idx.database.Dictionary;
import modnlp.idx.database.DictProperties;
import modnlp.idx.headers.HeaderDBManager;
import modnlp.idx.gui.CorpusChooser;
import modnlp.util.IOUtil;
import java.util.Comparator;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.StringTokenizer;
import java.io.FileInputStream;
import java.io.IOException;
import javax.swing.JOptionPane;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.io.File;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JMenu;
import modnlp.tec.client.gui.event.ConcordanceDisplayEvent;
import modnlp.tec.client.gui.event.ConcordanceDisplayListener;
import modnlp.tec.client.gui.event.ConcordanceListSizeEvent;

/**
 *  Display concordances etc: interact with
 *  <a href="../server/index.html">TEC Servers</a> or work
 *  stand-alone accessing the corpus index through <a
 *  href="../../idx/database/Dictionary.html">Dictionary.java</a>
 *
 * 
 * @author  S Luz &#60;luzs@acm.org&#62;
 * @version <font size=-1>$Id: $</font>
 * @see  
*/
public class Browser
  implements ConcordanceBrowser , ConcordanceDisplayListener 
{

  // constants
  public static final String RELEASE = "0.9.7";
  public static final String REVISION = "$Revision: 1.9 $";
  String BRANDNAME = "MODNLP/T";
  private static final String PLGLIST = "teclipluginlist.txt";
  private static final boolean debug = true;

  private String defaultIcon = "modnlp/tec/client/icons/modnlp-small.jpg";
  // properties, state
  private boolean standAlone = true;
  private boolean commandLineServer  = false;
  private boolean advConcFlag = false;
  private boolean firstRemoteFlag = true;
  private boolean initialCorpusSelection = true;
  /** Deafult location of TecServer  */
  private String remoteServer;
  /** Default port */
  private int remotePort = 1240;
  /** Deafult URL where to find header files */
  private String remoteWebcli;
  private String headerBaseURL;
  private String headerExt = "hed";
  private String keywordString;
  private String encoding = null;
  private String xquerywhere = null;
  private int language = Constants.LANG_EN;
  private String subcorpusName ="";

  // GUI
  private SplashScreen splashScreen;
  private BrowserFrame browserFrame;
  private PreferPanel preferenceFrame;
  private GraphicalSubcorpusSaver guiSubcorpusSaver = null;

  private ClientProperties clProperties;


  // DBs
  private Dictionary dictionary = null;
  private HeaderDBManager hdbmanager = null;

  // threads
  private SortThread sortThread = null;
  private ConcordanceThread concThread = null;
  private ConcordanceProducer concordanceProducer = null;
  private HeaderProducer headerProducer = null;
  public HashMap<String, String> headermap = new HashMap<String, String>();

  // text data 
  private ConcordanceVector concVector = new ConcordanceVector();
  //***
  List<StateChanged> listeners = new ArrayList<StateChanged>();
  public void addChangeListener(StateChanged toAdd){
    listeners.add(toAdd);
  }
  public Browser (String sp) {
    standAlone = false;
    commandLineServer = true;
    clProperties = new ClientProperties();
    preferenceFrame = new PreferPanel(this);
    browserFrame = new BrowserFrame(0, 0, this);
    preferenceFrame.addDefaultChangeListener(browserFrame);
    String serv = sp.substring(0,sp.indexOf(':'));
    int port = (new Integer(sp.substring(sp.indexOf(':')+1))).intValue();
    setRemoteCorpus(serv,port);
    init();
  }

  public Browser (boolean sa) {
    standAlone = sa;
    commandLineServer = false;
    clProperties = new ClientProperties();
    preferenceFrame = new PreferPanel(this);
    browserFrame = new BrowserFrame(0, 0, this);
    preferenceFrame.addDefaultChangeListener(browserFrame);
    init();
  }

  private final void init(){
    //System.err.println("BRAND"+System.getProperty("jnlp.browser.brand"));
    if (clProperties.getProperty("browser.brand") != null) {
      setBrand(clProperties.getProperty("browser.brand"));
      System.err.println("BRAND"+clProperties.getProperty("browser.brand"));
    }
    if (clProperties.getProperty("browser.icon") != null) {
      defaultIcon = clProperties.getProperty("browser.icon");
      System.err.println("ICON"+clProperties.getProperty("browser.icon"));
    }

    browserFrame.addWindowListener(new WindowAdapter() {
        public void windowClosing(WindowEvent e) {
          quit();
        }
      });
    if ( !commandLineServer )
      if ( isStandAlone() )
        chooseNewLocalCorpus();
      else
        initialCorpusSelection();
    
    splashScreen = new SplashScreen("Initialising. Please wait...", 20,
                                    defaultIcon);
    incProgress();    

    browserFrame.initGUI();
    incProgress();

    addRemoteCorpusMenu();
    loadPlugins();
    incProgress();

      
    //headerBaseURL = "http://"+remoteServer+"/tec/headers";
    //if ( clProperties.getProperty("tec.client.headers") != null )
    //  headerBaseURL = clProperties.getProperty("tec.client.headers");
    //if ( clProperties.getProperty("tec.client.port") != null )
    //  remotePort = new Integer(clProperties.getProperty("tec.client.port")).intValue();
    //f.setAdvSearchOptions();
    browserFrame.pack();
    incProgress();
    splashScreen.dismiss();
    browserFrame.setVisible(true);
  }

  public void quit(){
    System.err.println("BYE...");

    // stop all threada
    if (concThread != null)
      concThread.stop();
    if (sortThread != null)
      sortThread.stop();
    
     if (guiSubcorpusSaver != null)
      stopSubCorpusSaverGUI();
     
    // close all DBs
    if (dictionary != null)
      dictionary.close();
    if (hdbmanager != null)
      hdbmanager.close();
    System.exit(0);
  }


  public ConcordanceThread getConcordanceThread(){
    return concThread;
  }

  public ConcordanceVector getConcordanceVector(){
    return concVector;
  }

  public String getKeywordString(){
    return keywordString;
  }

  public void setStandAlone(boolean b){
    standAlone = b;
  }

  public boolean isStandAlone() {
    return standAlone;
  }

  public void incProgress(){
    splashScreen.incProgress();
  }

  public void dismissProgress(){
    splashScreen.incProgress();
  }

  private void addRemoteCorpusMenu () {
    String m = clProperties.getProperty("remote.corpora");
    if (m == null){
      System.err.println("addRemoteCorpusMenu: no remote corpus spec found in tecli.properties.");
      return;
    }
    StringTokenizer st = new StringTokenizer(m, ";");
    System.err.println("addRemoteCorpusMenu: setting remote corpus menu: "+ st);
    while (st.hasMoreTokens()){
      String s = st.nextToken();
      StringTokenizer st2 = new StringTokenizer(s, "\t");
      if (st2.countTokens() != 2){
        System.err.println("addRemoteCorpusMenu: wrong corpus spec in tecli.properties: "+ s);
      }
      else{
        String menudesc = st2.nextToken();
        String webcli_server = st2.nextToken();
        //int port = new Integer(st2.nextToken()).intValue();
        browserFrame.addRemoteCorpusMenuItem(webcli_server, menudesc);
      }
    }
  }
    
  private void loadPlugins () {
    splashScreen.setMessage("Loading plugins...");
    splashScreen.incProgress();
    ClassLoader cl = this.getClass().getClassLoader();
    BufferedReader in
      = new BufferedReader(new 
                           InputStreamReader(cl.getResourceAsStream(PLGLIST)));
    String plg = null;
    try {
      while ( (plg = in.readLine() ) != null ){
        try {
          System.err.println("Loading: " + plg);
          StringTokenizer st = new StringTokenizer(plg, ":");
          // first token: class name (ignore for now)
          final Plugin tp = (Plugin)IOUtil.loadPlugin(st.nextToken(),cl);
          tp.setParent(this);
          browserFrame.addPluginMenuItem(tp, st.nextToken());
          splashScreen.incProgress();
        }
        catch (ClassNotFoundException e) {
          System.err.println("Warning (Browser): error loading plugin: "+e);
          e.printStackTrace(System.err);
        }
      }
    }
    catch (Exception e) {
      e.printStackTrace(System.err);
    }
  }

  public void loadConcordance(BufferedReader input, String query){
    keywordString = query;
    if (sortThread != null)
      sortThread.stop();
    browserFrame.labelMessage("Load concordance list. Please wait...");
    browserFrame.updateStatusLabelScroll("");
    if ( (concThread != null) ) {
      concThread.stop();
    }
    concVector.clear();
    concVector.setHalfConcordance(preferenceFrame.getContextSize());
    concVector.setSortContextHorizon(0);
    concVector.setBrowserQuery(query);
    concThread = 
      new ConcordanceThread(concVector, 
                            input,
                            null);
    concThread.start();
    //--??--concThread.addConcordanceDisplayListener(concVector);
    //SwingUtilities.invokeLater(concThread);
    //concList = new ListDisplay(this, concThread.conc);
    //currentIndex = 0;
    concThread.addConcordanceDisplayListener(browserFrame);
    concThread.addConcordanceDisplayListener(this);
    browserFrame.progressBarUnknownStart("Loading... ");
  }


  public void requestConcordance(String query){
    keywordString = query;
    if (sortThread != null)
      sortThread.stop();
    browserFrame.labelMessage("Building concordance list. Please wait...");
    browserFrame.updateStatusLabelScroll("");
    TecClientRequest request = new TecClientRequest();
    request.put("keyword",keywordString);
    request.put("context",preferenceFrame.getContextSize());
    request.put("sgml",preferenceFrame.getSGMLFlag());
    String casestate = browserFrame.getCase()? "sensitive" : "insensitive";
    request.put("case",casestate);
    request.put("request","concord");
    if ( subCorpusSelected() )
      request.put("xquerywhere",xquerywhere);
    if ( (concThread != null) ) {
      concThread.stop();
    }
    concVector.clear();
    concVector.setHalfConcordance(request.getContextSize());
    concVector.setSortContextHorizon(0);
    concVector.setBrowserQuery(query);
    //concList.removeAll();
    //concList.reset();
    if (standAlone) {
      concThread = 
        new ConcordanceThread(concVector,
                              concordanceProducer.getBufferedReader(), 
                              request);
           
      concThread.start();
      concordanceProducer.setRequest(request);
      concordanceProducer.start();
    }
    else {
      request.setServerURL(remoteWebcli);
      request.setServerPORT(remotePort);
      request.setServerProgramPath("/concordancer");
      concThread = new ConcordanceThread(concVector, request);
      concThread.setEncoding(encoding);
      //****
      
      concThread.start();
    }
    //--??--concThread.addConcordanceDisplayListener(concVector);
    //SwingUtilities.invokeLater(concThread);
    //concList = new ListDisplay(this, concThread.conc);
    //currentIndex = 0;
    concThread.addConcordanceDisplayListener(browserFrame);
    concThread.addConcordanceDisplayListener(this);
    browserFrame.progressBarUnknownStart("Searching... ");
  }
  
  @Override
    public void showSubcorpusSaveSelector() {
        guiSubcorpusSaver.activate();
    }
   

  
  //****highlight context
  public void showContext(int col, String str){
       boolean isleft = true;
       if(col>4)
           isleft=false;
       browserFrame.clicked = str;
       browserFrame.column = Math.abs(5-(col+1));
       startSorting(browserFrame.column,isleft);
      
      browserFrame.setShowDetailString(col,str);
       
  }

  public void startSorting(int horizon, boolean sortleft){
    Comparator cprer ;
    ///******* reset show detail
    browserFrame.resetShowDetailString();
    if ( concThread.atWork()  ) {
      if ( ! browserFrame.interruptDownloading() )
        return;
      concThread.stop();
    }
    if (sortThread != null){
      sortThread.stop();
    }
    int sortContextHorizon = browserFrame.getSortLeftCtxHorizon();
    if(language == modnlp.Constants.LANG_AR){
        sortContextHorizon = browserFrame.getSortRightCtxHorizon();
    }
    if(horizon == -111){
         cprer = new FilenameComparer();
    }
    else{
        if (sortleft){ 
            //*****
            if (browserFrame.clicked.equalsIgnoreCase("")) {
                concVector.setSortContextHorizon(0-horizon);
            }
            else{
                concVector.setSortContextHorizon(0-browserFrame.column);
            }

        }
        else {
            if (browserFrame.clicked.equalsIgnoreCase("")) {
                sortContextHorizon = browserFrame.getSortRightCtxHorizon();
                if(language == modnlp.Constants.LANG_AR){
                    sortContextHorizon = browserFrame.getSortLeftCtxHorizon();
                }
                concVector.setSortContextHorizon(0+horizon);
            }
            else{
                concVector.setSortContextHorizon(0+browserFrame.column);
            }
        }
    //which context are we comparing
         cprer = sortleft ?
          new LeftComparer(sortContextHorizon, 
                           preferenceFrame.maxContext/2, 
                           browserFrame.getPunctuation()) :
          new RightComparer(sortContextHorizon, 
                            preferenceFrame.maxContext/2, 
                            browserFrame.getPunctuation());

    }
    sortThread = new SortThread(concVector, cprer);
    
    sortThread.addConcordanceDisplayListener(browserFrame);
    sortThread.start();
 
    browserFrame.progressBarUnknownStart("Sorting... ");
    browserFrame.labelMessage("Sorting with context horizon "
                              +sortContextHorizon+(sortleft?" (left)":" (right)")); 
    
    
  }

  public void showPreferencesEditor(){
    preferenceFrame.setSize(400,300);
    preferenceFrame.show(); 
  }

  public void showHelp()
  {
    HelpBrowser hb = new HelpBrowser();
    if ( hb.ok() )
      hb.show();
  }
  public void showAbout()
  {
    HelpBrowser hb = new HelpBrowser("modnlp/tec/client/help/about.html", "About...");
  if ( hb.ok() )
    hb.show();
  }

  public void showErrorMessage(String e){
    JOptionPane.showMessageDialog(null, e, "ERROR", JOptionPane.ERROR_MESSAGE);
  }

  /** Show extract of text identified by position <code>sel</code>
   * in the current <code>ConcordanceVector</code>
   *  @see ConcordanceVector
   */
  public void showExtract(ConcordanceObject sel)
  {
    String filename = sel.filename;
    long filepos = sel.filepos;
    if ( filepos < 0 ) {
      System.err.println("remoteServer ERROR: invalid position "+filepos);
    }
    else {
      TecClientRequest request = new TecClientRequest();
      request.put("filename",filename);
      request.put("context",preferenceFrame.getExtractContextSize());
      request.put("sgml",preferenceFrame.getSGMLFlag());
      request.put("position",filepos);
      request.put("request","extract");
      request.put("keyword",keywordString);
      request.setServerURL(remoteWebcli);
      //request.setServerPORT(remotePort);
      request.setServerProgramPath("/extractor");
      //if (concThread != null && concThread.isAlive() ){
      //concThread.stop();
      //}
      ContextClient tecClient = null;
      if (standAlone)
        tecClient = new ContextClient(request, dictionary);
      else
        tecClient = new ContextClient(request);
      tecClient.setVisible(true); // a bug in jdk 1.5 won't let it
                                  // show the frame unless it is
                                  // resized *after* it is made
                                  // visible, hence the call to start() below.
      tecClient.start();
      preferenceFrame.addDefaultChangeListener(tecClient);
    }
  }

  /** Show header file of text identified by position <code>sel</code>
   *  in the current <code>ConcordanceVector</code>
   *  @see ConcordanceVector
   */
  public void showHeader(ConcordanceObject sel)
  {
    String filename = sel.filename;
    //System.err.println("section="+sel.sectionID);
    //System.err.println("fn="+filename);
    String sep;
    if (headerBaseURL.startsWith("http://") || 
        headerBaseURL.startsWith("https://"))
      sep = "/";
    else
      sep = System.getProperty("file.separator"); //java.io.File.separator;
    String headerName =  //   (new File(filename)).getName();
      filename.substring(filename.lastIndexOf(sep)+1,filename.lastIndexOf('.'))
      +"."+headerExt;
    //System.err.println("-------\nfn="+filename+"\nheader="+headerName+"\nheaderBaseURL="+headerBaseURL+"\nsep="+sep);
    showHeader(headerName, sel.sectionID);
  }

 public void showHeader(String headerName,String secString)
  {
    //System.err.println("-"+headerBaseURL+"-"+headerName);
    int windowHeight = 600;
    int windowWidth = 500;
    String sep = System.getProperty("file.separator"); //java.io.File.separator;
    if (headerBaseURL.startsWith("http://") || 
        headerBaseURL.startsWith("https://") ||
        headerBaseURL.startsWith("file://"))
      sep = "/";
    //    else
    //  headerBaseURL = "file://"+headerBaseURL;
    String img = headerBaseURL+sep+headerName.substring(0,headerName.indexOf('.'))+".jpg";
    
    StringBuffer content = new StringBuffer();
    //System.err.println("URL--:"+headerBaseURL+sep+headerName);
    //HeaderReader header = new HeaderReader(headerBaseURL+headerName);
    //HeaderXMLHandler parser =  new HeaderXMLHandler();
    try {
      InputStream is = null;
      URL headerURL = null;

      if (standAlone) {
        //headerURL = new URL(headerBaseURL+sep+headerName);
        //is = headerURL.openConnection().getInputStream();
        is = new FileInputStream(headerBaseURL+sep+headerName);
        img = "file://"+img;
      }
      else {
        headerURL = new URL(headerBaseURL+sep+headerName);
        is = headerURL.openConnection().getInputStream();
      }
      //InputSource source = new InputSource(in);
      //source.setEncoding(encoding);
      if (preferenceFrame.isShowingSGMLFlag()){
        BufferedReader in = 
          new BufferedReader(new InputStreamReader(is,"UTF-8"));
        String tmp;
        while ( (tmp = in.readLine()) != null )
          content.append(tmp+"\n");
      }
      else {
        HeaderXMLHandler parser =  new HeaderXMLHandler(secString);
        String hburl = headerBaseURL.matches("(file|http|https)://.*") ? headerBaseURL : "file://"+headerBaseURL ;

        parser.setImgBase(hburl);
        parser.parse(is);
        content = new StringBuffer("<html><body style='width=100%;'"+parser.getSectionHighlightedContent()+"</body></html>");
        //content = new StringBuffer("<html><img src='"+img+"' height=183 width=128 alt='Book Cover'><pre>"+parser.getContent()+"</pre></html>");
        //System.err.println(content+"\n----------\n"+parser.getContent());
      }
    }
    catch (Exception e) {
      System.err.println("Error retrieving metadata: "+e);
      content.append("\nError retrieving metadata: "+e);
      e.printStackTrace(System.err);
    }
    // HeaderClass header = new HeaderClass(filename);
    FullTextWindow window =  new FullTextWindow(headerName,
                                                content);
    preferenceFrame.addDefaultChangeListener(window);
    
    window.setVisible(true); // a bug in jdk 1.5 won't let it show the
                             // frame unless it is resized *after* it
                             // is made visible
    window.setSize(windowWidth, windowHeight);
  }
  
  public void downloadConcordance(Download dlf) throws java.io.IOException {
    dlf.dumpConcordance(getConcordanceVector());
  }

  public int getPreferredFontSize (){
    return preferenceFrame.getFontSize();
  }

  public void setAdvConcFlag (boolean f){
    browserFrame.clickAdvConcFlag(f);
    advConcFlag = f; 
    if (!f)
        browserFrame.setTitle(browserFrame.getTitle().split(" Sub-corpus:")[0]);
    else
        browserFrame.setTitle(browserFrame.getTitle().split(" Sub-corpus:")[0]+" Sub-corpus: " + subcorpusName);

  }

  public boolean isSubCorpusSelectionON (){
    return advConcFlag;
  }
  
  public boolean subCorpusSelected() {
    if (advConcFlag && xquerywhere != null )
      return true;
    else
      return false;
  }
  
  public void chooseNewLocalCorpus(){
    CorpusChooser ncc = new CorpusChooser(clProperties.getProperty("last.index.dir"));
    int r;
    while (!((r = ncc.showChooseCorpus()) == CorpusChooser.APPROVE_OPTION ||
             (r != CorpusChooser.CANCEL_OPTION )) ) 
      {
        JOptionPane.showMessageDialog(null, "Please choose a corpus directory (folder)");      
      }
    if (r == CorpusChooser.CANCEL_OPTION)
      return;
    String cdir = ncc.getSelectedFile().toString();
    setLocalCorpus(cdir);
    browserFrame.setDirectionality();
  }

  public int getLanguage(){
    return language;
  }

  public void setLocalCorpus (String cdir) {
    DictProperties dictProps = new DictProperties(cdir);
    if (dictionary != null)
      dictionary.close();
    dictionary = new Dictionary(false,dictProps);
    clProperties.setProperty("last.index.dir", cdir);
    standAlone = true;
    clProperties.setProperty("stand.alone","yes");
    browserFrame.setTitle(getBrowserName()+": index at "+cdir);
    dictionary.setVerbose(debug);
    
    if ( dictProps.indexHeaders() ) {
      setLocalHeadersDirectory(dictProps);
      browserFrame.subcorpusMenuSetEnabled(true);
      browserFrame.headerButtonSetActive(true);
    }
    else {
      browserFrame.subcorpusMenuSetEnabled(false);
      browserFrame.headerButtonSetActive(false);
    }
    encoding = dictProps.getProperty("file.encoding");
    language = dictProps.getLanguage();
    headerExt = dictProps.getProperty("header.extension");
    if (guiSubcorpusSaver != null)
      stopSubCorpusSaverGUI();
    guiSubcorpusSaver = null;
    System.gc();
    try {
      hdbmanager = new HeaderDBManager(dictProps);
    }
    catch (Exception e) {
      JOptionPane.showMessageDialog(browserFrame, "Header DB error: Subcorpus selection disabled",
                                    "ERROR", JOptionPane.ERROR_MESSAGE);
      System.err.println("Browser: error opening header DB: " + e);
      e.printStackTrace(System.err);
    }
    concVector.setLanguage(language);
    concordanceProducer = new ConcordanceProducer(dictionary);
    headerProducer = new HeaderProducer(dictionary);
    browserFrame.setDirectionality();
    if (dictProps.indexHeaders())
      guiSubcorpusSaver = new GraphicalSubcorpusSaver(this);
    if (!initialCorpusSelection)
        browserFrame.loadRecentMenu();
    initialCorpusSelection = false;
  }

  private void setLocalHeadersDirectory(DictProperties dictProps){
    int r;
    String hh = null;
    if ((hh = dictProps.getProperty("headers.home")) == null)  // an unsafe default
      {
        CorpusChooser ncc = new CorpusChooser(null);
        while (!((r = ncc.showChooseDir("Choose a headers directory")) == CorpusChooser.APPROVE_OPTION ||
                 r != CorpusChooser.CANCEL_OPTION)  ) 
          {
            JOptionPane.showMessageDialog(null, "Please choose a headers directory (folder)");      
          }
        if (r == CorpusChooser.CANCEL_OPTION){
          String cdir = clProperties.getProperty("last.index.dir");
          hh = cdir.substring(0,cdir.lastIndexOf('/', cdir.length()-1))+"/headers/";
        }
        else
          hh = ncc.getSelectedFile().toString();
        dictProps.setProperty("headers.home", hh);
        dictProps.save();
      }
    headerBaseURL = hh.replaceFirst("[\\\\/]:", ":");
    //System.err.println("\n===hh="+hh+"\nheaderBaseURL="+headerBaseURL);
    clProperties.setProperty("tec.client.headers", headerBaseURL);
    preferenceFrame.setHeaderBaseURL(headerBaseURL);
  }

  public void chooseNewRemoteCorpus () {
    // default corpus location
    String dcl = clProperties.getCorpusURL();
    RemoteCorpusChooser rcc = 
      new RemoteCorpusChooser(browserFrame, dcl);
    int r;
    if ((r = rcc.showChooseCorpus()) == RemoteCorpusChooser.CANCEL_OPTION)
      return;
    if ( !firstRemoteFlag && r == RemoteCorpusChooser.SAME_IP)
      return;
    else
      firstRemoteFlag = false;

    //String s = rcc.getServer();
    //int p =  rcc.getPort();
    setRemoteCorpus(rcc.getFQDN());
  }
  
  public void setRemoteCorpus(String s, int p){
    remoteServer = s;
    remotePort = p;
    remoteWebcli = "http://"+s+":"+p;
    standAlone = false;
    TecClientRequest request = new TecClientRequest();
    request.setServerURL("http://"+remoteServer);
    request.setServerPORT(remotePort);
    setRemoteCorpus(request);
  }
  
  public void setRemoteCorpus(String url){
    remoteServer = TecClientRequest.getServerFQDN(url);
    remotePort = TecClientRequest.getServerURLPort(url);
    remoteWebcli = url;
    standAlone = false;
    TecClientRequest request = new TecClientRequest();
    request.setServerURL(url);
    request.setServerPORT(-1);
    setRemoteCorpus(request);
  }

  private void setRemoteCorpus(TecClientRequest request) {   
    request.put("request","headerbaseurl");
    request.setServerProgramPath("/headerbaseurl");
    try {
      if (dictionary != null)
        dictionary.close();
      if (guiSubcorpusSaver != null)
        stopSubCorpusSaverGUI();

      guiSubcorpusSaver = new GraphicalSubcorpusSaver(this);
      URL exturl = new URL(request.toString());
      HttpURLConnection exturlConnection = (HttpURLConnection) exturl.openConnection();
      //exturlConnection.setUseCaches(false);
      exturlConnection.setRequestMethod("GET");
      BufferedReader input = new
        BufferedReader(new
                       InputStreamReader(exturlConnection.getInputStream() ));
      headerBaseURL = input.readLine();
      System.err.println("headerBaseURL=>>>>"+headerBaseURL);
      if (headerBaseURL == null || headerBaseURL.equals(""))
        headerBaseURL = "http://"+remoteServer+"/tec/headers";
      preferenceFrame.setHeaderBaseURL(headerBaseURL);
      encoding = input.readLine();
      String lg = input.readLine();
       System.err.println("language (read)=>>>>"+lg);
      if (lg == null || lg.length() == 0) 
        language = Constants.LANG_EN;
      else
        language = (new Integer(lg)).intValue();
      concVector.setLanguage(language);
      lg = input.readLine();
      if (lg != null && lg.length() > 0) 
        headerExt = lg;
      if (encoding == null || encoding.equals("UTF8")){
        encoding = "UTF-8";
        System.err.println("set encoding to "+encoding);
      }
      browserFrame.subcorpusMenuSetEnabled(true);
      browserFrame.headerButtonSetActive(true);
      System.err.println("language=>>>>"+language);
      System.err.println("encoding=>>>>"+encoding);
      System.err.println("ext=>>>>"+headerExt);
      clProperties.setProperty("tec.client.server",remoteServer);
      clProperties.setProperty("tec.client.port",remotePort+"");
      clProperties.setProperty("stand.alone","no");
      clProperties.save();
      concordanceProducer = null;
      browserFrame.setDirectionality();
      if (!initialCorpusSelection)
        browserFrame.loadRecentMenu();
    initialCorpusSelection = false;
    }
    catch(IOException e)
      {   
        if (guiSubcorpusSaver != null)
          stopSubCorpusSaverGUI();
        showErrorMessage("Error: couldn't create URL input stream: "+e);
        System.err.println("Exception: couldn't create URL input stream: "+e);
        headerBaseURL = "http://"+remoteServer+"/tec/headers";
        System.err.println("Setting URL to "+headerBaseURL);
        language = Constants.LANG_EN;
        preferenceFrame.setHeaderBaseURL(headerBaseURL);
      }
    if (guiSubcorpusSaver.hasNetworkError())
      showErrorMessage("Error: couldn't select new remote corpus.");
    else
      browserFrame.setTitle(getBrowserName()+": index at "+remoteWebcli);

  }
  
  public boolean workOffline() {
    int option;
    if ((clProperties.getProperty("stand.alone")).equals("yes"))
      return true;
    if (JOptionPane.showConfirmDialog(browserFrame,
                                      "Work offline (stand-alone corpus)?",
                                      "Work offline (stand-alone corpus)?",
                                      JOptionPane.YES_NO_OPTION) 
        == JOptionPane.YES_OPTION)
      {
        clProperties.setProperty("stand.alone","yes");
        return true;
      }
    else 
      {
        clProperties.setProperty("stand.alone","no");
        return false;
      }
  }

  public void initialCorpusSelection() {
    int option = -1;
    String sal = clProperties.getProperty("stand.alone");
    
    String lc = (sal != null && sal.equals("yes")) ?
      clProperties.getProperty("last.index.dir") :
      clProperties.getCorpusURL();
    
    String [] opts = {"Use last corpus", "Choose new remote corpus", "Choose new local corpus"};
    JPanel pl = new JPanel();
    pl.setLayout(new BorderLayout());
    pl.add(new JLabel("The corpus you used last time was "+lc), BorderLayout.NORTH);
    pl.add(new JLabel("What would you like to do?"), BorderLayout.SOUTH);
    String op = 
      (String )JOptionPane.showInputDialog(browserFrame,
                                           pl,
                                           "Corpus selection",
                                           JOptionPane.QUESTION_MESSAGE,
                                           null,
                                           opts,
                                           "Use last corpus");
    for (int i = 0; i < opts.length; i++) {
      if (opts[i].equals(op)) {
        option = i;
        break;
      }
    }
    switch (option)
      {
      case 0:
         if (sal != null && sal.equals("yes"))
           setLocalCorpus(clProperties.getProperty("last.index.dir"));
         else
           if (clProperties.isWebcli()){
             clProperties.setServerPortAndPath(clProperties.getProperty("tec.webcli.server"));
             setRemoteCorpus(clProperties.getCorpusURL());
           }
           else
             setRemoteCorpus(clProperties.getProperty("tec.client.server"),
                             (new Integer(clProperties.getProperty("tec.client.port"))).intValue());
         break;
      case 1:
        chooseNewRemoteCorpus();
        break;
      case 2:
        chooseNewLocalCorpus();
        break;
      default:
        browserFrame.setTitle(getBrowserName()+": No index selected");
        break;
      }
    System.err.println("Saving Client properties "+clProperties);
    clProperties.save();
  }
 
  private void stopSubCorpusSaverGUI(){
    if (hdbmanager != null){
      hdbmanager.finalize();
      hdbmanager = null;
    }
    guiSubcorpusSaver.dispose();
  }

  public final void setXQueryWhere(String w){
    xquerywhere = w;
  }
  
  public final String getXQueryWhere(){
    return xquerywhere;
  }

  public final int getRemotePort(){
    return remotePort;
  }

  public final String getRemoteServer(){
    return remoteServer;
  }

  public final String getRemoteWebcli(){
    return remoteWebcli;
  }

  public final String getRemoteWebcliEnc(){
    try {
      return URLEncoder.encode(remoteWebcli, "UTF-8");
    }
    catch (Exception e){
      System.err.println(e+"Error encoding "+remoteWebcli+
                         "\n  UTF-8 not supported; Returning remoteServer ("+remoteServer+")");
      e.printStackTrace();
      return(remoteServer);
    }
  }
  
  public final Dictionary getDictionary(){
    return dictionary;
  }
  
  public final HashMap< String, String> getHeaderMap(){
    return headermap;
  }

  public final ClientProperties getClientProperties(){
    return clProperties;
  }

  public final HeaderDBManager getHeaderDBManager(){
    return hdbmanager;
  }
  
  public final HeaderProducer getHeaderProducer(){
    return headerProducer;
  }
  
  public final String getHeaderBaseUrl(){
    return headerBaseURL;
  }
  public final int getExpectedNoOfConcordances(){
    return concThread.getNoFound();
  }

  public final int getNoOfConcordancesReadSoFar(){
    return concThread.getNoRead();
  }

  public final boolean gotResponseFromServer(){
    return concThread.getServerResponded();
  }

  public final boolean isReceivingFromServer(){
    return concThread.atWork();
  }

  public final boolean isCaseSensitive(){
    return browserFrame.getCase();
  }

  public final boolean isSorting(){
    return (sortThread.atWork());
  }

  public final void setBrand(String b){
    BRANDNAME = b;
  }

  public final String getBrand(){
    return BRANDNAME;
  }
  
 public final String getEncoding(){
    return encoding;
  }
 
 public final boolean getPunctuation(){
    return browserFrame.getPunctuation();
  }

  // ok
  public final String getRelease (){
    return RELEASE;
  }
  
  // ok
  public final String getVersion (){
    return REVISION.substring(11,REVISION.lastIndexOf("$"));
  }
  
  // ok
  public String getBrowserName (){
    return getBrand()+" Concordance Browser (v. "+getRelease()+")";
  }

  public BrowserGUI getBrowserGUI(){
    return browserFrame;
  }

  public static void main(String[] args) {
    try {
      final Browser b;
      if (args.length > 0)
        if ( args[0].equals("-standalone") )
          b = new Browser(true);
        else
          b = new Browser(args[0]);
      else
        b = new Browser(false);
      b.browserFrame.setVisible(true);
    }
    catch (Exception e){
      System.err.println(e+" Usage: Browser HOSTNAME:PORTNUM\n See also client.properties");
      e.printStackTrace();
      System.exit(1);
    }
  }

    @Override
    public void concordanceChanged(ConcordanceDisplayEvent e) {
        if(e.getEventType()==2){
            concThread.stop();
            if(!e.getMessage().equalsIgnoreCase(" No concordances found"))
                for (StateChanged sl : listeners) sl.concordanceStateChanged();
            browserFrame.clearConcordanceList();
        }
    }
    
    @Override
    public void concordanceChanged(ConcordanceListSizeEvent e) {
    }

    @Override
    public void removeConcordanceLine(ConcordanceObject o) {
        browserFrame.removeConcordanceLine(o);
    }
     @Override
    public void removeConcordanceLineOnly(ConcordanceObject o) {
        browserFrame.removeConcordanceLineOnly(o);
    }
    
    @Override
    public void addConcordanceLine(ConcordanceObject o) {
        browserFrame.addConcordanceLine(o);
    }
    
    @Override
    public void redisplay() {
        browserFrame.redisplay();
    }

    @Override
    public void showSubcorpusSelector() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void loadNamedSubcorpus(String n) {
        
        browserFrame.setTitle(browserFrame.getTitle()+" Sub-corpus: " + n);
        subcorpusName = n;
        guiSubcorpusSaver.loadNamedCorpora(n);     
    }

    public void setSubcorpusName(String subcorpusName) {
        this.subcorpusName = subcorpusName;
    }
   
    @Override
    public void lodeRecentMenu() {
       browserFrame.loadRecentMenu();
    }
    
    public JMenu getRecentMenu(){
       return browserFrame.getRecentMenu();
    }

}
