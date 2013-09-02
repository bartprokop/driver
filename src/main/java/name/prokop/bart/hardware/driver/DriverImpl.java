package name.prokop.bart.hardware.driver;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.springframework.stereotype.Service;

/**
 *
 * @author Bart Prokop
 */
@Service
public class DriverImpl implements Driver {

    /**
     * Events received from devices. It is used as global event queue
     */
    private final HashMap<String, Device> devices = new HashMap<String, Device>();
    private final EventBus eventBus = new EventBus();

    @PostConstruct
    private void start() {
        eventBus.register(this);
    }

    @PreDestroy
    private void stop() {
        eventBus.unregister(this);
    }

    /**
     * Adds event to global event queue
     *
     * @param event to add to queue
     */
    public void postEvent(Event event) {
        eventBus.post(event);
    }

    public void registerListener(Object o) {
        eventBus.register(o);
    }

    @Subscribe
    public void handleEvent(DeviceDetectedEvent event) {
        synchronized (devices) {
            devices.put(event.getSourceDevice().getDeviceAddress(), event.getSourceDevice());
        }
    }

    @Subscribe
    public void handleEvent(DeviceDropEvent event) {
        synchronized (devices) {
            devices.remove(event.getSourceDevice().getDeviceAddress());
        }
    }

    public Device getDevice(String addr) {
        synchronized (devices) {
            return devices.get(addr);
        }
    }

    public Map<String, Device> getDevices() {
        synchronized (devices) {
            @SuppressWarnings("unchecked")
            Map<String, Device> retVal = (Map<String, Device>) devices.clone();
            return retVal;
        }
    }
}
