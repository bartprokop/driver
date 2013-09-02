/*
 * SimpleBarCodeReader.java
 *
 * Created on 22 listopad 2004, 18:06
 */
package name.prokop.bart.hardware.bpdriver.id.barcode;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import name.prokop.bart.hardware.driver.Device;
import name.prokop.bart.hardware.driver.Driver;
import name.prokop.bart.hardware.driver.DeviceDetectedEvent;
import name.prokop.bart.hardware.driver.DeviceDropEvent;
import name.prokop.bart.util.lang.BartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.Element;

/**
 *
 * @author Bart
 */
public class BogusReader implements Runnable, Device {

    @Autowired
    private Driver driver;
    private String busName;
    private Thread thread;
    private boolean pleaseTerminate = false;
    private boolean terminated = false;

    /**
     * Creates a new instance of UnicardBus
     */
    public BogusReader(Element busConf) throws BartException {
        this.busName = busConf.getAttribute("name");
    }

    @PostConstruct
    private void init() {
        new Thread(this).start();
    }

    @PreDestroy
    private void destroy() {
        pleaseTerminate = true;
    }

    @Override
    public void run() {
        //postEvents(getBusName() + " " + Long.toString(System.currentTimeMillis()) + " MKDR_SIMPLE DeviceDetected");
        driver.postEvent(new DeviceDetectedEvent(this));

        while (!pleaseTerminate) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }

            //postEvents(getBusName() + " " + Long.toString(System.currentTimeMillis()) + " MKDR_SIMPLE CardDetected 1234567890");
            driver.postEvent(BarCodeScannedEvent.produceEvent(this, "1234567890"));
        }
        //postEvents(getBusName() + " " + Long.toString(System.currentTimeMillis()) + " MKDR_SIMPLE DeviceDrop");
        driver.postEvent(new DeviceDropEvent(this));
        terminated = true;
    }

    @Override
    public String getDeviceAddress() {
        return busName;
    }

    public int compareTo(Device o) {
        return getDeviceAddress().compareTo(o.getDeviceAddress());
    }

    public String getBusInfo() {
        return "Wypełnić danymi";
    }

    public String getDeviceDescription() {
        return "Wypełnić danymi";
    }

    public String getDeviceInfo() {
        return "Wypełnić danymi";
    }

    public void setDevices(Device[] devices) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
