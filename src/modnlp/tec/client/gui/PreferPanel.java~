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

import java.awt.LayoutManager;
import java.awt.GridLayout;
import java.awt.FlowLayout;
import java.awt.BorderLayout;
import java.awt.Font;
import java.lang.reflect.Array;
import java.util.Vector;
import java.util.Enumeration;
import javax.swing.JFrame;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import javax.swing.JPanel;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.JCheckBox;
import modnlp.tec.client.ConcordanceBrowser;
import javax.swing.JButton;
import javax.swing.JLabel;
import java.awt.Dimension;
import javax.swing.border.EmptyBorder;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import modnlp.tec.client.gui.event.DefaultChangeEvent;
import modnlp.tec.client.gui.event.DefaultChangeListener;
import modnlp.tec.client.gui.event.FontSizeChangeEvent;
import modnlp.tec.client.gui.event.SortHorizonChangeEvent;
import modnlp.tec.client.gui.event.TecDefaultChangeListener;
import modnlp.tec.client.gui.event.DefaultManager;

//import java.awt.*;
/**
 *  This class receives and manages user preferences 
 * 
 * @author  S. Luz &#60;luzs@acm.org&#62;
 * @version <font size=-1>$Id: PreferPanel.java,v 1.3 2001/07/31 16:18:53 luzs Exp $</font>
 * @see  ContextClient
 * @see  Browser
 */
