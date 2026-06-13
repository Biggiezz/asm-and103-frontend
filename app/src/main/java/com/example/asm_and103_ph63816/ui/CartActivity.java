package com.example.asm_and103_ph63816.ui;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.asm_and103_ph63816.R;
import com.example.asm_and103_ph63816.adapter.CartAdapter;
import com.example.asm_and103_ph63816.handle.CartHandle;
import com.example.asm_and103_ph63816.model.Cart;
import com.example.asm_and103_ph63816.model.Order;
import com.example.asm_and103_ph63816.model.Response;
import com.example.asm_and103_ph63816.services.HttpRequest;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;

public class CartActivity extends AppCompatActivity {
    private RecyclerView rcvCart;
    private ImageView imgBack;
    private CartAdapter adapter;
    private ArrayList<Cart> cartList = new ArrayList<>();
    private TextView tvTotalAmount;
    private MaterialButton btnOrder;
    private HttpRequest httpRequest;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        initUi();

        httpRequest = new HttpRequest();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();


        adapter = new CartAdapter(this, cartList, new CartHandle() {
            @Override
            public void onIncrease(int position) {
                Cart cart = cartList.get(position);
                cart.setQuantity(cart.getQuantity() + 1);
                updateQuantity(cart);

            }

            @Override
            public void onDecrease(int position) {
                Cart cart = cartList.get(position);
                if (cart.getQuantity() > 1) {
                    cart.setQuantity(cart.getQuantity() - 1);
                    updateQuantity(cart);
                }
            }

            @Override
            public void onDelete(int position) {
                deleteCartItem(cartList.get(position).get_id());

            }
        });

        rcvCart.setLayoutManager(new LinearLayoutManager(this));
        rcvCart.setAdapter(adapter);

        btnOrder.setOnClickListener(v -> placeOrder());
        imgBack.setOnClickListener(v -> finish());
        loadCart();
    }

    private void initUi() {
        imgBack = findViewById(R.id.imgBack);
        rcvCart = findViewById(R.id.rcvCart);
        tvTotalAmount = findViewById(R.id.tvTotalAmount);
        btnOrder = findViewById(R.id.btnOrder);
    }

    private void loadCart() {
        httpRequest.callAPI().getCart(userId).enqueue(new Callback<Response<ArrayList<Cart>>>() {
            @Override
            public void onResponse(@NonNull Call<Response<ArrayList<Cart>>> call, @NonNull retrofit2.Response<Response<ArrayList<Cart>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getStatus() == 200) {
                    cartList.clear();
                    cartList.addAll(response.body().getData());
                    adapter.notifyDataSetChanged();
                    calculateTotal();
                }
            }

            @Override
            public void onFailure(Call<Response<ArrayList<Cart>>> call, Throwable t) {
                Toast.makeText(CartActivity.this, "Lỗi tải giỏ hàng", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateQuantity(Cart cart) {
        httpRequest.callAPI().updateCartQuantity(cart.get_id(), cart).enqueue(new Callback<Response<Cart>>() {
            @Override
            public void onResponse(Call<Response<Cart>> call, retrofit2.Response<Response<Cart>> response) {
                if (response.isSuccessful()) {
                    adapter.notifyDataSetChanged();
                    calculateTotal();
                }
            }

            @Override
            public void onFailure(Call<Response<Cart>> call, Throwable t) {
            }
        });
    }

    private void deleteCartItem(String id) {
        httpRequest.callAPI().deleteCartItem(id).enqueue(new Callback<Response<Cart>>() {
            @Override
            public void onResponse(Call<Response<Cart>> call, retrofit2.Response<Response<Cart>> response) {
                if (response.isSuccessful()) {
                    loadCart();
                }
            }

            @Override
            public void onFailure(Call<Response<Cart>> call, Throwable t) {
            }
        });
    }

    private void calculateTotal() {
        double total = 0;
        for (Cart cart : cartList) {
            if (cart.getProduct() != null) {
                total += cart.getQuantity() * cart.getProduct().getPrice();
            }
        }
        tvTotalAmount.setText(total + " VND");
    }

    private void placeOrder() {
        if (cartList.isEmpty()) {
            Toast.makeText(this, "Giỏ hàng trống", Toast.LENGTH_SHORT).show();
            return;
        }

        double total = 0;
        for (Cart cart : cartList) {
            if (cart.getProduct() != null) {
                total += cart.getQuantity() * cart.getProduct().getPrice();
            }
        }

        String date = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(new Date());
        Order order = new Order(userId, new ArrayList<>(cartList), total, "Pending", date);

        httpRequest.callAPI().addOrder(order).enqueue(new Callback<Response<Order>>() {
            @Override
            public void onResponse(Call<Response<Order>> call, retrofit2.Response<Response<Order>> response) {
                if (response.isSuccessful() && response.body().getStatus() == 200) {
                    Toast.makeText(CartActivity.this, "Đặt hàng thành công", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<Response<Order>> call, Throwable t) {
                Toast.makeText(CartActivity.this, "Lỗi đặt hàng", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
