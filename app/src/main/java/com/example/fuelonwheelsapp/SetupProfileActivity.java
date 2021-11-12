package com.example.fuelonwheelsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SetupProfileActivity extends AppCompatActivity {

    //view Binding
    private ActivitySetupProfileBinding binding;
    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;
    private ProgressDialog progressDialog;
    private UserViewModel userViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySetupProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //setContentView(R.layout.activity_setup_profile);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        //databaseReference = FirebaseDatabase.getInstance().getReference();
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        //init progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);

        progressDialog.setMessage("Signing In");
        progressDialog.show();
        //isProfileComplete();
        isProfileCompleteUsingLiveData();

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
        User user = new User(fullName,email,phone);
        saveUserDetails(user);
    }

    private void saveUserDetails(User user) {
        progressDialog.setMessage("Saving details");
        progressDialog.show();
        userViewModel.saveUserDetailsAsProfile(user);
        userViewModel.dataSavedSuccessfully.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(aBoolean)
                {
                    progressDialog.dismiss();
                    gotoDashboardActivity();
                    finish();
                }
                else{
                    progressDialog.dismiss();
                    Toast.makeText(SetupProfileActivity.this, "Some error occurred while saving profile", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void gotoDashboardActivity() {
        Intent intent = new Intent(SetupProfileActivity.this,DashboardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
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
    private void isProfileCompleteUsingLiveData(){
        userViewModel.getResponseAsUserProfile().observe(this, new Observer<User>() {
            @Override
            public void onChanged(User user) {
                progressDialog.dismiss();
                if (user != null && user.getFullName() != null
                        && user.getFullName().length() > 0
                        && user.getEmail() != null
                        && user.getEmail().length() > 0
                        && user.getPhoneNo() != null
                        && user.getPhoneNo().length() > 0) {
                    //Toast.makeText(SetupProfileActivity.this, "" + user.getPhoneNo() + "," + user.getEmail(), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SetupProfileActivity.this, DashboardActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
                else
                {
                    binding.setupContainer.setVisibility(View.VISIBLE);
                    setUpViews();
                    binding.setupBtnNext.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            checkValidations(); // save and send to Dashboard
                        }
                    });
                }
            }
        });

    }
}