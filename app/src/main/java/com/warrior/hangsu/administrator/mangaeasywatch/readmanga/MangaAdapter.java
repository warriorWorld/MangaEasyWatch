package com.warrior.hangsu.administrator.mangaeasywatch.readmanga;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.warrior.hangsu.administrator.mangaeasywatch.utils.FileUtil;
import com.warrior.hangsu.administrator.mangaeasywatch.utils.Globle;
import com.warrior.hangsu.administrator.mangaeasywatch.utils.ImageUtil;

import java.io.File;
import java.util.ArrayList;

import uk.co.senab.photoview.PhotoView;

public class MangaAdapter extends PagerAdapter {
    private ArrayList<String> pathList = new ArrayList<String>();
    private Context context;
    //    private ImageSize imageSize;
    private Bitmap bp;
    //这种方式存了一堆view 占用了大量的内存 所以不用了
//    private ArrayList<View> list = new ArrayList<View>();


    // private ArrayList<View> list = new ArrayList<View>();
    // 所需要的view数量由cache决定 =2*cache+1(当前页)+1(有时候先添加后移除 所以还有一个)
    // 因为可能同时存在2*cache+1(当前页)+1(有时候先添加后移除 所以还有一个)个页面 而父控件必须不能重复 所以我需要这么多view
    PhotoView v0;
    PhotoView v1;
    PhotoView v2;
    PhotoView v3;
    PhotoView v4;
    PhotoView v5;

    public MangaAdapter(Context context, ArrayList<String> pathList) {
        this.context = context;
        this.pathList = pathList;
        v0 = new PhotoView(context);
        v1 = new PhotoView(context);
        v2 = new PhotoView(context);
        v3 = new PhotoView(context);
        v4 = new PhotoView(context);
        v5 = new PhotoView(context);
//        imageSize = new ImageSize(2000, 4000);
    }


    @Override
    public int getCount() {
        return pathList.size();
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        // 官方提示这样写
        return arg0 == arg1;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        // container.removeView(list.get(position));
        // 这个position代表的是list中item的位置

//        container.removeView(list.get(position));

        switch (position % 6) {
            case 0:
                container.removeView(v0);
                break;
            case 1:
                container.removeView(v1);
                break;
            case 2:
                container.removeView(v2);
                break;
            case 3:
                container.removeView(v3);
                break;
            case 4:
                container.removeView(v4);
                break;
            case 5:
                container.removeView(v5);
                break;
        }
    }


    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        // Toast.makeText(context,
        // "加载view" + position + " 子view数量" + container.getChildCount(), 0)
        // .show();

//        v = new GetMedicineView(context, medicines.get(position));
//        list.add(v);
//        container.addView(v);
//        return v;


        switch (position % 6) {
            case 0:
//                ImageLoader.getInstance().displayImage(pathList.get(position), new ImageViewAware
//                        (v0), Globle.options, imageSize, null, null);
                bp = ImageUtil.getImageFromSDFile(pathList.get(position));
                v0.setImageBitmap(bp);
                container.addView(v0);
                return v0;
            case 1:
                bp = ImageUtil.getImageFromSDFile(pathList.get(position));
                v1.setImageBitmap(bp);
                container.addView(v1);
                return v1;
            case 2:
                bp = ImageUtil.getImageFromSDFile(pathList.get(position));
                v2.setImageBitmap(bp);
                container.addView(v2);
                return v2;
            case 3:
                bp = ImageUtil.getImageFromSDFile(pathList.get(position));
                v3.setImageBitmap(bp);
                container.addView(v3);
                return v3;
            case 4:
                bp = ImageUtil.getImageFromSDFile(pathList.get(position));
                v4.setImageBitmap(bp);
                container.addView(v4);
                return v4;
            case 5:
                bp = ImageUtil.getImageFromSDFile(pathList.get(position));
                v5.setImageBitmap(bp);
                container.addView(v5);
                return v5;
        }
        return null;
    }
//根据源码 这个方法可以解决缓存至少是2的问题 但是这个方法不太好 所以我改成用六个缓存view
//    @Override
//    public float getPageWidth(int position) {
//        return 1.01f;
//    }
}
