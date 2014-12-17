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
package modnlp.dstruct;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import java.io.IOException;
import java.util.Vector;
/**
 *  List of filenames (full path) in the corpus
 *
 * @author  S. Luz &#60;luzs@acm.org&#62;
 * @version <font size=-1>$Id: CorpusList.java,v 1.2 2006/05/22 17:26:02 amaral Exp $</font>
 * @see  
*/
public class CorpusList extends  Vector
{

  public CorpusList (String flist)
  {
    super();
    try {
      BufferedReader in
        = new BufferedReader(new FileReader(flist));
      String fname = null;
      while ( (fname = in.readLine()) != null )
        {
          if (fname.charAt(0) == '#')
            continue;
          this.add(fname);
        }
    }
    catch (IOException e){
      System.err.println("Error reading corpus list "+flist);
      e.printStackTrace();
    }
  }

  public CorpusList (File[] flist) 
  {
    super();
    for (int i = 0; i < flist.length; i++) {
      this.add(flist[i].toString());
    }
  }

  public CorpusList (Object[] flist) 
  {
    super();
    for (int i = 0; i < flist.length; i++) {
      this.add(flist[i].toString());
    }
  }

}



