/** 
 * Project: MODNLP/TEC/SERVER.
 *
 * Copyright (c) 2009-2019 S Luz, 2016 Shane Shehan
 *           (c) 2006 S.Luz (luzs@acm.org)
 *           (with contributions by Noel Skehan)
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

import modnlp.Constants;
import modnlp.dstruct.FrequencyHash;
import modnlp.idx.database.Dictionary;
import modnlp.idx.headers.HeaderDBManager;
import modnlp.idx.query.WordQuery;
import modnlp.idx.query.WordQueryException;

import java.net.Socket;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.io.OutputStreamWriter;
import java.io.InputStreamReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import modnlp.idx.query.SubcorpusConstraints;

/** Deal with client's requests for concordance and extracts through
 * the methods described below.
 * 
 * @author Nino Luz &#60;luzs@acm.org&#62;
 * @version $Id: TecConnection.java,v 1.6 2003/08/08 18:40:02 luzs Exp $
 * @see TecServer
 * @see TecCorpusFile 
 * @see TecLogFile 
 * @see Dictionary
 * @see FilePosStr
 */
public class TecConnection extends Thread {

  private static final int MAXCTX = 400;
  private static final int MAXEXT = 600;
  Socket  cSokt = null;
  HeaderDBManager hdbm = null;
  Dictionary dtab;
  TecLogFile logf;
  String sqlquery = "";
  private String dateStarted ;

  /** Initialize a new connection thread
   */
  public TecConnection(Socket s, Dictionary d, TecLogFile f, HeaderDBManager h, String dateServerStart) {
    cSokt = s;
    dtab = d;
    logf = f;
    hdbm = h;
    setPriority(NORM_PRIORITY - 1);
    //Get datetime when starting the server
    dateStarted = dateServerStart;
    start();
  }

  /** Wait for connections (forever) parse clients requests
   *  and trigger the appropriate actions
   */
  public void run() {

    BufferedReader is = null;
    PrintWriter os = null;
    //PrintWriter os = null;
    //SQLConnection sc;
    InetAddress inaddrr;
    
    try {
      os = 
        new PrintWriter(new OutputStreamWriter(cSokt.getOutputStream(),
                                               dtab.getDictProps().getProperty("file.encoding")),
                        true);
      //os = new PrintWriter
      //  (new BufferedOutputStream(cSokt.getOutputStream()));
      is = new BufferedReader
        (new InputStreamReader(cSokt.getInputStream(), 
                               dtab.getDictProps().getProperty("file.encoding")));
      //sc = new SQLConnection();
      inaddrr = cSokt.getInetAddress();
      String inLine, outLine;
      if ((inLine = is.readLine()) != null) 
        {		 
          os.println("HTTP/1.0 200 OK\n");
          //logf.logMsg("->"+inLine+"<-");
          Request req = new Request(inLine);
          processInput(req, os);
          // limit logged size of very large column requests (to prevent log from growing too large)
          if (req.typeOfRequest() == Request.COLUMNBATCH)
            logf.logMsg("["+inaddrr.getHostName()+"] "+inLine.substring(0,inLine.lastIndexOf("column="))+"...");
          else
            logf.logMsg("["+inaddrr.getHostName()+"] "+inLine);

          os.println("");
          //os.println("____FINISHED___");
          os.flush(); 
        }
      // Cleanup
      is.close();
      os.close();
      cleanUp(cSokt);
                }
		catch (WordQueryException e) {
                  logf.logMsg("doConcordance: Malformed query: |"+e.getOriginalQuery());
                  os.println(-1);
			os.println("Error: Malformed query:"+e.getOriginalQuery());
			os.println("");
			os.flush();
			cleanUp(cSokt);
		}
		catch (IOException e) {
      logf.logMsg("TecConnection: Connection lost to client socket "+e);
      cleanUp(cSokt);
			return;
		}	
		catch (Exception e) {
      logf.logMsg("TecConnection: serious exception caught: " + e);
			e.printStackTrace();
			cleanUp(cSokt);
		}
  }
	
  /** Close client socket 
   * @param cls   the socket to be closed 
   */
  private void cleanUp (Socket cls)  {
    try {
      //System.out.println("cleaning up ...");
      cls.close();
    }
    catch (IOException e) {
      logf.logMsg("TecConnection: error closing Socket "+e);
    }
  }
	
