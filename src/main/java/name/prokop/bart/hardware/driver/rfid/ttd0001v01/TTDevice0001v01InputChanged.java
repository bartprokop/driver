/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package name.prokop.bart.hardware.driver.rfid.ttd0001v01;

/**
 *
 * @author bart
 */
public class TTDevice0001v01InputChanged extends TTDevice0001v01Event {

    private final int inputNumber;
    private final boolean open;

    public TTDevice0001v01InputChanged(TTDevice0001v01 device, int inputNumber, boolean open) {
        super(device);
        this.inputNumber = inputNumber;
        this.open = open;
    }

    public boolean isOpen() {
        return open;
    }

    public boolean isShort() {
        return !open;
    }

    public int getInputNumber() {
        return inputNumber;
    }

    @Override
    public String toString() {
        return super.toString() + " Input:" + inputNumber + "/" + (isOpen() ? "OPEN" : "SHORT");
    }
}
