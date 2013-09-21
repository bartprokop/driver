package name.prokop.bart.driver.gigatek;

import gnu.io.SerialPort;
import java.io.InputStream;
import java.io.OutputStream;
import name.prokop.bart.hardware.driver.common.PortEnumerator;

public class UR220 {
    
    public static void main(String... args) throws Exception {
        SerialPort serialPort = PortEnumerator.getSerialPort("COM1");
        serialPort.setSerialPortParams(19200, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
        serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);
        
        InputStream inputStream = serialPort.getInputStream();
        OutputStream outputStream = serialPort.getOutputStream();
        
        while (true) {
            if (inputStream.available() > 0) {
                int read = inputStream.read();
                System.out.println(read + " " + (char) read + " " + Integer.toHexString(read));
            }
        }

//        serialPort.close();
    }
}
