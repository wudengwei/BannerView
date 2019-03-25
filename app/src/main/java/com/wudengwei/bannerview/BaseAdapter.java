package com.wudengwei.bannerview;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseAdapter<T> extends RecyclerView.Adapter<BaseViewHolder> {

    protected List<T> mList;//不同类型集合(实际的item数量)
    private int mLayoutId;

    public BaseAdapter(@LayoutRes int layoutId, List<T> list) {
        if (list == null) {
            mList = new ArrayList<>();
        } else {
            mList = list;
        }
        mLayoutId = layoutId;
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int type) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(mLayoutId,viewGroup,false);
        BaseViewHolder viewHolder = new BaseViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder viewHolder, int position) {
        int pos = position % mList.size();
        convert(viewHolder,mList.get(pos),position);
    }

    protected abstract void convert(BaseViewHolder viewHolder, T item, int position);

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        return mList.size() < 2 ? 1 : Integer.MAX_VALUE;
    }
}
