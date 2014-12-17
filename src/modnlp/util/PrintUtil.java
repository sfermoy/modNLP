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

import modnlp.dstruct.WordFrequencyPair;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

/**
 *   Print and display utilities for the command line interface
 *
 * @author  S Luz &#60;luzs@acm.org&#62;
 * @version <font size=-1>$Id: PrintUtil.java,v 1.2 2006/05/22 17:26:02 amaral Exp $</font>
 * @see  
*/

public class PrintUtil {

  private static int prevsize = 0;


  public static String toString(boolean[] ba){
    StringBuffer sb = new StringBuffer();
    for (int i = 0; i < ba.length ; i++)
      sb.append(ba[i]+",");
    if (sb.length() < 1)
      return "";
    return sb.substring(0,sb.length()-1);
  }

  public static String toString(byte[] ba){
    StringBuffer sb = new StringBuffer();
    for (int i = 0; i < ba.length ; i++)
      sb.append(ba[i]+",");
    if (sb.length() < 1)
      return "";
    return sb.substring(0,sb.length()-1);
  }

  public static String toString(int[] ba){
    StringBuffer sb = new StringBuffer();
    for (int i = 0; i < ba.length ; i++)
      sb.append(ba[i]+",");
    if (sb.length() < 1)
      return "";
    return sb.substring(0,sb.length()-1);
  }

  public static String toString(long[] ba){
    StringBuffer sb = new StringBuffer();
    for (int i = 0; i < ba.length ; i++)
      sb.append(ba[i]+",");
    if (sb.length() < 1)
      return "";
    return sb.substring(0,sb.length()-1);
  }

  public static String toString(double[] ba){
    StringBuffer sb = new StringBuffer();
    for (int i = 0; i < ba.length ; i++)
      sb.append(ba[i]+",");
    if (sb.length() < 1)
      return "";
    return sb.substring(0,sb.length()-1);
  }

  public static String toString(String[] ba){
    StringBuffer sb = new StringBuffer();
    for (int i = 0; i < ba.length ; i++)
      sb.append(ba[i]+",");
    if (sb.length() < 1)
      return "";
    return sb.substring(0,sb.length()-1);
  }

  public static String toString(WordFrequencyPair[] wfp){
    StringBuffer sb = new StringBuffer();
    for (int i = 0; i < wfp.length ; i++)
      sb.append(wfp[i].getWord()+",");
    if (sb.length() < 1)
      return "";
    return sb.substring(0,sb.length()-1);
  }

  public static String toString(Vector v){
    StringBuffer sb = new StringBuffer();
    for (Enumeration e = v.elements() ; e.hasMoreElements() ;)
      sb.append((String)e.nextElement()+",");
    if (sb.length() < 1)
      return "";
    return sb.substring(0,sb.length()-1);
  }

  public static String toString(Set v){
    StringBuffer sb = new StringBuffer();
    for (Iterator e = v.iterator() ; e.hasNext() ;)
      sb.append((String)e.next()+",");
    if (sb.length() < 1)
      return "";
    return sb.substring(0,sb.length()-1);
  }

  public static void resetCounter ()
  {
    prevsize = 0;
  }

  public static void donePrinting(){
    System.err.println("...done");
    resetCounter();
  }

  public static void printNoMove(String header, int counter)
  {
    if ( prevsize == 0 )
      System.err.print(header+counter);
    else {
      for ( int i = 0; i < prevsize ; i++)
        System.err.print("\b");
      System.err.print(counter);
    }
    prevsize = (new String(counter+"")).length();
    //for (int i = 0 ; i < 100000; i++)
    //  System.out.print("");
  }


}
