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
package modnlp.tec.client;
import java.io.IOException;
import java.io.InputStream;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.AttributeList;
import org.xml.sax.Attributes;
import org.xml.sax.ErrorHandler;
//import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;



/**
 *  
 *
 * 
 * @author  Saturnino Luz &#60;luzs@acm.org&#62;
 * @version <font size=-1>$Id: HeaderXMLHandler.java,v 1.2 2003/08/06 16:58:56 luzs Exp $</font>
 * @see  
*/


public class HeaderXMLHandler extends DefaultHandler
{

  StringBuffer content = new StringBuffer(1000);
  int error = 0;
  private String elementName = "";
  private String elementAttribs = "";
  private StringBuffer elementContent = new StringBuffer("");
  private int depthContext = -2;
  private static String[] ignorableArray =  {"","teiHeader"};
  private SAXParser parser;
  private String defSpacer = "<div style=\"margin-left: 2px;\">";
  private String spacer = "";
  private int tab = 20;
  private String Highlight;
  String bgcolor ="";

  public HeaderXMLHandler(String section) throws ParserConfigurationException,
                                   SAXException
  {
    super();
    Highlight = section;
    SAXParserFactory spf = SAXParserFactory.newInstance();
    spf.setValidating(false);
    spf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false); 
    parser = spf.newSAXParser();
  }

  public void parse (InputStream is) throws SAXException,
                  IOException
  {
    parser.parse(is, this);
  }
  public void startElement (final String namespaceURI,
                              final String localName,
                              final String name,
                              final Attributes atts) 
  {
     
    if ( !ignorableElement(name) )
      {
          if(depthContext < 0)
             spacer = "<div style=\""+bgcolor+"margin-left: "+tab*(depthContext+2)+"px;\">";
          else{
              if(depthContext == 0)
                {
                    spacer="<div style=\""+bgcolor+"margin-left: "+tab*(depthContext+2)+"px;\">";
                }
              else{
                  spacer="<div style=\""+bgcolor+"margin-left: "+tab*(depthContext+2)+"px;\">";
              }
          }           
//        for (int i = 0; i < depthContext; i++)
//          content.append(spacer);
        String at = "";
        if (elementAttribs != null) {
          //at = " "+spacer+elementAttribs;
          at = ""+elementAttribs.replace(",", "<br>");
          if(at.contains("id")){
              spacer=spacer.replace(bgcolor, "");
              bgcolor="";
          }
          at = at.replace("id: "+ Highlight+"<br>", "<font color=red>"+ "#id: "+ Highlight+"</font>"+"<br>");
          if(at.contains("#id")){
              bgcolor = "background-color: #FFFF00;";
              spacer = "<div style=\"background-color: #FFFF00;margin-left: "+tab*(depthContext+2)+"px;\">";
          }
        }
        content.append(spacer);
        if(at.equals(""))
            content.append("<b>"+fixElementName(elementName)+": " +"</b>"+ 
                       elementContent +"</div>");
        else
            content.append("<b>"+fixElementName(elementName)+": " +"</b>"+"<div style=\""+bgcolor+"margin-left: "+tab+"px;\">"+at+"</div>" + 
                           elementContent +"</div>");
        //System.out.println("Content: |" + elementContent + "|");
      } 
    depthContext++;
    elementName = name;
    if (atts !=null && atts.getLength()> 0)
      elementAttribs = formatAttributes(atts);
    else {
      elementAttribs = null;
    }
    elementContent = new StringBuffer("");
  }
  
  public void endElement (final String namespaceURI,
                            final String localName,
                            final String name)
  {
    if ( !ignorableElement(name)  )
      {
         
          if(depthContext < 0)
             spacer = "<div style=\""+bgcolor+"margin-left: "+tab*(depthContext+2)+"px;\">";
          else{
              if(depthContext == 0)
                {
                    spacer="<div style=\""+bgcolor+"margin-left: "+tab*(depthContext+2)+"px;\">";
                }
              else{
                  spacer="<div style=\""+bgcolor+"margin-left: "+tab*(depthContext+2)+"px;\">";
              }
          }
          content.append(spacer);
//        for (int i = 0; i < depthContext; i++)
//          content.append(spacer);
        String at = "";
       
        if (elementAttribs != null) {
           // at = " "+spacer+elementAttribs;
           at = ""+elementAttribs.replace(",", "<br>");
        }
        if(at.equals(""))
            content.append("<b>"+fixElementName(elementName)+": " +"</b>"+ 
                       elementContent +"</div>");
        else
            content.append("<b>"+fixElementName(elementName)+": " +"</b>"+"<div style=\""+bgcolor+"margin-left: "+tab+"px;\">"+at+"</div>" + 
                           elementContent +"</div>");
        //System.out.println("Content: |" + elementContent + "|");
      } 
    depthContext--;
    elementName = "";
    elementContent = new StringBuffer("");
  }
  
  public void characters (char ch[], int start, int length)
  {
    for (int i = start; i < start + length; i++) {
      if ( !Character.isISOControl(ch[i]) )
        elementContent.append(ch[i]);
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
    error++;
    content.append("Warning: " +
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
    error++;
    content.append("Recoverable Error: " +
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
    error++;
    content.append("Fatal Error: " +
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
   *  Utilities
   */

  private  boolean ignorableElement(String name) 
  {

    for (int i = 0; i < ignorableArray.length; i++)
      if ( elementName.equals(ignorableArray[i]) )
        return true;
    
    if ( elementName.equals(name)  
         &&  elementContent.toString().equals("") )
      return true;
    else 
      return false;
  }
  
  private String fixElementName(String name) {
    
    StringBuffer out = new StringBuffer();
    out.append( Character.toUpperCase(name.charAt(0)) );
    for (int i = 1; i <  name.length(); i++ )  
      if ( Character.isUpperCase(name.charAt(i)) ) 
        out.append(" " + name.charAt(i) );
      else
        out.append( name.charAt(i) );
    
    return out.toString();
  }
  
  public StringBuffer getContent(){
    return content;
  }

  private final String formatAttributes (Attributes atts){
    int l = atts.getLength();
    StringBuffer sb = new StringBuffer("");
    if (l > 0) {
      sb.append(atts.getLocalName(0)+": "+atts.getValue(0));
    }
    for (int i = 1; i < l; i++) {
      sb.append(", "+atts.getLocalName(i)+": "+atts.getValue(i));
    }
    return sb.toString();
  }

}


