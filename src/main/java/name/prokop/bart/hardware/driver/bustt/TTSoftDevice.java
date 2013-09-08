/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package name.prokop.bart.hardware.driver.bustt;

import java.io.IOException;
import java.util.Random;
import java.util.logging.Logger;
import name.prokop.bart.hardware.driver.Device;
import name.prokop.bart.hardware.driver.Driver;
import name.prokop.bart.hardware.driver.common.BitsAndBytes;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Base class for devices developed by TT Soft
 *
 * @author bart
 */
public abstract class TTSoftDevice implements Device {

    @Autowired
    protected Driver driver;
    private static final Logger logger = Logger.getLogger(TTSoftDevice.class.getName());
    private static final Random random = new Random(System.currentTimeMillis());
    protected final TTSoftBus bus;
    private final int id;
    private int lastTransactionId = 0;

    public TTSoftDevice(TTSoftBus bus, int id) {
        this.bus = bus;
        this.id = id;
    }

    public enum TTSoftDevicePriority {

        /**
         * Urządzenie odpytywane bez przerwy
         */
        High,
        /**
         * Urządzenie odpytywane normalnie
         */
        Normal,
        /**
         * Urządzenie odpytywane rzadko
         */
        Low;
    }

    protected abstract void takeControl();

    @Override
    public String getDeviceAddress() {
        return bus.getBusName() + ":" + id;
    }

    protected final byte[] talk(TTFrame frame) throws IOException {
        try {
            frame.setId(id);
            frame.setCurrTrId(newTransactionId());
            frame.setPrevTrId(lastTransactionId);
            byte[] retVal = frame.talk(bus);
            lastTransactionId = BitsAndBytes.promoteByteToInt(frame.getCurrTrId());
            return retVal;
        } catch (IOException ioe) {
            logger.warning(getDeviceAddress() + " IO error: " + ioe.getMessage());
            throw ioe;
        }
    }

    public abstract TTSoftDevicePriority getDevicePriority();

    private synchronized final int newTransactionId() {
        int retVal;
        do {
            retVal = random.nextInt(256);
        } while (retVal == 0 || retVal == lastTransactionId);
        return retVal;
    }
}
