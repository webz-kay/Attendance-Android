package com.example.nanoatt;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomAdapter extends BaseAdapter {
    private  Context context;
    private ArrayList<Item> items;
    LayoutInflater inflater;

    public CustomAdapter(Context context, ArrayList<Item> items) {
        this.context = context;
        this.items = items;
        this.inflater=(LayoutInflater.from(context));
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int i) {
        return items.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view=inflater.inflate(R.layout.list_item, null);
        TextView txtName = view.findViewById(R.id.txtName);
        TextView txtDate = view.findViewById(R.id.txtDate);
        TextView txtTimeIn = view.findViewById(R.id.txtTimeIn);
        TextView txtTimeOut = view.findViewById(R.id.txtTimeOut);
        Item item =items.get(i);
        txtName.setText(item.getUser());
        txtDate.setText(item.getDate());
        txtTimeIn.setText(item.getTime_in());
        txtTimeOut.setText(item.getTime_out());
        return view;
    }
}
