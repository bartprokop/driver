/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package name.prokop.bart.hardware.driver.rfid.sl500;

import name.prokop.bart.hardware.driver.common.ToString;
import name.prokop.bart.hardware.driver.rfid.RFIDCardDetectedProperty;
import name.prokop.bart.hardware.driver.rfid.RFIDCardType;
import name.prokop.bart.hardware.driver.rfid.SerialNumberType;

/**
 *
 * @author bart
 */
public class TTDevice0002BlockErased extends TTDevice0002Event implements RFIDCardDetectedProperty {

    private final RFIDCardType cardType;
    private final byte[] serialNumber;
    private final byte[] block;

    public TTDevice0002BlockErased(TTDevice0002 device, RFIDCardType cardType, byte[] serialNumber, byte[] block) {
        super(device);
        this.cardType = cardType;
        this.serialNumber = serialNumber;
        this.block = block;
    }

    public byte[] getBlock() {
        return block;
    }

    
    @Override
    public byte[] getCardSerialNumber() {
        return serialNumber;
    }

    public String getBlockAsString() {
        return ToString.byteArrayToString(block);
    }

    @Override
    public RFIDCardType getRFIDCardType() {
        return cardType;
    }

    @Override
    public String getSerialNumber() {
        return cardType.serialNumberToString(serialNumber);
    }

    @Override
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
