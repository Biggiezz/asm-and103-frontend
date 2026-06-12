package com.example.asm_and103_ph63816;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.asm_and103_ph63816.model.Product;
import com.example.asm_and103_ph63816.model.Response;
import com.example.asm_and103_ph63816.services.HttpRequest;
import com.example.asm_and103_ph63816.utils.ProductImageUtil;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;

public class ProductDetail extends AppCompatActivity {
    private boolean isEdit;
    private String productId;
    private String imageProduct;
    private TextView tvProductFormTitle;
    private MaterialButton btnSave, btnCancel;
    private TextInputEditText edtProductName, edtProductVolume, edtProductPrice, edtProductQuantity, edtProductDescription, edtProductStar;
    private HttpRequest httpRequest;
    private ImageView imgProduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_product_detail);

        initUi();
        httpRequest = new HttpRequest();

        imgProduct.setOnClickListener(v -> chooseImage());
        isEdit = getIntent().getBooleanExtra("isEdit",
                "edit".equals(getIntent().getStringExtra("edit")));
        productId = getIntent().getStringExtra("productId");
        tvProductFormTitle.setText(isEdit ? "Cập nhật sản phẩm" : "Thêm sản phẩm");
        btnSave.setText(isEdit ? "Lưu thay đổi" : "Thêm sản phẩm");
        if (isEdit) {
            imageProduct = getIntent().getStringExtra("image");
            edtProductName.setText(getIntent().getStringExtra("name"));
            edtProductVolume.setText(String.valueOf(getIntent().getIntExtra("volume", 0)));
            edtProductPrice.setText(String.valueOf(getIntent().getIntExtra("price", 0)));
            edtProductQuantity.setText(String.valueOf(getIntent().getIntExtra("quantity", 0)));
            edtProductDescription.setText(getIntent().getStringExtra("description"));
            edtProductStar.setText(String.valueOf(getIntent().getIntExtra("star", 0)));
            ProductImageUtil.loadImage(this, imageProduct, imgProduct);
        }
        btnSave.setOnClickListener(v -> {
            String name = edtProductName.getText().toString().trim();
            String volume = edtProductVolume.getText().toString().trim();
            String price = edtProductPrice.getText().toString().trim();
            String quantity = edtProductQuantity.getText().toString().trim();
            String description = edtProductDescription.getText().toString().trim();
            String star = edtProductStar.getText().toString().trim();
            if (name.isEmpty() || volume.isEmpty() || price.isEmpty() ||
                    quantity.isEmpty() || description.isEmpty() || star.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            int volumeInt = Integer.parseInt(volume);
            int priceInt = Integer.parseInt(price);
            int quantityInt = Integer.parseInt(quantity);
            int starInt = Integer.parseInt(star);

            Product product = new Product(name, volumeInt, priceInt, quantityInt, description, starInt);
            product.set_id(isEdit ? productId : createProductId());
            product.setImage(imageProduct);
            if (isEdit) {
                if (productId == null || productId.trim().isEmpty()) {
                    Toast.makeText(this, "Không tìm thấy mã sản phẩm để cập nhật", Toast.LENGTH_LONG).show();
                    return;
                }
                httpRequest.callAPI().updateProductById(productId, product).enqueue(responeUpdateProduct);

            }
        });
        btnCancel.setOnClickListener(v -> finish());
    }

    private void chooseImage() {
        openGallery();
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            imageProduct = uri.toString();
            getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
            imgProduct.setImageURI(uri);
        }
    }

    private void initUi() {
        tvProductFormTitle = findViewById(R.id.tvProductFormTitle);
        imgProduct = findViewById(R.id.imgProduct);
        edtProductName = findViewById(R.id.edtProductName);
        edtProductVolume = findViewById(R.id.edtProductVolume);
        edtProductPrice = findViewById(R.id.edtProductPrice);
        edtProductQuantity = findViewById(R.id.edtProductQuantity);
        edtProductDescription = findViewById(R.id.edtProductDescription);
        edtProductStar = findViewById(R.id.edtProductStar);
        btnSave = findViewById(R.id.btnSaveProduct);
        btnCancel = findViewById(R.id.btnCancelProduct);
    }

    Callback<Response<Product>> responeUpdateProduct = new Callback<Response<Product>>() {
        @Override
        public void onResponse(Call<Response<Product>> call, retrofit2.Response<Response<Product>> response) {
            if (response.isSuccessful()) {
                if ((response.body() != null ? response.body().getStatus() : 0) == 200) {
                    Toast.makeText(ProductDetail.this, "Cập nhật thành công", Toast.LENGTH_LONG).show();
                    return;
                } else {
                    Toast.makeText(ProductDetail.this, "Lỗi, Cập nhật thất bại" + response.body().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }

        @Override
        public void onFailure(Call<Response<Product>> call, Throwable throwable) {
            Toast.makeText(ProductDetail.this, "Lỗi, Cập nhật thất bại" + throwable.getMessage(), Toast.LENGTH_LONG).show();
            Log.e("API updateProduct", "Update Product fail", throwable);
        }
    };

    private String createProductId() {
        return "SP" + System.currentTimeMillis();
    }
}
