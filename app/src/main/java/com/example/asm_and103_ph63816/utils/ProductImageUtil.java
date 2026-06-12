package com.example.asm_and103_ph63816.utils;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.asm_and103_ph63816.R;

public class ProductImageUtil {
    private static final String BASE_IMAGE_URL = "http://10.0.2.2:3000/";

    private ProductImageUtil() {
    }

    public static void loadImage(Context context, String image, ImageView imageView) {
        Glide.with(context)
                .load(getImageUrl(image))
                .placeholder(R.drawable.bg_warning_pill)
                .error(R.drawable.bg_warning_pill)
                .into(imageView);
    }

    public static String getImageUrl(String image) {
        if (image == null || image.trim().isEmpty()) {
            return null;
        }

        image = image.replace("\\", "/");
        if (image.startsWith("content://") || image.startsWith("file://")) {
            return image;
        }
        if (image.startsWith("http://") || image.startsWith("https://")) {
            return image
                    .replace("http://localhost:3000/", BASE_IMAGE_URL)
                    .replace("http://127.0.0.1:3000/", BASE_IMAGE_URL)
                    .replace("/upload/", "/uploads/");
        }
        if (image.startsWith("/")) {
            image = image.substring(1);
        }
        return (BASE_IMAGE_URL + image).replace("/upload/", "/uploads/");
    }
}
