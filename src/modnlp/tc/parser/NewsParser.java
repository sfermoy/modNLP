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
package modnlp.tc.parser;

import modnlp.tc.dstruct.*;
/**
 * Parse a Reuters file and store the results as a ParsedCorpus object.
 * 
 * @author  Saturnino Luz &#60;luzs@acm.org&#62;
 * @version <font size=-1>$Id: NewsParser.java,v 1.1.1.1 2005/05/26 13:59:30 amaral Exp $</font>
 * @see  TypeListHandler
*/

public class NewsParser extends Parser
{

  /** 
   * parseNews: Set up parser object, perform parsing
   */
  public void  parse ()
  {
    try {
      XMLHandler xh = new XMLHandler(filename);
      xh.parse();
      System.err.println("xml parsed ");
      parsedCorpus = xh.getParsedCorpus();
    }
    catch (Exception e) 
      {
        System.err.println("Error parsing "+filename);
        e.printStackTrace();
      }
  }
  
  /**
   *  main method for test purposes. 
   */
  public static void main(String[] args) {
    try {
      NewsParser f = new NewsParser();
      f.setFilename(args[0]);
      System.out.println(f.getParsedCorpus());
    }
    catch (Exception e){
      System.err.println("modnlp.tc.parser.NewsParser: ");
      System.err.println("Usage: NewsParser FILENAME");
      //e.printStackTrace();
    } 
  }  

}