  /** Select a request and perform appropriate action
   * @param req   a <code>Request</code>
   * @param os    the output stream (to be received by client)
   * @see Request
   */
  private void processInput(Request req, PrintWriter os) 
    throws IOException
  {
    String outStr = null;
    switch (req.typeOfRequest())
      {
      case Request.CONCORD:
        doConcordance(req, os);
        break;
      case Request.EXTRACT:
        getExtract(req, os);
        break;
      case Request.FREQLIST:
        getFreqList(req, os);
        break;
      case Request.FREQWORD:
        getFreqWord(req, os);
        break;
      case Request.COLUMNBATCH:
        getFreqColumn(req, os);
        break;
      case Request.NOOFTOKENS:
        getTotalNoOfTokens(req, os);
        break;
      case Request.CDESCRIPTION:
        getCorpusDescription(req, os);
        break;
      case Request.HEADERBASEURL:
        getHeaderBaseURL(os);
        break;
      case Request.ALLHEADERS:
        getAllHeaders(req, os);
        break;
      case Request.STARTDATE:
        getServerStartDate(req, os);
        break;
        //case Request.HEADEREXT:
        //getHeaderEXT(os);
        //break;
      case Request.ATTCHOPTSPECS:
        sendAttributeChooserSpecs(os);
        break;
      case Request.ATTCHOSPTIONS:
        sendAttOptionSet(req,os);
        break;
      default:
        logf.logMsg("TecServ: couldn't understand req "+
                    req.get("request")+req.typeOfRequest());
        break;
      }
  }
	
	
  /** Retrieve each line containing a concordance for
   *  a given keyword
   *
   * @param req    A pre-parsed client request (key-value pairs)
   * @param os     the output stream (to be received by client)
   * @see Request
   * @see TecCorpusFile
   */
  private void doConcordance(Request req, PrintWriter os) 
		throws IOException
  {
    WordQuery wquery = null;
    try {
      boolean cse = ((String)req.get("case")).equalsIgnoreCase("sensitive");
      wquery = new WordQuery ((String) req.get("keyword"), dtab, cse);
      //System.out.println("=%%%=>"+wquery.getKeyword());
      int ctx = getSafeInteger((String)req.get("context"),MAXCTX).intValue();
      boolean ignx = 
        ((String)req.get("sgml")).equalsIgnoreCase("no")? true : false;
      String xquerywhere = (String)req.get("xquerywhere");
      //System.err.println("xquerywhere->"+xquerywhere);
      if (xquerywhere == null)
        dtab.printConcordances(wquery, ctx, ignx, os);
      else
        dtab.printConcordances(wquery, ctx, ignx, os,
                               hdbm.getSubcorpusConstraints(xquerywhere));
    }
    catch (WordQueryException e) {
      logf.logMsg("doConcordance: Malformed query: |"+wquery+"|"+e);
			os.println(-1);
      os.println("Malformed query: "+wquery);
      os.flush();
    }
    catch (NullPointerException e) {
      e.printStackTrace();
      logf.logMsg("doConcordance: word not found: |"+wquery+"|"+e);
			os.println(-1);
			os.println("Server error retrieving concordance of "+wquery);
      os.flush();
    }
  }
	
  private void sendAttributeChooserSpecs(PrintWriter os) 
		throws IOException
  {
    os.println(dtab.getDictProps().getProperty("xquery.attribute.chooser.specs"));
    os.flush();
  }

  private void sendAttOptionSet(Request req, PrintWriter os) 
		throws IOException
  {
    String xqueryattribs = (String)req.get("xqueryattribs");
    String s = hdbm.getOptionSetString(xqueryattribs);
    if (s == null){
      os.println("_,__,_");
    }
    else if (s.length() < 6) // a little hack to force transmission of very short option lists
      os.println(s+"_,__,_");
    else
      os.println(s);
    os.flush();
  }


