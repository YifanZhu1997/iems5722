package com.example.A4_1155150604;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;


public class LoginTask extends AsyncTask<String, Void, Void> {
    private Context context;
    private static ArrayList<String> para_names = new ArrayList<String>(Arrays.asList("name","password"));
    private ArrayList<String> para_values = new ArrayList<String>();
    private String result;
    private String message;
    private String name;
    private String password;
    int user_id=0;

    public LoginTask(String name, String password, Context context) {
        this.name=name;
        this.password=password;
        this.context = context;
    }

    @Override
    protected Void doInBackground(String... strings) {
        this.para_values.add(name);
        this.para_values.add(password);

        String json_result = Utils.postHTTPRequest(strings[0], para_names, para_values);
        if (json_result.equals("")) {
            return null;
        }

        try {
            JSONObject json = new JSONObject(json_result);
            String status = json.getString("status");
            if (status.equals("OK")){
                this.result="OK";
                this.user_id=json.getInt("user_id");
            } else if (status.equals("ERROR")){
                this.result="ERROR";
                this.message=json.getString("message");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        if(this.result==null){
            Toast.makeText(context, "Login failed", Toast.LENGTH_SHORT).show();
        } else if(this.result.equals("OK")){
            ChatroomActivityFragment.setLoginInformation(true, name, user_id);
            Toast.makeText(context, "You have successfully logged in as " + name, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(context, ChatroomActivity.class);
            context.startActivity(intent);
        } else if(this.result.equals("ERROR")){
            Toast.makeText(context, this.message, Toast.LENGTH_SHORT).show();
        }
    }
}
