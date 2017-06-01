package com.warrior.hangsu.administrator.mangaeasywatch.statistics;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.PointF;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.warrior.hangsu.administrator.mangaeasywatch.R;
import com.warrior.hangsu.administrator.mangaeasywatch.brokenline.paint.BrokenLineSet;
import com.warrior.hangsu.administrator.mangaeasywatch.brokenline.paint.BrokenLineView;
import com.warrior.hangsu.administrator.mangaeasywatch.db.DbAdapter;
import com.warrior.hangsu.administrator.mangaeasywatch.utils.BaseActivity;
import com.warrior.hangsu.administrator.mangaeasywatch.utils.ToastUtil;
import com.warrior.hangsu.administrator.mangaeasywatch.utils.TopBar;

import java.util.ArrayList;
import java.util.Arrays;

public class StatisticsActivity extends BaseActivity implements View.OnClickListener, View.OnLongClickListener, AdapterView.OnItemClickListener, OnItemLongClickListener {
    private TopBar topBar;
    private ArrayList<StatisticsBean> list = new ArrayList<StatisticsBean>();
    private ArrayList<StatisticsListViewBean> totalList = new ArrayList<StatisticsListViewBean>();
    private ArrayList<PointF> pointLineList = new ArrayList<PointF>();
    private String[] mangaNameList;
    private DbAdapter db;//数据库
    private BrokenLineView HPWNView;
    private ListView mangaLv;
    private MangaListAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statisctics);
        db = new DbAdapter(this);

        initUI();
        refresh();
    }

    private void refresh() {
        mangaNameList = db.queryAllMangaName();
        for (int i = 0; i < mangaNameList.length; i++) {
            StatisticsListViewBean item = new StatisticsListViewBean();
            item.setMangaName(mangaNameList[i]);
            ArrayList<StatisticsBean> hpwnList = db.queryHPWN(mangaNameList[i]);
            item.setList(hpwnList);
            int pageAmount = 0;
            float wordAmount = 0;
            for (int j = 0; j < hpwnList.size(); j++) {
                pageAmount = pageAmount + hpwnList.get(j).getPage_amount();
                wordAmount = wordAmount + (hpwnList.get(j).getHPWN() * ((float) (hpwnList.get(j).getPage_amount()) / 100));
            }
            item.setPageAmount(pageAmount);
            item.setWordAmount((int) wordAmount);
            totalList.add(item);
        }
        if (null != mangaNameList && mangaNameList.length > 0) {
            HPWNView.setVisibility(View.VISIBLE);
            refresh(0);
            topBar.setTitle(mangaNameList[0]);
        } else {
            topBar.setTitle("数据统计");
            HPWNView.setVisibility(View.GONE);
        }
        initListView();
    }

