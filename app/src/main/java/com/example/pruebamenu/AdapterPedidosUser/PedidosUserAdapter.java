package com.example.pruebamenu.AdapterPedidosUser;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pruebamenu.FinishOrder;
import com.example.pruebamenu.Models.cartModel;
import com.example.pruebamenu.Models.pedidoModel;
import com.example.pruebamenu.MyOrders;
import com.example.pruebamenu.R;

import java.util.List;

public class PedidosUserAdapter extends RecyclerView.Adapter<PedidosUserAdapter.PedidosUserViewHolder>{
    List<pedidoModel> modelList;
    MyOrders myOrders;

    public PedidosUserAdapter(MyOrders myOrders,List<pedidoModel> modelList) {
        this.modelList = modelList;
        this.myOrders = myOrders;
    }

    @NonNull
    @Override
    public PedidosUserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_pedidos_design, parent, false);
        PedidosUserViewHolder view = new PedidosUserViewHolder(itemView);
        return view;
    }

    @Override
    public void onBindViewHolder(@NonNull PedidosUserViewHolder holder, int position) {
        holder.txorderID.setText(modelList.get(position).getpID());
        holder.txtotal.setText(String.format("$%.2f", modelList.get(position).getPagoTotal()));
        holder.txdate.setText(modelList.get(position).getpDate());
        holder.txnegocioName.setText(modelList.get(position).getNeogioName());

        boolean isExpanded = modelList.get(position).isExpanded();
        holder.layoutEx.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }


    class PedidosUserViewHolder extends RecyclerView.ViewHolder{
        TextView txorderID, txnegocioName, txtotal, txdate;
        LinearLayout layoutEx;
        CardView cardP;
        public PedidosUserViewHolder(@NonNull View itemView) {
            super(itemView);
            cardP = itemView.findViewById(R.id.cardPedidoItem);
            txorderID = itemView.findViewById(R.id.txnopedido);
            txnegocioName = itemView.findViewById(R.id.txnegocio);
            txtotal = itemView.findViewById(R.id.txtotalpedido);
            txdate = itemView.findViewById(R.id.txfechapedido);
            layoutEx = itemView.findViewById(R.id.expandableLayout);

            cardP.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pedidoModel model = modelList.get(getAdapterPosition());
                    model.setExpanded(!model.isExpanded());
                    notifyItemChanged(getAdapterPosition());
                }
            });
        }
    }
}
