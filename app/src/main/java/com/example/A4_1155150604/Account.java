package com.example.A4_1155150604;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.widget.TextView;



public class Account extends AppCompatActivity {
    private String get_userid(){
        Intent intent = getIntent();
        String user_id = intent.getStringExtra("user_id");
        return user_id;
    }
    private String get_username(){
        Intent intent = getIntent();
        String user_name = intent.getStringExtra("user_name");
        return user_name;
    }
    private String get_wallet(){
        Intent intent = getIntent();
        String wallet = intent.getStringExtra("wallet");
        return wallet;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        setContentView(R.layout.activity_account);
       // ActionBar toolbar=getSupportActionBar();
        //Toolbar toolbar = findViewById(R.id.toolbar);
        //toolbar.setTitle("Account Information");
        //setSupportActionBar(toolbar);
        //toolbar.setDisplayHomeAsUpEnabled(true);

        TextView tv1 = findViewById(R.id.account_name);
        TextView tv2 = findViewById(R.id.account_id);
        TextView tv3 = findViewById(R.id.account_wallet);

        tv1.setText(get_username());
        tv2.setText(get_userid());
        tv3.setText(get_wallet());



    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        getMenuInflater().inflate(R.menu.menu_chatroom, menu);
        return true;
    }
}