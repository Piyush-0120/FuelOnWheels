package com.example.fuelonwheelsapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class DashboardActivity extends AppCompatActivity {

//    private FirebaseAuth firebaseAuth;

    int homeState;
    Button nextButton;
    TableLayout deliveryDetails;
    TextView eta;
    private FOWViewModel viewModel;
    TextView address;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        homeState = 0;
        nextButton = findViewById(R.id.next_button);
        deliveryDetails = findViewById(R.id.delivery_details);
        eta = findViewById(R.id.eta);
        viewModel = new ViewModelProvider(this).get(FOWViewModel.class);
        address = findViewById(R.id.delivery_address);
        nextButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        switch (homeState) {
                            case 0:
                                getSupportFragmentManager().beginTransaction()
                                        .setReorderingAllowed(true)
                                        .add(R.id.fragmentContainerView2, OrderFragment.class, null)
                                        .addToBackStack("order_setup")
                                        .commit();
                                homeState = 1;
                                break;
                            case 1:
                                if(viewModel.getPetrolQuantInt().intValue()==0 && viewModel.getDieselQuantInt().intValue()==0)
                                    break;
                                getSupportFragmentManager().beginTransaction()
                                        .setReorderingAllowed(true)
                                        .add(R.id.fragmentContainerView3, PaymentFragment.class, null)
                                        .addToBackStack("payment")
                                        .commit();
                                homeState = 2;
                                break;
                            case 2:
                                address.setText(viewModel.getOrderLocation());
                                nextButton.setVisibility(View.GONE);
                                getSupportFragmentManager().beginTransaction()
                                        .setReorderingAllowed(true)
                                        .add(R.id.fragmentContainerView4, ThankYouFragment.class, null)
                                        .addToBackStack("thank_you")
                                        .commit();

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        nextButton.setVisibility(View.VISIBLE);
                                        getSupportFragmentManager().popBackStack("order_setup", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                                    }
                                }, 3000);

                                homeState = 0;
                                nextButton.setEnabled(false);
                                deliveryDetails.setVisibility(View.VISIBLE);

                                new CountDownTimer(10000, 1000) {
                                    @Override
                                    public void onTick(long l) {
                                        updateTimer((int) l / 1000);
                                    }
                                    @Override
                                    public void onFinish() {
                                        eta.setText("Package Delivered");
                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                deliveryDetails.setVisibility(View.GONE);
                                                nextButton.setEnabled(true);
                                            }
                                        }, 2000);
                                    }
                                }.start();
                        }
                        updateNxtBtn();
                    }
                }
        );

        //Bottom navigation
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        NavHostFragment navHostFragment = (NavHostFragment)getSupportFragmentManager().findFragmentById(R.id.fragmentContainerView);
        if(navHostFragment!=null){
            NavController navController = navHostFragment.getNavController();
            navController.addOnDestinationChangedListener(
                new NavController.OnDestinationChangedListener() {
                    @Override
                    public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {
                        if(destination.getId() == R.id.homeFragment) {
                            nextButton.setVisibility(View.VISIBLE);
                            if(homeState==0 && !nextButton.isEnabled())
                                deliveryDetails.setVisibility(View.VISIBLE);
                        }
                        else {
                            nextButton.setVisibility(View.GONE);
                            deliveryDetails.setVisibility(View.GONE);
                        }
                        getSupportFragmentManager().popBackStack("order_setup", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                        homeState = 0;
                        updateNxtBtn();
                    }
                });
            NavigationUI.setupWithNavController(bottomNavigationView,navController);
        }

        /*
        else {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerView, new HomeFragment()).commit();

            bottomNavigationView.setSelectedItemId(R.id.homeFragment);
            bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment fragment = null;
                    switch (item.getItemId()) {
                        case R.id.homeFragment:
                            fragment = new HomeFragment();
                            break;
                        case R.id.profileFragment:
                            fragment = new ProfileFragment();
                            break;
                        case R.id.settingsFragment:
                            fragment = new SettingsFragment();
                            break;
                    }
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerView, fragment).commit();
                    return true;
                }
            });
        }
         */
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        homeState = Math.max(0, homeState-1);
        updateNxtBtn();
    }

    public void updateTimer(int secondsLeft) {
        int minutes = secondsLeft / 60;
        int seconds = secondsLeft - (minutes * 60);

        String secondString = String.valueOf(seconds);

        if(seconds < 10)
            secondString = "0" + secondString;

        eta.setText(String.valueOf(minutes) + ":" + secondString);
    }

    public void updateNxtBtn() {
        switch(homeState) {
            case 0:
                nextButton.setText("Order");
                break;
            case 1:
                nextButton.setText("Next");
                break;
            case 2:
                nextButton.setText("Place Order");
        }
    }
}