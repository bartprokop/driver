/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package name.prokop.bart.hardware.driver.common;

/**
 *
 * @author Bart
 */
public class BitsAndBytes {

    public static byte[] byteToAscii(byte b) {
        return ToString.byteToHexString(b).getBytes();
    }

    public static int buildInt(byte[] a) {
        //System.out.println(ToString.byteArrayToString(a));
        int i = 0;
        if (a.length == 4) {
            i |= promoteByteToInt(a[0]) << 24;
            i |= promoteByteToInt(a[1]) << 16;
            i |= promoteByteToInt(a[2]) << 8;
            i |= promoteByteToInt(a[3]);
            return i;
        }
        if (a.length == 2) {
            i |= promoteByteToInt(a[0]) << 8;
            i |= promoteByteToInt(a[1]);
            return i;
        }
        throw new IllegalArgumentException("a.length should be either 4 or 2.");
    }

    public static int buildInt(byte b3, byte b2, byte b1, byte b0) {
        int i = 0;
        i |= promoteByteToInt(b3) << 24;
        i |= promoteByteToInt(b2) << 16;
        i |= promoteByteToInt(b1) << 8;
        i |= promoteByteToInt(b0);
        return i;
    }

    public static byte castIntToByte(int i) {
        i &= 0xFF;
        return (byte) i;
    }

    public static char castIntToChar(int i) {
        i &= 0xFFFF;
        return (char) i;
    }

    public static byte castLongToByte(long i) {
        i &= 0xFF;
        return (byte) i;
    }

    public static int castLongToInt(long i) {
        i &= 0xFFFFFFFF;
        return (int) i;
    }

    public static byte castCharToByte(char c) {
        return (byte) (c & 0xFF);
    }

    public static long combine(int h, int l) {
        return ((long) h << 32) | l;
    }

    public static byte encodeAsBCD(int a, int b) {
        byte r = castIntToByte(b);
        r += 16 * castIntToByte(a);
        return r;
    }

    /**
     * @param value
     * @param idx 0 for eight most right bits, 3 for most left bits
     * @return
     */
    public static byte extractByte(int value, int idx) {
        value = (value >> (8 * idx)) & 0xFF;
        return castIntToByte(value);
    }

    public static byte extractByte(long value, int idx) {
        value = (value >> (8 * idx)) & 0xFF;
        return castLongToByte(value);
    }

    public static void fillBigEndian(byte[] a, int i) {
        a[0] = castIntToByte(i >> 24);
        a[1] = castIntToByte(i >> 16);
        a[2] = castIntToByte(i >> 8);
        a[3] = castIntToByte(i);
    }

    public static void fillLittleEndian(byte[] a, int i) {
        a[0] = castIntToByte(i);
        a[1] = castIntToByte(i >> 8);
        a[2] = castIntToByte(i >> 16);
        a[3] = castIntToByte(i >> 24);
    }

    public static byte[] toBigEndian(int i) {
        byte[] a = new byte[4];
        a[0] = castIntToByte(i >> 24);
        a[1] = castIntToByte(i >> 16);
        a[2] = castIntToByte(i >> 8);
        a[3] = castIntToByte(i);
        return a;
    }

    public static byte[] toLittleEndian(int i) {
        byte[] a = new byte[4];
        a[0] = castIntToByte(i);
        a[1] = castIntToByte(i >> 8);
        a[2] = castIntToByte(i >> 16);
        a[3] = castIntToByte(i >> 24);
        return a;
    }

    public static int high(long l) {
        l = l >> 32;
        l &= 0xFFFFFFFF;
        return (int) l;
    }

    public static int low(long l) {
        l &= 0xFFFFFFFF;
        return (int) l;
    }

    /**
     * Promotes byte b to unsigned 16 bit value (0-255)
     *
     * @param b
     * @return
     */
    public static char promoteByteToChar(byte b) {
        return (char) (b & 0xff);
    }

    /**
     * Promotes byte b to unsigned 32 bit value (0-255)
     *
     * @param b
     * @return
     */
    public static int promoteByteToInt(byte b) {
        return (int) (b & 0xff);
    }

    /**
     * Promotes byte b to unsigned 64 bit value (0-255)
     *
     * @param b
     * @return
     */
    public static long promoteByteToLong(byte b) {
        return (long) (b & 0xff);
    }

    public static int promoteCharToInt(char c) {
        return (int) (c & 0xffff);
    }

    public static void main(String[] args) {
        System.out.println((byte) 0xAA);
        System.out.println(promoteByteToInt((byte) 0xAA));
        System.out.println(castIntToByte(512));
        System.out.println(castIntToByte(255));
        System.out.println(castIntToByte(-1));
    }

    public static byte[] subArray(byte[] array, int startIndex, int endIndex) {
        byte[] retVal = new byte[endIndex - startIndex];
        for (int i = 0; i < retVal.length; i++) {
            retVal[i] = array[startIndex + i];
        }
        return retVal;
    }
}
