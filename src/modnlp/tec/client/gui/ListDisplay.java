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

  public void redisplayConc(){
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
          data[i][0] ="<html>"+ cobjct.sfilename.trim()+"     </html>";
          data[i][leftctx] = "<html>"+cobjct.getLeftContext().trim()+"</html>";
          data[i][2] = "<html>  " +cobjct.getKeyword().trim() +"</html>  ";
          data[i][rightctxt] ="<html>" + cobjct.getKeywordAndRightContext().substring(cobjct.getKeywordAndRightContext().indexOf(" ")+1).trim()+"</html>";
          String sortctxStr ="";
          if(cobjct.getSortContextHorizon() > 0 )
          {
              if(parent.getLanguage() == modnlp.Constants.LANG_AR){ // if arabic rendering we need to highlight different part
                  String[] contextArray = cobjct.getKeywordAndRightContext().trim().split(" ");
                  String[] otherContextArray = cobjct.getLeftContext().trim().split(" ");
                  sortctxStr = contextArray[cobjct.getSortContextHorizon()];
                  if(contextArray.length != 0){
                    String sortedWord = contextArray[cobjct.getSortContextHorizon()];
                    if(sortedWord.trim().equalsIgnoreCase(mosaicSelected.trim()) ){
                        contextArray[cobjct.getSortContextHorizon()] = "<font color=\"#FF00FF\">"+sortedWord+"</font>";
                    }else{
                        contextArray[cobjct.getSortContextHorizon()] = "<font color=\"red\">"+sortedWord+"</font>";
                    }
       
                    StringBuilder builder = new StringBuilder();
                    StringBuilder otherbuilder = new StringBuilder();
                    //blue for mosaic selected in other pos
                    for(String s : contextArray) {
                        if(s.trim().equalsIgnoreCase(mosaicSelected.trim()) ){
                            builder.append("<b><font color=\"#FF00FF\">"+s+"</font> </b>");  
                        }else{
                            builder.append(s+" ");
                        }  
                    }
                    for(String s : otherContextArray) {
                        if(s.trim().equalsIgnoreCase(mosaicSelected.trim()) ){
                            otherbuilder.append("<font color=\"#FF00FF\">"+s+"</font> ");
                        }else{
                            otherbuilder.append(s+" ");
                        }  
                    }
                    if(sortctxStr.equalsIgnoreCase(mosaicSelected.trim()) && !mosaicSelected.equalsIgnoreCase("")){
                        //we would need to use our arabic tokenisation to identify words and also wrapping the entire string in html 
                        //makes it display left to right
//      data[i][1] ="<font color=\"#00BFFF\">" + builder.toString().substring(cobjct.getKeywordAndRightContext().indexOf(" ")+1).trim()+"</font>";
//      data[i][rightctxt] ="<font color=\"#00BFFF\">" + otherbuilder.toString().substring(cobjct.getKeywordAndRightContext().indexOf(" ")+1).trim()+"</font>";
                        data[i][1] ="<html>" + builder.toString().substring(cobjct.getKeywordAndRightContext().indexOf(" ")+1).trim()+"</html>";
                    } 
                    else{
                        data[i][1] ="<html>" + builder.toString().substring(cobjct.getKeywordAndRightContext().indexOf(" ")+1).trim()+"</html>";
                    }
              }
              }else{
                String trimmed = cobjct.getKeywordAndRightContext().trim();
                String[] contextArray = trimmed.split("\\s+");
                String[] otherContextArray = cobjct.getLeftContext().trim().split(" ");
                if (cobjct.getSortContextHorizon()>= contextArray.length)
                   continue;
                else{
                    sortctxStr = contextArray[cobjct.getSortContextHorizon()];
                    System.out.println(sortctxStr);
                    sortctxStr = sortctxStr.replaceFirst("^[^\\p{Alpha}]","");
                    System.out.println(sortctxStr);
                    sortctxStr = sortctxStr.replaceFirst("[^\\p{Alpha}]+[\\p{Alpha}]*", "");
                    System.out.println(sortctxStr);
                    
                }
                if(contextArray.length != 0){                                     
                    String sortedWord = contextArray[cobjct.getSortContextHorizon()];                   
                    String striped = sortedWord.replaceFirst("^[^\\p{Alpha}]","");
                    striped = striped.replaceFirst("[^\\p{Alpha}]+[\\p{Alpha}]*", "");
                    if(striped.trim().equalsIgnoreCase(mosaicSelected.trim()) ){
                        contextArray[cobjct.getSortContextHorizon()] = "<font color=\"#FF00FF\">"+sortedWord+"</font>";
                    }else{
                        contextArray[cobjct.getSortContextHorizon()] = "<font color=\"red\">"+sortedWord+"</font>";
                    }
                    StringBuilder builder = new StringBuilder();
                    StringBuilder otherbuilder = new StringBuilder();
                    for(String s : contextArray) {
                        String striped1 = s.replaceFirst("^[^\\p{Alpha}]","");
                        striped1 = striped1.replaceFirst("[^\\p{Alpha}]+[\\p{Alpha}]*", "");
                      if(striped1.trim().equalsIgnoreCase(mosaicSelected.trim()) ){
                          builder.append("<b><font color=\"#FF00FF\">"+s+"</font> </b>");  
                      }else{
                          builder.append(s+" ");
                      }  
                    }
                    for(String s : otherContextArray) {
                        String striped1 = s.replaceFirst("^[^\\p{Alpha}]","");
                        striped1 = striped1.replaceFirst("[^\\p{Alpha}]+[\\p{Alpha}]*", "");
                        if(striped1.trim().equalsIgnoreCase(mosaicSelected.trim()) ){
                            otherbuilder.append("<font color=\"#FF00FF\">"+s+"</font> ");
                        }else{
                            otherbuilder.append(s+" ");
                        }  
                    }
                    if(sortctxStr.equalsIgnoreCase(mosaicSelected.trim())  && !mosaicSelected.equalsIgnoreCase("") ){
                        data[i][rightctxt] ="<html> <font color=\"#00BFFF\">" + builder.toString().substring(cobjct.getKeywordAndRightContext().indexOf(" ")+1).trim()+"</font> </html>";
                        data[i][leftctx] = "<html> <font color=\"#00BFFF\">"+otherbuilder.toString().trim()+"</font> </html>";
                    }
                    else
                        data[i][rightctxt] ="<html>" + builder.toString().substring(cobjct.getKeywordAndRightContext().indexOf(" ")+1).trim()+"</html>";
                 }
              }
          }
          
          if(cobjct.getSortContextHorizon() < 0 )
          {
              if(parent.getLanguage() == modnlp.Constants.LANG_AR){// if arabic rendering we need to highlight different part
                  String[] contextArray = cobjct.getLeftContext().split(" ");
                  String[] otherContextArray = cobjct.getKeywordAndRightContext().trim().split(" ");
                  sortctxStr = contextArray[contextArray.length + cobjct.getSortContextHorizon()];
                 if(contextArray.length != 0){
                    String sortedWord = contextArray[contextArray.length + cobjct.getSortContextHorizon()];
                    if(sortedWord.trim().equalsIgnoreCase(mosaicSelected.trim()) ){
                        contextArray[contextArray.length + cobjct.getSortContextHorizon()] = "<font color=\"#FF00FF\">"+sortedWord+"</font>";
                    }else{
                        contextArray[contextArray.length + cobjct.getSortContextHorizon()] = "<font color=\"red\">"+sortedWord+"</font>";
                    }
                    StringBuilder builder = new StringBuilder();
                    StringBuilder otherbuilder = new StringBuilder();
                    for(String s : contextArray) {
                        if(s.trim().equalsIgnoreCase(mosaicSelected.trim()) ){
                            builder.append("<b><font color=\"#FF00FF\">"+s+"</font> </b>");  
                        }else{
                            builder.append(s+" ");
                        }  
                    }
                    for(String s : otherContextArray) {
                        if(s.trim().equalsIgnoreCase(mosaicSelected.trim()) ){
                            otherbuilder.append("<font color=\"#FF00FF\">"+s+"</font> ");
                        }else{
                            otherbuilder.append(s+" ");
                        }  
                    }
                    if(sortctxStr.equalsIgnoreCase(mosaicSelected.trim())  && !mosaicSelected.equalsIgnoreCase("") ){
                        //we would need to use our arabic tokenisation to identify words and also wrapping the entire string in html 
                        //makes it display left to right
//                        data[i][3] ="<html><font color=\"#00BFFF\">" + builder.toString().trim()+"</font></html>";
//                        data[i][leftctx] = "<html><font color=\"#00BFFF\">"+otherbuilder.toString().trim()+"</font> </html>";
                         data[i][3] ="<html>" + builder.toString().trim()+"</html>";
                    }
                    else
                        data[i][3] ="<html>" + builder.toString().trim()+"</html>";
                  }
              }
              else{
                String[] contextArray = cobjct.getLeftContext().split(" ");
                int SortStringPos = contextArray.length+ cobjct.getSortContextHorizon();
                if (SortStringPos > contextArray.length || SortStringPos<0 )
                    continue;
                sortctxStr = contextArray[contextArray.length+ cobjct.getSortContextHorizon()];
                //sortctxStr = sortctxStr.replaceFirst("^[^\\p{Alpha}]","");
                sortctxStr = sortctxStr.replaceFirst("[^\\p{Alpha}]+[\\p{Alpha}]*", "");
                String[] otherContextArray = cobjct.getKeywordAndRightContext().trim().split(" ");
                if(contextArray.length != 0){
                    String sortedWord = contextArray[contextArray.length + cobjct.getSortContextHorizon()];
                    String striped = sortedWord.replaceFirst("^[^\\p{Alpha}]","");
                    striped = striped.replaceFirst("[^\\p{Alpha}]+[\\p{Alpha}]*", "");
                    if(striped.trim().equalsIgnoreCase(mosaicSelected.trim()) ){
                        contextArray[contextArray.length + cobjct.getSortContextHorizon()] = "<font color=\"#FF00FF\">"+sortedWord+"</font>";
                    }else{
                        contextArray[contextArray.length + cobjct.getSortContextHorizon()] = "<font color=\"red\">"+sortedWord+"</font>";
                    }
                    StringBuilder builder = new StringBuilder();
                    StringBuilder otherbuilder = new StringBuilder();
                    for(String s : contextArray) {
                        String striped1 = s.replaceFirst("^[^\\p{Alpha}]","");
                        striped1 = striped1.replaceFirst("[^\\p{Alpha}]+[\\p{Alpha}]*", "");
                      if(striped1.trim().equalsIgnoreCase(mosaicSelected.trim()) ){
                          builder.append("<b><font color=\"#FF00FF\">"+s+"</font> </b>");                
                      }else{
                          builder.append(s+" ");
                      }  
                    }
                    for(String s : otherContextArray) {
                        String striped2 = s.replaceFirst("^[^\\p{Alpha}]","");
                        striped2 = striped2.replaceFirst("[^\\p{Alpha}]+[\\p{Alpha}]*", "");
                        if(striped2.trim().equalsIgnoreCase(mosaicSelected.trim()) ){
                            otherbuilder.append("<font color=\"#FF00FF\">"+s+"</font> ");
                        }else{
                            otherbuilder.append(s+" ");
                        }  
                    }
                    if(sortctxStr.equalsIgnoreCase(mosaicSelected.trim())  && !mosaicSelected.equalsIgnoreCase("") ){
                        data[i][1] = "<html> <font color=\"#00BFFF\">" + builder.toString().trim()+"</font> </html>";
                        data[i][rightctxt] = "<html> <font color=\"#00BFFF\">" + otherbuilder.substring(cobjct.getKeywordAndRightContext().indexOf(" ")+1).trim()+"</font> </html>";
                    }
                    else
                        data[i][1] ="<html>" + builder.toString().trim()+"</html>";
                }
              }
          }
    if(!useUserWidths){  
          for (int j = 0; j < 4; j++) {
              if(fm.stringWidth(data[i][j]) > maxLengths[j])
                  maxLengths[j] = fm.stringWidth(data[i][j]) - fm.stringWidth("<html></html>");
          } 
          if(cobjct.getSortContextHorizon() < 0){

             int temp =  maxLengths[leftctx] -fm.stringWidth("<htm</html<font color=\"red\"></font>");
                  maxLengths[leftctx] = temp;
          }
          if(cobjct.getSortContextHorizon() > 0){
             int temp = maxLengths[rightctxt] -fm.stringWidth("<htm</htm<font color=\"red\"></font>");
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





