package com.example.A4_1155150604;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.ListView;
import android.widget.Toast;

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

        Utils.parseJSONMessages(json_result, messages, idNamePage);
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
