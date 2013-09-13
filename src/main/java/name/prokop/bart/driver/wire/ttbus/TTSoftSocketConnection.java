package name.prokop.bart.driver.wire.ttbus;

import java.io.IOException;
import java.net.Socket;

/**
 *
 * @author Bartłomiej Piotr Prokop
 */
public class TTSoftSocketConnection extends TTSoftIOStreamConnection {

    private Socket socket = null;
    private String hostName;
    private int port;

    public static void main(String... args) throws Exception {
        TTSoftConnection connection = getConnection("192.168.146.175", 4001);
        try {
            TTSoftBus.discoverDevices(connection);
        } finally {
            connection.close();
        }
    }

    public static TTSoftConnection getConnection(String hostName, int port) {
        return new TTSoftSocketConnection(hostName, port);
    }

    private TTSoftSocketConnection(String hostName, int port) {
        this.hostName = hostName;
        this.port = port;
    }

    @Override
    public String getAddress() {
        return hostName + ":" + port;
    }

    @Override
    public byte[] talk(TTSoftFrame frame) {
        try {
            if (socket == null) {
                socket = new Socket(hostName, port);
            }
            return talk(socket.getInputStream(), socket.getOutputStream(), frame);
        } catch (IOException e) {
            close();
        }
        try {
            socket = new Socket(hostName, port);
            return talk(socket.getInputStream(), socket.getOutputStream(), frame);
        } catch (IOException e) {
            close();
            return null;
        }
    }

    @Override
    public void close() {
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
            } finally {
                socket = null;
            }
        }
    }
}
