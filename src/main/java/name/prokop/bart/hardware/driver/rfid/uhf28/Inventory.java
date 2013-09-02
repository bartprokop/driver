/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package name.prokop.bart.hardware.driver.rfid.uhf28;

import java.io.ByteArrayInputStream;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Administrator
 */
public class Inventory {
    
    public enum Status {
        
        CompletlyFinished,
        InventoryScanTimeOverFlow,
        OtherResponsesFollow,
        ReaderProcessLimit;
        
        static Status parse(byte b) {
            switch (b) {
                case 1:
                    return CompletlyFinished;
                case 2:
                    return InventoryScanTimeOverFlow;
                case 3:
                    return OtherResponsesFollow;
                case 4:
                    return ReaderProcessLimit;
                default:
                    return null;
            }
        }
    }
    private Status status;
    private Set<TagEntry> tags = new HashSet<TagEntry>();
    
    Inventory(byte[] frame) {
        status = Status.parse(frame[3]);
        processTags(frame);
    }
    
    private void processTags(byte[] frame) {
        ByteArrayInputStream bais = new ByteArrayInputStream(frame, 6, frame.length - 8);
        while (bais.available() > 0) {
            int len = bais.read();
//            System.out.println("LEN: " + len);
            byte[] sn = new byte[len];
            bais.read(sn, 0, len);
            tags.add(new TagEntry(sn));
        }
    }
    
    public Status getStatus() {
        return status;
    }
    
    public Set<TagEntry> getTags() {
        return tags;
    }
}
