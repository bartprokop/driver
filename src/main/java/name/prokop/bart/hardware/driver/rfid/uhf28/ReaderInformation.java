/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package name.prokop.bart.hardware.driver.rfid.uhf28;

/**
 *
 * @author Administrator
 */
public class ReaderInformation {

    private String version;
    private String type;

    ReaderInformation(byte[] frame) {
        version = frame[4] + "." + frame[5];
        if (frame[6] == 0xa) {
            type = "UHFREADER28";
        } else {
            type = "unknown";
        }
    }

    public String getVersion() {
        return version;
    }

    public String getType() {
        return type;
    }
}
