/**
 *  (c) 2007 S Luz <luzs@acm.org>
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
package modnlp.util;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import org.xml.sax.*;
import org.xml.sax.helpers.*;
//import org.xml.sax.ext.*;
import javax.xml.parsers.*;
//import uk.co.wilson.xml.MinML2;
/**
 * Split an XML file into an "index file" + one or more "include
 * files" corresponding to a selected element of the original file.
 * 
 * The parameters on the command line are:
 * - filename: the file to be split (e.g. f.xml)
 * - split_element: the name of the element to go into the split files (e.g. speech)
 * - splitfile_dtd: the DTD for the split file (e.g. epi.dtd)
 *
 * You will need to create epi.dtd by copying the dtd for f.xml to
 * epi.dtd and adding an attribute <code>include</code> to the
 * split_element. For instance, add the following after the speech element:
 *  <code> <!ATTLIST speech include CDATA #IMPLIED> </code>
 *
 * Next you will need to create speech.dtd by copying the speech
 * element specification from the original dtd.
 *
 * @author  Saturnino Luz &#60;luzs@acm.org&#62;
 * @version <font size=-1>$Id: XMLSplitter.java,v 1.1.1.1 2005/05/26 13:59:30 amaral Exp $</font>
 * @see  NewsParser
*/

public class XMLSplitter extends DefaultHandler //implements LexicalHandler
{

  private int iDepth = 0;  // current depth on xml tree (for indentation) 
  private String rootfn;   // input filename minus extension
  private String filename;   // input filename minus extension
  private StringBuffer cdata = new StringBuffer();
  private boolean incOpened = false;
  private BufferedWriter idxWriter;
  private BufferedWriter incWriter;
  private int incCounter = 0;
  InputSource source;
  String lastElement = null;
  String encoding = "UTF-8";
  DecimalFormat dForm = new DecimalFormat("000");
  /**
   * name of element to be exported to include files and marked with
   * include attribute in the index file
   **/
  private String incElement;  

  /** 
   *  Set up parser
   */
  public  XMLSplitter(String fn, String ie, String dtd, String enc) throws Exception{
    encoding = enc;
    File f = new File(fn);
    filename = fn;
    this.rootfn = f.getName().substring(0,fn.indexOf('.'));
    //filename = rootfn+"-full.xml";
    //f.renameTo(new File(filename));
    this.idxWriter = 
      new BufferedWriter(new OutputStreamWriter(new FileOutputStream(rootfn+".hed"),encoding));
    idxWriter.write("<?xml version='1.0' encoding='UTF-8' standalone='no'?>\n");
    idxWriter.write("<!DOCTYPE ecpc_EP SYSTEM '"+dtd+"'>");
    this.incElement = ie;
  }
  
  public void startDocument(){
      System.err.println("PID:"+source.getPublicId()+"SID:"+source.getSystemId()+"ENC:"+source.getEncoding()+"NAM:");

  }

  /**
   * Callback method activated when an XML start element 'event' occurs
   *
   * @see org.xml.sax.HandlerBase
   */
  public void startElement (final String namespaceURI,
                              final String localName,
                              final String name,
                              final Attributes attl)
  {
    try {
      lastElement = name;
      String atts = attributeListToString(attl);
      if ( name.equals(incElement) ){
        String ifn = rootfn+"-"+dForm.format((long)incCounter++)+".xml";
        incWriter =  
          new BufferedWriter(new OutputStreamWriter(new FileOutputStream(ifn),encoding));
        incOpened = true;
        incWriter.write("<?xml version='1.0' encoding='UTF-8' standalone='no'?>\n");
        incWriter.write("<!DOCTYPE "+incElement+" SYSTEM '"+incElement+".dtd'>\n");
        incWriter.write("<"+name+atts+">");
        idxWriter.write("\n");
        for (int i = 0; i < iDepth; i++) {
          idxWriter.write(" ");
        }
        idxWriter.write("<"+name+atts+" include='"+ifn+"'/>");
      }
      else if (incOpened) {
        cdata.append("<"+name+atts+">");
      }
      else {
        idxWriter.write("\n");
        for (int i = 0; i < iDepth; i++) {
          idxWriter.write(" ");
        }
        idxWriter.write("<"+name+atts+">");
        iDepth++;
      }
    } catch (Exception e) {
      System.err.println(e);
			e.printStackTrace();
      System.exit(1);  
    }


  }
  
  /**
   * Callback method activated when an XML end element 'event' occurs
   *
   * @see org.xml.sax.HandlerBase
   */
  public void endElement (final String namespaceURI,
                            final String localName,
                            final String name)
  {
    try {
      if ( name.equals(incElement) ){
        incWriter.write(cdata+"</"+name+">");
        cdata = new StringBuffer();
        incWriter.close();
        incOpened = false;
        return;
      }
      if (incOpened)
        cdata.append("</"+name+">");
      else {
        iDepth--;
        String cd = cdata.toString().trim();
        int cdl = cd.length();
        if (cdl>60)
          idxWriter.write("\n"+cd);
        else if (cdl>0)
          idxWriter.write(cd);
        if (!lastElement.equals(name) || cdl>60 ){
          idxWriter.write("\n");
          for (int i = 0; i < iDepth  ; i++) {
            idxWriter.write(" ");
          }
        }
        idxWriter.write("</"+name+">");
        cdata = new StringBuffer();
      }
    } catch (Exception e) {
        System.err.println(e);
    }
  }
  
  
  /**
   * Callback for PCDATA events. Store text content from <code>BODY</code> tags.
   *
   * @param ch[] array containing XML PCDATA (text)
   * @param start where the text starts
   * @param length where it ends
   * @see org.xml.sax.HandlerBase
   */
  public void characters (char ch[], int start, int length)
  { 
    try {
      String cd = (new String(ch,start,length)).trim();
      if (cd.length()==0)
        return;
      cdata.append(cd+" ");
    } catch (Exception e) {
      System.err.println(e);
    }
  }
  
