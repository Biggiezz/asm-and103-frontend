package com.example.asm_and103_ph63816.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.asm_and103_ph63816.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

public class CreateAccountActivity extends AppCompatActivity {
    private TextInputEditText edtEmail, edtPassword;
    private Toolbar toolbarCreateAccountScreen;
    private TextView tvLogIn;
    private MaterialButton btnCreateAccount;
    private FirebaseAuth mAuthCreateAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_account);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initUi();
        mAuthCreateAccount = FirebaseAuth.getInstance();

        btnCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = edtEmail.getText().toString().trim();
                String password = edtPassword.getText().toString().trim();
                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(CreateAccountActivity.this, "Vui lòng không được bỏ trống", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (password.length() < 6) {
                    Toast.makeText(CreateAccountActivity.this, "Mật khẩu phải có ít nhất 6 ký tự", Toast.LENGTH_SHORT).show();
                    return;
                }
                mAuthCreateAccount.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Toast.makeText(CreateAccountActivity.this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(CreateAccountActivity.this, LoginActivity.class);
                        startActivity(intent);

                        finish();
                    } else {
                        Toast.makeText(CreateAccountActivity.this, "Đăng ký thất bại", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void initUi() {
        toolbarCreateAccountScreen = findViewById(R.id.toolbarCreateAccountScreen);
        toolbarCreateAccountScreen.setNavigationOnClickListener(v ->{
            Intent intent = new Intent(CreateAccountActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        btnCreateAccount = findViewById(R.id.btnCreateAccount);
        tvLogIn = findViewById(R.id.tvLogIn);
        tvLogIn.setText(Html.fromHtml("<u>" + "LogIn" + "x.co</u>"));
    }
}