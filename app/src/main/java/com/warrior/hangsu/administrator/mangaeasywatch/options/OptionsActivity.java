package com.warrior.hangsu.administrator.mangaeasywatch.options;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.StaticLayout;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.warrior.hangsu.administrator.mangaeasywatch.R;
import com.warrior.hangsu.administrator.mangaeasywatch.statistics.StatisticsActivity;
import com.warrior.hangsu.administrator.mangaeasywatch.teach.TeachActivity;
import com.warrior.hangsu.administrator.mangaeasywatch.utils.BaseActivity;
import com.warrior.hangsu.administrator.mangaeasywatch.utils.Globle;
import com.warrior.hangsu.administrator.mangaeasywatch.utils.SharedPreferencesUtils;
import com.warrior.hangsu.administrator.mangaeasywatch.utils.ToastUtil;
import com.warrior.hangsu.administrator.mangaeasywatch.utils.TopBar;

/**
 * Created by Administrator on 2016/5/15.
 */
public class OptionsActivity extends BaseActivity implements View.OnClickListener {
    private TextView teachTv, exceptionsTv, checkUpdateTv, contactAuthorTv, statiscticsTv;
    private TopBar topBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);
        initUI();
    }

    private void initUI() {
        teachTv = (TextView) findViewById(R.id.teach_tv);
        exceptionsTv = (TextView) findViewById(R.id.exceptions_tv);
        checkUpdateTv = (TextView) findViewById(R.id.check_update);
        contactAuthorTv = (TextView) findViewById(R.id.contact_author);
        statiscticsTv = (TextView) findViewById(R.id.statisctics_tv);

        teachTv.setOnClickListener(this);
        exceptionsTv.setOnClickListener(this);
        checkUpdateTv.setOnClickListener(this);
        contactAuthorTv.setOnClickListener(this);
        statiscticsTv.setOnClickListener(this);

        topBar = (TopBar) findViewById(R.id.top_bar);
        topBar.setOnTopBarClickListener(new TopBar.OnTopBarClickListener() {

            @Override
            public void onRightClick() {
            }

            @Override
            public void onTitleClick() {

            }

            @Override
            public void onLeftClick() {
                OptionsActivity.this.finish();
            }
        });
        if (!TextUtils.isEmpty(Globle.versionName)) {
            topBar.setRightText("v" + Globle.versionName);
        }
    }

    private void showContactDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("联系作者");
        builder.setMessage("有什么建议或者发现了bug都可以联系我\n作者:苏航\n邮箱:772192594@qq.com");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();
    }

    private void showExceptionsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("免责条款");
        builder.setMessage("本应用全部资源来自mangareader网站,并且包含'下载'功能,凡以本应用" +
                "下载的漫画不得用于商业用途,如若违反与本应用无关,与本应用作者无关.\n" +
                "作者:苏航\n" +
                "QQ:772192594");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.teach_tv:
                Intent intent = new Intent(OptionsActivity.this, TeachActivity.class);
                startActivity(intent);
                break;
            case R.id.exceptions_tv:
                showExceptionsDialog();
                break;
            case R.id.check_update:
                ToastUtil.tipShort(OptionsActivity.this, "额...我没有服务器,你可以直接找我问有没有最新的...");
                break;
            case R.id.contact_author:
                showContactDialog();
                break;
            case R.id.statisctics_tv:
                Intent intent1 = new Intent(OptionsActivity.this, StatisticsActivity.class);
                startActivity(intent1);
                break;
        }
    }
}