  /** Retrieve a bit of text surrounding a given keyword
   *  
   * @param req    A pre-parsed client request (key-value pairs)
   * @param os     the output stream (to be received by client)
   * @see Request
   * @see TecCorpusFile
   */
  public void  getExtract(Request req,  PrintWriter os)
  {
    String fn = null;
    try {
    int ctx = getSafeInteger((String)req.get("context"),MAXEXT).intValue();
    fn  = (String)req.get("filename");
    long  bp  = new Integer((String) req.get("position")).longValue();
    boolean ignx = ((String)req.get("sgml")).equalsIgnoreCase("no")? true : false;
    //logf.logMsg("Reading "+fn);
    String textLine = 
      dtab.getExtract((String)req.get("filename"),
                            ctx, bp, ignx);
    os.println(textLine);
    }
    catch (Exception e) {
      logf.logMsg("doConcordance: error reading corpus "+e);
    }
  }

  public void getHeaderBaseURL(PrintWriter os){
    //System.err.println(dtab.getDictProps().getProperty("headers.url"));
    os.println(dtab.getDictProps().getProperty("headers.url"));
    os.println(dtab.getDictProps().getProperty("file.encoding"));
    os.println(dtab.getDictProps().getLanguage());
    os.println(dtab.getDictProps().getProperty("header.extension"));
    //if (lg != null)
    //os.flush();
  }


  /** Retrieve a (case-insensitive) frequency list
   *  
   * @param req    A pre-parsed client request (key-value pairs)
   * @param os     the output stream (to be received by client)
   * @see Request
   * @see TecCorpusFile
   */
  public void  getFreqList(Request req,  PrintWriter os)
  {
    int sf, ms;
    boolean cs;
    try {
      sf = (new Integer((String)req.get("skipfirst"))).intValue();
      ms = (new Integer((String)req.get("maxlistsize"))).intValue();
      cs = ((String)req.get("casesensitive")).equalsIgnoreCase("TRUE");
    }
    catch (Exception e){
      // error: print entire fqlist
      System.err.println("Exception caught: Printing whole fqlist: "+req);
      dtab.printSortedFreqList(os);
      return;
    }
    if (req.get("xquerywhere")==null )
      dtab.printSortedFreqList(os, sf, ms,!cs);
    else
      dtab.printSortedFreqList(os, sf, ms,
                               hdbm.getSubcorpusConstraints((String)req.get("xquerywhere")),
                               !cs);
  }


  /** Retrieve the frequency of a keyword (possibly in a case-insensitive way).
   *
   *  Parameters to be passed through the GET-like request by the
   *  client are: <code>casesensitive</code> to indicate whether case
   *  matters in searching for the <code>keyword</code>, and
   *  <code>xquerywhere</code> to specify subcorpus search
   *  constraints.
   *
   * @param req    A pre-parsed client request (key-value pairs)
   * @param os     the output stream (to be received by client)
   *
   * @see Request
   * @see TecCorpusFile
   */
  public void  getFreqWord(Request req,  PrintWriter os)
  {
    boolean cs;
    WordQuery wquery = null;
    String xquerywhere = null;

    try {
      cs = ((String)req.get("casesensitive")).equalsIgnoreCase("TRUE");
      wquery = new WordQuery ((String) req.get("keyword"), dtab, cs);
      xquerywhere = (String)req.get("xquerywhere");
    }
    catch (Exception e){
      // error: print entire fqlist
      System.err.println("Exception caught: Retrieving word frequency: "+req);
      dtab.printSortedFreqList(os);
      return;
    }
    if (req.get("xquerywhere")==null )
      os.println(dtab.getFrequency(wquery.getKeyWordForms()));
    else
      os.println(dtab.getFrequency(wquery.getKeyWordForms(),
                                   hdbm.getSubcorpusConstraints((String)req.get("xquerywhere"))));
  }
  
 
  public void  getFreqColumn(Request req,  PrintWriter os)
  {
    boolean cs;
    WordQuery wquery = null;
    String xquerywhere = null;
    String colstr = (String) req.get("column");
    String[] col = unmergeStrings(colstr);
    int[] freqs= new int[col.length];
    
    for (int i = 0; i < col.length; i++) {
        
        try {
          cs = ((String)req.get("casesensitive")).equalsIgnoreCase("TRUE");
          wquery = new WordQuery (col[i], dtab, cs);
          xquerywhere = (String)req.get("xquerywhere");
        }
        catch (Exception e){
          // error: print entire fqlist
          System.err.println("Exception caught: Retrieving word frequency: "+req);
          dtab.printSortedFreqList(os);
          return;
        }
        if (req.get("xquerywhere")==null )
          freqs[i]=dtab.getFrequency(wquery.getKeyWordForms());
        else
           freqs[i]=dtab.getFrequency(wquery.getKeyWordForms(),
                                       hdbm.getSubcorpusConstraints((String)req.get("xquerywhere")));
        col[i]=""+freqs[i];
    }
      
      os.println(mergeStrings(col));
  }
      

