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
import com.warrior.hangsu.administrator.mangaeasywatch.utils.Logger;
import com.warrior.hangsu.administrator.mangaeasywatch.utils.SharedPreferencesUtils;
import com.warrior.hangsu.administrator.mangaeasywatch.utils.ToastUtil;
import com.warrior.hangsu.administrator.mangaeasywatch.volley.MStringRequest;
import com.warrior.hangsu.administrator.mangaeasywatch.volley.VolleyTool;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 这个理论上可以爬所有在一页中集中了所有图片的网站
 */
public class ReptileListActivity extends Activity implements OnClickListener {
    private boolean start = true;// 用于开启和关闭线程
    private String urlResult;// 要下载的图片的地址
    private int realPage = 1;//目前真正的位置 从handler中获得
    private int tryAmount = 0;
    private TextView tryAmountTv;
    private Button startBtn, clearPathBtn, stopBtn;
    private TextView explainTv;
    private EditText mangaNameET, mangaPathET;
    private String mangaPath, mangaName;
    private ArrayList<String> pathArray = new ArrayList<String>();
    /**
     * jsoup 方式获取图片地址集
     */
    private org.jsoup.nodes.Document doc;

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
        setContentView(R.layout.activity_reptile_list);
        initUI();
        ToastUtil.tipShort(this, "因为下载的是一张张的图片,所以无需全部下载完成," +
                "即可直接观看漫画,用户可以边看边下载");
    }

    private void initUI() {
        explainTv = (TextView) findViewById(R.id.explain);
        tryAmountTv = (TextView) findViewById(R.id.amount);
        startBtn = (Button) findViewById(R.id.start);
        mangaNameET = (EditText) findViewById(R.id.manga_name_et);
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
            String url = mangaPath;
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
                                    pathArray.add(urlResult);
                                }

                                downLoadPic(1);
                            }
                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError arg0) {
                    Logger.d("网络错误" + arg0);
                    getImageURL();
                    tryAmount++;
                    Message msg = handler2.obtainMessage();
                    msg.obj = 1;
                    msg.sendToTarget();
                }
            });
            VolleyTool.getInstance(ReptileListActivity.this).getRequestQueue()
                    .add(request);
        }
    }

    //TODO
    private void getGelImageURL() {
        if (start) {
            new Thread() {
                @Override
                public void run() {
                    try {
                        //这里第几页都没关系 只要存在就行
                        doc = Jsoup.connect(mangaPath)
                                .timeout(10000).get();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (null != doc) {
                        Element page = doc.getElementById("post-list");
                        if (null != page) {
//                            Elements href = page.getElementsByTag("span.a.href");
                            Elements href = doc.select("span.thumb");
                            for (int i = 0; i < href.size(); i++) {
                                Logger.d("href" + href.get(i).lastElementSibling().attr("href"));
                            }
                        }
                    }
                }
            }.start();
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
                    if (!TextUtils.isEmpty(pathArray.get(page - 1))) {
                        //从网络上获取到图片
                        bp = loadImageFromNetwork(pathArray.get(page - 1), page);
                        if (null != bp) {
                            //把图片保存到本地
                            FileUtil.saveBitmap(
                                    bp, mangaName + "_" + page + ".jpg", mangaName);

                            Message msg = handler2.obtainMessage();
                            msg.arg2 = page;
                            if (page + 1 <= pathArray.size()) {
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
        SharedPreferencesUtils.setSharedPreferencesData(this, "list_explain",
                explainTv.getText().toString());
        SharedPreferencesUtils.setSharedPreferencesData(this, "list_manganame",
                mangaName);
        SharedPreferencesUtils.setSharedPreferencesData(this, "list_mangaPath",
                mangaPathET.getText().toString());
    }

    private void recoverStatus() {
        if (null != SharedPreferencesUtils.getSharedPreferencesData(this,
                "list_mangaPath")) {
            Toast.makeText(this, "程序恢复状态" + explainTv.getText().toString(), Toast.LENGTH_SHORT)
                    .show();
            String explain;
            explain = SharedPreferencesUtils.getSharedPreferencesData(this,
                    "list_explain");
            explainTv.setText(explain);
            mangaName = SharedPreferencesUtils.getSharedPreferencesData(
                    this, "list_manganame");
            mangaPath = SharedPreferencesUtils.getSharedPreferencesData(this,
                    "list_mangaPath");
            mangaNameET.setText(mangaName);
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
                ReptileListActivity.this.finish();
            }
        });
        builder.setNegativeButton("是", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Globle.reptileing = true;
                Intent intent = new Intent(ReptileListActivity.this, MainActivity.class);
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
                if (!TextUtils.isEmpty(mangaPathET.getText().toString()) && !TextUtils.isEmpty(mangaNameET.getText().toString())) {
                    start = true;
                    mangaPath = mangaPathET.getText().toString();
                    mangaName = mangaNameET.getText().toString();
                    Log.d("剪裁", mangaPath);
                    explainTv.setText("正在获取所有的图片地址");
//                    if (mangaPath.contains("gelbooru")) {
                    getGelImageURL();
//                    } else {
//                        getImageURL();
//                    }
                } else {
                    Toast.makeText(ReptileListActivity.this, "不能为空!", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.clean_btn:
                mangaNameET.setText("");
                mangaPathET.setText("");
                break;
            case R.id.stop:
                start = false;
                explainTv.setText("已停止");
                break;
        }
    }
}
