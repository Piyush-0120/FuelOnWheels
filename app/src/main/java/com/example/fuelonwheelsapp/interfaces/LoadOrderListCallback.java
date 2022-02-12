package com.example.fuelonwheelsapp.interfaces;

import com.example.fuelonwheelsapp.profile.orders.Order;

import java.util.ArrayList;

public interface LoadOrderListCallback{
    public void onResponse(ArrayList<Order> orders, String exception);
}
