package com.example.A4_1155150604;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabItem;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

public class FetchWallet extends AsyncTask<String, Void, Void> {
    private static String emptyMessageList = "Server returns 0 messages";
    private int user_id;
    private String user_name;
    private Context context;
    private String wallet;
    ItemView mwallet;


    public FetchWallet(Context context,int user_id, String user_name, String wallet, ItemView mwallet){

        this.context = context;
        this.user_id = user_id;
        this.user_name = user_name;
        this.wallet = wallet;
        this.mwallet = mwallet;
    }

    @Override
    protected Void doInBackground(String... strings) {
        String json_result = Utils.fetchPage(String.format(strings[0]));
        if (json_result.equals("")) {
            return null;
        }
        try {
            JSONObject json = new JSONObject(json_result);
            String status = json.getString("status");
            if (!status.equals("OK")) { //status error
                return null;
            }

            JSONArray data = json.getJSONArray("data");

            wallet = data.getJSONObject(0).getString("wallet");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        //userid.setText(Integer.toString(user_id));
        //username.setText(user_name);
        //balance.setText(wallet);
        mwallet.setRightDesc(wallet);
    }
}
