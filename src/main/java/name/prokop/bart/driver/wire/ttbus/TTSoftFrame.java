package name.prokop.bart.driver.wire.ttbus;

import name.prokop.bart.commons.bits.ByteBits;

/**
 *
 * @author Bartłomiej P. Prokop
 */
public final class TTSoftFrame {

    static final int BOF = 0xCA;
    private final TTSoftFrameType frameType;
    private byte id;
    private byte currTrId;
    private byte prevTrId;
    private final byte[] data;
    static final int EOF = 0xAC;
    private final int retryCount;
    private final int timeout;

    public TTSoftFrame(TTSoftFrameType frameType, byte[] data) {
        this(frameType, data, 3, 120);
    }

    public TTSoftFrame(TTSoftFrameType frameType, byte[] data, int retryCount, int timeout) {
        this.frameType = frameType;
        if (data == null) {
            data = new byte[0];
        }
        this.data = data;
        this.retryCount = retryCount;
        this.timeout = timeout;
    }

    public TTSoftFrameType getFrameType() {
        return frameType;
    }

    public byte getId() {
        return id;
    }

    public void setId(byte id) {
        this.id = id;
    }

    public void setId(int id) {
        this.id = ByteBits.narrow(id);
    }

    public byte getCurrTrId() {
        return currTrId;
    }

    public void setCurrTrId(byte currTrId) {
        this.currTrId = currTrId;
    }

    public void setCurrTrId(int currTrId) {
        this.currTrId = ByteBits.narrow(currTrId);
    }

    public byte getPrevTrId() {
        return prevTrId;
    }

    public void setPrevTrId(byte prevTrId) {
        this.prevTrId = prevTrId;
    }

    public void setPrevTrId(int prevTrId) {
        this.prevTrId = ByteBits.narrow(prevTrId);
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
