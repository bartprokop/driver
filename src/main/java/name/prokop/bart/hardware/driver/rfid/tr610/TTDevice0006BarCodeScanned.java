/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package name.prokop.bart.hardware.driver.rfid.tr610;

import name.prokop.bart.hardware.driver.SerialNumberDetectedProperty;
import name.prokop.bart.hardware.driver.rfid.SerialNumberType;

/**
 *
 * @author bart
 */
public class TTDevice0006BarCodeScanned extends TTDevice0006Event implements SerialNumberDetectedProperty {

    private final String code;

    public TTDevice0006BarCodeScanned(TTDevice0006 device, String code) {
        super(device);
        this.code = code;
    }

    public String getSerialNumber() {
        return code;
    }

    public SerialNumberType getSerialNumberType() {
        return SerialNumberType.BarCode;
    }

    public boolean isLocalReader() {
        return false;
    }

    @Override
    public String toString() {
        return super.toString() + " : " + getSerialNumber();
    }
}
