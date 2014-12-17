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
package modnlp.tec.client;

import modnlp.Constants;
import java.util.StringTokenizer;
import java.util.Iterator;
import java.util.Vector;
import java.util.EventListener;

import javax.swing.ListModel;
import javax.swing.event.EventListenerList;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

/**
 *  Manage the array of concordances returned by the
 *  server. Introduced as a replacement for ConcArray
 * 
 * @author  S. Luz &#60;luzs@acm.org&#62;
 * @version <font size=-1>$Id: ConcArray.java,v 1.3 2008/10/05 13:48:05 luzs Exp $</font>
 * @see ContextClient */
public class ConcordanceVector extends Vector<ConcordanceObject> implements ListModel{

  public static final String SEPTOKEN = " |'`\"-_,.?!;:<>{}[]=+/\\%$*&()";
  public static final int UPDATE_INTERVAL = 20;
  
  private boolean fireChangeContinuously = false; 
  private int lengthLongestFname = 0;
  private int sortContextHorizon;
  private int halfConcordance;
  private int index = 0;
  private int BUFFER_SIZE = 300;
  private int lastindex = BUFFER_SIZE;
  private EventListenerList listenerList = new EventListenerList();
  private ListDataEvent ldEvent = null;
  private int language = Constants.LANG_EN;
  private boolean punctuation = false;

  public ConcordanceVector (){
    super();
    sortContextHorizon = 0;
    halfConcordance = 0;
  }

  public ConcordanceVector (int size){
    super(size);
    sortContextHorizon = 0;
    halfConcordance = 0;
  }

  public final void setLanguage(int l){
    language = l;
  }

  public final int getLanguage(){
    return language;
  }

  public final void setPunctuation(boolean p){
    this.punctuation = p;
  }

  public final boolean getPunctuation(){
    return punctuation;
  }


  public final int getLengthLongestFname(){
    return lengthLongestFname;
  }

  public final int getSortContextHorizon(){
    return sortContextHorizon;
  }

  public final void setSortContextHorizon(int ch){
    sortContextHorizon = ch;
  }

  public final void setFireChangeContinuously(boolean v){
    fireChangeContinuously = v;
  }

  public final boolean isFireChangeContinuously(){
    return fireChangeContinuously;
  }

  public final void setHalfConcordance(int hc){
    halfConcordance = hc;
  }

  public final int getHalfConcordance(){
    return halfConcordance;
  }

  public boolean add(String inputString)
  {
    //System.out.println("HERE|"+inputString);

    ConcordanceObject co = new ConcordanceObject(inputString, this);
    
    if( co.concordance.equals("null") || co.concordance.equals("") )
      {
        return false;
      }
    else {
      int lfn = co.getFilenameLength();
      if (lfn > lengthLongestFname)
        lengthLongestFname = lfn;
      co.setIndex(index);
      super.add(co);
      if (fireChangeContinuously){
        fireIntervalAdded(index, index);
        lastindex = index;
      }
      else if (index == lastindex ) {
        //System.err.println("======="+(index-BUFFER_SIZE)+"========"+index);
        fireIntervalAdded(index-BUFFER_SIZE, index);
        lastindex = index+BUFFER_SIZE;
      }
      index++;
      return true;
    }
  }

  // to be invoked when last element has been added
  public void doneAdding(){
    if (!fireChangeContinuously && (index < lastindex)){
      fireIntervalAdded(index, lastindex);
      lastindex = index;
    }
  }


  public void clear(){
    int ei = this.size()-1;
    super.clear();
    index = 0;
    lastindex = BUFFER_SIZE;
    if (ei > 0)
      fireIntervalRemoved(0,ei);
  }

  public int getSize(){
    return size();
  }

  public Object getElementAt(int index) {
    return elementAt(index);
  }

  public void dumpValues()
  {
    for (Iterator<ConcordanceObject> p = iterator(); p.hasNext(); )
      {
        ConcordanceObject co = p.next();
        System.out.println("Concordance: "+ co.concordance);
        System.out.println("Filename: "+ co.filename);
        System.out.println("Filepos: "+ co.filepos);
      }    
  }

  // notifyContentChange: to be invoked when an external program (such
  // as sort) changes all contents of this listmodel
  public void notifyContentChange(){
    fireContentsChanged(0, size()-1);
  }

  // ListModel interface (now implemented in gui.ConcordanceListModel)
  public void addListDataListener(ListDataListener l) {
    listenerList.add(ListDataListener.class, l);
  }

  public void removeListDataListener(ListDataListener l) {
    listenerList.remove(ListDataListener.class, l);
  }

  protected void fireContentsChanged(int index0, int index1)
  {
    Object[] listeners = listenerList.getListenerList();
    ListDataEvent e = null;
    
    for (int i = listeners.length - 2; i >= 0; i -= 2) {
      if (listeners[i] == ListDataListener.class) {
        if (e == null) {
          e = new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, index0, index1);
        }
        ((ListDataListener)listeners[i+1]).contentsChanged(e);
      }
    }
  }

  protected void fireIntervalAdded(int index0, int index1)
  {
    Object[] listeners = listenerList.getListenerList();
    ListDataEvent e = null;
    
    for (int i = listeners.length - 2; i >= 0; i -= 2) {
      if (listeners[i] == ListDataListener.class) {
        if (e == null) {
          e = new ListDataEvent(this, ListDataEvent.INTERVAL_ADDED, index0, index1);
        }
        ((ListDataListener)listeners[i+1]).intervalAdded(e);
      }
    }
  }
  
  
  protected void fireIntervalRemoved(int index0, int index1)
  {
    Object[] listeners = listenerList.getListenerList();
    ListDataEvent e = null;
    
    for (int i = listeners.length - 2; i >= 0; i -= 2) {
      if (listeners[i] == ListDataListener.class) {
        if (e == null) {
          e = new ListDataEvent(this, ListDataEvent.INTERVAL_REMOVED, index0, index1);
        }
        ((ListDataListener)listeners[i+1]).intervalRemoved(e);
      }
    }
  }

    public <T extends EventListener> T[] getListeners(Class<T> listenerType) {
        return listenerList.getListeners(listenerType);
    }


}
