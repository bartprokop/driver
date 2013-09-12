package name.prokop.bart.driver.wire.ttbus;

public class TTSoftDatagramConnection implements TTSoftConnection {

    public static TTSoftConnection getConnection(String hostName, int port) {
        return new TTSoftDatagramConnection(hostName, port);
    }

    private TTSoftDatagramConnection(String hostName, int port) {
    }

    @Override
    public String getAddress() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

//    private static void discoverDevice(int id, SocketAddress sa, DatagramSocket ds) throws Exception {
//        TTFrame frame = new TTFrame(TTFrameType.FramePlugAndPlay, null);
//        frame.setId(id);
//        try {
//            byte[] answer = frame.talk(sa, ds, 1, 120);
//            discoverDevice(id, answer);
//        } catch (IOException e) {
//            System.out.println("Unsuccessful query @ " + id);
//        }
//    }
//    private static void discoverBusDevices(String udpHost, int remote, int local) throws Exception {
//        DatagramSocket ds = new DatagramSocket(local);
//        SocketAddress sa = new InetSocketAddress(udpHost, remote);
//        for (int id = 1; id < 255; id++) {
//            discoverDevice(id, sa, ds);
//        }
//        ds.close();
//    }
//    private DatagramSocket initDatagramSocket(int localPort) throws Exception {
//        DatagramSocket ds = new DatagramSocket(localPort);
//        return ds;
//    }
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
    @Override
    public byte[] talk(TTSoftFrame frame) {
        return null;
    }

    @Override
    public void close() {
    }
}
