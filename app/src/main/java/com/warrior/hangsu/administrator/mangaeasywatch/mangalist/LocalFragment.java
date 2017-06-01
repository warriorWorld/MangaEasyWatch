package com.warrior.hangsu.administrator.mangaeasywatch.mangalist;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
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
import com.warrior.hangsu.administrator.mangaeasywatch.onemangalist.MangaDetailsActivity;
import com.warrior.hangsu.administrator.mangaeasywatch.utils.FileUtil;
import com.warrior.hangsu.administrator.mangaeasywatch.utils.Globle;

import java.io.File;
import java.util.ArrayList;

public class LocalFragment extends Fragment implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {
    private View mainView;
    private View emptyView;
    private ImageView emptyIV;
    private TextView emptyTV;
    private GridView mangaGV;
    private ArrayList<MangaBean> mangaList = new ArrayList<MangaBean>();
    private MangaGridAdapter adapter;
    private static final String DST_FOLDER_NAME = "reptile";
    private File[] files;//第二级目录 具体漫画们
    private int l;//仅用于截取长度


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.activity_local, null);
        initUI(mainView);
        initGridView();

        initFile();
        return mainView;
    }

    public void initFile() {
        mangaList.clear();
        File parentPath = Environment
                .getExternalStorageDirectory();
        String storagePath = parentPath.getAbsolutePath() + "/" + DST_FOLDER_NAME;
        File f = new File(storagePath);//第一级目录 reptile
        if (!f.exists()) {
            f.mkdirs();
        }
        l = f.toString().length() + 1;
        files = f.listFiles();//第二级目录 具体漫画们
        if (null != files && files.length > 0) {
            for (int i = 0; i < files.length; i++) {
                Bitmap bp = null;
                MangaBean item = new MangaBean();

                String title = files[i].toString();
                title = title.substring(l, title.length());
//            ToastUtil.tipShort(this, title);
                File[] files1 = files[i].listFiles();//第三级目录 某具体漫画内部
                if (files1.length > 0 && !files1[0].isDirectory()) {
                    //如果某漫画文件夹第一次目录就直接是图片文件
//                bp = ImageUtil.getImageFromSDFile(files1[0].toString());
                    item.setBpPath(files1[0].toString());
                    item.setTitle(title);
                    mangaList.add(item);
                } else if (files1.length > 0 && files1[0].isDirectory()) {
                    //二级文件夹
                    File[] files2 = files1[0].listFiles();//第三级目录 某具体漫画内部的内部
//                bp = ImageUtil.getImageFromSDFile(files2[0].toString());
                    item.setBpPath(files2[0].toString());
                    item.setTitle(title);
                    mangaList.add(item);
                } else {
                    //空文件夹
                    item.setTitle(title);
                    mangaList.add(item);
                }
            }
        }
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
        emptyIV.setBackgroundResource(R.drawable.local_empty);
        emptyTV.setText("还没有本地漫画哦~");
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Globle.mangaPathThumbnail = files[position].toString();
        String title = files[position].toString();
        title = title.substring(l, title.length());
        Globle.mangaTitle = title;
        Intent intent = new Intent(getActivity(), MangaDetailsActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
        showDeleteDialog(i);
        return true;
    }

    private void showDeleteDialog(final int i) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("是否删除?");
        builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                FileUtil.deleteFile(files[i]);
                initFile();
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
            if (!TextUtils.isEmpty(item.getBpPath())) {
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
    }
}
