/** 
 * (c) 2006 S Luz <luzs@acm.org>
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
import modnlp.tec.client.TecClientRequest;
import java.net.Socket;
import java.net.URL;
import java.io.PrintStream;
import java.io.BufferedReader;
import java.net.InetAddress;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.HttpURLConnection;

/**
 *  This is a Client to interact with the <a
 *  href="../../Server/index.html">TEC Server</a> This thread does
 *  substantially less than its name might suggest: it does simply
 *  extraction of text surrounding concondances.  Most of the client
 *  interaction is actually done in <code>ConcordanceThread
 *  </code>. (<b>This is another legacy class from UMIST in need of a
 *  complete rewrite</b>)
 *
 * @author  S. Luz &#60;luzs@acm.org&#62;
 * @version <font size=-1>$Id: ContextClient.java,v 1.5 2003/06/22 13:48:05 luzs Exp $</font>
 * @see  TecServer
 * @see  ConcordanceThread
 * @see  ListDisplay
 * @see  ConcArray
 * @see  FullTextWindow
 * @see  HeaderReader
*/
public class ContextClient extends FullTextWindow
	implements Runnable
{

  public int  PORTNUM = 1240;
  public String  SERVER = "ronaldo.ccl.umist.ac.uk"; //"ubatuba.ccl.umist.ac.uk";
  public String  keywordString = null;
  public String allLines[] = new String[1000];
  private Thread ftwThread;
  TecClientRequest request;

  int windowHeight = 250;
  int windowWidth = 600;
  
  Socket  socket;
  PrintStream output;
  BufferedReader input;
  Dictionary dictionary = null;
  //DataInputStream input;
  String encoding = "UTF-8";  // the name of the charset


  /** Create an "extract thread" with its own socket
   *  through which <code>request</code> will be sent to <code>TecServer</code>
   */
  public ContextClient(String server, String request){
    super("TecWindow");
    setContentType("text/html");
    SERVER = server;
    try {
      // Send a choices to the server
      socket = new Socket(InetAddress.getByName(SERVER), PORTNUM);
      input = new
        BufferedReader(new
                       InputStreamReader(socket.getInputStream(),
                                         encoding));
      //input = new DataInputStream(socket.getInputStream());
      output = new PrintStream(socket.getOutputStream());
      output.println(request);
      //start();
    }
    catch(IOException e)
      {
        System.err.println("Exception: couldn't create stream socket"+e);
        System.exit(1);
      }
  }
  
  public ContextClient(TecClientRequest rq){
    super(rq.get("filename").toString());
    setContentType("text/html");
    SERVER = rq.getServerURL();
    keywordString = rq.get("keyword").toString();
    try {
      URL exturl = new URL(rq.toString());
      HttpURLConnection exturlConnection = (HttpURLConnection) exturl.openConnection();
      //exturlConnection.setUseCaches(false);
      exturlConnection.setRequestMethod("GET");
      input = new
        BufferedReader(new
                       InputStreamReader(exturlConnection.getInputStream(),
                                         encoding));
      //start();
    }
    catch(IOException e)
      {
        System.err.println("Exception: couldn't create URL input stream"+e);
        System.exit(1);
      }
  }
  
  // no connection mode: retrieve line directly from dictionary
  public ContextClient(TecClientRequest rq, Dictionary dc){
    super(rq.get("filename").toString());
    setContentType("text/html");
    request = rq;
    keywordString = rq.get("keyword").toString();
    dictionary = dc;
    //start();
  }

  /** Create an "extract thread" with its own built-in output window
   *
   * @param srv  The name of the server to which we will connect
   * @param rq   The request line for the server (CGI-style)
   * @param fn   The name of the file from which to extract
   * @param key  The concordance key
   */
  public ContextClient(String srv, String rq, String fn, String key){
    super(fn);
    setContentType("text/html");
    SERVER = srv;
    keywordString = key;
    //window =  new FullTextWindow(fn);
    try {
      // Send a choices to the server
      socket = new Socket(InetAddress.getByName(SERVER), PORTNUM);
      input = new
				BufferedReader(new
                                               InputStreamReader(socket.getInputStream(),
                                                                 encoding));
      //input = new DataInputStream(socket.getInputStream());
      output = new PrintStream(socket.getOutputStream());
      output.println(rq);
      start();
    }
    catch(IOException e)
      {
        System.err.println("Exception: couldn't create stream socket"+e);
        System.exit(1);
      }
  }



  /** Receive a line from the server and display it
   *  on this thread's <code>window</code>
   */
  public void run() {
    setSize(windowWidth, windowHeight);
    setVisible(true);
    try {
      String textLine;
      if (dictionary == null)
        textLine = input.readLine();
      else
        textLine = dictionary.getExtract((String)request.get("filename"),
                                         (new Integer((String)request.get("context"))).intValue(),
                                         (new Integer((String)request.get("position"))).longValue(),
                                         ((String)request.get("sgml")).equalsIgnoreCase("no")? true : false);
      allLines[0] = textLine;
      
      text = allLines;
      displayConcExtract(keywordString);

      if ( output != null ) output.close();
      if ( input != null ) input.close();
    }
    catch (Exception e)
      {
        System.err.println("Exception: " + e);
        e.printStackTrace();
        displayText("Error: "+e);
      }
  }
  
  public void start() {
    if ( ftwThread == null ){
      ftwThread = new Thread(this);
      ftwThread.setPriority (Thread.NORM_PRIORITY + 2);
      ftwThread.start();
    }
  }

  public void stop() {
    if ( ftwThread != null ){
      //ftwThread.stop();
      ftwThread = null;
    }
  }


  private Integer getSafeInteger(String inp, int max){
    try {
      Integer ctx = new Integer(inp);
      if (ctx.intValue() > max) // check context limiti
				ctx = new Integer(max);
      return ctx;
    }
    catch (NumberFormatException e){
      Integer ctx = new Integer(max);
      return ctx;
    }
  }
	

}
