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
package modnlp.tc.dstruct;

import modnlp.dstruct.Probabilities;
import java.util.Set;
import java.io.Serializable;
/**
 *  Define methodos to be implemented by all probability models for
 *  text categorisation. This is meant as an abstraction over
 *  probability models in which events are described as sets of
 *  documents (e.g. <code>BVProbabilityModel</code>, which implements
 *  the model described in [Sebastiani, 02] and elsewhere), and models
 *  where events are sets of token placeholders (as in [Mitchell, 97],
 *  for instance). It is assumed that <code>TCProbabilityModel</code>
 *  will be serialised and seved to disk for later use (e.g.  by a
 *  classifer), hence the extension of <code>Serializable</code> and
 *  the fields and methods to store and retrieve creation
 *  information. <code>TCProbabilityModel</code>s will be used
 *  typically in conjunction with <code>TCInvertedIndex</code> objects.
 *
 * @author  S Luz &#60;luzs@acm.org&#62;
 * @version <font size=-1>$Id: TCProbabilityModel.java,v 1.1.1.1 2005/05/26 13:59:30 amaral Exp $</font>
 * @see  BVProbabilityModel TCInvertedIndex
*/
public abstract class TCProbabilityModel implements Serializable {


  public static final byte NOSMOOTHING = 0;
  public static final byte LAPLACE = 1;

  /**
   * All TC probability models must be based on an inverted index
   *
   */
  public TCInvertedIndex invertedIndex = null;

  /**
   * The program that created this probability model
   */
  String creator = null;
  /**
   * The arguments passed to this model's creator.
   */
  String[] creatorArgs = null;
  /**
   * Smoothing techniques to be used in estimating probabilities
   * (getProbabilities()): 0 = no smoothing (MLE), 1 = Laplace
   * (add-one), ...
   */
  byte smoothingType = 0;  


  /**
   * Get a summary of probabilities associated with
   * <code>term</code> and <code>category</code>
   *
   * @param term a <code>String</code> value
   * @param category a <code>String</code> value
   * @return a <code>Probabilities</code> value
   */
  public abstract Probabilities getProbabilities(String term, String category);


  /**
   * Get the value of creationInfo.
   * @return value of creationInfo.
   */
  public String getCreator() {
    return creator;
  }
  
  /**
   * Set the value of creationInfo.
   * @param v  Value to assign to creationInfo.
   */
  public void setCreator(String  v) {
    this.creator = v;
  }
    
  
  /**
   * Get the value of creatorArgs.
   * @return value of creatorArgs.
   */
  public String[] getCreatorArgs() {
    return creatorArgs;
  }

  /**
   * Get the value of creatorArgs as list of comma-separated values.
   * @return value of creatorArgs.
   */
  public String getCreatorArgsCSV() {
    if (creatorArgs.length == 0)
      return "";
    StringBuffer args = new StringBuffer(creatorArgs[0]);
    for (int i = 1 ; i < creatorArgs.length ; i++)
      args.append(","+creatorArgs[i]);
    return args.toString();
  }
  
  /**
   * Set the value of creatorArgs.
   * @param v  Value to assign to creatorArgs.
   */
  public void setCreatorArgs(String[]  v) {
    this.creatorArgs = v;
  }
  
  /**
   * Get the value of creationInfo.
   * @return value of creationInfo.
   */
  public String getCreationInfo() {
    StringBuffer args = new StringBuffer();
    for (int i = 0 ; i < creatorArgs.length ; i++)
      args.append(" "+creatorArgs[i]);
    return creator+args;
  }

  public byte getSmoothingType() {
    return smoothingType;
  }

  public void setSmoothingType(byte s) {
    smoothingType = s;
  }

}
