package com.example.A4_1155150604;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ChatroomAdapter extends ArrayAdapter<Chatroom> {
    private int resourceId;

    public ChatroomAdapter(Context context, int textViewResourceId, ArrayList<Chatroom> objects) {
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
            holder.chatroom = view.findViewById(R.id.chatroom_name);
            view.setTag(holder);
        } else {
            view = convertView;
            holder = (ViewHolder) view.getTag();
        }
        Chatroom cr = getItem(position);
        holder.chatroom.setVisibility(View.VISIBLE);
        holder.chatroom.setText(cr.name);
        return view;
    }

    class ViewHolder {
        TextView chatroom;
    }
}
