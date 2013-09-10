package name.prokop.bart.driver.wire.ttbus;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import name.prokop.bart.hardware.driver.common.BitsAndBytes;
import name.prokop.bart.hardware.driver.common.Fletcher16;
import name.prokop.bart.hardware.driver.common.Word16bit;

public abstract class TTSoftIOStreamConnection implements TTSoftConnection {

    byte[] talk(InputStream is, OutputStream os, TTFrame frame) throws IOException {
        IOException e = null;
        int retryCount = frame.getRetryCount();
        while (retryCount-- > 0) {
            try {
                sleep(15);
                clearRxBuffer(is);
//                long t = System.currentTimeMillis();
                send(os, frame);
                byte[] receive = receive(is, frame.getTimeout());
//                t = System.currentTimeMillis() - t;
//                if (dumpTalkTime) {
//                    System.err.println(is.getClass().getSimpleName() + " : " + t + " ms");
//                }
                return receive;
            } catch (IOException ex) {
                e = ex;
            }
        }
        throw e;
    }

    private void clearRxBuffer(InputStream is) throws IOException {
        while (is.available() > 0) {
            is.read();
        }
    }

    private void send(OutputStream os, TTFrame frame) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        baos.write(frame.getFrameType().getTypeByte());
        baos.write(frame.getId());
        baos.write(frame.getCurrTrId());
        baos.write(frame.getPrevTrId());
        int len = 5 + 1 + frame.getData().length + 3 + 1;
        baos.write(len);
        baos.write(frame.getData());
        baos.write(calcXor(frame.getData()));
        byte[] bytes = baos.toByteArray();
        Word16bit fletcher16 = Fletcher16.fletcher16(bytes);

        baos.reset();
        baos.write(TTFrame.BOF);
        baos.write(bytes);
        baos.write(fletcher16.getHigh());
        baos.write(fletcher16.getLow());
        baos.write(TTFrame.EOF);
        os.write(baos.toByteArray());
        os.flush();
        baos.close();
    }

    private static byte[] receive(InputStream is, int timeout) throws IOException {
        // początek ramki
        while (timeout-- > 0) {
            if (is.available() > 0) {
                int bof = is.read();
                if (bof == TTFrame.BOF) {
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
        if (is.read() != TTFrame.EOF) {
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

    private static void sleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
        }
    }
}
