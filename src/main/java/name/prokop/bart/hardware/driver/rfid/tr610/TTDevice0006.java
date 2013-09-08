/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package name.prokop.bart.hardware.driver.rfid.tr610;

import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Calendar;
import name.prokop.bart.hardware.driver.Device;
import name.prokop.bart.hardware.driver.Driver;
import name.prokop.bart.hardware.driver.DeviceDetectedEvent;
import name.prokop.bart.hardware.driver.common.DOMUtil;
import name.prokop.bart.hardware.driver.common.URLGrabber;
import name.prokop.bart.hardware.driver.rfid.RFIDDevice;
import name.prokop.bart.hardware.driver.rfid.RelayOpenAbility;
import name.prokop.bart.hardware.driver.rfid.tr610.db.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.Document;

/**
 *
 * @author bart
 */
public abstract class TTDevice0006 implements RelayOpenAbility, RFIDDevice {

    @Autowired
    protected Driver driver;
    protected static final Logger LOG = LoggerFactory.getLogger(TTDevice0006.class);
    private final String serialNumber;
    private final SocketAddress socketAddress;
    private long lastSeen = System.currentTimeMillis();
    protected final TTDevice0006Server parent;

    public TTDevice0006(TTDevice0006Server parent, SocketAddress socketAddress, String serialNumber) {
        this.parent = parent;
        this.socketAddress = socketAddress;
        this.serialNumber = serialNumber;
        driver.postEvent(new DeviceDetectedEvent(this));
        sendTime();
    }

    protected void processDatagram(String[] tokens) {
        String command = tokens[0].substring(1);
        if (command.equals("P")) {
            lastSeen = System.currentTimeMillis();
            send("0P\t", false);
        }
        if (command.equals("S")) {
            driver.postEvent(new TTDevice0006CardDetected(this, tokens[1]));
        }
        if (command.equals("B")) {
            driver.postEvent(new TTDevice0006BarCodeScanned(this, tokens[1]));
        }
        if (command.equals("K")) {
            driver.postEvent(new TTDevice0006ButtonPressed(this, tokens[1]));
        }
        if (command.equals("DB")) {
            processUploadDatabase(tokens);
        }
    }
    private char msgCounter = 'a';

    protected String send(String s, boolean trackReply) {
        if (trackReply) {
            s = msgCounter++ + s;
            if (msgCounter > 'z') {
                msgCounter = 'a';
            }
        }
        //System.err.println(parent.datagramSocket.getInetAddress() + " >> " + s);
        byte[] buf = s.getBytes();
        try {
            DatagramPacket packet = new DatagramPacket(buf, buf.length, socketAddress);
            parent.datagramSocket.send(packet);
            return s;
        } catch (Exception e) {
            e.printStackTrace(System.err);
            return null;
        }
    }

    public void lockDevice() {
        String s = send("LOCK\t", true);
        sleep(100);
        send(s, false);
    }

    public void unlockDevice() {
        String s = send("UNLOCK\t", true);
        sleep(100);
        send(s, false);
    }

    public void openRelay() {
        openRelay(1, 10);
    }

    public void openRelay(int relay, int time) {
        send("0R\t" + relay + "\t" + time + "\t", false);
    }

    public String databaseAddRecord(TibboDatabaseRecord record) {
        return send("DB\tA\t" + record.toUDPSequence(), true);
    }

    private void sendTime() {
        Calendar c = Calendar.getInstance();
        int y = c.get(Calendar.YEAR) - 2000;
        int m = c.get(Calendar.MONTH) + 1;
        int d = c.get(Calendar.DAY_OF_MONTH);
        int H = c.get(Calendar.HOUR_OF_DAY);
        int M = c.get(Calendar.MINUTE);
        int S = c.get(Calendar.SECOND);
        send("0T\t" + y + "\t" + m + "\t" + d + "\t" + H + "\t" + M + "\t" + S + "\t", false);
    }

    @Override
    public String getDeviceAddress() {
        return parent.getBusName() + ":" + serialNumber;
    }

    @Override
    public String toString() {
        return socketAddress + " / SN: " + serialNumber;
    }

    public long getLastSeen() {
        return lastSeen;
    }

    public SocketAddress getSocketAddress() {
        return socketAddress;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public String getURLBase() {
        InetSocketAddress tibboIP = (InetSocketAddress) socketAddress;
        return "http://" + tibboIP.getHostName() + "/";
    }

    public Document retrieveDocument(String file) {
        try {
            return DOMUtil.parse(URLGrabber.getDocumentAsString(getURLBase() + file));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public TibboDatabase retrieveDatabase() {
        TibboDatabase retVal = new TibboDatabase();
        TibboHuman.parse(retrieveDocument("dbHumans.html"), retVal);
        TibboCard.parse(retrieveDocument("dbCards.html"), retVal);
        TibboCardLogEntry.parse(retrieveDocument("dbCardLog.html"), retVal);
        return retVal;
    }
    private DatabaseUploader databaseUploader = null;

    private void processUploadDatabase(String[] tokens) {
        if (databaseUploader == null) {
            return;
        }
        //System.out.println("<< : " + tokens[3]);
        databaseUploader.processUploadDatabase(tokens);
    }

    public void databaseClearTable(String table) {
        String s = send("DB\tCLR_TBL\t" + table + "\t", true);
        sleep(100);
        send(s, false);
    }

    public void databaseDrop() {
        send("DB\tDROP_DB\t", true);
    }

    public void databaseUpload(TibboDatabase tibboDatabase) {
        databaseUploader = new DatabaseUploader(tibboDatabase);
    }

    public int compareTo(Device o) {
        return getDeviceAddress().compareTo(o.getDeviceAddress());
    }

    public String getDeviceInfo() {
        return "Remote: " + socketAddress.toString() + " Local:" + parent.getBusInfo();
    }

    private class DatabaseUploader implements Runnable {

        private final TibboDatabase tibboDatabase;
        private TibboDatabaseRecord currentRecord;
        private char replyChar = '0';

        public DatabaseUploader(TibboDatabase tibboDatabase) {
            this.tibboDatabase = tibboDatabase;
            new Thread(this).start();
        }

        public synchronized void run() {
            String s;
            try {
                lockDevice();
                for (TibboHuman th : tibboDatabase.getHumans().values()) {
                    currentRecord = th;
                    s = databaseAddRecord(currentRecord);
                    wait(500);
                    while (s.charAt(0) != replyChar) {
                        System.out.println("Mamy: " + s + " - " + replyChar);
                        send(s, false);
                        wait(500);
                    }
                }
                for (TibboCard tc : tibboDatabase.getCards().values()) {
                    currentRecord = tc;
                    s = databaseAddRecord(currentRecord);
                    wait(500);
                    while (s.charAt(0) != replyChar) {
                        System.out.println("Mamy: " + s + " - " + replyChar);
                        send(s, false);
                        wait(500);
                    }
                }
                unlockDevice();
                System.out.println("Zakonczono upload");
            } catch (InterruptedException ie) {
            }
        }

        public synchronized void processUploadDatabase(String[] tokens) {
            if (databaseUploader == null) {
                return;
            }
            System.out.println("<< : " + tokens[2] + " - " + tokens[3]);
            currentRecord.setPos(Integer.parseInt(tokens[3]));
            replyChar = tokens[0].charAt(0);
            notify();
        }
    }

    private static void sleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException ie) {
        }
    }
}
