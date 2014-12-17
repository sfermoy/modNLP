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
import java.io.*;
/**
 *  This class implements the most basic functionality of a tc.parser
 *  object: read the content of a file, assign it a category and a
 *  unique ID. This class will tipically be extended, and the
 *  <code>parse()</code> method will tipically b overridden. 
 *
 * @author  S Luz &#60;luzs@acm.org&#62;
 * @version <font size=-1>$Id: Parser.java,v 1.1.1.1 2005/05/26 13:59:30 amaral Exp $</font>
 * @see NewsParser
 * @see LingspamEmailParser
*/
public class Parser {

  String filename = null;
  ParsedCorpus parsedCorpus = null;

  /**
   * Create a new <code>Parser</code> instance. This will usually be
   * followed by a call to setFilename(). This constructor takes no
   * parameters so that the class can be loaded and instantiated
   * dynamically (as a plugin) by MakeProbabilityModel etc.
   *
   * @see modnlp.tc.induction.MakeProbabilityModel
   */
  public Parser() {
  }

  /** 
   * Set name of file to be parsed 
   */
  public void setFilename (String fn){
    this.filename = fn;
  }

  /**
   * Get parsed corpus, parsing filename if necessary
   */
  public ParsedCorpus getParsedCorpus () {
    if (parsedCorpus == null)
      parse();
    return parsedCorpus;
  }

  /**
   * Process ('parse') input file <code>filename</code> and add its
   * contents literally to parsedCorpus wih ID and category =
   * <code>filename</code>. This method isn't likely to be very
   * useful, so subclasses will tipically  override it
   * (e.g. NewsParser).
   *
   * @see NewsParser
   */
  public void parse() {
    parsedCorpus = new ParsedCorpus();
    ParsedDocument pni = new ParsedDocument();
    try{
      File f = new File(filename);
      pni.setId(filename);
      pni.addCategory(filename);
      BufferedReader in
        = new BufferedReader(new FileReader(filename));
      String line = null;
      while ( (line = in.readLine()) != null )
        {
          pni.addText(line);
        }
      parsedCorpus.addParsedDocument(pni);
    }
    catch (Exception e) 
      {
        System.err.println("Error parsing "+filename);
        e.printStackTrace();
      }
  }
  //abstract void parse();

}
