package com.example.fuelonwheelsapp.repositories;


import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.fuelonwheelsapp.dashboard.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileRepository {
    private final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    private final DatabaseReference rootReference = FirebaseDatabase.getInstance().getReference();
    private final DatabaseReference profileReference = rootReference.child("users").child(firebaseUser.getUid());
    private User user;

    public ProfileRepository(){
        Log.d("ProfileRepository",firebaseUser.getUid());
        //Log.d("ProfileRepository",profileReference.toString());
    }


    public MutableLiveData<User> getResponseFromDatabase(){
        MutableLiveData<User> mutableLiveData = new MutableLiveData<>();
        profileReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    user = snapshot.getValue(User.class);
                    //Log.d("ProfileRepositoryMutble",mutableLiveData.getValue().getPhoneNo()+","+user.getEmail());
                }
                mutableLiveData.setValue(user);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        return mutableLiveData;
    }

    public LiveData<Boolean> saveUserData(User user){
        MutableLiveData<Boolean> booleanMutableLiveData = new MutableLiveData<>();
        String userId = firebaseUser.getUid();
        profileReference.setValue(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        booleanMutableLiveData.setValue(true);
                        Log.d("ProfileRepository","Saved successfully");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        booleanMutableLiveData.setValue(false);
                        Log.d("ProfileRepository",""+e.getMessage());
                    }
                });
        return booleanMutableLiveData;
    }



}
