/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modnlp.tec.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PipedReader;
import java.io.PipedWriter;
import java.io.PrintWriter;
import modnlp.idx.database.Dictionary;
import modnlp.idx.headers.HeaderDBManager;
import modnlp.idx.query.WordQuery;
import modnlp.idx.query.WordQueryException;

/**
 *
 * @author shane
 */
public class HeaderProducer implements Runnable{


  BufferedReader in = null;
  PrintWriter out = null;
  TecClientRequest request;
  Dictionary dictionary;
  HeaderDBManager hdbm = null;
  
  public HeaderProducer (Dictionary d){
    super();
    this.dictionary = d;
    //this.request = r;
    try {
      hdbm = new HeaderDBManager(d.getDictProps());
      System.err.println("HeaderProducer opening header DB... ");
    } catch (Exception e) {
      System.err.println("HeaderProducer opening header DB: " + e);
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
      System.err.println("HeaderProducer error creating pipe: " + e);
    }
    return in;
  }
  
  public void start() {
    Thread thread = new Thread(this, "HeaderProducer");
    thread.start();
  }

  public void run () {
    try {
        dictionary.printHeaders(out, hdbm);
    }
    catch (WordQueryException e){
      out.println(1);
      out.println("ERROR (Concordancer.run): Invalid query="+request.get("keyword")+e);
    }
  }
}
