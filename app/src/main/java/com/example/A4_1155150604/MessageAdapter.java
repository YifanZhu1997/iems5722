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
            holder.redEnvelopeLayout = view.findViewById(R.id.red_envelope_layout);
            holder.tv_receive = view.findViewById(R.id.tv_receive);
            holder.tv_send = view.findViewById(R.id.tv_send);
            holder.extra_info = view.findViewById(R.id.extra_info);
            view.setTag(holder);
        } else {
            view = convertView;
            holder = (ViewHolder) view.getTag();
        }
        Message msg = getItem(position);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        if (msg.content.length() > 22 && msg.content.substring(0, 22).equals("!@#$(RED_ENVELOPE)!@#$")) {
            holder.redEnvelopeLayout.setVisibility(View.VISIBLE);
            holder.rightLayout.setVisibility(View.GONE);
            holder.leftLayout.setVisibility(View.GONE);
            holder.extra_info.setVisibility(View.VISIBLE);
            holder.extra_info.setText(String.format("Red envelope from %s\n%s", msg.user_name, msg.time));
            params.gravity = Gravity.CENTER;
            holder.extra_info.setLayoutParams(params);
        } else if (msg.type == Message.TYPE_RECEIVE) {
            holder.leftLayout.setVisibility(View.VISIBLE);
            holder.rightLayout.setVisibility(View.GONE);
            holder.redEnvelopeLayout.setVisibility(View.GONE);
            holder.extra_info.setVisibility(View.VISIBLE);
            holder.tv_receive.setText(String.format("User: %s\n%s", msg.user_name, msg.content));
            holder.extra_info.setText(msg.time);
            params.gravity = Gravity.LEFT;
            holder.extra_info.setLayoutParams(params);
        } else if (msg.type == Message.TYPE_SEND) {
            holder.rightLayout.setVisibility(View.VISIBLE);
            holder.leftLayout.setVisibility(View.GONE);
            holder.redEnvelopeLayout.setVisibility(View.GONE);
            holder.extra_info.setVisibility(View.VISIBLE);
            holder.tv_send.setText(String.format("User: %s\n%s", msg.user_name, msg.content));
            holder.extra_info.setText(msg.time);
            params.gravity = Gravity.RIGHT;
            holder.extra_info.setLayoutParams(params);
        }
        return view;
    }

    class ViewHolder {
        LinearLayout leftLayout;
        LinearLayout rightLayout;
        LinearLayout redEnvelopeLayout;
        TextView tv_receive;
        TextView tv_send;
        TextView extra_info;
    }
}