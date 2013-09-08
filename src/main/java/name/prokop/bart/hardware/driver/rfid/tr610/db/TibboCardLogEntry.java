/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package name.prokop.bart.hardware.driver.rfid.tr610.db;

import java.util.Date;
import java.util.List;
import name.prokop.bart.hardware.driver.common.BartDate;
import name.prokop.bart.hardware.driver.common.DOMUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author bart
 */
public class TibboCardLogEntry implements TibboDatabaseRecord, Comparable<TibboCardLogEntry> {

    private int pos;
    private TibboCard card;
    private Date timestamp;

    public TibboCardLogEntry(Element e, TibboDatabase database) {
        pos = Integer.parseInt(e.getAttribute("pos"));
        timestamp = BartDate.parseDateChecked("yyyy-M-d H:m:s", DOMUtil.getChildElement(e, "timestamp").getTextContent().trim());
        card = database.getCards().get(Integer.parseInt(DOMUtil.getChildElement(e, "cardRef").getTextContent().trim()));
    }

    public String toUDPSequence() {
        throw new IllegalStateException("This is read only table");
    }

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    public TibboCard getCard() {
        return card;
    }

    public void setCard(TibboCard card) {
        this.card = card;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public static void parse(Document document, TibboDatabase database) {
        List<Element> log = DOMUtil.getChildElements(document.getDocumentElement());
        for (Element e : log) {
            TibboCardLogEntry entry = new TibboCardLogEntry(e, database);
            database.getCardLog().put(entry.getPos(), entry);
        }
    }

    @Override
    public String toString() {
        return pos + ". " + timestamp + " -Card> " + card;
    }

    public int compareTo(TibboCardLogEntry o) {
        return timestamp.compareTo(o.timestamp);
    }
}
