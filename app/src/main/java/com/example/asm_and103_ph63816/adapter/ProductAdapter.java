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
import com.example.asm_and103_ph63816.handle.ItemProductHandle;
import com.example.asm_and103_ph63816.model.Product;
import com.example.asm_and103_ph63816.utils.ProductImageUtil;

import java.util.ArrayList;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    private final ArrayList<Product> list;
    private final ItemProductHandle itemProductHandle;
    private final Context context;

    public ProductAdapter(Context context, ArrayList<Product> list, ItemProductHandle itemProductHandle) {
        this.context = context;
        this.list = list;
        this.itemProductHandle = itemProductHandle;
    }

    public void setData(ArrayList<Product> data) {
        list.clear();
        if (data != null) {
            list.addAll(data);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product_asm, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = list.get(position);
        holder.tvProductName.setText("Tên sản phẩm: " + product.getName());
        holder.tvProductVolume.setText("Khối lượng: " + product.getVolume() + " kg");
        holder.tvProductPrice.setText("Giá tiền: " + product.getPrice() + " VND");
        holder.tvProductQuantity.setText("Số lượng: " + product.getQuantity());
        holder.tvProductStar.setText("Chất lượng: " + product.getStar() + " sao");

        ProductImageUtil.loadImage(holder.itemView.getContext(), product.getImage(), holder.imgProduct);

        holder.itemView.setOnClickListener(v -> {
            itemProductHandle.onEdit(holder.getAdapterPosition());
        });
        holder.imgEdit.setOnClickListener(v -> {
            itemProductHandle.onEdit(holder.getAdapterPosition());
        });
        holder.imgDelete.setOnClickListener(v -> {
            itemProductHandle.onDelete(holder.getAdapterPosition());
        });
        holder.imgAddToCart.setOnClickListener(v -> {
            itemProductHandle.onAddToCart(holder.getAdapterPosition());
        });
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProduct, imgEdit, imgDelete, imgAddToCart;
        TextView tvProductName, tvProductVolume, tvProductPrice, tvProductQuantity, tvProductStar;

        ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.imgProduct);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvProductVolume = itemView.findViewById(R.id.tvProductVolume);
            tvProductPrice = itemView.findViewById(R.id.tvProductPrice);
            tvProductQuantity = itemView.findViewById(R.id.tvProductQuantity);
            tvProductStar = itemView.findViewById(R.id.tvProductStar);
            imgEdit = itemView.findViewById(R.id.imgEdit);
            imgDelete = itemView.findViewById(R.id.imgDelete);
            imgAddToCart = itemView.findViewById(R.id.imgAddToCart);
        }
    }
}
