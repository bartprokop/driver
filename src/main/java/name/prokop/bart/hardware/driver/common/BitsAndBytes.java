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

    public static long combine(int h, int l) {
        return ((long) h << 32) | l;
    }

//    public static void fillBigEndian(byte[] a, int i) {
//        a[0] = castIntToByte(i >> 24);
//        a[1] = castIntToByte(i >> 16);
//        a[2] = castIntToByte(i >> 8);
//        a[3] = castIntToByte(i);
//    }

//    public static void fillLittleEndian(byte[] a, int i) {
//        a[0] = castIntToByte(i);
//        a[1] = castIntToByte(i >> 8);
//        a[2] = castIntToByte(i >> 16);
//        a[3] = castIntToByte(i >> 24);
//    }

//    public static byte[] toBigEndian(int i) {
//        byte[] a = new byte[4];
//        a[0] = castIntToByte(i >> 24);
//        a[1] = castIntToByte(i >> 16);
//        a[2] = castIntToByte(i >> 8);
//        a[3] = castIntToByte(i);
//        return a;
//    }

//    public static byte[] toLittleEndian(int i) {
//        byte[] a = new byte[4];
//        a[0] = castIntToByte(i);
//        a[1] = castIntToByte(i >> 8);
//        a[2] = castIntToByte(i >> 16);
//        a[3] = castIntToByte(i >> 24);
//        return a;
//    }

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
     * Promotes byte b to unsigned 64 bit value (0-255)
     *
     * @param b
     * @return
     */
    public static long promoteByteToLong(byte b) {
        return (long) (b & 0xff);
    }

    public static void main(String[] args) {
//        System.out.println((byte) 0xAA);
//        System.out.println(castIntToByte(512));
//        System.out.println(castIntToByte(255));
//        System.out.println(castIntToByte(-1));
    }

    public static byte[] subArray(byte[] array, int startIndex, int endIndex) {
        byte[] retVal = new byte[endIndex - startIndex];
        for (int i = 0; i < retVal.length; i++) {
            retVal[i] = array[startIndex + i];
        }
        return retVal;
    }
}
