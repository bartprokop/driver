/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package name.prokop.bart.hardware.driver.rfid.tr610.db;

import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author bart
 */
public class TibboDatabase {

    private final Map<Integer, TibboHuman> humans = new TreeMap<Integer, TibboHuman>();
    private final Map<Integer, TibboCard> cards = new TreeMap<Integer, TibboCard>();
    private final Map<Integer, TibboCardLogEntry> cardLog = new TreeMap<Integer, TibboCardLogEntry>();

    public TibboHuman add(TibboHuman h) {
        if (h.getPos() == 0) {
            h.setPos(humans.size() + 1);
        }
        humans.put(h.getPos(), h);
        return h;
    }

    public TibboCard add(TibboCard c) {
        if (c.getPos() == 0) {
            c.setPos(cards.size() + 1);
        }
        cards.put(c.getPos(), c);
        return c;
    }

    public Map<Integer, TibboCardLogEntry> getCardLog() {
        return cardLog;
    }

    public Map<Integer, TibboCard> getCards() {
        return cards;
    }

    public Map<Integer, TibboHuman> getHumans() {
        return humans;
    }

    public TibboHuman findHuman(String name) {
        for (TibboHuman h : humans.values()) {
            if (h.getName().equalsIgnoreCase(name)) {
                return h;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Humans:\n");
        for (TibboHuman h : humans.values()) {
            sb.append(h).append('\n');
        }
        sb.append("Cards:\n");
        for (TibboCard c : cards.values()) {
            sb.append(c).append('\n');
        }
        sb.append("Log:\n");
        for (TibboCardLogEntry e : cardLog.values()) {
            sb.append(e).append('\n');
        }
        return sb.toString();
    }
}
