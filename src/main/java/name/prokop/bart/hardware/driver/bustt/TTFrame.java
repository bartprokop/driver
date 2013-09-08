package name.prokop.bart.hardware.driver.bustt;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.util.Arrays;
import name.prokop.bart.hardware.driver.common.BitsAndBytes;
import name.prokop.bart.hardware.driver.common.Fletcher16;
import name.prokop.bart.hardware.driver.common.Word16bit;

/**
 *
 * @author bart
 */
public class TTFrame {

    public static boolean dumpTalkTime = false;
    private static final int TIMEOUT = 350;
    private static final int BOF = 0xCA;
    private final TTFrameType frameType;
    private byte id;
    private byte currTrId;
    private byte prevTrId;
    private final byte[] data;
    private static final int EOF = 0xAC;

    public TTFrame(TTFrameType frameType, byte[] data) {
        this.frameType = frameType;
        if (data == null) {
            data = new byte[0];
        }
        this.data = data;
    }

    private void clearRxBuffer(InputStream is) throws IOException {
        while (is.available() > 0) {
            is.read();
        }
    }

    private void clearRxBuffer(DatagramSocket datagramSocket) throws IOException {
        while (true) {
            try {
                datagramSocket.setSoTimeout(1);
                byte[] receiveData = new byte[256];
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                datagramSocket.receive(receivePacket);
            } catch (IOException e) {
                return;
            }
        }
    }

