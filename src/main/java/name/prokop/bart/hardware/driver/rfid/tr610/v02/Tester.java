/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package name.prokop.bart.hardware.driver.rfid.tr610.v02;

import com.google.common.eventbus.Subscribe;
import name.prokop.bart.hardware.driver.Event;
import name.prokop.bart.hardware.driver.rfid.RelayOpenAbility;
import name.prokop.bart.hardware.driver.rfid.tr610.TTDevice0006ButtonPressed;

/**
 *
 * @author bart
 */
public class Tester {

    @Subscribe
    public void bpdriverEventOccured(Event e) {
        if (e.getSourceDevice() instanceof RelayOpenAbility && e instanceof TTDevice0006ButtonPressed) {
            System.err.println(e);
            TTDevice0006v02 device = (TTDevice0006v02) e.getSourceDevice();
            TTDevice0006ButtonPressed ee = (TTDevice0006ButtonPressed) e;
            switch (ee.getButton()) {
                case LU:
                    device.overrideLogoOff();
                    break;
                case LD:
                    device.overrideLogo("Linia1", "Linia2", "Linia3");
                    break;
                case RU:
                    device.overrideLogo("", "Ala ma kota", "");
                    break;
                case RD:
                    device.overrideLogo("012345678901234567890", "012345678901234567890", "012345678901234567890");
                    break;
            }
        }
    }
}
