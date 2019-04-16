/**
 *
 *   Copyright (c) 2008 S Luz <luzs@acm.org>. All Rights Reserved.
 *
 *   This program is free software; you can redistribute it and/or
 *   modify it under the terms of the GNU General Public License
 *   as published by the Free Software Foundation; either version 2
 *   of the License, or (at your option) any later version.
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

import java.util.HashMap;
import modnlp.tec.client.gui.BrowserGUI;
import modnlp.tec.client.gui.event.ConcordanceDisplayEvent;
import modnlp.tec.client.gui.event.ConcordanceListSizeEvent;
import modnlp.idx.database.Dictionary;
import modnlp.idx.headers.HeaderDBManager;

/**
 *  General concordance browser. Classes implementing this interface
 *  will typically control {@link modnlp.tec.client.gui.BrowserFrame}
 *  objects.
 * 
 * @author  S Luz &#60;luzs@acm.org&#62;
 * @version <font size=-1>$Id: $</font>
 * @see  modnlp.tec.client.gui.BrowserFrame
 * @see  NewBrowser
*/
public interface ConcordanceBrowser {

  String RELEASE = "0.0.0";
  String REVISION = "$Revision: 0.0 $";
  String BRANDNAME = "MODNLP/TEC";

  public void requestConcordance(String query);

  public void startSorting(int horizon, boolean sortleft);

  public void showExtract(ConcordanceObject sel);

  public void showHeader(ConcordanceObject sel);

  public void showHelp();

  public void showAbout();

  public void showErrorMessage(String e);

  public void downloadConcordance(Download dld) throws java.io.IOException;

  public void chooseNewLocalCorpus();

  public void chooseNewRemoteCorpus();

  public void concordanceChanged(ConcordanceDisplayEvent e);

  public void concordanceChanged(ConcordanceListSizeEvent e);
  
  public void setRemoteCorpus(String s, int p);
    
  public void quit();

  public void incProgress();

  public void setXQueryWhere(String query);

  public void setAdvConcFlag(boolean v);

  public String getXQueryWhere();

  public boolean isStandAlone();

  public Dictionary getDictionary();

  public HeaderDBManager getHeaderDBManager();

  public String getRemoteServer();

  public int getRemotePort();

  public String getBrand();

  public int getPreferredFontSize();

  public void showPreferencesEditor();

  public void showSubcorpusSelector();

  public ConcordanceVector getConcordanceVector();

  public String getKeywordString();

  public int getExpectedNoOfConcordances();

  public int getNoOfConcordancesReadSoFar();

  public BrowserGUI getBrowserGUI();

  public boolean gotResponseFromServer();

  public boolean isReceivingFromServer();

  public boolean isCaseSensitive();

  public ClientProperties getClientProperties();

  public boolean isSorting();

  public String getRelease();

  public String getVersion ();

  public String getBrowserName ();
  
  public HashMap< String, String> getHeaderMap();
  
  public HeaderProducer getHeaderProducer();
  
  public  String getEncoding();

  public int getLanguage();

  public boolean isSubCorpusSelectionON ();

  public boolean subCorpusSelected();
  
  public void showContext(int col, String str);
  
  public void addChangeListener(StateChanged toAdd);
  
  public  String getHeaderBaseUrl();
  
  public void removeConcordanceLine(ConcordanceObject o);
  
  public void removeConcordanceLineOnly(ConcordanceObject o);
  
  public void addConcordanceLine(ConcordanceObject o);
  
  public void redisplay();

}
