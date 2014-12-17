/**
 *  (c) 2010 S Luz <luzs@acm.org>
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
 *  Token object, recodring surface form plus position on string.
 *
 * @author  S Luz &#60;luzs@acm.org&#62;
 * @version <font size=-1>$Id: $</font>
 * @see  
*/

public class Token
{
  public String str;
  public long start;
  public long end;
  
  public boolean equals(Object obj)
  {
    if (!(obj instanceof Token)) {
      return false;
    }
    
    Token another = (Token)obj;
    return ((str == null ? another.str == null : str.equals(another.str)) &&
            (start == another.start) && (end == another.end));
  }
  
  public String toString()
  {
    return ("(" +
            str + "," +
            Long.toString(start) + "," +
            Long.toString(end) + ")");
  }
}

