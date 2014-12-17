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
package modnlp.tec.client.plugin;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Stroke;
import java.awt.BasicStroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.uci.ics.jung.graph.ArchetypeVertex;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.decorators.DefaultToolTipFunction;
import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.decorators.EdgeShape;
import edu.uci.ics.jung.graph.decorators.EllipseVertexShapeFunction;
import edu.uci.ics.jung.graph.decorators.PickableVertexPaintFunction;
import edu.uci.ics.jung.graph.decorators.ConstantVertexPaintFunction;
import edu.uci.ics.jung.graph.decorators.StringLabeller;
import edu.uci.ics.jung.graph.decorators.VertexStringer;


import edu.uci.ics.jung.graph.decorators.*;

import edu.uci.ics.jung.graph.impl.DirectedSparseEdge;
import edu.uci.ics.jung.graph.impl.DirectedSparseGraph;
import edu.uci.ics.jung.graph.impl.DirectedSparseVertex;
import edu.uci.ics.jung.visualization.DefaultGraphLabelRenderer;
import edu.uci.ics.jung.visualization.FRLayout;
import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
import edu.uci.ics.jung.visualization.PluggableRenderer;
import edu.uci.ics.jung.visualization.ShapePickSupport;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.AbsoluteCrossoverScalingControl;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ScalingControl;

public class ConcordanceGraph {

  Graph graph;
  VisualizationViewer vv;
  boolean showLabels = true;
  protected static final ConstantVertexPaintFunction KWPAINT = 
    new ConstantVertexPaintFunction(Color.blue, Color.black);
  protected static final String EDGE_WEIGHT = "edge_weight";
  protected static final String VERTEX_FREQUENCY = "vertex_frequency";

  protected NumberEdgeValue edge_weight = new UserDatumNumberEdgeValue(EDGE_WEIGHT);
  protected NumberVertexValue vfrequency = new UserDatumNumberVertexValue(VERTEX_FREQUENCY);

  protected EdgeWeightStrokeFunction edgeWeightStroke;
  protected EdgeStringer edgeStringer;


