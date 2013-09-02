/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package name.prokop.bart.hardware.driver.rfid.tr610.db;

/**
 *
 * @author bart
 */
public interface TibboDatabaseRecord {

    public String toUDPSequence();

    public int getPos();

    public void setPos(int pos);
}
