/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package name.prokop.bart.hardware.driver.rfid;

/**
 *
 * @author bart
 */
public interface MifareReader extends RFIDDevice {

    public enum ClassicKeyType {

        KeyA, KeyB;
    }

    public byte[] readBlock(ClassicKeyType keyType, byte[] key, int blockNumber) throws MifareException;

    public void writeBlock(ClassicKeyType keyType, byte[] key, int blockNumber, byte[] data) throws MifareException;
}
