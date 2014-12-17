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
import modnlp.idx.database.DictProperties;
import modnlp.idx.query.WordQuery;

import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.PrintStream;


/**
 *  Command line interface for querying the index for concordances. 
 *
 * @author  S Luz &#60;luzs@acm.org&#62;
 * @version <font size=-1>$Id: Query.java,v 1.1 2006/05/22 17:26:02 amaral Exp $</font>
 * @see  modnp.tec.client.Browser (with -standalone option) for a proper GUI 
*/
public class Query {
  private static boolean verbose = true;

  public static void main(String[] args) {
    Dictionary d = null;
    try {
      d = new Dictionary(new DictProperties(args[0]));
      if (args[1].equals("-f")){ // freq list
        d.printSortedFreqList(new java.io.PrintWriter(System.out));
      }
      else if (args[1].equals("-q")) { // interactive query
          BufferedReader cline 
            = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("Enter query or press <ENTER> to quit.\n> ");
        String req = cline.readLine();
        while ( ! req.equalsIgnoreCase("") ) {          
          System.err.println("No. of concordances for "+req+": ");
          d.printConcordances(new WordQuery(req,d,false), 50, true, new java.io.PrintWriter(System.out));
          System.out.print("\n> ");
          System.out.flush();
          req = cline.readLine();
        }
      }
      else if (args[1].equals("-l")) { // one-off query
        String req = args[2];
        System.err.println("No. of concordances for "+req+": ");
        d.printConcordances(new WordQuery(req,d,false), 50, 
                            true, new java.io.PrintWriter(System.out));
        System.out.flush();
      }
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
    System.err.println("\nUSAGE: Query corpus-directory options");
    System.err.println("\tprint modnlp.idx.Dictionary tables to stdout");
    System.err.println("\tOptions:");
    System.err.println("\t\t-f       print frequency list");
    System.err.println("\t\t-q       interactive query");
    System.err.println("\t\t-l QUERY query dictionary and quit.");
  }
}
