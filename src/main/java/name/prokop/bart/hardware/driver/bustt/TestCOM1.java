/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package name.prokop.bart.hardware.driver.bustt;

/**
 *
 * @author bart
 */
public class TestCOM1 {

    public static void main(String... args) throws Exception{
        TTSoftBus.main(new String[]{"+RWR", "COM1"});
    }
}
