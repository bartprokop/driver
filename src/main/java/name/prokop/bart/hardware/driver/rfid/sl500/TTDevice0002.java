/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package name.prokop.bart.hardware.driver.rfid.sl500;

import gnu.io.SerialPort;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Logger;
import name.prokop.bart.hardware.comm.PortEnumerator;
import name.prokop.bart.hardware.driver.Device;
import name.prokop.bart.hardware.driver.Driver;
import name.prokop.bart.hardware.driver.DeviceDetectedEvent;
import name.prokop.bart.hardware.driver.DeviceDropEvent;
import name.prokop.bart.hardware.driver.rfid.MifareException;
import name.prokop.bart.hardware.driver.rfid.MifareReader;
import name.prokop.bart.hardware.driver.rfid.RFIDCardType;
import name.prokop.bart.hardware.driver.rfid.ReadBlockAbility;
import name.prokop.bart.util.ToString;
import name.prokop.bart.util.lang.BartException;
import name.prokop.bart.util.lang.BitsAndBytes;
import name.prokop.bart.util.security.unicard.Ultralight;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author bart
 */
public class TTDevice0002 implements Device, MifareReader, ReadBlockAbility {

    @Autowired
    private Driver driver;
    private String readerModel = "....";
    private final Object semaphore = new Object();
    private boolean needOvertakeControl = false;
    private boolean overtakeControlGranted = false;
    public static final String CONFIG_KEY = TTDevice0002.class.getSimpleName();
    private static final Logger logger = Logger.getLogger(TTDevice0002.class.getName());
    private String busName;
    private int deviceNumber;
    private FiniteState finiteState;
    private SerialPort serialPort;
    private MifareCardType currentCardType = null;
    private byte[] currentCardSerialNumber = null;
    private static byte[] SEC_KEY = new byte[]{(byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff};

    public int compareTo(Device o) {
        return getDeviceAddress().compareTo(o.getDeviceAddress());
    }

    public String getBusInfo() {
        return serialPort.getName();
    }

    public String getDeviceDescription() {
        return "TTD-0002 Czytnik desktop MIFARE";
    }

    public String getDeviceInfo() {
        return getDeviceAddress() + " / " + serialPort.getName() + " / " + readerModel;
    }

    public void readBlock(int blockid) {
        byte[] returval = new byte[0];
        try {
            returval = readBlock(ClassicKeyType.KeyA, getSecurityKey(), blockid);
            driver.postEvent(new TTDevice0002ReadBlock(this, getCurrentRFIDCardType(), currentCardSerialNumber, returval));
        } catch (MifareException ex) {
            driver.postEvent(new TTDevice0002ReadBlock(this, getCurrentRFIDCardType(), currentCardSerialNumber, returval));
            ex.printStackTrace();
        }
    }

    public RFIDCardType getCurrentRFIDCardType() {
        switch (currentCardType) {
            case Classic1k:
                return RFIDCardType.Mifare1K;
            case Classic4k:
                return RFIDCardType.Mifare4K;
            case Ultralight:
                return RFIDCardType.MifareUltralight;
            default:
                throw new IllegalThreadStateException();
        }
    }

    public void writeBlock(int blockid, byte[] data) {
        try {
            writeBlock(ClassicKeyType.KeyA, getSecurityKey(), blockid, data);
        } catch (MifareException ex) {
            ex.printStackTrace();
        }
    }

    public void eraseBlock(int blockid) {
        byte[] trailer = new byte[0];
        try {
            trailer = readBlock(ClassicKeyType.KeyA, SEC_KEY, blockid);
            for (int a = 0; a < trailer.length; a++) {
                trailer[a] = 0;
            }
            writeBlock(ClassicKeyType.KeyA, getSecurityKey(), blockid, trailer);
            driver.postEvent(new TTDevice0002BlockErased(this, getCurrentRFIDCardType(), currentCardSerialNumber, trailer));
        } catch (MifareException ex) {
            driver.postEvent(new TTDevice0002BlockErased(this, getCurrentRFIDCardType(), currentCardSerialNumber, trailer));
            ex.printStackTrace();
        }
    }

    public byte[] getSecurityKey() {
        return SEC_KEY;
    }

    public void setDeviceAddress(String deviceAddress) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private enum MifareCardType {

        Ultralight,
        Classic1k,
        Classic4k,
        DESFire,
        Pro,
        ProX,
        SHC1102;
    }

    private enum FiniteState {

        Idle,
        RequestOnUltralight,
        RequestOnClassic,
        RequestOnDESFire,
        RequestOnPro,
        RequestOnProX,
        RequestOnSHC1102,
        CardSelectedClassic,
        CardSelectedUltralight;
    }

    public TTDevice0002(String portName) throws BartException {
        try {
            initializeSerialPort(portName);
        } catch (Exception e) {
            throw new BartException("Nie moge zainicjowac portu szeregowego: " + portName, e);
        }
    }

    private void fsmLoop() throws IOException {
        backToIdle();

        while (true) {
            sleep(10);
            if (pleaseTerminate) {
                return;
            }

            if (beepTime != 0) {
                rfBeep(beepTime);
                beepTime = 0;
            }

            switch (finiteState) {
                case Idle:
                    idleBlink();
                    idleRequest();
                    break;
                case RequestOnUltralight:
                    selectUltralight();
                    break;
                case RequestOnClassic:
                    selectClassic();
                    break;
                case RequestOnDESFire:
                    break;
                case RequestOnPro:
                case RequestOnProX:
                    break;
                case RequestOnSHC1102:
                    break;
                case CardSelectedClassic:
                    if (needOvertakeControl) {
                        overtakeControlGranted = true;
                        while (overtakeControlGranted) {
                            sleep(10);
                        }
                    }
                    checkPresenceClassic();
                    break;
                case CardSelectedUltralight:
                    checkPresenceUltralight();
                    break;
            }
        }
    }

    private void initReader() throws IOException {
        readerModel = rfGetModel();
        System.out.println("Detected model: " + readerModel);
        rfBeep((byte) 4);
        rfInitType(TYPE_A);
        deviceNumber = rfGetDeviceNumber();
        /*
         * if (deviceNumber != 19717) {//0x4d05 - forma zabezpieczenia czytników
         * desktop pleaseTerminate = true; }
         */
        rfAntennaSta(ANTENNA_ON);
        driver.postEvent(new DeviceDetectedEvent(this));
    }

    private void shutdownReader() throws IOException {
        driver.postEvent(new DeviceDropEvent(this));
        rfBeep((byte) 4);
        rfAntennaSta(ANTENNA_OFF);
        rfLight(LED_RED);
    }

    private void idleBlink() {
        byte color = LED_GREEN;
        if (System.currentTimeMillis() % 500 < 250) {
            color = LED_NONE;
        }
        try {
            rfLight(color);
        } catch (IOException e) {
        }
    }

    private void idleRequest() throws IOException {
        try {
            int cardType = rfRequest(REQ_STD);
            switch (cardType) {
                case CARD_TYPE_Ultra_light:
                    finiteState = FiniteState.RequestOnUltralight;
                    currentCardType = MifareCardType.Ultralight;
                    break;
                case CARD_TYPE_Mifare_1k:
                    finiteState = FiniteState.RequestOnClassic;
                    currentCardType = MifareCardType.Classic1k;
                    break;
                case CARD_TYPE_Mifare_4k:
                    finiteState = FiniteState.RequestOnClassic;
                    currentCardType = MifareCardType.Classic4k;
                    break;
                case CARD_TYPE_Mifare_DESFire:
                    finiteState = FiniteState.RequestOnDESFire;
                    currentCardType = MifareCardType.DESFire;
                    break;
                case CARD_TYPE_Mifare_Pro:
                    finiteState = FiniteState.RequestOnPro;
                    currentCardType = MifareCardType.Pro;
                    break;
                case CARD_TYPE_Mifare_ProX:
                    finiteState = FiniteState.RequestOnProX;
                    currentCardType = MifareCardType.ProX;
                    break;
                case CARD_TYPE_SHC1102:
                    finiteState = FiniteState.RequestOnSHC1102;
                    currentCardType = MifareCardType.SHC1102;
                    break;
                default:
                    finiteState = FiniteState.Idle;
                    break;
            }
        } catch (StatusException se) {
            finiteState = FiniteState.Idle;
        }
    }

    private void backToIdle() {
        finiteState = FiniteState.Idle;
        currentCardSerialNumber = null;
        currentCardType = null;
    }

    private void selectClassic() throws IOException {
        try {
            currentCardSerialNumber = rfAnticoll();
            rfSelect(currentCardSerialNumber);
            rfLight(LED_RED);
            rfHalt();
            finiteState = FiniteState.CardSelectedClassic;
            if (currentCardType == MifareCardType.Classic1k) {
                driver.postEvent(new TTDevice0002CardDetected(this, RFIDCardType.Mifare1K, currentCardSerialNumber));
            }
            if (currentCardType == MifareCardType.Classic4k) {
                driver.postEvent(new TTDevice0002CardDetected(this, RFIDCardType.Mifare4K, currentCardSerialNumber));
            }
            //rfBeep((byte)1);
        } catch (StatusException se) {
            backToIdle();
        }
    }

    private void selectUltralight() throws IOException {
        try {
            currentCardSerialNumber = rfUlSelect();
            rfLight(LED_RED);
            rfHalt();
            finiteState = FiniteState.CardSelectedUltralight;
            driver.postEvent(new TTDevice0002CardDetected(this, RFIDCardType.MifareUltralight, currentCardSerialNumber));
            //rfBeep((byte)1);
        } catch (StatusException se) {
            backToIdle();
        }
    }

    private void checkPresenceClassic() throws IOException {
        try {
            rfRequest(REQ_ALL);
            rfAnticoll();
            rfSelect(currentCardSerialNumber);
            rfHalt();
        } catch (StatusException se) {
            if (currentCardType == MifareCardType.Classic1k) {
                driver.postEvent(new TTDevice0002CardRemoved(this, RFIDCardType.Mifare1K, currentCardSerialNumber));
            }
            if (currentCardType == MifareCardType.Classic4k) {
                driver.postEvent(new TTDevice0002CardRemoved(this, RFIDCardType.Mifare4K, currentCardSerialNumber));
            }
            backToIdle();
        }
    }

    private void checkPresenceUltralight() throws IOException {
        try {
            rfRequest(REQ_ALL);
            rfUlSelect();

            // VIVO
            Ultralight u = new Ultralight(currentCardSerialNumber);
            byte[] data = new byte[4];

            /*
             * BitsAndBytes.fill(data, u.encryptPage(0, (byte) 4));
             * rfUlWrite((byte) 4, data); BitsAndBytes.fill(data,
             * u.encryptPage(0, (byte) 5)); rfUlWrite((byte) 5, data);
             * BitsAndBytes.fill(data, u.encryptPage(0, (byte) 6));
             * rfUlWrite((byte) 6, data); BitsAndBytes.fill(data,
             * u.encryptPage(0, (byte) 7)); rfUlWrite((byte) 7, data);
             * BitsAndBytes.fill(data, u.encryptPage(0, (byte) 8));
             * rfUlWrite((byte) 8, data); BitsAndBytes.fill(data,
             * u.encryptPage(0, (byte) 9)); rfUlWrite((byte) 9, data);
             * BitsAndBytes.fill(data, u.encryptPage(0, (byte) 10));
             * rfUlWrite((byte) 10, data); BitsAndBytes.fill(data,
             * u.encryptPage(0, (byte) 11)); rfUlWrite((byte) 11, data);
             * BitsAndBytes.fill(data, u.encryptPage(0, (byte) 12));
             * rfUlWrite((byte) 12, data); BitsAndBytes.fill(data,
             * u.encryptPage(0, (byte) 13)); rfUlWrite((byte) 13, data);
             * BitsAndBytes.fill(data, u.encryptPage(0, (byte) 14));
             * rfUlWrite((byte) 14, data); BitsAndBytes.fill(data,
             * u.encryptPage(Ultralight.SECTOR15, (byte) 15)); rfUlWrite((byte)
             * 15, data);
             */

            rfHalt();
        } catch (StatusException se) {
            //se.printStackTrace();
            driver.postEvent(new TTDevice0002CardRemoved(this, RFIDCardType.MifareUltralight, currentCardSerialNumber));
            backToIdle();
        }
        //System.exit(0);
    }
    ////////////////////////////////////////////////////////////////////////////
    ///////////// Device functions /////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////

    /**
     * Read the reader model and product number Command code: 0x0401 Parameter:
     * 2 bytes device ID Respond data: reader model Example: Host send: AA BB 05
     * 00 00 00 04 01 05 SL500 return: AA BB 11 00 11 12 04 01 00 53 4C 35 30 30
     * 4C 2D 30 36 30 38 43
     *
     * @return
     * @throws IOException
     */
    private String rfGetModel() throws IOException {
        return new String(talk(serialPort, 0x0401, null));
    }

    /**
     * Read Equipment ID Command code: 0x0301
     *
     * @return
     * @throws IOException
     */
    private int rfGetDeviceNumber() throws IOException {
        byte[] dId = talk(serialPort, 0x0301, null);
        return BitsAndBytes.buildInt((byte) 0, (byte) 0, dId[0], dId[1]);
    }

    /**
     * Set buzz Command code: 0x0601 Parameter: 1byte buzz time, unit 10MS
     * Respond data: none
     *
     * @param param
     * @throws IOException
     */
    private void rfBeep(byte param) throws IOException {
        talk(serialPort, 0x0601, new byte[]{param});
    }
    private static final byte LED_NONE = 0x00;
    private static final byte LED_RED = 0x01;
    private static final byte LED_GREEN = 0x02;
    private static final byte LED_YELLOW = 0x03;

    /**
     * Sets LED color Command code: 0x0701 Parameter: 00 = off 01 = red 02 =
     * green 03 = yellow Respond data: none
     *
     * @param param
     * @throws IOException
     */
    private void rfLight(byte param) throws IOException {
        talk(serialPort, 0x0701, new byte[]{param});
    }
    private static final byte TYPE_A = 'A';
    private static final byte TYPE_B = 'B';
    private static final byte AT88RF020 = 'r';
    private static final byte ISO15693 = '1';

    /**
     * Command code: 0x0801 Parameter: 1 byte 'A': set TYPE_A mode 'B': set
     * TYPE_B mode '1': set ISO15693 mode Respond data: none
     *
     * @param param
     * @throws IOException
     */
    private void rfInitType(byte param) throws IOException {
        talk(serialPort, 0x0801, new byte[]{param});
    }
    private static final byte ANTENNA_OFF = 0x00;
    private static final byte ANTENNA_ON = 0x01;

    /**
     * Command code: 0x0C01
     *
     * @param param
     * @throws IOException
     */
    private void rfAntennaSta(byte param) throws IOException {
        talk(serialPort, 0x0C01, new byte[]{param});
    }
    private static final byte REQ_STD = 0x26;
    private static final byte REQ_ALL = 0x52;
    private static final int CARD_TYPE_Ultra_light = 0x4400;
    private static final int CARD_TYPE_Mifare_1k = 0x0400;
    private static final int CARD_TYPE_Mifare_4k = 0x0200;
    private static final int CARD_TYPE_Mifare_DESFire = 0x4403;
    private static final int CARD_TYPE_Mifare_Pro = 0x0800;
    private static final int CARD_TYPE_Mifare_ProX = 0x0403;
    private static final int CARD_TYPE_SHC1102 = 0x0033;

    /**
     * Function rfRequest: ReqA Command code: 0x0102 Parameter: 0x26 = REQ_STD
     * 0x52 = REQ_ALL Respond date: 2 bytes card type code
     *
     * @param mode
     * @return card type: 0x4400 = Ultra_light 0x0400 = Mifare_1k(S50) 0x0200 =
     * Mifare_4k(S70) 0x4403 = Mifare_DESFire 0x0800 = Mifare_Pro 0x0403 =
     * Mifare_ProX 0x0033 = SHC1102
     *
     * @throws IOException
     */
    private int rfRequest(byte mode) throws IOException {
        byte[] talk = talk(serialPort, 0x0102, new byte[]{mode});
        return BitsAndBytes.promoteByteToInt(talk[0]) << 8 + BitsAndBytes.promoteByteToInt(talk[1]);
    }

    /**
     * Anticollision Command code: 0x0202
     *
     * @return 4 bytes card serial number
     * @throws IOException
     */
    private byte[] rfAnticoll() throws IOException {
        byte[] id = talk(serialPort, 0x0202, null);
        if (id[0] == id[1] && id[1] == id[2] && id[2] == id[3] && id[3] == 0x00) {
            throw new StatusException("All bytes of serial number == 0");
        }
        return id;
    }

    /**
     * Select card Command code: 0x0302
     *
     * @param serialNumber 4 bytes card serial number
     * @return one byte card capacity code
     * @throws IOException
     */
    private byte rfSelect(byte[] serialNumber) throws IOException {
        return talk(serialPort, 0x0302, serialNumber)[0];
    }

    /**
     * Halt Command code: 0x0402
     *
     * @throws IOException
     */
    private void rfHalt() throws IOException {
        talk(serialPort, 0x0402, null);
    }
    private static byte KEY_TYPE_A = 0x60;
    private static byte KEY_TYPE_B = 0x61;

    /**
     * validate Mifare card key Command code: 0x0702
     *
     * @param type 1BYTE code validate mode(MODEL)
     * @param blockAddress 1BYTE absolute block number
     * @param key 6 bytes key
     * @throws IOException
     */
    private void rfM1Authentication2(byte type, byte blockAddress, byte[] key) throws IOException {
        byte[] frame = new byte[8];
        frame[0] = type;
        frame[1] = blockAddress;
        frame[2] = key[0];
        frame[3] = key[1];
        frame[4] = key[2];
        frame[5] = key[3];
        frame[6] = key[4];
        frame[7] = key[5];
        talk(serialPort, 0x0702, frame);
    }

    /**
     * Read block Command code: 0x0802
     *
     * @param blockAddress 1 byte absolute block address
     * @return 16 bytes of data
     * @throws IOException
     */
    private byte[] rfM1Read(byte blockAddress) throws IOException {
        return talk(serialPort, 0x0802, new byte[]{blockAddress});
    }

    /**
     * Write block Command code: 0x0902
     *
     * @param blockAddress 1 byte absolute block address
     * @param data 16 bytes of data to be written
     * @throws IOException
     */
    private void rfM1Write(byte blockAddress, byte[] data) throws IOException {
        byte[] frame = new byte[17];
        frame[0] = blockAddress;
        System.arraycopy(data, 0, frame, 1, 16);
        talk(serialPort, 0x0902, frame);
    }

    /**
     * initialize purse Command code: 0x0A02
     *
     * @param blockAddress 1 byte absolute block address
     * @param value 4 bytes initial value(low bytes in the former)
     * @throws IOException
     */
    private void rfM1Initval(byte blockAddress, int value) throws IOException {
        byte[] frame = new byte[5];
        frame[0] = blockAddress;
        frame[1] = BitsAndBytes.extractByte(value, 0);
        frame[2] = BitsAndBytes.extractByte(value, 1);
        frame[3] = BitsAndBytes.extractByte(value, 2);
        frame[4] = BitsAndBytes.extractByte(value, 3);
        talk(serialPort, 0x0A02, frame);
    }

    /**
     * decrement Command code: 0x0C02
     *
     * @param blockAddress 1 byte absolute block address
     * @param value 4 bytes decrement value (low bytes in the former)
     * @throws IOException
     */
    private void rfM1Decrement(byte blockAddress, int value) throws IOException {
        byte[] frame = new byte[5];
        frame[0] = blockAddress;
        frame[1] = BitsAndBytes.extractByte(value, 0);
        frame[2] = BitsAndBytes.extractByte(value, 1);
        frame[3] = BitsAndBytes.extractByte(value, 2);
        frame[4] = BitsAndBytes.extractByte(value, 3);
        talk(serialPort, 0x0C02, frame);
    }

    /**
     * Increament Command code: 0x0D02
     *
     * @param blockAddress 1 byte absolute block address
     * @param value 4 bytes increase value
     * @throws IOException
     */
    private void rfM1Increment(byte blockAddress, int value) throws IOException {
        byte[] frame = new byte[5];
        frame[0] = blockAddress;
        frame[1] = BitsAndBytes.extractByte(value, 0);
        frame[2] = BitsAndBytes.extractByte(value, 1);
        frame[3] = BitsAndBytes.extractByte(value, 2);
        frame[4] = BitsAndBytes.extractByte(value, 3);
        talk(serialPort, 0x0D02, frame);
    }

    /**
     * Ultralight card Anticoll and Select Command code: 0x1202
     *
     * @return 7 bytes ultralight UID
     * @throws IOException
     */
    private byte[] rfUlSelect() throws IOException {
        byte[] id = talk(serialPort, 0x1202, null);
        // TODO: check if similar problem what with Classics occurs.
        //if (id[0] == id[1] && id[1] == id[2] && id[2] == id[3] && id[3] == 0x00) {
        //    throw new StatusException("All bytes of serial number == 0");
        //}
        return id;
    }

    /**
     * Write a page of data into ultra light card Command code: 0x1302
     *
     * @param pageAddress 1 byte page address
     * @param data 4 bytes written data
     * @throws IOException
     */
    private void rfUlWrite(byte pageAddress, byte[] data) throws IOException {
        byte[] frame = new byte[5];
        frame[0] = pageAddress;
        frame[1] = data[0];
        frame[2] = data[1];
        frame[3] = data[2];
        frame[4] = data[3];
        talk(serialPort, 0x1302, frame);
    }

    ////////////////////////////////////////////////////////////////////////////
    ///////////// Serial port communication functions //////////////////////////
    ////////////////////////////////////////////////////////////////////////////
    private static class StatusException extends IOException {

        public StatusException(String message) {
            super(message);
            //System.err.println(message);
        }
    }

    private void initializeSerialPort(String portName) throws Exception {
        serialPort = PortEnumerator.getSerialPort(portName);
        serialPort.setSerialPortParams(19200, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
        serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);
    }

    private static void sleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
        }
    }

    private static void waitForData(InputStream is, long timeout, int minBytes) throws IOException {
        if (is.available() >= minBytes) {
            return;
        }
        while (timeout-- > 0) {
            sleep(1);
            if (is.available() >= minBytes) {
                return;
            }
        }
        throw new IOException("Timeout");
    }

    private static void send(SerialPort serialPort, byte[] frame) throws IOException {
        OutputStream outputStream = serialPort.getOutputStream();
        outputStream.write(0xaa);
        outputStream.write(0xbb);
        outputStream.write(frame.length + 2 + 1);
        outputStream.write(0x00); // length
        outputStream.write(0x00); // id
        outputStream.write(0x00); // id
        for (byte b : frame) {
            int i = BitsAndBytes.promoteByteToInt(b);
            outputStream.write(i);
            if (i == 0xaa) {
                outputStream.write(0x00);
            }
        }
        outputStream.write(calcCrc(frame)); // id
    }

    private static byte read(InputStream is) throws IOException {
        int b = is.read();
        if (b == -1) {
            throw new IOException("EOS");
        }
        if (b == 0xAA && is.read() != 0x00) {
            throw new IOException("Illegal escape");
        }
        return (byte) b;
    }

    private static byte[] read(InputStream is, int counter) throws IOException {
        byte[] retVal = new byte[counter];
        while (counter-- > 0) {
            retVal[retVal.length - counter - 1] = read(is);
        }
        return retVal;
    }

    private static byte[] receive(SerialPort serialPort) throws IOException {
        InputStream inputStream = serialPort.getInputStream();
        waitForData(inputStream, 1000, 2);
        byte[] retVal = new byte[2];
        inputStream.read(retVal, 0, 2);
        if (retVal[0] != (byte) 0xAA || retVal[1] != (byte) 0xBB) {
            throw new IOException("Invalid preamble");
        }

        waitForData(inputStream, 10, 2);
        retVal = read(inputStream, 2);
        int length = BitsAndBytes.promoteByteToInt(retVal[0]);

        waitForData(inputStream, 100, length);
        retVal = read(inputStream, length - 1);
        if (calcCrc(retVal) != read(inputStream)) {
            throw new IOException("Invalid XOR");
        }
        return retVal;
    }

    private static byte[] talk(SerialPort serialPort, int command, byte[] data) throws IOException {
        if (data == null) {
            data = new byte[0];
        }

        byte[] q = new byte[data.length + 2];
        q[0] = BitsAndBytes.extractByte(command, 1);
        q[1] = BitsAndBytes.extractByte(command, 0);
        System.arraycopy(data, 0, q, 2, data.length);

        send(serialPort, q);
        byte[] a = receive(serialPort);

        if (a[2] != q[0] || a[3] != q[1]) {
            throw new IOException("Command code mismatch");
        }
        /**
         * Error Code	Meanings 10	General error 11	Don't support this command 12
         * Command Parameter error
         *
         * 20	Request failure 21	Reset failure 22	Authenticate failure 23	Read
         * failure 24	Write failure
         */
        if (a[4] != 0) {
            switch (a[4]) {
                case 10:
                    throw new StatusException(a[4] + " - General error. Frame sent: " + ToString.byteArrayToString(data));
                case 11:
                    throw new StatusException(a[4] + " - Don't support this command. Frame sent: " + ToString.byteArrayToString(data));
                case 12:
                    throw new StatusException(a[4] + " - Command Parameter error. Frame sent: " + ToString.byteArrayToString(data));
                case 20:
                    throw new StatusException(a[4] + " - Request failure. Frame sent: " + ToString.byteArrayToString(data));
                case 21:
                    throw new StatusException(a[4] + " - Reset failure. Frame sent: " + ToString.byteArrayToString(data));
                case 22:
                    throw new StatusException(a[4] + " - Authenticate failure. Frame sent: " + ToString.byteArrayToString(data));
                case 23:
                    throw new StatusException(a[4] + " - Read failure. Frame sent: " + ToString.byteArrayToString(data));
                case 24:
                    throw new StatusException(a[4] + " - Write failure. Frame sent: " + ToString.byteArrayToString(data));
                default:
                    throw new StatusException(a[4] + " - OTHER STATUS CODE. Frame sent: " + ToString.byteArrayToString(data));
            }
        }

        byte[] retVal = new byte[a.length - 5];
        for (int i = 5; i < a.length; i++) {
            retVal[i - 5] = a[i];
        }

        return retVal;
    }

    private static byte calcCrc(byte[] frame) {
        byte retVal = 0;
        for (int i = 0; i < frame.length; i++) {
            retVal ^= frame[i];
        }
        return retVal;
    }
    ////////////////////////////////////////////////////////////////////////////
    ///////////// Public Reader functions //////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////
    private byte beepTime = 0x00;

    public void beep() {
        beepTime = 10;
    }
    ////////////////////////////////////////////////////////////////////////////
    /////////// BP Driver implementation ///////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////
    private boolean pleaseTerminate = false;

    public void run() {
        try {
            initReader();
            fsmLoop();
            shutdownReader();
        } catch (IOException e) {
            logger.warning(e.getMessage());
            e.printStackTrace(System.err);
        } finally {
            serialPort.close();
        }
    }

    @Override
    public String getDeviceAddress() {
        return busName + ":" + deviceNumber;
    }

    public void setBusName(String busName) {
        this.busName = busName;
    }

    @Override
    public synchronized byte[] readBlock(ClassicKeyType keyType, byte[] key, int blockNumber) throws MifareException {
        if (key.length != 6) {
            throw new MifareException("key.length != 6");
        }

        if (finiteState != FiniteState.CardSelectedClassic) {
            throw new MifareException("finiteState != FiniteState.CardSelectedClassic");
        }

        try {
            needOvertakeControl = true;
            while (!overtakeControlGranted) {
                sleep(1);
            }
            needOvertakeControl = false;

            try {
                rfRequest(REQ_ALL);
                rfAnticoll();
                rfSelect(currentCardSerialNumber);
                if (keyType == ClassicKeyType.KeyA) {
                    rfM1Authentication2(KEY_TYPE_A, BitsAndBytes.castIntToByte(blockNumber), key);
                } else {
                    rfM1Authentication2(KEY_TYPE_B, BitsAndBytes.castIntToByte(blockNumber), key);
                }
                byte[] result = rfM1Read(BitsAndBytes.castIntToByte(blockNumber));
                rfHalt();
                return result;
            } catch (IOException ioe) {
                throw new MifareException(ioe);
            }
        } finally {
            overtakeControlGranted = false;
        }
    }

    public void writeBlock(ClassicKeyType keyType, byte[] key, int blockNumber, byte[] data) throws MifareException {
        if (key.length != 6) {
            throw new MifareException("key.length != 6");
        }
        if (data.length != 16) {
            throw new MifareException("data.length != 16");
        }

        if (finiteState != FiniteState.CardSelectedClassic) {
            throw new MifareException("finiteState != FiniteState.CardSelectedClassic");
        }

        try {
            needOvertakeControl = true;
            while (!overtakeControlGranted) {
                sleep(1);
            }
            needOvertakeControl = false;

            try {
                rfRequest(REQ_ALL);
                rfAnticoll();
                rfSelect(currentCardSerialNumber);
                if (keyType == ClassicKeyType.KeyA) {
                    rfM1Authentication2(KEY_TYPE_A, BitsAndBytes.castIntToByte(blockNumber), key);
                } else {
                    rfM1Authentication2(KEY_TYPE_B, BitsAndBytes.castIntToByte(blockNumber), key);
                }
                rfM1Write(BitsAndBytes.castIntToByte(blockNumber), data);
                rfHalt();
            } catch (IOException ioe) {
                throw new MifareException(ioe);
            }
        } finally {
            overtakeControlGranted = false;
        }
    }
}
