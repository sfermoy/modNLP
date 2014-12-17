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

import modnlp.util.PrintUtil;
import modnlp.tc.util.IOUtil;
import java.util.Set;
/**
 *  Evaluate classifier performance for a range of CSV thresholds
 *
 * @author  S Luz &#60;luzs@acm.org&#62;
 * @version <font size=-1>$Id: MakeVaryThresholds.java,v 1.1.1.1 2005/05/26 13:59:30 amaral Exp $</font>
 * @see  
*/
public class MakeVaryThresholds {

  public static int NOSTEPS = 100;

  public static void main(String[] args) {
    try {
      String csvtin = args[0];
      System.err.println("Loading CSVTable...");
      CSVTable csvt = IOUtil.loadCSVTable(csvtin);
      System.out.println("TARGET DOCS for "+csvt.getCategory()+":\n"+csvt.getTargetDocSet());
      csvt.reset(); // reset document selection
      double lowertshd, uppertshd, step;
      if (args.length > 1 ){
        lowertshd = Double.parseDouble(args[1]);
        uppertshd = Double.parseDouble(args[2]);
        step = Double.parseDouble(args[3]);
      }
      else {
        MaxMinCSV mm = csvt.getMaxMinCSV();
        uppertshd = mm.max;
        lowertshd = mm.min;
        step = (uppertshd - lowertshd) / NOSTEPS;
      }
      int catcount = 0;
      int csize = 0;
      double gen = csvt.getCatGenerality();
      String clistfn = csvt.getClistFileName();
      String pmodel = csvt.getPmFileName();
      Set selected;
      for (double i = lowertshd; i < uppertshd ; i += step)
        {
          PrintUtil.printNoMove("Varying CSV threshold ...", (int) i);
          selected = csvt.applyThreshold(i);
          //System.out.println("SELECTED DOCS:\n"+selected);
          double acc = csvt.getAccuracy(), prc = csvt.getPrecision(), 
            rec = csvt.getRecall(), fot = csvt.getFallout();
          int tp = csvt.getTPsize(), fp = csvt.getFPsize(), 
            tn = csvt.getTNsize(), fn = csvt.getFNsize();
          System.out.println("EFFECTIVENESS:\n"
                             +"  accuracy =  "+acc
                             +"  precision = "+prc
                             +"  recall = "+rec
                             +"  fallout = "+fot
                             +"\n  TP = "+tp+" FP = "+fp
                             +" TN = "+tn+" FN = "+fn
                             );
          System.out.println("STATSHEADER:acc,prc,rec,fot,gen,tp,fp,tn,fn,clist,model,tshold");
          System.out.println("STATSLINE:"+
                             acc+
                             ","+prc+
                             ","+rec+
                             ","+fot+
                             ","+gen+
                             ","+tp+","+fp+","+tn+","+fn+
                             ","+clistfn+
                             ","+pmodel+
                             ","+i
                             );
        }
      PrintUtil.donePrinting();
    }
    catch (Exception e){
      System.err.println("USAGE:");
      System.err.println(" MakeVaryThresholds csvtfile low_threshold high_threshold step\n");
      System.err.println("SYNOPSIS:");
      System.err.println("  Record classifier effectiveness for CSV threshold values\n");
      System.err.println("  varying from 'low_threshold' to 'high_threshold incrementally'\n");
      System.err.println("  by 'step'.\n");
      System.err.println("ARGUMENTS:");
      System.err.println(" csvtfile: a serialised instance of modnlp.tc.eval.CSVTable\n");
      System.err.println(" low_threshold: lower bound for CSV threshold\n");
      System.err.println(" high_threshold: upper bound for CSV threshold\n");
      System.err.println(" step: CSV value increment\n");
      e.printStackTrace();
    } 
  }
}