  public ConcordanceGraph() {

    // create a simple graph for the demo
    graph = new DirectedSparseGraph();
    //StringLabeller sl = StringLabeller.getLabeller(graph);
    
    Vertex[] v = createVertices();
    createEdges(v);
    edgeWeightStroke = new EdgeWeightStrokeFunction(edge_weight);
    edgeStringer = new NumberEdgeValueStringer(edge_weight);

    PluggableRenderer pr = new PluggableRenderer();
    pr.setVertexStringer(new WordVertexStringer(v));
    pr.setEdgeStrokeFunction(edgeWeightStroke);
    pr.setEdgeStringer(edgeStringer);
    pr.setEdgeShapeFunction(new EdgeShape.QuadCurve());
    pr.setVertexShapeFunction(new WordVertexShape(vfrequency));
    pr.setVertexLabelCentering(true);
    /*
      pr.setVertexStringer(new UnicodeVertexStringer(v));
      pr.setVertexPaintFunction(new PickableVertexPaintFunction(pr, Color.lightGray, Color.white,  Color.yellow));
      pr.setGraphLabelRenderer(new DefaultGraphLabelRenderer(Color.cyan, Color.cyan));
      VertexIconAndShapeFunction dvisf =
      new VertexIconAndShapeFunction(new EllipseVertexShapeFunction());
      pr.setVertexShapeFunction(dvisf);
      pr.setVertexIconFunction(dvisf);
      loadImages(v, dvisf.getIconMap());
    */

    vv =  new VisualizationViewer(new FRLayout(graph), pr);
    vv.setPickSupport(new ShapePickSupport());
    vv.setBackground(Color.white);
    
    // add my listener for ToolTips
    vv.setToolTipFunction(new DefaultToolTipFunction());
        
    // create a frome to hold the graph
    final JFrame frame = new JFrame();
    Container content = frame.getContentPane();
    final GraphZoomScrollPane panel = new GraphZoomScrollPane(vv);
    content.add(panel);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    
    final ModalGraphMouse gm = new DefaultModalGraphMouse();
    vv.setGraphMouse(gm);
        
    final ScalingControl scaler = new AbsoluteCrossoverScalingControl();

    JSlider zoom = new JSlider();
    zoom.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          JSlider source = (JSlider)e.getSource();
          if (!source.getValueIsAdjusting()) {
            //System.err.println("=>"+source.getValue());
            scaler.scale(vv, (float)source.getValue()/50, vv.getCenter());
          }
        }
      });
    JCheckBox lo = new JCheckBox("Show Labels");
    lo.addItemListener(new ItemListener(){
        public void itemStateChanged(ItemEvent e) {
          showLabels = e.getStateChange() == ItemEvent.SELECTED;
          vv.repaint();
        }
      });
    lo.setSelected(true);
        
    JPanel controls = new JPanel();
    controls.add(zoom);
    controls.add(lo);
    controls.add(((DefaultModalGraphMouse) gm).getModeComboBox());
    content.add(controls, BorderLayout.SOUTH);
    
    frame.pack();
    frame.setVisible(true);
  }
    
  

  private Vertex[] createVertices() {
    Vertex[] v = new Vertex[10];
    for (int i = 0; i < 10; i++) {
      v[i] = graph.addVertex(new DirectedSparseVertex());
      vfrequency.setNumber(v[i], new Double((double)i/10));
    }
    return v;
  }
  

  /**
   * create edges for this demo graph
   * @param v an array of Vertices to connect
   */
  void createEdges(Vertex[] v) {
    Edge e = new DirectedSparseEdge(v[0], v[1]);
    edge_weight.setNumber(e, new Double(.3));
    graph.addEdge(e);
    e = new DirectedSparseEdge(v[1], v[0]);
    edge_weight.setNumber(e, new Double(1));
    graph.addEdge(e);
    e = new DirectedSparseEdge(v[2], v[0]);
    edge_weight.setNumber(e, new Double(1));
    graph.addEdge(e);
    e = new DirectedSparseEdge(v[0], v[3]);
    edge_weight.setNumber(e, new Double(.5));
    graph.addEdge(e);
    e = new DirectedSparseEdge(v[0], v[4]);
    edge_weight.setNumber(e, new Double(.2));
    graph.addEdge(e);
    e = new DirectedSparseEdge(v[4], v[0]);
    edge_weight.setNumber(e, new Double(.5));
    graph.addEdge(e);
    e = new DirectedSparseEdge(v[5], v[4]);
    edge_weight.setNumber(e, new Double(.6));
    graph.addEdge(e);
    e = new DirectedSparseEdge(v[4], v[5]);
    edge_weight.setNumber(e, new Double(.5));
    graph.addEdge(e);
    e = new DirectedSparseEdge(v[6], v[2]);
    edge_weight.setNumber(e, new Double(1));
    graph.addEdge(e);
    e = new DirectedSparseEdge(v[7], v[6]);
    edge_weight.setNumber(e, new Double(1));
    graph.addEdge(e);
    e = new DirectedSparseEdge(v[5], v[8]);
    edge_weight.setNumber(e, new Double(.3));
    graph.addEdge(e);
    e = new DirectedSparseEdge(v[5], v[9]);
    edge_weight.setNumber(e, new Double(.1));
    graph.addEdge(e);
    e = new DirectedSparseEdge(v[9], v[5]);
    edge_weight.setNumber(e, new Double(1));
    graph.addEdge(e);
  }
  

  public static void main(String[] args) 
  {
    new ConcordanceGraph();
  }


  // inner classes
  //////////////////////////////////////////////
  class WordVertexStringer implements VertexStringer {
    
    Map map = new HashMap();
    String[] labels = {
      "Keyword", "lefandright1", "lef1", "right1", "lefandright12",
      "lefandright2", "lef2", "left3", "right3", "lefandrigh3"
    };
    
    public WordVertexStringer(Vertex[] vertices) {
      for(int i=0; i<vertices.length; i++) {
        map.put(vertices[i], labels[i%labels.length]);
      }
    }
    
    public String getLabel(ArchetypeVertex v) {
      if(showLabels) {
        return (String)map.get(v);
      } else {
        return "";
      }
    }
  }

  private final static class EdgeWeightStrokeFunction
    implements EdgeStrokeFunction
  {
    protected static final Stroke basic = new BasicStroke(1);
    protected static final Stroke heavy = new BasicStroke(2);
    protected static final Stroke dotted = PluggableRenderer.DOTTED;
    
    protected boolean weighted = true;
    protected NumberEdgeValue edge_weight;
    
    public EdgeWeightStrokeFunction(NumberEdgeValue edge_weight)
    {
      this.edge_weight = edge_weight;
    }
    
    public void setWeighted(boolean weighted)
    {
      this.weighted = weighted;
    }
    
    public Stroke getStroke(Edge e)
    {
      if (weighted)
        {
          if (drawHeavy(e))
            return heavy;
          else
            return dotted;
        }
      else
        return basic;
    }
    
    protected boolean drawHeavy(Edge e)
    {
      double value = edge_weight.getNumber(e).doubleValue();
      if (value > 0.7)
        return true;
      else
        return false;
    }   
  }

  private final static class WordVertexShape 
    extends EllipseVertexShapeFunction 
    implements VertexSizeFunction 
  {
    protected NumberVertexValue frequency;
    protected boolean scale = true;
    
    public WordVertexShape(NumberVertexValue vfrequency)
    {
      this.frequency = vfrequency;
      setSizeFunction(this);
    }
        
    public int getSize(Vertex v)
    {
      if (scale)
        return (int)(frequency.getNumber(v).doubleValue() * 30) + 20;
      else
        return 20;
    }
  }

  
} // end ConcordanceGraph
