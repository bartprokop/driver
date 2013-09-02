/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package name.prokop.bart.hardware.driver.rfid;

/**
 *
 * @author Bart
 */
public interface RFIDCardRemovedProperty {

    /**
     * Card serial number (UNIQUE)
     * @return
     */
    public byte [] getCardSerialNumber();
    
    /**
     * Card type that coresponds with bol also.
     * 
     * @return Emulator, BarCode, Unique, Mifare
     */
    public RFIDCardType getRFIDCardType();

}
