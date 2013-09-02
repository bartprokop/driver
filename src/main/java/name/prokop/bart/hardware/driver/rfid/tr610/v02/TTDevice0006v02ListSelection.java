/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package name.prokop.bart.hardware.driver.rfid.tr610.v02;

import java.util.Map;
import name.prokop.bart.hardware.driver.rfid.tr610.TTDevice0006Event;

/**
 *
 * @author bart
 */
public class TTDevice0006v02ListSelection extends TTDevice0006Event {

    private final String cardSerialNumber;
    private final Map<Integer, Integer> selection;

    public TTDevice0006v02ListSelection(TTDevice0006v02 device, String sn, Map<Integer, Integer> selection) {
        super(device);
        switch (sn.length()) {
            case 8:
                cardSerialNumber = "M1" + sn;
                break;
            case 10:
                cardSerialNumber = "UQ" + sn;
                break;
            case 14:
                cardSerialNumber = "Ul" + sn;
                break;
            default:
                throw new IllegalStateException();
        }

        this.selection = selection;
    }

    public boolean isLocalReader() {
        return false;
    }

    @Override
    public String toString() {
        return super.toString() + "Karta: " + cardSerialNumber + " Wybór: " + selection + ".";
    }

    public String getCardSerialNumber() {
        return cardSerialNumber;
    }

    public Map<Integer, Integer> getSelection() {
        return selection;
    }
}
