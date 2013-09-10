/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package name.prokop.bart.driver.wire.ttbus;

/**
 *
 * @author rr163240
 */
public interface TTSoftConnection {

    public String getAddress();
    public byte[] talk(TTFrame frame);
    public void close();
}
