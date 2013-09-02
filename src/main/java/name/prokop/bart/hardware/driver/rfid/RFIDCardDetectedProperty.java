/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package name.prokop.bart.hardware.driver.rfid;

import name.prokop.bart.hardware.driver.SerialNumberDetectedProperty;

/**
 *
 * @author Bart
 */
public interface RFIDCardDetectedProperty extends SerialNumberDetectedProperty {

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
