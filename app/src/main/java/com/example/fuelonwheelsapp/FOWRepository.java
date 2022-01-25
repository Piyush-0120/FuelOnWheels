package com.example.fuelonwheelsapp;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.osmdroid.bonuspack.location.NominatimPOIProvider;
import org.osmdroid.bonuspack.location.POI;
import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.util.GeoPoint;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

interface OrderCallback{
    public void onResponse(Response response);
}
public class FOWRepository {
    private Context context;
    private String fuelLocation;
    private String exception;
    private GeoPoint userCoordinates;
    private GeoPoint fuelCoordinates;

    private final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    private final DatabaseReference rootReference = FirebaseDatabase.getInstance().getReference();
    private final DatabaseReference profileReference = rootReference.child("users").child(firebaseUser.getUid());
    private final DatabaseReference orderReference = rootReference.child("orderTree");

    FOWRepository(){
    }

    public void getResponseFromUsingCallback(Context context,GeoPoint geoPoint,Order order,OrderCallback orderCallback){
        Response response = new Response();
        FOWRepository.this.context = context;
        FOWRepository.this.userCoordinates=geoPoint;
        if(geoPoint==null) {
            exception = "Location not available";
            response.exception = exception;
            orderCallback.onResponse(response);
        }
        else {
            Log.d("FOWRepository", "getResponseFromUsingCallback: About to enter the FindNearFuelTask");
            new FindNearFuelTask(orderCallback,response,order).execute(geoPoint);
        }
    }

    private class FindNearFuelTask extends AsyncTask<GeoPoint, Integer, ArrayList<POI>> {
        public OrderCallback orderCallback=null;
        public Response response=null;
        public Order order=null;

        public FindNearFuelTask(OrderCallback callback,Response response1,Order order1){
            orderCallback=callback;
            response = response1;
            order = order1;
        }

        @Override
        protected ArrayList<POI> doInBackground(GeoPoint... geoPoints) {
            NominatimPOIProvider poiProvider = new NominatimPOIProvider(context.getPackageName());
            ArrayList<POI> pois = poiProvider.getPOICloseTo(geoPoints[0], "petrol", 50, 0.1);
            double radius = 0.1;
            while (pois.size() == 0 && radius <= 1.0) {
                pois = poiProvider.getPOICloseTo(geoPoints[0], "petrol", 50, radius);
                radius += 0.1;
            }
            // TODO : return the nearest one
            return pois;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            Log.d("FOWRepository", "onProgressUpdate: "+ Arrays.toString(values));
        }

        @Override
        protected void onPostExecute(ArrayList<POI> pois) {
            super.onPostExecute(pois);
            if (pois.size() == 0) {
                FOWRepository.this.exception = "Sorry! Service not available at your location";
            } else {
                POI poi = pois.get(0);
                FOWRepository.this.fuelCoordinates = poi.mLocation;
                FOWRepository.this.fuelLocation = poi.mDescription;
                order.setFuelLocation(poi.mDescription);

                Log.d("FOWRepository", "onPostExecute: Successfully found the nearest POI");
                //orderCallback.onResponse(response);
                buildRoad(userCoordinates, fuelCoordinates,response);
            }
        }

        public void buildRoad(GeoPoint userGeoPoint, GeoPoint fuelGeoPoint,Response response) {
            ArrayList<GeoPoint> waypoints = new ArrayList<>();
            waypoints.add(userGeoPoint);
            waypoints.add(fuelGeoPoint);
            new RouteTask(orderCallback,response,order).execute(waypoints);
        }
    }

    private class RouteTask extends AsyncTask<ArrayList<GeoPoint>, Integer, Road>
    {
        public OrderCallback orderCallback=null;
        public Response response=null;
        public Order order=null;

        public RouteTask(OrderCallback callback, Response response1,Order order1){
            orderCallback=callback;
            response =response1;
            order = order1;
        }
        @SafeVarargs
        @Override
        protected final Road doInBackground(ArrayList<GeoPoint>... arrayLists) {
            RoadManager roadManager = new OSRMRoadManager(context,context.getPackageName());
            return roadManager.getRoad(arrayLists[0]);
        }

        @Override
        protected void onPostExecute(Road road) {
            super.onPostExecute(road);
            Log.d("FOWRepository", "onPostExecute RouteTask: Successfully built the road");
            response.address = fuelLocation;
            response.fuelCoordinates = fuelCoordinates;
            response.road = road;

            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.getDefault());
            String currentDateandTime = sdf.format(new Date());
            String orderId =currentDateandTime+FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber().substring(3);
            orderId = "O"+orderId;
            sdf = new SimpleDateFormat("dd-MM-yyyy|HH:mm:ss:SSS", Locale.getDefault());
            String dateTime = sdf.format(new Date());
            response.orderId = orderId;
            order.setDateTime(dateTime);
            order.setOrderId(orderId);
            order.setUserId(firebaseUser.getUid());

            Log.d("FOWRepository", "onPostExecute: orderId"+orderId);
            // saving Data to DB
            saveOrderData(orderCallback,order,response);
        }
    }
    private void saveOrderData(OrderCallback orderCallback, Order order, Response response) {
        String orderId = order.getOrderId();
        order.setOrderId(null);
        orderReference.child(orderId).setValue(order)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        response.orderSavedToDatabase = true;
                        saveOrderHistoryToProfile(orderCallback,orderId,response);
                        Log.d("FOWRepository","Order Saved to DB successfully");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        response.orderSavedToDatabase = false;
                        response.exception = e.getMessage();
                        orderCallback.onResponse(response);
                        Log.d("FOWRepository",""+e.getMessage());
                    }
                });
    }

    private void saveOrderHistoryToProfile(OrderCallback orderCallback, String orderId, Response response) {
        String userId = firebaseUser.getUid();
        String status = "Confirm";
        profileReference.child("orders").child(orderId).setValue(status)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        response.orderSavedToDatabase = true;
                        Log.d("FOWRepository", "OrderHistory Saved to Profile successfully");
                        orderCallback.onResponse(response);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        response.orderSavedToDatabase = false;
                        response.exception = e.getMessage();
                        orderCallback.onResponse(response);
                        Log.d("FOWRepository", "" + e.getMessage());
                    }
                });
    }
}



