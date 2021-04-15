package com.example.A4_1155150604;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class ChatroomActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatroom);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("IEMS5722");
        setSupportActionBar(toolbar);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chatroom, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        /*if (id == R.id.action_logout) {
            ChatroomActivityFragment.setLoginInformation(false, "", 0);
            Toast.makeText(this, "You have logged out", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }*/
        switch (id){
            case R.id.action_logout:
                ChatroomActivityFragment.setLoginInformation(false, "", 0);
                Toast.makeText(this, "You have logged out", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                break;
            case R.id.action_add_friend:
                Log.d("ChatroomActivaty", "add_friend button clicked");
                ChatroomActivityFragment fragment = (ChatroomActivityFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_main);
                fragment.creatNewDialog();
                break;
            case R.id.Gen_QRcode:
                Log.d("ChatroomActivaty", "My QR Code Generating");
                ChatroomActivityFragment fragment_qrcode = (ChatroomActivityFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_main);
                fragment_qrcode.generateQrCode();
                break;
            case R.id.scan_QR_Code:
                Log.d("ChatroomActivaty", "Scan QR Code Generating");
                ChatroomActivityFragment fragment_scan_qrcode = (ChatroomActivityFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_main);
                fragment_scan_qrcode.scanCode();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void finish() {
        moveTaskToBack(true);
    }

}