  /** Retrieve the toal number of tokens in corpus
   *
   * @param req    A pre-parsed client request (key-value pairs)
   * @param os     the output stream (to be received by client)
   *
   * @see Request
   * @see TecCorpusFile
   */
  public void  getTotalNoOfTokens(Request req,  PrintWriter os)
  {
    os.println(dtab.getTotalNoOfTokens());
    
  }
  
  
  public void  getAllHeaders(Request req,  PrintWriter os)
  {
    try {
      int [] fks = dtab.getIndexedFileKeys();
      os.println(hdbm.getFileHeaderAttributeHuman());
        for (int i = 0; i < fks.length; i++) {
            String fdesc = hdbm.getFileHeaderAttributes(fks[i]);  
            String line = fdesc ;
            // System.err.println("--");
            // System.out.println(line);
            os.println(line);     
      }       
    }
    catch (Exception e) {
      System.err.println("CDescPrinter: " + e);
      e.printStackTrace();
    }

  }

  public void getCorpusDescription(Request req,  PrintWriter os)
  {
    boolean cs;
    try {
      cs = ((String)req.get("casesensitive")).equalsIgnoreCase("TRUE");
      int [] fks = dtab.getIndexedFileKeys();
      String xquerywhere =  (String)req.get("xquerywhere");
      
      double sumTTratios = 0;
      int tokenCount = 0;
      int countSubcorpusFiles =0;
        for (int i = 0; i < fks.length; i++) {
            if (xquerywhere == null){
                FrequencyHash fh = dtab.getFileFrequencyTable(fks[i], !cs);
                String fdesc = hdbm.getFileDescription(fks[i]);  
                String line = fks[i]+Constants.LINE_ITEM_SEP+fdesc+Constants.LINE_ITEM_SEP+
                  fh.getTokenCount()+Constants.LINE_ITEM_SEP+
                  fh.getTypeTokenRatio();
                // System.err.println("--");
                os.println(line);
            }
            else{
                SubcorpusConstraints  sbc=  hdbm.getSubcorpusConstraints(xquerywhere);
                if (sbc != null && sbc.acceptFile(  Integer.toString(fks[i]))){
                    FrequencyHash fh = dtab.getFileFrequencyTable(fks[i], !cs);
                    String fdesc = hdbm.getFileDescription(fks[i]);  
                    String line = fks[i]+Constants.LINE_ITEM_SEP+fdesc+Constants.LINE_ITEM_SEP+
                      fh.getTokenCount()+Constants.LINE_ITEM_SEP+
                      fh.getTypeTokenRatio();
                    // System.err.println("--");
                    sumTTratios += fh.getTypeTokenRatio();
                    tokenCount += fh.getTokenCount();
                    countSubcorpusFiles++;
                    os.println(line);
                }
            }
          
      }
        if (xquerywhere != null){
            dtab.printSubCorpusStats(os, (double) sumTTratios/countSubcorpusFiles, tokenCount);
            dtab.printNoItems(os, tokenCount);
        }else
        {
            dtab.printCorpusStats(os,!cs);
            dtab.printNoItems(os, fks.length);
        }
    }
    catch (Exception e) {
      System.err.println("CDescPrinter: " + e);
      e.printStackTrace();
    }
  }
  


  // utilities for internal use 
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
  private static final String STRING_SEPARATOR = "@|$|@";
private static final String STRING_SEPARATOR_REGEX = "@\\|\\$\\|@";

private String mergeStrings(String[] ss) {
    StringBuilder sb = new StringBuilder();
    for(String s : ss) {
        sb.append(s);
        sb.append(STRING_SEPARATOR);
    }
    return sb.toString();
}

private String[] unmergeStrings(String s) {
    return s.split(STRING_SEPARATOR_REGEX);
}

private void getServerStartDate(Request req, PrintWriter os) {
     os.println(dateStarted);
}
	
}


