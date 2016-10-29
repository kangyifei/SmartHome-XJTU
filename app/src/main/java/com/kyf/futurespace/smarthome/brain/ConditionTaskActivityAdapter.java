package com.kyf.futurespace.smarthome.brain;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.smarthome.R;
import com.kyf.futurespace.smarthome.utils.Task;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kyf on 2016/8/20 0020.
 */
public class ConditionTaskActivityAdapter extends BaseAdapter {
    List<Task> tasks = new ArrayList<>();
    Context mContext;
    Handler handler;
    final String TAG = "ConditionTaskActivity";

    public ConditionTaskActivityAdapter(List<Task> tasks, Context mContext, Handler handler) {
        this.tasks = tasks;
        this.mContext = mContext;
        this.handler = handler;
    }

    @Override
    public int getCount() {
        return tasks.size();
    }

    @Override
    public Object getItem(int i) {
        return tasks.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        ViewHolder holder;
        if (view == null) {
            view = inflater.inflate(R.layout.item_conditiontask_list,
                    viewGroup, false);
            holder = new ViewHolder();
            holder.condition = (TextView) view.findViewById(R.id.conditionTask_conditon);
            holder.order = (TextView) view.findViewById(R.id.conditionTask_order);
            holder.btnDelete = (Button) view.findViewById(R.id.conditionTask_btn);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        holder.condition.setText(getOrderString(tasks.get(i).getCondition())+"的时候");
        Log.w(TAG, "condition:" + tasks.get(i).getCondition());
        String orderStr = "";
        for (String s : tasks.get(i).getOrder()) {
            orderStr = orderStr + "," + getOrderString(s);
        }
        orderStr.replaceFirst(",", "");
        holder.order.setText(orderStr);
        Log.w(TAG, "orderStr" + orderStr);
        holder.btnDelete.setOnClickListener(new MyClickListener(i));
        return view;
    }

    private static class ViewHolder {
        TextView condition;
        TextView order;
        Button btnDelete;
    }

    private class MyClickListener implements View.OnClickListener {
        int mPosition;

        public MyClickListener(int position) {
            mPosition = position;
        }

        @Override
        public void onClick(View view) {
            Message msg = new Message();
            msg.what = 1;
            msg.obj = tasks.get(mPosition).getCondition();
            msg.arg1 = mPosition;
            handler.sendMessage(msg);
        }
    }

    private String getOrderString(String order) {
        String str = "";
        switch (order) {
            case "!BRLI0001":
                str = "关灯";
                break;
            case "!BRLI0000":
                str = "开灯";
                break;
            case "!BRDO0001":
                str = "关门";
                break;
            case "!BRDO0000":
                str = "开门";
                break;
            case "!BRAC0001":
                str = "关空调";
                break;
            case "!BRAC0000":
                str = "开空调";
                break;
            case "!BRWS0000":
                str = "开窗户";
                break;
            case "!BRWS0001":
                str = "关窗户";
                break;
            case "!CSRS0001":
                str = "下雨";
                break;
        }
        return str;
    }
}

