package name.prokop.bart.driver.wire.ttbus;

import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import name.prokop.bart.hardware.driver.common.PortEnumerator;

/**
 *
 * @author rr163240
 */
public class TTSoftSerialConnection extends TTSoftIOStreamConnection {

    private final SerialPort serialPort;
    private final InputStream inputStream;
    private final OutputStream outputStream;

    public static void main(String... args) throws Exception {
        TTSoftConnection connection = getConnection("COM1");
        try {
            TTSoftBus.discoverDevices(connection);
        } finally {
            connection.close();
        }
    }

    public static TTSoftConnection getConnection(String portName) {
        return new TTSoftSerialConnection(portName);
    }

    private TTSoftSerialConnection(String portName) {
        try {
            serialPort = PortEnumerator.getSerialPort(portName);
            serialPort.setSerialPortParams(9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_EVEN);
            serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);
            inputStream = serialPort.getInputStream();
            outputStream = serialPort.getOutputStream();
        } catch (IOException | UnsupportedCommOperationException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getAddress() {
        return serialPort.getName();
    }

    @Override
    public byte[] talk(TTSoftFrame frame) {
        try {
            return talk(inputStream, outputStream, frame);
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    public void close() {
        serialPort.close();
    }
}
