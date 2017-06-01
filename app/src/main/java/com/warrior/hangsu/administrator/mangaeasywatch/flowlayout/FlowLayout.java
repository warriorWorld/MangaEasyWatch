package com.warrior.hangsu.administrator.mangaeasywatch.flowlayout;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class FlowLayout extends ViewGroup {

    public FlowLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        //
    }

    public FlowLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FlowLayout(Context context) {
        this(context, null);
    }

    /**
     * 锟斤拷锟斤拷锟斤拷锟斤拷
     *
     * @widthMeasureSpec 锟斤拷锟斤拷锟斤拷锟缴达拷锟饺和诧拷锟斤拷模式锟斤拷息
     * @heightMeasureSpec 锟斤拷锟斤拷锟斤拷锟缴达拷叨群筒锟斤拷锟侥Ｊ斤拷锟较�
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // 锟斤拷取锟斤拷锟斤拷锟缴达拷锟斤拷锟斤拷息
        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
        // 锟斤拷取锟斤拷锟斤拷锟斤拷丶锟斤拷目锟斤拷模式 锟斤拷锟斤拷 wrap_content match_parent
        int modeWidth = MeasureSpec.getMode(widthMeasureSpec);
        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);
        int modeHeight = MeasureSpec.getMode(heightMeasureSpec);
        // 锟斤拷锟斤拷蔷锟饺分�,锟斤拷锟较边撅拷直锟接伙拷玫锟斤拷司锟饺分�,锟斤拷锟斤拷锟斤拷锟絯rap_content,锟斤拷锟斤拷要锟斤拷锟斤拷锟斤拷,锟斤拷锟铰达拷锟诫都锟斤拷为锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷
        // wrap_content
        int width = 0;
        int height = 0;

        // 锟斤拷录每一锟叫的匡拷锟斤拷锟竭讹拷
        int lineWidth = 0;
        int lineHeight = 0;

        // 锟矫碉拷锟节诧拷元锟截的革拷锟斤拷
        int cCount = getChildCount();

        for (int i = 0; i < cCount; i++) {
            View child = getChildAt(i);
            // 锟斤拷锟斤拷锟斤拷View锟侥匡拷透锟�
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
            // 锟矫碉拷LayoutParams
            // 锟斤拷锟斤拷锟斤拷为锟斤拷锟斤拷锟斤拷写锟斤拷generateLayoutParams锟斤拷锟斤拷,锟斤拷锟斤拷时锟斤拷锟截碉拷值锟斤拷MarginLayoutParams,锟斤拷锟斤拷锟斤拷要强转锟斤拷MarginLayoutParams
            MarginLayoutParams lp = (MarginLayoutParams) child
                    .getLayoutParams();

            // 锟斤拷View占锟捷的匡拷锟�
            int childWidth = child.getMeasuredWidth() + lp.leftMargin
                    + lp.rightMargin;
            // 锟斤拷View占锟捷的高讹拷
            int childHeight = child.getMeasuredHeight() + lp.topMargin
                    + lp.bottomMargin;

            // 锟斤拷锟斤拷
            if (lineWidth + childWidth > sizeWidth - getPaddingLeft()
                    - getPaddingRight()) {
                // 锟皆比得碉拷锟斤拷锟侥匡拷锟�
                width = Math.max(width, lineWidth);
                // 锟斤拷锟斤拷lineWidth
                lineWidth = childWidth;
                // 锟斤拷录锟叫革拷
                height += lineHeight;
                lineHeight = childHeight;
            } else
            // 未锟斤拷锟斤拷
            {
                // 锟斤拷锟斤拷锟叫匡拷
                lineWidth += childWidth;
                // 锟矫碉拷锟斤拷前锟斤拷锟斤拷锟侥高讹拷
                lineHeight = Math.max(lineHeight, childHeight);
            }
            // 锟斤拷锟揭伙拷锟斤拷丶锟�
            if (i == cCount - 1) {
                width = Math.max(lineWidth, width);
                height += lineHeight;
            }
        }

        Log.e("TAG", "sizeWidth = " + sizeWidth);
        Log.e("TAG", "sizeHeight = " + sizeHeight);
        // 锟斤拷锟斤拷蔷锟饺分碉拷锟绞癸拷锟街憋拷踊锟饺★拷锟斤拷锟街�,锟界不锟斤拷,锟斤拷使锟矫硷拷锟斤拷锟斤拷锟斤拷锟街�.
        setMeasuredDimension(
                //
                modeWidth == MeasureSpec.EXACTLY ? sizeWidth : width
                        + getPaddingLeft() + getPaddingRight(),
                modeHeight == MeasureSpec.EXACTLY ? sizeHeight : height
                        + getPaddingTop() + getPaddingBottom()//
        );

    }

    /**
     * 锟芥储锟斤拷锟叫碉拷View,一锟斤拷一锟叫的达拷锟�.锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷,锟斤拷一锟斤拷5锟斤拷view,
     * 锟节讹拷锟斤拷4锟斤拷view,锟斤拷锟斤拷锟斤拷1锟斤拷View锟斤拷锟斤拷锟斤拷
     */
    private List<List<View>> mAllViews = new ArrayList<List<View>>();
    /**
     * 每一锟叫的高讹拷
     */
    private List<Integer> mLineHeight = new ArrayList<Integer>();

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        mAllViews.clear();
        mLineHeight.clear();

        // 锟斤拷前ViewGroup锟侥匡拷锟�
        int width = getWidth();

        int lineWidth = 0;
        int lineHeight = 0;

        List<View> lineViews = new ArrayList<View>();

        int cCount = getChildCount();
        /**
         * for循锟斤拷锟斤拷一锟叫的匡拷锟斤拷约锟絝or循锟斤拷锟斤拷一锟斤拷锟斤拷锟斤拷叩母叨锟�
         * 锟斤拷锟絝or循锟斤拷锟斤拷锟斤拷要目锟斤拷锟斤拷为锟剿凤拷锟斤拷,一锟斤拷为一锟斤拷,锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟�
         */
        for (int i = 0; i < cCount; i++) {
            View child = getChildAt(i);
            MarginLayoutParams lp = (MarginLayoutParams) child
                    .getLayoutParams();

            int childWidth = child.getMeasuredWidth();
            int childHeight = child.getMeasuredHeight();

            // 锟斤拷锟斤拷锟揭拷锟斤拷锟�
            if (childWidth + lineWidth + lp.leftMargin + lp.rightMargin > width
                    - getPaddingLeft() - getPaddingRight()) {
                // 锟斤拷录LineHeight
                mLineHeight.add(lineHeight);
                // 锟斤拷录锟斤拷前锟叫碉拷Views
                mAllViews.add(lineViews);

                // 锟斤拷锟斤拷锟斤拷锟角碉拷锟叫匡拷锟斤拷懈锟�
                lineWidth = 0;
                lineHeight = childHeight + lp.topMargin + lp.bottomMargin;
                // 锟斤拷锟斤拷锟斤拷锟角碉拷View锟斤拷锟斤拷
                lineViews = new ArrayList<View>();
            }
            lineWidth += childWidth + lp.leftMargin + lp.rightMargin;
            lineHeight = Math.max(lineHeight, childHeight + lp.topMargin
                    + lp.bottomMargin);
            lineViews.add(child);

        }// for end
        // 锟斤拷锟斤拷锟斤拷锟揭伙拷锟�
        mLineHeight.add(lineHeight);
        mAllViews.add(lineViews);

        // 锟斤拷锟斤拷锟斤拷View锟斤拷位锟斤拷

        int left = getPaddingLeft();
        int top = getPaddingTop();

        // 锟斤拷锟斤拷
        int lineNum = mAllViews.size();
        int x = 0;
        for (int i = 0; i < lineNum; i++) {
            // 锟斤拷前锟叫碉拷锟斤拷锟叫碉拷View
            lineViews = mAllViews.get(i);
            lineHeight = mLineHeight.get(i);

            // 以下关于x的都是我写的
            // 这个流式布局仅适用于各view的宽度都相等并且marginleft的宽度也都相等的情况并且没有marginright的情况,自动平分一行的空间
            // 而不是像传统的流式布局那样最后的地方留空

            if (lineViews.size() > 0 && i != lineNum - 1) {
                // 如果是最后不单独计算 与上边对齐
                View child1 = lineViews.get(0);
                MarginLayoutParams lp1 = (MarginLayoutParams) child1
                        .getLayoutParams();
                x = (width - 2 * getPaddingLeft() - lineViews.size()
                        * child1.getMeasuredWidth() - lineViews.size()
                        * lp1.leftMargin)
                        / lineViews.size();
                x = x - (x + lp1.leftMargin) / (lineViews.size() + 1);
            }

            for (int j = 0; j < lineViews.size(); j++) {
                View child = lineViews.get(j);
                // 锟叫讹拷child锟斤拷状态
                if (child.getVisibility() == View.GONE) {
                    continue;
                }

                MarginLayoutParams lp = (MarginLayoutParams) child
                        .getLayoutParams();

                int lc = left + lp.leftMargin + x;
                int tc = top + lp.topMargin;
                int rc = lc + child.getMeasuredWidth();
                int bc = tc + child.getMeasuredHeight();

                // 为锟斤拷View锟斤拷锟叫诧拷锟斤拷
                child.layout(lc, tc, rc, bc);

                left += child.getMeasuredWidth() + lp.leftMargin
                        + lp.rightMargin + x;
            }
            left = getPaddingLeft();
            top += lineHeight;
        }

    }

    /**
     * 锟诫当前ViewGroup锟斤拷应锟斤拷LayoutParams
     * 锟斤拷为锟斤拷锟斤拷锟斤拷锟斤拷只锟斤拷要margin锟斤拷值,锟斤拷锟斤拷直锟接凤拷锟斤拷margin
     */
    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }

}
