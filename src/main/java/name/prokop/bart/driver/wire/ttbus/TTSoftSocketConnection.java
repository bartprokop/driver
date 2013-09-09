/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package name.prokop.bart.driver.wire.ttbus;

import java.io.IOException;
import java.net.Socket;

/**
 *
 * @author rr163240
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
    public byte[] talk(TTFrame frame) {
        return null;
    }

    @Override
    public void close() {
    }
}
