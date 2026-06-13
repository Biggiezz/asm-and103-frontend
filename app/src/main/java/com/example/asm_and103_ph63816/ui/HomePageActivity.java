package com.example.asm_and103_ph63816.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.asm_and103_ph63816.R;
import com.google.android.material.card.MaterialCardView;

public class HomePageActivity extends AppCompatActivity {
    private ImageView imgProfile;
    private MaterialCardView btnCardCategory;
    private MaterialCardView btnCardProduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_homepage);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initUi();
        setOnClick();
    }

    private void setOnClick() {

        imgProfile.setOnClickListener(v -> {
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
        });

        btnCardCategory.setOnClickListener(v -> {
            startActivity(new Intent(this, CategoryActivity.class));
        });

        btnCardProduct.setOnClickListener(v -> {
            startActivity(new Intent(this, ProductActivity.class));
        });
    }

    private void initUi() {
        imgProfile = findViewById(R.id.imgProfile);
        btnCardCategory = findViewById(R.id.btnCardCategory);
        btnCardProduct = findViewById(R.id.btnCardProduct);
    }
}