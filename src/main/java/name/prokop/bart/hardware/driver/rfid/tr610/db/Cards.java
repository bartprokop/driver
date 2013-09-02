/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package name.prokop.bart.hardware.bpdriver.rfid.ttd0006.db;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

/**
 *
 * @author bart
 */
public class Cards {

    public static void main(String[] args) throws Exception {
        DatagramSocket datagramSocket = new DatagramSocket(2009);
        datagramSocket.setSoTimeout(1000);
        for (int i = 0; i < 100; i++) {
            byte[] buf = "TA\tALA\t".getBytes();
            DatagramPacket packet = new DatagramPacket(buf, buf.length, new InetSocketAddress("192.168.147.50", 2009));
            datagramSocket.send(packet);
            Thread.sleep(1000);
        }
    }
}
