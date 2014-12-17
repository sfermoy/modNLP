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
package modnlp.util;

import java.io.*;
import java.util.Date;

/**
 *  Handle error messages 
 *
 * @author  S Luz &#60;luzs@acm.org&#62;
 * @version <font size=-1>$Id: LogStream.java,v 1.2 2006/05/22 17:26:02 amaral Exp $</font>
 * @see  
*/
public class LogStream extends 
PrintWriter {

  /** If <code>true</code> print debug messager to System.err 
   */
  public boolean debugOn = true;
  public Date startDate;

  /** 
   * Create a new file and record server startup time
   * @param fname  name (full path) of the log file
   * @exception IOException if IO error occurred 
   */
  public LogStream(String fname) throws IOException{
      super(new FileWriter(fname, true));
      startDate = new Date();
      logMsg("Starting TecServer at "+startDate);
  }
    
  public LogStream(PrintStream ps) throws IOException{
      super(ps);
      startDate = new Date();
  }
    


  /** Write <code>msg</code> into this logfile and 
   *  print msg to stderr id <code>debugOn</code>
   */
  public void logMsg (Object msg){
    msg = msg+" ["+(new Date())+"]";
    this.println(msg);
    dbgMsg(msg);
    this.flush();
  }

  /** Write <code>msg</code> into this logfile and 
   *  print msg to stderr id <code>debugOn</code>
   */
  public void logMsg (Object msg, Exception e){
    msg = msg+": "+e+" ["+(new Date())+"]";
    this.println(msg);
    dbgMsg(msg, e);
    this.flush();
  }


  public void dbgMsg (Object msg){
    if (debugOn)
      System.err.println(msg);
  }

  public void dbgMsg (Object msg, Exception e){
    if (debugOn) {
      System.err.println(msg);
      e.printStackTrace(System.err);
    }
  }

}
