package com.warrior.hangsu.administrator.mangaeasywatch.mangalist;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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
import com.warrior.hangsu.administrator.mangaeasywatch.db.DbAdapter;
import com.warrior.hangsu.administrator.mangaeasywatch.onemangalist.MangaDetailsActivity;
import com.warrior.hangsu.administrator.mangaeasywatch.readmanga.ReadMangaActivity;
import com.warrior.hangsu.administrator.mangaeasywatch.utils.FileUtil;
import com.warrior.hangsu.administrator.mangaeasywatch.utils.Globle;
import com.warrior.hangsu.administrator.mangaeasywatch.webmangadetails.WebMangaDetailsActivity;

import java.io.File;
import java.util.ArrayList;

public class CollectFragment extends Fragment implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {
    private View mainView;
    private View emptyView;
    private ImageView emptyIV;
    private TextView emptyTV;
    private GridView mangaGV;
    private ArrayList<MangaBean> mangaList = new ArrayList<MangaBean>();
    private MangaGridAdapter adapter;
    private DbAdapter db;//数据库


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.activity_local, null);
        db = new DbAdapter(getActivity());

        initUI(mainView);
        initGridView();

        initCollect();
        return mainView;
    }

    private void initFavorites() {
        ArrayList<String> favorites = db.queryAllFavorite();
        if (null != favorites && favorites.size() > 0) {
            MangaBean item = new MangaBean();
            item.setTitle("喜欢");
            item.setBpPath(favorites.get(0));
            mangaList.add(item);
            initGridView();
        }
    }

    public void initCollect() {
        mangaList.clear();
        mangaList = db.queryAllCollect();
        initFavorites();
        initGridView();
    }

    private void initGridView() {
        if (null == adapter) {
            adapter = new MangaGridAdapter(getActivity());
            mangaGV.setAdapter(adapter);
            mangaGV.setOnItemClickListener(this);
            mangaGV.setOnItemLongClickListener(this);
            mangaGV.setEmptyView(emptyView);
        } else {
            adapter.notifyDataSetChanged();
        }
    }


    private void initUI(View v) {
        mangaGV = (GridView) v.findViewById(R.id.manga_gv);
        emptyView = v.findViewById(R.id.empty_view);
        emptyIV = (ImageView) v.findViewById(R.id.image);
        emptyTV = (TextView) v.findViewById(R.id.text);
        emptyIV.setBackgroundResource(R.drawable.collect_empty);
        emptyTV.setText("还没有收藏漫画哦~");
    }

    private void showDeleteDialog(final int i) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("是否删除?");
        builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                db.deleteCollectByMangaName(mangaList.get(i).getTitle());
                initCollect();
            }
        });
        builder.setNegativeButton("否", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if ("喜欢".equals(mangaList.get(position).getTitle())) {
            Globle.sourceFrom = Globle.Source.favorite;

            Intent intent = new Intent(getActivity(), ReadMangaActivity.class);
            startActivity(intent);
        } else {
            Globle.mangaPathThumbnail = mangaList.get(position).getWebBpPath();
            String title = mangaList.get(position).getTitle();
            Globle.mangaTitle = title;
            Intent intent = new Intent(getActivity(), WebMangaDetailsActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
        showDeleteDialog(i);
        return true;
    }


    class MangaGridAdapter extends BaseAdapter {
        private Context context;

        public MangaGridAdapter(Context context) {
            this.context = context;
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
            MangaBean item = mangaList.get(position);
            if (!TextUtils.isEmpty(item.getWebBpPath())) {
                ImageLoader.getInstance().displayImage(item.getWebBpPath(), viewHolder.manga_view, Globle.options);
            } else if (!TextUtils.isEmpty(item.getBpPath())) {
                ImageLoader.getInstance().displayImage(item.getBpPath(), viewHolder.manga_view, Globle.options);
            }
            viewHolder.manga_title.setText(item.getTitle());
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
        db.closeDb();
    }
}
