package com.kyf.futurespace.smarthome.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.example.smarthome.R;

import java.util.List;

/**
 * Created by kangy on 2016/12/22.
 */

public class ListAdapter extends BaseAdapter {

    private List<ListData> lists;
    private Context mContext;
    private RelativeLayout layout;

    public ListAdapter(List<ListData> lists,Context mContext) {

        this.lists = lists;
        this.mContext = mContext;
    }
    @Override
    public int getCount() {
        return lists.size();
    }

    @Override
    public Object getItem(int position) {
        return lists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        if(lists.get(position).getFlag() == ListData.RECEIVER){
            layout = (RelativeLayout) inflater.inflate(R.layout.appweight_leftitem, null);
        }
        if (lists.get(position).getFlag() == ListData.SEND) {
            layout = (RelativeLayout) inflater.inflate(R.layout.appweight_rightitem, null);
        }
        TextView tv = (TextView) layout.findViewById(R.id.tv);
        tv.setText(lists.get(position).getContent());
        return layout;
    }
}
