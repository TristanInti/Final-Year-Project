package com.example.customer_fyp;

import com.google.firebase.firestore.Exclude;

public class Order {

    private String itemID, model, brand, status, quantity, price, Images, email;

    public Order(String itemID, String model, String brand, String price, String quantity, String images, String email) {
        this.itemID = itemID;
        this.model = model;
        this.brand = brand;
        this.price = price;
        this.quantity = quantity;
        Images = images;
        this.email = email;
    }

    public Order() {

    }

    public String getItemID() {
        return itemID;
    }

    public void setItemID(String itemID) {
        this.itemID = itemID;
    }

    public String getOrderModel() {
        return model;
    }

    public void setOrderModel(String model) {
        this.model = model;
    }

    public String getOrderBrand() {
        return brand;
    }

    public void setOrderBrand(String brand) {
        this.brand = brand;
    }

    @Exclude
    public String getStatus() {
        return status;
    }

    @Exclude
    public void setStatus(String status) {
        this.status = status;
    }

    public String getOrderPrice() {
        return price;
    }

    public void setOrderPrice(String price) {
        this.price = price;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getImages() {
        return Images;
    }

    public void setImages(String images) {
        Images = images;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
