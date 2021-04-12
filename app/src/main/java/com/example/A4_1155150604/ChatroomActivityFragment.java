package com.example.A4_1155150604;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

public class ChatroomActivityFragment extends Fragment {
    private ArrayList<Chatroom> chatrooms;
    private ChatroomAdapter chatroomAdapter;
    private ListView lv;
    private static String user_name = "";
    private static int user_id = 0;
    private static boolean hasLoggedIn = false;

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

        lv = view.findViewById(R.id.listview_main);
        chatrooms = new ArrayList<Chatroom>();
        chatroomAdapter = new ChatroomAdapter(getActivity(), R.layout.listview_chatroom_item, chatrooms);
        lv.setAdapter(chatroomAdapter);

        FetchChatroomListTask task = new FetchChatroomListTask(chatrooms, chatroomAdapter, lv, getContext());
        task.execute("http://3.17.158.90/api/a3/get_chatrooms");

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

}