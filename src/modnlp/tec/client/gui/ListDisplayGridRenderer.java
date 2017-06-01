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

import java.awt.Component;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.FontMetrics;
import javax.swing.JLabel;
import javax.swing.ListCellRenderer;
import modnlp.tec.client.ConcordanceObject;
import javax.swing.JList;
import static javax.swing.SwingConstants.LEFT;

/**
 *  Cell renderer for TEC concordance list
 *
 * 
 * @author  Saturnino Luz &#60;luzs@acm.org&#62;
 * @version <font size=-1>$Id: ListDisplayRenderer.java,v 1.1.1.1 2000/07/07 16:54:36 luz Exp $</font>
 * @see  
*/
public class ListDisplayGridRenderer extends JLabel 
  implements ListCellRenderer {//, ListSelectionListener  {

  private ConcordanceObject cobjct;
  private static final int MAXFNSIZE = 13;
  private static final Color FILENAME_COLOR = Color.red.darker();
  private static final Color KWORD_COLOR = Color.green.darker();
  private static final Color SORT_COLOR = Color.blue.darker();
  // we will set this string in and reset it to null each 
  // time sort is called
  public String showDetailString = null;


  private int keyWordPosition = 0;

  public ListDisplayGridRenderer() {
    super();
    setOpaque(true);
    setHorizontalAlignment(LEFT);
  }

  public Component getListCellRendererComponent(JList list,
                                                Object value,
                                                int index,
                                                boolean isSelected,
                                                boolean cellHasFocus) {
    
    
    if (isSelected) {
      setBackground(list.getSelectionBackground());
      setForeground(list.getSelectionForeground());
    } else {
      setBackground(list.getBackground());
      setForeground(list.getForeground());
    }
    cobjct = (ConcordanceObject)value;
    int linesize;
    //FontMetrics fm = getFontMetrics(getFont());
    //setPreferredSize(new Dimension(1800,(int)(fm.getAscent()*1.1)));
    if (cobjct == null){
      setText("");
    }
    else {
      //setText(cobjct.htmlConcLine(MAXFNSIZE));
      //setText(cobjct.textConcLine(MAXFNSIZE));
      setText("");
    }
    return this;
  }
  
  
  public void paintComponent(Graphics g){
    if (cobjct == null){
      return;
    }
    FontMetrics fm = getFontMetrics(getFont());
    super.paintComponent(g);

    //g.drawString(cobjct.textConcLine(MAXFNSIZE), 0, fm.getAscent());
    String concordance = cobjct.concordance;
    String filename = cobjct.sfilename;
    
    // highlight filename
    //int asc = fm.getAscent();
    int concoffset = fm.stringWidth(cobjct.sfilename);
    HighlightString hls = cobjct.indexOfKeyword();
    
    int kwoffset = concoffset+fm.stringWidth(concordance.substring(0, hls.position));
    if (kwoffset>keyWordPosition)
      keyWordPosition = kwoffset;
    else if (kwoffset<keyWordPosition){
      concoffset += keyWordPosition-kwoffset; 
      kwoffset = keyWordPosition;
    }
    if ( cobjct.getSortContextHorizon() != 0 ) {
        HighlightString hls2 = cobjct.indexOfSortContext();
        if(hls2.string.equalsIgnoreCase(showDetailString)){

              g.setColor(Color.RED);

          }
    }
    g.drawString(cobjct.concordance, concoffset, fm.getAscent());
    g.setColor(FILENAME_COLOR);
    g.drawString(filename, 0, fm.getAscent());
    //System.out.println("horizon->"+cobjct.sortContextHorizon+"<-");
    // highlight keyword
    g.setColor(KWORD_COLOR);


      
    g.drawString(hls.string, 
                 kwoffset,
                 //fm.stringWidth(filename+concordance.substring(0, hls.position)),
                 fm.getAscent());
    // highlight sort keyword (if needed)
    
    if ( cobjct.getSortContextHorizon() != 0 ) {
      g.setColor(SORT_COLOR);
      HighlightString hls2 = cobjct.indexOfSortContext();
      //*****
      if(hls2.string.equalsIgnoreCase(showDetailString)){
         
          g.setColor(Color.MAGENTA);
          
      }
      
      g.drawString(hls2.string, 
                   concoffset+fm.stringWidth(concordance.substring(0, hls2.position)),
                   //fm.stringWidth(filename+concordance.substring(0, hls.position)),
                   fm.getAscent());
      
      }
    }
  

  public Dimension getMinimumSize()
  { FontMetrics fm = getFontMetrics(getFont());
    return new Dimension(1700,(int)(fm.getAscent()*1.3)); }
  
  public Dimension getPreferredSize()
  { return getMinimumSize(); }



}












