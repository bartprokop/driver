package name.prokop.bart.driver.wire.ttbus;

import java.io.IOException;
import java.net.Socket;

/**
 *
 * @author Bartłomiej Piotr Prokop
 */
public class TTSoftSocketConnection extends TTSoftIOStreamConnection {

    private Socket socket = null;

    public static void main(String... args) throws Exception {
        TTSoftConnection connection = getConnection("xxx", 4000);
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
        try {
            socket = new Socket(hostName, port);
        } catch (IOException e) {
        }
    }

    @Override
    public String getAddress() {
        return socket.getInetAddress().getHostName();
    }

    @Override
    public byte[] talk(TTSoftFrame frame) {
        return null;
    }

    @Override
    public void close() {
    }
}
