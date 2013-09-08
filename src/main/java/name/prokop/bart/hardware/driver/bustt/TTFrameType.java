package name.prokop.bart.hardware.driver.bustt;

public enum TTFrameType {

    Frame1((byte) 0x01),
    FramePlugAndPlay((byte) 0x03),
    Frame4((byte) 0x04),
    Frame5((byte) 0x05),
    Frame6((byte) 0x06);
    private byte typeByte;

    private TTFrameType(byte typeByte) {
        this.typeByte = typeByte;
    }

    public byte getTypeByte() {
        return typeByte;
    }

    public static TTFrameType fromInt(int type) {
        switch (type) {
            case 1:
                return Frame1;
            default:
                throw new IllegalArgumentException("Unknown frame type");
        }
    }
}
