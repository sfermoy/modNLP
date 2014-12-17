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
package modnlp.tec.client;

import modnlp.tec.client.gui.event.*;

import java.util.Comparator;
import java.util.Collections;
import java.util.Vector;
import java.util.Iterator;

/**
 *  Receive reply from server (list of concondances) 
 *  and display them from time to time. This class should
 *  in the future be an extension of ListDisplay and so its
 *  updates by itself (thus its design as a class that implements
 *  <code>Runnable</code> rather than extends 
 *  <code>java.lang.Thread</code> .
 * 
 * @author  S. Luz &#60;luzs@acm.org&#62;
 * @version <font size=-1>$Id: SortThread.java,v 1.1.1.1 2000/07/07 16:54:36 luz Exp $</font>
 * @see  
 */
public class SortThread  
  implements Runnable, ConcordanceMonitor{


  private Thread thread = null;
  private ConcordanceVector concVector;
  private Comparator comparator;
  private Vector<ConcordanceDisplayListener> concDisplayListeners = 
    new Vector<ConcordanceDisplayListener>();
		
  public SortThread(ConcordanceVector v, Comparator c) 
  {
    concVector = v;
    comparator = c;			
  }
	
  public void run() 
  {
				
    Collections.sort(concVector, comparator);		
    //long e = (new Date()).getTime();
    //System.out.println("End Time---->:"+e);
    //fireDisplayEvent(0);
    concVector.notifyContentChange();
    fireDisplayEvent(0,ConcordanceDisplayEvent.FIRSTDISPLAY_EVT,
                     "Sort completed.");
    stop();
  }
	
  public void start(){
    if ( thread == null ){
      thread = new Thread(this);
      thread.start();
    }
  }
		
  public void stop() {
    //System.err.println("\nStopping thread here\n");    
    thread = null;
  }
		
  public boolean atWork() {
    return ( thread != null );
  }

  private final void fireDisplayEvent (int from, int evt, String msg) {
    for (Iterator<ConcordanceDisplayListener> p = concDisplayListeners.iterator(); p.hasNext(); )
      {
        ConcordanceDisplayListener cdl = p.next();
        if (cdl != null)
          cdl.concordanceChanged(new ConcordanceDisplayEvent(this, from, evt, msg));
      }
  }

  /* Implement ConcordanceMonitor */
  
  public void addConcordanceDisplayListener(ConcordanceDisplayListener cdl)
  {
    concDisplayListeners.add(cdl);
  }
  
  public void removeConcordanceDisplayListener(ConcordanceDisplayListener cdl)
  {
    concDisplayListeners.remove(cdl);
  }	


}
