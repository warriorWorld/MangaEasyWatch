package com.warrior.hangsu.administrator.mangaeasywatch.teach;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.warrior.hangsu.administrator.mangaeasywatch.R;
import com.warrior.hangsu.administrator.mangaeasywatch.readmanga.DepthPageTransformer;
import com.warrior.hangsu.administrator.mangaeasywatch.readmanga.HackyViewPager;
import com.warrior.hangsu.administrator.mangaeasywatch.readmanga.NewMangaAdapter;
import com.warrior.hangsu.administrator.mangaeasywatch.shotscreen.ScreenShot;
import com.warrior.hangsu.administrator.mangaeasywatch.shotscreen.ShotView;
import com.warrior.hangsu.administrator.mangaeasywatch.sort.FileComparator;
import com.warrior.hangsu.administrator.mangaeasywatch.translate.TranslateActivity;
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
public class TeachActivity extends Activity implements OnClickListener {
    private ViewPager vp;
    private Button finishTeachBtn;
    private TopBar topBar;
    private TeachAdapter adapter;
    private int[] pathList = {R.drawable.teach1, R.drawable.teach2, R.drawable.teach3,
            R.drawable.teach4, R.drawable.teach5, R.drawable.teach6, R.drawable.teach7};
    private ArrayList<Bitmap> bpList = new ArrayList<Bitmap>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teach);
        initBpList();
        initUI();
        initViewPager();
    }

    private void initBpList() {
        Bitmap bp;
        for (int i = 0; i < pathList.length; i++) {
            bp = readBitMap(TeachActivity.this, pathList[i]);
            bpList.add(bp);
        }
    }

    /**
     * 以最省内存的方式读取本地资源的图片
     *
     * @param context
     * @param resId
     * @return
     */
    public static Bitmap readBitMap(Context context, int resId) {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.RGB_565;
        opt.inPurgeable = true;
        opt.inInputShareable = true;
        //获取资源图片
        InputStream is = context.getResources().openRawResource(resId);
        return BitmapFactory.decodeStream(is, null, opt);
    }

    private void initUI() {
        vp = (ViewPager) findViewById(R.id.view_pager);
        finishTeachBtn = (Button) findViewById(R.id.finish_teach);

        finishTeachBtn.setOnClickListener(this);
        topBar = (TopBar) findViewById(R.id.top_bar);
        topBar.setOnTopBarClickListener(new TopBar.OnTopBarClickListener() {

            @Override
            public void onRightClick() {
            }

            @Override
            public void onTitleClick() {
            }

            @Override
            public void onLeftClick() {
                TeachActivity.this.finish();
            }
        });
    }


    private void initViewPager() {
        if (null == adapter) {
            adapter = new TeachAdapter(TeachActivity.this, bpList);
            vp.setOffscreenPageLimit(1);
            vp.setAdapter(adapter);

            vp.setPageTransformer(true, new DepthPageTransformer());
            vp.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    int size = pathList.length;
                    topBar.setRightText(position + 1 + "/" + size);
                    if (position == size - 1) {
                        //显示完成按钮
                        finishTeachBtn.setVisibility(View.VISIBLE);
                    } else {
                        finishTeachBtn.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
        } else {
            adapter.notifyDataSetChanged();
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.finish_teach:
                TeachActivity.this.finish();
                break;
        }
    }

}
