package modnlp.gui;

import java.lang.Math;
import java.text.DecimalFormat;

/**
 * An extension of RangeJSlider to select a range of values on an
 * log/exp scale using two thumb controls.
 */
public class ExpRangeSlider extends RangeSlider {

  private int offset = 50;
  private double div = 50;
  private int base = 10;
  private int min = 0;
  private int max = 150;
  private DecimalFormat df = new DecimalFormat("#.##");
  
  public ExpRangeSlider() {
    super();
  }

  public void setOffset (int v){
    offset = v;
  }

  public int getOffset (){
    return offset;
  }

  public void setBase (int v){
    base = v;
  }

  public int getBase (){
    return base;
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

  public void setDiv (double v){
    div = v;
  }

  public double getDiv (){
    return div;
  }

  public void setMaximum(int v){
    max = v+offset;
    super.setMaximum(max);
  }
  
  public void setUpperValue(int v){
    super.setUpperValue(v+offset);
  }
  
  public String getExpValueString (double v){
    return df.format(v);
  }
  
  public double getExpValue () {
    int v = super.getValue();
    double e = getScaledExp(v);
    //System.err.println("V: "+v+" E:"+e);
    return e;
  }

  public double getExpUpperValue () {
    int v = super.getUpperValue();
    double e = getScaledExp(v);
    //System.err.println("UV: "+v+" UE:"+e);
    return e;
  }

  private double getScaledExp(int v){
    return Math.pow(base, (v-offset)/div);
  }
  
  
  
}
