/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package name.prokop.bart.hardware.driver.rfid;

import java.io.IOException;

/**
 *
 * @author bart
 */
public class MifareException extends IOException {

    public MifareException(String cause) {
        super(cause);
    }

    public MifareException(Throwable t) {
        super(t);
    }
}
