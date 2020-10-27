package com.example.pruebamenu.AdapterMenu;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pruebamenu.MenuActivity;
import com.example.pruebamenu.Models.cartModel;
import com.example.pruebamenu.Models.itemModel;
import com.example.pruebamenu.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class CustomAdapter extends RecyclerView.Adapter<ViewHolder> {
    List<itemModel> modelList;
    MenuActivity menuActivity;
    cartModel model = new cartModel();
    double newP =  0;
    int total = 0;
    String note, negocioID, userID, userDir, date;
    boolean status = false, currentUser;
    private FirebaseAuth mAuth;

    public CustomAdapter(MenuActivity menuActivity, List<itemModel> modelList) {
        this.menuActivity = menuActivity;
        this.modelList = modelList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_menu, parent, false);
        ViewHolder viewHolder = new ViewHolder(itemView);
        mAuth = FirebaseAuth.getInstance();
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        String aux = modelList.get(position).getItemIngred();
        currentUser = true; //validateUser();
        if(aux != ""){
            holder.txDesc.setVisibility(View.VISIBLE);
        }
        holder.txName.setText(modelList.get(position).getItemName());
        holder.txDesc.setText(modelList.get(position).getItemIngred());
        holder.txPrice.setText("Precio");
        if (currentUser){
            holder.myCheck.setVisibility(View.VISIBLE);
            holder.txPrice.setText(String.format("$%.2f", modelList.get(position).getItemPrice()));
            if(menuActivity.cartModelList.size() > 0){
                for (int i = 0; i < menuActivity.cartModelList.size(); i ++){
                    if(menuActivity.cartModelList.get(i).getCart_itemID().equals(modelList.get(position).getItemId())){
                        holder.myCheck.setChecked(true);
                    }
                }
            }
            holder.myCheck.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(((CheckBox) v).isChecked()){
                        cartModel model = new cartModel(
                                modelList.get(position).getItemId(),
                                modelList.get(position).getItemName(),
                                modelList.get(position).getItemIngred(),
                                note,
                                negocioID,
                                userID,
                                userDir,
                                date,
                                modelList.get(position).getItemPrice(),
                                newP,
                                total,
                                status
                        );
                        menuActivity.cartModelList.add(model);
                    }else{
                        for (int i = 0; i < menuActivity.cartModelList.size(); i ++){
                            if(menuActivity.cartModelList.get(i).getCart_itemID().equals(modelList.get(position).getItemId())){
                                menuActivity.cartModelList.remove(i);
                            }
                        }
                    }
                }
            });
        }
        else{
            holder.myCheck.setVisibility(View.GONE);
            holder.txPrice.setText(String.format("$%.2f", modelList.get(position).getItemPrice()));
        }
    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }

/*    public boolean validateUser() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        boolean value;
        if (user != null) {
            value = true;
        }
        else{
            value = false;
        }
        return value;
    }*/
}
