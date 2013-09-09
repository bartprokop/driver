package name.prokop.bart.driver.wire.ttbus;

import name.prokop.bart.hardware.driver.common.BitsAndBytes;

/**
 *
 * @author Bartłomiej P. Prokop
 */
public final class TTFrame {

    public static boolean dumpTalkTime = true;
    private static final int TIMEOUT = 350;
    static final int BOF = 0xCA;
    private final TTFrameType frameType;
    private byte id;
    private byte currTrId;
    private byte prevTrId;
    private final byte[] data;
    static final int EOF = 0xAC;

    public TTFrame(TTFrameType frameType, byte[] data) {
        this.frameType = frameType;
        if (data == null) {
            data = new byte[0];
        }
        this.data = data;
    }

//    private void clearRxBuffer(DatagramSocket datagramSocket) throws IOException {
//        while (true) {
//            try {
//                datagramSocket.setSoTimeout(1);
//                byte[] receiveData = new byte[256];
//                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
//                datagramSocket.receive(receivePacket);
//            } catch (IOException e) {
//                return;
//            }
//        }
//    }
//    private void send(SocketAddress socketAddress, DatagramSocket datagramSocket) throws IOException {
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        send(baos);
//        byte[] byteArray = baos.toByteArray();
//        DatagramPacket packet = new DatagramPacket(byteArray, byteArray.length, socketAddress);
//        datagramSocket.send(packet);
//    }
//    byte[] talk(TTSoftBus bus) throws IOException {
//        if (bus.getDatagramRemoteSocketAddress() != null && bus.getDatagramSocket() != null) {
//            return talk(bus.getDatagramRemoteSocketAddress(), bus.getDatagramSocket(), 3, TIMEOUT);
//        } else {
//            return talk(bus.getInputStream(), bus.getOutputStream(), 3, TIMEOUT);
//        }
//    }
//
//    byte[] talk(SocketAddress socketAddress, DatagramSocket datagramSocket, int retryCount, int timeout) throws IOException {
//        IOException e = null;
//        while (retryCount-- > 0) {
//            try {
//                sleep(15);
//                clearRxBuffer(datagramSocket);
//                long t = System.currentTimeMillis();
//                datagramSocket.setSoTimeout(timeout);
//                send(socketAddress, datagramSocket);
//                byte[] receiveData = new byte[256];
//                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
//                datagramSocket.receive(receivePacket);
//                byte[] receive = Arrays.copyOfRange(receivePacket.getData(), receivePacket.getOffset(), receivePacket.getOffset() + receivePacket.getLength());
//                receive = receive(new ByteArrayInputStream(receive), 10);
//                t = System.currentTimeMillis() - t;
//                if (dumpTalkTime) {
//                    System.err.println(socketAddress + " : " + t + " ms");
//                }
//                return receive;
//            } catch (IOException ex) {
//                e = ex;
//            }
//        }
//        throw new IOException(e);
//    }
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
}
