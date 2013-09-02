/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package name.prokop.bart.hardware.bpdriver.id.barcode;

import name.prokop.bart.hardware.driver.Device;
import name.prokop.bart.hardware.driver.Event;

/**
 *
 * @author Bart
 */
public class BarCodeReaderEvent extends Event {

    public BarCodeReaderEvent(Device source) {
        super(source);
    }

}
