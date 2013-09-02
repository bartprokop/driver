/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package name.prokop.bart.hardware.driver.rfid.tr610;

/**
 *
 * @author bart
 */
public class TTDevice0006ButtonPressed extends TTDevice0006Event {

    private TTDevice0006Button button;

    public TTDevice0006ButtonPressed(TTDevice0006 device, String button) {
        super(device);
        this.button = TTDevice0006Button.valueOf(button);
    }

    public boolean isLocalReader() {
        return false;
    }

    @Override
    public String toString() {
        return super.toString() + " Button:" + button;
    }

    /**
     * @return the button
     */
    public TTDevice0006Button getButton() {
        return button;
    }
}
