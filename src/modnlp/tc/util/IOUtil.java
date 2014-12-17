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
package modnlp.tc.util;

import java.io.*;
import modnlp.tc.dstruct.*;
import modnlp.tc.parser.*;
import modnlp.tc.tsr.*;
import modnlp.tc.evaluation.*;
/**
 *  Handle PM serialization, plugin loading, and other IO stuff
 *
 * @author  S Luz &#60;luzs@acm.org&#62;
 * @version <font size=-1>$Id: IOUtil.java,v 1.1.1.1 2005/05/26 13:59:30 amaral Exp $</font>
 * @see  
*/
public class IOUtil extends modnlp.util.IOUtil {

  public static final String PARSER_PLUGIN_BASE = "modnlp.tc.parser.";
  public static final String TSR_PLUGIN_BASE = "modnlp.tc.tsr.";

  public static void dumpProbabilityModel (TCProbabilityModel pm, String filename) 
  {
    try {
      FileOutputStream out = new FileOutputStream(filename);
      ObjectOutputStream s = new ObjectOutputStream(out);
      s.writeObject(pm);
      s.flush();
    }
    catch (Exception e){
      System.err.println("Error saving Probability Model"); 
      e.printStackTrace();
    }
  }

  public static TCProbabilityModel loadProbabilityModel (String filename)
  {
    try {
      FileInputStream in = new FileInputStream(filename);
      ObjectInputStream s = new ObjectInputStream(in);
      return (TCProbabilityModel)s.readObject();
    }
    catch (Exception e){
      System.err.println("Error reading Probability Model");
      e.printStackTrace();
    }
    return null;
  }


  public static void dumpCSVTable (CSVTable csvt, String filename) 
  {
    try {
      FileOutputStream out = new FileOutputStream(filename);
      ObjectOutputStream s = new ObjectOutputStream(out);
      s.writeObject(csvt);
      s.flush();
    }
    catch (Exception e){
      System.err.println("Error saving CSV table"); 
      e.printStackTrace();
    }
  }

  public static CSVTable loadCSVTable (String filename)
  {
    try {
      FileInputStream in = new FileInputStream(filename);
      ObjectInputStream s = new ObjectInputStream(in);
      return (CSVTable)s.readObject();
    }
    catch (Exception e){
      System.err.println("Error reading CSVTable");
      e.printStackTrace();
    }
    return null;
  }
  

  public static Parser loadParserPlugin(String plugin) throws Exception {
    ClassLoader cl = ClassLoader.getSystemClassLoader();
    String pc = PARSER_PLUGIN_BASE+plugin;
    return (Parser)cl.loadClass(pc).newInstance();
  }

  public static TermFilter loadTSRPlugin(String plugin) throws Exception {
    ClassLoader cl = ClassLoader.getSystemClassLoader();
    String fc = TSR_PLUGIN_BASE+plugin;
    return (TermFilter)cl.loadClass(fc).newInstance();
  }


  public static void main(String[] args) {
    try {
      TCProbabilityModel pm = loadProbabilityModel(args[0]);
      System.err.println("loading prob model...");
      //if (args.length > 1){
      //  WordScorePair[] ws = pm.getWordScoreArray();
      //  for (int i = 0; i < ws.length; i++)
      //    System.out.println(ws[i]);
      //}
      System.out.println(pm.getCreationInfo());
      if  (args.length > 1 && args[1].equals("-d")){
        dumpProbabilityModel(pm,args[2]);
      }
    }
    catch (Exception e){
      System.err.println("USAGE:");
      System.err.println(" IOUtil probabilitymodel -d dumpfile\n");
      System.err.println("SYNOPSIS:\n load probabilitymodel and print creation info.");
    }

  }


}
