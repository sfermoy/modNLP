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
package modnlp.idx.gui;

import javax.swing.JFileChooser;
/**
 *  Choose a set of files for indexing
 *
 * @author  S Luz &#60;luzs@acm.org&#62;
 * @version <font size=-1>$Id: $</font>
 * @see  
*/

public class CorpusFilesChooser extends JFileChooser {

  public CorpusFilesChooser () {
    super();
    init();
  }

  public CorpusFilesChooser (String cd) {
    super(cd);
    init();
  }

  private void init() {
    setFileSelectionMode(JFileChooser.FILES_ONLY);
    setMultiSelectionEnabled(true);
  }

  public int showChooseFiles () {
    try {
      return showDialog(null, "Choose files to index");
    } 
    catch (Exception e) { 
      System.err.println("Error choosing files"+e);
      return JFileChooser.ERROR_OPTION;
    }
  }

}
