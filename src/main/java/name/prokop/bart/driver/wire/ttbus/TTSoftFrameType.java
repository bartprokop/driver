package name.prokop.bart.driver.wire.ttbus;

public enum TTSoftFrameType {

    Frame1((byte) 0x01),
    FrameConfiguration((byte) 0x02),
    FramePlugAndPlay((byte) 0x03),
    Frame4((byte) 0x04),
    Frame5((byte) 0x05),
    Frame6((byte) 0x06);
    private byte typeByte;

    private TTSoftFrameType(byte typeByte) {
        this.typeByte = typeByte;
    }

    public byte getTypeByte() {
        return typeByte;
    }

    public static TTSoftFrameType fromInt(int type) {
        switch (type) {
            case 1:
                return Frame1;
            default:
                throw new IllegalArgumentException("Unknown frame type");
        }
    }
}
