package com.warrior.hangsu.administrator.mangaeasywatch.onemangalist;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.warrior.hangsu.administrator.mangaeasywatch.R;
import com.warrior.hangsu.administrator.mangaeasywatch.db.DbAdapter;
import com.warrior.hangsu.administrator.mangaeasywatch.mangalist.MangaBean;
import com.warrior.hangsu.administrator.mangaeasywatch.readmanga.ReadMangaActivity;
import com.warrior.hangsu.administrator.mangaeasywatch.utils.BaseActivity;
import com.warrior.hangsu.administrator.mangaeasywatch.utils.FileUtil;
import com.warrior.hangsu.administrator.mangaeasywatch.utils.Globle;
import com.warrior.hangsu.administrator.mangaeasywatch.utils.SharedPreferencesUtils;
import com.warrior.hangsu.administrator.mangaeasywatch.utils.ToastUtil;
import com.warrior.hangsu.administrator.mangaeasywatch.utils.TopBar;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MangaDetailsActivity extends BaseActivity implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {
    private GridView mangaGV;
    private View reptile;
    private TopBar topBar;
    private ArrayList<MangaBean> mangaList = new ArrayList<MangaBean>();
    private MangaGridAdapter adapter;
    private File[] files;
    private DbAdapter db;//数据库

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manga_details);
        initUI();
        ToastUtil.tipShort(this,"另一个类 gif");
//        // 用于判断是否第一次运行
//        String isRun = SharedPreferencesUtils.getSharedPreferencesData(this,
//                "isrun");
//        if (TextUtils.isEmpty(isRun)) { // 应用第一次运行时执行
//            SharedPreferencesUtils.setSharedPreferencesData(this, "isrun",
//                    "had run");
        initFile();
//        }
        db = new DbAdapter(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initFile() {
        mangaList.clear();
        File f = new File(Globle.mangaPathThumbnail);
        int l = f.toString().length() + 1;
        files = f.listFiles();//某具体漫画内部 如one_piece文件夹
        MangaBean item = new MangaBean();


//        ToastUtil.tipShort(this, files.length + "最后一个" + files[files.length - 1].toString().substring(l, files[files.
//                length - 1].toString().length()) + "第一个" + files[0].toString().substring(l,
//                files[0].toString().length()));


        if (files.length > 0 && isAllPic(files)) {
            //因为这里边是乱排序的 所以我只能遍历一遍了
            //只有全部是图片的时候才用这个
//            bp = ImageUtil.getImageFromSDFile(files[0].toString());
            //取得缩略图
            item.setBpPath(files[0].toString());
            //取得标题
            item.setTitle("全部");
            mangaList.add(item);
            Globle.mangaPath = Globle.mangaPathThumbnail;
        } else if (files.length > 0) {
            //因为这里边是乱排序的 所以我只能遍历一遍了
            //只要包含文件夹 那就采用这种方式
            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) {
                    //这个判断是为了应对既有文件夹又有图片的情况
                    item = new MangaBean();

                    String title = files[i].toString();
                    title = title.substring(l, title.length());

                    File[] files2 = files[i].listFiles();
//                bp = ImageUtil.getImageFromSDFile(files2[0].toString());
//                bp = ImageLoader.getInstance().loadImageSync("file://" + files2[0].toString(), new ImageSize(120, 200), Globle.options);
                    item.setBpPath(files2[0].toString());
                    item.setTitle(title);
                    mangaList.add(item);
                }
            }
        }
        initGridView();
    }

    private boolean isAllPic(File[] files) {
        boolean allPic = true;
        for (int i = 0; i < files.length; i++) {
            if (files[i].isDirectory()) {
                allPic = false;
            }
        }
        return allPic;
    }

    private void initGridView() {
        if (null == adapter) {
            adapter = new MangaGridAdapter(this);
            mangaGV.setAdapter(adapter);
            mangaGV.setOnItemClickListener(this);
            mangaGV.setOnItemLongClickListener(this);
        } else {
            adapter.notifyDataSetChanged();
        }
    }

    private void initUI() {
        mangaGV = (GridView) findViewById(R.id.manga_gv);

        topBar = (TopBar) findViewById(R.id.top_bar);
        topBar.setTitle(Globle.mangaTitle);
        topBar.setOnTopBarClickListener(new TopBar.OnTopBarClickListener() {

            @Override
            public void onRightClick() {
                initFile();
            }

            @Override
            public void onTitleClick() {

            }

            @Override
            public void onLeftClick() {
                MangaDetailsActivity.this.finish();
            }
        });
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Globle.sourceFrom = Globle.Source.local;
        if (!mangaList.get(0).getTitle().equals("全部")) {
            Globle.mangaPath = files[position].toString();
        }
        Intent intent = new Intent(MangaDetailsActivity.this, ReadMangaActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
        showDeleteDialog(i);
//        insertStatiscticsTb(i);
        return true;
    }

    private void showDeleteDialog(final int i) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("是否删除?");
        builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                insertStatiscticsTb(i);
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

    //TODO
    private void insertStatiscticsTb(final int i) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        String date = sdf.format(curDate);
        File[] childFile = files[i].listFiles();
        int page_amount = childFile.length;
        int queryWordCount = SharedPreferencesUtils.getIntSharedPreferencesData
                (this, files[i].toString() + "queryWordCount");
        int HPWN = Math.round((queryWordCount * 100) / page_amount);
        db.insertStatiscticsTb(date, Globle.mangaTitle, HPWN, page_amount);
        SharedPreferencesUtils.setSharedPreferencesData
                (MangaDetailsActivity.this, files[i].toString() + "queryWordCount", 0);
        ToastUtil.tipShort(MangaDetailsActivity.this, "漫画:" + Globle.mangaTitle + "百页单词量:"
                + HPWN + "日期:" + date + "页数:" + page_amount);
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
//            viewHolder.manga_view.setBackground(item.getBpPath());
//            ImageLoader.getInstance().displayImage(item.getBpPath(), viewHolder.manga_view, Globle.options);
            ImageLoader.getInstance().displayImage(item.getBpPath(), new ImageViewAware
                    (viewHolder.manga_view), Globle.options, imageSize, null, null);
            viewHolder.manga_title.setText(item.getTitle());
            return convertView;
        }
    }

    private class ViewHolder {
        private ImageView manga_view;
        private TextView manga_title;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ImageLoader.getInstance().clearMemoryCache();
        ImageLoader.getInstance().clearDiskCache();
        db.closeDb();
    }
}
