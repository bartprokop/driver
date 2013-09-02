/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package name.prokop.bart.hardware.driver.rfid;

/**
 *
 * @author bart
 */
public enum SerialNumberType {
    /**
     * Ogólnie numer seryjny - niezdefiniowanego typu
     */
    Other,
    /**
     * Kod kreskowy
     */
    BarCode,
    /**
     * Karta transponderowa RFID
     */
    RFIDCard,
    /**
     * Zeskanowany dokument
     */
    OCRDocument;
}
