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
package modnlp.idx.headers;

import modnlp.Constants;
import modnlp.idx.query.SubcorpusConstraints;
import modnlp.idx.database.DictProperties;

import org.xmldb.api.DatabaseManager;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.CompiledExpression;
import org.xmldb.api.base.Database;
import org.xmldb.api.base.ResourceIterator;
import org.xmldb.api.base.Resource;
import org.xmldb.api.base.ResourceSet;
import org.xmldb.api.base.XMLDBException;
import org.exist.xmldb.DatabaseInstanceManager;
import org.exist.xmldb.XQueryService;

import org.xmldb.api.modules.CollectionManagementService;
import org.xmldb.api.modules.XMLResource;
import org.xmldb.api.modules.XPathQueryService;

import java.io.File;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.InputStreamReader;

import java.util.HashSet;

/**
 *  Add and remove header files from eXist XML db
 *
 * @author  S Luz &#60;luzs@acm.org&#62;
 * @version <font size=-1>$Id: $</font>
 * @see  
*/

public class HeaderDBManager {

  /**
   * Identifier for variable to which header queries will bind. 
   */
  public static final String XQVAR = "$s";
  public static final int DEFAULTMAXCACHESIZE = 20;

  Database database = null;
  Collection collection = null;
  public static final String EXIST_CONF_NAME = "eXistConf.xml";
  private static String PS = java.io.File.separator;
  static final String RELATIVE_EXIST_DB_PATH = "eXistMNLP"+PS+"data";
  static final String URI = "xmldb:exist://";
  static final String colName = "xmldb:exist:///db/headers";
  String queryRootElementPath = null;  // e.g. //intervention
  String queryReturnSIDAttPath = null; // e.g. (speech|writing)/@ref
  String queryRootFileDescPath = null;
  String queryFileDescReturn = null;
  ConstraintCache cache;

  public HeaderDBManager (DictProperties dp)
    throws ClassNotFoundException,
           IllegalAccessException,
           InstantiationException,
           XMLDBException
  {
    String mcs = dp.getProperty("xquery.max.cache.size");
    if (mcs == null) {
      cache = new ConstraintCache(DEFAULTMAXCACHESIZE);
    }
    else {
      try {
        cache = new ConstraintCache((new Integer(mcs)).intValue());
      } catch (java.lang.NumberFormatException e) {
        cache = new ConstraintCache(DEFAULTMAXCACHESIZE);
      }
    }

    String envHome = dp.getEnvHome();
    queryRootElementPath = dp.getProperty("xquery.root.element.path");
    queryReturnSIDAttPath = dp.getProperty("xquery.return.attribute.path");
    queryRootFileDescPath = dp.getProperty("xquery.root.filedescription.path");
    queryFileDescReturn = dp.getProperty("xquery.file.description.return");

    String driver = "org.exist.xmldb.DatabaseImpl";
    Class cl = Class.forName(driver);
    database = (Database)cl.newInstance();
    database.setProperty("create-database", "true");
    database.setProperty("configuration", getConfName(envHome));
    DatabaseManager.registerDatabase(database);
    // try to get collection
    collection =
      DatabaseManager.getCollection(colName, "admin", "");
    if(collection == null) {
      // collection does not exist: get root collection and create
      Collection root = DatabaseManager.getCollection(URI + "/db");
      CollectionManagementService mgtService = (CollectionManagementService)
        root.getService("CollectionManagementService", "1.0");
      collection = mgtService.createCollection(colName);
    }
  }

  private String getConfName(String eh){
    String cf = eh+PS+EXIST_CONF_NAME;
    File f = new File(cf);
    if(!f.canRead()) {
      try {
        System.err.println("Warn: HeaderDBManager.getConfName): creating eXist config"+cf);
        String abspath = eh+PS+RELATIVE_EXIST_DB_PATH;
        (new File(abspath)).mkdirs();
        ClassLoader cl = this.getClass().getClassLoader();
        BufferedReader in
          = new BufferedReader(new 
                               InputStreamReader(cl.getResourceAsStream(EXIST_CONF_NAME)));
        FileWriter out = new FileWriter(f);
        String l = null;
        while ( (l = in.readLine() ) != null )
          out.write(l.replaceAll(RELATIVE_EXIST_DB_PATH, abspath)+"\n");
        in.close();
        out.close();
      }
      catch (Exception e) {
        System.err.println("Error: HeaderDBManager.getConfName): "+e);
        e.printStackTrace(System.err);
        System.exit(1);
      }
    }
    return cf;
  }

  public void add(String fname, int fid) throws java.io.IOException, XMLDBException{
    // create new XMLResource; an id will be assigned to the new resource
    File f = new File(fname);
    if(!f.canRead()) {
      throw new java.io.IOException("Error (HeaderDBManager): cannot read file " + fname);
    }
    XMLResource document = (XMLResource)collection.createResource(fid+"", "XMLResource");
    document.setContent(f);
    System.out.print("storing document " + document.getId() + "...");
    collection.storeResource(document);
    System.out.println("ok.");    
  }


