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
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
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
import javax.swing.JViewport;
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
  private JTable table;

  public ListDisplay(BrowserGUI parent, ListModel lm) 
  {
    super(new BorderLayout());
    
    String[] columnNames = {"Filename", "Left Context", "Keyword","Right Context"};
    String[][] data = new String[lm.getSize()][4];
    
    
    
    table = new JTable(data, columnNames);
    table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    
    this.parent = parent;
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
    add(jscroll, BorderLayout.NORTH);
    
    listSelectionModel = list.getSelectionModel();
    //listSelectionModel.addListSelectionListener(parent);
    table.getSelectionModel().addListSelectionListener(parent);
    table.getColumnModel().getSelectionModel().addListSelectionListener(parent);

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
        JViewport viewport = (JViewport)table.getParent();

        // This rectangle is relative to the table where the
        // northwest corner of cell (0,0) is always (0,0).
        Rectangle rect = table.getCellRect(index, 1, true);

        // The location of the viewport relative to the table
        Point pt = viewport.getViewPosition();

        // Translate the cell location so that it is relative
        // to the view, assuming the northwest corner of the
        // view is (0,0)
       // if (rect.y-pt.y +200>0)
             rect.setLocation(rect.x-pt.x, rect.y-pt.y+400);

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
    //removes and readds all the components to the window  
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
          data[i][0] ="<html>"+ cobjct.sfilename.trim()+"</html>";
          data[i][leftctx] = "<html>"+cobjct.getLeftContext().trim()+"</html>";
          data[i][2] = "<html>" +cobjct.getKeyword().trim() +"</html>";
          data[i][rightctxt] ="<html>" + cobjct.getKeywordAndRightContext().substring(cobjct.getKeywordAndRightContext().indexOf(" ")+1).trim()+"</html>";
          if(cobjct.getSortContextHorizon() > 0 )
          {
              if(parent.getLanguage() == modnlp.Constants.LANG_AR){ // if arabic rendering we need to highlight different part
                  String[] contextArray = cobjct.getKeywordAndRightContext().trim().split(" ");
                  if(contextArray.length != 0){
                        contextArray[cobjct.getSortContextHorizon()] = "<font color=\"red\">"+contextArray[cobjct.getSortContextHorizon()]+"</font>";
                        StringBuilder builder = new StringBuilder();
                        for(String s : contextArray) {
                          builder.append(s+" ");
                        }
                    data[i][1] ="<html>" + builder.toString().substring(cobjct.getKeywordAndRightContext().indexOf(" ")+1).trim()+"</html>";
              }
              }else{
                String trimmed =cobjct.getKeywordAndRightContext().trim();
                String[] contextArray = trimmed.split("\\s+");
                 if(contextArray.length != 0){

                      contextArray[cobjct.getSortContextHorizon()] = "<font color=\"red\">"+contextArray[cobjct.getSortContextHorizon()]+"</font>";
                      StringBuilder builder = new StringBuilder();

                      for(String s : contextArray) {
                        builder.append(s+" ");
                      }

                      data[i][3] ="<html>" + builder.toString().substring(cobjct.getKeywordAndRightContext().indexOf(" ")+1).trim()+"</html>";

                 }
              }
          }
          
          if(cobjct.getSortContextHorizon() < 0 )
          {
              if(parent.getLanguage() == modnlp.Constants.LANG_AR){// if arabic rendering we need to highlight different part
                  String[] contextArray = cobjct.getLeftContext().split(" ");
                  if(contextArray.length != 0){
                        contextArray[contextArray.length + cobjct.getSortContextHorizon()] = "<font color=\"red\">"+contextArray[contextArray.length + cobjct.getSortContextHorizon()]+"</font>";
                        StringBuilder builder = new StringBuilder();
                        for(String s : contextArray) {
                          builder.append(s+" ");
                        }
                    data[i][3] ="<html>" + builder.toString().trim()+"</html>";
                  }
              }
              else{
                String[] contextArray = cobjct.getLeftContext().split(" ");
                if(contextArray.length != 0){
                      contextArray[contextArray.length+cobjct.getSortContextHorizon()] = "<font color=\"red\">"+contextArray[contextArray.length+cobjct.getSortContextHorizon()]+"</font>";
                      StringBuilder builder = new StringBuilder();
                      for(String s : contextArray) {
                        builder.append(s+" ");
                      }
                      data[i][1] ="<html>" + builder.toString().trim()+"</html>";
                }
              }
          }
          
          for (int j = 0; j < 4; j++) {
              if(fm.stringWidth(data[i][j]) > maxLengths[j])
                  maxLengths[j] = fm.stringWidth(data[i][j]) - fm.stringWidth("<htm</html>");
          } 
          if(cobjct.getSortContextHorizon() < 0){
              maxLengths[leftctx]= maxLengths[leftctx] -fm.stringWidth("<font color=\"red\"></font>");
          }
          if(cobjct.getSortContextHorizon() > 0){
              maxLengths[rightctxt]= maxLengths[rightctxt] -fm.stringWidth("<font color=\"red\"></font>");
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
    
    if ( ((ConcordanceObject) listModel.getElementAt(0)).getSortContextHorizon() != 0 ) {
    
    }

    //turn off grid
    table.setShowGrid(false);
    
    // set selection mode
    table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    
    //readd listeners
    table.getSelectionModel().addListSelectionListener(parent);
    table.getColumnModel().getSelectionModel().addListSelectionListener(parent);
    
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

