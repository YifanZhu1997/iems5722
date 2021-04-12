package com.example.A4_1155150604;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class ChatActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getIntent().getBundleExtra("data").getString("chatroom_name"));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chat, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_exit:
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                System.exit(0);
                return true;
            case R.id.action_refresh:
                ChatActivityFragment fragment = (ChatActivityFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_chat);
                fragment.refreshFirstPage();
                break;
            default:
        }

        return super.onOptionsItemSelected(item);
    }

    public String getChatroomName() {
        return getIntent().getBundleExtra("data").getString("chatroom_name");
    }

    public int getChatroomId() {
        return getIntent().getBundleExtra("data").getInt("chatroom_id");
    }

    public int getUserId_() {
        return getIntent().getBundleExtra("data").getInt("user_id");
    }

    public String getUserName() {
        return getIntent().getBundleExtra("data").getString("user_name");
    }

}


