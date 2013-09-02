/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package name.prokop.bart.hardware.driver.common;

import name.prokop.bart.hardware.driver.Device;
import name.prokop.bart.hardware.driver.Driver;
import name.prokop.bart.hardware.driver.Event;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author Bart
 */
public final class DeviceErrorEvent extends Event {
    @Autowired
    private static Driver driver;

    public static DeviceErrorEvent generate(Device source) {
        return new DeviceErrorEvent(source);
    }

    public static void generateAndPost(Device source) {
        driver.postEvent(DeviceErrorEvent.generate(source));
    }

    private DeviceErrorEvent(Device source) {
        super(source);
    }

}