//    private void refresh(String mangaName) {
//        if (!TextUtils.isEmpty(mangaName)) {
//            list = db.queryHPWN(mangaName);
//            if (list.size() > 1) {
//                BrokenLineSet item = new BrokenLineSet();
//                item.setLineColor(getResources().getColor(R.color.top_bar));
//                item.setMarkColor(getResources().getColor(R.color.top_bar));
//                item.setAxleColor(getResources().getColor(R.color.top_bar));
//                item.setAxle_type(BrokenLineView.AxleType.HalfXY);
//                item.setX_text(list.get(0).getDate() + "到"
//                        + list.get(list.size() - 1).getDate());
//                item.setY_text("百页单词量");
//                int[] hpwnList = new int[list.size()];
//                for (int i = 0; i < list.size(); i++) {
//                    hpwnList[i] = list.get(i).getHPWN();
//                }
//                Arrays.sort(hpwnList);
//                int maxHPWN = hpwnList[hpwnList.length - 1];
//                int minHPWN = hpwnList[0];
//                if (maxHPWN != minHPWN) {
//                    item.setOrigin_y(minHPWN);
//                    item.setY_d_value(1);
//                    item.setY_count(maxHPWN - minHPWN);
//                    item.setOrigin_x(0);
//                    item.setY_d_value(1);
//                    item.setX_count(list.size() - 1);
//
//                    for (int i = 0; i < list.size(); i++) {
//                        Float x = Float.valueOf(i);
//                        Float y = Float.valueOf(list.get(i).getHPWN());
//                        pointLineList.add(new PointF(x, y));
//                    }
//                } else {
//                    //TODO
//                }
//
//                HPWNView.setBrokenLineSet(item);
//                HPWNView.setPointLineList(pointLineList);
//                HPWNView.invalidate();
//            } else if (list.size() == 1) {
//                //TODO
//            }
//
//        }
//    }

    private void refresh(int position) {
        if (!TextUtils.isEmpty(mangaNameList[position])) {
            list = totalList.get(position).getList();
            if (list.size() > 1) {
                HPWNView.setVisibility(View.VISIBLE);
                BrokenLineSet item = new BrokenLineSet();
                item.setLineColor(getResources().getColor(R.color.top_bar));
                item.setMarkColor(getResources().getColor(R.color.top_bar));
                item.setAxleColor(getResources().getColor(R.color.top_bar));
                item.setAxle_type(BrokenLineView.AxleType.HalfXY);
                item.setX_text(list.get(0).getDate() + "到"
                        + list.get(list.size() - 1).getDate());
                item.setY_text("百页单词量");
                int[] hpwnList = new int[list.size()];
                for (int i = 0; i < list.size(); i++) {
                    hpwnList[i] = list.get(i).getHPWN();
                }
                Arrays.sort(hpwnList);
                int maxHPWN = hpwnList[hpwnList.length - 1];
                int minHPWN = hpwnList[0];
                if (maxHPWN != minHPWN) {
                    item.setOrigin_y(minHPWN);
                    item.setY_d_value(1);
                    item.setY_count(maxHPWN - minHPWN);
                    item.setOrigin_x(0);
                    item.setY_d_value(1);
                    item.setX_count(list.size() - 1);
                    pointLineList.clear();
                    for (int i = 0; i < list.size(); i++) {
                        Float x = Float.valueOf(i);
                        Float y = Float.valueOf(list.get(i).getHPWN());
                        pointLineList.add(new PointF(x, y));
                    }
                } else {
                    ToastUtil.tipShort(StatisticsActivity.this, "百页单词量:" + list.get(0).getHPWN());
                    HPWNView.setVisibility(View.GONE);
                }

                HPWNView.setBrokenLineSet(item);
                HPWNView.setPointLineList(pointLineList);
                HPWNView.invalidate();
            } else if (list.size() == 1) {
                ToastUtil.tipShort(StatisticsActivity.this, "百页单词量:" + list.get(0).getHPWN());
                HPWNView.setVisibility(View.GONE);
            }

        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        refresh(i);
        topBar.setTitle(mangaNameList[i]);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
        showDeleteDialog(topBar.getTitle());
        return true;
    }

    /**
     * arraylistadapter
     */
    private class MangaListAdapter extends BaseAdapter {
        private Context context;
        private ArrayList<StatisticsListViewBean> list;

        public MangaListAdapter(Context context, ArrayList<StatisticsListViewBean> list) {
            this.context = context;
            this.list = list;
        }

        public void setList(ArrayList<StatisticsListViewBean> list) {
            this.list = list;
        }

        public ArrayList<StatisticsListViewBean> getList() {
            return list;
        }

        @Override
        public int getCount() {
            if (null == list || list.size() == 0)
                return 0;
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {

                convertView = LayoutInflater.from(context).inflate(
                        R.layout.item_listview_manga, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.mangaName_tv = (TextView) convertView
                        .findViewById(R.id.manga_name);
                viewHolder.page_amount_tv = (TextView) convertView
                        .findViewById(R.id.page_amount_tv);
                viewHolder.word_amount_tv = (TextView) convertView
                        .findViewById(R.id.word_amount_tv);

                convertView.setTag(viewHolder);
            } else {
                // 初始化过的话就直接获取
                viewHolder = (ViewHolder) convertView.getTag();
            }
            StatisticsListViewBean item = list.get(position);
            viewHolder.mangaName_tv.setText(item.getMangaName());
            viewHolder.page_amount_tv.setText(item.getPageAmount() + "");
            viewHolder.word_amount_tv.setText(item.getWordAmount() + "");
            return convertView;
        }
    }

    private class ViewHolder {
        TextView mangaName_tv;
        TextView page_amount_tv;
        TextView word_amount_tv;
    }

    public static int getMaxNum(int... a) {
        Arrays.sort(a);
        int maxNum = a[a.length - 1];
        return maxNum;
    }

    public static int getMinNum(int... a) {
        Arrays.sort(a);
        int minNum = a[0];
        return minNum;
    }

    private void initUI() {
        HPWNView = (BrokenLineView) findViewById(R.id.hpwn_view);
        HPWNView.setOnLongClickListener(this);
        mangaLv = (ListView) findViewById(R.id.manga_list);

        topBar = (TopBar) findViewById(R.id.top_bar);
        topBar.setOnTopBarClickListener(new TopBar.OnTopBarClickListener() {

            @Override
            public void onRightClick() {
//                showMangaNameListDialog();
            }

            @Override
            public void onTitleClick() {
//                showMangaNameListDialog();
            }

            @Override
            public void onLeftClick() {
                StatisticsActivity.this.finish();
            }
        });
    }

    private void initListView() {
        if (null == adapter) {
            adapter = new MangaListAdapter(StatisticsActivity.this,
                    totalList);
            mangaLv.setAdapter(adapter);
            mangaLv.setOnItemClickListener(this);
            mangaLv.setOnItemLongClickListener(this);
        } else {
            adapter.setList(totalList);
            adapter.notifyDataSetChanged();
        }
    }

//    private void showMangaNameListDialog() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(
//                StatisticsActivity.this);
////        builder.setTitle("选项");
//        String s = "";
//        for (int i = 0; i < mangaNameList.length; i++) {
//            if (!TextUtils.isEmpty(mangaNameList[i])) {
//                if (i == mangaNameList.length - 1) {
//                    s = s + mangaNameList[i];
//                } else {
//                    s = s + mangaNameList[i] + ",";
//                }
//            }
//        }
//        String[] list = {s};
//        builder.setItems(list, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                topBar.setTitle(mangaNameList[which]);
//                refresh(mangaNameList[which]);
//            }
//        });
//        AlertDialog dialog = builder.create();
//        dialog.setCancelable(true);
//        dialog.show();
//    }

    private void showDeleteDialog(final String mangaName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("是否删除该漫画所有数据?");
        builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                db.deleteStatiscticsByMangaName(mangaName);
                refresh();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.closeDb();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

        }
    }

    @Override
    public boolean onLongClick(View view) {
        switch (view.getId()) {
            case R.id.hpwn_view:
                showDeleteDialog(topBar.getTitle());
                break;
        }
        return true;
    }
}
