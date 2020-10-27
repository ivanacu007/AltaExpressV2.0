package com.example.pruebamenu.AdapterResFinishOrder;

import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pruebamenu.FinishOrder;
import com.example.pruebamenu.Models.cartModel;
import com.example.pruebamenu.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import static android.content.Context.MODE_PRIVATE;

public class AdapterResFinishOrder extends RecyclerView.Adapter<AdapterResFinishOrder.ResFinishViewHolder> {
    List<cartModel> cartModelList;
    FinishOrder finishOrder;

    public AdapterResFinishOrder(FinishOrder finishOrder, List<cartModel> cartModelList) {
        this.cartModelList = cartModelList;
        this.finishOrder = finishOrder;
    }

    @NonNull
    @Override
    public ResFinishViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.res_finish_layout, parent, false);
        ResFinishViewHolder view = new ResFinishViewHolder(itemView);
        return view;
    }

    @Override
    public void onBindViewHolder(@NonNull ResFinishViewHolder holder, int position) {
        holder.itemName.setText(cartModelList.get(position).getCart_itemName());
        holder.itemCount.setText(String.valueOf("X"+cartModelList.get(position).getItem_cantidadTotal()));
        holder.itemTotal.setText(String.format("$%.2f", cartModelList.get(position).getCartItem_newPrice()));
    }

    @Override
    public int getItemCount() {
        return cartModelList.size();
    }
    public void getList() {
        SharedPreferences sharedPreferences = finishOrder.getSharedPreferences("shared preferences", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("mylist", null);
        Type type = new TypeToken<ArrayList<cartModel>>() {
        }.getType();
        cartModelList = gson.fromJson(json, type);
        if (cartModelList == null) {
            cartModelList = new ArrayList<>();
        }
    }

    class ResFinishViewHolder extends RecyclerView.ViewHolder{
        TextView itemName, itemCount, itemTotal;
        LinearLayout layoutEx;
        public ResFinishViewHolder(@NonNull View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.txtitemname);
            itemCount = itemView.findViewById(R.id.txtitemcount);
            itemTotal = itemView.findViewById(R.id.txtitemtotal);
        }
    }
}
