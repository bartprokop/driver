/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package name.prokop.bart.hardware.driver.rfid.tr610;

import java.io.IOException;
import java.net.*;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import name.prokop.bart.hardware.driver.Device;
import name.prokop.bart.hardware.driver.Driver;
import name.prokop.bart.hardware.driver.DeviceDropEvent;
import name.prokop.bart.hardware.driver.rfid.tr610.v02.TTDevice0006v02;
import name.prokop.bart.hardware.driver.rfid.tr610.v03.TTDevice0006v03;
import name.prokop.bart.hardware.driver.rfid.tr610.v04.TTDevice0006v04;
import name.prokop.bart.hardware.driver.rfid.tr610.v05.TTDevice0006v05;
import name.prokop.bart.hardware.driver.rfid.tr610.v06.TTDevice0006v06;
import name.prokop.bart.hardware.driver.rfid.tr610.v07.TTDevice0006v07;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.Element;

/**
 *
 * @author bart
 */
public class TTDevice0006Server implements Runnable {

    @Autowired
    private Driver driver;
    public static final String CONFIG_KEY = TTDevice0006Server.class.getSimpleName();
    private static final Logger logger = LoggerFactory.getLogger(TTDevice0006Server.class);
    private Map<SocketAddress, TTDevice0006> devices = new HashMap<SocketAddress, TTDevice0006>();
    private static final Map<String, String> uptodateSignatures = new HashMap<String, String>();

    static {
        uptodateSignatures.put("TTD-0006-XX", "1234567890ABCDEF"); // Coś dla jaj
        uptodateSignatures.put("TTD-0006-02", "TTD-0006-02r0011"); // LAN READER
        uptodateSignatures.put("TTD-0006-03", "TTD-0006-03r0014"); // ROWERES
        uptodateSignatures.put("TTD-0006-04", "TTD-0006-04r0010");
        uptodateSignatures.put("TTD-0006-05", "TTD-0006-05r0010");
        uptodateSignatures.put("TTD-0006-06", "TTD-0006-06r0010");
        uptodateSignatures.put("TTD-0006-07", "TTD-0006-07r0010"); // E-Dziecko
    }
    private String busName = "Tibbo";
    DatagramSocket datagramSocket = null;
    private byte[] receiveData = new byte[1024];

    public TTDevice0006Server() {
        try {
            initializePort();
        } catch (Exception e) {
            logger.warn("Nie moge otworzyc portu UDP 2009" + e);
            return;
        }
    }

    @PostConstruct
    private void init() {
        System.out.println("init - bus");
        new Thread(this).start();
    }

    @PreDestroy
    private void stop() {
        pleaseTerminate = true;
    }

    public final void initializePort() throws SocketException {
        datagramSocket = new DatagramSocket(2009);
        datagramSocket.setSoTimeout(1000);
        logger.info("UDP Socket opend on 2006 port.");
    }

    public String getBusName() {
        return busName;
    }
    private boolean pleaseTerminate = false;

    public void run() {
        try {
            while (!pleaseTerminate) {
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                try {
                    checkOutdated();
                    datagramSocket.receive(receivePacket);
                    String datagram = new String(receivePacket.getData(), receivePacket.getOffset(), receivePacket.getLength());
                    String datagramTokens[] = datagram.split("\t");
                    //System.out.println(receivePacket.getAddress() + " << " + datagram);
                    if (devices.get(receivePacket.getSocketAddress()) == null && datagramTokens[0].equals("0P")) {
                        String deviceType = datagramTokens[1];
                        String serialNumber = datagramTokens[2];
                        String signature = datagramTokens[3];
                        String desiredSg = uptodateSignatures.get(deviceType);
                        if (desiredSg != null && !desiredSg.equals(signature)) {
                            logger.warn("Device: " + deviceType + ", serial number: " + serialNumber + " uses firmware: " + signature + ". Please upgrade to: " + desiredSg + ".");
                        }

                        checkDuplicatedSerialNumber(serialNumber);

                        if (deviceType.equals("TTD-0006-02")) {
                            devices.put(receivePacket.getSocketAddress(), new TTDevice0006v02(this, receivePacket.getSocketAddress(), serialNumber));
                        }
                        if (deviceType.equals("TTD-0006-03")) {
                            devices.put(receivePacket.getSocketAddress(), new TTDevice0006v03(this, receivePacket.getSocketAddress(), serialNumber));
                        }
                        if (deviceType.equals("TTD-0006-04")) {
                            devices.put(receivePacket.getSocketAddress(), new TTDevice0006v04(this, receivePacket.getSocketAddress(), serialNumber));
                        }
                        if (deviceType.equals("TTD-0006-05")) {
                            devices.put(receivePacket.getSocketAddress(), new TTDevice0006v05(this, receivePacket.getSocketAddress(), serialNumber));
                        }
                        if (deviceType.equals("TTD-0006-06")) {
                            devices.put(receivePacket.getSocketAddress(), new TTDevice0006v06(this, receivePacket.getSocketAddress(), serialNumber));
                        }
                        if (deviceType.equals("TTD-0006-07")) {
                            devices.put(receivePacket.getSocketAddress(), new TTDevice0006v07(this, receivePacket.getSocketAddress(), serialNumber));
                        }
                    }
                    TTDevice0006 device = devices.get(receivePacket.getSocketAddress());
                    if (device != null) {
                        device.processDatagram(datagramTokens);
                    } else {
                        logger.warn("No device for " + receivePacket.getSocketAddress());
                    }
                } catch (SocketTimeoutException ste) {
                } catch (IOException ioe) {
                    logger.warn(busName, ioe);
                } catch (Exception e) {
                    logger.warn(busName, e);
                }
            }
            for (TTDevice0006 device : devices.values()) {
                driver.postEvent(new DeviceDropEvent(device));
            }
        } finally {
            datagramSocket.close();
        }
    }

    private void checkOutdated() {
        TTDevice0006 deviceToDelete = null;
        for (TTDevice0006 ttd : devices.values()) {
            if (System.currentTimeMillis() - ttd.getLastSeen() > 24000) {
                deviceToDelete = ttd;
            }
        }

        if (deviceToDelete != null) {
            driver.postEvent(new DeviceDropEvent(deviceToDelete));
            devices.remove(deviceToDelete.getSocketAddress());
            System.out.println("Removing Tibbo device (timeout): " + deviceToDelete);
        }
    }

    private void checkDuplicatedSerialNumber(String serialNumber) {
        TTDevice0006 deviceToDelete = null;
        for (TTDevice0006 ttd : devices.values()) {
            if (ttd.getSerialNumber().equals(serialNumber)) {
                deviceToDelete = ttd;
            }
        }

        if (deviceToDelete != null) {
            driver.postEvent(new DeviceDropEvent(deviceToDelete));
            devices.remove(deviceToDelete.getSocketAddress());
            System.out.println("Removing Tibbo device (duplicate): " + deviceToDelete);
        }
    }

    public String getBusInfo() {
        return datagramSocket.getLocalSocketAddress().toString();
    }

    public void setDevices(Device[] devices) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
