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
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.warrior.hangsu.administrator.mangaeasywatch.listener.OnEditResultListener;
import com.warrior.hangsu.administrator.mangaeasywatch.widght.EditDialog;

public abstract class BaseActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        ToastUtil.tipShort(this, "previous activity:" + this.getLocalClassName());
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
    protected void showBaseEditDialog(OnEditResultListener listener){
        EditDialog dialog=new EditDialog(this);
        dialog.setOnEditResultListener(listener);
        dialog.show();
    }
}
