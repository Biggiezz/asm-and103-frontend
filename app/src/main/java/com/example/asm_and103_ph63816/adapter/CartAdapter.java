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
import com.example.asm_and103_ph63816.model.Cart;
import com.example.asm_and103_ph63816.utils.ProductImageUtil;

import java.util.ArrayList;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    private Context context;
    private ArrayList<Cart> list;
    private CartItemListener listener;

    public interface CartItemListener {
        void onIncrease(int position);
        void onDecrease(int position);
        void onDelete(int position);
    }

    public CartAdapter(Context context, ArrayList<Cart> list, CartItemListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        Cart cart = list.get(position);
        if (cart.getProduct() != null) {
            holder.tvName.setText(cart.getProduct().getName());
            holder.tvPrice.setText("Giá: " + cart.getProduct().getPrice() + " VND");
            ProductImageUtil.loadImage(context, cart.getProduct().getImage(), holder.imgProduct);
        }
        holder.tvQuantity.setText(String.valueOf(cart.getQuantity()));

        holder.imgIncrease.setOnClickListener(v -> listener.onIncrease(position));
        holder.imgDecrease.setOnClickListener(v -> listener.onDecrease(position));
        holder.imgDelete.setOnClickListener(v -> listener.onDelete(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProduct, imgIncrease, imgDecrease, imgDelete;
        TextView tvName, tvPrice, tvQuantity;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.imgCartProduct);
            imgIncrease = itemView.findViewById(R.id.imgIncrease);
            imgDecrease = itemView.findViewById(R.id.imgDecrease);
            imgDelete = itemView.findViewById(R.id.imgDeleteCartItem);
            tvName = itemView.findViewById(R.id.tvCartProductName);
            tvPrice = itemView.findViewById(R.id.tvCartProductPrice);
            tvQuantity = itemView.findViewById(R.id.tvCartQuantity);
        }
    }
}
