/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package name.prokop.bart.hardware.driver.rfid.tr610;

import name.prokop.bart.hardware.driver.rfid.RFIDCardDetectedProperty;
import name.prokop.bart.hardware.driver.rfid.RFIDCardType;
import name.prokop.bart.hardware.driver.rfid.SerialNumberType;


/**
 *
 * @author bart
 */
public class TTDevice0006CardDetected extends TTDevice0006Event implements RFIDCardDetectedProperty {

    private RFIDCardType cardType = null;
    private final byte[] serialNumber;

    public TTDevice0006CardDetected(TTDevice0006 device, String serial) {
        super(device);
        serialNumber = new byte[serial.length() / 2];
        for (int i = 0; i < serial.length(); i = i + 2) {
            serialNumber[i / 2] = (byte) Integer.parseInt(serial.substring(i, i + 2), 16);
        }
        switch (serialNumber.length) {
            case 4:
                cardType = RFIDCardType.Mifare1K;
                break;
            case 5:
                cardType = RFIDCardType.Unique;
                break;
            case 7:
                cardType = RFIDCardType.MifareUltralight;
                break;
            default:
                throw new IllegalStateException();
        }
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
        return super.toString() + " : " + getSerialNumber();
    }
}
