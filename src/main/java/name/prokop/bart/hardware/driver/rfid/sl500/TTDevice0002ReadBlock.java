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
public class TTDevice0002ReadBlock extends TTDevice0002Event implements RFIDCardDetectedProperty {

    private final RFIDCardType cardType;
    private final byte[] serialNumber;
    private final byte[] block;

    public TTDevice0002ReadBlock(TTDevice0002 device, RFIDCardType cardType, byte[] serialNumber, byte[] block) {
        super(device);
        this.cardType = cardType;
        this.serialNumber = serialNumber;
        this.block = block;
    }

    public byte[] getBlock() {
        return block;
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
