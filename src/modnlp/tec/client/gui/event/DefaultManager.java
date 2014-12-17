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
 *  Objects implementing this interface 
 *  should manage defaults (preferences) and generated
 *  the appropriate events when preferences change. 
 * 
 * @author  Saturnino Luz &#60;luzs@acm.org&#62;
 * @version <font size=-1>$Id: DefaultManager.java,v 1.2 2000/07/11 18:06:49 luz Exp $</font>
 * @see  PreferPanel
 * @see  ListDisplay
*/
public interface DefaultManager {

  /**
   * Set obj as a default display listener (that is, an object
   * which will be notified of default changes. 
   * 
   */
  public void addDefaultChangeListener(DefaultChangeListener obj);

  public void removeDefaultChangeListener(DefaultChangeListener obj);


}
