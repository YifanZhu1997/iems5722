package com.example.A4_1155150604;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;


public class ChatActivityFragment extends Fragment {
    private IdNamePage idNamePage;
    private static String getMessageURL = "http://3.17.158.90/api/a3/get_messages?chatroom_id=%d&page=%d";
    private static String sendMessageURL = "http://3.17.158.90/api/a3/send_message";
    private static String socketURL = "http://3.17.158.90:8001";
    private static String lastPage = "Already the last page";
    private static String input_empty_alert = "Please enter a text";
    private ArrayList<Message> messages;
    private EditText et_input;
    private MessageAdapter messageAdapter;
    private ListView lv;
    private Socket socket;
    private static int notify_id = 1;

    public class ChatActivityHandler extends Handler {
        @Override
        public void handleMessage(@NonNull android.os.Message msg) {
            if (msg.what == ACTION_CONNECT_ERROR) {
                Toast.makeText(getActivity(), R.string.socket_connect_error, Toast.LENGTH_SHORT).show();
            } else if (msg.what == ACTION_DISCONNECTED) {
                Toast.makeText(getActivity(), R.string.socket_disconnected, Toast.LENGTH_SHORT).show();
            } else if (msg.what == ACTION_USER_JOINED) {
                Toast.makeText(getActivity(), R.string.socket_user_joined, Toast.LENGTH_SHORT).show();
            } else if (msg.what == ACTION_NEW_MESSAGE) {
                JSONObject json = (JSONObject) msg.obj;
                Message new_message = null;
                try {
                    new_message = new Message(json.getInt("id"), json.getString("message"), json.getInt("user_id"), json.getString("name"), Message.TYPE_RECEIVE, json.getString("message_time"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (new_message != null) {
                    if (new_message.user_id == idNamePage.user_id) {
                        new_message.type = Message.TYPE_SEND;
                        messages.add(new_message);
                        messageAdapter.notifyDataSetChanged();
                        lv.setSelection(messages.size() - 1);
                    } else {
                        messages.add(new_message);
                        messageAdapter.notifyDataSetChanged();
                        if (isBackground()) {
                            lv.setSelection(messages.size() - 1);
                            sendNotification(new_message);
                        }
                    }

                }
            }
        }
    }

    private static final int ACTION_CONNECT_ERROR = 1;
    private static final int ACTION_USER_JOINED = 2;
    private static final int ACTION_DISCONNECTED = 3;
    private static final int ACTION_NEW_MESSAGE = 4;
    private ChatActivityHandler handler = new ChatActivityHandler();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            socket = IO.socket(socketURL);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        socket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
        socket.on(Socket.EVENT_CONNECT, onConnect);
        socket.on(Socket.EVENT_DISCONNECT, onDisconnect);
        socket.on("user joined", onUserJoined);
        socket.on("new message", onNewMessage);

        socket.connect();

        createNotificationChannel();
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        lv = view.findViewById(R.id.listview_chat);
        et_input = view.findViewById(R.id.et_input);
        messages = new ArrayList<Message>();
        messageAdapter = new MessageAdapter(getActivity(), R.layout.listview_chat_item, messages);
        lv.setAdapter(messageAdapter);

        fetchPage(idNamePage);

        view.findViewById(R.id.bt_send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = et_input.getText().toString();
                if (!content.isEmpty()) {
                    Message new_message = new Message(content, idNamePage.user_id, idNamePage.user_name);
                    et_input.setText("");

                    PostMessageTask task = new PostMessageTask(new_message, idNamePage, getContext());
                    task.execute(sendMessageURL);

                } else {
                    Toast.makeText(getActivity(), input_empty_alert, Toast.LENGTH_SHORT).show();
                }
            }
        });

        lv.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScroll(AbsListView view, int first, int visible, int total) {
            }

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL && lv.getFirstVisiblePosition() == 0) {
                    if (idNamePage.currentPage < idNamePage.totalPage) {
                        idNamePage.currentPage += 1;
                        fetchPage(idNamePage);
                    } else {
                        Toast.makeText(getActivity(), lastPage, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }

    @Override
    public void onDestroy() {
        socket.disconnect();
        socket.off();
        super.onDestroy();
    }

    private Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    android.os.Message message = handler.obtainMessage(ACTION_CONNECT_ERROR);
                    message.sendToTarget();
                }
            });
        }
    };

    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject json = new JSONObject();
                    try {
                        json.put("room", idNamePage.chatroomId);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    socket.emit("user join", json);
                }
            });
        }
    };

    private Emitter.Listener onUserJoined = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    android.os.Message message = handler.obtainMessage(ACTION_USER_JOINED);
                    message.sendToTarget();
                }
            });
        }
    };

    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject json = (JSONObject) args[0];
                    android.os.Message message = handler.obtainMessage(ACTION_NEW_MESSAGE, json);
                    message.sendToTarget();
                }
            });
        }
    };

    private Emitter.Listener onDisconnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    android.os.Message message = handler.obtainMessage(ACTION_DISCONNECTED);
                    message.sendToTarget();
                }
            });
        }
    };

    public void fetchPage(IdNamePage idNamePage) {
        FetchMessageListTask task = new FetchMessageListTask(messages, messageAdapter, lv, idNamePage, getContext());
        task.execute(getMessageURL);
    }

    public void refreshFirstPage() {
        messages.clear();
        idNamePage.currentPage = idNamePage.firstPage;
        fetchPage(idNamePage);
    }

    private void createNotificationChannel() {
        NotificationManager notificationManager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        String mChannelId = "channelID";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getContext().getString(R.string.app_name);
            NotificationChannel channel = new NotificationChannel(mChannelId, name, NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void sendNotification(Message message) {
        NotificationManager notificationManager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        String mChannelId = "channelID";

        Intent intent = new Intent(getContext(), ChatActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getContext(), 0, intent, 0);
        NotificationCompat.Builder bBuilder =
                new NotificationCompat.Builder(getContext(), mChannelId)
                        .setSmallIcon(R.drawable.cuhk)
                        .setContentTitle(idNamePage.chatroom_name)
                        .setContentText(message.user_name + ": " + message.content)
                        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true);

        Notification notification = bBuilder.build();
        notification.defaults |= Notification.DEFAULT_SOUND;
        notification.defaults |= Notification.DEFAULT_VIBRATE;

        notificationManager.notify(notify_id, notification);
        notify_id += 1;
    }

    private boolean isBackground() {
        ActivityManager activityManager = (ActivityManager) getContext()
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager
                .getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(getContext().getPackageName())) {
                if (appProcess.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    return true;
                } else {
                    return false;
                }
            }
        }
        return false;
    }

    public void onAttach(Activity activity) {
        super.onAttach(activity);
        idNamePage = new IdNamePage();
        idNamePage.chatroomId = ((ChatActivity) activity).getChatroomId();
        idNamePage.user_id = ((ChatActivity) activity).getUserId_();
        idNamePage.user_name = ((ChatActivity) activity).getUserName();
        idNamePage.chatroom_name = ((ChatActivity) activity).getChatroomName();
    }

}