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

import java.io.File;
import javax.swing.JFileChooser;
/**
 *  Choose directory for storing a corpus
 *
 * @author  S Luz &#60;luzs@acm.org&#62;
 * @version <font size=-1>$Id: $</font>
 * @see  
*/

public class CorpusChooser extends JFileChooser {

  public CorpusChooser (String def) {
    super("Choose a location for the index");
    setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    if (def != null)
      setSelectedFile(new File(def));
  }

  public int showChooseCorpus () {
    try {
      setDialogTitle("Choose a location for the index");
      return showDialog(null, "Choose a location for the index");
    } catch (Exception e) {
      System.err.println("Error choosing corpus index dir"+e);
      return JFileChooser.ERROR_OPTION;
    }
  }

  public int showChooseDir (String msg) {
    try {
      setDialogTitle(msg);
      return showDialog(null, msg);
    } catch (Exception e) {
      System.err.println("Error choosing index directory"+e);
      return JFileChooser.ERROR_OPTION;
    }
  }

}
