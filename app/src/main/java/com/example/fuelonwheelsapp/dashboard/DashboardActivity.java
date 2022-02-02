package com.example.fuelonwheelsapp.dashboard;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fuelonwheelsapp.viewModels.FOWViewModel;
import com.example.fuelonwheelsapp.R;
import com.example.fuelonwheelsapp.interfaces.OrderCallback;
import com.example.fuelonwheelsapp.profile.orders.Order;
import com.google.android.material.bottomnavigation.BottomNavigationView;


import java.util.Locale;

public class DashboardActivity extends AppCompatActivity {
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;
    private static final int PERMISSION_REQUEST_ACCESS_COARSE_LOCATION = 2;
    private static final int PERMISSION_REQUEST_ACCESS_FINE_LOCATION = 3;
    private static final int PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE = 4;
    private static final String TAG = "DashboardActivity";

    private int homeState;
    private Button nextButton;
    private TableLayout deliveryDetails;
    private TextView eta;
    private FOWViewModel viewModel;
    private ProgressDialog progressDialog;
    private View mLayout;
    private TextView address;
    private TextView fueladdress;
    private TextView dashboard_tv_distance;
    private TextView delivering_to;
    private String fullName;
    private TextView deliveryCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        Intent intent = getIntent();
        fullName = intent.getStringExtra("fullName");
        homeState = 0;
        nextButton = findViewById(R.id.next_button);
        deliveryDetails = findViewById(R.id.delivery_details);
        delivering_to = findViewById(R.id.delivering_to);
        eta = findViewById(R.id.eta);
        mLayout=findViewById(R.id.dashboard_main_layout);
        dashboard_tv_distance=findViewById(R.id.dashboard_table_tv_distance);
        deliveryCode=findViewById(R.id.delivery_code);
        //init progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("Placing your Order");

        viewModel = new ViewModelProvider(this).get(FOWViewModel.class);
        address = findViewById(R.id.delivery_address);
        fueladdress = findViewById(R.id.fuel_address);
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
                                if(viewModel.getPetrolQuantInt() ==0 && viewModel.getDieselQuantInt() ==0)
                                    break;
                                getSupportFragmentManager().beginTransaction()
                                        .setReorderingAllowed(true)
                                        .add(R.id.fragmentContainerView3, PaymentFragment.class, null)
                                        .addToBackStack("payment")
                                        .commit();
                                homeState = 2;
                                // TODO : Wait for getting nearest petrol pump
                                // store latitude and longitude
                                // find POI
                                // get the nearest POI
                                // store it in FOWViewModel
                                // TODO : show route and petrol pump address
                                //viewModel.setOrderPlaced(true);
                                break;
                            case 2:
                                // TODO : Improve the Initialization
                                Order order = new Order();
                                order.setnDiesel(viewModel.getDieselQuantInt().toString());
                                order.setnPetrol(viewModel.getPetrolQuantInt().toString());
                                order.setDeliveryCharge("50");
                                order.setDieselPrice("94");
                                order.setPetrolPrice("100");
                                order.setTotalAmount(String.valueOf((viewModel.getDieselQuantInt()*94+viewModel.getPetrolQuantInt()*100+50)));
                                order.setPaymentMethod("COD");
                                order.setUserLocation(viewModel.getOrderLocation());
                                progressDialog.show();

                                //findNearestFuel
                                viewModel.getResponseUsingCallback(getApplicationContext(),viewModel.getUserGeoPoint(),order, new OrderCallback() {
                                    @Override
                                    public void onResponse(Response response) {
                                        progressDialog.dismiss();
                                        if(response.exception==null && response.address!=null){
                                            fueladdress.setText(response.address);
                                            viewModel.setFuelCoordinates(response.fuelCoordinates);
                                            viewModel.setFuelLocation(response.address);
                                            viewModel.setRoad(response.road);
                                            address.setText(viewModel.getOrderLocation());
                                            delivering_to.setText(fullName);
                                            deliveryCode.setText(response.orderId);
                                            if(response.road!=null){
                                                Log.d(TAG,"road is NOT null in "+TAG);
                                                viewModel.setBuildRoad(true);
                                                viewModel.setDelivered(false);
                                                dashboard_tv_distance.setText(String.format(Locale.getDefault(),
                                                        "%.2f km (%.2f minutes)",response.road.mLength,response.road.mDuration/60));
                                            }
                                            else{
                                                Log.d(TAG,"road is null in "+TAG);
                                            }
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
                                            Log.d(TAG,"executed before Counter");
                                            new CountDownTimer(30000, 1000) {
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
                                        else{
                                            Log.d(TAG,"fuel address is null");
                                            String exception="Sorry! We encountered a problem.";
                                            if(response.exception!=null)
                                                exception=response.exception;
                                            Toast.makeText(DashboardActivity.this, exception, Toast.LENGTH_SHORT).show();
                                            new Handler().postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    nextButton.setVisibility(View.VISIBLE);
                                                    getSupportFragmentManager().popBackStack("order_setup", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                                                }
                                            }, 1000);
                                            homeState = 0;
                                        }
                                    }
                                });
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

