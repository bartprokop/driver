/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package name.prokop.bart.hardware.driver.rfid.sl500;

import name.prokop.bart.hardware.driver.rfid.RFIDCardRemovedProperty;
import name.prokop.bart.hardware.driver.rfid.RFIDCardType;
import name.prokop.bart.util.ToString;

/**
 *
 * @author bart
 */
public class TTDevice0002CardRemoved extends TTDevice0002Event implements RFIDCardRemovedProperty {

    private final RFIDCardType cardType;
    private final byte[] serialNumber;

    public TTDevice0002CardRemoved(TTDevice0002 device, RFIDCardType cardType, byte[] serialNumber) {
        super(device);
        this.cardType = cardType;
        this.serialNumber = serialNumber;
    }

    public byte[] getCardSerialNumber() {
        return serialNumber;
    }

    public RFIDCardType getRFIDCardType() {
        return cardType;
    }

    @Override
    public String toString() {
        return super.toString() + " : " + cardType.serialNumberToString(serialNumber) + " : " + ToString.byteArrayToString(getCardSerialNumber());
    }
}
