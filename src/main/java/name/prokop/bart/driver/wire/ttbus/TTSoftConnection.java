package name.prokop.bart.driver.wire.ttbus;

public interface TTSoftConnection {

    public String getAddress();
    public byte[] talk(TTSoftFrame frame);
    public void close();
}
