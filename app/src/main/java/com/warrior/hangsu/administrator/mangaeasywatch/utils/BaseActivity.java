package com.warrior.hangsu.administrator.mangaeasywatch.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.warrior.hangsu.administrator.mangaeasywatch.listener.OnEditResultListener;
import com.warrior.hangsu.administrator.mangaeasywatch.widght.EditDialog;

import java.util.Date;

public abstract class BaseActivity extends FragmentActivity {
    private long createTime, startStayTime, firstResumeTime, endStayTime;
    protected final String PREVIOUS_ACTIVITY_NAME_KEY = "previous_activity_name_key";
    protected String previousActivityName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createTime = System.currentTimeMillis();
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        Intent intent = getIntent();
        if (null != intent) {
            previousActivityName = intent.getStringExtra(PREVIOUS_ACTIVITY_NAME_KEY);
            ToastUtil.tipShort(this, "上一个" + previousActivityName);
        }
    }

    @Override
    public void startActivity(Intent intent) {
//        ToastUtil.tipShort(this, "" + this.getLocalClassName());
        intent.putExtra(PREVIOUS_ACTIVITY_NAME_KEY, this.getLocalClassName().toString());
        super.startActivity(intent);
    }

    protected void showBaseDialog(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();
    }

    protected void showBaseEditDialog(OnEditResultListener listener) {
        EditDialog dialog = new EditDialog(this);
        dialog.setOnEditResultListener(listener);
        dialog.show();
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (firstResumeTime == 0) {
            firstResumeTime = System.currentTimeMillis();
        }
        startStayTime = System.currentTimeMillis();
    }

    @Override
    protected void onPause() {
        super.onPause();
        endStayTime = System.currentTimeMillis();
    }

    protected long getCreatePeriod() {
        return firstResumeTime - createTime;
    }

    protected long getStayPeriod() {
        return endStayTime - startStayTime;
    }

    public String getPreviousActivityName() {
        return previousActivityName;
    }
}
