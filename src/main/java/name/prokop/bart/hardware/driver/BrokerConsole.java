/*
 * BrokerConsole.java
 *
 * Created on 12 czerwiec 2004, 19:59
 */
package name.prokop.bart.hardware.driver;

import com.google.common.eventbus.Subscribe;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Trivial implementation that just prints events to console
 *
 * @author Bart
 */
@Component
public class BrokerConsole {

    private int eventCount = 0;
    @Autowired
    private Driver driver;

    /**
     * Creates a new instance of BrokerConsole
     */
    @PostConstruct
    private void init() {
        driver.registerListener(this);
    }

    @Subscribe
    public void handleEvent(Event event) {
        System.out.println("Event " + Integer.toString(++eventCount) + ": " + event);
    }
}
