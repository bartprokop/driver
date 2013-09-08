/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package name.prokop.bart.hardware.driver.common;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author bart
 */
public final class PostClient {

    public static String postClient(String url, Map<String, String> params) throws IOException {
        return postClient(new URL(url), params);
    }

    public static String postClient(URL url, Map<String, String> params) throws IOException {
        StringBuilder sb = new StringBuilder();
        if (params == null) {
            params = new HashMap<String, String>();
        }
        for (String paramName : params.keySet()) {
            try {
                sb.append(URLEncoder.encode(paramName, "UTF-8"));
                sb.append('=');
                sb.append(URLEncoder.encode(params.get(paramName), "UTF-8"));
                sb.append('&');
            } catch (UnsupportedEncodingException uee) {
                uee.printStackTrace(System.err);
            }
        }
        if (sb.length() > 0) {
            sb.delete(sb.length() - 1, sb.length());
        }

        URLConnection conn = url.openConnection();
        conn.setDoInput(true);
        conn.setDoOutput(true);

        OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
        wr.write(sb.toString());
        wr.flush();

        sb.delete(0, sb.length());
        InputStreamReader ir = new InputStreamReader(conn.getInputStream(), "UTF-8");
        char buf[] = new char[100];
        int len;
        while ((len = ir.read(buf)) != -1) {
            sb.append(buf, 0, len);
        }

        wr.close();
        ir.close();
        return sb.toString();
    }

    public static void main(String... args) throws Exception {
        URL url = new URL("http://e-dziecko.appspot.com/webinfo");
        HashMap<String, String> parametry = new HashMap<String, String>();
        parametry.put("Ala", "ma kota");
        parametry.put("Kot", "ma Ale");
        System.out.println(postClient(url, parametry));
    }
}
