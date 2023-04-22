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

import java.io.PrintWriter;
import java.io.IOException;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.File;
import java.util.Iterator;

/**
 *  Download concordance list
 *
 * 
 * @author  S. Luz &#60;luzs@acm.org&#62;
 * @version <font size=-1>$Id: Download.java,v 1.1.1.1 2000/07/07 16:54:36 luz Exp $</font>
 * @see  
*/
public class Download extends PrintWriter {

  private String keyword = "";
  
  public Download(String fname) throws java.io.IOException
  {
    super(new BufferedWriter(new FileWriter(fname))); 
  }

  public Download(File file) throws java.io.IOException
  {
    super(new BufferedWriter(new FileWriter(file))); 
  }

  public void dumpConcordance(ConcordanceVector conc) throws java.io.IOException
  {
    int lfn = conc.getLengthLongestFname();
    if ( conc == null )
      return;
    this.println(keyword);
    this.println(conc.getSize());
    for (Iterator<ConcordanceObject> p = conc.iterator(); p.hasNext(); )
      {
        ConcordanceObject co = p.next();
        //String fn = conc.concArray[count].filename;
        //String offs = adjustOffSet(lfn,fn.length());
        //this.println(fn+offs+"|"+conc.concArray[count].concordance);
        this.println(co.csvConcLine());
      }
    this.close();
  }

  public void setKeyword(String s){
    keyword = s;
  }
  
  public String adjustOffSet(int maxs, int size){

    char[] auxA = new char[maxs-size];
    for(int i = 0; i < (maxs-size) ; i++)
      auxA[i] = ' ';
    
    return new String(auxA);
  }

}
