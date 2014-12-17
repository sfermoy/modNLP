/**
 *  (c) 2009 Saturnino Luz <luzs@acm.org>
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
package modnlp.idx.gui;

import modnlp.idx.IndexManager;

/**
 *  Simple command line IndexingReporter to be used as a non-GUI
 *  alternative to IndexManagerUI
 *
 * @author  Saturnino Luz &#60;luzs@acm.org&#62;
 * @version <font size=-1>$Id: $</font>
 * @see  
*/

public class IndexManagerCL implements IndexingReporter {
  IndexManager parent;

  public IndexManagerCL(IndexManager p){
    parent = p;
  }

  public void print(String m){
    System.err.println(m);
  }
  public void enableChoice(boolean c){}

  public void addIndexedFile(String fname){}

  public void removeIndexedFile(String fname){}

}
