package com.warrior.hangsu.administrator.mangaeasywatch.reptile;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.warrior.hangsu.administrator.mangaeasywatch.R;
import com.warrior.hangsu.administrator.mangaeasywatch.mangalist.MainActivity;
import com.warrior.hangsu.administrator.mangaeasywatch.utils.FileUtil;
import com.warrior.hangsu.administrator.mangaeasywatch.utils.Globle;
import com.warrior.hangsu.administrator.mangaeasywatch.utils.SharedPreferencesUtils;
import com.warrior.hangsu.administrator.mangaeasywatch.utils.ToastUtil;
import com.warrior.hangsu.administrator.mangaeasywatch.volley.MStringRequest;
import com.warrior.hangsu.administrator.mangaeasywatch.volley.VolleyTool;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 这个理论上可以爬所有一页一页翻的那种图片网站(前提是网址是递增的)
 */
public class ReptileMangaActivity extends Activity implements OnClickListener {
    private boolean start = true;// 用于开启和关闭线程
    private String urlResult;// 要下载的图片的地址
    private int realPage = 1;//目前真正的位置 从handler中获得
    private int tryAmount = 0;
    private TextView tryAmountTv;
    private Button startBtn, clearPathBtn, stopBtn;
    private int startPosition = 1;
    private TextView explainTv;
    private EditText startPageET, endPageET, mangaPathET;
    private String mangaPath;
    private int endPosition;
    private String[] pathList;

