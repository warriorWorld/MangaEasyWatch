package com.warrior.hangsu.administrator.mangaeasywatch.webmangadetails;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.warrior.hangsu.administrator.mangaeasywatch.R;
import com.warrior.hangsu.administrator.mangaeasywatch.db.DbAdapter;
import com.warrior.hangsu.administrator.mangaeasywatch.mangalist.MangaBean;
import com.warrior.hangsu.administrator.mangaeasywatch.readmanga.ReadMangaActivity;
import com.warrior.hangsu.administrator.mangaeasywatch.reptile.ReptileMangaReaderActivity;
import com.warrior.hangsu.administrator.mangaeasywatch.utils.BaseActivity;
import com.warrior.hangsu.administrator.mangaeasywatch.utils.Globle;
import com.warrior.hangsu.administrator.mangaeasywatch.utils.Logger;
import com.warrior.hangsu.administrator.mangaeasywatch.utils.ToastUtil;
import com.warrior.hangsu.administrator.mangaeasywatch.utils.TopBar;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL;

public class WebMangaDetailsActivity extends BaseActivity implements AdapterView.OnItemClickListener, View.OnClickListener {
    private GridView mangaGV;
    private View collectV;
    private TopBar topBar;
    /**
     *
     */
    private boolean chooseing = false;//判断是否在选择状态
    private int choosePosition = 0;//判断选择的是第几个

