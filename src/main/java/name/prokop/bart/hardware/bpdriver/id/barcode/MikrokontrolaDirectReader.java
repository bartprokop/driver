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
import name.prokop.bart.hardware.driver.Driver;
import name.prokop.bart.hardware.driver.Event;
import name.prokop.bart.hardware.driver.DeviceDetectedEvent;
import name.prokop.bart.hardware.driver.DeviceDropEvent;
import name.prokop.bart.util.ToString;
import name.prokop.bart.util.lang.BartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 *
 * @author Bart
 */
public class MikrokontrolaDirectReader implements Runnable, Device {

    @Autowired
    private Driver driver;
    private String busName;
    private SerialPort serialPort;
    private InputStream inputStream;
    private Thread thread;
    private boolean pleaseTerminate = false;
    private boolean terminated = false;

    private boolean initializeSerialPort(String portName) {
        try {
            serialPort = name.prokop.bart.hardware.comm.PortEnumerator.getSerialPort(portName);
        } catch (Exception e) {
            return false;
        }

        try {
            serialPort.setSerialPortParams(2400,
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
                Thread.sleep(100);
            } catch (InterruptedException e) {
            }
            try {
                if (inputStream.available() >= 5) {
                    int size = inputStream.available();
                    byte[] buf = new byte[size];
                    inputStream.read(buf, 0, size);
                    buffer = "";
                    for (int i = 0; i < 5; i++) {
                        buffer += ToString.byteToHexString(buf[i]);
                    }
                    buffer += '\n';
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

            int cutPos1 = buffer.indexOf('\n');//CR

            if (cutPos1 > 0) {
                //postEvents(getBusName() + " " + Long.toString(System.currentTimeMillis()) + " MKDR_SIMPLE CardDetected "+buffer.substring(0, cutPos1));
                postEvents(BarCodeScannedEvent.produceEvent(this, buffer.substring(0, cutPos1)));
                buffer = buffer.substring(cutPos1);
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

    public boolean isTerminated() {
        return terminated;
    }

    public int compareTo(Device o) {
        return getDeviceAddress().compareTo(o.getDeviceAddress());
    }

    public String getBusInfo() {
        return "Wypełnić danymi";
    }

    public String getDeviceDescription() {
        return "Wypełnić danymi";
    }

    public String getDeviceInfo() {
        return "Wypełnić danymi";
    }

    public void setDevices(Device[] devices) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
