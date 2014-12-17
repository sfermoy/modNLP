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

import modnlp.dstruct.CorpusList;
import modnlp.idx.inverted.TokeniserRegex;
import modnlp.dstruct.TokenMap;
import modnlp.idx.database.Dictionary;
import modnlp.idx.database.SubcorpusDirectory;
import modnlp.idx.database.DictProperties;
import modnlp.idx.database.AlreadyIndexedException;

import java.io.File;
import java.util.Enumeration;

/**
 *  Print dictionary tables onto stdout (for testing purposes)
 *
 * @author  S Luz &#60;luzs@acm.org&#62;
 * @version <font size=-1>$Id: Dump.java,v 1.1 2006/05/22 17:26:02 amaral Exp $</font>
 * @see  
*/
public class Dump {
  private static boolean verbose = true;

  public static void main(String[] args) {
    Dictionary d = null;
    try {
      d = new Dictionary(new DictProperties(args[0])); 
      d.dump();
      d.printSortedFreqList(new java.io.PrintWriter(System.out));
      SubcorpusDirectory s = new SubcorpusDirectory(d);
      s.dump();
      d.close();
    } // end try
    catch (Exception ex){
      if (d != null)
        d.close();
      System.err.println(ex);
      ex.printStackTrace();
      usage();
    }
  }



  public static void usage() {
    System.err.println("\nUSAGE: modnlp.idx.Dump ");
    System.err.println("\tprint all modnlp.idx.Dictionary tables to stdout");
  }
}
