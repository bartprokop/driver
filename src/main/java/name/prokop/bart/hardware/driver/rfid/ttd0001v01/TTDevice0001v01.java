/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package name.prokop.bart.hardware.driver.rfid.ttd0001v01;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import name.prokop.bart.hardware.driver.Device;
import name.prokop.bart.hardware.driver.DeviceDetectedEvent;
import name.prokop.bart.driver.wire.ttbus.TTFrame;
import name.prokop.bart.driver.wire.ttbus.TTFrameType;
import name.prokop.bart.driver.wire.ttbus.TTSoftConnection;
import name.prokop.bart.driver.wire.ttbus.TTSoftDevice;
import name.prokop.bart.driver.wire.ttbus.TTSoftDevicePriority;
import name.prokop.bart.hardware.driver.common.BitsAndBytes;
import name.prokop.bart.hardware.driver.common.ToString;
import name.prokop.bart.hardware.driver.rfid.MifareException;
import name.prokop.bart.hardware.driver.rfid.MifareReader.ClassicKeyType;
import name.prokop.bart.hardware.driver.rfid.RFIDCardType;

/**
 *
 * @author bart
 */
public class TTDevice0001v01 extends TTSoftDevice {

    private static final Logger logger = Logger.getLogger(TTDevice0001v01.class.toString());
    private long invocationCounter = 0;
    private FMState fmState = FMState.Iddle;
    private RFIDCardType currentCardType = null;
    private byte[] currentCardSerialNumber = null;
    private boolean input1 = true, input2 = true;

    public void openRelay() {
        openRelay(10);
    }

    public void universalOpenRelay(int time) {
        // dawniej *10 teraz bez mnozenia
        openRelay(time);
    }

    @Override
    public int compareTo(Device o) {
        return getDeviceAddress().compareTo(o.getDeviceAddress());
    }

    @Override
    public String getDeviceDescription() {
        return "Czytnik TTD-0001 MIFARE v. 01";
    }

    @Override
    public String getDeviceInfo() {
        return getDeviceAddress() + " @ " + connection.getAddress();
    }

    public byte[] readBlock(ClassicKeyType keyType, byte[] key, int blockNumber) throws MifareException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void writeBlock(ClassicKeyType keyType, byte[] key, int blockNumber, byte[] data) throws MifareException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private enum FMState {

        Iddle, CardInField;
    }

    private synchronized void actualizeInputs() {

        try {
            Thread.sleep(20);
        } catch (InterruptedException ex) {
            Logger.getLogger(TTDevice0001v01.class.getName()).log(Level.SEVERE, null, ex);
        }

        TTFrame frame = new TTFrame(TTFrameType.Frame1, txGetStatus((byte) (0x00)));

        try {
            byte[] state = talk(frame);

            byte b = state[2];

            this.input1 = ((~b) & 1) == 1;
            this.input2 = ((~b) & 2) == 2;

        } catch (IOException e) {
            logger.warning(getDeviceAddress() + " IOEx: " + e.getMessage());

        }
    }

    public TTDevice0001v01(int id, TTSoftConnection connection) {
        super(id, connection);
        postEvent(new DeviceDetectedEvent(this));
//        actualizeInputs();
    }

    @Override
    public TTSoftDevicePriority getDevicePriority() {
        return TTSoftDevicePriority.High;
    }

    @Override
    public void takeControl() {
        invocationCounter++;
        processOrders();

        switch (fmState) {
            case Iddle:
                doIddle();
                return;
            case CardInField:
                doCardInField();
                return;
            default:
                throw new IllegalStateException();
        }
    }

    private void processOrders() {
        System.out.println(internalOrders.size());
        if (internalOrders.size() > 0) {
            InternalOrder order = internalOrders.remove(0);
            System.out.println("Odebrano");

            if (order instanceof OpenRelay) {
                OpenRelay or = (OpenRelay) order;
                TTFrame frame = new TTFrame(TTFrameType.Frame1, txOpenRelay(or.time));
                try {
                    talk(frame);
                    TTDevice0001v01RelayOpened e = new TTDevice0001v01RelayOpened(this);
                    postEvent(e);
                } catch (IOException e) {
                    logger.warning(getDeviceAddress() + " IOEx: " + e.getMessage());
                }
            }

            if (order instanceof SetKey) {
                SetKey sk = (SetKey) order;
                TTFrame frame = new TTFrame(TTFrameType.Frame1, txLoadKeyE2((byte) 0, new byte[]{(byte) 0xB5, (byte) 0x72, (byte) 0x56, (byte) 0xEB, (byte) 0xD4, (byte) 0x4A}));
                try {
                    byte[] talk = talk(frame);
                    System.out.println("RES: " + ToString.byteArrayToString(talk));
                } catch (IOException e) {
                    logger.warning(getDeviceAddress() + " IOEx: " + e.getMessage());
                }
            }
        }
    }

