/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package name.prokop.bart.hardware.driver.rfid.uhf28;

import gnu.io.SerialPort;
import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import name.prokop.bart.hardware.comm.PortEnumerator;
import name.prokop.bart.hardware.driver.Device;
import name.prokop.bart.hardware.driver.DeviceDetectedEvent;
import name.prokop.bart.hardware.driver.DeviceDropEvent;
import name.prokop.bart.hardware.driver.Driver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author Administrator
 */
public class UHF28 implements Device, Runnable {

    private final static Logger logger = LoggerFactory.getLogger(UHF28.class);
    @Autowired
    private Driver driver;
    private String comPort;
    private int baudRate = 115200;
    private String ipAddress;
    private int ipPort = 27011;
    private Socket socket;
    private SerialPort serialPort;
    private InputStream inputStream;
    private OutputStream outputStream;
    private Protocol protocol = new Protocol();
    private String deviceAddress;
    private boolean pleaseTerminate = false;
    private Map<String, TagEntry> tagsInField = new HashMap<String, TagEntry>();

    public void setComPort(String comPort) {
        this.comPort = comPort;
    }

    public void setBaudRate(int baudRate) {
        this.baudRate = baudRate;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public void setIpPort(int ipPort) {
        this.ipPort = ipPort;
    }

    @PostConstruct
    private void start() {
        Thread thread = new Thread(this);
        thread.start();
    }

    @PreDestroy
    private void destroy() {
        pleaseTerminate = true;
    }

    private void setUpConnection() {
        if (comPort != null) {
            try {
                serialPort = PortEnumerator.getSerialPort(comPort);
                serialPort.setSerialPortParams(baudRate, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
                serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);
                inputStream = serialPort.getInputStream();
                outputStream = serialPort.getOutputStream();
            } catch (Exception e) {
                serialPort = null;
                e.printStackTrace();
            }
        } else if (ipAddress != null) {
            try {
                socket = new Socket(ipAddress, ipPort);
                inputStream = socket.getInputStream();
                outputStream = socket.getOutputStream();
            } catch (Exception e) {
                socket = null;
                e.printStackTrace();
            }
        }

        Protocol.sleep(150);

        if (serialPort == null && socket == null) {
            inputStream = new ByteArrayInputStream("".getBytes());
            outputStream = new ByteArrayOutputStream();
        }
    }

    private void closeConnections() {
        if (serialPort != null) {
            serialPort.close();
        }
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException ioe) {
            }
        }
        inputStream = null;
        outputStream = null;
    }

    public String getDeviceAddress() {
        return deviceAddress;
    }

    public void setDeviceAddress(String deviceAddress) {
        this.deviceAddress = deviceAddress;
    }

    public String getDeviceDescription() {
        return "Long Range UHF Reader";
    }

    public String getDeviceInfo() {
        return ":)";
    }

    public int compareTo(Device o) {
        return deviceAddress.compareTo(o.getDeviceAddress());
    }

    @Override
    public void run() {
        setUpConnection();
        driver.postEvent(new DeviceDetectedEvent(this));
        while (!pleaseTerminate) {
            Protocol.sleep1ms();
            protocol.cmdInventory();
            try {
                protocol.flush(inputStream);
                protocol.send(outputStream);
                protocol.receive(inputStream);
                Inventory inventory = protocol.getInventory();

                // update current tags in field
                for (TagEntry tagEntry : inventory.getTags()) {
                    if (tagsInField.get(tagEntry.getSerial()) == null) {
                        driver.postEvent(new UHF28TagFound(this, tagEntry));
                    }
                    tagsInField.put(tagEntry.getSerial(), tagEntry);
                }
                // remove tags not in field anymore
                Iterator<TagEntry> iterator = tagsInField.values().iterator();
                while (iterator.hasNext()) {
                    TagEntry tagEntry = iterator.next();
                    if (tagEntry.getCreated() < System.currentTimeMillis() - 3000) {
                        driver.postEvent(new UHF28TagLost(this, tagEntry));
                        iterator.remove();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        closeConnections();
        driver.postEvent(new DeviceDropEvent(this));
    }
}
