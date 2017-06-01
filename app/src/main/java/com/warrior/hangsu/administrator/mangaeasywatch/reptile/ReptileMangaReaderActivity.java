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
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
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
import com.warrior.hangsu.administrator.mangaeasywatch.volley.MStringRequest;
import com.warrior.hangsu.administrator.mangaeasywatch.volley.VolleyTool;
import com.warrior.hangsu.administrator.mangaeasywatch.widght.RoundProgressBar;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 这个是专门爬mangareader这个网站的
 */
public class ReptileMangaReaderActivity extends Activity implements OnClickListener, View.OnLongClickListener {
    private boolean start = true;// 用于开启和关闭线程
    private String urlResult = "", prefetch = "";// 要下载的图片的地址
    private String mangaName;// 漫画名称
    private int folderSize = 5;// 子文件夹话数
    private int realEpisode = 1, realPage = 1;//目前真正的位置 从handler中获得
    /**
     * 网址有规律且一页一图且多章节型 目前只适用于www.mangareader.net这个网址
     */
    private int nowEpisode = 1, nowPage = 1, endEpisode;
    private int tryAmount = 0;
    private boolean isLastPage = false;// 用于判断是哪张图片
    private TextView explainTv, tryAmountTv, mangaNameTv;
    private EditText episodeET, pageET, mangaNameET, endEpisodeET;
    private Button folderSizeBtn;
    private Button startBtn, stopBtn;
    private String[] folderSizeList = {"5", "10", "15", "20"};
    private int pageSize;
    private String[] pathList;
    /**
     * 普通用户画面
     */
    private View friendlyView, detailsView;
    private RoundProgressBar roundProgressBar;
    private TextView downloadYetTv;
    /**
     * jsoup 方式获取图片地址集
     */
    private org.jsoup.nodes.Document doc;
    // handler
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (Integer.valueOf(msg.obj.toString())) {
                case 0:
                    explainTv.setText("第" + msg.arg1 + "话,第" + msg.arg2 + "页下载完成");
                    realEpisode = msg.arg1;
                    realPage = msg.arg2;
                    break;
                case 1:
                    // explainTv.setText("第" + msg.arg1 + "话,第" + msg.arg2 + "页尝试第"
                    // + tryAmount + "遍");
                    tryAmountTv.setText("遇到错误" + tryAmount + "次");
                    break;
                case 2:
                    initPicPathList();
                    break;
                case 3:
                    explainTv.setText("第" + msg.arg1 + "话,第" + msg.arg2 + "页下载完成");
                    realEpisode = msg.arg1;
                    realPage = msg.arg2;
                    downloadYetTv.setText(downloadYetTv.getText().toString() + "\n" + "第" + msg.arg1 + "话下载完成");
                    getPageSize();
                    break;
                case 4:
                    explainTv.setText("已停止");
                    episodeET.setText(msg.arg1 + "");
                    pageET.setText(msg.arg2 + "");
                    break;
            }
        }

        ;
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reptile_mangareader);
        initUI();
    }

    private void initUI() {
        explainTv = (TextView) findViewById(R.id.explain);
        tryAmountTv = (TextView) findViewById(R.id.amount);
        episodeET = (EditText) findViewById(R.id.episode);
        endEpisodeET = (EditText) findViewById(R.id.end_episode);
        mangaNameET = (EditText) findViewById(R.id.manga_name);
        folderSizeBtn = (Button) findViewById(R.id.folder_size);
        folderSizeBtn.setVisibility(View.INVISIBLE);
        pageET = (EditText) findViewById(R.id.page);
        startBtn = (Button) findViewById(R.id.start);
        stopBtn = (Button) findViewById(R.id.stop);
        mangaNameTv = (TextView) findViewById(R.id.manga_name_roundtv);
        friendlyView = findViewById(R.id.friendly_view);
        detailsView = findViewById(R.id.details_view);
        roundProgressBar = (RoundProgressBar) findViewById(R.id.download_progressbar);
        downloadYetTv = (TextView) findViewById(R.id.download_yet_tv);
        startBtn.setOnClickListener(this);
        stopBtn.setOnClickListener(this);
        folderSizeBtn.setOnClickListener(this);
        friendlyView.setOnLongClickListener(this);
        detailsView.setOnClickListener(this);

        if (null != SharedPreferencesUtils.getSharedPreferencesData(this,
                "mangaName")) {
            mangaName = SharedPreferencesUtils.getSharedPreferencesData(this,
                    "mangaName");
            mangaNameET.setText(mangaName);
            mangaNameTv.setText(mangaName);
        }
        recoverStatus();
        if (Globle.friendlyDownload) {
            friendlyLaunch();
        }
    }

    private void friendlyLaunch() {
        mangaNameTv.setText(Globle.mangaName);
        episodeET.setText(Globle.startPoint + "");
        pageET.setText("1");
        endEpisodeET.setText(Globle.endPoint + "");
        folderSizeBtn.setText("10");
        mangaNameET.setText(Globle.mangaName);
        startDownload();
    }


    /**
     * 把所有在pathlist里的图片全下载下来
     *
     * @param episode
     * @param page
     */
    private void downLoadPic(final int episode, final int page) {
        if (start) {
            // 将图片下载并保存
            new Thread() {
                public void run() {
                    Bitmap bp = null;
                    if (!TextUtils.isEmpty(pathList[page - 1])) {
                        //从网络上获取到图片
                        bp = loadImageFromNetwork(pathList[page - 1], episode, page);
                        if (null != bp) {
                            //把图片保存到本地
                            FileUtil.saveBitmap(bp, mangaName + "_" + episode
                                            + "_" + page + ".jpg",
                                    getChildFolderName(episode), mangaName);

                            Message msg = handler.obtainMessage();
                            msg.arg1 = episode;
                            msg.arg2 = page;

                            roundProgressBar.setProgress(page);
                            if (page + 1 <= pageSize) {
                                downLoadPic(episode, page + 1);
                                msg.obj = 0;
                            } else {
                                //下载完成
                                nowEpisode++;
                                msg.obj = 3;
                            }

                            msg.sendToTarget();
                        } else {
                            downLoadPic(episode, page);
                            tryAmount++;
                            Message msg = handler.obtainMessage();
                            msg.obj = 1;
                            msg.sendToTarget();
                        }
                    } else {
                        downLoadPic(episode, page + 1);
                    }
                }
            }.start();
        }
    }

    /**
     * 获得当前话的所有图片地址
     */
    private void initPicPathList() {
        if (start) {
            explainTv.setText("正在获取第" + nowEpisode + "话所有的图片地址");

            String url = "http://www.mangareader.net/" + mangaName + "/"
                    + nowEpisode + "/" + nowPage;
            HashMap<String, String> params = new HashMap<String, String>();
            MStringRequest request = new MStringRequest(url, params,
                    new Response.Listener<String>() {

                        @Override
                        public void onResponse(String arg0) {
                            // 得到包含某正则表达式的字符串
                            Pattern p;
                            p = Pattern.compile("http:[^\f\n\r\t]*?(jpg|png|gif|jpeg)");
                            Matcher m;
                            m = p.matcher(arg0);
                            // String xxx;
                            //用于调换预加载和当前图片地址
                            int cycle = 0;
                            while (m.find()) {
                                // 获取到图片的URL 先获取到的第二个后获取到的第一个
                                if (cycle == 1) {
                                    urlResult = m.group();
                                } else if (cycle == 0) {
                                    prefetch = m.group();
                                }
                                cycle++;
                            }
                            if (nowPage != pageSize) {
                                pathList[nowPage - 1] = urlResult;
                                pathList[nowPage] = prefetch;
                                Logger.d(urlResult + "\n" + prefetch);
                            } else {
                                pathList[nowPage - 1] = prefetch;
                                Logger.d(prefetch);
                            }
                            if (nowPage == pageSize || nowPage + 1 == pageSize) {
                                //已找到所有的图片地址
                                nowPage = 1;
                                downLoadPic(nowEpisode, 1);
                            } else {
                                nowPage = nowPage + 2;
                                initPicPathList();
                            }
                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError arg0) {
                    initPicPathList();
                    tryAmount++;
                    tryAmountTv.setText("遇到错误" + tryAmount + "次");
                }
            });
            VolleyTool.getInstance(ReptileMangaReaderActivity.this).getRequestQueue()
                    .add(request);
        }
    }

    /**
     * 获取某一话的总页数
     */
    private void getPageSize() {
        if (start && nowEpisode <= endEpisode) {
            new Thread() {
                @Override
                public void run() {
                    try {
                        //这里第几页都没关系 只要存在就行
                        doc = Jsoup.connect("http://www.mangareader.net/" + mangaName + "/"
                                + nowEpisode + "/" + 1)
                                .timeout(10000).get();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (null != doc) {
                        Element page = doc.getElementById("selectpage");
                        if (null != page) {
                            Logger.d("page不为空");
                            Element lastPage = page.select("select option").last();
                            pageSize = Integer.valueOf(lastPage.text());
                            pathList = new String[pageSize];
                            roundProgressBar.setMax(pageSize);

                            Message msg = handler.obtainMessage();
                            msg.obj = 2;
                            msg.sendToTarget();
                        } else {
                            Logger.d("page为空");
                            //有的漫画的章节并非连续的 这时候就跳到下一章 直到存在为止 写上第N话不存在
                            nowEpisode++;
                            getPageSize();
                        }
                    }
                }
            }.start();
        } else if (nowEpisode > endEpisode) {
            Message msg = handler.obtainMessage();
            msg.obj = 4;
            msg.arg1 = realEpisode + 1;
            msg.arg2 = 1;
            msg.sendToTarget();
        }
    }


    private String getChildFolderName(int episode) {
        String res;
        int start = ((int) (episode / folderSize)) * folderSize + 1;
        int end = start + folderSize - 1;
        res = start + "-" + end;
        return res;
    }


    /**
     * 网络获取图片
     *
     * @param imageUrl
     * @return
     */
    private Bitmap loadImageFromNetwork(String imageUrl, int episode, int page) {
        Bitmap bitmap = null;
        try {
            InputStream is = new URL(imageUrl).openStream();
            bitmap = BitmapFactory.decodeStream(is);
        } catch (IOException e) {
            downLoadPic(episode, page);
            tryAmount++;
            Message msg = handler.obtainMessage();
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
//        Toast.makeText(this, "程序退出,保存当前状态" + explainTv.getText().toString(), Toast.LENGTH_SHORT)
//                .show();
        /**
         * 网址有规律且一页一图且多章节型 目前只适用于www.mangareader.net这个网址
         */
        SharedPreferencesUtils.setSharedPreferencesData(this, "position",
                explainTv.getText().toString());
        SharedPreferencesUtils.setSharedPreferencesData(this, "nowEpisode",
                nowEpisode);
        SharedPreferencesUtils.setSharedPreferencesData(this, "nowPage",
                realPage + 1);
        SharedPreferencesUtils.setSharedPreferencesData(this, "folderSize",
                folderSize);
        SharedPreferencesUtils.setSharedPreferencesData(this, "endEpisode",
                endEpisode);
    }

    private void recoverStatus() {
        /**
         * 网址有规律且一页一图且多章节型 目前只适用于www.mangareader.net这个网址
         */
        if (null != SharedPreferencesUtils.getSharedPreferencesData(this,
                "position")) {
//            Toast.makeText(this, "程序恢复状态" + explainTv.getText().toString(), Toast.LENGTH_SHORT)
//                    .show();
            String position;
            position = SharedPreferencesUtils.getSharedPreferencesData(this,
                    "position");
            explainTv.setText(position);
            nowEpisode = SharedPreferencesUtils.getIntSharedPreferencesData(
                    this, "nowEpisode");
            nowPage = SharedPreferencesUtils.getIntSharedPreferencesData(this,
                    "nowPage");
            endEpisode = SharedPreferencesUtils.getIntSharedPreferencesData(this,
                    "endEpisode");
            folderSize = SharedPreferencesUtils.getIntSharedPreferencesData(
                    this, "folderSize");
            episodeET.setText(String.valueOf(nowEpisode));
            endEpisodeET.setText(String.valueOf(endEpisode));
            pageET.setText(String.valueOf(nowPage));
            folderSizeBtn.setText(String.valueOf(folderSize));
        }
    }

    private void showLogoutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("是否在后台继续下载?");
        builder.setPositiveButton("否", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Globle.reptileing = false;
                ReptileMangaReaderActivity.this.finish();
            }
        });
        builder.setNegativeButton("是", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Globle.reptileing = true;
                Intent intent = new Intent(ReptileMangaReaderActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();
    }

    private void showFloderSizeListDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(
                ReptileMangaReaderActivity.this);
        builder.setTitle("选项");
        builder.setItems(folderSizeList, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        folderSizeBtn.setText(folderSizeList[0]);
                        break;
                    case 1:
                        folderSizeBtn.setText(folderSizeList[1]);
                        break;
                    case 2:
                        folderSizeBtn.setText(folderSizeList[2]);
                        break;
                    case 3:
                        folderSizeBtn.setText(folderSizeList[3]);
                        break;
                }
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == event.KEYCODE_BACK) {
            showLogoutDialog();
        }
        return true;
    }

    private void startDownload() {
        if (!TextUtils.isEmpty(episodeET.getText().toString())
                && !TextUtils.isEmpty(pageET.getText().toString())
                && !TextUtils.isEmpty(mangaNameET.getText().toString())
                && !TextUtils.isEmpty(folderSizeBtn.getText().toString())
                && !TextUtils.isEmpty(endEpisodeET.getText().toString())) {
            start = true;
            nowEpisode = Integer.valueOf(episodeET.getText().toString());
            endEpisode = Integer.valueOf(endEpisodeET.getText().toString());
            nowPage = Integer.valueOf(pageET.getText().toString());
            realEpisode = nowEpisode;
            realPage = nowPage - 1;
            mangaName = mangaNameET.getText().toString();
            folderSize = Integer.valueOf(folderSizeBtn.getText().toString());

            SharedPreferencesUtils.setSharedPreferencesData(this,
                    "mangaName", mangaName);
            mangaNameTv.setText(mangaName);
            getPageSize();
        } else {
            Toast.makeText(ReptileMangaReaderActivity.this, "不能为空!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start:
                startDownload();
                break;
            case R.id.stop:
                start = false;
                Message msg = handler.obtainMessage();
                msg.obj = 4;
                if (realPage == pageSize) {
                    msg.arg1 = realEpisode + 1;
                    msg.arg2 = 1;
                } else {
                    msg.arg1 = realEpisode;
                    msg.arg2 = realPage + 1;
                }
                msg.sendToTarget();
                break;
            case R.id.folder_size:
                showFloderSizeListDialog();
                break;
        }
    }

    @Override
    public boolean onLongClick(View view) {
        switch (view.getId()) {
            case R.id.friendly_view:
                friendlyView.setVisibility(View.GONE);
                folderSizeBtn.setVisibility(View.VISIBLE);
                break;
            case R.id.details_view:
                friendlyView.setVisibility(View.VISIBLE);
                folderSizeBtn.setVisibility(View.GONE);
                break;
        }
        return true;
    }
}
