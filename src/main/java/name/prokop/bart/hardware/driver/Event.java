/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package name.prokop.bart.hardware.driver;

import java.util.EventObject;

/**
 *
 * @author Bart
 */
public class Event extends EventObject {

    public Event(Device source) {
        super(source);
    }
    private long eventTime = System.currentTimeMillis();

    public long getEventTime() {
        return eventTime;
    }

    public Device getSourceDevice() {
        return (Device) getSource();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "@" + getSourceDevice().getClass().getSimpleName() + "=" + getSourceDevice().getDeviceAddress();
    }
}
