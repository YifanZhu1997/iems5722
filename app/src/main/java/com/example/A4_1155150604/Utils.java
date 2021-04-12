package com.example.A4_1155150604;

import android.net.Uri;

import org.json.JSONArray;
import org.json.JSONObject;

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

    public static String parsePostMessageResult(String jsonString) {
        String status = null;
        try {
            JSONObject json = new JSONObject(jsonString);
            status = json.getString("status");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return status;
    }

    public static void parseJSONChatrooms(String jsonString, ArrayList<Chatroom> chatrooms) {
        try {
            JSONObject json = new JSONObject(jsonString);
            String status = json.getString("status");
            if (!status.equals("OK")) { //status error
                return;
            }

            JSONArray chatroomArray = json.getJSONArray("data");
            for (int i = 0; i < chatroomArray.length(); i++) {
                String name = chatroomArray.getJSONObject(i).getString("name");
                int id = chatroomArray.getJSONObject(i).getInt("id");
                chatrooms.add(new Chatroom(name, id));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void parseJSONMessages(String jsonString, ArrayList<Message> messages, IdNamePage idNamePage) {
        try {
            JSONObject json = new JSONObject(jsonString);
            String status = json.getString("status");
            if (!status.equals("OK")) {
                return;
            }

            JSONObject data = json.getJSONObject("data");
            int currentPage = data.getInt("current_page");
            int totalPage = data.getInt("total_pages");
            idNamePage.currentPage = currentPage;
            idNamePage.totalPage = totalPage;

            JSONArray messageArray = data.getJSONArray("messages");

            for (int i = 0; i < messageArray.length(); i++) {
                int id = messageArray.getJSONObject(i).getInt("id");
                String content = messageArray.getJSONObject(i).getString("message");
                int user_id = messageArray.getJSONObject(i).getInt("user_id");
                String user_name = messageArray.getJSONObject(i).getString("name");
                String time = messageArray.getJSONObject(i).getString("message_time");

                if (user_name.equals(idNamePage.user_name) && user_id == idNamePage.user_id) {
                    messages.add(0, new Message(id, content, user_id, user_name, Message.TYPE_SEND, time));
                } else {
                    messages.add(0, new Message(id, content, user_id, user_name, Message.TYPE_RECEIVE, time));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