public class PreferPanel extends JFrame 
	implements  ActionListener, ItemListener, DefaultManager 
{

  private static final String COTXBT = "Concordance context ";
  private static final String EXTXBT = "File extract context ";
  private static String [] fseltab = {"6","8","10", "12","14","16"};
  private static  int FSELMAX = Array.getLength(fseltab);
  public int maxContext = 130;
  public int maxExtrCtx = 600;

  private JPanel set1 = new JPanel();
  private JPanel set2 = new JPanel();

  private JComboBox  fontsel;
  private Vector <DefaultChangeListener> defaultListeners = new Vector<DefaultChangeListener> ();
  private String HIDESGML = "";//"Show markup along with text";
  private JTextField context = new JTextField(""+(maxContext/2)+"",3);
  private JTextField extrctx = new JTextField(""+(maxExtrCtx/2)+"",3);
  private JTextField httpProxyField = new JTextField();
  private JTextField headerBaseField = new JTextField();
  private JTextField headersDirField = new JTextField();
  private String httpProxy = null;
  private JComboBox sortctx = new JComboBox();
  public static final int SCTXMAX = 6;
  public int fontSize = 12;
  public int sortContextHorizon = 1;
  public JCheckBox ckbSGML = new JCheckBox(HIDESGML);
  public String stSGML = "no";
  public ConcordanceBrowser targFrame;
	JButton applyButton = new JButton("Apply");
	JButton cancelButton = new JButton("Cancel");
	JButton doneButton = new JButton("Done");
  JLabel COTXBTLabel = new JLabel(COTXBT);
  JLabel EXTXBTLabel = new JLabel(EXTXBT);
  String maxContextLabel = "Number of characters (maximum "+maxContext+")";
  String maxExtrCtxLabel = "Number of characters (maximum "+maxExtrCtx+")";

  /** Set up layout and display
   * @param mother The frame to be repainted when preferences change
   */
  public PreferPanel (ConcordanceBrowser mother){
    super();

    targFrame = mother;

    JPanel title = new JPanel();
    title.add(new JLabel("Preferences:"));
    JPanel prefer = new JPanel();
    JPanel ctrl = new JPanel();

    prefer.setLayout(new GridLayout(8,2,2,2));

    // row 1
    prefer.add(COTXBTLabel);
    prefer.add(context);
    context.setToolTipText(maxContextLabel);

    // row 2
    prefer.add(EXTXBTLabel);
    prefer.add(extrctx);
    extrctx.setToolTipText(maxExtrCtxLabel);

    // row 3
    JLabel xl = new JLabel("Show markup along with text");
    prefer.add(xl);
    prefer.add(ckbSGML);

    fontsel = new JComboBox();
    for (int i = 0 ; i < FSELMAX ; i++)
      fontsel.addItem(fseltab[i]);

    // row 4
    JLabel fl = new JLabel("Font size ");    
    prefer.add(fl);
		fontsel.setSelectedItem(fontSize+"");
    prefer.add(fontsel);

    for (int i = 1 ; i <= SCTXMAX ; i++)
      sortctx.addItem(""+i);

    // row 5
    JLabel sl = new JLabel("Sort context horizon ");    
    prefer.add(sl); 
    prefer.add(sortctx);

    // row 6
    httpProxyField.setMaximumSize(new Dimension(300, 28)); 
    JLabel pl = new JLabel("HTTP Proxy:  ");
    if (System.getProperty("tec.client.runmode") != null &&
        System.getProperty("tec.client.runmode").equals("jnlp"))
      {
        httpProxyField.setEnabled(false);
        httpProxyField.setBackground(prefer.getBackground());
        httpProxyField.setText("Using browser settings!");
      }
    else
      if (System.getProperty("http.proxyHost") != null){
        httpProxyField.setText(System.getProperty("http.proxyHost"));
        if (System.getProperty("http.proxyPort") != null)
          httpProxyField.setText(httpProxyField.getText()
                                 +":"
                                 +System.getProperty("http.proxyPort"));
      }
    prefer.add(pl);
    prefer.add(httpProxyField);

    // row 7
    JLabel hl = new JLabel("Headers URL:  ");
    prefer.add(hl);
    prefer.add(headerBaseField);

    // row 8
    JLabel lh = new JLabel("Local headers:  ");
    prefer.add(lh);
    prefer.add(headersDirField);

    //ctrl.setLayout(new GridLayout(1,3,4,4));

    ctrl.add( cancelButton );
    ctrl.add(new JLabel("       "));
    ctrl.add(applyButton);
    ctrl.add( doneButton );

    //c.weightx = 0.0;

    //ctrl.setFont( new Font("Helvetica", Font.BOLD, 12));
    //prefer.setFont( new Font("Helvetica", Font.PLAIN,12));
    prefer.setBorder(new EmptyBorder(4,4,4,4));
    this.getContentPane().setLayout(new BorderLayout());
    this.getContentPane().add(title, BorderLayout.NORTH);
		this.getContentPane().add(prefer, BorderLayout.CENTER);
    this.getContentPane().add(ctrl, BorderLayout.SOUTH);

		ckbSGML.addItemListener(this);
		cancelButton.addActionListener(this);
		applyButton.addActionListener(this);
		doneButton.addActionListener(this);


  }

  /** Reset value of context; used for initialization and for
   *  redisplay when user enters an invalid value */
  public void resetContext() {
    context.setText(""+maxContext/2);
    maxContextLabel = "Number of characters (maximum "+maxExtrCtx+")";
    context.setToolTipText(maxContextLabel);
  }

  /** Reset value of  extract context; used for initialization and for
   *  redisplay when user enters an invalid value */
  public void resetExtrCtx() {
    extrctx.setText(""+(maxExtrCtx/2)+"");
    maxExtrCtxLabel = "Number of characters (maximum "+maxExtrCtx+")";
    extrctx.setToolTipText(maxExtrCtxLabel);
  }


	/*  public void paint(Graphics g){
    validate();
  }
	*/

  public void actionPerformed(ActionEvent evt)
  {
		//labelMessage("Building concordance list. Please wait...");
		String arg = evt.getActionCommand();
		System.out.println("ARG:"+arg);
		Object source = evt.getSource();
    if(source == applyButton || source == doneButton )
			{
				try {// check if context and extract context are OK
					if ( (new Integer(context.getText())).intValue() 
							 > maxContext )
						resetContext();
					if ( (new Integer(extrctx.getText())).intValue() 
							 > maxExtrCtx )
						resetExtrCtx();
				}
				catch (NumberFormatException e){
					resetExtrCtx();
					resetContext();
				}
				int nfs = (new Integer((String)fontsel.getSelectedItem())).intValue();
				if ( nfs != fontSize )
					{
						fontSize = nfs;
						raiseDefaultChangeEvent(new FontSizeChangeEvent(this, nfs));
					}
				int nsctx = (new Integer((String)sortctx.getSelectedItem())).intValue();
				if ( nsctx != sortContextHorizon )
					{
						sortContextHorizon = nsctx;
						raiseDefaultChangeEvent(new SortHorizonChangeEvent(this, nsctx));
					}
        if ( updatedHTTPProxySelection() ) {
          System.setProperty("http.proxyHost",getHTTPProxyHost());
          System.setProperty("http.proxyPort",getHTTPProxyPort());
        }
      }
		if ( source == doneButton  || source == cancelButton ) 
			dispose();
	}

  public String getHTTPProxy () 
  {
    return httpProxy;
  }

  public String getHTTPProxyHost () 
  {
    try {
      return httpProxy.substring(0,httpProxy.lastIndexOf(":"));
    } 
    catch (Exception e) {
      return "";
    }
  }
  public String getHTTPProxyPort () 
  {
    try {
      return httpProxy.substring(httpProxy.indexOf(":")+1);
    } 
    catch (Exception e) {
      return "";
    }
  }

  public String getHTTPProxySelection () 
  {
    String ht = httpProxyField.getText();
    //ht = ht.substring(ht.indexOF(" ")+1);
    // ht.substring(0,ht.lastIndexOf(" "));
    return ht.trim();
  }
  private boolean updatedHTTPProxySelection() 
  {
    if (getHTTPProxySelection().equals(getHTTPProxy()))
      return false;
    httpProxy = getHTTPProxySelection();
    return true;
  }

	public void itemStateChanged(ItemEvent e) {
		Object source = e.getItemSelectable();

		if (source == ckbSGML){
				if ( e.getStateChange() == ItemEvent.DESELECTED )// set sgml flag
					stSGML = "no";
				else 
					stSGML = "yes";
		}
	}

	public int getContextSize ()
	{
		return (new Integer (context.getText())).intValue();
	}

	public int getExtractContextSize ()
	{
		return (new Integer (extrctx.getText())).intValue();
	}

	public int getSortHorizon ()
	{
		return sortContextHorizon;
	}

	public int getFontSize ()
	{
		return fontSize;
	}

	public String getSGMLFlag ()
	{
		return stSGML;
	}

	public boolean isShowingSGMLFlag ()
	{
          return stSGML.equals("yes");
	}

	public String getHeaderBaseURL ()
	{
		return headerBaseField.getText();
	}

	public void setHeaderBaseURL (String u)
	{
		headerBaseField.setText(u);
	}

	public String getHeadersDir ()
	{
		return headersDirField.getText();
	}

	public void setHeadersDir (String u)
	{
		headersDirField.setText(u);
	}


	// The DefaultManager interface methds

  public void addDefaultChangeListener(DefaultChangeListener obj)
	{
		defaultListeners.addElement(obj);
	}

  public void removeDefaultChangeListener(DefaultChangeListener obj)
	{
		defaultListeners.removeElement(obj);
	}

	private void raiseDefaultChangeEvent (FontSizeChangeEvent e){

		for (Enumeration f = defaultListeners.elements(); 
				 f.hasMoreElements() ;)
			{
				TecDefaultChangeListener li = (TecDefaultChangeListener)f.nextElement();
				li.defaultChanged(e);
			}
	}
	private void raiseDefaultChangeEvent (SortHorizonChangeEvent e){

		for (Enumeration f = defaultListeners.elements(); 
				 f.hasMoreElements() ;)
			{
				TecDefaultChangeListener li = (TecDefaultChangeListener)f.nextElement();
				li.defaultChanged(e);
			}
	}
	private void raiseDefaultChangeEvent (DefaultChangeEvent e){

		for (Enumeration f = defaultListeners.elements(); 
				 f.hasMoreElements() ;)
			{
				DefaultChangeListener li = (DefaultChangeListener)f.nextElement();
				li.defaultChanged(e);
			}
		
	}
}
