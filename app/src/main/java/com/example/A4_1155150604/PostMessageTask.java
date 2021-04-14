package com.example.A4_1155150604;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;

public class PostMessageTask extends AsyncTask<String, Void, Void> {
    private static ArrayList<String> para_names = new ArrayList<String>(Arrays.asList("chatroom_id", "user_id", "name", "message"));
    private ArrayList<String> para_values = new ArrayList<String>();
    private IdNamePage idNamePage;
    private Message new_message;
    private Context context;
    private String message;
    private String result;
    private String money;

    public PostMessageTask(Message new_message, IdNamePage idNamePage, Context context) {
        this.context = context;
        this.idNamePage = idNamePage;
        this.new_message = new_message;
    }

    public PostMessageTask(Message new_message, IdNamePage idNamePage, Context context, String money) {
        this.context = context;
        this.idNamePage = idNamePage;
        this.new_message = new_message;
        this.money = money;
    }

    @Override
    protected Void doInBackground(String... strings) {
        this.para_values.add(idNamePage.chatroomId + "");
        this.para_values.add(new_message.user_id + "");
        this.para_values.add(new_message.user_name);
        this.para_values.add(new_message.content);

        String json_result = Utils.postHTTPRequest(strings[0], para_names, para_values);
        if (json_result.equals("")) {
            return null;
        }

        try {
            JSONObject json = new JSONObject(json_result);
            String status = json.getString("status");
            if (status.equals("OK")) {
                this.result = "OK";
            } else if (status.equals("ERROR")) {
                this.result = "ERROR";
                this.message = json.getString("message");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        if (this.result == null) {
            Toast.makeText(context, "Post message failed", Toast.LENGTH_SHORT).show();
            recoverBalance();
        } else if (this.result.equals("ERROR")) {
            Toast.makeText(context, this.message, Toast.LENGTH_SHORT).show();
            recoverBalance();
        }
    }

    private void recoverBalance() {
        BigDecimal balance = new BigDecimal(idNamePage.balance);
        BigDecimal money_ = new BigDecimal(money);
        BigDecimal remain = balance.add(money_);
        remain.setScale(2);
        idNamePage.balance = remain.toString();
    }
}
