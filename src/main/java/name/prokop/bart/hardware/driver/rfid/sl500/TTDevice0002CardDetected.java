/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package name.prokop.bart.hardware.driver.rfid.sl500;

import name.prokop.bart.hardware.driver.rfid.RFIDCardDetectedProperty;
import name.prokop.bart.hardware.driver.rfid.RFIDCardType;
import name.prokop.bart.hardware.driver.rfid.SerialNumberType;
import name.prokop.bart.util.ToString;

/**
 *
 * @author bart
 */
public class TTDevice0002CardDetected extends TTDevice0002Event implements RFIDCardDetectedProperty {

    private final RFIDCardType cardType;
    private final byte[] serialNumber;

    public TTDevice0002CardDetected(TTDevice0002 device, RFIDCardType cardType, byte[] serialNumber) {
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

    public String getSerialNumber() {
        return cardType.serialNumberToString(serialNumber);
    }

    public SerialNumberType getSerialNumberType() {
        return SerialNumberType.RFIDCard;
    }

    public boolean isLocalReader() {
        return true;
    }

    @Override
    public String toString() {
        return super.toString() + " : " + getSerialNumber() + " : " + ToString.byteArrayToString(getCardSerialNumber());
    }

}
