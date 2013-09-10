package name.prokop.bart.driver.wire.ttbus;

import name.prokop.bart.hardware.driver.common.BitsAndBytes;

/**
 *
 * @author Bartłomiej P. Prokop
 */
public final class TTFrame {

    static final int BOF = 0xCA;
    private final TTFrameType frameType;
    private byte id;
    private byte currTrId;
    private byte prevTrId;
    private final byte[] data;
    static final int EOF = 0xAC;
    private final int retryCount;
    private final int timeout;

    public TTFrame(TTFrameType frameType, byte[] data) {
        this(frameType, data, 3, 120);
    }

    public TTFrame(TTFrameType frameType, byte[] data, int retryCount, int timeout) {
        this.frameType = frameType;
        if (data == null) {
            data = new byte[0];
        }
        this.data = data;
        this.retryCount = retryCount;
        this.timeout = timeout;
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

    public byte[] getData() {
        return data;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public int getTimeout() {
        return timeout;
    }
}
