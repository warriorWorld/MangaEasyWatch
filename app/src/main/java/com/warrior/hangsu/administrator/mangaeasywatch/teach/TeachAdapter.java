package com.warrior.hangsu.administrator.mangaeasywatch.teach;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.warrior.hangsu.administrator.mangaeasywatch.utils.Globle;

import java.util.ArrayList;

import uk.co.senab.photoview.PhotoView;

public class TeachAdapter extends PagerAdapter {
    //    private int[] pathList;
    private ArrayList<Bitmap> bpList;
    private Context context;
    private Bitmap bp;

    public TeachAdapter(Context context, ArrayList<Bitmap> bpList) {
        this.context = context;
//        this.pathList = pathList;
        this.bpList = bpList;
    }


    @Override
    public int getCount() {
        return bpList.size();
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        // 官方提示这样写
        return arg0 == arg1;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }


    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ImageView v0;
        v0 = new ImageView(context);
//        bp = ImageUtil.getImageFromSDFile(pathList.get(position));
//        v0.setImageBitmap(bp);
        v0.setImageBitmap(bpList.get(position));
        container.addView(v0);
        return v0;
    }
//根据源码 这个方法可以解决缓存至少是2的问题 但是这个方法不太好 所以我改成用六个缓存view
//    @Override
//    public float getPageWidth(int position) {
//        return 1.01f;
//    }
}
