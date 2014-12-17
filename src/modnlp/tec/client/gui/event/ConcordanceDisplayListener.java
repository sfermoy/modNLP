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


/**
 *  Listen for list display events (e.g. changes on range of elements of 
 * the concordance array to be displayed) 
 * 
 * @author  Saturnino Luz &#60;luzs@acm.org&#62;
 * @version <font size=-1>$Id: ConcordanceDisplayListener.java,v 1.3 2000/07/11 18:06:49 luz Exp $</font>
 * @see  
*/
public interface ConcordanceDisplayListener {

  /**
   * Event raised (typically by a ConcordanceMonitor) to indicate
   * that a concordance list has changed in a way that requires 
   * its displayed form to be updated.
   *
   * @param e a concordance list event 
   */
  public void concordanceChanged(ConcordanceDisplayEvent e);

  public void concordanceChanged(ConcordanceListSizeEvent e);

}
