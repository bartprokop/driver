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
public class DummyDevice implements Device {

    private String deviceAddress;
    @Autowired
    private Driver driver;

    @PostConstruct
    private void init() {
        driver.postEvent(new DeviceDetectedEvent(this));
    }
    
    @PreDestroy
    private void destroy() {
        driver.postEvent(new DeviceDropEvent(this));
    }

    public String getDeviceAddress() {
        return deviceAddress;
    }

    public void setDeviceAddress(String deviceAddress) {
        this.deviceAddress = deviceAddress;
    }

    public String getDeviceDescription() {
        return "Dummy device";
    }

    public String getDeviceInfo() {
        return "Dummy device";
    }

    public int compareTo(Device o) {
        return getDeviceAddress().compareTo(o.getDeviceAddress());
    }
}
