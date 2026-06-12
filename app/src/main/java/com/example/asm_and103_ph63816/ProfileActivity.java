package com.example.asm_and103_ph63816;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.asm_and103_ph63816.ui.LoginActivity;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private TextView tvProfileEmail;
    private MaterialButton btnLogoutProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        auth = FirebaseAuth.getInstance();
        tvProfileEmail = findViewById(R.id.tvProfileEmail);
        btnLogoutProfile = findViewById(R.id.btnLogoutProfile);

        FirebaseUser user = auth.getCurrentUser();
        if (user != null && user.getEmail() != null) {
            tvProfileEmail.setText(user.getEmail());
        }

        btnLogoutProfile.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Đăng xuất " + user.getEmail());
            builder.setMessage("Bạn chắc chắn muốn đăng xuất tài khoản?");
            builder.setPositiveButton("Đồng ý", (dialog, which) -> {
                auth.signOut();
                Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                dialog.dismiss();
            });
            builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());
            AlertDialog dialog1 = builder.create();
            dialog1.show();

        });
    }
}