    private void doIddle() {
        TTFrame frame = new TTFrame(TTFrameType.Frame1, txGetStatus((byte) (invocationCounter % 2)));
        try {
            byte[] state = talk(frame);
            analyseInputs(state[2]);
            //System.out.println(ToString.byteArrayToString(state));
            // added second condition for escaping from "unknown card"
            if (state[0] == 0x01 && state[1] != 0x00) {
                fmState = FMState.CardInField;
                switch (state[1]) {
                    case 1:
                        currentCardType = RFIDCardType.Mifare1K;
                        currentCardSerialNumber = BitsAndBytes.subArray(state, 3, 7);
                        break;
                    case 2:
                        currentCardType = RFIDCardType.Mifare4K;
                        currentCardSerialNumber = BitsAndBytes.subArray(state, 3, 7);
                        break;
                    case 3:
                        currentCardType = RFIDCardType.MifareUltralight;
                        currentCardSerialNumber = BitsAndBytes.subArray(state, 3, 10);
                        break;
                    default:
                        throw new IllegalStateException();
                }
                TTDevice0001v01CardDetected e = new TTDevice0001v01CardDetected(this, currentCardType, currentCardSerialNumber);
                postEvent(e);
            }
        } catch (IOException e) {
            logger.warning(getDeviceAddress() + " IOEx: " + e.getMessage());

        }
    }

    private void doCardInField() {
        //TTFrame frame = new TTFrame(TTFrameType.Frame1, txGetStatus((byte) 2));
        TTFrame frame = new TTFrame(TTFrameType.Frame1, txGetStatus((byte) (invocationCounter % 2)));
        try {
            byte[] state = talk(frame);
            analyseInputs(state[2]);
            //System.out.println(ToString.byteArrayToString(state));
            if (state[0] == 0x00) {
                fmState = FMState.Iddle;
                TTDevice0001v01CardRemoved e = new TTDevice0001v01CardRemoved(this, currentCardType, currentCardSerialNumber);
                postEvent(e);
                currentCardType = null;
                currentCardSerialNumber = null;
            }
        } catch (IOException e) {
            logger.warning(getDeviceAddress() + " IOEx: " + e.getMessage());

        }
    }

    private void analyseInputs(byte b) {
        boolean i1 = ((~b) & 1) == 1;
        boolean i2 = ((~b) & 2) == 2;

        // zwarcie (wciśnięcie przycisku)
        if (!input1 && i1) {
            postEvent(new TTDevice0001v01InputChanged(this, 1, true));
        }
        if (!input2 && i2) {
            postEvent(new TTDevice0001v01InputChanged(this, 2, true));
        }
        // rozwarcie stykow
        if (input1 && !i1) {
            postEvent(new TTDevice0001v01InputChanged(this, 1, false));
        }
        if (input2 && !i2) {
            postEvent(new TTDevice0001v01InputChanged(this, 2, false));
        }

        this.input1 = i1;
        this.input2 = i2;
    }

    ////////////////////////////////////////////////////////////////////////////
    ///////// low level communication //////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////
    private static byte[] txGetStatus(byte led) {
        return new byte[]{0x01, led};
    }

    private static byte[] txOpenRelay(byte time) {
        System.out.println("**************************************");
        return new byte[]{0x02, time};
    }

    private static byte[] txReadPage(byte addr) {
        return new byte[]{0x03, addr};
    }

    private static byte[] txWritePage(byte addr, int data) {
        return new byte[]{0x04, addr, BitsAndBytes.extractByte(data, 3), BitsAndBytes.extractByte(data, 2), BitsAndBytes.extractByte(data, 1), BitsAndBytes.extractByte(data, 0)};
    }

    private static byte[] txWritePage(byte addr, byte[] data) {
        return new byte[]{0x04, addr, data[3], data[2], data[1], data[0]};
    }

    private static byte[] txLoadKeyE2(byte idx, byte[] key) {
        return new byte[]{0x05, idx, key[0], key[1], key[2], key[3], key[4], key[5]};
    }
    private static final byte KEY_TYPE_A = 0x01;
    private static final byte KEY_TYPE_B = 0x00;

    private static byte[] txWriteDataToBlock(byte keyIdx, byte keyType, byte[] data) {
        return new byte[]{0x05, keyIdx, keyType, data[0], data[1], data[2], data[3],
            data[4], data[5], data[6], data[7],
            data[8], data[9], data[10], data[11],
            data[12], data[13], data[14], data[15]};
    }

    private static byte[] txReadDataFromBlok(byte keyIdx, byte keyType, byte dataIdx) {
        return new byte[]{0x05, keyIdx, keyType, dataIdx};
    }
    ////////////////////////////////////////////////////////////////////////////
    /////////// orders /////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////
    private final List<InternalOrder> internalOrders = new ArrayList<>();

    public void openRelay(int time) {
        internalOrders.add(new OpenRelay(BitsAndBytes.castIntToByte(time)));
        System.out.println("Dodano" + internalOrders.size());
    }

    public void setKey() {
        internalOrders.add(new SetKey());
    }

    private class InternalOrder {
    }

    private class OpenRelay extends InternalOrder {

        final byte time;

        public OpenRelay(byte time) {
            this.time = time;
        }
    }

    private class SetKey extends InternalOrder {
    }
}
