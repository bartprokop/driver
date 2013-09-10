package name.prokop.bart.driver.wire.ttbus;

import java.io.IOException;
import java.net.Socket;

/**
 *
 * @author Bartłomiej Piotr Prokop
 */
public class TTSoftSocketConnection extends TTSoftIOStreamConnection {

    private Socket socket = null;

    public static TTSoftConnection getConnection(String hostName, int port) {
        return new TTSoftSocketConnection(hostName, port);
    }

    private TTSoftSocketConnection(String hostName, int port) {
        try {
            socket = new Socket(hostName, port);
        } catch (IOException e) {
        }
    }

    @Override
    public String getAddress() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public byte[] talk(TTFrame frame) {
        return null;
    }
//    private static void discoverBusDevices(String hostName, int port) throws Exception {
//        Socket socket = new Socket(hostName, port);
//        for (int id = 1; id < 255; id++) {
//            discoverDevice(id, socket.getInputStream(), socket.getOutputStream());
//        }
//        socket.close();
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

    @Override
    public void close() {
    }
}
