package name.prokop.bart.driver.wire.ttbus;

import java.util.ArrayList;
import java.util.List;
import name.prokop.bart.hardware.driver.common.BitsAndBytes;
import name.prokop.bart.hardware.driver.rfid.ttd0001v01.TTDevice0001v01;

/**
 *
 * @author bart
 */
public class TTSoftBus implements Runnable {

    private List<TTSoftDevice> devices = new ArrayList<>();
    private boolean pleaseTerminate = false;
    private boolean terminated = false;
    private final TTSoftConnection connection;

    public static void main(String... args) throws Exception {
//        TTSoftConnection c = TTSoftSerialConnection.getConnection("COM1");
        TTSoftConnection c = TTSoftSocketConnection.getConnection("192.168.146.175", 4001);
        TTSoftBus ttSoftBus = new TTSoftBus(c);
        TTDevice0001v01 ttDevice0001v01 = new TTDevice0001v01(1, c);
        ttSoftBus.devices.add(ttDevice0001v01);
        new Thread(ttSoftBus).start();
        Thread.sleep(2000);
        ttDevice0001v01.openRelay(60);
    }

    private TTSoftBus(TTSoftConnection connection) {
        this.connection = connection;
    }

    public void terminate() {
        pleaseTerminate = true;
    }

    public boolean isTerminated() {
        return terminated;
    }

    public static void discoverDevices(TTSoftConnection connection) throws Exception {
        for (int id = 1; id < 255; id++) {
            System.out.print(id + " ");
            if (id % 20 == 0) {
                System.out.println();
            }
            TTSoftFrame frame = new TTSoftFrame(TTSoftFrameType.FramePlugAndPlay, null, 1, 120);
            frame.setId(id);
            byte[] answer = connection.talk(frame);
            if (answer != null) {
                discoverDeviceType(id, answer);
            }
        }
        System.out.println();
    }

    private static void discoverDeviceType(int id, byte[] answer) {
        String type = new String(BitsAndBytes.subArray(answer, 0, 4));
        String ver = new String(BitsAndBytes.subArray(answer, 4, 6));
        String sn = new String(BitsAndBytes.subArray(answer, 6, 12));
//        System.err.println("Encrypted frames: " + (BitsAndBytes.subArray(answer, 12, 13)[0] != 48));
        if (type.equals("0001")) {
            if (ver.equals("01")) {
//                e = (Element) busElement.appendChild(Configuration.getInstance().createElement("TTD0001v01"));
                type += " (MIFARE Reader)";
            }
            if (ver.equals("03")) {
//                e = (Element) busElement.appendChild(Configuration.getInstance().createElement("TTD0001v03"));
                type += " (MIFARE Bike Reader)";
            }
        }

        if (type.equals("0005")) {
//            e = (Element) busElement.appendChild(Configuration.getInstance().createElement("TTD0005"));
            type += " (LCD mono display)";
        }

        System.out.println("\nDetected @ " + id + " : type: " + type + " v. " + ver + " s/n " + sn);
    }

    public void run() {
        while (!pleaseTerminate) {
            for (TTSoftDevice device : devices) {
                try {
                    device.takeControl();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
            }
        }
        for (TTSoftDevice device : devices) {
//            BPDriverBusHolder.getInstance().postEvent(DeviceDropEvent.generate(device));
        }

        connection.close();
        terminated = true;
    }
}
