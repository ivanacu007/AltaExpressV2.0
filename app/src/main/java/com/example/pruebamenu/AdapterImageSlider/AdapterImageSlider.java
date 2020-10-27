package com.example.pruebamenu.AdapterImageSlider;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;
import com.example.pruebamenu.Models.sliderModel;
import com.example.pruebamenu.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterImageSlider extends RecyclerView.Adapter<SliderViewHolder>{
    private List<sliderModel> sliderItems;
    private ViewPager2 viewPager2;
    Context context;
    private String name;

    public AdapterImageSlider(List<sliderModel> sliderItems, ViewPager2 viewPager2) {
        this.sliderItems = sliderItems;
        this.viewPager2 = viewPager2;
    }

    @NonNull
    @Override
    public SliderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.slider_item_design, parent, false);
        context = parent.getContext();
        SliderViewHolder sliderViewHolder = new SliderViewHolder(itemView);
        sliderViewHolder.setOnClickListener(new SliderViewHolder.ClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                name = sliderItems.get(position).getName();
                Toast.makeText(context, name, Toast.LENGTH_SHORT).show();
            }
        });
        return sliderViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull SliderViewHolder holder, int position) {
        Picasso.get().load(sliderItems.get(position).getImg()).into(holder.imgItem);
        holder.titleItem.setText(sliderItems.get(position).getName());
        if (position == sliderItems.size() - 2) {
            viewPager2.post(runnable);
        }
    }

    @Override
    public int getItemCount() {
        return sliderItems.size();
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            sliderItems.addAll(sliderItems);
            notifyDataSetChanged();
        }
    };
}
