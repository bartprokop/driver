/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package name.prokop.bart.hardware.driver.rfid.tr610.db;

import java.util.List;
import name.prokop.bart.hardware.driver.common.DOMUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author bart
 */
public class TibboCard implements TibboDatabaseRecord {

    private int pos;
    private String serialNumber;
    private String cardType;
    private TibboHuman human;

    public static void parse(Document document, TibboDatabase database) {
        List<Element> tibboCards = DOMUtil.getChildElements(document.getDocumentElement());
        for (Element tc : tibboCards) {
            TibboCard tibboCard = new TibboCard(tc, database);
            database.getCards().put(tibboCard.getPos(), tibboCard);
        }
    }

    public TibboCard() {
    }

    public TibboCard(String cardType, String serialNumber, TibboHuman human) {
        this.cardType = cardType;
        this.serialNumber = serialNumber;
        this.human = human;
    }

    public TibboCard(Element e, TibboDatabase database) {
        pos = Integer.parseInt(e.getAttribute("pos"));
        serialNumber = DOMUtil.getChildElement(e, "serialNumber").getTextContent().trim();
        cardType = DOMUtil.getChildElement(e, "cardType").getTextContent().trim();
        int humanRef = Integer.parseInt(DOMUtil.getChildElement(e, "humanRef").getTextContent().trim());
        human = database.getHumans().get(humanRef);
    }

    public String toUDPSequence() {
        return "Cards\t" + serialNumber + "\t" + cardType + "\t" + human.getPos() + "\t";
    }

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    public TibboHuman getHuman() {
        return human;
    }

    public void setHuman(TibboHuman human) {
        this.human = human;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    @Override
    public String toString() {
        return pos + ". (" + cardType + ')' + serialNumber + " -Human> " + human;
    }
}
