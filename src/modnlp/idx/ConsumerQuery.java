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
package modnlp.idx;

import modnlp.idx.database.Dictionary;
import modnlp.idx.query.WordQuery;
import java.io.*;


/**
 *  Sample implementation of using Dictionary in a producer-consumer
 *  pipe pattern (over a PipedWriter)
 *
 * @author  S Luz &#60;luzs@acm.org&#62;
 * @version <font size=-1>$Id: ConsumerQuery.java,v 1.1 2006/05/22 17:26:02 amaral Exp $</font>
 * @see  
*/
public class ConsumerQuery implements Runnable {

  Thread thread;
  BufferedReader in = null;
  String query;

  public ConsumerQuery (String q){
    query = q;
    ConcordanceProducer ct = new ConcordanceProducer();
    ct.start();
    thread = new Thread(this);
    thread.start();
  }

  public static void main(String[] args) {
    ConsumerQuery cq = new ConsumerQuery(args[0]);
  }

  public void run(){
    if ( in != null) {
      try {
        String input;
        int c = 0;
        while ((input = in.readLine()) != null) {
          System.out.println("--GOT: "+input+" count = "+c++);
          System.out.flush();
        }
      } catch (IOException e) {
        System.err.println("OOOps" + e);
      }
    }
  }

  class ConcordanceProducer extends Thread {

    public void run () {
      try {
        PipedWriter pipeOut = new PipedWriter();
        in = new BufferedReader(new PipedReader(pipeOut));
        PrintWriter out = new PrintWriter(pipeOut);
        Dictionary d = new Dictionary(false);
        d.printConcordances(new WordQuery(query,d,false), 
                            50, true, out);        
      } catch (IOException e) {
        System.err.println("Concordancer error: " + e);
      }
    }
  }

}
