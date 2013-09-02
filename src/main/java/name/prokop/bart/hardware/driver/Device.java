/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package name.prokop.bart.hardware.driver;

/**
 *
 * @author Bart
 */
public interface Device extends Comparable<Device> {

    /**
     * Adres urządzenia po którym jednoznacznie urządzenie identyfiluje BPDriver
     *
     * @return adres urządzenia (logiczny)
     */
    public String getDeviceAddress();

    /**
     * Opis urządzenia - informacyjnie dla GUI
     *
     * @return opis urządzenia
     */
    public String getDeviceDescription();

    /**
     * Informacja o stanie urządzenia w formie tekstowej
     *
     * @return informacja o urządzeniu
     */
    public String getDeviceInfo();
}
