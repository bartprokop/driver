/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package name.prokop.bart.hardware.driver.dummy;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import name.prokop.bart.hardware.driver.Device;
import name.prokop.bart.hardware.driver.Driver;
import name.prokop.bart.hardware.driver.DeviceDetectedEvent;
import name.prokop.bart.hardware.driver.DeviceDropEvent;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author Administrator
 */
public class DummyBusDevice implements Device {

    private final DummyBus dummyBus;

    public DummyBusDevice(DummyBus dummyBus) {
        this.dummyBus = dummyBus;
    }

    public void init() {
        dummyBus.driver.postEvent(new DeviceDetectedEvent(this));
    }

    public void destroy() {
        dummyBus.driver.postEvent(new DeviceDropEvent(this));
    }

    public String getDeviceAddress() {
        return dummyBus.busName + ":" + hashCode();
    }

    public String getDeviceDescription() {
        return "Dummy Bus Device";
    }

    public String getDeviceInfo() {
        return getDeviceDescription();
    }

    public int compareTo(Device o) {
        return getDeviceAddress().compareTo(o.getDeviceAddress());
    }
}
