/** 
 *  (c) 2008 S Luz <luzs@acm.org>
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
 * Foundation, 59 Temple Place - Sui
te 330, Boston, MA 02111-1307, USA.
*/

package modnlp.tec.client.gui;

import modnlp.tec.client.gui.event.TecDefaultChangeListener;
import modnlp.tec.client.gui.event.DefaultChangeEvent;
import modnlp.tec.client.gui.event.SortHorizonChangeEvent;
import modnlp.tec.client.gui.event.FontSizeChangeEvent;
import modnlp.tec.client.gui.event.ConcordanceDisplayListener;
import modnlp.tec.client.gui.event.ConcordanceDisplayEvent;
import modnlp.tec.client.gui.event.ConcordanceListSizeEvent;

import javax.swing.JFrame;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;

/**
 *  Interface for Browser GUIs 
 *
 * 
 * @author  S Luz &#60;luzs@acm.org&#62;
 * @version <font size=-1>$Id: $</font>
 * @see  
*/
public abstract class BrowserGUI extends JFrame 
  implements ListSelectionListener, ConcordanceDisplayListener, TecDefaultChangeListener
{

  public abstract void updateStatusLabel(String s);

  public abstract int getPreferredFontSize();
  
    public abstract boolean getPunctuation();
  
  public abstract int getLanguage();

  public abstract void valueChanged(ListSelectionEvent e);

  public abstract void concordanceChanged(ConcordanceDisplayEvent e);

  public abstract void concordanceChanged(ConcordanceListSizeEvent e);

  public abstract void defaultChanged(DefaultChangeEvent e);

  public abstract void defaultChanged(SortHorizonChangeEvent e);

  public abstract void defaultChanged(FontSizeChangeEvent e);
  
 
}
