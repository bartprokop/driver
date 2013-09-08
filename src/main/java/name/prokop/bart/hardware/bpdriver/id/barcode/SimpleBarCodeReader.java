/*
 * SimpleBarCodeReader.java
 *
 * Created on 22 listopad 2004, 18:06
 */
package name.prokop.bart.hardware.bpdriver.id.barcode;

import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;
import java.io.IOException;
import java.io.InputStream;
import name.prokop.bart.hardware.driver.Device;
import name.prokop.bart.hardware.driver.Event;
import name.prokop.bart.hardware.driver.Driver;
import name.prokop.bart.hardware.driver.DeviceDetectedEvent;
import name.prokop.bart.hardware.driver.DeviceDropEvent;
import name.prokop.bart.hardware.driver.common.PortEnumerator;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author Bart
 */
public class SimpleBarCodeReader implements Runnable, Device {

    @Autowired
    Driver driver;
    public static final String CONFIG_KEY = SimpleBarCodeReader.class.getSimpleName();
    private final String busName;
    private SerialPort serialPort;
    private InputStream inputStream;
    private boolean pleaseTerminate = false;
    private boolean terminated = false;

    /**
     * Creates a new instance of UnicardBus
     */
    public SimpleBarCodeReader(String comPort) throws IOException {
        this.busName = comPort;

        if (!initializeSerialPort(comPort)) {
            throw new IOException("Nie moge zainicjowac portu szeregowego: " + comPort);
        }
    }

    private boolean initializeSerialPort(String portName) {
        try {
            serialPort = PortEnumerator.getSerialPort(portName);
        } catch (Exception e) {
            return false;
        }

        try {
            serialPort.setSerialPortParams(9600,
                    SerialPort.DATABITS_8,
                    SerialPort.STOPBITS_1,
                    SerialPort.PARITY_NONE);
            serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);

            inputStream = serialPort.getInputStream();
        } catch (UnsupportedCommOperationException e) {
            return false;
        } catch (IOException e) {
            return false;
        }

        return true;
    }

    @Override
    public void run() {
        postEvents(new DeviceDetectedEvent(this));
        try {
            while (inputStream.available() > 0) {
                inputStream.read();
            }
        } catch (IOException e) {
        }

        String buffer = "";
        while (!pleaseTerminate) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
            }
            try {
                if (inputStream.available() > 0) {
                    int size = inputStream.available();
                    byte[] buf = new byte[size];
                    inputStream.read(buf, 0, size);
                    buffer += new String(buf);
                }
            } catch (IOException e) {
                buffer = "";
            }

            if (buffer.equals("")) {
                continue;
            }

            while (buffer.indexOf('\r') == 0) {
                buffer = buffer.substring(1);
            }
            while (buffer.indexOf('\n') == 0) {
                buffer = buffer.substring(1);
            }

            {
                int cutPos1 = buffer.indexOf('\n');//CR
                int cutPos2 = buffer.indexOf('\r');//LF

                if (cutPos2 < cutPos1) {
                    cutPos1 = cutPos2;
                }

                if (cutPos1 > 0) {
                    postEvents(BarCodeScannedEvent.produceEvent(this, buffer.substring(0, cutPos1)));
                    buffer = buffer.substring(cutPos1);
                }
            }
        }

        serialPort.close();
        postEvents(new DeviceDropEvent(this));
        terminated = true;
    }

    private void postEvents(Event event) {
        driver.postEvent(event);
    }

    @Override
    public String getDeviceAddress() {
        return busName;
    }

    public int compareTo(Device o) {
        return getDeviceAddress().compareTo(o.getDeviceAddress());
    }

    public String getBusInfo() {
        return "Wypełnić danymi";
    }

    public String getDeviceDescription() {
        return "Podstawowy czytnik kodów kreskowych";
    }

    public String getDeviceInfo() {
        return "Wypełnić danymi";
    }

    public void setDevices(Device[] devices) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
