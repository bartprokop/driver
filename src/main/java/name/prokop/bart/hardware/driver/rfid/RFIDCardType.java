/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package name.prokop.bart.hardware.driver.rfid;

import name.prokop.bart.hardware.driver.common.ToString;

/**
 *
 * @author bart
 */
public enum RFIDCardType {

    /**
     * 125 kHz Unique, 5 byte of serial number
     */
    Unique,
    /**
     * Mifare Classic 1KB
     */
    Mifare1K,
    /**
     * Mifare Classic 4KB
     */
    Mifare4K,
    /**
     * Mifare Ultralight
     */
    MifareUltralight,
    /**
     * EPC Gen 2 UHF card
     */
    EPCGen2;

    public String serialNumberToString(byte[] serialNumber) {
        String retVal = "";
        for (int i = 0; i < serialNumber.length; i++) {
            retVal += ToString.byteToHexString(serialNumber[i]);
        }
        return getPrefix() + retVal;
    }

    public String getPrefix() {
        switch (this) {
            case Unique:
                return "UQ";
            case Mifare1K:
                return "M1";
            case Mifare4K:
                return "M4";
            case MifareUltralight:
                return "Ul";
            case EPCGen2:
                return "G2";
            default:
                throw new IllegalStateException();
        }
    }
}
