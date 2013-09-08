/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package name.prokop.bart.hardware.driver.rfid.tr610.v02;

import java.net.SocketAddress;
import java.util.HashMap;
import java.util.Map;
import name.prokop.bart.hardware.driver.common.ToString;
import name.prokop.bart.hardware.driver.rfid.tr610.TTDevice0006;
import name.prokop.bart.hardware.driver.rfid.tr610.TTDevice0006Server;

/**
 *
 * @author bart
 */
public class TTDevice0006v02 extends TTDevice0006 {

    public TTDevice0006v02(TTDevice0006Server parent, SocketAddress socketAddress, String serialNumber) {
        super(parent, socketAddress, serialNumber);
    }

    @Override
    protected void processDatagram(String[] tokens) {
        super.processDatagram(tokens);
        if (tokens[0].equals("Y1")) {
            driver.postEvent(new TTDevice0006v02TanningTimeEntered(this, tokens[1], tokens[2]));
        }
        if (tokens[0].equals("Y2")) {
            if (tokens[1].equals("C") || tokens[1].equals("S")) {
                processUploader1(tokens);
            }
            if (tokens[1].equals("R")) {
                processSelectionResult(tokens);
            }
        }
    }

    public void displayMessage(String message) {
        send("0DM\t" + message + "\t", false);
    }

    public void displayText(int font, int aligment, String text) {
        text = text.replaceAll("\n", "`");
        send("0DT\t" + font + "\t" + aligment + "\t" + text + "\t", false);
    }

    public void enterTanningMode() {
        send("0X1\t", false);
    }

    public void overrideLogoOff() {
        send("0LO\tF\t", false);
    }

    public void overrideLogo(String line1, String line2, String line3) {
        send("0LO\tT\t" + line1 + "\t" + line2 + "\t" + line3 + "\t", false);
    }
    private SelectableListUploader uploader1 = null;

    public void requestUserSelection(Map<String, Integer> avaiableChoices) {
        uploader1 = new SelectableListUploader(avaiableChoices);
    }

    private void processUploader1(String[] tokens) {
        if (uploader1 != null) {
            uploader1.processUpload(tokens);
        }
    }

    private void processSelectionResult(String[] tokens) {
        Map<Integer, Integer> map = new HashMap<Integer, Integer>();
        for (int i = 3; i < tokens.length; i++) {
            map.put(i - 3, Integer.parseInt(tokens[i]));
        }
        TTDevice0006v02ListSelection event = new TTDevice0006v02ListSelection(this, tokens[2], map);
        driver.postEvent(event);
    }

    public String getDeviceDescription() {
        return "LAN MIFARE READER";
    }

    private class SelectableListUploader implements Runnable {

        private final Map<String, Integer> avaiableChoices;

        public SelectableListUploader(Map<String, Integer> avaiableChoices) {
            this.avaiableChoices = avaiableChoices;
            new Thread(this).start();
        }

        public synchronized void run() {
            try {
                send("0X2\tC\t" + avaiableChoices.keySet().size() + "\t", false);
                wait();
                int pos = 0;
                for (String key : avaiableChoices.keySet()) {
                    String s = ToString.string2noAccents(key);
                    if (s.length() > 15) {
                        s = s.substring(0, 15);
                    }
                    send("0X2\tS\t" + (pos++) + "\t" + s + "\t" + avaiableChoices.get(key) + "\t", false);
                    wait();
                }
                send("0X2\tSTART\t" + avaiableChoices.keySet().size() + "\t", false);
            } catch (InterruptedException ie) {
            }
        }

        public synchronized void processUpload(String[] tokens) {
            notify();
        }
    }
}
