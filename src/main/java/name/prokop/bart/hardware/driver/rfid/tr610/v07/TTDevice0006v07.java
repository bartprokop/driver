/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package name.prokop.bart.hardware.driver.rfid.tr610.v07;

import java.net.SocketAddress;
import name.prokop.bart.hardware.driver.rfid.tr610.TTDevice0006;
import name.prokop.bart.hardware.driver.rfid.tr610.TTDevice0006Server;

/**
 * E-Dziecko - kontroler obecności dziecka w przedszkolu
 * @author bart
 */
public class TTDevice0006v07 extends TTDevice0006 {

    public TTDevice0006v07(TTDevice0006Server parent, SocketAddress socketAddress, String serialNumber) {
        super(parent, socketAddress, serialNumber);
    }

    @Override
    protected void processDatagram(String[] tokens) {
        super.processDatagram(tokens);
    }

    public String getDeviceDescription() {
        return "EDziecko";
    }
}
