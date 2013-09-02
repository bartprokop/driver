/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package name.prokop.bart.hardware.driver.rfid.uhf28;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 *
 * @author Administrator
 */
public class Protocol {

    private byte[] frame;
    private byte addr = (byte) 0xFF;
    private long lastSend;
    private long lastRecv;
    private int framesSend = 0;
    private int framesRecv = 0;

    public byte[] getFrame() {
        return frame;
    }

    public void send(OutputStream os) throws IOException {
        os.write(frame);
        framesSend++;
        lastSend = System.currentTimeMillis();
    }

    public void flush(InputStream is) {
        try {
            while (is.available() > 0) {
                is.read();
                sleep1ms();
            }
        } catch (IOException e) {
        }
    }

    public void receive(InputStream is) throws IOException {
        sleep1ms();
        resetTimeouts();
        int len = 0;
        while (!isRecTimeout()) {
            if (is.available() == 0) {
                sleep1ms();
                continue;
            }
            len = readByte(is);
            break;
        }
        if (isRecTimeout()) {
            throw new IOException("Timeout 1");
        }
        if (len < 3) {
            throw new IOException("Illegal length");
        }
        frame = new byte[len + 1];
        frame[0] = (byte) len;
        for (int idx = 1; idx <= len; idx++) {
            while (is.available() == 0 && !isRecTimeout()) {
                sleep1ms();
            }
            if (isRecTimeout() || is.available() == 0) {
                throw new IOException("Timeout 2");
            }
            frame[idx] = (byte) readByte(is);
        }
        int crc = crc(frame);
//        System.out.println(ToString.byteArrayToString(frame));
        if (frame[len - 1] != lo(crc) || frame[len] != hi(crc)) {
            throw new IOException("Bad CRC");
        }
        framesRecv++;
//        System.out.println(System.currentTimeMillis() - lastSend);
//        System.out.println(framesSend + "/" + framesRecv);
        if (is.available() > 0) {
            throw new IOException("orphant bytes on wire");
        }
    }

    public void cmdGetReaderInformation() {
        frame = new byte[5];
        frame[0] = (byte) (frame.length - 1);
        frame[1] = addr;
        frame[2] = 0x21;
        int crc = crc(frame);
        frame[3] = lo(crc);
        frame[4] = hi(crc);
    }

    public void cmdInventory() {
        frame = new byte[7];
        frame[0] = (byte) (frame.length - 1);
        frame[1] = addr;
        frame[2] = 0x01; // inventory        
        frame[3] = 0x03; // TID from 3rd word
        frame[4] = 0x03; // TID - SN 3*8*2 = 48
        int crc = crc(frame);
        frame[5] = lo(crc);
        frame[6] = hi(crc);
    }

    public ReaderInformation getReaderInformation() {
        return new ReaderInformation(frame);
    }

    public Inventory getInventory() {
        return new Inventory(frame);
    }

    private int readByte(InputStream is) throws IOException {
        int b = is.read();
        lastRecv = System.currentTimeMillis();
        return b;
    }

    private void resetTimeouts() {
        lastRecv = System.currentTimeMillis();
    }

    private boolean isRecTimeout() {
        return System.currentTimeMillis() - lastRecv > 2000;
    }

    public static void sleep1ms() {
        try {
            Thread.sleep(1);
        } catch (InterruptedException ie) {
        }
    }

    public static void sleep(int time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException ie) {
        }
    }

    public static byte hi(int x) {
        return (byte) ((x >> 8) & 0xff);
    }

    public static byte lo(int x) {
        return (byte) (x & 0xff);
    }

    /**
     *
     * @param bytes
     * @return
     */
    public static int crc(byte[] pucY) {
        final int PRESET_VALUE = 0xFFFF;
        final int POLYNOMIAL = 0x8408;

        int ucX = pucY.length - 2;
        int uiCrcValue = PRESET_VALUE;

        for (int ucI = 0; ucI < ucX; ucI++) {
            uiCrcValue = (uiCrcValue ^ (pucY[ucI] & 0xFF));
            for (int ucJ = 0; ucJ < 8; ucJ++) {
                if ((uiCrcValue & 0x0001) != 0) {
                    uiCrcValue = (uiCrcValue >> 1) ^ POLYNOMIAL;
                } else {
                    uiCrcValue = (uiCrcValue >> 1);
                }
            }
        }

        uiCrcValue &= PRESET_VALUE;

        //System.out.println(Integer.toHexString(uiCrcValue));
        return uiCrcValue;
    }
}
