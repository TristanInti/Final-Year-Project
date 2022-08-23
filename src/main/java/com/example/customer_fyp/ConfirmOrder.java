package com.example.customer_fyp;

public class ConfirmOrder {

    private String customer, orderID;
    private int itemQuantity;
    private String totalItemQuantity;
    private String subtotal;

    public ConfirmOrder(String customer, int itemQuantity, String subtotal, String orderID, String totalItemQuantity) {
        //String customer, String orderID, String itemID, String itemModel, String itemBrand, int itemQuantity, double subtotal, double itemPrice
        /*this.customer = customer;
        this.orderID = orderID;
        this.itemID = itemID;
        this.itemModel = itemModel;
        this.itemBrand = itemBrand;
        this.itemQuantity = itemQuantity;
        this.subtotal = subtotal;
        this.itemPrice = itemPrice;*/

        this.orderID = orderID;
        this.customer = customer;
        this.subtotal = subtotal;
        this.itemQuantity =itemQuantity;
        this.totalItemQuantity = totalItemQuantity;
    }

    public ConfirmOrder() {

    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public int getItemQuantity() {
        return itemQuantity;
    }

    public void setItemQuantity(int itemQuantity) {
        this.itemQuantity = itemQuantity;
    }

    public String getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(String subtotal) {
        this.subtotal = subtotal;
    }

    public String getTotalItemQuantity() {
        return totalItemQuantity;
    }

    public void setTotalItemQuantity(String totalItemQuantity) {
        this.totalItemQuantity = totalItemQuantity;
    }
}
