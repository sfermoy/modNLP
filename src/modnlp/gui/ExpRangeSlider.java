package modnlp.gui;

import java.lang.Math;
import java.text.DecimalFormat;
import java.awt.Insets;
import java.awt.GridBagConstraints;
import java.awt.Dimension;
import javax.swing.JLabel;
import javax.swing.JFrame;
import java.awt.GridBagLayout;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

/**
 * An extension of RangeJSlider to select a range of values on an
 * log/exp scale using two thumb controls.
 * @author S Luz <luzs@acm.org>
 */
public class ExpRangeSlider extends RangeSlider {

  // The in slide value will be scaled according to an exp of this base 
  private int base = 10;
  // the minimum value of the slider (after scaling; ie not the value in the super class)
  private int min = 0;
  // the maximum value
  private int max = 100;

  // granularity controls the spacing of the decimal steps
  private double granularity = 50;

  // the default value for the zero (will be converted to negative)
  private int zero = 40;
  
  private DecimalFormat df = new DecimalFormat("#.##");
  
  public ExpRangeSlider() {
    super();
  }
  
  public void setBase (int v){
    base = v;
  }

  public int getBase (){
    return base;
  }

  public void setZero (int v){
    zero = v;
  }

  public int getZero (){
    return zero;
  }

  
  public void setMin (int v){
    min = v;
  }

  public int getMin (){
    return min;
  }

  public void setMax (int v){
    max = v;
  }

  public int getMax (){
    return max;
  }

  public void setGranularity (double v){
    granularity = v;
    setMaximum(super.getMaximum());
    setMinimum(super.getMinimum());
    //System.err.println("Max: "+max+" Min:"+min+" MaxU"+super.getMaximum()+" MinU"+super.getMinimum());

  }

  public double getGranularity (){
    return granularity;
  }

  public void setMaximum(int v){
    max = (int)Math.round(granularity *
                          ExpRangeSlider.log_n((double)base, (double)v));
    super.setMaximum(max);
  }

  public void setMinimum(int v){
    setMinimum((double) v);
  }
    
  public void setMinimum(double v){
    if (v <= 0)
      min = (int)Math.round(-1 * granularity *
                            ExpRangeSlider.log_n((double)base, zero));
    else
      min = (int) Math.round(granularity*
                             ExpRangeSlider.log_n(base, v));
    super.setMinimum(min);
  }
  
  private static final double log_n(double base, double v){
    return(Math.log(v)/Math.log(base));
  }

  public void setLowerValue(double v){
    //System.err.println("SVd: "+v);
    super.setValue(getUnscaledValue(v));
  }

  //public void setValue(int v){
  //  System.err.println("SVi: "+v);
  //  super.setValue(getUnscaledValue(v));
  //}

  public void setUpperValue(double v){
    super.setUpperValue(getUnscaledValue(v));
  }

  public void setUpperValue(int v){
    super.setUpperValue(getUnscaledValue(v));
  }
  
  public String getExpValueString (double v){
    return df.format(v);
  }
  
  public double getExpValue () {
    int v = super.getValue();
    double e = getScaledExp(v);
    return e;
  }

  public double getExpUpperValue () {
    int v = super.getUpperValue();
    double e = getScaledExp(v);
    return e;
  }

  private double getScaledExp(int v){
    //return Math.pow(base, (v-offset)/div);
    return Math.pow(base, v/granularity);
  }

  private int getUnscaledValue(double v){
    //return Math.pow(base, (v-offset)/div);
    int e;
    if ( v == 0.0)
      e = min;
    else
      e = (int) Math.round(log_n(base, v)*granularity);
    return e;
  }

  

  /**
   * Test slider
   * @param args String[]
   */
  public static void main(String[] args) {
    ExpRangeSlider expsl = new ExpRangeSlider();

    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (Exception ex) {
      ex.printStackTrace();
    }

    SwingUtilities.invokeLater(new Runnable() {
        public void run() {
          JPanel expslPanel = new JPanel(new GridBagLayout());
          JLabel expslValue1 = new JLabel();
          JLabel expslValue2 = new JLabel();
             // range slider settings
          expslValue1.setHorizontalAlignment(JLabel.LEFT);
          expslValue2.setHorizontalAlignment(JLabel.LEFT);        
          expsl.setPreferredSize(new Dimension(220,
                                               expsl.getPreferredSize().height));
          expsl.setMinimum(0);
          expsl.setMaximum(100);
          expsl.setGranularity(50);
          expsl.setZero(50);
          
          expsl.setLowerValue(0);
          expsl.setUpperValue(100);
        
          // Initialize value display.
          expslValue1.setText("0");
          expslValue2.setText(expsl.getExpValueString(expsl.getExpUpperValue()));
          final JLabel expslTitle = new JLabel("Frequency range", JLabel.CENTER);
          
          expslPanel.add(expslValue1,
                         new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                                                GridBagConstraints.NORTHWEST,
                                                GridBagConstraints.NONE,
                                                new Insets(0, 0, 3, 0), 0, 0));
          expslPanel.add(expslTitle,
                         new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                                                GridBagConstraints.NORTH,
                                                GridBagConstraints.NONE,
                                                new Insets(0, 0, 6, 0), 0, 0));
          expslPanel.add(expslValue2,
                         new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                                                GridBagConstraints.NORTHEAST,
                                                GridBagConstraints.NONE,
                                                new Insets(0, 0, 6, 0), 0, 0));
          expslPanel.add(expsl      ,
                         new GridBagConstraints(0, 2, 2, 1, 0.0, 0.0,
                                                GridBagConstraints.NORTHWEST,
                                                GridBagConstraints.NONE,
                                                new Insets(0, 0, 0, 0), 0, 0));

          expsl.addChangeListener(new ChangeListener() {
              public void stateChanged(ChangeEvent e) {
                ExpRangeSlider slider = (ExpRangeSlider) e.getSource();
                double minFreq = slider.getExpValue();
                double maxFreq = slider.getExpUpperValue();
                expslValue1.setText(slider.getExpValueString(minFreq));
                expslValue2.setText(slider.getExpValueString(maxFreq));
              }
            });
          // Create window frame.
          JFrame frame = new JFrame();
          frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
          frame.setResizable(false);
          frame.setTitle("ExpRange Slider Demo");
        
          // Set window content and validate.
          frame.getContentPane().setLayout(new BorderLayout());
          frame.getContentPane().add(expslPanel, BorderLayout.CENTER);
          frame.pack();
        
          // Set window location and display.
          frame.setLocationRelativeTo(null);
          frame.setVisible(true);
        }
      });
  }
  
  
}