                        if(destination.getId()==R.id.orderListFragment || destination.getId()==R.id.orderDetailFragment
                        || destination.getId()==R.id.accountFragment || destination.getId()==R.id.accountEditFragment
                        || destination.getId()==R.id.aboutUsFragment || destination.getId()==R.id.paymentSetUpFragment){
                            bottomNavigationView.setVisibility(View.GONE);
                        }
                        else{
                            bottomNavigationView.setVisibility(View.VISIBLE);
                        }

                    }
                });
            NavigationUI.setupWithNavController(bottomNavigationView,navController);
        }
    }
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
//                                           @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        switch (requestCode) {
//            case PERMISSION_REQUEST_ACCESS_FINE_LOCATION:
//                // If request is cancelled, the result arrays are empty.
//                if (grantResults.length > 0 &&
//                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    // Permission is granted. Continue the action or workflow
//                    // in your app.
//                }  else {
//                    // Explain to the user that the feature is unavailable because
//                    // the features requires a permission that the user has denied.
//                    // At the same time, respect the user's decision. Don't link to
//                    // system settings in an effort to convince the user to change
//                    // their decision.
//                    Snackbar.make(mLayout, "Location permission denied.",
//                            Snackbar.LENGTH_SHORT)
//                            .show();
//                }
//            case PERMISSION_REQUEST_ACCESS_COARSE_LOCATION:
//                // If request is cancelled, the result arrays are empty.
//                if (grantResults.length > 0 &&
//                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    // Permission is granted. Continue the action or workflow
//                    // in your app.
//                }  else {
//                    // Explain to the user that the feature is unavailable because
//                    // the features requires a permission that the user has denied.
//                    // At the same time, respect the user's decision. Don't link to
//                    // system settings in an effort to convince the user to change
//                    // their decision.
//                    Snackbar.make(mLayout, "Location permission denied.",
//                            Snackbar.LENGTH_SHORT)
//                            .show();
//                }
//            case PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE:
//                // If request is cancelled, the result arrays are empty.
//                if (grantResults.length > 0 &&
//                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    // Permission is granted. Continue the action or workflow
//                    // in your app.
//                }  else {
//                    // Explain to the user that the feature is unavailable because
//                    // the features requires a permission that the user has denied.
//                    // At the same time, respect the user's decision. Don't link to
//                    // system settings in an effort to convince the user to change
//                    // their decision.
//                    Snackbar.make(mLayout, "Permission write to external storage denied.",
//                            Snackbar.LENGTH_SHORT)
//                            .show();
//                    Log.d("DashboardActivity.java","in else of External storage case");
//                }
//        }
//        // Other 'case' lines to check for other
//        // permissions this app might request.
//    }

    /*
    private void requestPermissions() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // Display a SnackBar with cda button to request the missing permission.
            Snackbar.make(mLayout, "Location permission required",
                    Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Request the permission
                    ActivityCompat.requestPermissions(DashboardActivity.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            PERMISSION_REQUEST_ACCESS_FINE_LOCATION);
                }
            }).show();

        } else {
            Snackbar.make(mLayout, "Location Unavailable", Snackbar.LENGTH_SHORT).show();
            // Request the permission. The result will be received in onRequestPermissionResult().
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_ACCESS_COARSE_LOCATION);
        }
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)) {
            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // Display a SnackBar with cda button to request the missing permission.
            Snackbar.make(mLayout, "Location permission required",
                    Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Request the permission
                    ActivityCompat.requestPermissions(DashboardActivity.this,
                            new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                            PERMISSION_REQUEST_ACCESS_COARSE_LOCATION);
                }
            }).show();

        } else {
            Snackbar.make(mLayout, "Location Unavailable", Snackbar.LENGTH_SHORT).show();
            // Request the permission. The result will be received in onRequestPermissionResult().
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_ACCESS_COARSE_LOCATION);
        }

        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // Display a SnackBar with cda button to request the missing permission.
            Snackbar.make(mLayout, "Write to external storage permission required",
                    Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Request the permission
                    ActivityCompat.requestPermissions(DashboardActivity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE);
                }
            }).show();

        } else {
            Snackbar.make(mLayout, "Write to external storage unavailable", Snackbar.LENGTH_SHORT).show();
            // Request the permission. The result will be received in onRequestPermissionResult().
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE);
        }
    }
     */


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
        if(seconds==0){
            viewModel.setDelivered(true);
            viewModel.setBuildRoad(false);
            viewModel.setFuelCoordinates(null);
            viewModel.setFuelLocation(null);
            viewModel.setRoad(null);
        }
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