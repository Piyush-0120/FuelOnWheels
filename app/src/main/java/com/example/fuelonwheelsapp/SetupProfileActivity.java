package com.example.fuelonwheelsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.fuelonwheelsapp.databinding.ActivitySetupProfileBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SetupProfileActivity extends AppCompatActivity {

    //view Binding
    private ActivitySetupProfileBinding binding;
    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySetupProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //setContentView(R.layout.activity_setup_profile);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        databaseReference = FirebaseDatabase.getInstance().getReference();

        //init progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);

        if(firebaseUser!=null)
        {
            progressDialog.setMessage("Signing In");
            progressDialog.show();

            if(isProfileComplete()) {
                progressDialog.dismiss();
                Intent intent = new Intent(SetupProfileActivity.this, DashboardActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
            else{
                progressDialog.dismiss();
                setUpViews();
                binding.setupBtnNext.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        checkValidations(); // save and send to Dashboard
                    }
                });
            }

        }

    }

    private void checkValidations() {
        String fullName = binding.setupEtFname.getText().toString().trim();
        String phone = binding.setupEtPhone.getText().toString().trim();
        String email = binding.setupEtEmail.getText().toString().trim();
        //TODO: Do more validations with edit text
        if(TextUtils.isEmpty(fullName)){
            binding.setupEtFname.setError("Field cannot be empty");
            binding.setupEtFname.requestFocus();
            return;
        }
        if(TextUtils.isEmpty(phone)){
            binding.setupEtPhone.setError("Field cannot be empty");
            binding.setupEtPhone.requestFocus();
            return;
        }
        if(TextUtils.isEmpty(email)){
            binding.setupEtEmail.setError("Field cannot be empty");
            binding.setupEtEmail.requestFocus();
            return;
        }
        saveUserDetails(fullName,email,phone);
    }

    private void saveUserDetails(String fullName, String email, String phone) {

        progressDialog.setMessage("Saving details");
        progressDialog.show();
        // saving to users node
        String userId = firebaseUser.getUid();
        User user = new User(fullName,email,phone);
        databaseReference.child("users")
                .child(userId)
                .setValue(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressDialog.dismiss();
                        //send to Dashboard Activity
                        Intent intent = new Intent(SetupProfileActivity.this, DashboardActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(SetupProfileActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
        progressDialog.dismiss();
    }


    private void setUpViews() {
        if(firebaseUser.getPhoneNumber()!=null && firebaseUser.getPhoneNumber().length()!=0){
            binding.setupEtPhone.setText(firebaseUser.getPhoneNumber());
            binding.setupEtPhone.setEnabled(false);
        }
        if(firebaseUser.getEmail()!=null && firebaseUser.getEmail().length()!=0){
            binding.setupEtEmail.setText(firebaseUser.getEmail());
            binding.setupEtFname.setText(firebaseUser.getDisplayName());
            binding.setupEtEmail.setEnabled(false);
        }
    }

    private boolean isProfileComplete() {
        final Boolean[] flag = {false};
        ValueEventListener userDetailListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if(user != null && user.getFullName()!=null && user.getEmail()!=null && user.getPhoneNo()!=null){
                    flag[0] = true;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(SetupProfileActivity.this, ""+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                Log.w("ActivitySetupProfile", "loadPost:onCancelled", databaseError.toException());
            }
        };
        databaseReference.child("users").child(firebaseUser.getUid()).addValueEventListener(userDetailListener);
        return flag[0];
    }
}