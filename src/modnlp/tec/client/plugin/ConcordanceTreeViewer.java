/**
 *  (c) 2008 S Luz <luzs@acm.org>
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

import modnlp.tec.client.Plugin;
import modnlp.tec.client.ConcordanceBrowser;
import modnlp.tec.client.ConcordanceObject;
import modnlp.tec.client.gui.SubcorpusCaseStatusPanel;
import modnlp.idx.inverted.TokeniserRegex;
import modnlp.idx.inverted.TokeniserJP;
import modnlp.util.Tokeniser;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import javax.swing.JTextField;

import java.util.Vector;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

import prefuse.Constants;
import prefuse.controls.ControlAdapter;
import prefuse.data.Table;
import prefuse.data.Tree;
import prefuse.data.Tuple;
import prefuse.data.Node;
import prefuse.data.Edge;
import prefuse.data.SpanningTree;
import prefuse.util.ui.JFastLabel;
import prefuse.util.FontLib;
import prefuse.visual.VisualItem;




/**
 *  Basic concordance tree generator. A concordance tree is a prefix
 *  tree (trie) encoding the context to the right of a concordance; or a
 *  (i.e. a right-to-left prefix tree) encoding the left
 *  context of a concordance
 *
 * @author  S Luz &#60;luzs@acm.org&#62;
 * @version <font size=-1>$Id: $</font>
 * @see  
*/

