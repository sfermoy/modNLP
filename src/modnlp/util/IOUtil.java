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

/**
 *  Handle PM serialization, plugin loading, and other IO stuff
 *
 * @author  S Luz &#60;luzs@acm.org&#62;
 * @version <font size=-1>$Id: IOUtil.java,v 1.1.1.1 2005/05/26 13:59:30 amaral Exp $</font>
 * @see  
*/
public class IOUtil  {


  public static Object loadPlugin(String plugin) throws Exception {
    ClassLoader cl = ClassLoader.getSystemClassLoader();
    return (Object)cl.loadClass(plugin).newInstance();
  }

  public static Object loadPlugin(String plugin, ClassLoader cl) throws Exception {
    return (Object)cl.loadClass(plugin).newInstance();
  }

}
