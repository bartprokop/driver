/*
 * Network.java
 *
 * Created on 26 grudzień 2004, 19:17
 */
package name.prokop.bart.hardware.driver.common;

/**
 *
 * @author bart
 */
public class Network {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        System.out.println("Lokalny adres IP: " + getLocalIp());
    }

    public static String getLocalIp() {
        String localIp = "127.0.0.1";

        try {
            java.net.InetAddress addr = java.net.InetAddress.getLocalHost();
            localIp = addr.getHostAddress();
        } catch (java.net.UnknownHostException e) {
        } catch (Exception e) {
        }
        return localIp;
    }
}
