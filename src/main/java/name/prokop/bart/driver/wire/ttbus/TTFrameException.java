package name.prokop.bart.driver.wire.ttbus;

import java.io.IOException;

/**
 *
 * @author bart
 */
public class TTFrameException extends IOException {

    TTFrameException(String string) {
        super(string);
    }

    TTFrameException(int frameType, int id, int currCnt, int prevCnt, int len, int avaiable) {
        super("data under- or over- run: "
                + " frameType=" + Integer.toHexString(frameType)
                + " id=" + Integer.toHexString(id)
                + " currCnt=" + Integer.toHexString(currCnt)
                + " prevCnt=" + Integer.toHexString(prevCnt)
                + " len=" + len
                + " avaiable=" + avaiable);
    }
}