/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package name.prokop.bart.hardware.driver.rfid.ttd0001v01;

import name.prokop.bart.hardware.driver.common.ToString;
import name.prokop.bart.hardware.driver.rfid.RFIDCardType;
import name.prokop.bart.hardware.driver.rfid.SerialNumberType;



/**
 *
 * @author bart
 */
public class TTDevice0001v01CardDetected extends TTDevice0001v01Event {

    private final RFIDCardType cardType;
    private final byte[] serialNumber;

    public TTDevice0001v01CardDetected(TTDevice0001v01 device, RFIDCardType cardType, byte[] serialNumber) {
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
        return false;
    }

    @Override
    public String toString() {
        return super.toString() + " : " + getSerialNumber() + " : " + ToString.byteArrayToString(getCardSerialNumber());
    }

}
