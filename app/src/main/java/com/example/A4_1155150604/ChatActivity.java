package com.example.A4_1155150604;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

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
            case R.id.action_refresh:
                ChatActivityFragment fragment = (ChatActivityFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_chat);
                fragment.refreshFirstPage();
                break;
            case R.id.action_red_envelope:
                setRedEnvelopeInformation();
                break;
            default:

        }

        return super.onOptionsItemSelected(item);
    }

    private void setRedEnvelopeInformation() {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        final EditText totalMoney = new EditText(this);
        totalMoney.setHint("Total money");
        totalMoney.setGravity(Gravity.CENTER);
        layout.addView(totalMoney);

        final EditText number = new EditText(this);
        number.setHint("Number of envelopes");
        number.setGravity(Gravity.CENTER);
        layout.addView(number);

        AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);
        builder.setTitle("Red Envelope Information").setIcon(R.mipmap.ic_red_envelope_foreground).setView(layout)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String totalMoney_ = totalMoney.getText().toString();
                String number_ = number.getText().toString();
                if (checkTotalMoneyFormat(totalMoney_) == true && checkNumberFormat(number_) == true && checkMoneyNumberRelationship(totalMoney_, number_) == true) {
                }
            }
        });
        builder.show();
    }

    private boolean checkTotalMoneyFormat(String money) {
        if (money.equals("")) {
            Toast.makeText(this, "Please enter the money", Toast.LENGTH_SHORT);
            return false;
        }

        try {
            double doubleMoney = Double.valueOf(money);
            if (doubleMoney <= 0 || doubleMoney > 1000) {
                Toast.makeText(this, "The money must be larger than 0 and smaller than 1000", Toast.LENGTH_SHORT).show();
                return false;
            } else {
                int dcimalDigits = 0;
                int indexOf = money.indexOf(".");
                if (indexOf > 0) {
                    dcimalDigits = money.length() - 1 - indexOf;
                }

                //check if the digit number after . is more than 2
                if (dcimalDigits > 2) {
                    Toast.makeText(this, "Money format error", Toast.LENGTH_SHORT).show();
                    return false;
                } else {
                    return true;
                }
            }
        } catch (Exception e) {
            Toast.makeText(this, "Money format error", Toast.LENGTH_SHORT).show();
            return false;
        }

    }

    private boolean checkNumberFormat(String number) {
        try {
            int intNumber = Integer.valueOf(number);
            if (intNumber <= 0 || intNumber > 100) {
                Toast.makeText(this, "The number must be between 1 to 100", Toast.LENGTH_SHORT).show();
                return false;
            } else {
                return true;
            }
        } catch (Exception e) {
            Toast.makeText(this, "Number format error", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private boolean checkMoneyNumberRelationship(String money, String number) {
        double average_num = Double.valueOf(money) / Integer.valueOf(number);
        if (average_num < 0.01) {
            Toast.makeText(this, "Error (Not everyone can share a portion of money)", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
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


