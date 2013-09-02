/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package name.prokop.bart.hardware.driver;

import name.prokop.bart.hardware.driver.rfid.SerialNumberType;

/**
 *
 * @author Bart
 */
public interface SerialNumberDetectedProperty {

    /**
     * Serial number of identity device
     * @return
     */
    public String getSerialNumber();

    /**
     * Card type that coresponds with bol also.
     * 
     * @return Emulator, BarCode, Unique, Mifare
     */
    public SerialNumberType getSerialNumberType();

    /**
     * Okresla czy jest to czytnik lokalny (podłączony do komputera w celu obsługi GUI)
     * @return true if the reader is local reader (used for GUI processing)
     */
    public boolean isLocalReader();
}
