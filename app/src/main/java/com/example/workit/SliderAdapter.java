package com.example.workit;

import static com.example.workit.R.layout.slider_item;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.smarteist.autoimageslider.SliderView;
import com.smarteist.autoimageslider.SliderViewAdapter;

public class SliderAdapter extends SliderViewAdapter<SliderAdapter.ViewHolder> {

    Context context;
    int[] images;
    public SliderAdapter(Context context, int[] images) {
        this.context = context;
        this.images = images;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.slider_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        viewHolder.imgView.setImageResource(images[position]);
    }

    @Override
    public int getCount() {
        return images.length;
    }

    public class ViewHolder extends SliderViewAdapter.ViewHolder {
        ImageView imgView;
        public ViewHolder(View itemView) {
            super(itemView);
            imgView = itemView.findViewById(R.id.slideImg);
        }
    }
}
