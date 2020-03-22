package com.application.sergiomanes.ListasDeCompras.ui.adapter;

import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.application.sergiomanes.ListasDeCompras.R;
import com.application.sergiomanes.ListasDeCompras.model.Lista;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import androidx.recyclerview.widget.RecyclerView;

public class ListsAdapter extends RecyclerView.Adapter<ListsAdapter.ItemHolder> {
    private ArrayList<Lista> list;
    private OnItemClickListener onItemClickListener;
    private OnItemLongClickListener onItemLongClickListener;
    Resources resources;

    public ListsAdapter(ArrayList<Lista> list, Resources resources, OnItemClickListener item, OnItemLongClickListener itemlong) {
        this.list = list;
        this.onItemClickListener = item;
        this.onItemLongClickListener = itemlong;
        this.resources = resources;
    }

    @Override
    public ListsAdapter.ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                resources.getLayout(R.layout.lists_recycler_view),
                parent, false);

        return new ListsAdapter.ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(final ListsAdapter.ItemHolder holder, final int position) {
        Lista lista = list.get(position);
        holder.paid.setText(String.valueOf(lista.getSubtotal()));
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", Locale.US);

        holder.date.setText(formatter.format(lista.getCreatedDate()));
        holder.name.setText((lista.getName() == null || lista.getName().isEmpty()) ? resources.getString(R.string.sin_nombre) : lista.getName());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.OnItemClick(holder.getAdapterPosition());
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                onItemLongClickListener.OnItemLongClick(holder.getAdapterPosition());
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ItemHolder extends RecyclerView.ViewHolder {

        private TextView paid, date, name;

        public ItemHolder(View itemView) {
            super(itemView);
            paid = (TextView) itemView.findViewById(com.application.sergiomanes.ListasDeCompras.R.id.numeroTextView);
            date = (TextView) itemView.findViewById(com.application.sergiomanes.ListasDeCompras.R.id.fechaTextView);
            name = (TextView) itemView.findViewById(R.id.nombreTextView);
        }
    }


    public interface OnItemClickListener{
        void OnItemClick(int pos);
    }

    public interface OnItemLongClickListener{
        void OnItemLongClick(int pos);
    }
}
