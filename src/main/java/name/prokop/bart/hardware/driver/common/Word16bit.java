/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package name.prokop.bart.hardware.driver.common;

/**
 *
 * @author bart
 */
public class Word16bit {

    private byte high, low;

    public Word16bit() {
    }

    public Word16bit(byte high, byte low) {
        this.high = high;
        this.low = low;
    }

    public byte getHigh() {
        return high;
    }

    public int getHighAsInt() {
        return BitsAndBytes.promoteByteToInt(high);
    }

    public void setHigh(byte high) {
        this.high = high;
    }

    public byte getLow() {
        return low;
    }

    public int getLowAsInt() {
        return BitsAndBytes.promoteByteToInt(low);
    }

    public void setLow(byte low) {
        this.low = low;
    }
}
