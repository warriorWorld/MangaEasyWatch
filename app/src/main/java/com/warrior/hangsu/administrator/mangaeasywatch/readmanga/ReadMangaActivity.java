package com.warrior.hangsu.administrator.mangaeasywatch.readmanga;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.warrior.hangsu.administrator.mangaeasywatch.R;
import com.warrior.hangsu.administrator.mangaeasywatch.db.DbAdapter;
import com.warrior.hangsu.administrator.mangaeasywatch.shotscreen.ScreenShot;
import com.warrior.hangsu.administrator.mangaeasywatch.shotscreen.ShotView;
import com.warrior.hangsu.administrator.mangaeasywatch.sort.FileComparator;
import com.warrior.hangsu.administrator.mangaeasywatch.sort.FileComparator1;
import com.warrior.hangsu.administrator.mangaeasywatch.sort.FileComparator2;
import com.warrior.hangsu.administrator.mangaeasywatch.translate.TranslateActivity;
import com.warrior.hangsu.administrator.mangaeasywatch.utils.FileUtil;
import com.warrior.hangsu.administrator.mangaeasywatch.utils.Globle;
import com.warrior.hangsu.administrator.mangaeasywatch.utils.Logger;
import com.warrior.hangsu.administrator.mangaeasywatch.utils.SharedPreferencesUtils;
import com.warrior.hangsu.administrator.mangaeasywatch.utils.ToastUtil;
import com.warrior.hangsu.administrator.mangaeasywatch.utils.TopBar;
import com.warrior.hangsu.administrator.mangaeasywatch.volley.MStringRequest;
import com.warrior.hangsu.administrator.mangaeasywatch.volley.VolleyTool;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * /storage/sdcard0/reptile/one-piece
 * <p/>
 * Created by Administrator on 2016/4/4.
 */
public class ReadMangaActivity extends Activity implements OnClickListener, View.OnLongClickListener {
    private HackyViewPager mangaPager;
    private DiscreteSeekBar seekBar;
    private View showSeekBar;
    //    private RelativeLayout translateRL;
//    private EditText translateET;
//    private Button okBtn;
    // 截图的view
    private ShotView shotView = null;
    private TopBar topBar;
    private NewMangaAdapter adapter;
    private ImageView favoriteIv;
    private File f;
    private File[] files;
    private ArrayList<String> pathList = new ArrayList<String>();
    private String[] webPathList;
    private int historyPosition = 1;
    private int finalPosition = 1;
    private ProgressDialog progressBar;
    private DbAdapter db;//数据库
    /**
     * 爬虫
     */
    private String urlResult = "", prefetch = "";
    private boolean isLastPage;
    private int pageSize;

