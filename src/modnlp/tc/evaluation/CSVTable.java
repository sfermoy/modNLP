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
package modnlp.tc.evaluation;
import modnlp.tc.dstruct.*;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.io.Serializable;
/**
 *  Store document ids and their categorisation status value (CSV) w.r.t. a
 *  given category.
 *
 * @author  Saturnino Luz &#60;luzs@acm.org&#62;
 * @version <font size=-1>$Id: CSVTable.java,v 1.1.1.1 2005/05/26 13:59:30 amaral Exp $</font>
 * @see  
*/
public class CSVTable extends HashMap implements Serializable{

  /** 
   * The target category. 
   */
  String category;
  // for archival puporses only
  String pmFileName = "unknown";
  String clistFileName = "unknown";
  
  /**
   * Get the value of PmFileName.
   * @return value of PmFileName.
   */
  public String getPmFileName() {
    return pmFileName;
  }
  
  /**
   * Set the value of PmFileName.
   * @param v  Value to assign to PmFileName.
   */
  public void setPmFileName(String  v) {
    this.pmFileName = v;
  }
  
  /**
   * Get the value of clistFileName.
   * @return value of clistFileName.
   */
  public String getClistFileName() {
    return clistFileName;
  }
  
  /**
   * Set the value of clistFileName.
   * @param v  Value to assign to clistFileName.
   */
  public void setClistFileName(String  v) {
    this.clistFileName = v;
  }
  

  /**
   * Set of documents actually classified (i.e. classified by the
   * target function) as belonging to category
   */
  Set targetDocSet = null;

  /**
   *  Documents selected by CSV; this variable is set by applyThreshold()
   * @see CSVTable#applyThreshold
   */
  Set selectedDocSet = null;

  public void reset () {
    selectedDocSet = null;
  }

  public CSVTable (String cat)
  {
    super();
    targetDocSet = new HashSet();
    setCategory(cat);
  }

  public double getCSV (String id) 
  {
    return ((Double) get(id)).doubleValue();
  } 

  public Object setCSV (String id, double csv){
    return super.put(id, new Double(csv));
  }

  public boolean addToTargetDocSet (String id){
    return targetDocSet.add(id);
  }
  
  public Set getTargetDocSet(){
    return targetDocSet;
  }

  /**
   * Apply thresholding strategy and (as a side-effect)  set the value of selectedDocSet 
   */
  public Set applyThreshold(String tmethod, double generality){
    if ( selectedDocSet != null )
      return selectedDocSet;
    double threshold = 0;
    try { 
      threshold = Double.parseDouble(tmethod);
      tmethod = "UCut";
    } 
    catch (NumberFormatException e ){
      tmethod = "proportional";
    }
    if (tmethod.equals("UCut"))
      selectedDocSet = getAboveThresholdDocSet(threshold);
    else if (tmethod.equals("proportional"))
      selectedDocSet = getProportionalThresholdDocSet(generality);
    else
      System.err.println("ERROR: Unsupported thresholding method");
    return selectedDocSet;
  }

  public Set applyThreshold(double t){
    selectedDocSet = getAboveThresholdDocSet(t);
    return selectedDocSet;
  }

  private Set getAboveThresholdDocSet(double threshold){
    Set out = new HashSet();
    System.out.println("APPLYING CSV THRESHOLD: "+threshold);
    for (Iterator e = this.entrySet().iterator(); e.hasNext() ;)
			{
        Map.Entry kv = (Map.Entry) e.next();
        double csv = ((Double)kv.getValue()).doubleValue(); 
        if ( csv >= threshold)
          out.add(kv.getKey());
      }
    return out;
  }

  private Set getProportionalThresholdDocSet(double generality){
    List v = new ArrayList(this.values());
    Collections.sort(v);
    // size of set of documents to ignore
    long cutoff = v.size() - Math.round(generality * v.size());
    Set out = new HashSet();
    System.out.println("THRESHOLD PROPORTIONAL TO g_cat = "+generality
                       +" (filtering out "+cutoff+" docs)");
    double threshold = 0;
    for (Iterator e = v.iterator(); e.hasNext() ; cutoff--){
      Double csv = (Double) e.next();
      if (cutoff == 0)
        threshold = csv.doubleValue();
    }
    return getAboveThresholdDocSet(threshold);
  }

  
  public MaxMinCSV getMaxMinCSV() {
    double mx = 0, mn = 0, csv = 0;
    for (Iterator e =  this.values().iterator(); e.hasNext() ;){
      csv = ((Double) e.next()).doubleValue();
      if (csv > mx)
        mx = csv;
      else if (csv < mn)
        mn = csv;
    }
    return new MaxMinCSV(mx, mn);
  }

  public int getTPsize(){ // TP  = selected \cap target
    int s = 0;
    for (Iterator e = selectedDocSet.iterator(); e.hasNext() ;)
      if ( targetDocSet.contains(e.next()) )
        s++;
    return s;
  }
  
  public int getFPsize(){ //  |FP| = |target| -  - |selected \cap target| = | target| - |TP|
    return selectedDocSet.size() - getTPsize(); 
  }

  public int getTNsize(){ //  TN = corpus - (selected \cup target), so |TN| = |corpus| - (|selected| + |target| - |selected \cap target|)  
    return getCorpusSize() - 
      (selectedDocSet.size() + targetDocSet.size() - getTPsize());
  }

  public int getFNsize(){ //  |FN| =  |selected| -  |selected \cap target|)  = |selected| - |TP|
    return targetDocSet.size() - getTPsize();
  }

  public int getCorpusSize() { // TP + TN + FP + FN
    return size();
  }

  public double getPrecision() {
    if (selectedDocSet == null)
      System.err.println("ERROR: thresholding must be applied before precision can be calculated");
    double tp = (double) getTPsize();
    //double fp = (double) selectedDocSet.size() - tp;
    return tp / (double) selectedDocSet.size();
  }

  public double getRecall() {
    if (selectedDocSet == null)
      System.err.println("ERROR: thresholding must be applied before recall can be calculated");
    double tp = (double) getTPsize();
    //double fn = (double) targetDocSet.size() - tp;
    return tp / (double) targetDocSet.size();
  }

  public double getAccuracy() {
    if (selectedDocSet == null)
      System.err.println("ERROR: thresholding must be applied before accuracy can be calculated");
    double tp = (double) getTPsize();
    double tn = (double) getTNsize();
    //double fn = (double) targetDocSet.size() - tp;
    return (tp + tn) / (double) getCorpusSize();
  }

  public double getFallout() { // = FP / FP + TN
    if (selectedDocSet == null)
      System.err.println("ERROR: thresholding must be applied before fallout can be calculated");
    double fp = (double) getFPsize();
    double tn = (double) getTNsize();
    return fp / (fp + tn);
  }


  public double getCatGenerality(){
    return (double)targetDocSet.size()/this.size();
  }

  /**
   * Get the value of category.
   * @return value of category.
   */
  public String getCategory() {
    return category;
  }
  
  /**
   * Set the value of category.
   * @param v  Value to assign to category.
   */
  public void setCategory(String  v) {
    this.category = v;
  }
}
