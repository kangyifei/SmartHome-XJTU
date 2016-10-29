package com.kyf.futurespace.smarthome.brain;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ListView;

import com.example.smarthome.R;
import com.kyf.futurespace.smarthome.utils.Data;
import com.kyf.futurespace.smarthome.utils.Task;

import java.util.ArrayList;

/**
 * Created by kyf on 2016/8/20 0020.
 */
public class ConditionTaskActivity extends Activity {
    Data data = Data.getInstance();
    ArrayList<Task> tasks = data.getTaskWaitingList();
    ListView listView = null;
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            byte i = (byte) msg.what;
            switch (i) {
                case 1:
                    data.deleteTaskWaiting((String) msg.obj);
                    Log.w("TasksWaitingNUM", "" + data.getTasksWaitingNum());
                    tasks.remove(msg.arg1);
                    conditionTaskActivityAdapter.notifyDataSetChanged();

            }
        }
    };
    ConditionTaskActivityAdapter conditionTaskActivityAdapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.conditiontask_list);
        listView = (ListView) findViewById(R.id.conditionTask_list);
        conditionTaskActivityAdapter = new ConditionTaskActivityAdapter(tasks, this, mHandler);
        listView.setAdapter(conditionTaskActivityAdapter);
    }
}
