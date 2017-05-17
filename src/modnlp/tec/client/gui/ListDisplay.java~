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

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ComponentListener;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;

import modnlp.tec.client.ConcordanceBrowser;
import modnlp.tec.client.ConcordanceObject;
import modnlp.tec.client.gui.event.DefaultChangeEvent;
import modnlp.tec.client.gui.event.FontSizeChangeEvent;
import modnlp.tec.client.gui.event.SortHorizonChangeEvent;
import modnlp.tec.client.gui.event.TecDefaultChangeListener;

/**
 *  This class implements the object on which the output
 *  of a concordance interaction (list of lines centered
 *  on a given word, KWIC format) is displayed
 *
 * @author  S. Luz &#60;luzs@acm.org&#62;
 * @version <font size=-1>$Id: ListDisplay.java,v 1.4 2003/06/22 13:48:05 luzs Exp $</font>
 * @see  ContextClient
 */
public class ListDisplay extends JPanel
  implements TecDefaultChangeListener, ComponentListener {
  public JList list;
  public JScrollPane jscroll;
  //public ConcArray conc;
  public ConcordanceBrowser parent;
  Font font; 
  public static int MAXCOL = 180;
  public static int OFFSET = 40;
  public static String HSEP = " | "; 
  /** Width of the scroll pane and list (to be used in resizing events etc) */
  public static int LWIDTH  = 888;
  /** Height of the scroll pane and list (to be used in resizing events etc) */
  public static int LHEIGHT = 473;
  /** Height of the scroll button panel  */
  public static int BWIDTH  = 32;
  /** Height of the scroll button panel  */
  public static int BHEIGHT = 400;
  public int NOROWS = 25;
  // distance to beginning of array 
  public int  concArrayOffset = 0;
  public int  nowDisplayingfrom = 0;
  private ListModel listModel;
  private JButton ffwdb = null;
  private JButton rwndb = null;
  private JButton sfwdb = null;
  private JButton swndb = null;
  public ListDisplayRenderer renderer = new ListDisplayRenderer();
  private boolean resizePending = false;
  private JProgressBar scrollProgress;
  private ListSelectionModel listSelectionModel;
  private JTable table;

  public ListDisplay(BrowserGUI parent, ListModel lm) 
  {
    super();
    
    String[] columnNames = {"Filename", "Left Context", "Keyword","Right Context"};
    String[][] data = new String[lm.getSize()][4];
    
    
    
    table = new JTable(data, columnNames);
    
    parent = parent;
    font = new Font("Monospaced", Font.PLAIN, 12);//parent.getPreferredFontSize());
    //setPreferredSize(new Dimension(LWIDTH+50, LHEIGHT+30));
   
    listModel = lm; //parent.getConcordanceVector(); //new DefaultListModel();
    list = new JList(listModel);
    list.setCellRenderer(renderer);    
    renderer.setFont(font);
    //setCellPrototype(ConcordanceObject.RENDERER_PROTOTYPE);
    
    //table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    jscroll = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    jscroll.setPreferredSize(new Dimension(LWIDTH, LHEIGHT));
  
    // jscroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
    
    jscroll.setWheelScrollingEnabled(true);
    add(jscroll);
    
    listSelectionModel = list.getSelectionModel();
    listSelectionModel.addListSelectionListener(parent);
    
    addComponentListener(this);
  }
///******************************************
 
  public int getIndexOfDetail(int col, String str){
      for (int i = 0; i < list.getModel().getSize()-1; i++) {
          ConcordanceObject cobjct = (ConcordanceObject) list.getModel().getElementAt(i);
          String concordance = cobjct.concordance;
          HighlightString hls = cobjct.indexOfSortContext();
          String test = hls.string;
          if(test.equalsIgnoreCase(str))
              return i;
      
      }
    return 3;
  }
  
  public void refresh()
  {
      revalidate();
      repaint();
      jscroll.revalidate();
  }
  
  public void setViewToIndex(int index)
  {
      int guess = index-2+(LHEIGHT/list.getFixedCellHeight());
      int len = list.getModel().getSize()-1;
      //scroll to top first to avoid having target index above vieport
      list.ensureIndexIsVisible(0);
      revalidate();
      repaint();
      jscroll.revalidate();
      
      // fallback to going to the end if our next sttement fails
      if(guess>len){
      list.ensureIndexIsVisible(len);
      }
      else{
      // guess the number of indices forward we have to go to get seleted
      //to top of viewport
      list.ensureIndexIsVisible(guess);
      }
      revalidate();
      repaint();
      jscroll.revalidate();
    
  }
  
  public void addListSelectionListener(ListSelectionListener lsl){
    listSelectionModel.addListSelectionListener(lsl);
  }


  public ConcordanceObject getSelectedValue () {
    return (ConcordanceObject) list.getSelectedValue();
  }

  public int getSelectedIndex (){	
    int index = list.getSelectedIndex();
    return index;	
  }

  public ListModel getListModel(){
    return listModel;
  }

  public void setFontSize(int fs){
    resetFontIfChanged((float)fs);
    setCellPrototype(ConcordanceObject.RENDERER_PROTOTYPE);
  }

  public int getFontSize(){
    return font.getSize();
  }

  public void setCellPrototype(ConcordanceObject co){
    list.setPrototypeCellValue(co);
    
  }

  /** Resets font when user preferences change
   *  @return <code>true</code> if font has been reset 
   */
  public boolean resetFontIfChanged(float nsz){
    if ( renderer.getFont().getSize() != nsz ){
      font = font.deriveFont(nsz);
      renderer.setFont( font );
      return true;
    }
    else 
      return false;
  }

  // The TEC default change interface
  public void defaultChanged(DefaultChangeEvent e)
  {
    //System.err.println("Default change"+preferenceFrame.getFontSize());
  }
  public void defaultChanged(SortHorizonChangeEvent e)
  {
    //System.err.println("New sort"+preferenceFrame.getFontSize());
  }

  public void defaultChanged(FontSizeChangeEvent e)
  {
    //resetFontIfChanged(e.getNewSize());
    //System.err.println("New font"+preferenceFrame.getFontSize());
    if ( resetFontIfChanged(e.getNewSize()) ){
      revalidate();
      repaint();
    }
  }

  // ComponentListener
  public void componentResized(ComponentEvent e) {
    resizePending = true;
    Component ec = e.getComponent();
    if (! isShowing() )
      return;
    int w = ec.getWidth();
    int h = ec.getHeight();
    Dimension dlist = new Dimension(w-8, h-10);
    //setPreferredSize(new Dimension(w, h-120));
    redisplayConc();
    
  }

  public void redisplayConc(){
      
   remove(jscroll);
    String[] columnNames = {"Filename", "Left Context", "Keyword","Right Context"};
    String[][] data = new String[listModel.getSize()][4];
    Graphics g =null;
    ConcordanceObject cobjct;
    int[] maxLengths ={15,15,15,15};
    
    //Setup to measure Fonts
    BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g2d = img.createGraphics();
    FontMetrics fm = g2d.getFontMetrics(font);
    
    //converting listmodel to data array
    for (int i = 0; i < listModel.getSize(); i++) {
          cobjct = (ConcordanceObject) listModel.getElementAt(i);
          data[i][0] = " "+ cobjct.sfilename.trim()+" ";
          data[i][1] = cobjct.getLeftContext().trim();
          data[i][2] = " " +cobjct.getKeyword().trim() +" ";
          data[i][3] = cobjct.getKeywordAndRightContext().substring(cobjct.getKeywordAndRightContext().indexOf(" ")+1).trim();
          for (int j = 0; j < 4; j++) {
              if(fm.stringWidth(data[i][j]) > maxLengths[j])
                  maxLengths[j] = fm.stringWidth(data[i][j]);
          } 
      }
    
    table = new JTable(data, columnNames);
    table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    
    //setting column widths
    TableColumn column = null;
    for (int i = 0; i < 4; i++) {
        column = table.getColumnModel().getColumn(i);
        column.setPreferredWidth(maxLengths[i]); 
     } 
    
    //set column allignments
    DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
    rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
    rightRenderer.setFont(font);
    table.getColumnModel().getColumn(1).setCellRenderer(rightRenderer);
    
    DefaultTableCellRenderer centreRenderer = new DefaultTableCellRenderer();
    centreRenderer.setHorizontalAlignment(JLabel.CENTER);
    centreRenderer.setFont(font);
    table.getColumnModel().getColumn(2).setCellRenderer(centreRenderer);
    
    DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer();
    leftRenderer.setHorizontalAlignment(JLabel.LEFT);
    leftRenderer.setFont(font);
    table.getColumnModel().getColumn(3).setCellRenderer(leftRenderer);
    
    DefaultTableCellRenderer filenameRenderer = new DefaultTableCellRenderer();
    filenameRenderer.setHorizontalAlignment(JLabel.LEFT);
    filenameRenderer.setFont(font);
    table.getColumnModel().getColumn(0).setCellRenderer(filenameRenderer);
    

    //set column color
    centreRenderer.setForeground(Color.blue);
    filenameRenderer.setForeground(Color.RED);

    //turn off grid
    table.setShowGrid(false);
    
    jscroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
    jscroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    jscroll.getViewport().setView(table);
    add(jscroll);
    revalidate();
    repaint();
  }

  public void componentHidden(ComponentEvent e) {
  }
  public void componentMoved(ComponentEvent e) {
  }
  public void componentShown(ComponentEvent e) {		
  }
}
