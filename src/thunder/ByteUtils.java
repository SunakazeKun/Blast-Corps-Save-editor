/*
 * Copyright (C) 2016 Aurum
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package thunder;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.ArrayUtils;

public class ByteUtils {
    
    public static byte[] getBytesFromOffset(int s, int l, byte[] d) {
        /*
            Returns an array of bytes from a given offset.
            * s :   Offset
            * l :   Length
            * d :   Data
        */
        
        List<Byte> b = new ArrayList();
        for (int i = 0 ; i < l ; i++)
            b.add(d[s + i]);
        Byte[] b2 = b.toArray(new Byte[b.size()]);
        
        return ArrayUtils.toPrimitive(b2);
    }
    
    public static byte[] hexStringToBytes(String in) {
        /*
            Returns an array of bytes from a hex string.
        */
        
        int l = in.length();
        byte[] b = new byte[l / 2];
        for (int i = 0; i < l; i += 2) {
            b[i / 2] = (byte) ((Character.digit(in.charAt(i), 16) << 4) + Character.digit(in.charAt(i + 1), 16));
        }
        return b;
    }
    
    public static int bytesToInt(byte[] b) {
        /*
            Converts an array of bytes to an Integer value.
        */
        
        ByteBuffer b2 = ByteBuffer.wrap(b);
        return b2.getInt();
    }
    
    public static long bytesToShort(byte[] b) {
        /*
            Converts an array of bytes to a Short value.
        */
        
        ByteBuffer b2 = ByteBuffer.wrap(b);
        return b2.getShort();
    }
    
    public static long bytesToLong(byte[] b) {
        /*
            Converts an array of bytes to a Long value.
        */
        
        ByteBuffer b2 = ByteBuffer.wrap(b);
        return b2.getLong();
    }
    
    public static float bytesToFloat(byte[] b) {
        /*
            Converts an array of bytes to a Float value.
        */
        
        ByteBuffer b2 = ByteBuffer.wrap(b);
        return b2.getFloat();
    }
    
    public static double bytesToDouble(byte[] b) {
        /*
            Converts an array of bytes to a Double value.
        */
        
        ByteBuffer b2 = ByteBuffer.wrap(b);
        return b2.getDouble();
    }
    
    public static byte[] intToBytes(int i) {
        /*
            Converts an Integer value to an array of bytes.
        */
        
        ByteBuffer b = ByteBuffer.allocate(4);
        b.putInt(i);
        return b.array();
    }
    
    public static byte[] shortToBytes(short s) {
        /*
            Converts a Short value to an array of bytes.
        */
        
        ByteBuffer b = ByteBuffer.allocate(2);
        b.putShort(s);
        return b.array();
    }
    
    public static byte[] longToBytes(long l) {
        /*
            Converts a Long value to an array of bytes.
        */
        
        ByteBuffer b = ByteBuffer.allocate(8);
        b.putLong(l);
        return b.array();
    }
    
    public static byte[] floatToBytes(float f) {
        /*
            Converts a Float value to an array of bytes.
        */
        
        ByteBuffer b = ByteBuffer.allocate(4);
        b.putFloat(f);
        return b.array();
    }
    
    public static byte[] doubleToBytes(double d) {
        /*
            Converts a Double value to an array of bytes.
        */
        
        ByteBuffer b = ByteBuffer.allocate(8);
        b.putDouble(d);
        return b.array();
    }
}