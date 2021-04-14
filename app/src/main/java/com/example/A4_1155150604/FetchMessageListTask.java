package com.example.A4_1155150604;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class FetchMessageListTask extends AsyncTask<String, Void, Void> {
    private static String emptyMessageList = "Server returns 0 messages";
    private ArrayList<Message> messages;
    private MessageAdapter messageAdapter;
    private ListView lv;
    private IdNamePage idNamePage;
    private Context context;
    private int originMessagesSize;


    public FetchMessageListTask(ArrayList<Message> messages, MessageAdapter messageAdapter, ListView lv, IdNamePage idNamePage, Context context) {
        this.messages = messages;
        this.messageAdapter = messageAdapter;
        this.lv = lv;
        this.context = context;
        this.idNamePage = idNamePage;
        this.originMessagesSize = this.messages.size();
    }

    @Override
    protected Void doInBackground(String... strings) {
        String json_result = Utils.fetchPage(String.format(strings[0], idNamePage.chatroomId, idNamePage.currentPage));
        if (json_result.equals("")) {
            return null;
        }

        try {
            JSONObject json = new JSONObject(json_result);
            String status = json.getString("status");
            if (!status.equals("OK")) {
                return null;
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

                if (user_id == idNamePage.user_id) {
                    messages.add(0, new Message(id, content, user_id, user_name, Message.TYPE_SEND, time));
                } else {
                    messages.add(0, new Message(id, content, user_id, user_name, Message.TYPE_RECEIVE, time));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        if (messages.isEmpty()) {
            Toast.makeText(context, emptyMessageList, Toast.LENGTH_SHORT).show();
        }

        messageAdapter.notifyDataSetChanged();
        lv.setSelection(messages.size() - originMessagesSize);
    }

}
