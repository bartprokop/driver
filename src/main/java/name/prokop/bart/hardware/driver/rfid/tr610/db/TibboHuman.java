/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package name.prokop.bart.hardware.driver.rfid.tr610.db;

import java.util.List;
import name.prokop.bart.hardware.driver.common.DOMUtil;
import name.prokop.bart.hardware.driver.common.ToString;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author bart
 */
public class TibboHuman implements TibboDatabaseRecord, Comparable<TibboHuman> {

    private int pos;
    private String name;

    public static void parse(Document document, TibboDatabase database) {
        List<Element> tibboHumans = DOMUtil.getChildElements(document.getDocumentElement());
        for (Element th : tibboHumans) {
            TibboHuman tibboHuman = new TibboHuman(th, database);
            database.getHumans().put(tibboHuman.getPos(), tibboHuman);
        }
    }

    public TibboHuman() {
    }

    public TibboHuman(int pos, String name) {
        this.pos = pos;
        this.name = name;
    }

    private TibboHuman(Element th, TibboDatabase database) {
        pos = Integer.parseInt(th.getAttribute("pos"));
        name = DOMUtil.getChildElement(th, "name").getTextContent().trim();
    }

    public String toUDPSequence() {
        return "Humans\t" + ToString.string2noAccents(name) + "\t";
    }

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "[" + pos + "]. " + name;
    }

    public int compareTo(TibboHuman o) {
        return name.compareTo(o.name);
    }
}
