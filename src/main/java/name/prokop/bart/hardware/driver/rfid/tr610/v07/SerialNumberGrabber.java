/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package name.prokop.bart.hardware.driver.rfid.tr610.v07;

import java.util.LinkedHashSet;
import java.util.Set;
import name.prokop.bart.hardware.driver.Event;
import name.prokop.bart.hardware.driver.rfid.RFIDCardDetectedProperty;

/**
 *
 * @author bart
 */
public class SerialNumberGrabber {

    private Set<String> serialNumbers = new LinkedHashSet<String>();

    public static void main(String... args) throws Exception {
//        BPDriver.runInBackground(false);
//        BrokerEvent.getInstance().addListener(new SerialNumberGrabber());
    }

    public void bpdriverEventOccured(Event e) {
        if (e instanceof RFIDCardDetectedProperty) {
            RFIDCardDetectedProperty x = (RFIDCardDetectedProperty) e;
            serialNumbers.add(x.getSerialNumber());
            System.err.println("--------------------------");
            for (String s : serialNumbers) {
                System.err.println(s);
            }
        }
    }
}
