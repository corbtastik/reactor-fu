package io.corbs.stocks;

import java.net.HttpURLConnection;
import java.net.URL;

public class RequestRunnable implements Runnable {

    private final String url;

    public RequestRunnable(String url) {
        this.url = url;
    }

    @Override
    public void run() {
        String result = "";
        int code;
        try {
            URL siteURL = new URL(url);
            HttpURLConnection connection = (HttpURLConnection)siteURL.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            code = connection.getResponseCode();
            if (code == 200) {
                result = "* Green *\t";
            }
        } catch (Exception e) {
            result = "-> Red <-\t";
        }
        System.out.println(url + "\t\tStatus:" + result);
    }
}
