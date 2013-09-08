/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package name.prokop.bart.hardware.driver.bustt;

import gnu.io.SerialPort;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import name.prokop.bart.hardware.driver.DeviceDropEvent;
import name.prokop.bart.hardware.driver.common.BitsAndBytes;
import name.prokop.bart.hardware.driver.common.PortEnumerator;

/**
 *
 * @author bart
 */
public class TTSoftBus {

    private static final Logger logger = Logger.getLogger(TTSoftBus.class.toString());
    public static final String CONFIG_KEY = TTSoftBus.class.getSimpleName();
    private List<TTSoftDevice> devices = new ArrayList<TTSoftDevice>();
    private SerialPort serialPort = null;
    private Socket socket = null;
    private DatagramSocket datagramSocket = null;
    private SocketAddress datagramRemoteSocketAddress = null;
    private boolean pleaseTerminate = false;
    private boolean terminated = false;
    private final String busName = "XXX";

    private TTSoftBus() {
    }

    private static SerialPort initializeSerialPort(String portName) throws Exception {
        SerialPort serialPort = PortEnumerator.getSerialPort(portName);
        serialPort.setSerialPortParams(9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_EVEN);
        serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);
        return serialPort;
    }

    private Socket initSocket(String hostName, int port) throws Exception {
        return new Socket(hostName, port);
    }

    private DatagramSocket initDatagramSocket(int localPort) throws Exception {
        DatagramSocket ds = new DatagramSocket(localPort);
        return ds;
    }

    public String getBusName() {
        return busName;
    }

    public void terminate() {
        pleaseTerminate = true;
    }

    public boolean isTerminated() {
        return terminated;
    }

    private static void discoverBusDevices(String comPortName) throws Exception {
        SerialPort comPort = initializeSerialPort(comPortName);
        for (int id = 1; id < 255; id++) {
            discoverDevice(id, comPort.getInputStream(), comPort.getOutputStream());
        }
        comPort.close();
    }

    private static void discoverBusDevices(String hostName, int port) throws Exception {
        Socket socket = new Socket(hostName, port);
        for (int id = 1; id < 255; id++) {
            discoverDevice(id, socket.getInputStream(), socket.getOutputStream());
        }
        socket.close();
    }

    private static void discoverBusDevices(String udpHost, int remote, int local) throws Exception {
        DatagramSocket ds = new DatagramSocket(local);
        SocketAddress sa = new InetSocketAddress(udpHost, remote);
        for (int id = 1; id < 255; id++) {
            discoverDevice(id, sa, ds);
        }
        ds.close();
    }

    private static void discoverDevice(int id, SocketAddress sa, DatagramSocket ds) throws Exception {
        TTFrame frame = new TTFrame(TTFrameType.FramePlugAndPlay, null);
        frame.setId(id);
        try {
            byte[] answer = frame.talk(sa, ds, 1, 120);
            discoverDevice(id, answer);
        } catch (IOException e) {
            System.out.println("Unsuccessful query @ " + id);
        }
    }

    private static void discoverDevice(int id, InputStream is, OutputStream os) throws Exception {
        TTFrame frame = new TTFrame(TTFrameType.FramePlugAndPlay, null);
        frame.setId(id);
        try {
            byte[] answer = frame.talk(is, os, 1, 120);
            discoverDevice(id, answer);
        } catch (IOException e) {
            System.out.println("Unsuccessful query @ " + id);
        }
    }

    private static void discoverDevice(int id, byte[] answer) throws Exception {
        String type = new String(BitsAndBytes.subArray(answer, 0, 4));
        String ver = new String(BitsAndBytes.subArray(answer, 4, 6));
        String sn = new String(BitsAndBytes.subArray(answer, 6, 12));
        System.err.println("Encrypted frames: " + (BitsAndBytes.subArray(answer, 12, 13)[0] != 48));
        if (type.equals("0001")) {
            if (ver.equals("01")) {
//                e = (Element) busElement.appendChild(Configuration.getInstance().createElement("TTD0001v01"));
                type += " (MIFARE Reader)";
            }
            if (ver.equals("03")) {
//                e = (Element) busElement.appendChild(Configuration.getInstance().createElement("TTD0001v03"));
                type += " (MIFARE Bike Reader)";
            }
        }

        if (type.equals("0005")) {
//            e = (Element) busElement.appendChild(Configuration.getInstance().createElement("TTD0005"));
            type += " (LCD mono display)";
        }

        System.out.println("Detected @ " + id + " : type: " + type + " v. " + ver + " s/n " + sn);
    }

    public void run() {
        while (!pleaseTerminate) {
            for (TTSoftDevice device : devices) {
                try {
                    device.takeControl();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
            }
        }
        for (TTSoftDevice device : devices) {
//            BPDriverBusHolder.getInstance().postEvent(DeviceDropEvent.generate(device));
        }

        try {
            if (serialPort != null) {
                serialPort.close();
            }
            if (socket != null) {
                socket.close();
            }
            if (datagramSocket != null) {
                datagramSocket.close();
            }
        } catch (Exception ex) {
        } finally {
            terminated = true;
        }
    }

    public static void main(String[] args) throws Exception {
        if (args[0].startsWith("+")) {
            if (args.length == 2) {
                discoverBusDevices(args[1]);
            }
            if (args.length == 3) {
                discoverBusDevices(args[1], Integer.parseInt(args[2]));
            }
            if (args.length == 4) {
                discoverBusDevices(args[1], Integer.parseInt(args[2]), Integer.parseInt(args[3]));
            }
        }
    }

    OutputStream getOutputStream() throws IOException {
        if (serialPort != null) {
            return serialPort.getOutputStream();
        }
        if (socket != null) {
            return socket.getOutputStream();
        }
        throw new IllegalStateException();
    }

    InputStream getInputStream() throws IOException {
        if (socket != null) {
            return socket.getInputStream();
        }
        if (serialPort != null) {
            return serialPort.getInputStream();
        }
        throw new IllegalStateException();
    }

    SocketAddress getDatagramRemoteSocketAddress() {
        return datagramRemoteSocketAddress;
    }

    DatagramSocket getDatagramSocket() {
        return datagramSocket;
    }

    public String getBusInfo() {
        String retVal = getBusName() + " (";
        if (serialPort != null) {
            retVal += serialPort.getName();
        }
        if (socket != null) {
            retVal += socket.getRemoteSocketAddress() + " : " + socket.getPort();
        }
        if (datagramSocket != null) {
            retVal += datagramSocket.getRemoteSocketAddress();
        }
        return retVal + ")";
    }
}
