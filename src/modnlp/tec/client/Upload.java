/** 
 *  (c) 2009 S Luz <luzs@acm.org>
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
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;


/**
 *  Upload saved concordance list
 *
 * 
 * @author  S. Luz &#60;luzs@acm.org&#62;
 * @version <font size=-1>$Id: Download.java,v 1.1.1.1 2000/07/07 16:54:36 luz Exp $</font>
 * @see  
*/
public class Upload extends BufferedReader {
  ConcordanceVector concVector = new ConcordanceVector();

  public Upload(File f) throws java.io.IOException
  {
    super(new FileReader(f)); 
  }

  public void readConcordances() throws java.io.IOException
  {
    String concordance = null;
    while ((concordance = readLine()) != null)
      {
        int fne = concordance.indexOf('|');
        int hce = concordance.indexOf('|',fne+1);
        String fn = concordance.substring(0,fne);
        if (concVector.getHalfConcordance() == 0)
          concVector.setHalfConcordance((new Integer(concordance.substring(fne+1,hce))).intValue());
        String cl = concordance.substring(hce+1);
        concVector.add(fn+"|0|"+cl);
      }
    this.close();
  }

  public final ConcordanceVector getConcordanceVector(){
    return concVector;
  }


}
