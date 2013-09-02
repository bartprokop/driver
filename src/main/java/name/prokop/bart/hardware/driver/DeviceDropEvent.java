/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package name.prokop.bart.hardware.driver;

import name.prokop.bart.hardware.driver.Device;
import name.prokop.bart.hardware.driver.Event;

/**
 *
 * @author Bart
 */
public final class DeviceDropEvent extends Event {

    public DeviceDropEvent(Device source) {
        super(source);
    }
}
