package com.wudengwei.bannerview;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;

import java.util.LinkedHashSet;

public class BaseViewHolder extends RecyclerView.ViewHolder {

    private SparseArray<View> mViewList;


    public BaseViewHolder(@NonNull View itemView) {
        super(itemView);
        mViewList = new SparseArray<>();
    }

    /**
     * 从SparseArray中查找view，
     * 不存在就使用itemView.findViewById,并添加到SparseArray
     * @param viewId view的id
     * @param <T> view的子类
     * @return
     */
    protected <T extends View> T getViewById(int viewId) {
        View view = mViewList.get(viewId);
        if (view == null) {
            view = itemView.findViewById(viewId);
            mViewList.put(viewId,view);
        }
        return (T) view;
    }
}
