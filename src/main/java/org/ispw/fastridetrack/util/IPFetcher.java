package org.ispw.fastridetrack.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class IPFetcher {
    public static String getPublicIP() throws Exception {
        URL url = new URL("https://api.ipify.org");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            return in.readLine();
        }
    }
}

