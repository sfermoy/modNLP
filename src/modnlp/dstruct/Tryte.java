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
package modnlp.dstruct;

/**
 *  A 24-bit unsigned integer (for use in compressed positional indices)
 *
 * @author  S Luz &#60;luzs@acm.org&#62;
 * @version <font size=-1>$Id: $</font>
 * @see  
*/

public class Tryte {
  
  public static final int [][] POWERS = {
    {8388608, 4194304, 2097152, 1048576, 524288, 262144, 131072, 65536},
    {32768, 16384, 8192, 4096, 2048, 1024, 512, 256},
    {128, 64, 32, 16, 8, 4, 2, 1}
  };

  /**
   * <code>bytes</code>: store the 3 bytes encompassed by this Tryte
   * in big-endian order (leftmost byte is most significant).
   *
   */
  private byte [] bytes = {0, 0, 0};
  
  public Tryte(int in) {
    
    int [] ib = {0, 0, 0};
    for (int i = 0; i < 3; i++){
      for (int j = 0; j < POWERS[i].length; j++){
        if (in >= POWERS[i][j] ){
          ib[i] |= POWERS[2][j];
          in -= POWERS[i][j];
        }
      }
      bytes[i] = (byte)ib[i];
    }
    
  }

  public int intValue() {
    int ou = byteToUnsignedInt(bytes[0]) << 16;
    ou |= byteToUnsignedInt(bytes[1]) << 8;
    ou |= byteToUnsignedInt(bytes[2]);
    return ou;
  }


  public static int byteToUnsignedInt (byte b) {
    //System.err.println("b="+b+"uint="+(b < 0 ? 256+b : b));
    return b < 0 ? 256+b : b;
  }

}
