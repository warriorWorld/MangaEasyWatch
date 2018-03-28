package com.warrior.hangsu.administrator.mangaeasywatch.mangalist;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.wandoujia.ads.sdk.Ads;
import com.warrior.hangsu.administrator.mangaeasywatch.R;
import com.warrior.hangsu.administrator.mangaeasywatch.options.OptionsActivity;
import com.warrior.hangsu.administrator.mangaeasywatch.readmanga.DepthPageTransformer;
import com.warrior.hangsu.administrator.mangaeasywatch.reptile.ReptileListActivity;
import com.warrior.hangsu.administrator.mangaeasywatch.reptile.ReptileMangaActivity;
import com.warrior.hangsu.administrator.mangaeasywatch.reptile.ReptileMangaReaderActivity;
import com.warrior.hangsu.administrator.mangaeasywatch.sort.FileComparator3;
import com.warrior.hangsu.administrator.mangaeasywatch.teach.TeachActivity;
import com.warrior.hangsu.administrator.mangaeasywatch.utils.BaseActivity;
import com.warrior.hangsu.administrator.mangaeasywatch.utils.BaseParameterUtil;
import com.warrior.hangsu.administrator.mangaeasywatch.utils.Globle;
import com.warrior.hangsu.administrator.mangaeasywatch.utils.SharedPreferencesUtils;
import com.warrior.hangsu.administrator.mangaeasywatch.utils.ToastUtil;
import com.warrior.hangsu.administrator.mangaeasywatch.utils.TopBar;
import com.warrior.hangsu.administrator.mangaeasywatch.webmangadetails.WebMangaDetailsActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends BaseActivity implements View.OnClickListener, View.OnLongClickListener {
    private View reptile, reptileing, optionsV;
    private TopBar topBar;
    private List<Fragment> fragList;
    private List<String> titleList;
    private ViewPager pager;
    private CollectFragment collectFragment = new CollectFragment();
    private LocalFragment localFragment = new LocalFragment();
    private WebFragment webFragment = new WebFragment();
    private String[] optionsList = {"切换站点", "搜索", "分类", "跳转", "刷新"};
    private int nowPosition;
    private String[] mangaTypeNames = {"全部", "战斗", "冒险", "搞笑", "恶魔", "剧情", "杀必死", "幻想", "掰弯",
            "后宫", "历史", "恐怖", "言情", "魔幻", "战争", "成年", "机战", "军事", "秘密", "一击",
            "心理", "浪漫", "校园", "科幻", "青年", "少女", "少女爱", "少年", "少年爱", "生活", "爱情", "体育",
            "超能力", "超自然", "悲剧", "吸血鬼", "耽美", "百合"};
    private String[] mangaTypeCodes = {"all", "action", "adventure", "comedy", "demons", "drama", "ecchi", "fantasy", "gender-bender",
            "harem", "historical", "horror", "josei", "magic", "martial-arts", "mature", "mecha", "military", "mystery", "one-shot",
            "psychological", "romance", "school-life", "sci-fi", "seinen", "shoujo", "shoujoai", "shounen", "shounenai", "slice-of-life", "smut", "sports",
            "super-power", "supernatural", "tragedy", "vampire", "yaoi", "yuri"};
    /**
     * 豌豆荚接入广告
     *
     * @param savedInstanceState
     */
    private static final String APP_ID = "100042330";
    private static final String SECRET_KEY = "c8195274e669a7fdd38f346370b743b0";
    private static final String BANNER = "920ba5788da5ae09b6b718e6c45ac4f9";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUI();
        //我来试一下家里的电脑
        if (!isNetworkConnected()) {
            nowPosition = 1;
        } else {
            nowPosition = 0;
            topBar.setRightText("选项");
            ToastUtil.tipLong(MainActivity.this, "本应用漫画资源全部来自www.mangareader.net,用户也可直接通过浏览器浏览.");
        }
        // 获取版本名称
        try {
            PackageManager manager = this.getPackageManager();
            PackageInfo info;
            info = manager.getPackageInfo(MainActivity.this.getPackageName(), 0);
            Globle.versionName = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        initVP();
        initAd();
        // 用于判断是否第一次运行
        String isRun = SharedPreferencesUtils.getSharedPreferencesData(this,
                "isrun");
        if (TextUtils.isEmpty(isRun) || isRun.equals("reject")) { // 应用第一次运行时执行
            //只有接受条款的才能继续运行
            showExceptionsDialog();
        }
    }

    /**
     * 检测网络是否可用
     *
     * @return
     */
    public boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(this.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return ni != null && ni.isConnectedOrConnecting();
    }

    private void initVP() {
        // 构建底部标题栏
        titleList = new ArrayList<String>();
        titleList.add("网上资源");
        titleList.add("本地漫画");
        titleList.add("我的收藏");

        // 构建viewpager
        fragList = new ArrayList<Fragment>();
        fragList.add(webFragment);
        fragList.add(localFragment);
        fragList.add(collectFragment);

        MainAdapter adapter = new MainAdapter(
                getSupportFragmentManager(), fragList, titleList);
        pager.setAdapter(adapter);
        pager.setOffscreenPageLimit(2);
        pager.setCurrentItem(nowPosition);
        pager.setPageTransformer(true, new DepthPageTransformer());
        pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                nowPosition = position;
                switch (position) {
                    case 0:
                        topBar.setRightText("选项");
                        ToastUtil.tipLong(MainActivity.this, "本应用漫画资源全部来自www.mangareader.net,用户也可直接通过浏览器浏览.");
                        if (TextUtils.isEmpty(Globle.mangaTypeName)) {
                            topBar.setTitle("网络漫画(全部)");
                        } else {
                            topBar.setTitle("网络漫画(" + Globle.mangaTypeName + ")");
                        }
                        cutReptileOptions(true);
                        break;
                    case 1:
                        topBar.setRightBackground(getResources().getDrawable(R.drawable.refresh));
                        topBar.setTitle("本地漫画");
                        cutReptileOptions(true);
                        break;
                    case 2:
                        topBar.setRightBackground(getResources().getDrawable(R.drawable.refresh));
                        topBar.setTitle("收藏列表");
                        cutReptileOptions(false);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void initAd() {
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                try {
                    Ads.init(MainActivity.this, APP_ID, SECRET_KEY);
                    return true;
                } catch (Exception e) {
                    Log.e("ads-sample", "error", e);
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean success) {
                final ViewGroup container = (ViewGroup) findViewById(R.id.banner);

                if (success) {
                    /**
                     * pre load
                     */
                    Ads.preLoad(BANNER, Ads.AdFormat.banner);

                    /**
                     * add ad views
                     */
                    View bannerView = Ads.createBannerView(MainActivity.this, BANNER);
                    container.addView(bannerView, new ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                    ));

                } else {
                    TextView errorMsg = new TextView(MainActivity.this);
                    errorMsg.setText("init failed");
                    container.addView(errorMsg);
                }
            }
        }.execute();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Globle.reptileing) {
            reptile.setBackgroundResource(R.drawable.reptileing);
            reptileing.setVisibility(View.VISIBLE);
        } else {
            reptile.setBackgroundResource(R.drawable.reptile);
            reptileing.setVisibility(View.GONE);
        }
        ToastUtil.tipLong(this, BaseParameterUtil.getInstance(this).getIPAddress(this));
    }

    private void initUI() {
        pager = (ViewPager) findViewById(R.id.view_pager);
        reptile = findViewById(R.id.reptile);
        reptileing = findViewById(R.id.reptileing);
        optionsV = findViewById(R.id.options);

        reptile.setOnClickListener(this);
        reptile.setOnLongClickListener(this);
        optionsV.setOnClickListener(this);
        optionsV.setOnLongClickListener(this);


        topBar = (TopBar) findViewById(R.id.top_bar);
        topBar.setOnTopBarClickListener(new TopBar.OnTopBarClickListener() {

            @Override
            public void onRightClick() {
                switch (nowPosition) {
                    case 0:
                        showOptionsDialog();
                        break;
                    case 1:
                        localFragment.initFile();
                        break;
                    case 2:
                        collectFragment.initCollect();
                        break;
                }
            }

            @Override
            public void onTitleClick() {

            }

            @Override
            public void onLeftClick() {
                showLogoutDialog();
            }
        });
        topBar.setTopBarLongClickLister(new TopBar.OnTopBarLongClickListener() {
            @Override
            public void onLeftLongClick() {

            }

            @Override
            public void onRightLongClick() {

            }

            @Override
            public void onTitleLongClick() {
                showSortAndRenameFilesDialog();
            }
        });
    }

    private void cutReptileOptions(boolean isReptile) {
        if (isReptile) {
            reptile.setVisibility(View.VISIBLE);
            optionsV.setVisibility(View.GONE);
        } else {
            reptile.setVisibility(View.GONE);
            optionsV.setVisibility(View.VISIBLE);
        }
    }

    //TODO
    private void sortAndRenameFile(String manganame) {
        String oldPath = Environment
                .getExternalStorageDirectory().getAbsolutePath() + "/" + "reptile"
                + "/" + "download";
        String newPath = Environment
                .getExternalStorageDirectory().getAbsolutePath() + "/" + "reptile"
                + "/" + manganame;
        File f = new File(oldPath);
        File[] files = f.listFiles();
        ArrayList<File> fileArrayList = new ArrayList<File>();
        for (int i = 0; i < files.length; i++) {
            fileArrayList.add(files[i]);
        }
        Collections.sort(fileArrayList, new FileComparator3());//通过重写Comparator的实现类

        //如果子目录不存在 建立一个子目录
        File folder = new File(newPath);
        if (!folder.exists()) {
            folder.mkdirs();
        } else {
            ToastUtil.tipShort(MainActivity.this, "该文件夹已存在,请重新命名!");
            return;
        }
        for (int i = 0; i < fileArrayList.size(); i++) {
            File to = new File(newPath, manganame + "(" + i + ")" + ".jpg");

            fileArrayList.get(i).renameTo(to);
        }
        ToastUtil.tipShort(MainActivity.this, "完成");
    }


    //TODO
    private void showSortAndRenameFilesDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.dialog_editnum, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("是否按修改时间重新排序?");
        builder.setView(view);
        final EditText edit = (EditText) view
                .findViewById(R.id.edit_name_dialog);
        edit.setInputType(InputType.TYPE_CLASS_TEXT);
        edit.setHint("如one piece");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                sortAndRenameFile(edit.getText().toString());
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showLogoutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("是否退出?");
        builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MainActivity.this.finish();
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

    private void showOptionsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(
                MainActivity.this);
        builder.setTitle("选项");
        builder.setItems(optionsList, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        ToastUtil.tipShort(MainActivity.this, "目前只有这一个站点");
                        break;
                    case 1:
                        showSeachDialog();
                        ToastUtil.tipShort(MainActivity.this, "请输入准确的漫画名称,单词间需加空格");
                        break;
                    case 2:
                        showMangaTypeDialog();
                        break;
                    case 3:
                        showSkipDialog();
                        break;
                    case 4:
                        webFragment.initWebManga();
                        break;
                }
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showMangaTypeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(
                MainActivity.this);
        builder.setTitle("选择漫画类型");
        builder.setItems(mangaTypeNames, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Globle.mangaTypeCode = mangaTypeCodes[which];
                Globle.mangaTypeName = mangaTypeNames[which];
                webFragment.page = 0;
                webFragment.initWebManga();
                topBar.setTitle("漫画列表(" + Globle.mangaTypeName + ")");
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * 点击跳转
     *
     * @param
     */
    private void showSkipDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.dialog_editnum, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("跳转");
        builder.setView(view);
        final EditText edit = (EditText) view
                .findViewById(R.id.edit_name_dialog);
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                int res = Integer.valueOf(edit.getText().toString());
                webFragment.page = (int) ((res - 1) * webFragment.onePageSize);
                webFragment.initWebManga();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * 搜索功能 TODO
     *
     * @param
     */
    private void showSeachDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.dialog_editnum, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("搜索漫画");
        builder.setView(view);
        final EditText edit = (EditText) view
                .findViewById(R.id.edit_name_dialog);
        edit.setInputType(InputType.TYPE_CLASS_TEXT);
        edit.setHint("如one piece");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Globle.mangaPathThumbnail = "nothing";
                String title = edit.getText().toString();
                title = title.replaceAll(" ", "-");
                Globle.mangaTitle = title;
                Intent intent = new Intent(MainActivity.this, WebMangaDetailsActivity.class);
                startActivity(intent);
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showExceptionsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("免责条款");
        builder.setMessage("本应用全部资源来自mangareader网站,并且包含'下载'功能,凡以本应用下载的漫画不得用于商业用途,如若违反与本应用无关,与本应用作者无关.\n作者:苏航\nQQ:772192594");
        builder.setPositiveButton("接受本条款", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                SharedPreferencesUtils.setSharedPreferencesData(MainActivity.this, "isrun",
                        "accept");
                Intent intent = new Intent(MainActivity.this, TeachActivity.class);
                startActivity(intent);
            }
        });
        builder.setNegativeButton("拒绝", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                SharedPreferencesUtils.setSharedPreferencesData(MainActivity.this, "isrun",
                        "reject");
                MainActivity.this.finish();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();
    }


    @Override
    public boolean onLongClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.reptile:
                intent = new Intent(MainActivity.this, ReptileMangaActivity.class);
                startActivity(intent);
                break;
            case R.id.options:
                intent = new Intent(MainActivity.this, ReptileListActivity.class);
                startActivity(intent);
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        showLogoutDialog();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.reptile:
                Intent intent = new Intent(MainActivity.this, ReptileMangaReaderActivity.class);
                startActivity(intent);
                break;
            case R.id.options:
                Intent intent1 = new Intent(MainActivity.this, OptionsActivity.class);
                startActivity(intent1);
                break;
        }
    }
}
