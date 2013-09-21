package name.prokop.bart.hardware.driver.rfid.ttd0001v01;

import name.prokop.bart.driver.wire.ttbus.TTSoftConnection;
import name.prokop.bart.driver.wire.ttbus.TTSoftFrame;
import name.prokop.bart.driver.wire.ttbus.TTSoftFrameType;
import name.prokop.bart.driver.wire.ttbus.TTSoftSerialConnection;
import name.prokop.bart.hardware.driver.common.ToString;

public class DeviceReset {

    public static void main(String... args) {
        TTSoftFrame frame = new TTSoftFrame(TTSoftFrameType.FrameConfiguration, new byte [] {(byte)0xBB});
        frame.setId(1);

        TTSoftConnection c = TTSoftSerialConnection.getConnection("COM1");
        byte[] talk = c.talk(frame);
        System.out.println(ToString.byteArrayToString(talk));
        c.close();
    }
}
