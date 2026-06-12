package com.example.asm_and103_ph63816.services;

import com.example.asm_and103_ph63816.model.Cart;
import com.example.asm_and103_ph63816.model.Order;
import com.example.asm_and103_ph63816.model.Product;
import com.example.asm_and103_ph63816.model.Response;

import java.util.ArrayList;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.Part;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiServices {
    @GET("productRouter/get-list-product")
    Call<Response<ArrayList<Product>>> getListProduct();

    @GET("categoryRouter/get-list-category")
    Call<Response<ArrayList<Product>>> getListCategory();

    @PUT("productRouter/update-product-by-id/{id}")
    Call<Response<Product>> updateProductById(@Path("id") String id, @Body Product product);

    @DELETE("productRouter/delete-product-by-id/{id}")
    Call<Response<Product>> deleteProductById(@Path("id") String id);

    @POST("productRouter/search-product")
    Call<Response<ArrayList<Product>>> searchProduct(@Query("name") String name);

    @POST("productRouter/add-product")
    Call<Response<Product>> addProduct(@Body Product product);

    @Multipart
    @POST("productRouter/add-product")
    Call<Response<Product>> addProduct(
            @Part("name") RequestBody name,
            @Part("volume") RequestBody volume,
            @Part("price") RequestBody price,
            @Part("quantity") RequestBody quantity,
            @Part("description") RequestBody description,
            @Part("star") RequestBody star,
            @Part MultipartBody.Part image
    );

    // Cart Endpoints
    @POST("cartRouter/add-to-cart")
    Call<Response<Cart>> addToCart(@Body Cart cart);

    @GET("cartRouter/get-cart/{userId}")
    Call<Response<ArrayList<Cart>>> getCart(@Path("userId") String userId);

    @PUT("cartRouter/update-cart-quantity/{id}")
    Call<Response<Cart>> updateCartQuantity(@Path("id") String id, @Body Cart cart);

    @DELETE("cartRouter/delete-cart-item/{id}")
    Call<Response<Cart>> deleteCartItem(@Path("id") String id);

    // Order Endpoints
    @POST("orderRouter/add-order")
    Call<Response<Order>> addOrder(@Body Order order);

    @GET("orderRouter/get-orders/{userId}")
    Call<Response<ArrayList<Order>>> getOrders(@Path("userId") String userId);
}
