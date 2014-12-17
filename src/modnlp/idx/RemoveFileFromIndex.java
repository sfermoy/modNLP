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
import modnlp.idx.database.NotIndexedException;

import java.io.File;
import java.util.Enumeration;

/**
 *  Read input file(s) and create case, occurrence, position and
 *  frequency tables for use with TEC
 *
 * @author  S Luz &#60;luzs@acm.org&#62;
 * @version <font size=-1>$Id: RemoveFileFromIndex.java,v 1.2 2006/05/22 17:26:02 amaral Exp $</font>
 * @see  
*/
public class RemoveFileFromIndex {
  private static boolean verbose = true;


  public static void main(String[] args) {
    Dictionary d = null;
    try {
      d = new Dictionary(true); 
      // RemoveFileFromIndex mti = new RemoveFileFromIndex();
      CorpusList clist =  new CorpusList(args[0]);
      for (Enumeration e = clist.elements(); e.hasMoreElements() ;) {
        try {
          String fname = (String)e.nextElement();
          if (verbose) {
            System.err.print("\n----- De-indexing: "+fname+" ------\n");
          }
          d.removeFromDictionary(fname);
        }
        catch (NotIndexedException ex){
          System.err.println("Warning: "+ex);
          System.err.println("Ignoring this entry.");
        }
      } // end for
      d.dump();
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
    System.err.println("\nUSAGE: RemoveFileFromIndex REMOVE_LIST ");
    System.err.println("\tRemove each file in REMOVE_LIST from index");
  }
}
