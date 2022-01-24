package com.example.fuelonwheelsapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseAuth;

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

    FOWRepository(){
    }

    public void getResponseFromUsingCallback(Context context,GeoPoint geoPoint,OrderCallback orderCallback){
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
            new FindNearFuelTask(orderCallback,response).execute(geoPoint);
        }
    }

    private class FindNearFuelTask extends AsyncTask<GeoPoint, Integer, ArrayList<POI>> {
        public OrderCallback orderCallback=null;
        public Response response=null;

        public FindNearFuelTask(OrderCallback callback,Response response1){
            orderCallback=callback;
            response = response1;
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
                Log.d("FOWRepository", "onPostExecute: Successfully found the nearest POI");
                //orderCallback.onResponse(response);
                buildRoad(userCoordinates, fuelCoordinates,response);
            }
        }

        public void buildRoad(GeoPoint userGeoPoint, GeoPoint fuelGeoPoint,Response response) {
            ArrayList<GeoPoint> waypoints = new ArrayList<>();
            waypoints.add(userGeoPoint);
            waypoints.add(fuelGeoPoint);
            new RouteTask(orderCallback,response).execute(waypoints);
        }
    }

    private class RouteTask extends AsyncTask<ArrayList<GeoPoint>, Integer, Road>
    {
        public OrderCallback orderCallback=null;
        public Response response=null;
        public RouteTask(OrderCallback callback, Response response1){
            orderCallback=callback;
            response =response1;
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
            response.orderId = "O"+orderId;
            Log.d("FOWRepository", "onPostExecute: orderId"+orderId);
            // TODO : save all the details to DB
            orderCallback.onResponse(response);
        }
    }

}


