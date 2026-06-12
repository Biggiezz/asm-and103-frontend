package com.example.asm_and103_ph63816.model;

import java.util.ArrayList;

public class Order {
    private String _id;
    private String userId;
    private ArrayList<Cart> items;
    private double totalAmount;
    private String status; // e.g., "Pending", "Confirmed", "Shipped", "Delivered"
    private String orderDate;

    public Order() {
    }

    public Order(String userId, ArrayList<Cart> items, double totalAmount, String status, String orderDate) {
        this.userId = userId;
        this.items = items;
        this.totalAmount = totalAmount;
        this.status = status;
        this.orderDate = orderDate;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public ArrayList<Cart> getItems() {
        return items;
    }

    public void setItems(ArrayList<Cart> items) {
        this.items = items;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }
}