  public void endDocument() {
    try {
      System.err.println("end document");
      idxWriter.close();
    } catch (Exception e) {
      System.err.println(e);
    }
  }

 /**
   * Print a message for ignorable whitespace.
   *
   * @see org.xml.sax.DocumentHandler#ignorableWhitespace
   */
  public void ignorableWhitespace (char ch[], int start, int length)
  {
    //System.out.print("Ignorable Whitespace found ");
  }
  
  /**
   * Report all warnings, and continue parsing.
   *
   * @see org.xml.sax.ErrorHandler#warning
   */
  public void warning (SAXParseException exception)
  {
    System.err.print("Warning: " +
                   exception.getMessage() +
                   " (" +
                   exception.getSystemId() +
                   ':' +
                   exception.getLineNumber() +
                   ',' +
                   exception.getColumnNumber() +
                   ')' + "\n");
  }
  
  
  /**
   * Report all recoverable errors, and try to continue parsing.
   *
   * @see org.xml.sax.ErrorHandler#error
   */
  public void error (SAXParseException exception)
  {
    System.err.print("Recoverable Error: " +
                   exception.getMessage() +
                   " (" +
                   exception.getSystemId() +
                   ':' +
                   exception.getLineNumber() +
                   ',' +
                   exception.getColumnNumber() +
                   ')' + "\n");
  }
  
  /**
   * Report all fatal errors, and try to continue parsing.
   *
   * <p>Note: results are no longer reliable once a fatal error has
   * been reported.</p>
   *
   * @see org.xml.sax.ErrorHandler#fatalError
   */
  public void fatalError (SAXParseException exception)
  {
    System.err.print("Fatal Error: " +
                   exception.getMessage() +
                   " (" +
                   exception.getSystemId() +
                   ':' +
                   exception.getLineNumber() +
                   ',' +
                   exception.getColumnNumber() +
                   ')' + "\n");
  }

  public void processingInstruction(String target, String data)
    throws SAXException
  {
    System.err.println("TA"+target+"DA"+data);
  }

  public void skippedEntity(String name)
    throws SAXException
  {
    System.err.println("SK"+name);
  }

  public void notationDecl(String name, String publicId, String systemId)
    throws SAXException 
  {
    System.err.println(name+"--"+publicId+""+systemId);

  }

  public void unparsedEntityDecl(String name, String publicId, String systemId, String notationName)
    throws SAXException
  {
    System.err.println(name+"--"+publicId+""+systemId+"--"+notationName);
  }

  /////// LexicalHandler interface
  public void comment(char[] ch, int start, int length)
    throws SAXException
  {
    System.err.println("COM:"+new String(ch,start,length));                       
  }

  public void startCDATA()
  throws SAXException
  {
  }

  public void endCDATA()
  throws SAXException
  {
  }

  public void startEntity(String name)
  throws SAXException
  {
  }

  public void endEntity(String name)
  throws SAXException
  {
  }

  public void startDTD(String name, String publicId, String systemId)
  throws SAXException
  {
    System.err.println(name+"--"+publicId+""+systemId+"--"+systemId);
  }
    
  public void endDTD()
  throws SAXException
  {     
  }


  private String attributeListToString(Attributes attl){
    StringBuffer sb = new StringBuffer();
    int j = attl.getLength();
    for (int i = 0; i < j; i++) {
      sb.append(" "+attl.getQName(i)+"='"+attl.getValue(i)+"'");
    }
    return sb.toString();
  }

  public void  parse ()
  {
    try {
      FileInputStream in = new FileInputStream(filename);
      source = new InputSource(in);
      SAXParserFactory spf = 
        SAXParserFactory.newInstance();
      spf.setValidating(true);
      SAXParser sp = spf.newSAXParser();
      //ParserAdapter pa = 
      //  new ParserAdapter(sp.getParser());
      XMLReader pa = sp.getXMLReader();
      //XMLReader pa = XMLReaderFactory.createXMLReader();
      pa.setContentHandler(this);
      pa.setDTDHandler(this);
      pa.setErrorHandler(this);
      //pa.setProperty("/handlers/LexicalHandler",this);
      source.setEncoding(encoding);
      System.err.println("PID:"+source.getPublicId()+"SID:"+source.getSystemId()+"ENC:"+source.getEncoding());
      pa.parse(source);
      System.err.println("xml parsed ");
    }
    catch (Exception e) 
      {
        System.err.println("Error parsing "+filename);
        e.printStackTrace();
      }
  }

  public static void main(String[] args) {
    try {
      String splitel = args[0];
      String dtd = args[1];
      for (int i = 2; i < args.length; i++) {
        System.err.println("-->Splitting: "+args[i]);
        BufferedReader b = new BufferedReader(new InputStreamReader(new FileInputStream(args[i])));
        String l = b.readLine();
        b.close();
        int s = l.indexOf("encoding=")+10;
        int e1 = l.indexOf('\'',s);
        int e2 = l.indexOf('"',s);
        int e = e1 < e2 && e1 > 0 ? e1 : e2; 
        String encoding = l.substring(s,e);
        XMLSplitter xs = new XMLSplitter(args[i], splitel, dtd, encoding);
        xs.parse();
      }
    } catch (Exception e) {
      System.err.println(e+" Usage: XMLSplitter filename split_element splitfiledtd");
			e.printStackTrace();
      System.exit(1);
    }
  }
}

