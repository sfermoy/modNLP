/**
 * (c) 2009 S Luz <luzs@acm.org>
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

import modnlp.tec.client.ConcordanceBrowser;

import java.awt.Color;
import javax.swing.JPanel;
import javax.swing.JLabel;

/**
 *  Display status of subcorpus selection and case-sensitivity of search
 *
 * @author  S Luz &#60;luzs@acm.org&#62;
 * @version <font size=-1>$Id: $</font>
 * @see  
*/

public class SubcorpusCaseStatusPanel extends JPanel {

  public static final String SCOFFTIP = "Full corpus search (subcorpus selection OFF)";
  public static final String SCONTIP = "Subcorpus selection ";
  public static final String CASEOFFTIP = "Case-insensitive keyword(s)";
  public static final String CASEONTIP = "Case-sensitive keyword(s)";
  public static final String SCOFF = "fc";
  public static final String SCON = "sc";
  public static final String CASEOFF = "ci";
  public static final String CASEON = "cs";

  JLabel scStatusLabel = new JLabel(SCOFF);
  JLabel caseStatusLabel = new JLabel(CASEOFF);

  private ConcordanceBrowser parent = null;

  public SubcorpusCaseStatusPanel (Object p){
    parent = (ConcordanceBrowser)p;
    scStatusLabel.setForeground(Color.RED);
    add(scStatusLabel);
    add(new JLabel("-"));
    caseStatusLabel.setForeground(Color.RED);
    add(caseStatusLabel);
  }

  public void updateStatus(){
    // case status
    if (parent.isCaseSensitive()){
      caseStatusLabel.setText(CASEON);
      caseStatusLabel.setToolTipText(CASEONTIP);
    }
    else{
      caseStatusLabel.setText(CASEOFF);
      caseStatusLabel.setToolTipText(CASEOFFTIP);
    }
    // subcorpus selection status
    if (parent.isSubCorpusSelectionON()){
      scStatusLabel.setText(SCON);
      scStatusLabel.setToolTipText(SCONTIP+": "+parent.getXQueryWhere());
    }
    else{
      scStatusLabel.setText(SCOFF);
      scStatusLabel.setToolTipText(SCOFFTIP);
    }
  }

  public void setCaseSensitiveStatus(boolean b){
      if (b){
        caseStatusLabel.setText(CASEON);
        caseStatusLabel.setToolTipText(CASEONTIP);
      }
      else{
        caseStatusLabel.setText(CASEOFF);
        caseStatusLabel.setToolTipText(CASEOFFTIP);
      }
  }

  public void setSCSelectionStatus(boolean b){
      if (b){
        scStatusLabel.setText(SCON);
        scStatusLabel.setToolTipText(SCONTIP+": "+parent.getXQueryWhere());
      }
      else{
        scStatusLabel.setText(SCOFF);
        scStatusLabel.setToolTipText(SCOFFTIP);
      }
  }

}
