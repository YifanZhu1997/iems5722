package com.example.A4_1155150604;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

public class PostMessageTask extends AsyncTask<String, Void, Void> {
    private static String postMessageFail = "Message posting/broadcasting failed";
    private static ArrayList<String> para_names = new ArrayList<String>(Arrays.asList("chatroom_id", "user_id", "name", "message"));
    private ArrayList<String> para_values = new ArrayList<String>();
    private int chatroomId;
    private Message new_message;
    private Context context;
    private String result;

    public PostMessageTask(Message new_message, int chatroomId, Context context) {
        this.context = context;
        this.chatroomId = chatroomId;
        this.new_message = new_message;
    }

    @Override
    protected Void doInBackground(String... strings) {
        this.para_values.add(chatroomId + "");
        this.para_values.add(new_message.user_id + "");
        this.para_values.add(new_message.user_name);
        this.para_values.add(new_message.content);

        String json_result = Utils.postHTTPRequest(strings[0], para_names, para_values);
        if (json_result.equals("")) {
            return null;
        }

        this.result = Utils.parsePostMessageResult(json_result);
        return null;
    }

    protected void onPostExecute(Void aVoid) {
        if (result == null || !result.equals("OK")) {
            Toast.makeText(context, postMessageFail, Toast.LENGTH_SHORT).show();
        }
    }
}
