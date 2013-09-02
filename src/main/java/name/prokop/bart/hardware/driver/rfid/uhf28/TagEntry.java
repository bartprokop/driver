/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package name.prokop.bart.hardware.driver.rfid.uhf28;

import java.util.Date;

/**
 *
 * @author Administrator
 */
public class TagEntry {

    private final String serial;
    private final long created = System.currentTimeMillis();

    TagEntry(byte[] sn) {
        StringBuilder sb = new StringBuilder();
        for (byte b : sn) {
            sb.append(Integer.toHexString(b & 0xff));
        }
        serial = sb.toString().toUpperCase();
    }

    @Override
    public boolean equals(Object obj) {
        TagEntry o = (TagEntry) obj;
        return serial.equals(o.serial);
    }

    @Override
    public int hashCode() {
        return serial.hashCode();
    }

    @Override
    public String toString() {
        return serial + " @ " + new Date(created);
    }

    public long getCreated() {
        return created;
    }

    public String getSerial() {
        return serial;
    }
}
