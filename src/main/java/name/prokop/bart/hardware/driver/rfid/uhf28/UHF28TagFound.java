/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package name.prokop.bart.hardware.driver.rfid.uhf28;

import name.prokop.bart.hardware.driver.Device;

/**
 *
 * @author Administrator
 */
public class UHF28TagFound extends UHF28TagEvent {

    public UHF28TagFound(Device source, TagEntry tagEntry) {
        super(source, tagEntry);
    }

    @Override
    public String toString() {
        return super.toString() + " - ZNALEZIONO";
    }
    
    
}
