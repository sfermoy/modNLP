/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modnlp.tec.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.util.HashMap;
import java.util.Vector;
import modnlp.tec.client.gui.event.ConcordanceDisplayEvent;
import modnlp.tec.client.gui.event.ConcordanceDisplayListener;

/**
 *
 * @author shane
 */
public class HeaderDownloadThread implements Runnable {
    private Thread dldThread;
    private HashMap<String,String> headermap = null;
// String returned by server for reference
//     String queryFileVisReturn = "{data($s/document/@filename)}<sep/>"
//                + "{data($s/document/@format)}<sep/>"
//                + "{data($s/document/collection_title)}<sep/>"
//                + "{data($s/document/editor)}<sep/>"
//                + "{for $section in $s/section return"
//                    + " concat (\"<section/>\",data($section/@id),\"<sep/>\","
//+ "data($section/@title),\"<sep/>\","    
//                    + "data($section/@outlet),\"<sep/>\","
//                    + "data($section/@internet_outlet),\"<sep/>\","
//                    + "data($section/@publication_date),\"<sep/>\","
//                    + "data($section/@authorship_date),\"<sep/>\","
//                        + "string-join($section/author/name, ', '),\"<sep/>\","
//                        + "string-join($section/translation/translator/name, ', '),\"<sep/>\","
//                        + "data($section/translation/source/@date),\"<sep/>\","
//                        + "data($section/translation/source/@filename),\"<sep/>\","
//                        + "data($section/translation/source/@language),\"<sep/>\","
//                        + "data($section/translation/source/original_title),\"<sep/>\""
//                +")}";
    
    String[] fileAttrNames = {"Filename","Format","Collection_Title", "Editor"};
    String[] sectionAttrNames = {"ID","Title","Outlet","Internet_Outlet", "Publication_Date", "Authorship_Date", "Author","Translator",
        "Source_Date", "Source_Filename", "Source_Language", "Original_Title"};
 
  private String commandToServer = null;
  /** default port; normally reset by Browser */

  boolean stop = false;
  boolean serverResponded = false;
   int ctRead = 0;
  private TecClientRequest request = null;
  
  Socket  socket = null;
  //PrintStream output;
  BufferedReader input = null;
  String encoding = "UTF-8";  // the name of the charset
  
  
  /** Starts a new connection and performs a request to
   *  be displayed in the <code>ListDisplay</code> provided.
   *
   * @param server  The default server to which we'll connect
   * @param upd     The ListDisplay to update
   * @param ch      The request to be passed on to <code> server</code>
   */
  public HeaderDownloadThread(TecClientRequest cr, HashMap<String,String> headers)
  {
    headermap = headers;
    request = cr;
    //HASHMAP GPES HERE
    serverResponded = false;
  }
  
    public HeaderDownloadThread(BufferedReader in,TecClientRequest cr, HashMap<String,String> headers)
  {
    headermap = headers;
    input = in;
    request = cr;
    //HASHMAP GPES HERE
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

      serverResponded = true;

    }
    catch (java.lang.Exception e){
      stop();
    }
  }
  
    @Override
    public void run() {
    stop = false;
    String[] sections =null;
    String[] fileAttributes = null;
    String[] sectionAttributes = null;
    try {
      initConcordanceThread();
      String header = null;
      while (!stop ) {

        if ((header = input.readLine()) == null ){
          break;
        }
        if (header.length() > 0)
        {
              //System.out.println(header+"\n"); 
              sections = header.split("<section/>");
              for (int i = 0; i < sections.length; i++) {
                String jsonString = "{";
                String section = sections[i];                
                  if(i == 0){
                      fileAttributes = section.split("<sep/>");
                  }else{
                      sectionAttributes = section.split("<sep/>");
                      for (int j = 0; j < fileAttributes.length; j++) {
                        jsonString += fileAttrNames[j] +":\"" + fileAttributes[j]+"\", ";
                      }
                      for (int j = 0; j < sectionAttributes.length-1; j++) {
                          jsonString += sectionAttrNames[j] +":\"" + sectionAttributes[j]+"\", ";
                      }
                      jsonString += sectionAttrNames[sectionAttributes.length-1] +":\"" + sectionAttributes[sectionAttributes.length-1]+"\" }";
                      headermap.put(fileAttributes[0]+sectionAttributes[0], jsonString);
                      //System.out.println(jsonString);
                  }
              }
        }
        else {
          System.err.println("__headerThread:skipped: "+ctRead);
          continue;
        }
        ctRead++;
      } // end while
        
        System.out.println("Header Download Finished");
      stop();
    }
    catch (Exception e){
        System.err.println("HeaderThread Error" +e);
      stop();
    }
  }
    
    public void start(){
    if ( dldThread == null ){
      dldThread = new Thread(this);
      dldThread.start();
    }
  }

  public void stop() {
    if (dldThread == null)
      return;
    dldThread = null;
    try{
      if (socket != null) 
        socket.close();
      input = null;
      socket = null;
    }
    catch (java.lang.Exception e){
      System.err.println("Error Stopping thread "+e);
    }
  }
}
