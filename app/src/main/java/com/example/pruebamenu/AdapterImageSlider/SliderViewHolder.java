package com.example.pruebamenu.AdapterImageSlider;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pruebamenu.R;

public class SliderViewHolder extends RecyclerView.ViewHolder {
    ImageView imgItem;
    TextView titleItem;
    public SliderViewHolder(@NonNull View itemView) {
        super(itemView);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mClickListener.onItemClick(v, getAdapterPosition());
            }
        });
        imgItem = itemView.findViewById(R.id.imageItemSlider);
        titleItem = itemView.findViewById(R.id.item_name);
    }

    private SliderViewHolder.ClickListener mClickListener;

    //    //interface for clicklistener
    public interface ClickListener {
        void onItemClick(View view, int position);
        //void onItemLongClick(View view, int position);
    }
    public void setOnClickListener(SliderViewHolder.ClickListener clickListener) {
        mClickListener = clickListener;
    }
}
