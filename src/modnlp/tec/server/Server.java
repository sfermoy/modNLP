/** 
 * Project: MODNLP/TEC/SERVER
 *
 * Copyright (c) 2006-2010 S.Luz (luzs@acm.org)
 *           All Rights Reserved.
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
 *
 *     
 */ 
package modnlp.tec.server;

import modnlp.idx.database.Dictionary;
import modnlp.idx.database.DictProperties;
import modnlp.idx.headers.HeaderDBManager;

import java.util.StringTokenizer;
import java.util.Hashtable; 
import java.util.Vector; 
import java.util.Enumeration;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.net.ServerSocket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;



/** Streamserver for TEC clients. Initially, load a precompiled
 * serialized hashtable for stroring all the words in the corpus
 * and await client connections. Then deal with requests through
 * the methods described below. 
 * 
 * @author S Luz &#60;luzs@acm.org&#62;
 * @version $Id: TecServer.java,v 1.3 2003/06/22 17:55:15 luzs Exp $
 * @see TecCorpusFile 
 * @see TecLogFile
 * @see TecDicFile
 * @see TecDictionary
 * @see FilePosStr
 */
public class Server extends Thread {

  private static final boolean DEBUG = false;
  private static final int MAXCTX = 400;
  private static final int MAXEXT = 600;
  // These are feaaults to be chaged in main() 
  private static ServerProperties sprop = new ServerProperties();
  private static String LOGF = sprop.getProperty("log.file");
  private static final int  PORTNUM = new Integer(sprop.getProperty("port.number")).intValue();
  
  private static ServerSocket serverSocket;
  private static Dictionary dtab;
  private static TecLogFile logf;
  private HeaderDBManager hdbm;
    private static String dateStarted;
	
	
  /** Initialize the server
   */
  public Server() {
    super("Server");
    try {
      System.out.println("socket open");
      serverSocket = new ServerSocket(PORTNUM);
      System.out.println("socket open");
      DictProperties dictProps = new DictProperties(sprop.getProperty("index.dir"));
      dtab = new Dictionary(dictProps);
      // separate method for initLogFile  to catch different IOException
      //Save satrtdate
      String pattern = "MM/dd/yyyy HH:mm:ss";
      DateFormat df = new SimpleDateFormat(pattern);
      Date today = Calendar.getInstance().getTime();      
      dateStarted = df.format(today);
      
      
      initLogFile();
      logf.logMsg("TEC Server accepting connections on port # "+PORTNUM);
      setLogDebug();
      // startSQLConnect();
    }
    catch (FileNotFoundException e) {
      logf.logMsg("TecServer: couldn't find file "+e);
      System.exit(1);
    }
    catch (IOException e) {
      System.err.println("TecServer: couldn't create socket"+e);
      System.exit(1);
    }
  }

  public void run() {
    try {
      hdbm = new HeaderDBManager(dtab.getDictProps());
    while (true)
      {
        new TecConnection( serverSocket.accept(), dtab, logf, hdbm, dateStarted);
      }
    }
    catch (IOException e) {
      logf.logMsg("TecServer: couldn't create socket"+e);
    }
    catch (Exception e){
      System.err.println("Error establishing connection "+e);
      e.printStackTrace();
    }
  }
  
  
  /** Initialize the logfile for this server
   */
  public void initLogFile() {
    try{
      logf    = new TecLogFile(LOGF);
    }
    catch (IOException e){
      System.err.println("initLogFile: " + e);
      System.exit(-1);
    }
  }
  
  private void setLogDebug() {
    if (DEBUG)
      logf.debugOn = true;
    else 
      logf.debugOn = false;
  }
	
  /** Start a server to listen for connection on
   *  <code>PORTNUM</code>
   * @param arg[0]  The (serialized object) dictionary
   *                file to be used by the server
   */
  public static void main(String[] arg) throws IOException {
    //System.out.println("hey");
    Server server = new Server();
    //server.setDaemon(true);
    server.start();
  }
	
}
