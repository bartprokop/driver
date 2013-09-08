package name.prokop.bart.hardware.driver;

import java.util.Map;

/**
 * @author Bart Prokop
 */
public interface Driver {

    /**
     * Adds event to global event queue
     *
     * @param event to add to queue
     */
    public void postEvent(Event event);

    public void registerListener(Object o);

    public Device getDevice(String addr);

    public Map<String, Device> getDevices();
}
