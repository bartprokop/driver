/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package name.prokop.bart.hardware.driver.common;


/**
 *
 * @author bart
 */
public class Fletcher16 {

    public static Word16bit fletcher16(byte[] data) {
        Word16bit check = new Word16bit();

        char sum1 = 0xff, sum2 = 0xff;

        int len = data.length;
        int i = 0;

        while (len > 0) {
            int tlen = len > 21 ? 21 : len;
            len -= tlen;
            do {
                sum1 += BitsAndBytes.promoteByteToChar(data[i++]);
                sum2 += sum1;
            } while (--tlen > 0);
            sum1 = BitsAndBytes.castIntToChar((sum1 & 0xff) + (BitsAndBytes.promoteCharToInt(sum1) >> 8));
            sum2 = BitsAndBytes.castIntToChar((sum2 & 0xff) + (BitsAndBytes.promoteCharToInt(sum2) >> 8));
        }
        /* Second reduction step to reduce sums to 8 bits */
        sum1 = BitsAndBytes.castIntToChar((sum1 & 0xff) + (BitsAndBytes.promoteCharToInt(sum1) >> 8));
        sum2 = BitsAndBytes.castIntToChar((sum2 & 0xff) + (BitsAndBytes.promoteCharToInt(sum2) >> 8));
        check.setHigh(BitsAndBytes.castCharToByte(sum1));
        check.setLow(BitsAndBytes.castCharToByte(sum2));

        return check;
    }

    public static void main(String[] args) {
        Word16bit w = fletcher16("12345678900987654321qazswedxsdcvfgbvfgtrfghjklijuh".getBytes());
        System.out.println(w.getHighAsInt() + ":" + w.getLowAsInt());
    }
}
