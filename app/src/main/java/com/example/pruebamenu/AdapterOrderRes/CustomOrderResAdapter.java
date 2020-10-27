package com.example.pruebamenu.AdapterOrderRes;

import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pruebamenu.Models.cartModel;
import com.example.pruebamenu.OrderResume;
import com.example.pruebamenu.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class CustomOrderResAdapter extends RecyclerView.Adapter<OrderResViewHolder> {
    OrderResume orderResume;
    List<cartModel> cartModelList;
    double total = 0.0;
    TextView txaux;

    public CustomOrderResAdapter(OrderResume orderResume, List<cartModel> cartModelList) {
        this.orderResume = orderResume;
        this.cartModelList = cartModelList;
    }

    @NonNull
    @Override
    public OrderResViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.resume_design, parent, false);
        OrderResViewHolder viewHolder = new OrderResViewHolder(itemView);
        //getList();
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull OrderResViewHolder holder, final int position) {
        getList();
        final int pos = position;
        holder.txresName.setText(cartModelList.get(position).getCart_itemName());
        holder.txresDesc.setText(cartModelList.get(position).getCart_itemDesc());
        holder.txresPrice.setText(String.format("$%.2f", cartModelList.get(position).getCart_itemPrice()));
        holder.spRes.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> spn,
                                               android.view.View v,
                                               int posicion,
                                               long id) {
                        String posicionSpin = spn.getItemAtPosition(posicion).toString();
                        String cartID = cartModelList.get(pos).getCart_itemID();
//                        Toast.makeText(spn.getContext(), "Has seleccionado " +
//                                        cartModelList.get(pos).getCart_itemID(),
//                                Toast.LENGTH_LONG).show();
                        updatePrecios(cartID, posicionSpin, pos);
                    }

                    public void onNothingSelected(AdapterView<?> spn) {
                    }
                });
    }

    public void getList() {
        SharedPreferences sharedPreferences = orderResume.getSharedPreferences("shared preferences", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("mylist", null);
        Type type = new TypeToken<ArrayList<cartModel>>() {
        }.getType();
        cartModelList = gson.fromJson(json, type);
        if (cartModelList == null) {
            cartModelList = new ArrayList<>();
        }
    }

    public void updatePrecios(String id, String cantidad, final int pos) {
        final int amount = Integer.parseInt(cantidad);
        double totalArticulos = 0;
        double viejo = 0, nuevo = 0;
        for (int i = 0; i < cartModelList.size(); i++) {
            if (cartModelList.get(i).getCart_itemID() == id) {
                if (amount > 1){
                    totalArticulos = cartModelList.get(i).getCart_itemPrice() * amount;
                    cartModelList.get(i).setCartItem_newPrice(totalArticulos);
                    cartModelList.get(i).setItem_cantidadTotal(amount);
                    nuevo = cartModelList.get(i).getCartItem_newPrice();
                    //notifyItemChanged(pos);
                }
                else{
                    viejo = cartModelList.get(i).getCart_itemPrice();
                    cartModelList.get(i).setItem_cantidadTotal(amount);
                    cartModelList.get(i).setCartItem_newPrice(viejo);
                    notifyItemChanged(pos);
                    //Toast.makeText(orderResume, String.valueOf(viejo), Toast.LENGTH_SHORT).show();
                }
            }
        }
        saveCartData();
    }
    public void saveCartData(){
        SharedPreferences sharedPreferences = orderResume.getSharedPreferences("shared preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(cartModelList);
        editor.putString("mylist", json);
        editor.apply();
    }


    @Override
    public int getItemCount() {
        return cartModelList.size();
    }
}
