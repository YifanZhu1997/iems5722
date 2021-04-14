package com.example.A4_1155150604;

import android.content.Context;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class FetchWallet extends AsyncTask<String, Void, Void> {
    private static String emptyMessageList = "Server returns 0 messages";
    private int user_id;
    private Context context;
    private IdNamePage idNamePage;
    private ArrayList<String> wallet = new ArrayList<>();
    ItemView mwallet;
    boolean needDisplay = true;


    public FetchWallet(Context context, int user_id, ArrayList<String> wallet, ItemView mwallet) {

        this.context = context;
        this.user_id = user_id;
        this.wallet = wallet;
        this.mwallet = mwallet;
    }

    public FetchWallet(Context context, IdNamePage idNamePage) {
        this.context = context;
        this.idNamePage = idNamePage;
        this.user_id = idNamePage.user_id;
        this.needDisplay = false;
    }

    @Override
    protected Void doInBackground(String... strings) {
        String json_result = Utils.fetchPage(String.format(strings[0], user_id));
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
            wallet.clear();
            wallet.add(data.getJSONObject(0).getString("wallet"));
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
        if (needDisplay) {
            mwallet.setRightDesc(wallet.get(0));
        } else {
            idNamePage.balance = wallet.get(0);
        }
    }
}
