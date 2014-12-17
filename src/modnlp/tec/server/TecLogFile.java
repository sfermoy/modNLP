/** 
 * Project: MODNLP/TEC/SERVER, Based on software developed for the
 * Translated English Corpora (TEC) project, from the Dept of Language
 * Engineering - UMIST (DLE-UMIST)
 *

 * Copyright (c) 2006 S.Luz (TCD)
 *           (c) 1998 S.Luz (DLE-UMIST) 
 *           All Rights Reserved.
 *
 *   This program is free software; you can redistribute it and/or
 *   modify it under the terms of the GNU General Public License
 *   as published by the Free Software Foundation; either version 2
 *   of the License, or (at your option) any later version.
 *   
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
     
 *   You should have received a copy of the GNU General Public License
 *   along with this program; if not, write to the Free Software
 *   Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 *
 *     
 */ 
package modnlp.tec.server;

import java.io.*;
import java.lang.*;
import java.util.Date;

/** Mantain Server Log file 
 * 
 * @author  Nino Luz <luz@acm.org>
 * @version $Id: TecLogFile.java,v 1.3 2003/06/22 17:55:15 luzs Exp $ * @version %I%, %G%
 * @see TecServer
 * @since JDK 1.1.3
 */
public class TecLogFile extends PrintWriter {

  /** If <code>true</code> print debug messager to System.err 
   */
  public boolean debugOn = true;
  public Date startDate;

  /** 
   * Create a new file and record server startup time
   * @param fname  name (full path) of the log file
   * @exception IOException if IO error occurred 
   */
  public TecLogFile(String fname) throws IOException{
      super(new FileWriter(fname, true));
      startDate = new Date();
      logMsg("Starting TecServer at "+startDate);
  }
    
  public TecLogFile(PrintStream ps) throws IOException{
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

  public void dbgMsg (Object msg){
    if (debugOn)
      System.err.println(msg);
  }
}
