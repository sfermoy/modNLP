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
package modnlp.tec.client;

import modnlp.tec.client.gui.ConcordanceListModel;
import modnlp.tec.client.gui.event.*;

import javax.swing.SwingUtilities;
import java.net.Socket;
import java.io.PrintStream;
import java.io.BufferedReader;
import java.net.HttpURLConnection;
import java.net.*;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.Vector;


/**
 *  Receive reply from server (list of concondances)
 *  and display them from time to time. This class should
 *  in the future be an extension of ListDisplay and so its
 *  updates by itself (thus its design as a class that implements
 *  <code>Runnable</code> rather than extends
 *  <code>java.lang.Thread</code> .
 *
 * @author  S. Luz &#60;luzs@acm.org&#62;
 * @version <font size=-1>$Id: ConcordanceThread.java,v 1.5 2003/08/06 16:58:56 luzs Exp $</font>
 * @see
*/
public class ConcordanceThread
	implements Runnable, ConcordanceMonitor{

  private Thread concThread;
  /**  This variable sets where we should start showing the list */
  int updateThreshold = 40;
  private String commandToServer = null;
  /** default port; normally reset by Browser */
  int noFound = 0;
  int ctRead;
  private ConcordanceVector concVector;
  boolean stop = false;
  boolean serverResponded = false;
  private Vector<ConcordanceDisplayListener> concDisplayListeners = 
    new Vector<ConcordanceDisplayListener>();

  private TecClientRequest request = null;
  
  Socket  socket = null;
  PrintStream output;
  BufferedReader input = null;
  String encoding = "UTF-8";  // the name of the charset

  //DataInputStream input;



  /** Starts a new connection and performs a request to
   *  be displayed in the <code>ListDisplay</code> provided.
   *
   * @param server  The default server to which we'll connect
   * @param upd     The ListDisplay to update
   * @param ch      The request to be passed on to <code> server</code>
   */
  public ConcordanceThread(ConcordanceVector clm, TecClientRequest cr)
  {
    request = cr;
    concVector = clm;
    serverResponded = false;
  }

  
  public ConcordanceThread(ConcordanceVector clm, BufferedReader in, TecClientRequest cr)
  {
    input = in;
    concVector = clm;
    request = cr;
    serverResponded = false;
  }
  
  public void setEncoding (String e){
    encoding = e;
  }

  /** Starts a new connection and performs a request to
   *  be displayed in the <code>ListDisplay</code> provided.
   *
   * @param server  The default server to which we'll connect
   * @param upd     The ListDisplay to update
   * @param ch      The request to be passed on to <code> server</code>
   */
  private void initConcordanceThread( )
  {
    try {
      noFound = -1;
      if (input == null) {
        //socket = new Socket(InetAddress.getByName(SERVER), PORTNUM);
        URL concurl = new URL( request.toString());
        HttpURLConnection concurlConnection =
          (HttpURLConnection) concurl.openConnection();
        concurlConnection.setUseCaches(false);
        concurlConnection.setRequestMethod("GET");
        concurlConnection.connect();  
        input = new
          BufferedReader(new
                         InputStreamReader(concurlConnection.getInputStream(),
                                           encoding));
      }
      
      String temp = input.readLine();
      while ( ! temp.matches("[0-9]+") ){
        System.err.println("Discarding: "+temp);
        temp = input.readLine();
      }
      serverResponded = true;
      noFound = (new Integer(temp)).intValue();
      System.out.println("____noFound_____"+noFound);
      if (noFound < 0)
        {
          String mes = input.readLine();
          System.out.println("________"+mes);
          ctRead = noFound;
          fireDisplayEvent(0,ConcordanceDisplayEvent.DOWNLOADSTATUS_EVT,mes);
          stop();
        }
    }
    catch (java.io.IOException e){
      fireDisplayEvent(0,ConcordanceDisplayEvent.DOWNLOADSTATUS_EVT,
                       "  **** TRANSFER INTERRUPTED **** "+e);
      stop();
    }
    catch (Exception e){
      e.printStackTrace();
      stop();
    }
  }

  public void run() {
    boolean fshow = false;
    boolean notru = true;
    stop = false;
    ctRead = 0;
    try {
      initConcordanceThread();
      String concordance = null;
      //int ctsz = request.getContextSize();
      if (noFound == 0)
        fireDisplayEvent(0,ConcordanceDisplayEvent.DOWNLOADCOMPLETE_EVT,
                         " No concordances found");
      concVector.ensureCapacity(noFound);
      //System.out.println("_________context size: "+ctsz);
      while (!stop && ctRead < noFound ) {

        if ((concordance = input.readLine()) == null ){
          break;
        }
        if (concordance.length() > 0)
          concVector.add(concordance); 
        else {
          System.out.println("__ConcordanceThread:skipped: "+ctRead);
          continue;
        }
        ctRead++;
        if ( ctRead > updateThreshold || ctRead >= noFound)
          if ( !fshow && noFound > 0)
            {
              if (noFound > ctRead) 
                fireDisplayEvent(0,ConcordanceDisplayEvent.FIRSTDISPLAY_EVT,
                                 "  Searching through "+noFound+" concordances ");
              else
                fireDisplayEvent(0,ConcordanceDisplayEvent.FIRSTDISPLAY_EVT,null);
              fshow = true;
            }
      } // end while
      concVector.doneAdding();
      concVector.trimToSize();
      noFound = ctRead;
      if ( !fshow ){
        fireDisplayEvent(0,ConcordanceDisplayEvent.FIRSTDISPLAY_EVT,null);
        fshow = true;
      }
      if (noFound > 0) {
        fireDisplayEvent(0,ConcordanceDisplayEvent.DOWNLOADCOMPLETE_EVT,
                         " Returned "+ctRead+" lines matching your query");
        fireListSizeEvent(noFound);
      }
      
      stop();
    }
    catch (java.io.IOException e){
      //parent.updateStatusLabel("  **** TRANSFER INTERRUPTED **** "+e);
      if (noFound > 0) {
        noFound = ctRead;
        fireListSizeEvent(noFound);
        fireDisplayEvent(0,ConcordanceDisplayEvent.DOWNLOADCOMPLETE_EVT,
                         " Returned "+ctRead+" lines matching your query");
      }
      stop();
    }
    catch (NullPointerException e){
      fireDisplayEvent(0,ConcordanceDisplayEvent.CHANGE_EVT,
                       "  ConcordanceThread error: "+e);
      stop();
    }
  }

  public void start(){
    if ( concThread == null ){
      concThread = new Thread(this);
      concThread.start();
    }
  }

  public void stop() {
    if (concThread == null)
      return;
    concThread = null;
    try{
      if (output != null){
        output.flush();
        output.close();
      }
      //if (input != null) 
      //  input.close();
      if (socket != null) 
        socket.close();
      input = null;
      output = null;
      socket = null;
    }
    catch (java.lang.Exception e){
      System.err.println("Error Stopping thread "+e);
    }
  }

  public final boolean atWork() {
    if ( concThread != null )
      return true;
    else
      return false;
  }
  
  public final int getNoFound () {
    return noFound;
  }

  public final int getNoRead () {
    return ctRead;
  }

  public final boolean getServerResponded () {
    return serverResponded;
  }
  
  private final void fireDisplayEvent (int from, int evt, String msg) {
    for (Iterator<ConcordanceDisplayListener> p = concDisplayListeners.iterator(); p.hasNext(); )
      {
        ConcordanceDisplayListener cdl = p.next();
        if (cdl != null)
          cdl.concordanceChanged(new ConcordanceDisplayEvent(this, from, evt, msg));
      }
  }

  private void fireListSizeEvent (int size)
  {
    for (Iterator<ConcordanceDisplayListener> p = concDisplayListeners.iterator(); p.hasNext(); )
      {
        ConcordanceDisplayListener cdl = p.next();
        if (cdl != null)
          cdl.concordanceChanged(new ConcordanceListSizeEvent(this, size));
      }
  }
  
  /* Implement ConcordanceMonitor */
  
  public void addConcordanceDisplayListener(ConcordanceDisplayListener cdl)
  {
    concDisplayListeners.add(cdl);
  }
  
  public void removeConcordanceDisplayListener(ConcordanceDisplayListener cdl)
  {
    concDisplayListeners.remove(cdl);
  }
}