    private void send(SocketAddress socketAddress, DatagramSocket datagramSocket) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        send(baos);
        byte[] byteArray = baos.toByteArray();
        DatagramPacket packet = new DatagramPacket(byteArray, byteArray.length, socketAddress);
        datagramSocket.send(packet);
    }

    private void send(OutputStream os) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        baos.write(frameType.getTypeByte());
        baos.write(id);
        baos.write(currTrId);
        baos.write(prevTrId);
        int len = 5 + 1 + data.length + 3 + 1;
        baos.write(len);
        baos.write(data);
        baos.write(calcXor(data));
        byte[] frame = baos.toByteArray();
        Word16bit fletcher16 = Fletcher16.fletcher16(frame);

        baos.reset();
        baos.write(BOF);
        baos.write(frame);
        baos.write(fletcher16.getHigh());
        baos.write(fletcher16.getLow());
        baos.write(EOF);
        os.write(baos.toByteArray());
        os.flush();
        baos.close();
    }

    private static byte[] receive(InputStream is, int timeout) throws IOException {
        // początek ramki
        while (timeout-- > 0) {
            if (is.available() > 0) {
                int bof = is.read();
                if (bof == BOF) {
                    break;
                } else {
                    throw new TTFrameException("timeout: no BOF recived (BOF != " + bof + " dec)");
                }
            }
            if (timeout == 0) {
                throw new TTFrameException("timeout: no BOF recived (nothing)");
            }
            sleep(1);
        }

        while (timeout-- > 0) {
            sleep(1);
            if (is.available() >= 5) {
                break;
            }
            if (timeout == 0) {
                while (is.available() > 0) {
                    is.read();
                }
                throw new TTFrameException("cannot read 5 header bytes. Avaiable: " + is.available());
            }
        }

        int frameType = is.read();
        int id = is.read();
        int currCnt = is.read();
        int prevCnt = is.read();
        int len = is.read();

        while (timeout-- > 0) {
            if (is.available() == len - 6) {
                break;
            }
            sleep(1);
            if (timeout == 0) {
                int avaiable = is.available();
                while (is.available() > 0) {
                    is.read();
                }
                throw new TTFrameException(frameType, id, currCnt, prevCnt, len, avaiable);
            }
        }

        byte[] data = new byte[len - 6 - 4];
        if (is.read(data) != len - 6 - 4) {
            throw new TTFrameException("cannot read data frame");
        }

        byte xor = BitsAndBytes.castIntToByte(is.read());
        if (xor != calcXor(data)) {
            throw new IOException("XOR problem");
        }

        // check fletcher
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        baos.write(frameType);
        baos.write(id);
        baos.write(currCnt);
        baos.write(prevCnt);
        baos.write(len);
        baos.write(data);
        baos.write(xor);
        Word16bit fletcher16 = Fletcher16.fletcher16(baos.toByteArray());
        if (fletcher16.getHighAsInt() != is.read() | fletcher16.getLowAsInt() != is.read()) {
            throw new TTFrameException("CRC Error");
        }
        if (is.read() != EOF) {
            throw new TTFrameException("Cannot get EOF");
        }
        if (is.available() > 0) {
            while (is.available() > 0) {
                is.read();
            }
            throw new TTFrameException("Extraordinary bytes after frame");
        }
        return data;
    }

    private static byte calcXor(byte[] data) {
        byte xor = 0x00;
        for (int i = 0; i < data.length; i++) {
            xor ^= data[i];
        }
        return xor;
    }

    byte[] talk(TTSoftBus bus) throws IOException {
        if (bus.getDatagramRemoteSocketAddress() != null && bus.getDatagramSocket() != null) {
            return talk(bus.getDatagramRemoteSocketAddress(), bus.getDatagramSocket(), 3, TIMEOUT);
        } else {
            return talk(bus.getInputStream(), bus.getOutputStream(), 3, TIMEOUT);
        }
    }

    byte[] talk(SocketAddress socketAddress, DatagramSocket datagramSocket, int retryCount, int timeout) throws IOException {
        IOException e = null;
        while (retryCount-- > 0) {
            try {
                sleep(15);
                clearRxBuffer(datagramSocket);
                long t = System.currentTimeMillis();
                datagramSocket.setSoTimeout(timeout);
                send(socketAddress, datagramSocket);
                byte[] receiveData = new byte[256];
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                datagramSocket.receive(receivePacket);
                byte[] receive = Arrays.copyOfRange(receivePacket.getData(), receivePacket.getOffset(), receivePacket.getOffset() + receivePacket.getLength());
                receive = receive(new ByteArrayInputStream(receive), 10);
                t = System.currentTimeMillis() - t;
                if (dumpTalkTime) {
                    System.err.println(socketAddress + " : " + t + " ms");
                }
                return receive;
            } catch (IOException ex) {
                e = ex;
            }
        }
        throw new IOException(e);
    }

    byte[] talk(InputStream is, OutputStream os, int retryCount, int timeout) throws IOException {
        IOException e = null;
        while (retryCount-- > 0) {
            try {
                sleep(15);
                clearRxBuffer(is);
                long t = System.currentTimeMillis();
                send(os);
                byte[] receive = receive(is, timeout);
                t = System.currentTimeMillis() - t;
                if (dumpTalkTime) {
                    System.err.println(is.getClass().getSimpleName() + " : " + t + " ms");
                }
                return receive;
            } catch (IOException ex) {
                e = ex;
            }
        }
        throw new IOException(e);
    }

    private static void sleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
        }
    }

    public TTFrameType getFrameType() {
        return frameType;
    }

    public byte getId() {
        return id;
    }

    public void setId(byte id) {
        this.id = id;
    }

    public void setId(int id) {
        this.id = BitsAndBytes.castIntToByte(id);
    }

    public byte getCurrTrId() {
        return currTrId;
    }

    public void setCurrTrId(byte currTrId) {
        this.currTrId = currTrId;
    }

    public void setCurrTrId(int currTrId) {
        this.currTrId = BitsAndBytes.castIntToByte(currTrId);
    }

    public byte getPrevTrId() {
        return prevTrId;
    }

    public void setPrevTrId(byte prevTrId) {
        this.prevTrId = prevTrId;
    }

    public void setPrevTrId(int prevTrId) {
        this.prevTrId = BitsAndBytes.castIntToByte(prevTrId);
    }
}
