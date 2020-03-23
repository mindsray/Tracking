package com.example.tracking;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.ArrayList;



public class MessageAdapter  extends  BaseAdapter{
    Context context;

    //    ArrayList<String> cusName;
    ArrayList<String> messages;

    public  MessageAdapter(Context context,ArrayList<String> messages) {
        this.context = context;
        this.messages = messages;
    }

    public int getCount(){
        return messages.size();

    }
    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if(convertView == null)
            convertView = inflater.inflate(R.layout.listview,parent,false);
        TextView textshow = convertView.findViewById(R.id.textshowMessage);
        textshow.setText(messages.get(position));


        return convertView;
    }

}
