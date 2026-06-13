package com.example.asm_and103_ph63816.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
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
import com.example.asm_and103_ph63816.adapter.CategoryAdapter;
import com.example.asm_and103_ph63816.handle.CategoryHandle;
import com.example.asm_and103_ph63816.model.Category;
import com.example.asm_and103_ph63816.model.Response;
import com.example.asm_and103_ph63816.services.HttpRequest;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;

public class CategoryActivity extends AppCompatActivity {
    private ImageView imgBack;
    private RecyclerView rcvCatogty;
    private CategoryAdapter adapter;
    private final ArrayList<Category> list = new ArrayList<>();
    private HttpRequest httpRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_category);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initUi();
        httpRequest = new HttpRequest();
        loadListCategory();
        imgBack.setOnClickListener(v -> finish());
    }

    private void loadListCategory() {
        fetchCategoryList();
    }

    private void fetchCategoryList() {
        httpRequest.callAPI().getListCategory().enqueue(getListCategory);
    }

    private void initUi() {
        imgBack = findViewById(R.id.imgBack);
        rcvCatogty = findViewById(R.id.rcvCategory);
        adapter = new CategoryAdapter(this, list, new CategoryHandle() {
            @Override
            public void onEdit(int position) {
                showUpdateDialog(list.get(position));
            }

            @Override
            public void onDelete(int position) {
                showDeleteDialog(list.get(position));
            }
        });

        rcvCatogty.setLayoutManager(new LinearLayoutManager(this));
        rcvCatogty.setAdapter(adapter);
    }

    private void showUpdateDialog(Category category) {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_category, null);
        AlertDialog builder = new AlertDialog.Builder(this)
                .setView(view)
                .create();
        TextInputEditText edtCategoryName = view.findViewById(R.id.edtCategoryName);
        MaterialButton btnUpdateCategory = view.findViewById(R.id.btnSaveCategory);
        MaterialButton btnCancelCategory = view.findViewById(R.id.btnCancelCategory);
        TextView tvCategoryFormTitle = view.findViewById(R.id.tvCategoryFormTitle);
        tvCategoryFormTitle.setText("Cập nhật danh mục");
        btnUpdateCategory.setText("Cập nhật");
        edtCategoryName.setText(category.getName());

        btnCancelCategory.setOnClickListener(v -> builder.dismiss());
        btnUpdateCategory.setOnClickListener(v -> {
            String categoryName = edtCategoryName.getText().toString().trim();
            if (categoryName.isEmpty()) {
                Toast.makeText(this, "Tên danh mục không được để trống", Toast.LENGTH_SHORT).show();
                return;
            }
            String categoryId = category.getId();
            if (categoryId == null || categoryId.trim().isEmpty()) {
                Toast.makeText(this, "Không tìm thấy ID danh mục để cập nhật", Toast.LENGTH_LONG).show();
                return;
            }
            Category updateCategory = new Category(categoryName);
            httpRequest.callAPI().updateCategoryById(categoryId, updateCategory).enqueue(updateCategoryAPI);
            builder.dismiss();
        });

        builder.show();
    }

    private void showDeleteDialog(Category category) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Xóa danh mục " + category.getName());
        builder.setMessage("Bạn có chắc chắn muốn xóa danh mục này không?");
        builder.setPositiveButton("Đồng ý", (dialog, which) -> {
            String categoryId = category.getId();
            if (categoryId == null || categoryId.trim().isEmpty()) {
                Toast.makeText(this, "Không tìm thấy ID danh mục để xóa", Toast.LENGTH_LONG).show();
                return;
            }
            httpRequest.callAPI().deleteCategoryById(categoryId).enqueue(deleteCategoryAPI);
        });
        builder.setNegativeButton("Không", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    Callback<Response<ArrayList<Category>>> getListCategory =
            new Callback<Response<ArrayList<Category>>>() {
                @Override
                public void onResponse(Call<Response<ArrayList<Category>>> call, retrofit2.Response<Response<ArrayList<Category>>> response) {
                    if (!response.isSuccessful() || response.body() == null) {
                        Toast.makeText(CategoryActivity.this, "Không lấy được danh mục: HTTP " + response.code(), Toast.LENGTH_LONG).show();
                        Log.e(">>> GetListCategory", "HTTP error: " + response.code());
                        return;
                    }
                    if (response.body().getStatus() == 200) {
                        adapter.setData(response.body().getData());
                    } else {
                        Toast.makeText(CategoryActivity.this, response.body().getMessage(), Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<Response<ArrayList<Category>>> call, Throwable throwable) {
                    Log.d(">>> GetListCategory", "onFailure: " + throwable.getMessage());
                    Toast.makeText(CategoryActivity.this, "Lỗi API: " + throwable.getMessage(), Toast.LENGTH_LONG).show();
                }
            };
    Callback<Response<Category>> updateCategoryAPI =
            new Callback<Response<Category>>() {
                @Override
                public void onResponse(Call<Response<Category>> call, retrofit2.Response<Response<Category>> response) {
                    if (!response.isSuccessful() || response.body() == null) {
                        Toast.makeText(CategoryActivity.this, "Cập nhật thất bại: HTTP " + response.code(), Toast.LENGTH_LONG).show();
                        return;
                    }
                    if (response.body().getStatus() == 200) {
                        Toast.makeText(CategoryActivity.this, "Cập nhật thành công", Toast.LENGTH_LONG).show();
                        fetchCategoryList();
                    } else {
                        Toast.makeText(CategoryActivity.this, "Cập nhật thất bại", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<Response<Category>> call, Throwable throwable) {
                    Log.e("UpdateCategory", "Lỗi API: "+ throwable.getMessage());
                    Toast.makeText(CategoryActivity.this, "Lỗi API: " + throwable.getMessage(), Toast.LENGTH_LONG).show();

                }
            };
    Callback<Response<Category>> deleteCategoryAPI =
            new Callback<Response<Category>>() {
                @Override
                public void onResponse(Call<Response<Category>> call, retrofit2.Response<Response<Category>> response) {
                    if (!response.isSuccessful() || response.body() == null) {
                        Toast.makeText(CategoryActivity.this, "Xóa thất bại: HTTP " + response.code(), Toast.LENGTH_LONG).show();
                        return;
                    }
                    if (response.body().getStatus() == 200) {
                        Toast.makeText(CategoryActivity.this, "Xóa danh mục thành công", Toast.LENGTH_LONG).show();
                        fetchCategoryList();
                    } else {
                        Toast.makeText(CategoryActivity.this, "Xóa danh mục thất bại", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<Response<Category>> call, Throwable throwable) {
                    Toast.makeText(CategoryActivity.this, "Lỗi API: " + throwable.getMessage(), Toast.LENGTH_LONG).show();
                }
            };

    @Override
    protected void onResume() {
        super.onResume();
        if (httpRequest != null) {
            fetchCategoryList();
        }
    }
}
