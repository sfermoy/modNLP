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
package modnlp.tc.tsr;

import modnlp.dstruct.Probabilities;
import modnlp.util.Maths;


/**
 * 
 * Term Space Reduction by GSS coefficient filtering (see
 * dimreduct-4up.pdf and termextract-4up.pdf [4ict2 web page])
 * 
 * @author  S. Luz &#60;luzs@acm.org&#62;
 * @version <font size=-1>$Id: GSScoefficient.java,v 1.1.1.1 2005/05/26 13:59:30 amaral Exp $</font>
 * @see  GenerateARFF
*/

public class GSScoefficient extends TermFilter
{


  public double computeLocalTermScore(String term, String cat){
    Probabilities p = pm.getProbabilities(term, cat);
    return (p.tc * p.ntnc) - (p.tnc * p.ntc);
  }

}
      
