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

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Button;
import java.awt.GridLayout;
import java.awt.Font;
import java.awt.Panel;
import java.awt.Event;
import java.awt.Label;

/**
 *  Pop up a Yes-or-no modal dialogue window  
 *
 * 
 * @author  Saturnino Luz &#60;luzs@acm.org&#62;
 * @version <font size=-1>$Id: YesOrNo.java,v 1.1.1.1 2000/07/07 16:54:36 luz Exp $</font>
 * @see  
*/
public class YesOrNo extends Dialog {
  Frame parent;
  Button yesButton = new Button("Yes");
  Button noButton = new Button("No");
  public boolean yes = false;
  
  public YesOrNo(Frame dw, String question) {
    super(dw, question, false);
    parent = dw;

    setLayout(new GridLayout(2,1));
    setFont(new Font("Helvetica",Font.BOLD, 14));
    setModal(true);
    add(new Label(question));

    Panel yn = new Panel();
    Label sp = new Label("  ");
    //yn.setLayout(new GridLayout(1,5));
    noButton.requestFocus();
    //yn.add(sp);
    yn.add("West",yesButton);
    //yn.add(sp);
    yn.add("East", noButton);
    //yn.add(sp);

    add(yn);
    pack();
  }
  
  public boolean action(Event event, Object arg) {
    if ( event.target == yesButton) {
      yes = true;
    }
    else if ( event.target == noButton) {
      yes = false;
    }
    hide();
    return true;
  }
}

