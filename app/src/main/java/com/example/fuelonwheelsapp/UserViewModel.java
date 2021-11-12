package com.example.fuelonwheelsapp;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

public class UserViewModel extends ViewModel {

    ProfileRepository repository = new ProfileRepository();
    public LiveData<Boolean> dataSavedSuccessfully;

    LiveData<User> getResponseAsUserProfile(){
        //Log.d("UserViewModel",repository.getResponseFromDatabase().getValue().getEmail());
        return repository.getResponseFromDatabase();
    }
    public void saveUserDetailsAsProfile(User user){
        dataSavedSuccessfully = repository.saveUserData(user);
    }

}
