/**
 *  (c) 2007 S Luz <luzs@acm.org>
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
package modnlp.tec.client.plugin;

import modnlp.Constants;
import modnlp.dstruct.FrequencyHash;
import modnlp.idx.headers.HeaderDBManager;
import modnlp.tec.client.Plugin;
import modnlp.tec.client.gui.*;
import modnlp.tec.client.ConcordanceBrowser;
import modnlp.tec.client.TecClientRequest;
import modnlp.tec.client.CorpusDescriptionProducer;
import modnlp.idx.database.Dictionary;

import java.awt.Font;
import java.awt.Dimension;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.FocusListener;
import java.awt.event.FocusEvent;
import java.awt.font.TextAttribute;

import java.net.URL;
import java.net.HttpURLConnection;
import javax.swing.JFrame;
import java.io.BufferedReader;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.Timer;
import java.io.PrintWriter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.JTable;
import javax.swing.table.JTableHeader;
import javax.swing.JScrollPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JLabel;
import java.util.StringTokenizer;
import java.io.PipedWriter;
import java.io.PipedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import javax.swing.JOptionPane;
import java.util.List;
import java.util.Vector;
import java.util.Collections;
import java.awt.event.MouseAdapter;
import javax.swing.table.TableColumnModel;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Color;
import javax.swing.JFileChooser;
import java.io.File;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Comparator;
import java.util.Map;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.text.NumberFormat;
import java.util.Enumeration;
import java.awt.Component;
import java.util.HashMap;
import javax.swing.table.TableCellRenderer;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.JEditorPane;
import javax.swing.JTextPane;
import modnlp.idx.query.SubcorpusConstraints;

/**
 *  Basic corpus description browser
 *
 * @author  S Luz &#60;luzs@acm.org&#62;
 * @version <font size=-1>$Id: $</font>
 * @see  
 */