  public SubcorpusConstraints getSubcorpusConstraints(String where){
    try {
      SubcorpusConstraints sc;
      if (where == null)
        return null;
      if ( (sc = cache.get(where)) != null ){
        System.err.println("Found cached query: "+where);
        return sc;
      }
      String resources[] = collection.listResources();
      sc = new SubcorpusConstraints();
      for (int i = 0; i < resources.length; i++) {
        XQueryService service =
          (XQueryService) collection.getService("XQueryService", "1.0");
        //service.setProperty("indent", "yes");
        String xq = "for "+XQVAR+" in doc('"+
          resources[i]+"')"+queryRootElementPath+
          " let $a := 'a' "+  // need to include this nonsensical let clause (possibly a bug in eXist
          " where "+where+
          " return data("+XQVAR+"/"+queryReturnSIDAttPath+")";
        CompiledExpression compiled = service.compile(xq);
        ResourceSet result = service.execute(compiled);
        ResourceIterator ri = result.getIterator();
        HashSet hs = new HashSet();
        while(ri.hasMoreResources()) {
          Resource r = ri.nextResource();
          hs.add(r.getContent());
        }
        if (!hs.isEmpty())
          sc.put(resources[i], hs);
      }
      cache.cache(where,sc);
      //System.out.println(sc);
      return sc; //.isEmpty()? null : sc;
    }
    catch (Exception ex){
      System.err.println("Error (HeaderDBManager.geSubcorpusConstrints): "+ex);
      ex.printStackTrace();
      return new SubcorpusConstraints();
    }
  }

  public String getFileDescription(int fno){
    try {
      //      String resources[] = collection.listResources();
      //for (int i = 0; i < resources.length; i++) {
      XQueryService service =
        (XQueryService) collection.getService("XQueryService", "1.0");
      //service.setProperty("indent", "yes");
      String xq = "let "+XQVAR+" := doc('"+fno+"')"+queryRootFileDescPath+
        " return <d>"+queryFileDescReturn+"</d>";
      //System.err.println(xq);
      CompiledExpression compiled = service.compile(xq);
      ResourceSet result = service.execute(compiled);
      ResourceIterator ri = result.getIterator();
      if (ri.hasMoreResources()) {
        Resource r = ri.nextResource();
        String desc = (String)r.getContent();
        System.err.println(desc);
        desc = desc.substring(3,desc.lastIndexOf("</d>"));
        return desc.replace('\n',' ');
      }
      //}
    return "";
    }
    catch (Exception ex){
      System.err.println("Error (HeaderDBManager.geSubcorpusConstrints): "+ex);
      ex.printStackTrace();
      return "";
    }
  }

  public String[] getOptionSet(String attchsr){
    String[] out = {};
    try {
      if (attchsr == null)
        return out;
      XQueryService service =
        (XQueryService) collection.getService("XQueryService", "1.0");
      //service.setProperty("indent", "yes");
      CompiledExpression compiled = 
        service.compile("for $s in distinct-values("+queryRootElementPath+"/"+
                        attchsr+") order by $s return data($s)");
      ResourceSet result = service.execute(compiled);
      int s = (int)result.getSize();
      out = new String[s];
      String tmp = null;
      for (int i = 0; i < s; i++) {
        tmp = (String)result.getResource(i).getContent();
        // '' messes up JComboBox in AttributeChooser
        if (tmp == null || tmp.length() == 0){
          System.err.println("HeaderDBManager: Empty value detected for "+attchsr+"\n  Setting it to ' '");
          tmp = " ";
        }
        out[i] = tmp;
      }
      return out;
    }
    catch (Exception ex){
      System.err.println("Error (HeaderDBManager.geSubcorpusConstrints): "+ex+" ATTCHRS: "+attchsr);
      ex.printStackTrace();
      return out;
    }
  }

  public String getOptionSetString(String attchsr){
    System.err.println(attchsr);
    String[] osa = getOptionSet(attchsr);
    if (osa == null || osa.length==0)
      return null;
    StringBuffer sb = new StringBuffer(osa[0]);
    for (int i = 1; i < osa.length; i++) {
      sb.append(Constants.ATTRIBUTE_OPTION_SEP+osa[i]);
    }
    //System.err.println("------------>"+sb);
    return sb.toString();
  }

  public void close() {
    finalize();
  }

  public void finalize() {
    try {
      DatabaseInstanceManager manager = (DatabaseInstanceManager)
        collection.getService("DatabaseInstanceManager", "1.0"); 
      manager.shutdown();
    }
    catch (XMLDBException e) {
      System.err.println("Warning (HeaderDBManager.finalize): "+e);
    }
  }
  
  public static void main(String[] args) {
    try {
      HeaderDBManager hm = new HeaderDBManager(new DictProperties(args[0]));
      // shut down the database
      String[] a = hm.getOptionSet(args[1]);
      for (int i = 0; i < a.length; i++) {
        System.err.println(a[i]);
      }
      DatabaseInstanceManager manager = (DatabaseInstanceManager)
        hm.collection.getService("DatabaseInstanceManager", "1.0"); 
      manager.shutdown();
    } // end try
    catch (Exception ex){
      System.err.println(ex);
      ex.printStackTrace();
    }
  }

  public static void mainAdd(String[] args) {
    try {
      HeaderDBManager hm = new HeaderDBManager(new DictProperties(args[0]));
      for (int i = 1; i < args.length ; i++)
        hm.add(args[i], i);
      // shut down the database
      DatabaseInstanceManager manager = (DatabaseInstanceManager)
        hm.collection.getService("DatabaseInstanceManager", "1.0"); 
      manager.shutdown();
    } // end try
    catch (Exception ex){
      System.err.println(ex);
      ex.printStackTrace();
    }
  }

}
