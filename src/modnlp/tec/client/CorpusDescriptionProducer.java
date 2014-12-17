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

import modnlp.dstruct.FrequencyHash;
import modnlp.idx.database.Dictionary;
import modnlp.idx.query.WordQuery;
import modnlp.idx.query.WordQueryException;
import modnlp.idx.headers.HeaderDBManager;


import java.io.*;
import java.text.NumberFormat;
  
/**
 *  Access and produce condordances directly from index
 *
 *  NB: NOT PROPERLY IMPLEMENTED YET. SEE plugins/CorpusDescriptionProducer.java 
 *      FOR THE CURRENT IMPLEMENTATION AS AN INNER CLASS!
 * @author  S Luz &#60;luzs@acm.org&#62;
 * @version <font size=-1>$Id: $</font>
 * @see  
*/

public class CorpusDescriptionProducer implements Runnable {

  BufferedReader in = null;
  PrintWriter out = null;
  TecClientRequest request;
  Dictionary dictionary;
  HeaderDBManager hdbm = null;
  
  public CorpusDescriptionProducer (Dictionary d){
    super();
    this.dictionary = d;
    //this.request = r;
    try {
      hdbm = new HeaderDBManager(d.getDictProps());
      System.err.println("CorpusDescriptionProducer opening header DB... ");
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
      int [] fks = dictionary.getIndexedFileKeys();
      //NumberFormat nf =  NumberFormat.getInstance();
      String xquerywhere = 
        (String)request.get("xquerywhere");
      // TODO: implement information per sub-corpus as well
      //if (xquerywhere == null)
      //  dictionary.printConcordances(wquery, ctx, ignx, out);
      //else
      //  dictionary.printConcordances(wquery, ctx, ignx, out,
      //                               hdbm.getSubcorpusConstraints(xquerywhere));
      dictionary.printCorpusStats(out,!cse);
      for (int i = 0; i < fks.length; i++) {
        FrequencyHash fh = dictionary.getFileFrequencyTable(fks[i], !cse);
        String fdesc = hdbm.getFileDescription(fks[i]);        
        out.println(fks[i]+":|:"+fdesc+":|:"+
                    fh.getTokenCount()+":|:"+
                    (fh.getTypeCount()/fh.getTokenCount()));
      }
    }
    catch (WordQueryException e){
      out.println(1);
      out.println("ERROR (Concordancer.run): Invalid query="+request.get("keyword")+e);
    }
  }
}
