package com.example.fuelonwheelsapp;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class FOWViewModel extends ViewModel {
    private MutableLiveData<Integer> petrolQuantInt = new MutableLiveData<Integer>();
    private MutableLiveData<Integer> dieselQuantInt = new MutableLiveData<Integer>();

    public void setPetrolQuantInt(Integer petrolQuantInt) {
        this.petrolQuantInt.setValue(petrolQuantInt);
    }

    public Integer getPetrolQuantInt() {
        return petrolQuantInt.getValue();
    }

    public void setDieselQuantInt(Integer dieselQuantInt) {
        this.dieselQuantInt.setValue(dieselQuantInt);
    }

    public Integer getDieselQuantInt() {
        return dieselQuantInt.getValue();
    }
}
