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

public class ConcordanceDisplayEvent extends EventObject{
  
  protected int firstIndex;
  protected String message;
  protected int eventType = CHANGE_EVT;
  public static final int CHANGE_EVT = 0;
  public static final int FIRSTDISPLAY_EVT = 1;
  public static final int DOWNLOADCOMPLETE_EVT = 2;
  public static final int DOWNLOADSTATUS_EVT = 3;

  /**
   *  Events raised on ConcordanceDisplayListener's to notify them of 
   *  concordance list changes (and request the appropriate display updates.
   * 
   * @author  Saturnino Luz &#60;luzs@acm.org&#62;
   * @version <font size=-1>$Id: ConcordanceDisplayEvent.java,v 1.1.1.1 2000/07/07 16:54:36 luz Exp $</font>
   * @see  
   */
  public ConcordanceDisplayEvent(Object source, int firstIndex)
  {
    super(source);
    this.firstIndex = firstIndex;
  }

  public ConcordanceDisplayEvent(Object source, int firstIndex, int evt, String msg) 
    throws IndexOutOfBoundsException {
    super(source);
    if (!isValidEventType(evt))
      throw new IndexOutOfBoundsException("Invalid ConcordanceDisplayEvent type specified");
    this.firstIndex = firstIndex;
    this.eventType = evt;
    this.message = msg;
  }

  private boolean isValidEventType (int evt){
    if (evt < 0 || evt > 3)
      return false;
    else
      return true;
  }

  /**
   *  Return the index of the first object (of a concordance array)
   *  to be displayed by a ConcordanceDisplayListener.
   *
   * @return the index from which  concordance list objects should 
   *         be displayed.
   */
  public int getFirstIndex() { return firstIndex; }

  public int getEventType() { return eventType; }

  public String getMessage() { return message; }


  /**
   * Returns a string that displays and identifies this
   * object's properties.
   *
   * @return a String representation of this object
   */
  public String toString() {
    String properties = 
	    " source=" + getSource() +  
      " firstIndex= " + firstIndex + 
      " ";
    return getClass().getName() + "[" + properties + "]";
  }
}

