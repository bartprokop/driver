/*
 * SimpleBarCodeReader.java
 *
 * Created on 22 listopad 2004, 18:06
 */
package name.prokop.bart.hardware.bpdriver.id.barcode;

import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import name.prokop.bart.hardware.driver.Device;
import name.prokop.bart.hardware.driver.Driver;
import name.prokop.bart.hardware.driver.Event;
import name.prokop.bart.hardware.driver.DeviceDetectedEvent;
import name.prokop.bart.hardware.driver.DeviceDropEvent;
import name.prokop.bart.util.lang.BartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.Element;

/**
 *
 * @author Bart
 */
public class HoneywellBarCodeReader implements Runnable, Device {

    @Autowired
    private Driver driver;
    public static final String CONFIG_KEY = HoneywellBarCodeReader.class.getSimpleName();
    private final String busName;
    private boolean pleaseTerminate = false;
    private boolean terminated = false;
    private Socket socket;
    private SerialPort serialPort;
    private BufferedReader bufferedReader;

    public HoneywellBarCodeReader(String ip, int port, String name) throws BartException {
        this.busName = name;

        try {
            initSocket(ip, port);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BartException("Cannot establish network conenction", e);
        }
    }

    /**
     * Creates a new instance of UnicardBus
     */
    public HoneywellBarCodeReader(Element busConf) throws BartException {
        this.busName = busConf.getAttribute("name");

        try {
            if (!busConf.getAttribute("serialPort").equals("")) {
                initializeSerialPort(busConf.getAttribute("serialPort"));
            } else {
                initSocket(busConf.getAttribute("ipAddress"), Integer.parseInt(busConf.getAttribute("ipPort")));
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new BartException("Cannot establish network conenction", e);
        }
    }

    /**
     * Creates a new instance of UnicardBus
     */
    private HoneywellBarCodeReader(String comPort) throws BartException, IOException {
        this.busName = comPort;

        if (!initializeSerialPort(comPort)) {
            throw new BartException("Nie moge zainicjowac portu szeregowego: " + comPort);
        }

    }

    private boolean initializeSerialPort(String portName) throws IOException {
        try {
            serialPort = name.prokop.bart.hardware.comm.PortEnumerator.getSerialPort(portName);
        } catch (Exception e) {
            return false;
        }

        try {
            serialPort.setSerialPortParams(9600,
                    SerialPort.DATABITS_7,
                    SerialPort.STOPBITS_2,
                    SerialPort.PARITY_SPACE);
            serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);
            bufferedReader = new BufferedReader(new InputStreamReader(serialPort.getInputStream()));
        } catch (UnsupportedCommOperationException e) {
            return false;
        }
        return true;
    }

    private void initSocket(String ip, int port) throws Exception {
        socket = new Socket(ip, port);
        bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

    }

    @Override
    public void run() {
        postEvents(new DeviceDetectedEvent(this));

        while (!pleaseTerminate) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
            }

            try {
                if (bufferedReader.ready()) {
                    String buffer = bufferedReader.readLine();
                    if (buffer != null) {
                        BarCodeScannedEvent event = BarCodeScannedEvent.produceEvent(this, buffer);
                        driver.postEvent(event);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        postEvents(new DeviceDropEvent(this));
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void postEvents(Event event) {
        driver.postEvent(event);
    }

    @Override
    public String getDeviceAddress() {
        return busName + ":1";
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