    /**
     * jsoup 方式获取图片地址集
     */
    private org.jsoup.nodes.Document doc;
    private Handler handler1 = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (Integer.valueOf(msg.obj.toString())) {
                case 0:
                    progressBar.setMessage("共" + pageSize + "页,大约需要" + Math.round(pageSize / 5) + "秒");
                    initPicPathList(1);
                    break;
                case 1:
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_manga);
        db = new DbAdapter(this);
        initUI();
        initProgressBar();
        if (Globle.sourceFrom == Globle.Source.local) {
            f = new File(Globle.mangaPath);
            initFile();
            refresh();
        } else if (Globle.sourceFrom == Globle.Source.web) {
            progressBar.show();
            ToastUtil.tipShort(this, "只不过在读这一话的所有图片地址,用不了多少流量!");
//            initWebFile(1, Globle.mangaPath);
            getPageSize();
        } else {
            initFavorites();
        }
    }

    private void initFavorites() {
        pathList = db.queryAllFavorite();
        refresh();
    }

    private void initProgressBar() {
        progressBar = new ProgressDialog(ReadMangaActivity.this);
        progressBar.setCancelable(false);
        progressBar.setTitle("有点慢,等等.");
        progressBar.setMessage("每20页大约耗时4秒(速度与网速成正比)");
    }

    /**
     * 进度条
     */
    private void initSeekBar() {
        seekBar.setMin(1);
        seekBar.setMax(pathList.size());
        seekBar.setProgress(historyPosition);
        seekBar.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
                finalPosition = value;
            }

            @Override
            public void onStartTrackingTouch(DiscreteSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(DiscreteSeekBar seekBar) {
                if (finalPosition >= 0) {
                    mangaPager.setCurrentItem(finalPosition - 1);
//                    cutSeekBar();
//                seekBar.setVisibility(View.GONE);
                }
            }
        });
    }

    /**
     * 本地图片读取
     */
    private void initFile() {
        files = f.listFiles();
        for (int i = 0; i < files.length; i++) {
            pathList.add("file://" + files[i].toString());
        }

        //这个判断是为了.................................不能说
        String s = pathList.get(0);
        if (s.contains(".jpg") || s.contains(".png") || s.contains(".bmp")) {
            s = s.substring(0, s.length() - 1 - 3);
            Log.d("s", "裁剪后的字符串" + s);
        } else if (s.contains(".jpeg")) {
            s = s.substring(0, s.length() - 1 - 4);
            Log.d("s", "裁剪后的字符串" + s);
        }
        // 从文件名中得到章节和页码
        boolean chapter = true;
        String[] arr = s.split("_");
        if (arr.length == 0) {
            arr = s.split("-");
        }

        Logger.d("pathList.get(0)" + pathList.get(0));
        if (pathList.get(0).contains("_") || pathList.get(0).contains("-")) {
            if (arr.length != 3) {
                return;
            }
            FileComparator comparator = new FileComparator();
            Collections.sort(pathList, comparator);
        } else if (pathList.get(0).contains("(")) {
            Logger.d("带括号");
            FileComparator1 comparator1 = new FileComparator1();
            Collections.sort(pathList, comparator1);
        } else {
            String[] arri = s.split("/");
            s = arri[arri.length - 1];
            try {
                //用于判断是否位数字的异教徒写法
                int isInt = Integer.valueOf(s);
                Logger.d("是数字");
                FileComparator2 comparator2 = new FileComparator2();
                Collections.sort(pathList, comparator2);
            } catch (NumberFormatException e) {

            }
        }
    }

    private void refresh() {
        initViewPager();
        initSeekBar();
    }


    /**
     * 获取到图片的URL 添加到pathlist 然后显示图片
     *
     * @param page
     * @param manganame
     */
    private void initWebFile(final int page,
                             final String manganame) {
        String url = "http://www.mangareader.net" + manganame + "/" + page;
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
                        int cycle = 0;
                        while (m.find()) {
                            // 获取到图片的URL
                            urlResult = m.group();
                            cycle++;
                            Log.d("res", urlResult);
                        }
                        pathList.add(urlResult);
                        if (cycle == 1) {
                            isLastPage = false;
                        } else if (cycle == 0) {
                            isLastPage = true;
                        }

                        if (isLastPage) {
                            //已找到所有的图片地址
                            progressBar.dismiss();
                            refresh();
                        } else {
                            initWebFile(page + 1, manganame);
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError arg0) {
            }
        });
        VolleyTool.getInstance(ReadMangaActivity.this).getRequestQueue()
                .add(request);
    }

    private void initPicPathList(final int page) {
        String url = "http://www.mangareader.net" + Globle.mangaPath + "/" + page;
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
                        if (page != pageSize) {
                            pathList.add(urlResult);
                            pathList.add(prefetch);
                            Logger.d(urlResult + "\n" + prefetch);
                        } else {
                            pathList.add(prefetch);
                            Logger.d(prefetch);
                        }
                        if (page == pageSize || page + 1 == pageSize) {
                            //已找到所有的图片地址
                            progressBar.dismiss();
                            refresh();
                        } else {
                            initPicPathList(page + 2);
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError arg0) {
            }
        });
        VolleyTool.getInstance(ReadMangaActivity.this).getRequestQueue()
                .add(request);
    }

    private void getPageSize() {
        new Thread() {
            @Override
            public void run() {
                try {
                    doc = Jsoup.connect("http://www.mangareader.net" + Globle.mangaPath + "/" + 1)
                            .timeout(10000).get();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (null != doc) {
                    Element page = doc.getElementById("selectpage");
                    Element lastPage = page.select("select option").last();
                    pageSize = Integer.valueOf(lastPage.text());

                    Message msg = handler1.obtainMessage();
                    msg.obj = 0;
                    msg.sendToTarget();
                }
            }
        }.start();

    }

    private void initUI() {
        mangaPager = (HackyViewPager) findViewById(R.id.manga_viewpager);
        favoriteIv = (ImageView) findViewById(R.id.favorite_iv);
        seekBar = (DiscreteSeekBar) findViewById(R.id.seekbar);
        showSeekBar = findViewById(R.id.show_seek_bar);
//        translateRL = (RelativeLayout) findViewById(R.id.translate_rl);
//        translateET = (EditText) findViewById(R.id.translate_et);
//        okBtn = (Button) findViewById(R.id.ok_btn);
//
//        okBtn.setOnClickListener(this);

        mangaPager.setOnLongClickListener(this);
        showSeekBar.setOnClickListener(this);
        favoriteIv.setOnClickListener(this);
        showSeekBar.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                String file = pathList.get(historyPosition);
                if (file.contains("file://")) {
                    file = file.substring(7, file.length());
                    showDeleteDialog(file);
                } else {
                    showSaveDialog(file);
                }
                return true;
            }
        });
        topBar = (TopBar) findViewById(R.id.top_bar);
        topBar.setTitle(Globle.mangaTitle);
        topBar.setOnTopBarClickListener(new TopBar.OnTopBarClickListener() {

            @Override
            public void onRightClick() {
                if (shotView == null) {
                    shotView = (ShotView) findViewById(R.id.shot_view);
                    shotView.setL(new ShotView.FinishShotListener() {
                        @Override
                        public void finishShot() {
//                            cutTopBar();
                            openTranslateActivity();
                        }
                    });
                } else {
                    shotView.setIsRunning(true);
                }

                Bitmap bgBitmap = shotView.getBitmap();
                if (bgBitmap != null) {
                    bgBitmap.recycle();
                }
                bgBitmap = ScreenShot.takeScreenShot(ReadMangaActivity.this);
                shotView.setBitmap(bgBitmap);
                shotView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onTitleClick() {
//                cutTopBar();
                openTranslateActivity();
            }

            @Override
            public void onLeftClick() {
                ReadMangaActivity.this.finish();
            }
        });
    }

    private void showDeleteDialog(final String fileName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("是否删除?");
        builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                URI uri = URI.create(fileName);
                File file = new File(fileName);
                if (file.exists()) {
                    file.delete();
                    ToastUtil.tipShort(ReadMangaActivity.this, "已删除");
                    pathList.remove(historyPosition);
                    //得把这个加上 因为之前给去掉了,而数据库中保存的是包含这个的
                    db.deleteFavoriteByFilePath("file://" + fileName);
                    initViewPager();
                }
            }
        });
        builder.setNegativeButton("否", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog dialog = builder.create();
        dialog.setCancelable(true);
        dialog.show();
    }

    private void showSaveDialog(final String imgPath) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("是否保存图片?");
        builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                downLoadPic(imgPath);
            }
        });
        builder.setNegativeButton("否", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog dialog = builder.create();
        dialog.setCancelable(true);
        dialog.show();
    }

    /**
     *
     */
    private void downLoadPic(final String path) {
        // 将图片下载并保存
        new Thread() {
            public void run() {
                Bitmap bp = null;
                if (!TextUtils.isEmpty(path)) {
                    //从网络上获取到图片
                    bp = loadImageFromNetwork(path);
                    if (null != bp) {
                        //把图片保存到本地
                        FileUtil.saveBitmap(bp, Globle.mangaTitle + "_" + Globle.currentChapter
                                + "_" + historyPosition + ".jpg", Globle.mangaTitle);
                    }
                }
            }
        }.start();
    }

    /**
     * 网络获取图片
     *
     * @param imageUrl
     * @return
     */
    private Bitmap loadImageFromNetwork(String imageUrl) {
        Bitmap bitmap = null;
        try {
            InputStream is = new URL(imageUrl).openStream();
            bitmap = BitmapFactory.decodeStream(is);
        } catch (IOException e) {
            Logger.d("下载失败" + e);
        }
        return bitmap;
    }

    private void openTranslateActivity() {
        Intent intent = new Intent(ReadMangaActivity.this, TranslateActivity.class);
        startActivity(intent);
    }

    private void initViewPager() {
        if (null == adapter) {
            adapter = new NewMangaAdapter(ReadMangaActivity.this, pathList);
            switch (Globle.sourceFrom) {
                case web:
                    mangaPager.setOffscreenPageLimit(3);
                    break;
                case local:
                    mangaPager.setOffscreenPageLimit(1);
                    break;
            }
            mangaPager.setAdapter(adapter);

            recoverState();
            mangaPager.setPageTransformer(true, new DepthPageTransformer());
            mangaPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    historyPosition = position;
                    topBar.setTitle(position + 1 + "/" + pathList.size());
                    if (seekBar.isShown()) {
                        toggleFavorite();
                    }
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
//            mangaPager.setCurrentItem(2);
        } else {
            adapter.setPathList(pathList);
            adapter.notifyDataSetChanged();
        }
    }

    private void cutSeekBar() {
        if (seekBar.isShown()) {
            seekBar.setVisibility(View.GONE);
            favoriteIv.setVisibility(View.GONE);
        } else {
            seekBar.setVisibility(View.VISIBLE);
            if (Globle.sourceFrom == Globle.Source.local ||
                    Globle.sourceFrom == Globle.Source.favorite) {
                favoriteIv.setVisibility(View.VISIBLE);
                toggleFavorite();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferencesUtils.setSharedPreferencesData(this, Globle.mangaPath,
                historyPosition);
        db.closeDb();
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferencesUtils.setSharedPreferencesData(this, Globle.mangaPath,
                historyPosition);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        SharedPreferencesUtils.setSharedPreferencesData(this, Globle.mangaPath,
                historyPosition);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        recoverState();
    }


    private void recoverState() {
        int p = SharedPreferencesUtils.getIntSharedPreferencesData(this,
                Globle.mangaPath);

        if (p >= 0) {
            historyPosition = p;
            mangaPager.setCurrentItem(p);
        }
    }

    private void toggleFavorite() {
        if (db.queryFavorited(pathList.get(historyPosition))) {
            favoriteIv.setImageResource(R.drawable.liked);
        } else {
            favoriteIv.setImageResource(R.drawable.like2);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//            case R.id.ok_btn:
//                cutTopBar();
//                break;
            case R.id.show_seek_bar:
                cutSeekBar();
                break;
            case R.id.favorite_iv:
                if (db.queryFavorited(pathList.get(historyPosition))) {
                    db.deleteFavoriteByFilePath(pathList.get(historyPosition));
                } else {
                    db.insertFavoriteTb(pathList.get(historyPosition));
                }
                toggleFavorite();
                break;
        }
    }

    @Override
    public boolean onLongClick(View v) {
        switch (v.getId()) {
            case R.id.manga_viewpager:
//                seekBar.setVisibility(View.VISIBLE);
                break;
        }
        return false;
    }
}
