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

import modnlp.tec.client.gui.event.*;

import java.awt.Font;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.FontMetrics;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.StringTokenizer;



import java.awt.Font;
import java.awt.Event;
import java.awt.Color;
import java.awt.Container;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.Date;
import org.xml.sax.Parser;
import org.xml.sax.InputSource;
import org.xml.sax.DocumentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.helpers.ParserFactory;
import javax.swing.JFrame;
import java.awt.event.ActionListener;
import javax.swing.JEditorPane;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import java.awt.event.ActionEvent;



/**
 *  Object to display header and file extrac information
 *  
 * @author  S. Luz &#60;luzs@acm.org&#62;
 * @version <font size=-1>$Id: FullTextWindow.java,v 1.2 2000/07/11 18:06:49 luz Exp $</font>
 * @see ContextClient  
*/
public class FullTextWindow extends JFrame
 implements ActionListener, TecDefaultChangeListener{

  private int MAXLIN = 40;
  /** This array stores the text to be displayed */
  public String text[];
  private boolean testConc = true;
  private int height, width;
  private JEditorPane outPane = new  JEditorPane("text/html", ""); // new  JTextArea(20,40);
  private JButton dismissButton =  new JButton("Dismiss");
  private boolean useText = false;
  private final String FONTNAME = "Sans Serif"; 
  
  /** Initialize Extract and/or Header window
   * with title */
  public FullTextWindow(String title)
  {
    super(title);
    outPane.setEditable(false); 
    outPane.setFont( new Font(FONTNAME, Font.PLAIN, 14) );
    outPane.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, true);
    useText = true;
    textInit();
  }
  
  /** Initialize Extract and/or Header window
   * with <code>title</code> and put <code>fulltext</code>
   * in it. */
  public FullTextWindow(String title, String[] fullText)
  {
    super(title);
    outPane.setEditable(false); 
    useText = true;
    text = fullText;
    outPane.setFont( new Font(FONTNAME, Font.PLAIN, 14) );
    outPane.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, true);
    textInit();
    displayText(text);
  }

  /** Initialize Extract and/or Header window
   * with <code>title</code> and put <code>fulltext</code>
   * in it. Full*/
  public FullTextWindow(String title, StringBuffer fullText)
  {
    super(title);
    outPane.setEditable(false); 
    useText = true;
    outPane.setFont( new Font(FONTNAME, Font.PLAIN, 14) );
    outPane.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, true);
    textInit();
    outPane.setText(fullText.toString());
		outPane.setCaretPosition(0);
  }

 public FullTextWindow(String title, String fullText)
  {
    super(title);
    outPane.setEditable(false); 
    useText = true;
    outPane.setFont( new Font(FONTNAME, Font.PLAIN, 14) );
    textInit();
    outPane.setText(fullText.toString());
		outPane.setCaretPosition(0);
  }



  /** Initialize Extract and/or Header window
   * with <code>title</code>, size <code>hxw</code> 
   * and put <code>fulltext</code>
   * in it. */
  public FullTextWindow(String title, String fullText[], int h, int w)
  {
    super(title);
    outPane.setEditable(false); 
    setBackground(Color.white);
    outPane.setFont( new Font(FONTNAME, Font.PLAIN, 14) );
    text = fullText;
    height = h;
    width = w;
  }

  /** Initialize window control/dismiss panel
   */
  private void textInit()
  {
    JPanel ctrl = new JPanel();

    ctrl.setLayout(new GridLayout(1,3));
    getContentPane().setLayout(new BorderLayout());
    outPane.setBackground(Color.white);
    ctrl.add( new JLabel(""));
    ctrl.add(dismissButton);
    ctrl.add( new JLabel(""));
    JScrollPane areaScrollPane = new JScrollPane(outPane);
    getContentPane().add("North",ctrl);
    getContentPane().add("Center",areaScrollPane);
    dismissButton.addActionListener(this);
  } 

	/*
  public void paint(Graphics g)
  {
    if (!useText)
      displayFile(g);
		
  }
	*/

  public void actionPerformed(ActionEvent evt)
  {
    if(evt.getSource() == dismissButton)
			dispose();
  }


  /** Display text extract indenting it to 80 cols,
   *  single out <code>key</code> and select it 
   * @deprecated
   */
  public void displayConcExtract(String key)
  {
    displayText();
  }
	
  private String indentText(int mxc, String stri)
  {
    StringTokenizer buf = new StringTokenizer(stri," ",false);  
    int ct = 0;
    String tmp = "";
    String out = "";
		
    while ( buf.hasMoreTokens() ){
      tmp = buf.nextToken()+" ";
      ct += tmp.length();
      if (ct > mxc ){
				tmp += "\n";
				ct = 0;
			}
      out += tmp;
    }
    return out;
  }
	
  public void setContentType(String t){
    outPane.setContentType(t);
  }

  /** Display text stored in the local array of <code>String</code>s
   *  <code>text</code>. Normally used to display header files 
   *  @see #text
   */
  public void displayText() {
    displayText(text);
  }
	
	
  /** Display text stored in an array of <code>String</code>s
   * Normally used to display header files */
  public void displayText(String[] text)  {
    StringBuffer sb = new StringBuffer();
    for(int count = 0; count < 1000 && text[count] != null; count++)
      {
				sb.append(text[count]);
      }
    outPane.setText(sb.toString());
    validate();
  }

  public void displayText(String t)  {
    outPane.setText(t);
    validate();
  }
	
  /** Reset this windows font size if current size 
   *  different from <code>nsz</code> 
   *  @return <code>true</code> if font has been reset 
   */
  public boolean resetFontIfChanged(int nsz){                 
    if ( outPane.getFont().getSize() != nsz ){
      outPane.setFont( new Font(FONTNAME, Font.PLAIN, nsz) );
      return true;
    }
    else 
      return false;
  }

	// The TEC default change interface
  public void defaultChanged(DefaultChangeEvent e){
  }
  public void defaultChanged(SortHorizonChangeEvent e){
  }

  public void defaultChanged(FontSizeChangeEvent e){
    outPane.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, true);
    if ( resetFontIfChanged(e.getNewSize()) )
      validate();
  }
}

