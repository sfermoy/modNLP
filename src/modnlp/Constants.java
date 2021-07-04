/**
 *   Copyright (c) 2007 S Luz <luzs@acm.org>. All Rights Reserved.
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
 **/ 
package modnlp;

/**
 *  Store final static vars used by client and server
 *
 * 
 * @author  Saturnino Luz &#60;luzs@acm.org&#62;
 * @version <font size=-1>$Id: $</font>
 * @see  
*/
public class Constants {

  public static final String ATTRIBUTE_OPTION_SEP = "_,_";
  public static final String LINE_ITEM_SEP = "<sep/>";

  public static final int LANG_EN = 0;
  public static final int LANG_JP = 1;
  public static final int LANG_AR = 2;

  // Request types (see also modnlp/server/Request.java)
  public static final int REQ_CONCORD = 1;
  public static final int REQ_EXTRACT = 2;
  public static final int REQ_SQLQUERY = 3;
  public static final int REQ_SQLMETAQ = 4;
  public static final int REQ_FREQLIST = 5;
  public static final int REQ_HEADERBASEURL = 6;
  public static final int REQ_ATTCHOPTSPECS = 7;
  public static final int REQ_ATTCHOSPTIONS = 8;
  public static final int REQ_CDESCRIPTION = 9;
  public static final int REQ_FREQWORD = 10;
  public static final int REQ_NOOFTOKENS = 11;
  //public static final int SERVERINFO = 7;
  public static final int REQ_NOTREQ = 999;


}
