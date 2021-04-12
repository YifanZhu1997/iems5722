package com.example.A4_1155150604;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;


public class MessageAdapter extends ArrayAdapter<Message> {
    private int resourceId;

    public MessageAdapter(Context context, int textViewResourceId, ArrayList<Message> objects) {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            view = LayoutInflater.from(getContext()).inflate(resourceId, null);
            holder.leftLayout = view.findViewById(R.id.left_layout);
            holder.rightLayout = view.findViewById(R.id.right_layout);
            holder.tv_receive = view.findViewById(R.id.tv_receive);
            holder.tv_send = view.findViewById(R.id.tv_send);
            holder.tv_time = view.findViewById(R.id.tv_time);
            view.setTag(holder);
        } else {
            view = convertView;
            holder = (ViewHolder) view.getTag();
        }
        Message msg = getItem(position);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        if (msg.type == Message.TYPE_RECEIVE) {
            holder.leftLayout.setVisibility(View.VISIBLE);
            holder.rightLayout.setVisibility(View.GONE);
            holder.tv_time.setVisibility(View.VISIBLE);
            holder.tv_receive.setText(String.format("User: %s\n%s", msg.user_name, msg.content));
            holder.tv_time.setText(msg.time);
            params.gravity = Gravity.LEFT;
            holder.tv_time.setLayoutParams(params);
        } else if (msg.type == Message.TYPE_SEND) {
            holder.rightLayout.setVisibility(View.VISIBLE);
            holder.leftLayout.setVisibility(View.GONE);
            holder.tv_time.setVisibility(View.VISIBLE);
            holder.tv_send.setText(String.format("User: %s\n%s", msg.user_name, msg.content));
            holder.tv_time.setText(msg.time);
            params.gravity = Gravity.RIGHT;
            holder.tv_time.setLayoutParams(params);
        }
        return view;
    }

    class ViewHolder {
        LinearLayout leftLayout;
        LinearLayout rightLayout;
        TextView tv_receive;
        TextView tv_send;
        TextView tv_time;
    }
}