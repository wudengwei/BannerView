package com.wudengwei.bannerview;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    BannerView bannerView;

    List<Integer> imgList;
    BaseAdapter baseAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bannerView = findViewById(R.id.banner_view);

        imgList = new ArrayList<>();
        Integer[] imgs = {R.drawable.a, R.drawable.b, R.drawable.c, R.drawable.d, R.drawable.a, R.drawable.b, R.drawable.c, R.drawable.d};
        imgList.addAll(Arrays.asList(imgs));
        baseAdapter = new BaseAdapter<Integer>(R.layout.recycler_item_image, imgList) {
            @Override
            protected void convert(BaseViewHolder viewHolder, Integer item, int position) {
                ImageView img = viewHolder.getViewById(R.id.img);
                //
                int pp = position % imgList.size();
                int url = imgList.get(position % imgList.size());
                Glide.with(getBaseContext()).load(url).into(img);
                TextView title = viewHolder.getViewById(R.id.title);
                title.setText(""+pp);
            }
        };
        bannerView.setAdapter(baseAdapter);
        bannerView.startLoop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        bannerView.startLoop();

        imgList.add(R.drawable.a);
        imgList.add(R.drawable.b);
        bannerView.notifyDataSetChanged();
    }

    @Override
    protected void onPause() {
        super.onPause();
        bannerView.stopLoop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}