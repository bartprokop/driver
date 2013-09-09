package name.prokop.bart.driver.wire.ttbus;

import gnu.io.SerialPort;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;
import name.prokop.bart.hardware.driver.common.BitsAndBytes;

/**
 *
 * @author bart
 */
public class TTSoftBus {

    private List<TTSoftDevice> devices = new ArrayList<>();
    private SerialPort serialPort = null;
    private Socket socket = null;
    private DatagramSocket datagramSocket = null;
    private SocketAddress datagramRemoteSocketAddress = null;
    private boolean pleaseTerminate = false;
    private boolean terminated = false;
    private final String busName = "XXX";
    private final TTSoftConnection connection;

    private TTSoftBus(TTSoftConnection connection) {
        this.connection = connection;
    }

//    private DatagramSocket initDatagramSocket(int localPort) throws Exception {
//        DatagramSocket ds = new DatagramSocket(localPort);
//        return ds;
//    }
    public String getBusName() {
        return busName;
    }

    public void terminate() {
        pleaseTerminate = true;
    }

    public boolean isTerminated() {
        return terminated;
    }

    public static void discoverDevices(TTSoftConnection connection) throws Exception {
        for (int id = 1; id < 255; id++) {
            discoverDevice(connection, id);
        }
    }

//    private static void discoverBusDevices(String hostName, int port) throws Exception {
//        Socket socket = new Socket(hostName, port);
//        for (int id = 1; id < 255; id++) {
//            discoverDevice(id, socket.getInputStream(), socket.getOutputStream());
//        }
//        socket.close();
//    }
//    private static void discoverBusDevices(String udpHost, int remote, int local) throws Exception {
//        DatagramSocket ds = new DatagramSocket(local);
//        SocketAddress sa = new InetSocketAddress(udpHost, remote);
//        for (int id = 1; id < 255; id++) {
//            discoverDevice(id, sa, ds);
//        }
//        ds.close();
//    }
//    private static void discoverDevice(int id, SocketAddress sa, DatagramSocket ds) throws Exception {
//        TTFrame frame = new TTFrame(TTFrameType.FramePlugAndPlay, null);
//        frame.setId(id);
//        try {
//            byte[] answer = frame.talk(sa, ds, 1, 120);
//            discoverDevice(id, answer);
//        } catch (IOException e) {
//            System.out.println("Unsuccessful query @ " + id);
//        }
//    }
    private static void discoverDevice(TTSoftConnection connection, int id) throws Exception {
        TTFrame frame = new TTFrame(TTFrameType.FramePlugAndPlay, null);
        frame.setId(id);
            byte[] answer = connection.talk(frame);
            if (answer != null) {
                discoverDeviceType(id, answer);
            }
//            byte[] answer = frame.talk(is, os, 1, 120);
    }

    private static void discoverDeviceType(int id, byte[] answer) {
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
