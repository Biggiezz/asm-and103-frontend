package com.example.asm_and103_ph63816.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.asm_and103_ph63816.R;
import com.example.asm_and103_ph63816.adapter.ProductAdapter;
import com.example.asm_and103_ph63816.handle.ItemProductHandle;
import com.example.asm_and103_ph63816.model.Cart;
import com.example.asm_and103_ph63816.model.Category;
import com.example.asm_and103_ph63816.model.Product;
import com.example.asm_and103_ph63816.model.Response;
import com.example.asm_and103_ph63816.services.HttpRequest;
import com.example.asm_and103_ph63816.utils.ProductImageUtil;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;

public class ProductActivity extends AppCompatActivity {
    private ProductAdapter adapter;
    private HttpRequest httpRequest;
    private RecyclerView recycleProductsAsm;
    private final ArrayList<Product> list = new ArrayList<>();
    private final ArrayList<Category> categoryList = new ArrayList<>();
    private FloatingActionButton fabAddProduct;
    private EditText edtSearch;
    private String imageProduct;
    private ImageView imgSelectedProduct, imgBack, imgCart;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private Runnable searchRunnable;
    String keyword = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.product_activity);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initUi();

        httpRequest = new HttpRequest();

        /// load list sản phẩm
        loadListProduct();
        loadListCategory();

        /// quay lại
        imgBack.setOnClickListener(v -> finish());

        /// giỏ hàng
        imgCart.setOnClickListener(v -> {
            Intent intent = new Intent(this, CartActivity.class);
            startActivity(intent);
        });

        /// nút thêm sản phẩm
        fabAddProduct.setOnClickListener(v -> {
            if (categoryList.isEmpty()) {
                loadListCategory();
                Toast.makeText(this, "Chưa có danh mục để chọn", Toast.LENGTH_LONG).show();
                return;
            }
            imageProduct = null;
            View view = LayoutInflater.from(this)
                    .inflate(R.layout.activity_product_detail, null);

            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setView(view)
                    .create();

            ImageView imgProduct = view.findViewById(R.id.imgProduct);
            TextInputEditText edtProductName = view.findViewById(R.id.edtProductName);
            TextInputEditText edtVolume = view.findViewById(R.id.edtProductVolume);
            TextInputEditText edtPrice = view.findViewById(R.id.edtProductPrice);
            TextInputEditText edtQuantity = view.findViewById(R.id.edtProductQuantity);
            TextInputEditText edtDescription = view.findViewById(R.id.edtProductDescription);
            TextInputEditText edtStar = view.findViewById(R.id.edtProductStar);
            Spinner spCategory = view.findViewById(R.id.spCategory);
            MaterialButton btnAddProduct = view.findViewById(R.id.btnSaveProduct);
            MaterialButton btnCancel = view.findViewById(R.id.btnCancelProduct);
            setupCategorySpinner(spCategory, "");

            imgProduct.setOnClickListener(v1 -> chooseImage(imgProduct));

            btnAddProduct.setOnClickListener(v1 -> {
                String productName = edtProductName.getText().toString().trim();
                String volume = edtVolume.getText().toString().trim();
                String price = edtPrice.getText().toString().trim();
                String quantity = edtQuantity.getText().toString().trim();
                String description = edtDescription.getText().toString().trim();
                String star = edtStar.getText().toString().trim();
                String categoryId = getSelectedCategoryId(spCategory);
                if (productName.isEmpty() || volume.isEmpty() || price.isEmpty()
                        || quantity.isEmpty() || description.isEmpty() || star.isEmpty()) {
                    Toast.makeText(this, "Vui lòng không bỏ trống", Toast.LENGTH_LONG).show();
                    return;
                }
                if (categoryId.isEmpty()) {
                    Toast.makeText(this, "Vui lòng chọn danh mục", Toast.LENGTH_LONG).show();
                    return;
                }
                if (imageProduct == null || imageProduct.trim().isEmpty()) {
                    Toast.makeText(this, "Vui lòng chọn ảnh sản phẩm", Toast.LENGTH_LONG).show();
                    return;
                }
                try {
                    Double.parseDouble(volume);
                    Double.parseDouble(price);
                    Integer.parseInt(quantity);
                    Double.parseDouble(star);
                    httpRequest.callAPI().addProduct(
                            createText(productName),
                            createText(volume),
                            createText(price),
                            createText(quantity),
                            createText(description),
                            createText(star),
                            createText(categoryId),
                            createImagePart(imageProduct)
                    ).enqueue(addProductAPI);
                    dialog.dismiss();
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "Vui lòng nhập số hợp lệ (Quantity là số nguyên)", Toast.LENGTH_LONG).show();
                } catch (IOException e) {
                    Toast.makeText(this, "Không đọc được ảnh đã chọn", Toast.LENGTH_LONG).show();
                }
            });

            btnCancel.setOnClickListener(v1 -> dialog.dismiss());

            dialog.show();
        });
    }


    private void initUi() {
        imgBack = findViewById(R.id.imgBack);
        imgCart = findViewById(R.id.imgCart);
        recycleProductsAsm = findViewById(R.id.recyleView);
        fabAddProduct = findViewById(R.id.fabAddProduct);
        edtSearch = findViewById(R.id.edtSearch);

        adapter = new ProductAdapter(this, list, new ItemProductHandle() {
            @Override
            public void onDelete(int position) {
                showDeleteDialog(list.get(position));
            }

            @Override
            public void onEdit(int position) {
                showUpdateDialog(list.get(position));
            }

            @Override
            public void onAddToCart(int position) {
                addToCart(list.get(position));
            }
        });

        recycleProductsAsm.setLayoutManager(new LinearLayoutManager(this));
        recycleProductsAsm.setAdapter(adapter);

        TextWatcher filterTextWatcher = new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                keyword = edtSearch.getText().toString().trim();
                if (searchRunnable != null) {
                    handler.removeCallbacks(searchRunnable);
                }

                searchRunnable = () -> {
                    if (keyword.isEmpty()) {
                        Log.d("SEARCH_API", "Lay lai danh sach ban dau");

                        httpRequest.callAPI()
                                .getListProduct()
                                .enqueue(getListProduct);
                    } else {
                        Log.d("SEARCH_API", "Tim kiem voi key: " + keyword);

                        httpRequest.callAPI()
                                .searchProduct(keyword)
                                .enqueue(getListProduct);
                    }
                };

                handler.postDelayed(searchRunnable, 500);

            }
        };
        edtSearch.addTextChangedListener(filterTextWatcher);
    }

    private void loadListCategory() {
        httpRequest.callAPI().getListCategory().enqueue(getListCategory);
    }

    private void setupCategorySpinner(Spinner spCategory, String selectedCategoryId) {
        ArrayList<String> categoryNames = new ArrayList<>();
        if (categoryList.isEmpty()) {
            categoryNames.add("Chưa có danh mục");
        } else {
            for (Category category : categoryList) {
                categoryNames.add(category.getName());
            }
        }

        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                categoryNames
        );
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCategory.setAdapter(categoryAdapter);

        int selectedPosition = getCategoryPosition(selectedCategoryId);
        if (selectedPosition >= 0) {
            spCategory.setSelection(selectedPosition);
        }
    }

    private int getCategoryPosition(String categoryId) {
        if (categoryId == null || categoryId.trim().isEmpty()) {
            return -1;
        }
        for (int i = 0; i < categoryList.size(); i++) {
            if (categoryId.equals(categoryList.get(i).getId())) {
                return i;
            }
        }
        return -1;
    }

    private String getSelectedCategoryId(Spinner spCategory) {
        int position = spCategory.getSelectedItemPosition();
        if (position < 0 || position >= categoryList.size()) {
            return "";
        }
        String categoryId = categoryList.get(position).getId();
        return categoryId == null ? "" : categoryId;
    }

    private void addToCart(Product product) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Cart cart = new Cart(userId, product.get_id(), 1);
        httpRequest.callAPI().addToCart(cart).enqueue(new Callback<Response<Cart>>() {
            @Override
            public void onResponse(Call<Response<Cart>> call, retrofit2.Response<Response<Cart>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getStatus() == 200) {
                    Toast.makeText(ProductActivity.this, "Đã thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ProductActivity.this, "Thêm thất bại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Response<Cart>> call, Throwable t) {
                Toast.makeText(ProductActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDeleteDialog(Product product) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Xóa sản phẩm");
        dialog.setMessage("Bạn có chắc chắn muốn xóa sản phẩm này không");
        dialog.setPositiveButton("Đồng ý", (dialog1, which) -> {
            httpRequest.callAPI().deleteProductById(product.get_id()).enqueue(deleteProductAPI);
            dialog1.dismiss();
        });

        dialog.setNegativeButton("Không", (dialog1, which) -> {
            dialog1.dismiss();
        });

        dialog.show();
    }

    private void showUpdateDialog(Product product) {
        imageProduct = product.getImage();
        View view = LayoutInflater.from(this).inflate(R.layout.activity_product_detail, null);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(view)
                .create();

        TextView tvProductFormTitle = view.findViewById(R.id.tvProductFormTitle);
        ImageView imgProduct = view.findViewById(R.id.imgProduct);
        TextInputEditText edtProductName = view.findViewById(R.id.edtProductName);
        TextInputEditText edtVolume = view.findViewById(R.id.edtProductVolume);
        TextInputEditText edtPrice = view.findViewById(R.id.edtProductPrice);
        TextInputEditText edtQuantity = view.findViewById(R.id.edtProductQuantity);
        TextInputEditText edtDescription = view.findViewById(R.id.edtProductDescription);
        TextInputEditText edtStar = view.findViewById(R.id.edtProductStar);
        Spinner spCategory = view.findViewById(R.id.spCategory);
        MaterialButton btnUpdate = view.findViewById(R.id.btnSaveProduct);
        MaterialButton btnCancel = view.findViewById(R.id.btnCancelProduct);

        tvProductFormTitle.setText("Cập nhật sản phẩm");
        edtProductName.setText(product.getName());
        edtVolume.setText(String.valueOf(product.getVolume()));
        edtPrice.setText(String.valueOf(product.getPrice()));
        edtQuantity.setText(String.valueOf(product.getQuantity()));
        edtDescription.setText(product.getDescription());
        edtStar.setText(String.valueOf(product.getStar()));
        setupCategorySpinner(spCategory, product.getCategoryId());
        ProductImageUtil.loadImage(this, imageProduct, imgProduct);

        imgProduct.setOnClickListener(v -> chooseImage(imgProduct));
        btnUpdate.setText("Cập nhật");

        btnUpdate.setOnClickListener(v -> {
            String productName = edtProductName.getText().toString().trim();
            String volume = edtVolume.getText().toString().trim();
            String price = edtPrice.getText().toString().trim();
            String quantity = edtQuantity.getText().toString().trim();
            String description = edtDescription.getText().toString().trim();
            String star = edtStar.getText().toString().trim();
            String categoryId = getSelectedCategoryId(spCategory);
            if (productName.isEmpty() || volume.isEmpty() || price.isEmpty()
                    || quantity.isEmpty() || description.isEmpty() || star.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_LONG).show();
                return;
            }
            if (categoryId.isEmpty()) {
                Toast.makeText(this, "Vui lòng chọn danh mục", Toast.LENGTH_LONG).show();
                return;
            }
            double volumeDouble;
            double priceDouble;
            int quantityInt;
            double starDouble;
            try {
                volumeDouble = Double.parseDouble(volume);
                priceDouble = Double.parseDouble(price);
                quantityInt = Integer.parseInt(quantity);
                starDouble = Double.parseDouble(star);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Vui lòng nhập số hợp lệ (Quantity là số nguyên)", Toast.LENGTH_LONG).show();
                return;
            }
            Product productUpdate = new Product(productName, volumeDouble, priceDouble, quantityInt, description, starDouble);
            productUpdate.setImage(imageProduct);
            productUpdate.setCategoryId(categoryId);
            httpRequest.callAPI().updateProductById(product.get_id(), productUpdate).enqueue(updateProductAPI);
            dialog.dismiss();
        });
        btnCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void chooseImage(ImageView imgProduct) {
        imgSelectedProduct = imgProduct;
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        startActivityForResult(intent, 1);
    }

    private RequestBody createText(String value) {
        return RequestBody.create(MediaType.parse("text/plain"), value);
    }

    private MultipartBody.Part createImagePart(String image) throws IOException {
        Uri uri = Uri.parse(image);
        InputStream inputStream = getContentResolver().openInputStream(uri);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int length;
        while (inputStream != null && (length = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, length);
        }
        if (inputStream != null) {
            inputStream.close();
        }
        RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), outputStream.toByteArray());
        return MultipartBody.Part.createFormData("image", "product.jpg", requestBody);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            imageProduct = uri.toString();
            getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
            if (imgSelectedProduct != null) {
                imgSelectedProduct.setImageURI(uri);
            }
        }
    }

    private void loadListProduct() {
        fetchProductList();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (httpRequest != null) {
            fetchProductList();
        }
    }

    private void fetchProductList() {
        httpRequest.callAPI().getListProduct().enqueue(getListProduct);
    }

    Callback<Response<Product>> updateProductAPI = new Callback<Response<Product>>() {
        @Override
        public void onResponse(Call<Response<Product>> call, retrofit2.Response<Response<Product>> response) {
            if (response.isSuccessful() && response.body().getStatus() == 200) {
                Toast.makeText(ProductActivity.this, "Cập nhật thành công", Toast.LENGTH_LONG).show();
                fetchProductList();
            } else {
                Toast.makeText(ProductActivity.this, "Cập nhật thất bại", Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onFailure(Call<Response<Product>> call, Throwable throwable) {
            Toast.makeText(ProductActivity.this, "Lỗi API: " + throwable.getMessage(), Toast.LENGTH_LONG).show();
        }
    };
    Callback<Response<Product>> addProductAPI = new Callback<Response<Product>>() {
        @Override
        public void onResponse(Call<Response<Product>> call, retrofit2.Response<Response<Product>> response) {
            if (response.isSuccessful() && response.body().getStatus() == 200) {
                Toast.makeText(ProductActivity.this, "Thêm sản phẩm thành công", Toast.LENGTH_LONG).show();
                fetchProductList();
            } else {
                Toast.makeText(ProductActivity.this, "Thêm sản phẩm thất bại", Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onFailure(Call<Response<Product>> call, Throwable throwable) {
            Toast.makeText(ProductActivity.this, "Lỗi API: " + throwable.getMessage(), Toast.LENGTH_LONG).show();
        }
    };

    Callback<Response<Product>> deleteProductAPI = new Callback<Response<Product>>() {
        @Override
        public void onResponse(Call<Response<Product>> call, retrofit2.Response<Response<Product>> response) {
            if (response.isSuccessful() && response.body().getStatus() == 200) {
                Toast.makeText(ProductActivity.this, "Xóa thành công", Toast.LENGTH_LONG).show();
                fetchProductList();
            } else {
                Toast.makeText(ProductActivity.this, "Xóa thất bại", Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onFailure(Call<Response<Product>> call, Throwable throwable) {
            Toast.makeText(ProductActivity.this, "Lỗi API: " + throwable.getMessage(), Toast.LENGTH_LONG).show();
        }
    };
    private final Callback<Response<ArrayList<Product>>> getListProduct =
            new Callback<Response<ArrayList<Product>>>() {
                @Override
                public void onResponse(Call<Response<ArrayList<Product>>> call, retrofit2.Response<Response<ArrayList<Product>>> response) {
                    Log.d("PRODUCT_API", "code: " + response.code());
                    Log.d("PRODUCT_API", "body: " + response.body());
                    if (response.isSuccessful() && response.body().getStatus() == 200) {
                        adapter.setData(response.body().getData());
                    } else {
                        Toast.makeText(ProductActivity.this, "Không lấy được sản phẩm", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<Response<ArrayList<Product>>> call, Throwable throwable) {
                    Log.d(">>> GetListProduct", "onFailure: " + throwable.getMessage());
                    Toast.makeText(ProductActivity.this, "Lỗi API: " + throwable.getMessage(), Toast.LENGTH_LONG).show();
                }
            };

    private final Callback<Response<ArrayList<Category>>> getListCategory =
            new Callback<Response<ArrayList<Category>>>() {
                @Override
                public void onResponse(Call<Response<ArrayList<Category>>> call, retrofit2.Response<Response<ArrayList<Category>>> response) {
                    if (response.isSuccessful() && response.body().getStatus() == 200) {
                        categoryList.clear();
                        if (response.body().getData() != null) {
                            categoryList.addAll(response.body().getData());
                        }
                    } else {
                        Toast.makeText(ProductActivity.this, "Không lấy được danh mục", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<Response<ArrayList<Category>>> call, Throwable throwable) {
                    Toast.makeText(ProductActivity.this, "Lỗi API danh mục: " + throwable.getMessage(), Toast.LENGTH_LONG).show();
                }
            };
}
