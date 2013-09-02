/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package name.prokop.bart.hardware.driver.rfid;

/**
 *
 * @author Krzysiek7
 */
public interface ReadBlockAbility {

    public void readBlock(int blockid);

    public void writeBlock(int blockid, byte[] data);

    public void eraseBlock(int blockid);
    
    public byte[] getSecurityKey();
}
