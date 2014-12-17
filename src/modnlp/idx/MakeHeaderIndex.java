/**
 *  (c) 2007 S Luz <luzs@acm.org>
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
package modnlp.idx;

import org.xmldb.api.DatabaseManager;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.Database;
import org.xmldb.api.base.ResourceIterator;
import org.xmldb.api.base.Resource;
import org.xmldb.api.base.ResourceSet;
import org.exist.xmldb.DatabaseInstanceManager;

import org.xmldb.api.modules.XPathQueryService;

/**
 *  Index header files on eXist DB
 *
 * @author  S Luz &#60;luzs@acm.org&#62;
 * @version <font size=-1>$Id: $</font>
 * @see IndexManager for a program the does the full job of indexing
 * words and (header) metadata
*/

public class MakeHeaderIndex {

 public static void main(String args[]) throws Exception {
   String driver = "org.exist.xmldb.DatabaseImpl";
   Class cl = Class.forName(driver);			
   Database database = (Database)cl.newInstance();
   DatabaseManager.registerDatabase(database);
   
   Collection col = 
     DatabaseManager.getCollection(
                                   "xmldb:exist:///db", "admin", ""
                                   );
   XPathQueryService service =
     (XPathQueryService) col.getService("XPathQueryService", "1.0");
   service.setProperty("indent", "yes");
   
   ResourceSet result = service.query("for $s in //intervention/(speaker|writer)/affiliation[@EPparty ='PSE'] return data($s/../../(speech|writing)/@ref)");
   ResourceIterator i = result.getIterator();
   while(i.hasMoreResources()) {
     Resource r = i.nextResource();
     System.out.println((String)r.getContent());
   }
   // shut down the database
   DatabaseInstanceManager manager = (DatabaseInstanceManager)
     col.getService("DatabaseInstanceManager", "1.0"); 
   manager.shutdown();
 }
}
