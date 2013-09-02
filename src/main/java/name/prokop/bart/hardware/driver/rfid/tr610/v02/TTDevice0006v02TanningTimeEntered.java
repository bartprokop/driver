/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package name.prokop.bart.hardware.driver.rfid.tr610.v02;

import name.prokop.bart.hardware.driver.rfid.tr610.TTDevice0006Event;

/**
 *
 * @author bart
 */
public class TTDevice0006v02TanningTimeEntered extends TTDevice0006Event {

    private final String cardSerialNumber;
    private final int time;

    public TTDevice0006v02TanningTimeEntered(TTDevice0006v02 device, String sn, String time) {
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

        this.time = Integer.parseInt(time);
    }

    public boolean isLocalReader() {
        return false;
    }

    @Override
    public String toString() {
        return super.toString() + "Karta: " + cardSerialNumber + " Time: " + time + " s.";
    }

    public String getCardSerialNumber() {
        return cardSerialNumber;
    }

    public int getTime() {
        return time;
    }
}
