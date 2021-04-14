package com.example.A4_1155150604;

import android.net.Uri;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class Utils {
    public static String fetchPage(String urlString) {
        StringBuilder json = new StringBuilder();
        InputStream is = null;
        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);

            conn.connect();
            int responseCode = conn.getResponseCode();

            if (responseCode == 200) {
                is = conn.getInputStream();
                String line = null;
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                while ((line = br.readLine()) != null) {
                    json.append(line);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return json.toString();
    }

    public static String postHTTPRequest(String urlString, ArrayList<String> para_names, ArrayList<String> para_values) {
        StringBuilder json = new StringBuilder();
        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            Uri.Builder builder = new Uri.Builder();

            for (int i = 0; i < para_names.size(); i++) {
                builder.appendQueryParameter(para_names.get(i), para_values.get(i));
            }
            String query = builder.build().getEncodedQuery();
            writer.write(query);
            writer.flush();
            writer.close();
            os.close();

            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                InputStream is = conn.getInputStream();
                String line = null;
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                while ((line = br.readLine()) != null) {
                    json.append(line);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return json.toString();
    }

    /**
     * OkHTTP
     * @param address
     * @param params
     * @param callback
     */
    public static void sendOkHttpGetRequest(String address, Map<String, String> params, okhttp3.Callback callback) {
        OkHttpClient client = new OkHttpClient();
        HttpUrl.Builder httpBuilder = HttpUrl.parse(address).newBuilder();
        if (params != null) {
            for(Map.Entry<String, String> param : params.entrySet()) {
                httpBuilder.addQueryParameter(param.getKey(),param.getValue());
            }
        }
        Request request = new Request.Builder()
                .url(httpBuilder.build())
                .build();
        client.newCall(request).enqueue(callback);
    }


}
