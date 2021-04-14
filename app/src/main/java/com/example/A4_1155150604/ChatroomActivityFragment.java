package com.example.A4_1155150604;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

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


}