package com.example.asm_and103_ph63816.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.asm_and103_ph63816.R;
import com.example.asm_and103_ph63816.handle.CategoryHandle;
import com.example.asm_and103_ph63816.model.Category;

import java.util.ArrayList;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {
    private final Context context;
    private final ArrayList<Category> list;
    private final CategoryHandle handle;

    public CategoryAdapter(Context context, ArrayList<Category> list, CategoryHandle handle) {
        this.context = context;
        this.list = list;
        this.handle = handle;
    }

    public void setData(ArrayList<Category> data) {
        list.clear();
        if (data!= null) {
            list.addAll(data);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CategoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_category, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryAdapter.ViewHolder holder, int position) {
        Category category = list.get(position);
        holder.tvCategoryName.setText("Tên danh mục: "+category.getName());
        holder.imgEditCategoryItem.setOnClickListener(v -> {
            int adapterPosition = holder.getAdapterPosition();
            if (adapterPosition != RecyclerView.NO_POSITION) {
                handle.onEdit(adapterPosition);
            }
        });
        holder.imgDeleteCategoryItem.setOnClickListener(v -> {
            int adapterPosition = holder.getAdapterPosition();
            if (adapterPosition != RecyclerView.NO_POSITION) {
                handle.onDelete(adapterPosition);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvCategoryName;
        private final ImageView imgEditCategoryItem;
        private final ImageView imgDeleteCategoryItem;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCategoryName = itemView.findViewById(R.id.tvCategoryName);
            imgEditCategoryItem = itemView.findViewById(R.id.imgEditCategoryItem);
            imgDeleteCategoryItem = itemView.findViewById(R.id.imgDeleteCategoryItem);
        }
    }
}
