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
package modnlp.idx.database;

import modnlp.dstruct.IntOffsetArray;

import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;
import java.util.Iterator;
/**
 *  Tuple binding for compressing and storing arrays of Java ints
 *  (ordered in ascending order) into a JE database. These ints will
 *  typically indicate the difference between a character offset
 *  (distance from the beginning of a file) and the preceding offset:
 *
 *  | pos_0 | pos_1-pos_0 | pos_2-pos_1 | .... | pos_n - pos_n-1 \
 * 
 *  The compression algorithm works as follows:
 *
 *  <pre>
 *  for each offset
 *    if (offset < max_size_of_unsigned_byte)
 *      store it as unsigned_byte
 *    else
 *      store an overflow marker // i.e. an unsigned byte of value 255
 *      store offset as unsigned_int
      end if
    end for
 *  </pre>
 *
 * This is a simple enough strategy that typically achieves a 75%
 * compression ratio (w.r.t. storage of absolute positions). Although the algorithm works for any ordered sequence of ints 
 * 
 * Caveat: we currently make no effort to store offsets greater than
 * the max size of an unsigned_int
 * 
 * @author  S Luz &#60;luzs@acm.org&#62;
 * @version <font size=-1>$Id: IntOffsetArrayBinding.java,v 1.2 2006/05/22 17:26:02 amaral Exp $</font>
 * @see  WordPositionTable
*/
public class IntOffsetArrayBinding extends TupleBinding {
  
  private int bytesReceived = 0; // no of bytes passed to objectToEntry() 
  private int bytesWritten = 0; // no of bytes actually written
  public static long MAXOFFSET_VALUE = (long)2*Integer.MAX_VALUE+1;

  // Write a IntOffsetArray to a TupleOutput
  public void objectToEntry(Object object, TupleOutput to) {
    
    IntOffsetArray ioao = (IntOffsetArray)object;
    int extraByteCount = 0;
    int[] ioa = ioao.getArray();
    to.writeUnsignedInt(ioa.length); // first 4 bytes tell us how large an array will be read
    for (int i = 0; i < ioa.length; i++) {
      if (ioa[i] > 254) {                // if int doesn't fit into a byte (minus one), store it in
        to.writeUnsignedByte(255);       // 4 bytes (unsigent int) and signal that by prefixing the uint with 255  
        if (ioa[i] > MAXOFFSET_VALUE)    // -- can't handle overflows past the size of an unsigned int (i.e. 2^32-1)
          System.err.println("Warning: Offset overflow at "+ioa[i]);
        to.writeUnsignedInt(ioa[i]);   // with a 255 byte)
        extraByteCount += 5;
        // System.err.println("IntOffsetArray overflow; writing integer = "+ioa[i]); //
      }
      else
        to.writeUnsignedByte(ioa[i]);
    }
    bytesWritten = ioa.length+extraByteCount;
    bytesReceived = ioa.length*4;
  }

  // Convert a TupleInput to a IntOffsetArray
  public Object entryToObject(TupleInput ti) {
    IntOffsetArray ioa = new IntOffsetArray();
    int[] osa = new int[(int)ti.readUnsignedInt()];
    int i = 0;
    try {
      while (true) {
        int pos = ti.readUnsignedByte();
        if (pos == 255)                  // 255 marks a byte overflow
          osa[i++] = (int)ti.readUnsignedInt();
        //System.err.println("Read short = "+(int)osa[i]); //
        else
          osa[i++] = pos;
      }
    }
    catch (IndexOutOfBoundsException e) {}
    ioa.setArray(osa);
    return ioa;
    }


  /**
   * <code>getCompressionRatio</code> return the compression ratio given
   * by bytesWritten/bytesReceived;
   *
   * @return the ratio of compression of the array passed to
   * objectToEntry() or zero if objectToEntry() hasn't been called.
   */
  public double getCompressionRatio () {
    return bytesReceived == 0? 0 :  1 - (double)bytesWritten/bytesReceived;
  }

  /**
   * Get no. of bytes passed to objectToEntry() so far.
   *
   * @return an <code>int</code> value
   */
  public int getBytesReceived () {
    return bytesReceived;
  }

  /**
   * Get no of bytes actually written into the Object stream so far
   *
   * @return a <code>int</code> value
   */
  public int getBytesWritten () {
    return bytesWritten;
  }

} 

