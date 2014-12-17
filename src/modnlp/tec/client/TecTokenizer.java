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
import java.lang.*;
import java.util.StringTokenizer;
/**
 *  A string tokenizer with a safe nextToken method 
 *
 * 
 * @author  Saturnino Luz &#60;luzs@acm.org&#62;
 * @version <font size=-1>$Id: TecTokenizer.java,v 1.1.1.1 2000/07/07 16:54:36 luz Exp $</font>
 * @see  
*/
public class TecTokenizer 
  extends StringTokenizer
{

  public TecTokenizer(String str) {
    super(str);
  }
  
  public TecTokenizer(String str, String delim){
    super(str, delim);
  } 
  public TecTokenizer(String str, String delim, boolean returnTokens)
  {
    super(str, delim, returnTokens);
  }


  /** Return next token, if present. Empty string, otherwise.
   *
   */ 
  public String safeNextToken (){
    if ( this.hasMoreTokens() )
      return this.nextToken();
    else 
      return "";
  }

}
