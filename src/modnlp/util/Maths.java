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
package modnlp.util;
/**
 *  Wrapper class maths
 *
 * @author  S Luz &#60;luzs@acm.org&#62;
 * @version <font size=-1>$Id: Maths.java,v 1.1.1.1 2005/05/26 13:59:30 amaral Exp $</font>
 * @see  
*/
public class Maths {

  public static double log2 (double ln) {
    return Math.log(ln)/Math.log(2D);
  }

  public static double safeLog2 (double ln) {
    if ( ln == 0 || Double.isNaN(ln) || Double.isInfinite(ln) )
      return 0;
    return Math.log(ln)/Math.log(2D);
  }

  public static double log (double ln, double base) {
    return Math.log(ln)/Math.log(base);
  }

  public static double safediv (double dividend, double divisor) {
    return divisor == 0 ? 0 : dividend / divisor;
  }


  /**
   * Safe  "x * log y"  with 0 log 0 =def 0,  for use computing info theoretic metrics 
   */
  public static double xTimesLog2y(double x, double y) {  
    if ( y == 0 || Double.isNaN(y))
      return 0;
    return x * log2(y);
  }
}
