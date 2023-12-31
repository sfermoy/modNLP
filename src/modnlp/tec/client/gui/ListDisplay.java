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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ComponentListener;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;

import modnlp.dstruct.TokenIndex;
import modnlp.tec.client.ConcordanceObject;
import modnlp.tec.client.gui.event.DefaultChangeEvent;
import modnlp.tec.client.gui.event.FontSizeChangeEvent;
import modnlp.tec.client.gui.event.SortHorizonChangeEvent;
import modnlp.tec.client.gui.event.TecDefaultChangeListener;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.ar.ArabicAnalyzer;

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
  Analyzer ARtokeniser = new ArabicAnalyzer(org.apache.lucene.util.Version.LUCENE_36);
  public JList list;
  public JScrollPane jscroll;
  //public ConcArray conc;
  public BrowserGUI parent;
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
  private MyTable table;

  private int[] maxLengths ={15,15,15,15};
  private boolean useUserWidths = false;
  private String mosaicSelected = "";

  
  /**
   * Creates a new <code>ListDisplay</code> instance.
   *
   * @param parent a <code>BrowserGUI</code> value
   * @param lm a <code>ListModel</code> value
   */
  public ListDisplay(BrowserGUI parent, ListModel lm) 
  {
    super(new BorderLayout());
    
    String[] columnNames = {"Filename", "Left Context", "Keyword","Right Context"};
    String[][] data = new String[lm.getSize()][4];
     
    table = new MyTable(data, columnNames);
    table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    
    this.parent = parent;
    font = new Font("Tahoma", Font.PLAIN, 12);
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
    add(jscroll, BorderLayout.NORTH);
    
    listSelectionModel = list.getSelectionModel();
    //listSelectionModel.addListSelectionListener(parent);
    table.getSelectionModel().addListSelectionListener(parent);
    table.getColumnModel().getSelectionModel().addListSelectionListener(parent);
    
    table.getColumnModel().addColumnModelListener(new TableColumnWidthListener());
    table.getTableHeader().addMouseListener(new TableHeaderMouseListener());
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
    Rectangle rect;
    if(index+10 >= table.getRowCount()){
      rect = table.getCellRect(index, 1, true);
      table.changeSelection(index, 0, true, false);
    }else{
      rect = table.getCellRect(index+10, 1, true);
      table.changeSelection(index-1, 0, true, false);
    } 
    table.scrollRectToVisible(rect);
  }
  
  public void addListSelectionListener(ListSelectionListener lsl){
    //listSelectionModel.addListSelectionListener(lsl);
    table.getSelectionModel().addListSelectionListener(lsl);
    table.getColumnModel().getSelectionModel().addListSelectionListener(lsl);
  }


  public ConcordanceObject getSelectedValue () {
    list.setSelectedIndex(table.getSelectedRow());
    return (ConcordanceObject) list.getSelectedValue();
  }
  
  public ConcordanceObject[] getSelectedObjects () {
    int[] interval =table.getSelectedRows();
    ConcordanceObject[] rows = new ConcordanceObject[interval.length];
    //System.out.println("tbl :  "+table.getSelectedRows().length+ " "+ interval[0]+ " end "  + interval[interval.length-1]);
    for (int i = 0; i < rows.length; i++) {
      list.setSelectedIndex(interval[i]);  
      rows[i]= (ConcordanceObject) list.getSelectedValue();
    }
    list.setSelectedIndex(interval[0]);
    return  rows;
  }

  public int getSelectedIndex (){	
    int index = table.getSelectedRow();
    return index;	
  }

  public ListModel getListModel(){
    return listModel;
  }

  public void setFontSize(int fs){
    resetFontIfChanged((float)fs);
    setCellPrototype(ConcordanceObject.RENDERER_PROTOTYPE);
  }
  
  public void setMosaicSelected(String sel){
    mosaicSelected = sel;
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
      redisplayConc();
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

  /**
   *  <code>redisplayConc</code> display concordance, highlighting
   *  keyword and mosaic selection (if applicable).
   *
   */
  public void redisplayConc()
  {
    //System.err.println("mosaicSelected="+mosaicSelected);
    //removes and readds all the components to the window  
    remove(jscroll);
    String[] columnNames = {"Filename", "Left Context", "Keyword","Right Context"};
    String[][] data = new String[listModel.getSize()][4];
    Graphics g =null;
    ConcordanceObject cobjct;
    
    //Setup to measure Fonts
    BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g2d = img.createGraphics();
    FontMetrics fm = g2d.getFontMetrics(font);
    
    //reverse contexts for arabic
    int leftctx = 1;
    int rightctxt = 3; 
    if(parent.getLanguage() == modnlp.Constants.LANG_AR){
      leftctx = 3;
      rightctxt = 1; 
    }
   
    //converting listmodel to data array
    for (int i = 0; i < listModel.getSize(); i++) {
      cobjct = (ConcordanceObject) listModel.getElementAt(i);
      String kw = cobjct.getKeyword();
      String lc = cobjct.getLeftContext();
      String rc = cobjct.getKeywordAndRightContext();
      data[i][0] ="<html>"+ cobjct.sfilename.trim()+"     </html>";
      data[i][leftctx] = "<html>"+lc.trim()+"</html>";
      data[i][2] = "<html>  " +kw.trim() +"</html>  ";
      data[i][rightctxt]
        ="<html>" + rc.substring(kw.length()).trim()+"</html>";
      String sortctxStr ="";
      if(cobjct.getSortContextHorizon() > 0 )
        { // highlight by sorted word on the right 
          int sch = cobjct.getSortContextHorizon();
          TokenIndex rti = cobjct.getRightTokenIndex();              
          
          if ( sch == 0 || sch+1 > rti.size() )
            continue;
          StringBuilder rcs = new StringBuilder("");
          // context left of sort word
          String lrc = rc.substring(rti.getEndPos(0), rti.getStartPos(sch));
          // sort word
          String sw  = rc.substring(rti.getStartPos(sch), rti.getEndPos(sch)); 
          // context right of sort word
          String rrc = rc.substring(rti.getEndPos(sch));
          
          if( mosaicSelected.trim().equalsIgnoreCase("") )
            { // no mosaic highlights
              rcs.insert(0, "<html>");
              rcs.append(lrc); 
              rcs.append("<font color='red'>");
              rcs.append(sw);
              rcs.append("</font>");
              rcs.append(rrc);
              rcs.append("</html>");
              data[i][rightctxt] = rcs.toString();
              // leftctx stays as default
            }
          else if ( mosaicSelected.trim().equalsIgnoreCase(sw) )
            {
              rcs.append(lrc);
              rcs.append("<font color='#FF00FF'>");
              rcs.append(sw);
              rcs.append("</font>");
              rcs.append(rrc);
              rcs.insert(0, "<html><font color='#00BFFF'>");
              rcs.append("</font></html>");
              rcs.insert(0, "<html><font color='#00BFFF'>");
              data[i][rightctxt] = rcs.toString();
              data[i][leftctx] =
                "<html> <font color='#00BFFF'>"+lc.trim()+"</font> </html>";
            }
        }
      if(cobjct.getSortContextHorizon() < 0 )
        { // left context sorting
          int sch = cobjct.getSortContextHorizon()*-1;
          TokenIndex lti = cobjct.getLeftTokenIndex();
          if ( sch == 0 || sch+1 > lti.size() )
            continue;
          //System.err.println("sch==>"+sch+" coord ==>\n"+ lti+"\n rc==>"+rc);
          // builder for left context  
          StringBuilder lcs = new StringBuilder("");
          // context left of sort word
          String lrc = lc.substring(0, lti.getStartPos(sch));
          // sort word
          String sw  = lc.substring(lti.getStartPos(sch), lti.getEndPos(sch)); 
          // context right of sort word
          String rrc = lc.substring(lti.getEndPos(sch));
          
          if( mosaicSelected.trim().equalsIgnoreCase("") )
            { // no mosaic highlights
              lcs.insert(0, "<html>");
              lcs.append(lrc); 
              lcs.append("<font color='red'>");
              lcs.append(sw);
              lcs.append("</font>");
              lcs.append(rrc);
              lcs.append("</html>");
              data[i][leftctx] = lcs.toString();
              // rightctxt stays as default
            }
          else if ( mosaicSelected.trim().equalsIgnoreCase(sw) )
            {
              lcs.append(lrc);
              lcs.append("<font color='#FF00FF'>");
              lcs.append(sw);
              lcs.append("</font>");
              lcs.append(rrc);
              lcs.insert(0, "<html><font color='#00BFFF'>");
              lcs.append("</font></html>");
              lcs.insert(0, "<html><font color='#00BFFF'>");
              data[i][leftctx] = lcs.toString();
              data[i][rightctxt] = "<html> <font color='#00BFFF'>"+
                rc.substring(kw.length()).trim()+"</font> </html>";
            }
        }
      if(!useUserWidths)
        {  
          for (int j = 0; j < 4; j++)
            {
              if(fm.stringWidth(data[i][j]) > maxLengths[j])
                maxLengths[j] = fm.stringWidth(data[i][j]) -
                  fm.stringWidth("<html></html>");
            } 
          if(cobjct.getSortContextHorizon() < 0)
            {
              int temp =  maxLengths[leftctx] -
                fm.stringWidth("<htm</html<font color=\"red\"></font>");
              maxLengths[leftctx] = temp;
            }
          if(cobjct.getSortContextHorizon() > 0)
            {
              int temp = maxLengths[rightctxt] -
                fm.stringWidth("<htm</htm<font color=\"red\"></font>");
              maxLengths[rightctxt] = temp;
            }
          useUserWidths = true;
        }
    }

    table = new MyTable(data, columnNames);
    table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    table.setFont(font);
    table.setRowHeight(table.getRowHeight()+table.getRowHeight()/5);
    
    //setting column widths
    TableColumn column = null;
    for (int i = 0; i < 4; i++) {
      column = table.getColumnModel().getColumn(i);
        
      column.setPreferredWidth(maxLengths[i]); 
    } 
    
    //set column allignments
    LeftContextRenderer rightAlignRenderer = new LeftContextRenderer();
    rightAlignRenderer.setHorizontalAlignment(JLabel.RIGHT);
    //rightRenderer.setIgnoreRepaint(true);
    //rightRenderer.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
    //rightRenderer.applyComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
    rightAlignRenderer.setFont(font);
    table.getColumnModel().getColumn(1).setCellRenderer(rightAlignRenderer);
    
    DefaultTableCellRenderer centreRenderer = new DefaultTableCellRenderer();
    centreRenderer.setHorizontalAlignment(JLabel.CENTER);
    centreRenderer.setFont(font);
    table.getColumnModel().getColumn(2).setCellRenderer(centreRenderer);
    
    RightContextRenderer leftAlignRenderer = new RightContextRenderer();
    leftAlignRenderer.setHorizontalAlignment(JLabel.LEFT);
    leftAlignRenderer.setFont(font);
    table.getColumnModel().getColumn(3).setCellRenderer(leftAlignRenderer);
    
    DefaultTableCellRenderer filenameRenderer = new DefaultTableCellRenderer();
    filenameRenderer.setHorizontalAlignment(JLabel.LEFT);
    filenameRenderer.setFont(font);
    table.getColumnModel().getColumn(0).setCellRenderer(filenameRenderer);
    

    //set column color
    centreRenderer.setForeground(Color.blue);
    filenameRenderer.setForeground(Color.RED);
    
    //update list
    list = new JList(listModel);
    

    //turn off grid
    table.setShowGrid(false);
    
    // set selection mode
    table.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
    
    //re-add listeners
    table.getSelectionModel().addListSelectionListener(parent);
    table.getColumnModel().getSelectionModel().addListSelectionListener(parent);
    table.getColumnModel().addColumnModelListener(new TableColumnWidthListener());
    table.getTableHeader().addMouseListener(new TableHeaderMouseListener());
    
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
  
  private class TableColumnWidthListener implements TableColumnModelListener
  {
    @Override
    public void columnMarginChanged(ChangeEvent e)
    {
      /* columnMarginChanged is called continuously as the column width is changed
         by dragging. Therefore, execute code below ONLY if we are not already
         aware of the column width having changed */
      if(!table.hasColumnWidthChanged())
        {
          /* the condition  below will NOT be true if
             the column width is being changed by code. */
          if(table.getTableHeader().getResizingColumn() != null)
            {
              // User must have dragged column and changed width
              table.setColumnWidthChanged(true);
            }
        }
    }

    @Override
    public void columnMoved(TableColumnModelEvent e) { }

    @Override
    public void columnAdded(TableColumnModelEvent e) { }

    @Override
    public void columnRemoved(TableColumnModelEvent e) { }

    @Override
    public void columnSelectionChanged(ListSelectionEvent e) { }
  }
  
  private class TableHeaderMouseListener extends MouseAdapter
  {
    @Override
    public void mouseReleased(MouseEvent e)
    {
      /* On mouse release, check if column width has changed */
      if(table.hasColumnWidthChanged())
        {
            
          TableColumn column = null;
          for (int i = 0; i < 4; i++) {
            column = table.getColumnModel().getColumn(i);
            maxLengths[i] = column.getWidth();
          } 
                
          // Reset the flag on the table.
          table.setColumnWidthChanged(false);
        }
    }

  }
}
class MyTable extends JTable {
  public MyTable(Object[][] data, Object[] columnNames){
    super(data,columnNames);
  }
  private boolean isColumnWidthChanged;
  public boolean hasColumnWidthChanged() {
    return isColumnWidthChanged;
  }

  public void setColumnWidthChanged(boolean widthChanged) {
    isColumnWidthChanged = widthChanged;
  }

}

class LeftContextRenderer extends DefaultTableCellRenderer {
    
  @Override
  public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
    final JViewport viewport = new JViewport();
    final Component c;
    c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    viewport.setView( c);      
    int width = viewport.getWidth();
    Dimension size = c.getPreferredSize();
    viewport.setViewPosition(new Point(size.width - width, 0));
    return viewport;
  }
}

class RightContextRenderer extends DefaultTableCellRenderer {
    
  @Override
  public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
    final JViewport viewport = new JViewport();
    final Component c;
    c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    viewport.setView( c);      
    //int width = viewport.getWidth();
    //Dimension size = c.getPreferredSize();
    //viewport.setViewPosition(new Point(size.width - width, 0));
    return viewport;
  }
}





