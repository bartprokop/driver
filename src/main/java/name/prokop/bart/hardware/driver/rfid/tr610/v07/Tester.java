/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package name.prokop.bart.hardware.driver.rfid.tr610.v07;

import name.prokop.bart.hardware.driver.Event;
import name.prokop.bart.hardware.driver.rfid.RelayOpenAbility;
import name.prokop.bart.hardware.driver.rfid.tr610.TTDevice0006ButtonPressed;

/**
 *
 * @author bart
 */
public class Tester {

    public void bpdriverEventOccured(Event e) {
        if (e.getSourceDevice() instanceof RelayOpenAbility && e instanceof TTDevice0006ButtonPressed) {
            System.err.println(e);
            TTDevice0006v07 device = (TTDevice0006v07) e.getSourceDevice();
            //device.openRelay();
            device.test();
        }
    }
}
