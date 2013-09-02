/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package name.prokop.bart.hardware.driver.rfid.uhf28;

import java.text.SimpleDateFormat;
import java.util.Date;
import name.prokop.bart.hardware.driver.Device;

/**
 *
 * @author Administrator
 */
public abstract class UHF28TagEvent extends UHF28Event {

    private final TagEntry tagEntry;

    public UHF28TagEvent(Device source, TagEntry tagEntry) {
        super(source);
        this.tagEntry = tagEntry;
    }

    public String getSerial() {
        return tagEntry.getSerial();
    }

    @Override
    public long getEventTime() {
        return tagEntry.getCreated();
    }

    @Override
    public String toString() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss:sss");
        return sdf.format(new Date(getEventTime())) + " - " + getSerial();
    }
}