public class CorpusDescriptionBrowser extends JFrame
  implements  Runnable, Plugin, FocusListener, WindowFocusListener
{
  
  public static final String FULLCORPUSTIP = "to select subcorpus, use Preferences -> Select subcorpus on main window.";
  public static final String SCOFFTIP = "Full corpus search (subcorpus selection ON)";

  private Thread ftwThread;
  
  private BufferedReader input;
  private JFrame thisFrame = null;

  // MAXCHUNKSIZE controls how often we notify the GUI that the table contents have changed
  private int MAXCHUNKSIZE = 900;
  private int NOCOLUMNS = 40;
  
  private HttpURLConnection exturlConnection;
  int dldCount = 0;
  double ttratio = 0;
  int notokens = 0;
  int dldi = 0;
  Timer dld_timer;
  JButton saveButton = new JButton("Save");

  private PrintWriter fqlout = null;

  DefaultTableModel model = new DefaultTableModel();
  DefaultTableModel noCaseModel = null; 
  JTable table = new JTable(model);

  JLabel statsLabel = new JLabel("                            ");
  SubcorpusCaseStatusPanel sccsPanel;


  private JProgressBar progressBar;

  private static String title = new String("MODNLP Plugin: CorpusDescriptionBrowser 0.2"); 
  private ConcordanceBrowser parent = null;
  private boolean guiLayoutDone = false;

  public CorpusDescriptionBrowser() {
    thisFrame = this;
  }

  public void setParent(Object p){
    parent = (ConcordanceBrowser)p;
    sccsPanel = new SubcorpusCaseStatusPanel(p);
  }

  public void activate() {
    if (guiLayoutDone){
      setVisible(true);
      return;
    }
    model.addColumn("#");
    model.addColumn("File");
    model.addColumn("Subcorpus");
    model.addColumn("Description");
    model.addColumn("# tokens");
    model.addColumn("TT ratio");
    TableColumnModel tcm = table.getColumnModel();
    tcm.setColumnMargin(10);
    TableColumn tcn = tcm.getColumn(0);
    tcn.setPreferredWidth(15);
    tcm.getColumn(1).setPreferredWidth(65);
    tcm.getColumn(2).setPreferredWidth(50);
    TableColumn tcd = tcm.getColumn(3);
    tcd.setPreferredWidth(450);
    TableColumn tct = tcm.getColumn(4);
    tct.setPreferredWidth(50);
    TableColumn tcr = tcm.getColumn(5);
    tcr.setPreferredWidth(50);
    FNumberRenderer fnr = new FNumberRenderer();
    PctRenderer pcr = new PctRenderer();
    tcn.setCellRenderer(fnr);
    tct.setCellRenderer(fnr);
    tcr.setCellRenderer(pcr);
    tcd.setCellRenderer(new HtmlTextAreaRenderer());
    table.setAutoCreateColumnsFromModel(false);
    
    table.setPreferredScrollableViewportSize(new Dimension(780, 500));
    JTableHeader header = table.getTableHeader();
    
    header.addMouseListener(new ColumnHeaderListener());    
    
    JScrollPane scrollPane = new JScrollPane(table);
    JButton dismissButton = new JButton("Quit");
    scrollPane.setPreferredSize(new Dimension(800, 600));
    dismissButton.addActionListener(new QuitListener());
    saveButton.addActionListener(new SaveListener());
    JButton go = new JButton("Get metadata");
    go.addActionListener(new GoListener());
        
    JPanel pa = new JPanel();
    //pa.setLayout(new BorderLayout());
    //JPanel pa0 = new JPanel();
    //JPanel pa1 = new JPanel();
    pa.add(go);

    pa.add(saveButton);
    pa.add(new JLabel("     "));
    pa.add(dismissButton);
    //pa.add(pa0,BorderLayout.NORTH);
    //pa.add(pa1,BorderLayout.SOUTH);
    progressBar = new JProgressBar(0,800);
    progressBar.setStringPainted(true);
    dld_timer = new Timer(300, new ActionListener() {
        public void actionPerformed(ActionEvent evt) {
          //Int perct = (Int) (progressBar.getPercentComplete()*100);
          progressBar.setValue(dldi++ % 6);
          if ( dldCount > 0 )
            {
              dld_timer.stop();
              //progressBar.setString("Done");
              progressBar.setValue(progressBar.getMaximum());         
            }
        }
      });

    JPanel pabottom = new JPanel();
    pabottom.setLayout(new BorderLayout());
    JPanel pa2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
    JPanel pa3 = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    pa2.add(progressBar);
    pa2.add(statsLabel);
    statsLabel.setSize(450,statsLabel.getHeight());

    pa3.add(sccsPanel);
    
    pabottom.add(pa2,BorderLayout.WEST);
    pabottom.add(pa3,BorderLayout.EAST);

    getContentPane().add(pa, BorderLayout.NORTH);
    getContentPane().add(scrollPane, BorderLayout.CENTER);
    getContentPane().add(pabottom, BorderLayout.SOUTH);

    addWindowFocusListener(this);
    saveButton.setEnabled(false);
    pack();
    setVisible(true);
    guiLayoutDone = true;
  }

  public void focusGained(FocusEvent e){
    checkSubCorpusSelectionStatus();
  }
  public void focusLost(FocusEvent e){
    checkSubCorpusSelectionStatus();
  }

  public void windowGainedFocus(WindowEvent e){
    checkSubCorpusSelectionStatus();
  }
  public void windowLostFocus(WindowEvent e){
    checkSubCorpusSelectionStatus();
  }

  private void checkSubCorpusSelectionStatus (){
    // NOT IMPLEMENTED YET
    if (parent.isSubCorpusSelectionON()){      
    }
    else{
    }
    return;
  }

  public void run() {
    String textLine = "";
    StringBuffer cstats = new StringBuffer();
    try {
      int i= 0;
      dldCount = 0;
      int rank = 1;
      int ttok = 0;
      //if (parent.isStandAlone()) {
      (new CDescPrinter()).start();
      //}
      sccsPanel.updateStatus();
      
      NumberFormat nf =  NumberFormat.getInstance(); 
      //new java.text.DecimalFormat("###,###,###,###.#####");
      //NumberFormat pf =  NumberFormat.getIntegerInstance(); 
      // new java.text.DecimalFormat("###.###");
      //System.err.println("-----starting");

      while (input==null){
        ftwThread.sleep(100);
      }
      while ((textLine = input.readLine()) != null)
        {
          try {
            if (textLine.equals(""))
              continue;
            //System.err.println("Line: |" + textLine+"|");
            String [] row = textLine.split(Constants.LINE_ITEM_SEP);
            if ( row[0].equals("0")) { // control info 
              if (row[1].equals(modnlp.idx.database.Dictionary.TTRATIO_LABEL)){
                ttratio = (new Double(row[2])).doubleValue();
                if (parent.isSubCorpusSelectionON())
                    cstats.append("Average "+row[1]+": "+nf.format(ttratio)+";  ");
                else
                    cstats.append(row[1]+": "+nf.format(ttratio)+";  ");
              }
              else if (row[1].equals(modnlp.idx.database.Dictionary.TTOKENS_LABEL)){
                notokens = (new Integer(row[2].toString())).intValue();
                cstats.append(row[1]+": "+nf.format(notokens)+";  ");
              }
              else if (row[1].equals(modnlp.idx.database.Dictionary.NOITEMS_LABEL)){
                int nitd = (new Integer(row[2].toString())).intValue();
                progressBar.setMaximum(nitd);
                progressBar.setValue(dldCount++);
                progressBar.setString("Displaying list...");
              }
            }
            else { // data
              Object [] orow = new Object[6]; 
              for (int j = 0; j < row.length; j++)
                orow[j] = row[j];
              if (row.length > 5){
                orow[0] = new Integer(row[0]);
                orow[4] = new Integer(row[4]);
                orow[5] = new Float(row[5]);
                // clean  spurious XML entities
                //System.err.println("Line: |" + textLine+"|\nOROW[3]="+orow[3]);
                orow[3] = row[3].replaceAll("&#[^;]+;"," ").trim();
              }
              else {// error in corpus description
                orow[3] = "Error retrieving this files description; please check header files.";
              }
              model.addRow(orow);
              progressBar.setValue(dldCount++);
            }
          }
          catch (NumberFormatException e){
            System.err.println("CorpusDescriptionBrowser: error reading line"+e);
            System.err.println("Line: |" + textLine+"|");
          }
        }
      statsLabel.setText(cstats.toString());
      saveButton.setEnabled(true);
      progressBar.setString("");
    }
    catch (Exception e)
      {
        statsLabel.setText(cstats.toString());
        saveButton.setEnabled(true);
        System.err.println("Exception: " + e);
        System.err.println("Line: |" + textLine+"|");
        e.printStackTrace();
      }
  }

  public void start() {
    model.setRowCount(0);
    input = null;
    try {
      if (parent.isStandAlone()) {
        PipedWriter pipeOut = new PipedWriter();
        input = new BufferedReader(new PipedReader(pipeOut));
        fqlout = new PrintWriter(pipeOut); 
      }
      else {
        TecClientRequest rq = new TecClientRequest();
        rq.setServerURL("http://"+parent.getRemoteServer());
        rq.setServerPORT(parent.getRemotePort());
        rq.put("request","corpusdesc");
        
            if (parent.isSubCorpusSelectionON())
            rq.put("xquerywhere",parent.getXQueryWhere());
    
        rq.put("casesensitive",parent.isCaseSensitive()?"TRUE":"FALSE");
        //}
        rq.setServerProgramPath("/corpusdesc");
        URL exturl = new URL(rq.toString());
        exturlConnection = (HttpURLConnection) exturl.openConnection();
        //exturlConnection.setUseCaches(false);
        exturlConnection.setRequestMethod("GET");
        //System.err.println("--input set---");
      }
    }
    catch(IOException e)
      {
        System.err.println("Exception: couldn't create stream socket"+e);
        JOptionPane.showMessageDialog(null, "Couldn't get corpus description: "+e);      
      }
    
    if ( ftwThread == null ){
      ftwThread = new Thread(this);
      ftwThread.setPriority (Thread.MIN_PRIORITY);
      ftwThread.start();
    }
  }

  public void stop() {
    if ( ftwThread != null ){
      //ftwThread.stop();
      if (!parent.isStandAlone() && exturlConnection != null)
        exturlConnection.disconnect();
      ftwThread = null;
    }
  }


  public void sortRowsBy(int colIndex, boolean ascending) {
    Vector data = model.getDataVector();
    Collections.sort(data, new ColumnSorter(colIndex, ascending));
    model.fireTableStructureChanged();
  }
    

  class ColumnSorter implements Comparator {
    int colIndex;
    boolean ascending;
    ColumnSorter(int colIndex, boolean ascending) {
      this.colIndex = colIndex;
      this.ascending = ascending;
    }

    public int compare(Object a, Object b) {
      Vector v1 = (Vector)a;
      Vector v2 = (Vector)b;
      Object o1 = v1.get(colIndex);
      Object o2 = v2.get(colIndex);

      //System.err.println("=="+o1+"=="+o2);
      // Treat empty strings as null
      if (o1 instanceof String && ((String)o1).length() == 0) {
        o1 = null;
      }
      if (o2 instanceof String && ((String)o2).length() == 0) {
        o2 = null;
      }
    
      // Sort nulls so they appear last, regardless
      // of sort order
      if (o1 == null && o2 == null) {
        return 0;
      } else if (o1 == null) {
        return 1;
      } else if (o2 == null) {
        return -1;
      } else if (o1 instanceof Integer)  {
        Integer io1;
        Integer io2;
        try {
          io1 = new Integer(o1.toString());
        }
        catch (NumberFormatException ne) {
          return 1;
        }
        try {
          io2 = new Integer(o2.toString());
        }
        catch (NumberFormatException ne) {
          return -1;
        }
        if (ascending) 
          return io1.compareTo(io2);
        else
          return io2.compareTo(io1);
      } else {
        String s1 =  o1.toString().toLowerCase();
        String s2 =  o2.toString().toLowerCase();
        if (ascending) {
          return s1.compareTo(s2);
        } else {
          return s2.compareTo(s1);
        }
      }
    }
  }


  public class ColumnHeaderListener extends MouseAdapter {
    boolean [] colAscend = {true, false, false, false,false,false}; // rank is in ascending, frequency and density in descending, and type in no particular order
    public void mouseClicked(MouseEvent evt) {
      JTable table = ((JTableHeader)evt.getSource()).getTable();
      TableColumnModel colModel = table.getColumnModel();
      
      int vColIndex = colModel.getColumnIndexAtX(evt.getX());
      int mColIndex = table.convertColumnIndexToModel(vColIndex);

      // Return if not clicked on any column header
      if (vColIndex == -1) {
        return;
      }
      colAscend[mColIndex] = !colAscend[mColIndex];
      sortRowsBy(mColIndex, colAscend[mColIndex]);
    }
  }

  class QuitListener implements ActionListener
  {
    public void actionPerformed(ActionEvent e)
    {
      stop();
      thisFrame.setVisible(false);
      //dispose();
    }
  }

  class GoListener implements ActionListener
  {
    public void actionPerformed(ActionEvent e)
    {
      stop();
      Integer i;
      dld_timer.start();
      progressBar.setString("Please be patient");
      progressBar.setMaximum(5);
      progressBar.setValue(0);
      statsLabel.setText("Retrieving metadata...");
      checkSubCorpusSelectionStatus();
      start();
    }
  }

  class SaveListener implements ActionListener {
    public void actionPerformed(ActionEvent e)
    {
      try
        {
          JFileChooser filedial = new JFileChooser();
          int returnVal = filedial.showDialog(thisFrame, "Save CSV file to disk");
          if (returnVal == JFileChooser.APPROVE_OPTION)
            {
              File file = filedial.getSelectedFile();
              //System.out.println(file.getName());
              PrintWriter dlf =
                new PrintWriter(new BufferedWriter(new FileWriter(file)));
              Object[] va =  (model.getDataVector()).toArray();
              dlf.println("Rank order\tType\tFrequency");
              for (int i = 0; i < va.length ; i++) {
                dlf.println(((Vector)va[i]).get(0)+"\t"
                            +((Vector)va[i]).get(1)+"\t"
                            +((Vector)va[i]).get(2)+"\t"
                            +((Vector)va[i]).get(3)+"\t"
                            +((Vector)va[i]).get(4)+"\t"
                            +((Vector)va[i]).get(5));
              }
              dlf.println("\nTOTAL:\t \t \t \t"+va.length+"\t"+notokens);
              dlf.close();
            }
        }
      catch (Exception ex) {
        JOptionPane.showMessageDialog((JFrame)parent.getBrowserGUI(), "Error writing freqency list" + ex,
                                      "Error!", JOptionPane.ERROR_MESSAGE);
      }
    }
  }

  class CDescPrinter extends Thread {
    public CDescPrinter () {
      super("Frequency list producer");
    }
    public void run (){
      try {
        if (parent.isStandAlone()){
            
          Dictionary d = parent.getDictionary();
          HeaderDBManager hdbm = parent.getHeaderDBManager();
          boolean cse = parent.isCaseSensitive();
          int [] fks = d.getIndexedFileKeys();

          double sumTTratios = 0;
          int tokenCount = 0;
          int countSubcorpusFiles =0;
          for (int i = 0; i < fks.length; i++) {
            if (parent.isSubCorpusSelectionON()){
               SubcorpusConstraints  sbc=  hdbm.getSubcorpusConstraints(parent.getXQueryWhere());
                if (sbc != null && sbc.acceptFile(  Integer.toString(fks[i]))){
                    FrequencyHash fh = d.getFileFrequencyTable(fks[i], !cse);
                    String fdesc = hdbm.getFileDescription(fks[i]);  
                    String line = fks[i]+Constants.LINE_ITEM_SEP+fdesc+Constants.LINE_ITEM_SEP+
                      fh.getTokenCount()+Constants.LINE_ITEM_SEP+
                      fh.getTypeTokenRatio();
                    // System.err.println("--");
                    sumTTratios += fh.getTypeTokenRatio();
                    tokenCount += fh.getTokenCount();
                    countSubcorpusFiles++;
                    fqlout.println(line);
                }
            }else{
            FrequencyHash fh = d.getFileFrequencyTable(fks[i], !cse);
            String fdesc = hdbm.getFileDescription(fks[i]);  
            String line = fks[i]+Constants.LINE_ITEM_SEP+fdesc+Constants.LINE_ITEM_SEP+
              fh.getTokenCount()+Constants.LINE_ITEM_SEP+
              fh.getTypeTokenRatio();
            //System.err.println("--");
            fqlout.println(line);
            }
          }
           if (parent.isSubCorpusSelectionON()){
            d.printSubCorpusStats(fqlout, (double) sumTTratios/countSubcorpusFiles, tokenCount);
            d.printNoItems(fqlout, tokenCount);
        }else
        {
             d.printCorpusStats(fqlout,!cse);
             d.printNoItems(fqlout, fks.length);
        }
          
         
        } 
        else{
          input = new
            BufferedReader(new
                           InputStreamReader(exturlConnection.getInputStream() ));
        }
      } catch (Exception e) {
        System.err.println("CDescPrinter: " + e);
        JOptionPane.showMessageDialog(null, "Couldn't get corpus description: "+e);
        e.printStackTrace();
      }
    }
  }

  public class FNumberRenderer extends DefaultTableCellRenderer {
    NumberFormat formatter = null;

    public FNumberRenderer() {
      super();
      setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
      formatter = NumberFormat.getInstance();
    }

    public void setValue(Object value) {
      if ((value != null) && (value instanceof Number)) {
        Number numberValue = (Number) value;
        value = formatter.format(numberValue.doubleValue());
      }
      super.setValue(value);
    }
  }

  public class PctRenderer extends DefaultTableCellRenderer {
    NumberFormat formatter = null;

    public PctRenderer() {
      super();
      setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
      formatter = NumberFormat.getPercentInstance();
      formatter.setMinimumFractionDigits(3);
    }

    public void setValue(Object value) {
      if ((value != null) && (value instanceof Number)) {
        Number numberValue = (Number) value;
        value = formatter.format(numberValue.doubleValue());
      } 
      super.setValue(value);
    }
  }

  public class HtmlTextAreaRenderer extends JTextPane implements TableCellRenderer { 
    private final DefaultTableCellRenderer renderer = new DefaultTableCellRenderer(); 
    
    // Column heights are placed in this Map 
    private final Map<JTable, Map<Object, Map<Object, Integer>>> tablecellSizes = new HashMap<JTable, Map<Object, Map<Object, Integer>>>(); 
    
    /** 
     * Creates an HTML-style  text area renderer. 
     */ 
    public HtmlTextAreaRenderer() { 
      //setPreferredSize(new Dimension(450,60));
      setContentType("text/html");
      //setLineWrap(true); 
      //setWrapStyleWord(true);
    } 
    
    /** 
     * Returns the component used for drawing the cell.  This method is 
     * used to configure the renderer appropriately before drawing. 
     * 
     * @param table      - JTable object 
     * @param value      - the value of the cell to be rendered. 
     * @param isSelected - isSelected   true if the cell is to be rendered with the selection highlighted; 
     *                   otherwise false. 
     * @param hasFocus   - if true, render cell appropriately. 
     * @param row        - The row index of the cell being drawn. 
     * @param column     - The column index of the cell being drawn. 
     * @return - Returns the component used for drawing the cell. 
     */ 
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, 
                                                   boolean hasFocus, int row, int column) { 
      // set the Font, Color, etc. 
      renderer.getTableCellRendererComponent(table, value, 
                                             isSelected, hasFocus, row, column); 
      setForeground(renderer.getForeground()); 
      setBackground(renderer.getBackground()); 
      setBorder(renderer.getBorder()); 
      setFont(renderer.getFont()); 
      int h = getContentHeight(renderer.getText());        
      TableColumnModel columnModel = table.getColumnModel(); 
      setSize(columnModel.getColumn(column).getWidth(), h); 
      setText(renderer.getText()); 

      int height_wanted = (int) getPreferredSize().getHeight(); 
      addSize(table, row, column, height_wanted); 
      height_wanted = findTotalMaximumRowSize(table, row); 
      if (height_wanted != table.getRowHeight(row)) { 
        table.setRowHeight(row, height_wanted); 
      } 
      return this; 
    } 
      
    /** 
     * @param table  - JTable object 
     * @param row    - The row index of the cell being drawn. 
     * @param column - The column index of the cell being drawn. 
     * @param height - Row cell height as int value 
     *               This method will add size to cell based on row and column number 
     */ 
    private void addSize(JTable table, int row, int column, int height) { 
      Map<Object, Map<Object, Integer>> rowsMap = tablecellSizes.get(table); 
      if (rowsMap == null) { 
        tablecellSizes.put(table, rowsMap = new HashMap<Object, Map<Object, Integer>>()); 
      } 
      Map<Object, Integer> rowheightsMap = rowsMap.get(row); 
      if (rowheightsMap == null) { 
        rowsMap.put(row, rowheightsMap = new HashMap<Object, Integer>()); 
      } 
      rowheightsMap.put(column, height); 
    } 
      
    /** 
     * Look through all columns and get the renderer.  If it is 
     * also a HtmlTextAreaRenderer, we look at the maximum height in 
     * its hash table for this row. 
     * 
     * @param table -JTable object 
     * @param row   - The row index of the cell being drawn. 
     * @return row maximum height as integer value 
     */ 
    private int findTotalMaximumRowSize(JTable table, int row) { 
      int maximum_height = 0; 
      Enumeration<TableColumn> columns = table.getColumnModel().getColumns(); 
      while (columns.hasMoreElements()) { 
        TableColumn tc = columns.nextElement(); 
        TableCellRenderer cellRenderer = tc.getCellRenderer(); 
        if (cellRenderer instanceof HtmlTextAreaRenderer) { 
          HtmlTextAreaRenderer tar = (HtmlTextAreaRenderer) cellRenderer; 
          maximum_height = Math.max(maximum_height, 
                                    tar.findMaximumRowSize(table, row)); 
        } 
      } 
      return maximum_height; 
    } 
      
    /** 
     * This will find the maximum row size 
     * 
     * @param table - JTable object 
     * @param row   - The row index of the cell being drawn. 
     * @return row maximum height as integer value 
     */ 
    private int findMaximumRowSize(JTable table, int row) { 
      Map<Object, Map<Object, Integer>> rows = tablecellSizes.get(table); 
      if (rows == null) return 0; 
      Map<Object, Integer> rowheights = rows.get(row); 
      if (rowheights == null) return 0; 
      int maximum_height = 0; 
      for (Map.Entry<Object, Integer> entry : rowheights.entrySet()) { 
        int cellHeight = entry.getValue(); 
        maximum_height = Math.max(maximum_height, cellHeight); 
      } 
      return maximum_height; 
    } 
  } // end HtmlTextAreaRenderer

  public static int getContentHeight(String content) {
    JTextPane t =new JTextPane();
    t.setSize(100,Short.MAX_VALUE);
    t.setText(content);
      
    return t.getPreferredSize().height;
  }  


  public class TextAreaRenderer extends JTextArea implements TableCellRenderer { 
    private final DefaultTableCellRenderer renderer = new DefaultTableCellRenderer(); 
    
    // Column heights are placed in this Map 
    private final Map<JTable, Map<Object, Map<Object, Integer>>> tablecellSizes = new HashMap<JTable, Map<Object, Map<Object, Integer>>>(); 
    
    /** 
     * Creates a text area renderer. 
     * (Adapted from public-domain source by Manivel P. at http://manivelcode.blogspot.co.uk/2008/08/how-to-wrap-text-inside-cells-of-jtable.html)
     */ 
    public TextAreaRenderer() { 
      setLineWrap(true); 
      setWrapStyleWord(true); 
    } 
    
    /** 
     * Returns the component used for drawing the cell.  This method is 
     * used to configure the renderer appropriately before drawing. 
     * 
     * @param table      - JTable object 
     * @param value      - the value of the cell to be rendered. 
     * @param isSelected - isSelected   true if the cell is to be rendered with the selection highlighted; 
     *                   otherwise false. 
     * @param hasFocus   - if true, render cell appropriately. 
     * @param row        - The row index of the cell being drawn. 
     * @param column     - The column index of the cell being drawn. 
     * @return - Returns the component used for drawing the cell. 
     */ 
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, 
                                                   boolean hasFocus, int row, int column) { 
      // set the Font, Color, etc. 
      renderer.getTableCellRendererComponent(table, value, 
                                             isSelected, hasFocus, row, column); 
      setForeground(renderer.getForeground()); 
      setBackground(renderer.getBackground()); 
      setBorder(renderer.getBorder()); 
      setFont(renderer.getFont()); 
      setText(renderer.getText()); 
 
      TableColumnModel columnModel = table.getColumnModel(); 
      setSize(columnModel.getColumn(column).getWidth(), 0); 
      int height_wanted = (int) getPreferredSize().getHeight(); 
      addSize(table, row, column, height_wanted); 
      height_wanted = findTotalMaximumRowSize(table, row); 
      if (height_wanted != table.getRowHeight(row)) { 
        table.setRowHeight(row, height_wanted); 
      } 
      return this; 
    } 


    /** 
     * @param table  - JTable object 
     * @param row    - The row index of the cell being drawn. 
     * @param column - The column index of the cell being drawn. 
     * @param height - Row cell height as int value 
     *               This method will add size to cell based on row and column number 
     */ 
    private void addSize(JTable table, int row, int column, int height) { 
      Map<Object, Map<Object, Integer>> rowsMap = tablecellSizes.get(table); 
      if (rowsMap == null) { 
        tablecellSizes.put(table, rowsMap = new HashMap<Object, Map<Object, Integer>>()); 
      } 
      Map<Object, Integer> rowheightsMap = rowsMap.get(row); 
      if (rowheightsMap == null) { 
        rowsMap.put(row, rowheightsMap = new HashMap<Object, Integer>()); 
      } 
      rowheightsMap.put(column, height); 
    } 
      
    /** 
     * Look through all columns and get the renderer.  If it is 
     * also a TextAreaRenderer, we look at the maximum height in 
     * its hash table for this row. 
     * 
     * @param table -JTable object 
     * @param row   - The row index of the cell being drawn. 
     * @return row maximum height as integer value 
     */ 
    private int findTotalMaximumRowSize(JTable table, int row) { 
      int maximum_height = 0; 
      Enumeration<TableColumn> columns = table.getColumnModel().getColumns(); 
      while (columns.hasMoreElements()) { 
        TableColumn tc = columns.nextElement(); 
        TableCellRenderer cellRenderer = tc.getCellRenderer(); 
        if (cellRenderer instanceof TextAreaRenderer) { 
          TextAreaRenderer tar = (TextAreaRenderer) cellRenderer; 
          maximum_height = Math.max(maximum_height, 
                                    tar.findMaximumRowSize(table, row)); 
        } 
      } 
      return maximum_height; 
    } 
      
    /** 
     * This will find the maximum row size 
     * 
     * @param table - JTable object 
     * @param row   - The row index of the cell being drawn. 
     * @return row maximum height as integer value 
     */ 
    private int findMaximumRowSize(JTable table, int row) { 
      Map<Object, Map<Object, Integer>> rows = tablecellSizes.get(table); 
      if (rows == null) return 0; 
      Map<Object, Integer> rowheights = rows.get(row); 
      if (rowheights == null) return 0; 
      int maximum_height = 0; 
      for (Map.Entry<Object, Integer> entry : rowheights.entrySet()) { 
        int cellHeight = entry.getValue(); 
        maximum_height = Math.max(maximum_height, cellHeight); 
      } 
      return maximum_height; 
    } 
  } // end TextAreaRenderer


}// end CorpusDescriptionBrowser
