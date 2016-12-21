package com.kyf.futurespace.smarthome;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.appwidget.AppWidgetProviderInfo;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;

import com.example.smarthome.R;

import static android.content.ContentValues.TAG;

/**
 * Created by kangy on 2016/11/22.
 */

    public class AppWidgetProviderToStart extends AppWidgetProvider {
    private static final int BUTTON_SHOW = 1;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.hasCategory(Intent.CATEGORY_ALTERNATIVE)) {
            // “按钮点击”广播
            Uri data = intent.getData();
            int buttonId = Integer.parseInt(data.getSchemeSpecificPart());
            if (buttonId == BUTTON_SHOW) {
                Intent acIntent=new Intent(context,MainActivity.class);
                acIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(acIntent);
                Log.d(TAG, "Button wifi clicked");
            }
        }
        super.onReceive(context, intent);

    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);

    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
        RemoteViews remoteView = new RemoteViews(context.getPackageName(), R.layout.appwidget);

        // 设置点击按钮对应的PendingIntent：即点击按钮时，发送广播。
        remoteView.setOnClickPendingIntent(R.id.btn_appwidget, getPendingIntent(context,
                BUTTON_SHOW));

        // 更新 widget
        appWidgetManager.updateAppWidget(appWidgetId, remoteView);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
    }

    private PendingIntent getPendingIntent(Context context, int buttonId) {
        Intent intent = new Intent();
        intent.setClass(context, AppWidgetProviderToStart.class);
        intent.addCategory(Intent.CATEGORY_ALTERNATIVE);
        intent.setData(Uri.parse("custom:" + buttonId));
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);
        return pi;
    }
}
