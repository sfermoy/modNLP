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

import java.util.regex.Matcher;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.File;
import modnlp.tc.dstruct.ParsedDocument;
import modnlp.tc.dstruct.ParsedCorpus;
import java.util.regex.Pattern;


/**
 * Parse the 20 newsgroups corpus files and store the results as a ParsedCorpus object.
 * 
 * The corpus is available at
 *  
 * http://qwone.com/~jason/20Newsgroups/
 * 
 * The 20NG by date was used for testing.
 *
 * @author  Saturnino Luz &#60;luzs@acm.org&#62;
 * @version <font size=-1>$Id: LingspamEmailParser.java,v 1.1.1.1 2013/05/26 13:59:30 amaral Exp $</font>
 * @see  
*/

public class P20newsgroupsParser extends Parser
{

  static final Pattern catPattern = Pattern.compile(".*?([a-z.]+)/[0-9]+");

  /** 
   * parse: Set up parser object, perform parsing
   *
   * The category not stored with the text but given by the path name
   * (e.g. alt.atheism/5346 for category 'alt.atheism')
   */
  public void  parse ()
  {
    parsedCorpus = new ParsedCorpus();
    ParsedDocument pni = new ParsedDocument();
    try {
      File f = new File(filename);
      String namenopath = f.getName();
      pni.setId(namenopath);
      pni.addCategory(getCategory(filename));
      BufferedReader in
        = new BufferedReader(new FileReader(filename));
      String line = null;
      boolean sttext = false;
      Pattern header = Pattern.compile("^.+:.*");
      while ( (line = in.readLine()) != null )
        {
          if (!sttext && header.matcher(line).matches())
            continue;
          sttext = true;
          pni.addText(line);
        }
      parsedCorpus.addParsedDocument(pni);
      //System.out.println(parsedCorpus);
    }
    catch (Exception e) 
      {
        System.err.println("Error parsing "+filename);
        e.printStackTrace();
      }
  }
  
  /**
   * In lingspam, the category (spam or legit) is not stored with
   * the text but given by the file name ('spmmsg*.txt denote spam.)
   */
  public static String getCategory(String fname){
    Matcher m = catPattern.matcher(fname);
    //System.out.println(fname+"--"+m);
    if (m.matches())
      return m.group(1);
    else
      return "NOCAT";
  }


  /**
   *  main method for test purposes.  
   *
   * Usage:P20newsgroupsParser  FILENAME
   *
   * 
   */
  public static void main(String[] args) {
    try {
      P20newsgroupsParser f = new P20newsgroupsParser();
      f.setFilename(args[0]);
      System.out.println(f.getParsedCorpus());
    }
    catch (Exception e){
      System.err.println("modnlp.tc.parser.P20newsgroupsParser: ");
      System.err.println("Usage: P20newsgroupsParser FILENAME");
      //e.printStackTrace();
    } 
  }  


}

