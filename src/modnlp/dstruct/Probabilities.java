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
/**
 *  Record a 4-entry joint probability table for (Boolean) random vars
 *  term and category as well as the priors for p(term) and
 *  p(category)
 *
 * @author  S Luz &#60;luzs@acm.org&#62;
 * @version <font size=-1>$Id: Probabilities.java,v 1.1.1.1 2005/05/26 13:59:30 amaral Exp $</font>
 * @see  
*/
public class Probabilities {
  public double tc;
  public double tnc;
  public double ntc;
  public double ntnc;
  public double t;
  public double c;

  public Probabilities(double pT,
                       double pC,
                       double pTAndC, 
                       double pTAnd_C, 
                       double p_TAndC, 
                       double p_TAnd_C
                       )
  {
    this.tc = pTAndC;
    this.tnc = pTAnd_C;
    this.ntc = p_TAndC;
    this.ntnc = p_TAnd_C;
    this.t = pT;
    this.c = pC;
  }

  public double getPTgivenC(){
    return  tc / c;
  }

  public double getPTgiven_C(){
    return  tnc / (1 - c);
  }  

  public String toString(){
    return 
      " p(t,c) = "+tc+
      " p(t,-c) = "+tnc+
      " p(-t,c) = "+ntc+
      " p(-t,-c) = "+ntnc; 
 }

}
