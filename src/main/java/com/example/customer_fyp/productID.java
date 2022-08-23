package com.example.customer_fyp;

import com.google.firebase.firestore.Exclude;

public class productID {

    @Exclude
    private String id;
    @Exclude private String imgID;

    private String Model, Brand;
    private int Stock;
    private double Price;
    private String Images;

    public productID(String Model, String Brand, int Stock, double Price, String Images) {

        if (Model.trim().equals("")||Brand.trim().equals("")) {
            Model = "No model name";
            Brand = "No brands name";
        }

        this.Model = Model;
        this.Brand = Brand;
        this.Stock = Stock;
        this.Price = Price;
        this.Images = Images;
    }

    public productID() {

    }

    public String getModel() {
        return Model;
    }

    public void setModel(String model) {
        Model = model;
    }

    public String getBrand() {
        return Brand;
    }

    public void setBrand(String brand) {
        Brand = brand;
    }

    public int getStock() {
        return Stock;
    }

    public void setStock(int stock) {
        Stock = stock;
    }

    public double getPrice() {
        return Price;
    }

    public void setPrice(double price) {
        Price = price;
    }

    public String getImages() {
        return Images;
    }

    public void setImages(String images) {
        Images = images;
    }

    @Exclude
    public String getImgID() {
        return imgID;
    }

    @Exclude
    public void setImgID(String imgID) {
        this.imgID = imgID;
    }
}
