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
package modnlp.tec.client.gui.event;

import java.util.EventObject;

public class ConcordanceListSizeEvent extends EventObject{
  
  private int noFound;

  /**
   *  Events raised on ConcordanceDisplayListener's to notify them of 
   *  concordance list changes (and request the appropriate display updates.
   * 
   * @author  Saturnino Luz &#60;luzs@acm.org&#62;
   * @version <font size=-1>$Id: ConcordanceListSizeEvent.java,v 1.1 2000/07/11 18:06:49 luz Exp $</font>
   * @see  
   */
  public ConcordanceListSizeEvent(Object source, int noFound)
  {
    super(source);
    this.noFound = noFound;
  }

  /**
   *  Return the index of the first object (of a concordance array)
   *  to be displayed by a ConcordanceDisplayListener.
   *
   * @return the index from which  concordance list objects should 
   *         be displayed.
   */
  public int getNoFound() { return noFound; }

  /**
   * Returns a string that displays and identifies this
   * object's properties.
   *
   * @return a String representation of this object
   */
  public String toString() {
    String properties = 
	    " source=" + getSource() +  
      " noFound= " + noFound + 
      " ";
    return getClass().getName() + "[" + properties + "]";
  }
}

