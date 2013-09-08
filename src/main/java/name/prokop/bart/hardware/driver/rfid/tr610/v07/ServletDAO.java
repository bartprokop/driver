/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package name.prokop.bart.hardware.driver.rfid.tr610.v07;

import java.io.IOException;
import java.util.*;
import name.prokop.bart.hardware.driver.common.BartDate;
import name.prokop.bart.hardware.driver.common.PostClient;
import name.prokop.bart.hardware.driver.common.Status;
import name.prokop.bart.hardware.driver.rfid.tr610.db.TibboCard;
import name.prokop.bart.hardware.driver.rfid.tr610.db.TibboCardLogEntry;
import name.prokop.bart.hardware.driver.rfid.tr610.db.TibboDatabase;
import name.prokop.bart.hardware.driver.rfid.tr610.db.TibboHuman;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author bart
 */
public final class ServletDAO {
    
    private final EDzieckoApplet owner;
    
    public ServletDAO(EDzieckoApplet owner) {
        this.owner = owner;
    }
    
    public Status sendDatabase(TibboDatabase tibboDatabase) {
        Set<JSONObject> requests = new HashSet<JSONObject>();
        Iterator<TibboCardLogEntry> iterator = tibboDatabase.getCardLog().values().iterator();
        int recordCount = 0;
        JSONObject database = null;
        JSONArray log = null;
        while (iterator.hasNext()) {
            try {
                TibboCardLogEntry tibboCardLogEntry = iterator.next();
                if (tibboCardLogEntry.getTimestamp().after(BartDate.encodeDate("20200101"))) {
                    continue;
                }
                if (recordCount++ % 25 == 0) {
                    database = new JSONObject();
                    log = new JSONArray();
                    database.putOpt("log", log);
                    requests.add(database);
                }
                JSONObject logEntry = new JSONObject();
                logEntry.putOpt("t", tibboCardLogEntry.getTimestamp().getTime());
                logEntry.putOpt("c", tibboCardLogEntry.getCard().getCardType() + tibboCardLogEntry.getCard().getSerialNumber());
                log.put(logEntry);
            } catch (JSONException jex) {
                return Status.fail(jex.getMessage());
            }
        }
        
        owner.getProgressBar().setMinimum(0);
        owner.getProgressBar().setMaximum(requests.size());
        for (JSONObject request : requests) {
            Map<String, String> params = new HashMap<String, String>();
            params.put("requestType", "UploadDatabase");
            params.put("przedszkoleId", owner.getPrzedszkoleKeyId() + "");
            params.put("tibboDatabase", request.toString());
            String response = null;
            try {
                response = PostClient.postClient(owner.getServerBase() + "appletsrv", params);
                owner.getProgressBar().setValue(owner.getProgressBar().getValue() + 1);
            } catch (IOException ioex) {
                return Status.fail(ioex.getMessage());
            }
            
            if (!Boolean.parseBoolean(response.trim())) {
                return Status.fail("Serwer niezaakceptował danych");
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException iex) {
                iex.printStackTrace(System.err);
            }
        }
        return Status.ok();
    }
    
    public TibboDatabase retrieveDatabase() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("requestType", "DownloadDatabase");
        params.put("przedszkoleId", owner.getPrzedszkoleKeyId() + "");
        String response = null;
        try {
            response = PostClient.postClient(owner.getServerBase() + "appletsrv", params);
        } catch (IOException ioex) {
            ioex.printStackTrace();
            return null;
        }
        
        TibboDatabase retVal = new TibboDatabase();
        try {
            JSONObject r = new JSONObject(response);
            JSONArray humans = r.getJSONArray("TibboHumans");
            JSONArray cards = r.getJSONArray("TibboCards");
            for (int i = 0; i < humans.length(); i++) {
                JSONObject h = humans.getJSONObject(i);
                retVal.add(new TibboHuman(h.getInt("pos"), h.getString("name")));
            }
            for (int i = 0; i < cards.length(); i++) {
                JSONObject c = cards.getJSONObject(i);
                retVal.add(new TibboCard(c.getString("t"), c.getString("s"), retVal.getHumans().get(c.getInt("h"))));
            }
        } catch (JSONException jex) {
            jex.printStackTrace();
            return null;
        }
        return retVal;
    }
}
