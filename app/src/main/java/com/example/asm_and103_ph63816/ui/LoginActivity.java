package com.example.asm_and103_ph63816.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.asm_and103_ph63816.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private TextInputEditText edtEmail, edtPassword;
    private TextView tvSignUp, tvForgotPass;
    private MaterialButton btnLogin;
    private FirebaseAuth mAuthForASM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initUi();
        mAuthForASM = FirebaseAuth.getInstance();

        btnLogin.setOnClickListener(v -> {
            String email = edtEmail.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Vui lòng không được bỏ trống", Toast.LENGTH_LONG).show();
                return;
            } else if (password.length() < 6) {
                Toast.makeText(this, "Mật khẩu phải có ít nhất 6 ký tự", Toast.LENGTH_LONG).show();
                return;
            }

            btnLogin.setEnabled(false);
            mAuthForASM.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                btnLogin.setEnabled(true);
                if (task.isSuccessful()) {
                    FirebaseUser user = mAuthForASM.getCurrentUser();
                    String userEmail = user != null ? user.getEmail() : email;
                    Toast.makeText(this, "Đăng nhập thành công: " + userEmail, Toast.LENGTH_LONG).show();
                    startActivity(new Intent(this, HomePageActivity.class));
                    finish();
                } else {
                    Exception exception = task.getException();
                    Log.e("LoginActivity", "Firebase login failed", exception);
                }
            });
        });
        tvSignUp.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, CreateAccountActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void initUi() {
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);

        tvSignUp = findViewById(R.id.tvSignUp);
        tvSignUp.setText(Html.fromHtml("<u>" + "Create new account" + "</u>"));
        tvForgotPass = findViewById(R.id.tvForgotPass);
        tvForgotPass.setText(Html.fromHtml("<u>" + "Forgot password?" + "</u>"));
    }
}
