package name.prokop.bart.driver.wire.ttbus;

import java.io.IOException;
import java.util.Random;
import name.prokop.bart.commons.bits.IntegerBits;
import name.prokop.bart.hardware.driver.Device;
import name.prokop.bart.hardware.driver.Driver;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Base class for devices developed by TT Soft
 *
 * @author bart
 */
public abstract class TTSoftDevice implements Device {

    @Autowired
    private Driver driver;
    private static final Random random = new Random(System.currentTimeMillis());
    private final int id;
    private int lastTransactionId = 0;
    protected final TTSoftConnection connection;

    public TTSoftDevice(int id, TTSoftConnection connection) {
        this.id = id;
        this.connection = connection;
    }

    protected abstract void takeControl();

    public abstract TTSoftDevicePriority getDevicePriority();

    protected void postEvent(Object o) {
        if (driver != null) {
        } else {
            System.out.println(o);
        }
    }

    @Override
    public String getDeviceAddress() {
        return connection.getAddress() + ":" + id;
    }

    protected final byte[] talk(TTSoftFrame frame) throws IOException {
        frame.setId(id);
        frame.setCurrTrId(newTransactionId());
        frame.setPrevTrId(lastTransactionId);
        byte[] retVal = connection.talk(frame);
        lastTransactionId = IntegerBits.promote(frame.getCurrTrId());
        return retVal;
    }

    private synchronized int newTransactionId() {
        int retVal;
        do {
            retVal = random.nextInt(256);
        } while (retVal == 0 || retVal == lastTransactionId);
        return retVal;
    }
}
