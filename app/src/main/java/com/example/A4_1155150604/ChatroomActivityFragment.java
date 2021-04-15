package com.example.A4_1155150604;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.zxing.integration.android.IntentIntegrator;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChatroomActivityFragment extends Fragment {
    private ArrayList<Chatroom> chatrooms;
    private ListView lv;
    private ChatroomAdapter chatroomAdapter;
    private static String user_name = "";
    private static int user_id = 0;
    private static boolean hasLoggedIn = false;
    private ArrayList<String> wallet = new ArrayList<>();
    private LinearLayout layout;
    private ImageView mHBack;
    private ImageView mHHead;
    private ImageView mUserLine;
    private TextView mUserName;
    private TextView mID;

    private ItemView mNickName;
    private ItemView mWallet;


    private String URL = "http://3.17.158.90/api/a3/";
    private String GET_USER_URL = URL + "get_user";
    private String CHECK_FRIEND_URL = URL + "check_friend";
    private String BEFRIEND_URL = URL + "add_friends";

    private  AlertDialog.Builder dialogBuilder;
    private  AlertDialog dialog;
    private  EditText id_search_box;
    private  Button friend_search;
    private  LinearLayout friend_display;
    private  TextView friend_id_display;
    private  TextView friend_name_display;
    private  Button add_friend;
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chatroom, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // LinearLayout room_layout = (LinearLayout)view.findViewById(R.id.account_layout);
        //TextView userid = (TextView)view.findViewById(R.id.user_id);
        //TextView username = (TextView)view.findViewById(R.id.user_name);
        //TextView balance = (TextView)view.findViewById(R.id.user_wallet);
        layout = (LinearLayout) view.findViewById(R.id.account_layout);
        mHBack = (ImageView) view.findViewById(R.id.h_back);
        mHHead = (ImageView) view.findViewById(R.id.h_head);
        mUserLine = (ImageView) view.findViewById(R.id.user_line);
        mUserName = (TextView) view.findViewById(R.id.user_name);
        mID = (TextView) view.findViewById(R.id.user_id);
        //下面item控件
        mNickName = (ItemView) view.findViewById(R.id.nickName);
        mWallet = (ItemView) view.findViewById(R.id.wallet1);

        layout.setVisibility(View.GONE);
        mHBack.setVisibility(View.GONE);
        mHHead.setVisibility(View.GONE);
        mUserLine.setVisibility(View.GONE);
        mUserName.setVisibility(View.GONE);
        //mUserName.setText();
        mID.setVisibility(View.GONE);
        mNickName.setVisibility(View.GONE);
        mWallet.setVisibility(View.GONE);

        lv = view.findViewById(R.id.listview_main);

        chatrooms = new ArrayList<Chatroom>();
        chatroomAdapter = new ChatroomAdapter(getActivity(), R.layout.listview_chatroom_item, chatrooms);
        lv.setAdapter(chatroomAdapter);
        FetchChatroomListTask task = new FetchChatroomListTask(chatrooms, chatroomAdapter, lv, getContext());
        task.execute("http://3.17.158.90/api/a3/get_chatrooms");

        BottomNavigationView navigation = (BottomNavigationView) view.findViewById(R.id.bottom_navigation);
        navigation.bringToFront();
        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.public_chatrooms:
                        chatrooms.clear();
                        chatroomAdapter.notifyDataSetChanged();
                        layout.setVisibility(View.GONE);

                        mHBack.setVisibility(View.GONE);
                        mHHead.setVisibility(View.GONE);
                        mUserLine.setVisibility(View.GONE);
                        mUserName.setVisibility(View.GONE);
                        mID.setVisibility(View.GONE);
                        mNickName.setVisibility(View.GONE);
                        mWallet.setVisibility(View.GONE);


                        FetchChatroomListTask task = new FetchChatroomListTask(chatrooms, chatroomAdapter, lv, getContext());
                        task.execute("http://3.17.158.90/api/a3/get_chatrooms");
                        return true;
                    case R.id.friends:
                        chatrooms.clear();
                        chatroomAdapter.notifyDataSetChanged();
                        layout.setVisibility(View.GONE);
                        mHBack.setVisibility(View.GONE);
                        mHHead.setVisibility(View.GONE);
                        mUserLine.setVisibility(View.GONE);
                        mUserName.setVisibility(View.GONE);
                        mID.setVisibility(View.GONE);
                        mNickName.setVisibility(View.GONE);
                        mWallet.setVisibility(View.GONE);


                        FetchFriendsTask task2 = new FetchFriendsTask(chatrooms, chatroomAdapter, lv, getContext());
                        task2.execute("http://3.17.158.90/api/a3/get_friends" + "?user_id=" + user_id);

                        return true;
                    case R.id.wallet:
                        chatrooms.clear();
                        chatroomAdapter.notifyDataSetChanged();
                        layout.setVisibility(View.VISIBLE);
                        mHBack.setVisibility(View.VISIBLE);
                        mHHead.setVisibility(View.VISIBLE);
                        mUserLine.setVisibility(View.VISIBLE);
                        mUserName.setVisibility(View.VISIBLE);
                        mID.setVisibility(View.VISIBLE);
                        mNickName.setVisibility(View.VISIBLE);
                        mWallet.setVisibility(View.VISIBLE);


                        FetchWallet task3 = new FetchWallet(getContext(), user_id, wallet, mWallet);//,userid,username);//,balance);
                        task3.execute("http://3.17.158.90/api/a3/get_wallet?user_id=%d");
                        //setContentView(R.layout.activity_main);
                        setData();
                        mUserName.setText(user_name);
                        mID.setText(Integer.toString(user_id));
                        mNickName.setRightDesc(user_name);
                        return true;

                }
                return false;
            }
        });


        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            //parent 代表listView View 代表 被点击的列表项 position 代表第几个 id 代表列表编号
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (hasLoggedIn) {
                    String chatroomName = chatrooms.get((int) id).name;
                    int chatroomId = chatrooms.get((int) id).id;
                    Intent intent = new Intent(getActivity(), ChatActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("chatroom_name", chatroomName);
                    bundle.putString("user_name", user_name);
                    bundle.putInt("chatroom_id", chatroomId);
                    bundle.putInt("user_id", user_id);
                    intent.putExtra("data", bundle);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    public static void setLoginInformation(boolean status, String userName, int userId) {
        hasLoggedIn = status;
        user_name = userName;
        user_id = userId;
    }


    private void setData() {
        Glide.with(getActivity()).load(R.drawable.head)
                .bitmapTransform(new BlurTransformation(getActivity(), 25), new CenterCrop(getActivity()))
                .into(mHBack);
        Glide.with(getActivity()).load(R.drawable.head)
                .bitmapTransform(new CropCircleTransformation(getActivity()))
                .into(mHHead);
    }

    /**
     *Add Friend action
     * @
     **/
    public void creatNewDialog(){
        dialogBuilder = new AlertDialog.Builder(getActivity());
        final View friendSearchView = getActivity().getLayoutInflater().inflate(R.layout.dialog_add_friend, null);
        id_search_box = (EditText) friendSearchView.findViewById(R.id.id_search_box);
        friend_search = (Button) friendSearchView.findViewById(R.id.friend_search);
        friend_display = (LinearLayout) friendSearchView.findViewById(R.id.friend_display);
        friend_name_display = (TextView) friendSearchView.findViewById(R.id.friend_name_display);
        friend_id_display = (TextView) friendSearchView.findViewById(R.id.friend_id_display);

        add_friend = (Button) friendSearchView.findViewById(R.id.add_friend);
        dialogBuilder.setView(friendSearchView);
        dialog = dialogBuilder.create();
        dialog.show();

        friend_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String friend_id = id_search_box.getText().toString().trim();
                searchFriend(friend_id);
            }
        });
        add_friend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("ChatRoomFragment", "add as friend clicked");
                if (!add_friend.getText().toString().trim().equalsIgnoreCase("Friended")) {
                    String friend_id = friend_id_display.getText().toString().trim();
                    String friend_name = friend_name_display.getText().toString().trim();
                    add_friend.setEnabled(true);
                    addFriend(Integer.toString(user_id), friend_id, user_name, friend_name);
                }
                else{
                    add_friend.setEnabled(false);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(),
                                    "Already added as Friend ", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

    }


    public  void searchFriend(final String friend_id){
        if (friend_id.isEmpty()){
            Toast.makeText(getActivity(),
                    "Please Enter the User ID", Toast.LENGTH_SHORT).show();
        }
        else {

            Map<String, String> map = new HashMap<>();
            map.put("user_id", friend_id);
            Utils.sendOkHttpGetRequest(GET_USER_URL, map, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(),
                                    "Server Error, Please try again", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    try {
                        String responseData = response.body().string();
                        final JSONObject jsonObject = new JSONObject(responseData);
                        //final JSONObject jsonObject = new JSONObject(response.body().string());
                        Log.d("Search riend", "get user response: " + jsonObject.toString());
                        if (jsonObject.getString("status").equalsIgnoreCase("OK")) {

                            final JSONArray data = jsonObject.getJSONArray("data");

                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        Log.d("Searched", "Display User Info");
                                        /**
                                         * jasonarray
                                         * */
                                        for(int i = 0; i < data.length(); i++)
                                        {
                                            friend_name_display.setText(data.getJSONObject(i).getString("name"));
                                            friend_id_display.setText(data.getJSONObject(i).getString("id"));
                                            //friend_id_display.setVisibility(View.VISIBLE);
                                            //friend_name_display.setVisibility(View.VISIBLE);
                                            checkFriend(Integer.toString(user_id), data.getJSONObject(i).getString("id"));
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                            });

                        } else if (jsonObject.getString("status").equalsIgnoreCase("ERROR")) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        friend_display.setVisibility(View.GONE);
                                        Toast.makeText(getActivity(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                                        Log.d("Search Friend", "NOT FOUND THE USER !!!");

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

            });
        }

    }
    /**
     * Check befriend or not
     * */
    private void checkFriend(String user_id, String friend_id) {
        Map<String, String> map = new HashMap<>();
        map.put("user_id", user_id);
        map.put("friend_id", friend_id);
        if(user_id.equals(friend_id)){
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getActivity(),
                            "Cannot add yourself as friends", Toast.LENGTH_SHORT).show();
                }

            });
            add_friend.setEnabled(false);
            return;
        }
        Utils.sendOkHttpGetRequest(CHECK_FRIEND_URL, map, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(),
                                "Server Error", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    String responseData = response.body().string();
                    final JSONObject jsonObject = new JSONObject(responseData);
                    //final JSONObject jsonObject = new JSONObject(response.body().string());
                    Log.d("Check Relation","befriended: " + jsonObject.toString());
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                if (jsonObject.getString("status").equalsIgnoreCase("OK")) {
                                    add_friend.setText("Friended");
                                    add_friend.setEnabled(false);

                                } else {
                                    add_friend.setEnabled(true);
                                    add_friend.setText("ADD AS FRIEND");
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            friend_display.setVisibility(View.VISIBLE);
                        }
                    });


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
    /**
     ** Add as friend post
     **/
    private void addFriend(final String user_id, final String friend_id, final String name, final String friend_name) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    RequestBody requestBody = new FormBody.Builder()
                            .add("user_id", user_id)
                            .add("friend_id", friend_id)
                            .build();
                    Request request = new Request.Builder()
                            .url(BEFRIEND_URL)
                            .post(requestBody)
                            .build();
                    Response response = null;
                    response = client.newCall(request).execute();
                    String result = response.body().string();
                    Log.d("Befriended", result);
                    if (result != null) {
                        final JSONObject jsonObject = new JSONObject(result);
                        if (jsonObject.getString("status").equalsIgnoreCase("OK")) {
                            //final JSONObject data = jsonObject.getJSONObject("data");
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    add_friend.setText("Friended");
                                    add_friend.setEnabled(false);
                                    //chatroomList.add(data.getString("name"));
                                    //chat_list.add(data.getString("id"));
                                    //arrayAdapter.notifyDataSetChanged();
                                }
                            });
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * @Function: generate qr code
     */
    public void generateQrCode() {
        dialogBuilder = new AlertDialog.Builder(getActivity());
        final View MyqrcodeView = getLayoutInflater().inflate(R.layout.fragment_chatroom_genqrcode, null);
        ImageView qen_QR_Code = (ImageView)MyqrcodeView.findViewById(R.id.qrcode_image);
        String gen_QR_Code_url = "http://api.qrserver.com/v1/create-qr-code/?data=" + user_id;
        Picasso.get().load(gen_QR_Code_url).resize(600,600).into(qen_QR_Code);
        dialogBuilder.setView(MyqrcodeView);
        dialog = dialogBuilder.create();
        dialog.show();
    }
    /****
     * @Function: Scan QR code
     * */
    public void scanCode() {
        IntentIntegrator integrator = new IntentIntegrator(getActivity());
        integrator.setCaptureActivity(CaptureActivity.class);
        integrator.setOrientationLocked(false);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        integrator.setPrompt("Scanning Code");
        integrator.initiateScan();
    }

}