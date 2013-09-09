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
public class TTSoftDatagramConnection implements TTSoftConnection {

    public static TTSoftConnection getConnection(String hostName, int port) {
        return new TTSoftDatagramConnection(hostName, port);
    }

    private TTSoftDatagramConnection(String hostName, int port) {
    }

    @Override
    public byte[] talk(TTFrame frame) {
        return null;
    }

    @Override
    public void close() {
    }
}
