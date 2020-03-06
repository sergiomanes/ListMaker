package com.application.sergiomanes.ListasDeCompras.ui.adapter;

import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.application.sergiomanes.ListasDeCompras.R;
import com.application.sergiomanes.ListasDeCompras.model.Producto;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;

public class DetailAdapter extends RecyclerView.Adapter<DetailAdapter.Holder> {

    private ArrayList<Producto> list;
    private OnItemClickListener onItemClickListener;
    private Resources resources;

    public DetailAdapter(ArrayList<Producto> list, Resources resources, OnItemClickListener item) {
        this.list = list;
        this.resources = resources;
        this.onItemClickListener = item;
    }

    @Override
    public Holder onCreateViewHolder( ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(resources.getLayout(R.layout.item_recycler_view), parent, false);

        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(final Holder holder, final int position) {
        Producto producto = list.get(position);
        holder.id.setText(String.valueOf(producto.getCode()));
        holder.name.setText(producto.getName());
        holder.count.setText(String.valueOf(producto.getCount()));
        holder.price.setText(String.valueOf(producto.getPrice()));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.OnItemClick(holder.getAdapterPosition());
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class Holder extends RecyclerView.ViewHolder {

        private TextView id, name, count, price;

        public Holder(View itemView) {
            super(itemView);
            id = itemView.findViewById(R.id.productIDTextView);
            name = itemView.findViewById(R.id.productNameEditText);
            count = itemView.findViewById(R.id.productCountTextView);
            price = itemView.findViewById(R.id.productPriceTextView);
        }
    }


    public interface OnItemClickListener{
        void OnItemClick(int pos);
    }
}