    private ArrayList<MangaBean> mangaList = new ArrayList<MangaBean>();
    private MangaGridAdapter adapter;
    private ImageView thumbnailIV;
    private TextView mangaNameTv, mangaAuthorTv, mangaTypeTv, lastUpdateTv;
    private String mangaName, mangaAuthor, mangaType = "", lastUpdate;
    private org.jsoup.nodes.Document doc;
    private Handler handler1 = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (Integer.valueOf(msg.obj.toString())) {
                case 0:
                    progressBar.dismiss();
                    refresh();
                    ToastUtil.tipShort(WebMangaDetailsActivity.this, Globle.mangaPathThumbnail + "");
                    break;
                case 1:
                    break;
            }
        }
    };
    private MangaBean item;
    private String[] optionsList = {"下载全部", "选择起始点"};
    private ProgressDialog progressBar;

    private DbAdapter db;//数据库

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manga_details_web);
        db = new DbAdapter(this);
        initUI();
        initProgressBar();
        initWebManga();

    }

    private void initProgressBar() {
        progressBar = new ProgressDialog(WebMangaDetailsActivity.this);
        progressBar.setCancelable(false);
        progressBar.setMessage("加载中...");
    }

    private void initWebManga() {
        progressBar.show();
        new Thread() {
            @Override
            public void run() {
                try {
                    doc = Jsoup.connect("http://www.mangareader.net/" + Globle.mangaTitle)
                            .timeout(10000).get();
                } catch (IOException e) {
                    e.printStackTrace();
                    progressBar.dismiss();
                }
                if (null != doc) {
                    Element masthead = doc.select("h2.aname").first();
                    Element masthead3 = doc.select("td.propertytitle").get(4)
                            .lastElementSibling();
                    Elements mastheads1 = doc.select("span.genretags");
//                    Element masthead4 = doc.select("div.chico_manga").last()
//                            .lastElementSibling();
                    Elements mastheads2 = doc.select("div.chico_manga");

                    Element content = doc.getElementById("listing");
                    Element dates = content.getElementsByTag("td").last();


                    Element imgElement = doc.getElementById("mangaimg");
                    Element imgElement1 = imgElement.getElementsByTag("img").first();
                    Globle.mangaPathThumbnail = imgElement1.attr("src");
                    Logger.d("地址" + Globle.mangaPathThumbnail);

                    mangaName = masthead.text();
                    mangaAuthor = masthead3.text();
                    for (int i = 0; i < mastheads1.size(); i++) {
                        //漫画类型
                        mangaType = mangaType + " " + mastheads1.get(i).text();
                    }
//                    lastUpdate = masthead4.text();
                    lastUpdate = dates.text();

                    String chapter;
                    String path;
                    for (int i = 0; i < mastheads2.size(); i++) {
                        //章节
                        if (mastheads2.size() <= 6) {
                            //跟底下那段一模一样 只不过当总章节小于6时需要特殊处理下
                            item = new MangaBean();
                            chapter = mastheads2.get(i).lastElementSibling().text();
                            String[] s = chapter.split(" ");
                            chapter = s[s.length - 1];
                            item.setTitle(chapter);
                            path = mastheads2.get(i).lastElementSibling().attr("href");
                            item.setPath(path);
                            mangaList.add(item);
                        } else {
                            if (i > 5) {
                                //前6个是最近更新的6个
                                item = new MangaBean();
                                chapter = mastheads2.get(i).lastElementSibling().text();
                                String[] s = chapter.split(" ");
                                chapter = s[s.length - 1];
                                item.setTitle(chapter);
                                path = mastheads2.get(i).lastElementSibling().attr("href");
                                item.setPath(path);
                                mangaList.add(item);
                            }
                        }
                    }

                    Message msg = handler1.obtainMessage();
                    msg.obj = 0;
                    msg.sendToTarget();
                } else {
                    progressBar.dismiss();
                }
            }
        }.start();

    }

    private void refresh() {
        ImageLoader.getInstance().displayImage(Globle.mangaPathThumbnail, thumbnailIV, Globle.options);
        mangaNameTv.setText("漫画名称:" + mangaName);
        mangaAuthorTv.setText("作者:" + mangaAuthor);
        mangaTypeTv.setText("类型:" + mangaType);
        lastUpdateTv.setText("最后更新:" + lastUpdate);
        cutCollect();

        initGridView();
    }

    private void initGridView() {
        if (null == adapter) {
            adapter = new MangaGridAdapter(this);
            mangaGV.setAdapter(adapter);
            mangaGV.setOnItemClickListener(this);
        } else {
            adapter.notifyDataSetChanged();
        }
    }

    private void initUI() {
        mangaGV = (GridView) findViewById(R.id.manga_gv);
        thumbnailIV = (ImageView) findViewById(R.id.thumbnail);
        mangaNameTv = (TextView) findViewById(R.id.manga_name);
        mangaAuthorTv = (TextView) findViewById(R.id.manga_author);
        mangaTypeTv = (TextView) findViewById(R.id.manga_type);
        lastUpdateTv = (TextView) findViewById(R.id.manga_update_date);
        collectV = findViewById(R.id.collect_view);

        collectV.setOnClickListener(this);
        topBar = (TopBar) findViewById(R.id.top_bar);
        topBar.setTitle(Globle.mangaTitle);
        topBar.setOnTopBarClickListener(new TopBar.OnTopBarClickListener() {

            @Override
            public void onRightClick() {
                showOptionsDialog();
            }

            @Override
            public void onTitleClick() {

            }

            @Override
            public void onLeftClick() {
                WebMangaDetailsActivity.this.finish();
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (chooseing) {
            if (choosePosition == 0) {
                Globle.startPoint = Integer.valueOf(mangaList.get(position).getTitle());
                choosePosition++;
            } else {
                Globle.mangaName = Globle.mangaTitle;
                Globle.endPoint = Integer.valueOf(mangaList.get(position).getTitle());
                Globle.friendlyDownload = true;
                Intent intent = new Intent(WebMangaDetailsActivity.this, ReptileMangaReaderActivity.class);
                startActivity(intent);
                WebMangaDetailsActivity.this.finish();
            }
        } else {
            Globle.sourceFrom = Globle.Source.web;
            Globle.mangaPath = mangaList.get(position).getPath();
            Globle.currentChapter = position + "";
            Intent intent = new Intent(WebMangaDetailsActivity.this, ReadMangaActivity.class);
            startActivity(intent);
        }
    }

    private void downloadAll() {
        Globle.startPoint = 1;
        Globle.mangaName = Globle.mangaTitle;
        Globle.endPoint = Integer.valueOf(mangaList.get(mangaList.size() - 1).getTitle());
        Globle.friendlyDownload = true;
        Intent intent = new Intent(WebMangaDetailsActivity.this, ReptileMangaReaderActivity.class);
        startActivity(intent);
        WebMangaDetailsActivity.this.finish();
    }

    private void showOptionsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(
                WebMangaDetailsActivity.this);
        builder.setTitle("选项");
        builder.setItems(optionsList, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        ToastUtil.tipShort(WebMangaDetailsActivity.this, "开始下载");
                        downloadAll();
                        break;
                    case 1:
                        chooseing = true;
                        ToastUtil.tipShort(WebMangaDetailsActivity.this, "请先点击起始话然后点击结束话!");
                        break;
                }
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.collect_view:
                if (db.queryCollected(Globle.mangaTitle)) {
                    db.deleteCollectByMangaName(Globle.mangaTitle);
                } else {
                    db.insertCollectTb(Globle.mangaPathThumbnail, Globle.mangaTitle);
                }
                cutCollect();
                break;
        }
    }

    private void cutCollect() {
        if (db.queryCollected(Globle.mangaTitle)) {
            collectV.setBackgroundResource(R.drawable.collected);
        } else {
            collectV.setBackgroundResource(R.drawable.collect);
        }
    }

    class MangaGridAdapter extends BaseAdapter {
        private Context context;
        private ImageSize imageSize;

        public MangaGridAdapter(Context context) {
            this.context = context;
            imageSize = new ImageSize(240, 400);
        }

        @Override
        public int getCount() {
            return mangaList.size();
        }

        @Override
        public Object getItem(int position) {
            return mangaList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(
                        R.layout.item_manga_web, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.manga_title = (TextView) convertView
                        .findViewById(R.id.chapter);

                convertView.setTag(viewHolder);
            } else {
                // 初始化过的话就直接获取
                viewHolder = (ViewHolder) convertView.getTag();
            }
            MangaBean item = mangaList.get(position);
            viewHolder.manga_title.setText("第" + item.getTitle() + "话");
            return convertView;
        }
    }

    private class ViewHolder {
        private TextView manga_title;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ImageLoader.getInstance().clearMemoryCache();
        ImageLoader.getInstance().clearDiskCache();
        Globle.friendlyDownload = false;
        db.closeDb();
    }
}
