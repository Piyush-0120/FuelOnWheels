package com.example.fuelonwheelsapp;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Order {
    private String userId;
    private String orderId;
    private String dateTime;
    private String userLocation;
    private String fuelLocation;
    private String petrolPrice;
    private String dieselPrice;
    private String nDiesel;
    private String nPetrol;
    private String deliveryCharge;
    private String paymentMethod;
    private String totalAmount;

    Order(){
        // default constructor
    }
    Order(String orderId, String dateTime, String userCoordinates, String fuelCoordinates, String userLocation, String fuelLocation, String petrolPrice, String dieselPrice, String nDiesel, String nPetrol, String deliveryCharge, String paymentMethod, String totalAmount){
        this.orderId = orderId;
        this.dateTime = dateTime;
        this.userLocation = userLocation;
        this.fuelLocation = fuelLocation;
        this.petrolPrice = petrolPrice;
        this.dieselPrice = dieselPrice;
        this.nDiesel = nDiesel;
        this.nPetrol = nPetrol;
        this.deliveryCharge = deliveryCharge;
        this.paymentMethod = paymentMethod;
        this.totalAmount = totalAmount;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getUserLocation() {
        return userLocation;
    }

    public void setUserLocation(String userLocation) {
        this.userLocation = userLocation;
    }

    public String getFuelLocation() {
        return fuelLocation;
    }

    public void setFuelLocation(String fuelLocation) {
        this.fuelLocation = fuelLocation;
    }

    public String getPetrolPrice() {
        return petrolPrice;
    }

    public void setPetrolPrice(String petrolPrice) {
        this.petrolPrice = petrolPrice;
    }

    public String getDieselPrice() {
        return dieselPrice;
    }

    public void setDieselPrice(String dieselPrice) {
        this.dieselPrice = dieselPrice;
    }

    public String getnDiesel() {
        return nDiesel;
    }

    public void setnDiesel(String nDiesel) {
        this.nDiesel = nDiesel;
    }

    public String getnPetrol() {
        return nPetrol;
    }

    public void setnPetrol(String nPetrol) {
        this.nPetrol = nPetrol;
    }

    public String getDeliveryCharge() {
        return deliveryCharge;
    }

    public void setDeliveryCharge(String deliveryCharge) {
        this.deliveryCharge = deliveryCharge;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
