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

import modnlp.idx.database.Dictionary;
import modnlp.idx.query.WordQuery;
import modnlp.idx.query.WordQueryException;
import modnlp.idx.headers.HeaderDBManager;


import java.io.*;
  
/**
 *  Access and produce condordances directly from index
 *
 * 
 * @author  S Luz &#60;luzs@acm.org&#62;
 * @version <font size=-1>$Id: $</font>
 * @see  
*/

public class ConcordanceProducer implements Runnable {

  BufferedReader in = null;
  PrintWriter out = null;
  TecClientRequest request;
  Dictionary dictionary;
  HeaderDBManager hdbm = null;
  
  public ConcordanceProducer (Dictionary d){
    super();
    this.dictionary = d;
    //this.request = r;
    try {
      hdbm = new HeaderDBManager(d.getDictProps());
      System.err.println("ConcordanceProducer opening header DB... ");
    } catch (Exception e) {
      System.err.println("Concordancer opening header DB: " + e);
    }
  }
  
  public void setRequest(TecClientRequest r){
    request = r;
  }

  public BufferedReader getBufferedReader (){
    try {
      PipedWriter pipeOut = new PipedWriter();
      out = new PrintWriter(pipeOut);
      in = new BufferedReader(new PipedReader(pipeOut));
    } catch (IOException e) {
      System.err.println("Concordancer error creating pipe: " + e);
    }
    return in;
  }
  
  public void start() {
    Thread thread = new Thread(this, "Producer");
    thread.start();
  }

  public void run () {
    try {
      boolean cse = ((String)request.get("case")).equalsIgnoreCase("sensitive");
      WordQuery wquery = 
        new WordQuery((String)request.get("keyword"), dictionary, cse);
      int ctx = 
        (new Integer((String)request.get("context"))).intValue();
      boolean ignx = 
        ((String)request.get("sgml")).equalsIgnoreCase("no")? true : false;
      String xquerywhere = 
        (String)request.get("xquerywhere");
      if (xquerywhere == null)
        dictionary.printConcordances(wquery, ctx, ignx, out);
      else
        dictionary.printConcordances(wquery, ctx, ignx, out,
                                     hdbm.getSubcorpusConstraints(xquerywhere));
    }
    catch (WordQueryException e){
      out.println(1);
      out.println("ERROR (Concordancer.run): Invalid query="+request.get("keyword")+e);
    }
  }
}
