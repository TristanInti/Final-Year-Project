package com.example.customer_fyp;

public class History {

    private String itemID, model, brand, email;
    private String quantity;
    private String price;
    private String Images;

    public History(String itemID, String model, String brand, String quantity, String price, String images, String email) {
        this.itemID = itemID;
        this.model = model;
        this.brand = brand;
        this.quantity = quantity;
        this.price = price;
        this.email = email;
        Images = images;
    }

    History() {

    }

    public String getItemID() {
        return itemID;
    }

    public void setItemID(String itemID) {
        this.itemID = itemID;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImages() {
        return Images;
    }

    public void setImages(String images) {
        Images = images;
    }
}
