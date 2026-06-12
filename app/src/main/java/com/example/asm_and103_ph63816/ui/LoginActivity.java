package com.example.asm_and103_ph63816.ui;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.asm_and103_ph63816.HomePageActivity;
import com.example.asm_and103_ph63816.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private TextInputEditText edtEmail, edtPassword;
    private Toolbar toolbarLoginScreen;
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
            if (!isNetworkAvailable()) {
                Toast.makeText(this, "Thiết bị chưa có Internet, không thể kết nối Firebase", Toast.LENGTH_LONG).show();
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
                    Log.e(TAG, "Firebase login failed", exception);
                    Toast.makeText(this, getLoginErrorMessage(exception), Toast.LENGTH_LONG).show();
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
        toolbarLoginScreen = findViewById(R.id.toolbarLoginScreen);
        btnLogin = findViewById(R.id.btnLogin);

        tvSignUp = findViewById(R.id.tvSignUp);
        tvSignUp.setText(Html.fromHtml("<u>" + "Create new account" + "</u>"));
        tvForgotPass = findViewById(R.id.tvForgotPass);
        tvForgotPass.setText(Html.fromHtml("<u>" + "Forgot password?" + "</u>"));
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
            return false;
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
            return capabilities != null && (
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                            || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                            || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
            );
        }
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }

    private String getLoginErrorMessage(Exception exception) {
        if (exception instanceof FirebaseAuthException) {
            String errorCode = ((FirebaseAuthException) exception).getErrorCode();
            if ("ERROR_INVALID_EMAIL".equals(errorCode)) {
                return "Email không đúng định dạng";
            }
            if ("ERROR_USER_NOT_FOUND".equals(errorCode)
                    || "ERROR_WRONG_PASSWORD".equals(errorCode)
                    || "ERROR_INVALID_CREDENTIAL".equals(errorCode)) {
                return "Email hoặc mật khẩu không đúng";
            }
            if ("ERROR_USER_DISABLED".equals(errorCode)) {
                return "Tài khoản này đã bị vô hiệu hóa";
            }
            if ("ERROR_OPERATION_NOT_ALLOWED".equals(errorCode)) {
                return "Firebase chưa bật đăng nhập bằng Email/Password";
            }
            if ("ERROR_NETWORK_REQUEST_FAILED".equals(errorCode)) {
                return "Không kết nối được Firebase. Kiểm tra mạng/emulator";
            }
        }
        if (exception != null && exception.getMessage() != null && !exception.getMessage().isEmpty()) {
            return "Đăng nhập thất bại: " + exception.getMessage();
        }
        return "Đăng nhập thất bại";
    }
}
