package com.example.asm_and103_ph63816.model;

public class Product {
    private String _id;
    private String name; // tên sản phẩm
    private double volume; // khối lượng
    private double price; // giá tiền
    private int quantity; // số lượng
    private String description; // đánh giá
    private double star; // số sao đánh giá
    private String image;

    public Product() {
    }

    public Product(String name, double volume, double price, int quantity, String description, double star) {
        this.name = name;
        this.volume = volume;
        this.price = price;
        this.quantity = quantity;
        this.description = description;
        this.star = star;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getVolume() {
        return volume;
    }

    public void setVolume(double volume) {
        this.volume = volume;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getStar() {
        return star;
    }

    public void setStar(double star) {
        this.star = star;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
