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
package modnlp.tec.client.gui;
import java.awt.*;
import java.lang.*;
/**
 * Object that pops up whenever the user does
 * something she shouldn't have done.
 * DEPRECATED!
 * 
 * @author  S. Luz &#60;luzs@acm.org&#62;
 * @version <font size=-1>$Id: AlertWindow.java,v 1.1.1.1 2000/07/07 16:54:36 luz Exp $</font>
 * @see  ContextClient
 */
public class AlertWindow extends Dialog {
  
  private boolean ok = false;

  public AlertWindow(Frame frame, String msg){
    super(frame,"Alert!!",false);
    add("North",new Label(msg));
    add("South",new Button("OK"));
    pack();
    //show();
  }

  synchronized public boolean ok() {
    try 
      {
	wait();
      } 
   catch(InterruptedException e) 
     {
     }
    return ok;
  }

  synchronized public boolean action(Event e, Object arg) {
    if ( ((String)arg).equals("OK") )
      ok = true;

    notifyAll();
    dispose();
    return true;
  }
}
