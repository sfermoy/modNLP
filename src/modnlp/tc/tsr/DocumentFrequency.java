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
import modnlp.util.PrintUtil;

import java.io.*;
import java.util.Arrays;

/**
 * 
 * A simple implementation of Term Space Reduction for Text
 * Categorisation based on frequency of documents in which a given
 * feature occurs...
 * 
 * @author  Saturnino Luz &#60;luzs@acm.org&#62;
 * @version <font size=-1>$Id: DocumentFrequency.java,v 1.1.1.1 2005/05/26 13:59:30 amaral Exp $</font>
 * @see  IndentationHandler
*/

public class DocumentFrequency extends TermFilter
{

  public double computeLocalTermScore(String term, String cat){
    Probabilities p = pm.getProbabilities(term, cat);
    return p.tc * ii.getCorpusSize();
  }

  public void computeGlobalDocFrequency() {
    ii.setFreqWordScoreArray(wsp);
  }

}