public class ConcordanceTreeViewer extends JFrame
  implements Runnable, Plugin
{
  

  public static final String NAME = "name";
  public static final String NODECOUNT = "nodecount";
  public static final String ROWCOUNT = "colcount";
  public static final int MAXCOLS = 200;

  public static final int GROW = 1;
  public static final int PRUNE = 2;

  private int current_action = GROW;
  private double prune_cutoff = 0.05;

  private boolean case_sensitive = false;
  /**
   * Describe left_context here.
   */
  private boolean left_context = false;

  private Thread thread;
  private JFrame thisFrame = null;
  private Tree tree = null;


  JLabel statsLabel = new JLabel("                            ");
  SubcorpusCaseStatusPanel sccsPanel;

  private JProgressBar progressBar;
  JPanel tpanel = new JPanel(new BorderLayout());

  private ConcordanceTree conc_tree =  null;

  private static String title = new String("MODNLP Plugin: ConcordanceTreeViewer 0.1"); 
  private ConcordanceBrowser parent = null;
  private boolean guiLayoutDone = false;

  public ConcordanceTreeViewer() {
    thisFrame = this;
  }

  // plugin interface method
  public void setParent(Object p){
    parent = (ConcordanceBrowser)p;
    sccsPanel = new SubcorpusCaseStatusPanel(p);

  }

  // plugin interface method
  public void activate() {
    if (guiLayoutDone){
      setVisible(true);
      return;
    }

    JButton dismissButton = new JButton("Quit");
    JButton growTreeButton = new JButton("Grow tree ->");
    JButton growTreeLeftButton = new JButton("<- Grow tree");
    
    dismissButton.addActionListener(new ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
          thisFrame.setVisible(false);
        }});

    growTreeButton.addActionListener(new ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
          stop();
          setLeftContext(false);
          current_action = GROW;
          start(); 
          }});

    growTreeLeftButton.
      addActionListener(new ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
            setLeftContext(true);
            current_action = GROW;
            stop(); start();
          }});



    JPanel cop = new JPanel();
    cop.setBorder(javax.swing.BorderFactory.createEtchedBorder());
    final JTextField cutoff = new JTextField("0.05", 4);
    JButton pruneTreeButton = new JButton("Prune tree");
    cop.add(pruneTreeButton);
    cop.add(cutoff);

    pruneTreeButton.
      addActionListener(new ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
            setPruneCutoff((new Double(cutoff.getText())).doubleValue());
            current_action = PRUNE;
            stop(); start();
          }});


    JPanel pas = new JPanel();
    pas.add(growTreeLeftButton);
    pas.add(growTreeButton);
    pas.add(dismissButton);
    pas.add(cop);

    //getContentPane().add(pan, BorderLayout.NORTH);
    //getContentPane().add(scrollPane, BorderLayout.CENTER);
    

    //addFocusListener(this);
    //textArea.setFont(new Font("Courier", Font.PLAIN, parent.getFontSize()));
    growTreeButton.setEnabled(true);
    
    progressBar = new JProgressBar(0,800);
    progressBar.setStringPainted(true);

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

    
    //JPanel tpanel = new JPanel();
    //tpanel.setSize(ConcordanceTree.WIDTH, ConcordanceTree.HEIGHT);
    //setSize(ConcordanceTree.WIDTH+10, ConcordanceTree.HEIGHT+10);

    Tree tree = new Tree();
    Table ntable = tree.getNodeTable();
    ntable.addColumn(NAME,String.class);
    ntable.addColumn(NODECOUNT, int.class);
    ntable.addColumn(ROWCOUNT,int.class);
    Node cnode = tree.addRoot();
    cnode.setString(NAME,"Concordance Tree");
    cnode.setInt(NODECOUNT,1);
    cnode.setInt(ROWCOUNT,1);
    
    // set visualisation display
    setDisplay(tree,1);

    /*
      conc_tree = new ConcordanceTree(tree, NAME);
      conc_tree.setBackground(Color.WHITE);
      conc_tree.setForeground(Color.BLACK);
      conc_tree.setFont(FontLib.getFont("Tahoma", Font.PLAIN, 8));
      
      final JFastLabel title = new JFastLabel("                 ");
      title.setPreferredSize(new Dimension(350, 20));
      title.setVerticalAlignment(SwingConstants.BOTTOM);
      title.setBorder(BorderFactory.createEmptyBorder(3,0,0,0));
      title.setFont(FontLib.getFont("Tahoma", Font.PLAIN, 16));
      title.setBackground(Color.WHITE);
      title.setForeground(Color.BLACK);
      
      
      conc_tree.addControlListener(new ControlAdapter() {
      public void itemEntered(VisualItem item, MouseEvent e) {
      if ( item.canGetString(NAME) )
      title.setText(item.getString(NAME));
      }
      public void itemExited(VisualItem item, MouseEvent e) {
      title.setText(null);
      }
      });
      tpanel.setBackground(Color.WHITE);
      tpanel.setForeground(Color.BLACK);
      tpanel.add(conc_tree, BorderLayout.CENTER);
    */
    
    getContentPane().add(pas, BorderLayout.NORTH);
    getContentPane().add(tpanel, BorderLayout.CENTER);
    getContentPane().add(pabottom, BorderLayout.SOUTH);

    pack();
    setVisible(true);
    guiLayoutDone = true;
    //    growTree(); 
  }

  /**
   * Get the <code>Left_context</code> value.
   *
   * @return a <code>boolean</code> value
   */
  public final boolean isLeftContext() {
    return left_context;
  }

  /**
   * Set the <code>Left_context</code> value.
   *
   * @param newLeft_context The new Left_context value.
   */
  public final void setLeftContext(final boolean newLeft_context) {
    this.left_context = newLeft_context;
  }


  public void start(){
    if ( thread == null ){
      thread = new Thread(this);
      thread.setPriority (Thread.MIN_PRIORITY);
      thread.start();
    }
  }

  public void stop() {
    if ( thread != null ){
      thread.stop();
      
      thread = null;
    }
  }

  public void run(){
    switch(current_action){
    case GROW:
      growTree(); break;
    case PRUNE:
      pruneTree(); break;
    default:
      growTree(); break;
    }
  }

  public void pruneTree(){
    Tree pt = getPrunedTree(tree);
    setDisplay(pt,pt.getRoot().getInt(ROWCOUNT));
  }

  public void growTree() {
    try
      {
        tree = null;
        sccsPanel.updateStatus();
        //ConcordanceObject[] va =  parent.getConcArray().concArray;
        //String tknregexp  = parent.getClientProperties().getProperty("tokeniser.regexp");
        Vector columns = new Vector();
        Tokeniser ss;
        int la = parent.getLanguage();
        switch (la) {
        case modnlp.Constants.LANG_EN:
          ss = new TokeniserRegex("");
          break;
        case modnlp.Constants.LANG_JP:
          ss = new TokeniserJP("");
            break;
        default:
          ss = new TokeniserRegex("");
          break;
        }

        //int[] colcounts = new int[MAXCOLS];
        //Arrays.fill(colcounts,0);
        int ct = 0;
        progressBar.setString("Growing tree...");
        int nrows = parent.getConcordanceVector().size();
        progressBar.setMaximum(nrows);
        progressBar.setValue(ct++);
        for (Iterator<ConcordanceObject> p = parent.getConcordanceVector().iterator(); p.hasNext(); ){
          ConcordanceObject co = p.next();
          progressBar.setValue(ct++);
          if (co == null)
            break;
          Object[] tkns;
          if (isLeftContext()){
            Object[] t = (ss.split(co.getLeftContext()+" "+parent.getKeywordString())).toArray();
            tkns = new Object[t.length];
            int j = t.length-1;
            for(int i=0; i<t.length; i++)
              tkns[j-i] = t[i];
          }
          else
            tkns = (ss.split(co.getKeywordAndRightContext())).toArray();
          Node cnode = null;
          String ctoken = (String)tkns[0];
          if (!case_sensitive)
            ctoken = ctoken.toLowerCase();

          // initialise (keyword/root node)
          if (tree == null){
            tree = new Tree();
            Table ntable = tree.getNodeTable();
            ntable.addColumn(NAME,String.class);
            ntable.addColumn(NODECOUNT, int.class);
            ntable.addColumn(ROWCOUNT,int.class);
            cnode = tree.addRoot();
            cnode.setString(NAME,ctoken);
            cnode.setInt(NODECOUNT, 1);
            cnode.setInt(ROWCOUNT, nrows);
            //colcounts[0]++;
          }
          else { 
            // update root node frequencies
            cnode = tree.getRoot();
            cnode.setInt(NODECOUNT, cnode.getInt(NODECOUNT)+1);
            //colcounts[0]++;
            //cnode.setInt(ROWCOUNT, cnode.getInt(ROWCOUNT)+1);
          }
          //System.err.println("ctoken = "+ctoken);
          // update trie by reading string from left to right 
          for (int i = 1; i < tkns.length; i++){
            ctoken = (String)tkns[i];

            if (!case_sensitive)
              ctoken = ctoken.toLowerCase();

            boolean found = false;
            //colcounts[i]++;

            for (Iterator children = cnode.children(); children.hasNext();){
              Node n = (Node)children.next();
              // --
              //System.err.println(new String(ch)+"c = "+n.getString(NAME));
              // ---
              if(ctoken.equals(n.getString(NAME))){// found a matching child
                cnode = n;
                cnode.setInt(NODECOUNT, cnode.getInt(NODECOUNT)+1);
                //cnode.setInt(ROWCOUNT, cnode.getInt(ROWCOUNT)+1);
                //System.err.println(new String(ch)+"c++ = "+n.getString(NAME)+" ct="+cnode.getInt(NODECOUNT));
                found = true;
                break;
              }
            } // end iteration 

            if (found)
              continue;
            // couldn't find a matching child; make one
            Node n = tree.addChild(cnode);
            n.setString(NAME,ctoken);
            n.setInt(NODECOUNT, 1);
            n.setInt(ROWCOUNT, nrows);
            //Edge e = tree.addChildEdge(cnode,n);
            //System.err.println(new String(ch)+"c = "+n.getString(NAME)+" e ="+e);
            cnode = n;
          } // end updtate trie for words to the right of kw
        } // end update trie for whole sentence
        progressBar.setString("Done");
        setDisplay(tree,nrows);
      } // end try
    catch (Exception ex) {
      ex.printStackTrace(System.err);
      JOptionPane.showMessageDialog(null, "Error creating concordance tree" + ex,
                                    "Error!", JOptionPane.ERROR_MESSAGE);
    }
  }


  private Tree updateColCounts(Tree tree, int[] c) {
    for (Iterator spant = tree.tuples(); spant.hasNext();) {
      Tuple n = (Tuple)spant.next();

      if (!(n instanceof Node) )
        continue;

      int d = ((Node)n).getDepth();
      ((Node)n).setInt(ROWCOUNT, c[d]);
      //System.err.println(new String(ch)+"c++ = "+n.getString(NAME)+" ct="+cnode.getInt(NODECOUNT));
    }
    return tree;
  }


 private Tree getPrunedTree(Tree tree) {
   Node r = tree.getRoot();
   Node[] togo = new Node[r.getChildCount()];
   Arrays.fill(togo,null);
   int i = 0;
   for (Iterator spant = r.children(); spant.hasNext();) {
     Node n = (Node)spant.next();
      System.err.println("n="+n.getString(NAME)+" nct="+n.getInt(NODECOUNT)+" cct="+n.getInt(ROWCOUNT)+" nrt="+((double)n.getInt(NODECOUNT)/n.getInt(ROWCOUNT)));
      if (((double)n.getInt(NODECOUNT)/n.getInt(ROWCOUNT)) < prune_cutoff){
        togo[i++] = n;
      }
      /*     else {
        int j = 0;
        Node[] togo1 = new Node[n.getChildCount()];
        Arrays.fill(togo1,null);
        for (Iterator<Node> st1 = n.children(); st1.hasNext();) {
          Node n1 = st1.next();
          System.err.println("n1="+n1.getString(NAME)+" nct="+n1.getInt(NODECOUNT)+" cct="+n1.getInt(ROWCOUNT)+" nrt="+((double)n1.getInt(NODECOUNT)/n1.getInt(ROWCOUNT)));
          if (((double)n1.getInt(NODECOUNT)/n1.getInt(ROWCOUNT)) < prune_cutoff){
            togo1[j++] = n1;
          }
        }
        for (int k = 0; k < togo1.length; k++)
          if (togo1[k] != null)
            tree.removeChild(togo1[k]);
      }
      */  
      //System.err.println(new String(ch)+"c++ = "+n.getString(NAME)+" ct="+cnode.getInt(NODECOUNT));
      }
   for (int k = 0; k < togo.length; k++)
     if (togo[k] != null){
       //tree.removeChildEdge(togo[k].getParentEdge());
       tree.removeChild(togo[k]);
     }
   System.err.println("-------------valid tree? "+tree.isValidTree());
   for (Iterator spant = r.children(); spant.hasNext();) {
     Node n = (Node)spant.next();
     System.err.println("----------n="+n.getString(NAME)+" nct="+n.getInt(NODECOUNT));
   }
   
   return tree;
 }

 private Tree getPrunedTreeTODO(Tree tree) {
    for (Iterator spant = tree.tuples(); spant.hasNext();) {
      Tuple t = (Tuple)spant.next();

      if (!(t instanceof Node) )
        continue;

      Node n = (Node)t;

      if (n.getInt(NODECOUNT)/n.getInt(ROWCOUNT) < prune_cutoff){
        tree.removeChild(n);
      }

      //System.err.println(new String(ch)+"c++ = "+n.getString(NAME)+" ct="+cnode.getInt(NODECOUNT));
    }
    return tree;
  }


  private void setDisplay(Tree t, int rc){
    conc_tree = new ConcordanceTree(t, NAME);
    
    conc_tree.setRowCount(rc);
    conc_tree.setMinFreqRatio(1f/rc);
    if (isLeftContext())
      conc_tree.setOrientation(Constants.ORIENT_RIGHT_LEFT);
    conc_tree.setDefaultTreeFont(FontLib.getFont("Tahoma", Font.PLAIN, 10));
    
    
    /*
    final JFastLabel title = new JFastLabel("                 ");
    title.setPreferredSize(new Dimension(350, 20));
    title.setVerticalAlignment(SwingConstants.BOTTOM);
    title.setBorder(BorderFactory.createEmptyBorder(3,0,0,0));
    title.setFont(FontLib.getFont("Tahoma", Font.PLAIN, 16));
    title.setBackground(Color.WHITE);
    title.setForeground(Color.BLACK);
    
    
    conc_tree.addControlListener(new ControlAdapter() {
        public void itemEntered(VisualItem item, MouseEvent e) {
          if ( item.canGetString(NAME) )
            title.setText(item.getString(NAME));
        }
        public void itemExited(VisualItem item, MouseEvent e) {
          title.setText(null);
        }
      });
    */

     tpanel.removeAll();
     conc_tree.setBackground(Color.WHITE);
     conc_tree.setForeground(Color.BLACK);
     tpanel.add(conc_tree, BorderLayout.CENTER);
     System.err.println("set tree"+conc_tree.getDefaultTreeFont());
    //panel.add(box, BorderLayout.SOUTH);
  }

  private void setPruneCutoff(double c){
    prune_cutoff = c;
  }


}

