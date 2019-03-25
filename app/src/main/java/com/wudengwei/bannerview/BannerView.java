package com.wudengwei.bannerview;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wudengwei
 * on 2019/3/22
 */
public class BannerView extends FrameLayout {
    private Context mContext;

    private RecyclerView rvBanner;//banner主视图
    private List<Integer> indicatorList;
    private RecyclerView rvBannerIndicator;//banner指示器
    private BaseAdapter itemAdapter;
    private BaseAdapter indicatorAdapter;

    private int itemSelectedIndex;// 当前显示item,因为使用Integer.MAX_VALUE（2147483647）当作item数量，让item起始位置尽量居中
    //private int itemCount = 0;//item总数
    private int loopDuration = 3000;//item滚动间隔ms

    protected int WHAT_AUTO_PLAY = 1000;//handler 处理的message
    protected boolean isLoop = true;//是否可以自动循环滚动
    protected boolean isPlaying = false;//表示是否正在滚动中

    public BannerView(@NonNull Context context) {
        this(context,null);
    }

    public BannerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public BannerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        View rootView = LayoutInflater.from(context).inflate(R.layout.custom_banner_view, this, true);

        rvBanner = rootView.findViewById(R.id.rv_banner);
        rvBannerIndicator = rootView.findViewById(R.id.rv_banner_indicator);
    }

    //通过设置recyclerView的adapter设置banner内容
    public void setAdapter(BaseAdapter baseAdapter) {
        itemAdapter = baseAdapter;
        itemSelectedIndex = getItemCount() * 10000;
        rvBanner.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        rvBanner.setAdapter(itemAdapter);
        rvBanner.scrollToPosition(itemSelectedIndex);
        rvBanner.addOnScrollListener(new RecyclerView.OnScrollListener() {
            //正在滚动时回调，回调2-3次，手指没抛则回调2次。scrollState = 2的这次不回调
            //1：scrollState = SCROLL_STATE_TOUCH_SCROLL(1) 正在滚动
            //2：scrollState = SCROLL_STATE_FLING(2) 手指做了抛的动作（手指离开屏幕前，用力滑了一下）
            //3：scrollState = SCROLL_STATE_IDLE(0) 停止滚动
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                Log.e("onScrollStateChanged",""+newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                //解决连续滑动时指示器不更新的问题
                if (getItemCount() < 2) return;
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int firstReal = linearLayoutManager.findFirstVisibleItemPosition();
                View viewFirst = linearLayoutManager.findViewByPosition(firstReal);
                float width = getWidth();
                if (width != 0 && viewFirst != null) {
                    float right = viewFirst.getRight();
                    float ratio = right / width;
                    if (ratio > 0.8) {
                        if (itemSelectedIndex != firstReal) {
                            itemSelectedIndex = firstReal;
                            indicatorAdapter.notifyDataSetChanged();
                        }
                    } else if (ratio < 0.2) {
                        if (itemSelectedIndex != firstReal + 1) {
                            itemSelectedIndex = firstReal + 1;
                            indicatorAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }
        });
        PagerSnapHelper pagerSnapHelper = new PagerSnapHelper();//想viewPager一样单页滑动
        pagerSnapHelper.attachToRecyclerView(rvBanner);

        setBannerIndicator();
    }
    
    private void setBannerIndicator() {
        indicatorList = new ArrayList<>();
        for (int i=0;i<getItemCount();i++) {
            indicatorList.add(i);
        }
        rvBannerIndicator.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        rvBannerIndicator.setAdapter(indicatorAdapter = new BaseAdapter<Integer>(R.layout.recycler_item_indicator,indicatorList) {
            @Override
            protected void convert(BaseViewHolder viewHolder, Integer item, int position) {
                ImageView img = viewHolder.getViewById(R.id.img);

                Drawable mSelectedDrawable = null;
                Drawable mUnselectedDrawable = null;
                if (mSelectedDrawable == null) {
                    //绘制默认选中状态图形
                    GradientDrawable selectedGradientDrawable = new GradientDrawable();
                    selectedGradientDrawable.setShape(GradientDrawable.OVAL);
                    selectedGradientDrawable.setColor(Color.RED);
                    selectedGradientDrawable.setSize(dp2px(5), dp2px(5));
                    selectedGradientDrawable.setCornerRadius(dp2px(5) / 2);
                    mSelectedDrawable = new LayerDrawable(new Drawable[]{selectedGradientDrawable});
                }
                if (mUnselectedDrawable == null) {
                    //绘制默认未选中状态图形
                    GradientDrawable unSelectedGradientDrawable = new GradientDrawable();
                    unSelectedGradientDrawable.setShape(GradientDrawable.OVAL);
                    unSelectedGradientDrawable.setColor(Color.GRAY);
                    unSelectedGradientDrawable.setSize(dp2px(5), dp2px(5));
                    unSelectedGradientDrawable.setCornerRadius(dp2px(5) / 2);
                    mUnselectedDrawable = new LayerDrawable(new Drawable[]{unSelectedGradientDrawable});
                }
                int selectedIndex = itemSelectedIndex % mList.size();
                img.setImageDrawable(selectedIndex == position?mSelectedDrawable:mUnselectedDrawable);
            }

            @Override
            public int getItemCount() {
                return mList.size();
            }
        });
    }

    public int getItemCount() {
        if (itemAdapter == null)
            return 0;
        return itemAdapter.mList.size();
    }

    public void notifyDataSetChanged() {
        itemAdapter.notifyDataSetChanged();
        indicatorList.clear();
        for (int i=0;i<getItemCount();i++) {
            indicatorList.add(i);
        }
        indicatorAdapter.notifyDataSetChanged();
    }

    //开始自动滑动
    public void startLoop() {
        if (isLoop) {
            if (!isPlaying) {
                mHandler.sendEmptyMessageDelayed(WHAT_AUTO_PLAY, loopDuration);
                isPlaying = true;
            }
        }
    }

    public void stopLoop() {
        if (isLoop) {
            if (isPlaying) {
                mHandler.removeMessages(WHAT_AUTO_PLAY);
                isPlaying = false;
            }
        }
    }


    Handler mHandler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == WHAT_AUTO_PLAY) {
                //定时时间到，do something
                rvBanner.smoothScrollToPosition(++itemSelectedIndex);
                indicatorAdapter.notifyDataSetChanged();
                mHandler.sendEmptyMessageDelayed(WHAT_AUTO_PLAY, loopDuration);
            }
            return false;
        }
    });

    //手指触摸banner，暂停滚动,松开则开始滚动
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                stopLoop();
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                startLoop();
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    protected int dp2px(int dp) {
        return (int) (0.5+TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                Resources.getSystem().getDisplayMetrics()));
    }
}
