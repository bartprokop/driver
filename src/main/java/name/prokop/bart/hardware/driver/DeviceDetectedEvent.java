/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package name.prokop.bart.hardware.driver;

/**
 *
 * @author Bart
 */
public final class DeviceDetectedEvent extends Event {

    public DeviceDetectedEvent(Device source) {
        super(source);
    }
}
