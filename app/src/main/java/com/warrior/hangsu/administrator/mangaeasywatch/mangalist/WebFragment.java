package com.warrior.hangsu.administrator.mangaeasywatch.mangalist;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.warrior.hangsu.administrator.mangaeasywatch.R;
import com.warrior.hangsu.administrator.mangaeasywatch.utils.Globle;
import com.warrior.hangsu.administrator.mangaeasywatch.utils.StringUtil;
import com.warrior.hangsu.administrator.mangaeasywatch.webmangadetails.WebMangaDetailsActivity;

import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public class WebFragment extends Fragment implements AdapterView.OnItemClickListener {
    private View mainView;
    private View emptyView;
    private GridView mangaGV;
    private ArrayList<MangaBean> mangaList = new ArrayList<MangaBean>();
    private MangaGridAdapter adapter;
    private org.jsoup.nodes.Document doc;
    private Handler handler1 = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (Integer.valueOf(msg.obj.toString())) {
                case 0:
                    initGridView();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mangaGV.setSelection(0);
                        }
                    }, 250);

                    break;
                case 1:
                    break;
            }
        }
    };

    public int page = 0;//当前页
    public int onePageSize = 30;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.activity_local, null);
        initUI(mainView);
        initGridView();

        initWebManga();
        return mainView;
    }

    public void initWebManga() {
        //每次翻页清理内存 合理清理imageloader缓存 使反应速度变快很多
        ImageLoader.getInstance().clearMemoryCache();
        ImageLoader.getInstance().clearDiskCache();
        new Thread() {
            @Override
            public void run() {
                try {
                    if (TextUtils.isEmpty(Globle.mangaTypeCode) || Globle.mangaTypeCode.equals("all")) {
                        doc = Jsoup.connect("http://www.mangareader.net/popular/" + page)
                                .timeout(10000).get();
                    } else {
                        doc = Jsoup.connect("http://www.mangareader.net/popular/" + Globle.mangaTypeCode + "/" + page)
                                .timeout(10000).get();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (null != doc) {
                    Elements test = doc.select("div.mangaresultitem h3 a");
                    Elements test1 = doc.select("div.imgsearchresults");
                    int count = test.size();
                    String title;
                    String path;
                    MangaBean item;
                    mangaList.clear();
                    for (int i = 0; i < count; i++) {
                        title = test.get(i).attr("href");
                        if (!TextUtils.isEmpty(title) && !title.equals("null")) {
                            item = new MangaBean();
                            title = StringUtil.cutString(title, 1, title.length());
                            path = test1.get(i).attr("style");
                            path = StringUtil.cutString(path, 22,
                                    path.length() - 2);
                            item.setWebBpPath(path);
                            item.setTitle(title);
                            mangaList.add(item);
                        }
                    }
                    onePageSize = mangaList.size();
                    Message msg = handler1.obtainMessage();
                    msg.obj = 0;
                    msg.sendToTarget();
                }
            }
        }.start();

    }

    private void initGridView() {
        if (null == adapter) {
            adapter = new MangaGridAdapter(getActivity());
            mangaGV.setAdapter(adapter);
            mangaGV.setOnItemClickListener(this);
            mangaGV.setEmptyView(emptyView);
        } else {
            adapter.notifyDataSetChanged();
        }
    }


    private void initUI(View v) {
        mangaGV = (GridView) v.findViewById(R.id.manga_gv);
        emptyView = v.findViewById(R.id.empty_view);
        emptyView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initWebManga();
            }
        });
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position == mangaList.size()) {
            page = page + mangaList.size();
            initWebManga();
        } else {
            Globle.mangaPathThumbnail = mangaList.get(position).getWebBpPath();
            String title = mangaList.get(position).getTitle();
            Globle.mangaTitle = title;
            Intent intent = new Intent(getActivity(), WebMangaDetailsActivity.class);
            startActivity(intent);
        }
    }


    class MangaGridAdapter extends BaseAdapter {
        private Context context;

        public MangaGridAdapter(Context context) {
            this.context = context;
        }

        @Override
        public int getCount() {
            if (mangaList.size() == 0) {
                return 0;
            } else {
                return mangaList.size() + 1;
            }
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
                        R.layout.item_manga, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.manga_view = (ImageView) convertView
                        .findViewById(R.id.manga_view);
                viewHolder.manga_title = (TextView) convertView
                        .findViewById(R.id.manga_title);

                convertView.setTag(viewHolder);
            } else {
                // 初始化过的话就直接获取
                viewHolder = (ViewHolder) convertView.getTag();
            }
            if (mangaList.size() > 0) {
                if (position != mangaList.size()) {
                    MangaBean item = mangaList.get(position);
                    if (!TextUtils.isEmpty(item.getWebBpPath())) {
                        ImageLoader.getInstance().displayImage(item.getWebBpPath(), viewHolder.manga_view, Globle.options);
                    }
                    viewHolder.manga_title.setText(item.getTitle());
                } else {
                    viewHolder.manga_view.setImageResource(R.drawable.next);
                    int p = (int) ((page + mangaList.size()) / mangaList.size());
                    viewHolder.manga_title.setText("下一页(" + p + ")");
                    viewHolder.manga_title.setTextSize(15);
                }
            }
            return convertView;
        }
    }

    private class ViewHolder {
        private ImageView manga_view;
        private TextView manga_title;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ImageLoader.getInstance().clearMemoryCache();
        ImageLoader.getInstance().clearDiskCache();
    }
}
