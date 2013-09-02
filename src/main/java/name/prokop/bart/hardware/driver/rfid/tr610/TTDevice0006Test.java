/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package name.prokop.bart.hardware.driver.rfid.tr610;

import com.google.common.eventbus.Subscribe;
import java.util.LinkedHashMap;
import java.util.Map;
import name.prokop.bart.hardware.driver.Event;
import name.prokop.bart.hardware.driver.rfid.RFIDCardDetectedProperty;
import name.prokop.bart.hardware.driver.rfid.tr610.v02.TTDevice0006v02;

/**
 *
 * @author bart
 */
public class TTDevice0006Test {

    @Subscribe
    public void bpdriverEventOccured(Event e) {
        if (e instanceof RFIDCardDetectedProperty) {
            TTDevice0006v02 device = (TTDevice0006v02) e.getSourceDevice();
            //device.openRelay();
            //device.enterTanningMode();
            Map<String, Integer> map = new LinkedHashMap<String, Integer>();
            map.put("AB1", 0);
            map.put("AL2", 1);
            map.put("ON3", 0);
            map.put("UU4", 1);
            map.put("UU5", 0);
            map.put("UU6", 1);
            map.put("UU7", 0);
            map.put("UU8ółć", 1);
            map.put("UU9fdg", 0);
            map.put("1", 0);
            map.put("22", 0);
            map.put("333", 0);
            map.put("4444", 0);
            map.put("55555", 0);
            map.put("666666", 0);
            map.put("7777777", 0);
            map.put("88888888", 0);
            map.put("999999999", 0);
            map.put("0000000000", 0);
            map.put("00000000001", 1);
            map.put("000000000022", 1);
            map.put("0000000000333", 1);
            map.put("00000000004444", 1);
            map.put("000000000055555", 1);
            map.put("0000000000666666", 1);
            map.put("00000000007777777", 1);
            map.put("000000000088888888", 1);
            map.put("0000000000999999999", 1);
            device.requestUserSelection(map);
        }

//        if (e instanceof TTDevice0006ButtonPressed) {
//            TTDevice0006ButtonPressed ev = (TTDevice0006ButtonPressed) e;
//            TTDevice0006 device = (TTDevice0006) e.getSourceDevice();
//            if (ev.getButton() == TTDevice0006Button.LU)
//                device.displayMessage("Ala ma kota, ale kot nie ma ali");
//            if (ev.getButton() == TTDevice0006Button.LD)
//                device.displayText(0, 0, "Ala ma kota, ale kot nie\nma\nali");
//            if (ev.getButton() == TTDevice0006Button.RU)
//                device.displayText(1, 0, "Ala ma kota, ale kot nie\nma\nali");
//            if (ev.getButton() == TTDevice0006Button.RD)
//                device.displayText(1, 7, "Dupa\nWolowa");
//        }
    }
}