    // 更新UI的handler
    private Handler handler2 = new Handler() {
        public void handleMessage(Message msg) {
            switch (Integer.valueOf(msg.obj.toString())) {
                case 0:
                    explainTv.setText("第" + msg.arg1 + "页下载完成");
                    realPage = msg.arg1;
                    break;
                case 1:
                    tryAmountTv.setText("遇到错误" + tryAmount + "次");
                    break;
                case 2:
                    explainTv.setText("已暂停");
                    break;
                case 3:
                    explainTv.setText("下载完成");
                    break;
            }
        }

        ;
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reptile);
        initUI();
        ToastUtil.tipShort(this, "因为下载的是一张张的图片,所以无需全部下载完成," +
                "即可直接观看漫画,用户可以边看边下载");
    }

    private void initUI() {
        explainTv = (TextView) findViewById(R.id.explain);
        tryAmountTv = (TextView) findViewById(R.id.amount);
        startBtn = (Button) findViewById(R.id.start);
        startPageET = (EditText) findViewById(R.id.start_page_et);
        endPageET = (EditText) findViewById(R.id.end_page);
        mangaPathET = (EditText) findViewById(R.id.manga_path);
        clearPathBtn = (Button) findViewById(R.id.clean_btn);
        stopBtn = (Button) findViewById(R.id.stop);

        startBtn.setOnClickListener(this);
        clearPathBtn.setOnClickListener(this);
        stopBtn.setOnClickListener(this);

        recoverStatus();
    }


    /**
     * 这个方法是为了 获取到图片的URL 第二种网站
     */
    private void getImageURL() {
        if (start) {
            String url = mangaPath + startPosition;
            HashMap<String, String> params = new HashMap<String, String>();
            MStringRequest request = new MStringRequest(url, params,
                    new Response.Listener<String>() {

                        @Override
                        public void onResponse(String arg0) {
                            if (TextUtils.isEmpty(arg0)) {
                                getImageURL();
                            } else {
                                // 得到包含某正则表达式的字符串
                                Pattern p;
                                p = Pattern.compile("http:[^\f\n\r\t]*?(jpg|png|gif|jpeg)");
                                Matcher m;
                                m = p.matcher(arg0);
                                while (m.find()) {
                                    // 获取到图片的URL
                                    urlResult = m.group();
                                    Log.d("res", urlResult);
                                }
                                pathList[startPosition - 1] = urlResult;

                                if (startPosition == endPosition) {
                                    //已找到所有的图片地址 从第一页开始下 不存在就跳过
                                    downLoadPic(1);
                                } else {
                                    startPosition++;
                                    getImageURL();
                                }
                            }
                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError arg0) {
                    getImageURL();
                    tryAmount++;
                    Message msg = handler2.obtainMessage();
                    msg.obj = 1;
                    msg.sendToTarget();
                }
            });
            VolleyTool.getInstance(ReptileMangaActivity.this).getRequestQueue()
                    .add(request);
        }
    }

    /**
     * 把所有在pathlist里的图片全下载下来
     *
     * @param page
     */
    private void downLoadPic(final int page) {
        if (start) {
            // 将图片下载并保存
            new Thread() {
                public void run() {
                    Bitmap bp = null;
                    if (!TextUtils.isEmpty(pathList[page - 1])) {
                        //从网络上获取到图片
                        bp = loadImageFromNetwork(pathList[page - 1], page);
                        if (null != bp) {
                            //把图片保存到本地
                            FileUtil.saveBitmap(
                                    bp,
                                    mangaPath.substring(
                                            mangaPath.length() - 17,
                                            mangaPath.length() - 12)
                                            + "_" + page + ".jpg", "other");

                            Message msg = handler2.obtainMessage();
                            msg.arg2 = page;
                            if (page + 1 <= endPosition) {
                                downLoadPic(page + 1);
                                msg.obj = 0;
                                msg.arg1 = page + 1;
                            } else {
                                msg.obj = 3;
                            }

                            msg.sendToTarget();
                        } else {
                            //遇错重试 并刷新错误说明
                            downLoadPic(page);
                            tryAmount++;
                            Message msg = handler2.obtainMessage();
                            msg.obj = 1;
                            msg.sendToTarget();
                        }
                    } else {
                        //如果相应位置为空 就尝试下一个
                        downLoadPic(page + 1);
                    }
                }
            }.start();
        }
    }


    /**
     * 网络获取图片
     *
     * @param imageUrl
     * @return
     */
    private Bitmap loadImageFromNetwork(String imageUrl,
                                        int page) {
        Bitmap bitmap = null;
        try {
            InputStream is = new URL(imageUrl).openStream();
            bitmap = BitmapFactory.decodeStream(is);
        } catch (IOException e) {
            downLoadPic(page);
            tryAmount++;
            Message msg = handler2.obtainMessage();
            msg.obj = 1;
            msg.sendToTarget();
        }
        return bitmap;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        start = false;
        saveStatus();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveStatus();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        recoverStatus();
    }

    private void saveStatus() {
        /**
         * lc-long_click进入的reptile
         */
        SharedPreferencesUtils.setSharedPreferencesData(this, "lc_position",
                explainTv.getText().toString());
        SharedPreferencesUtils.setSharedPreferencesData(this, "lc_nowPosition",
                realPage);
        SharedPreferencesUtils.setSharedPreferencesData(this, "lc_endPosition",
                endPosition);
        SharedPreferencesUtils.setSharedPreferencesData(this, "lc_mangaPath",
                mangaPathET.getText().toString());
    }

    private void recoverStatus() {
        if (null != SharedPreferencesUtils.getSharedPreferencesData(this,
                "lc_position")) {
            Toast.makeText(this, "程序恢复状态" + explainTv.getText().toString(), Toast.LENGTH_SHORT)
                    .show();
            String position;
            position = SharedPreferencesUtils.getSharedPreferencesData(this,
                    "lc_position");
            explainTv.setText(position);
            startPosition = SharedPreferencesUtils.getIntSharedPreferencesData(
                    this, "lc_nowPosition");
            endPosition = SharedPreferencesUtils.getIntSharedPreferencesData(
                    this, "lc_endPosition");
            mangaPath = SharedPreferencesUtils.getSharedPreferencesData(this,
                    "lc_mangaPath");
            startPageET.setText(String.valueOf(startPosition));
            endPageET.setText(String.valueOf(endPosition));
            mangaPathET.setText(mangaPath);
        }
    }

    private void showLogoutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("是否在后台继续下载?");
        builder.setPositiveButton("否", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Globle.reptileing = false;
                ReptileMangaActivity.this.finish();
            }
        });
        builder.setNegativeButton("是", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Globle.reptileing = true;
                Intent intent = new Intent(ReptileMangaActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
        AlertDialog dialog = builder.create();
        dialog.setCancelable(true);
        dialog.show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == event.KEYCODE_BACK) {
            showLogoutDialog();
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start:
                if (!TextUtils.isEmpty(startPageET.getText().toString())
                        && !TextUtils.isEmpty(mangaPathET.getText().toString())) {
                    start = true;
                    startPosition = Integer.valueOf(startPageET.getText().toString());
                    mangaPath = mangaPathET.getText().toString();
                    mangaPath = mangaPath.substring(0, mangaPath.length() - 1);
                    Log.d("剪裁", mangaPath);
                    if (!TextUtils.isEmpty(endPageET.getText().toString())) {
                        endPosition = Integer.valueOf(endPageET.getText().toString());
                    } else {
                        endPosition = 300;
                    }
                    realPage = startPosition - 1;
                    pathList = new String[endPosition];
                    explainTv.setText("正在获取所有的图片地址");
                    getImageURL();
                } else {
                    Toast.makeText(ReptileMangaActivity.this, "不能为空!", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.clean_btn:
                startPageET.setText("");
                endPageET.setText("");
                mangaPathET.setText("");
                break;
            case R.id.stop:
                start = false;
                explainTv.setText("已停止");
                startPageET.setText(String.valueOf(realPage + 1));
                break;
        }
    }
}
