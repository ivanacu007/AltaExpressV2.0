package com.example.pruebamenu.AdapterMenu;

import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.pruebamenu.R;

public class ViewHolder extends RecyclerView.ViewHolder {
    CheckBox myCheck;
    TextView txName, txDesc, txPrice;
    LinearLayout linearLayout;
    public ViewHolder(@NonNull View itemView) {
        super(itemView);
        myCheck = itemView.findViewById(R.id.checkItem);
        txName = itemView.findViewById(R.id.item_title);
        txDesc = itemView.findViewById(R.id.item_desc);
        txPrice = itemView.findViewById(R.id.item_price);
        linearLayout = itemView.findViewById(R.id.linearTitle);
    }
}
