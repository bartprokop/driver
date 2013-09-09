/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package name.prokop.bart.driver.wire.ttbus;

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
    private final TTSoftConnection connection;

    public TTSoftDevice(TTSoftBus bus, int id, TTSoftConnection connection) {
        this.bus = bus;
        this.id = id;
        this.connection = connection;
    }

    protected abstract void takeControl();

    @Override
    public String getDeviceAddress() {
        return bus.getBusName() + ":" + id;
    }

    protected final byte[] talk(TTFrame frame) throws IOException {
//        try {
        frame.setId(id);
        frame.setCurrTrId(newTransactionId());
        frame.setPrevTrId(lastTransactionId);
        byte[] retVal = connection.talk(frame);
        lastTransactionId = BitsAndBytes.promoteByteToInt(frame.getCurrTrId());
        return retVal;
//        } catch (IOException ioe) {
//            logger.warning(getDeviceAddress() + " IO error: " + ioe.getMessage());
//            throw ioe;
//    }
    }

    public abstract TTSoftDevicePriority getDevicePriority();

    private synchronized int newTransactionId() {
        int retVal;
        do {
            retVal = random.nextInt(256);
        } while (retVal == 0 || retVal == lastTransactionId);
        return retVal;
    }
}
