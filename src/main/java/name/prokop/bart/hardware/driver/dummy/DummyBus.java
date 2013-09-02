/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package name.prokop.bart.hardware.driver.dummy;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import name.prokop.bart.hardware.driver.Device;
import name.prokop.bart.hardware.driver.Driver;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author Administrator
 */
public class DummyBus implements Runnable {

    @Autowired
    Driver driver;
    private boolean pleaseTerminate = false;
    String busName;
    private DummyBusDevice device1 = new DummyBusDevice(this);
    private DummyBusDevice device2 = new DummyBusDevice(this);
    private DummyBusDevice device3 = new DummyBusDevice(this);
    private DummyBusDevice device4 = new DummyBusDevice(this);

    @PostConstruct
    private void init() {
        new Thread(this).start();
    }

    @PreDestroy
    private void stop() {
        pleaseTerminate = true;
    }

    public void run() {
        device1.init();
        device2.init();
        device3.init();
        device4.init();

        while (!pleaseTerminate) {
            sleep(100);
        }

        device4.destroy();
        device3.destroy();
        device2.destroy();
        device1.destroy();
    }

    private static void sleep(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ie) {
        }
    }

    public void setBusName(String busName) {
        this.busName = busName;
    }
}
