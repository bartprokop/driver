/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package name.prokop.bart.hardware.bpdriver.id.barcode;

import name.prokop.bart.hardware.driver.Device;
import name.prokop.bart.hardware.driver.SerialNumberDetectedProperty;
import name.prokop.bart.hardware.driver.rfid.SerialNumberType;

/**
 *
 * @author Bart
 */
public final class BarCodeScannedEvent extends BarCodeReaderEvent implements SerialNumberDetectedProperty {
    
    private String barCode;

    public static BarCodeScannedEvent produceEvent(Device source, String barCode) {
        return new BarCodeScannedEvent(source, barCode);
    }

    private BarCodeScannedEvent(Device source, String barCode) {
        super(source);
        this.barCode = barCode;
    }

    @Override
    public String getSerialNumber() {
        return barCode;
    }

    @Override
    public SerialNumberType getSerialNumberType() {
        return SerialNumberType.BarCode;
    }

    @Override
    public boolean isLocalReader() {
        return true;
    }

    @Override
    public String toString() {
        return super.toString() + " <- " + barCode;
    }

}
