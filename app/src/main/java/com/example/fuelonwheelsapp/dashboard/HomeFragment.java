package com.example.fuelonwheelsapp.dashboard;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.fuelonwheelsapp.viewModels.FOWViewModel;
import com.example.fuelonwheelsapp.R;

import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.tileprovider.tilesource.XYTileSource;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String MY_PREFS_NAME = "UserData";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private FOWViewModel viewModel;

    private static final String TAG = "HomeFragment.java";
    private static String MY_USER_AGENT;
    private final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;
    private MapView map = null;
    private Context ctx;
    private IMapController mapController;
    private LocationManager locationManager;
    private Geocoder geocoder;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Log.d(TAG, "onAttach");

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        ctx = getContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        MY_USER_AGENT = ctx.getPackageName();
        viewModel = new ViewModelProvider(requireActivity()).get(FOWViewModel.class);
        locationManager = (LocationManager) requireActivity().getSystemService(Context.LOCATION_SERVICE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        configureMap(view);
        requestPermissionsIfNecessary(new String[]{
                // if you need to show the current location, uncomment the line below
                Manifest.permission.ACCESS_FINE_LOCATION,
                // WRITE_EXTERNAL_STORAGE is required in order to show the map
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        });
        configureLocationManager();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onViewCreated");
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated");


        viewModel.getRoad().observe(getViewLifecycleOwner(), new Observer<Road>() {
            @Override
            public void onChanged(Road road) {
                Log.d(TAG, "In viewModel.getRoad().observe");
                if (road != null) {
                    Log.d(TAG, "Road variable is NOT null");
                    Drawable poiIcon = getResources().getDrawable(R.drawable.marker_default_focused_base);
                    Marker poiMarker = new Marker(map);
                    poiMarker.setTitle("Fuel");
                    poiMarker.setSnippet(viewModel.getFuelLocation());
                    poiMarker.setPosition(viewModel.getFuelCoordinates().getValue());
                    poiMarker.setIcon(poiIcon);
                    poiMarker.setId("fuelMarker");
                    map.getOverlays().add(poiMarker);
                    Polyline roadOverlay = RoadManager.buildRoadOverlay(road);
                    roadOverlay.setId("road");
                    roadOverlay.getOutlinePaint().setStrokeWidth(30.0f);
                    map.getOverlays().add(roadOverlay);
                    map.invalidate();
                } else {
                    Log.d(TAG, "Road variable is null");
                }
            }
        });

        viewModel.getDelivered().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean != null && aBoolean) {
                    for (int i = 0; i < map.getOverlays().size(); i++) {
                        try {
                            Overlay overlay = map.getOverlays().get(i);
                            if (overlay instanceof Polyline && ((Polyline) overlay).getId().equals("road")) {
                                Log.d(TAG, "road:" + i);
                                map.getOverlays().remove(overlay);
                                map.invalidate();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    for (int i = 0; i < map.getOverlays().size(); i++) {
                        try {
                            Overlay overlay = map.getOverlays().get(i);
                            if (overlay instanceof Marker && ((Marker) overlay).getId().equals("fuelMarker")) {
                                Log.d(TAG, "fuelMarker:" + i);
                                map.getOverlays().remove(overlay);
                                map.invalidate();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });

    }

    private final LocationListener locationListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            // TODO Auto-generated method stub
            updateLocation(location);
            locationManager.removeUpdates(this);
        }

        @Override
        public void onProviderDisabled(String provider) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onProviderEnabled(String provider) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            // TODO Auto-generated method stub

        }

    };

    private void configureLocationManager() {
        if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            requestPermissionsIfNecessary(new String[]{
                    // if you need to show the current location, uncomment the line below
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    // WRITE_EXTERNAL_STORAGE is required in order to show the map
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            });
            return;
        }

        Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if (lastKnownLocation == null) {
            lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }
        if(lastKnownLocation!=null){
            updateLocation(lastKnownLocation);
        }
        else{
            if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
            }
            else{
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            }
        }
    }

    private void updateLocation(Location loc) {
        Log.d(TAG, "updateLocation: Updating the location here");
        GeoPoint geoPoint = new GeoPoint(loc.getLatitude(),loc.getLongitude());
        viewModel.setUserGeoPoint(geoPoint);
        Marker startMarker = new Marker(map);
        startMarker.setPosition(geoPoint);
        startMarker.setId("userMarker");
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        startMarker.setIcon(getResources().getDrawable(R.drawable.marker_default));
        startMarker.setSnippet("My Location");
        generateLocation(geoPoint,startMarker);

    }

    private void generateLocation(GeoPoint geoPoint, Marker startMarker) {

        geocoder = new Geocoder(requireActivity(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(geoPoint.getLatitude(),geoPoint.getLongitude(),1);
            if (addresses!=null && addresses.size()>0){
                Log.i("Location info",addresses.get(0).toString());
                String add="";

                if(addresses.get(0).getAddressLine(0)!=null)
                    add+=addresses.get(0).getAddressLine(0);
                viewModel.setOrderLocation(add);
                viewModel.setUserGeoPoint(geoPoint);
                mapController.setCenter(geoPoint);
                mapController.animateTo(geoPoint,13.0, 1L);

                SharedPreferences.Editor editor = requireActivity().getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE).edit();
                editor.putString("userLatitude", String.valueOf(geoPoint.getLatitude()));
                editor.putString("userLongitude", String.valueOf(geoPoint.getLatitude()));
                editor.apply();

                Toast.makeText(requireActivity(), add, Toast.LENGTH_SHORT).show();
                startMarker.setSubDescription(add);
                startMarker.setImage(getResources().getDrawable(R.drawable.osm_ic_follow_me));
                map.getOverlays().add(startMarker);
                map.invalidate();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        ArrayList<String> permissionsToRequest = new ArrayList<>();
        for (int i = 0; i < grantResults.length; i++) {
            permissionsToRequest.add(permissions[i]);
        }
        if (permissionsToRequest.size() > 0) {
            ActivityCompat.requestPermissions(
                    requireActivity(),
                    permissionsToRequest.toArray(new String[0]),
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }



    private void requestPermissionsIfNecessary(String[] permissions) {
        ArrayList<String> permissionsToRequest = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(requireActivity(), permission)
                    != PackageManager.PERMISSION_GRANTED) {
                // Permission is not granted
                permissionsToRequest.add(permission);
            }
        }
        if (permissionsToRequest.size() > 0) {
            ActivityCompat.requestPermissions(
                    requireActivity(),
                    permissionsToRequest.toArray(new String[0]),
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    private void configureMap(View view) {
        map = (MapView) view.findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);
        //map.setTilesScaledToDpi(true);
        final float scale = requireActivity().getResources().getDisplayMetrics().density;
        //final int newScale = (int) (256 * scale);
        String[] OSMSource = new String[2];
        OSMSource[0] = "http://a.tile.openstreetmap.org/";
        OSMSource[1] = "http://b.tile.openstreetmap.org/";
        XYTileSource MapSource = new XYTileSource(
                "OSM",
                0,
                40,
                512,
                ".png",
                OSMSource
        );
        map.setTileSource(MapSource);
        //map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);
        mapController = map.getController();
        RotationGestureOverlay mRotationGestureOverlay = new RotationGestureOverlay(map);
        mRotationGestureOverlay.setEnabled(true);
        map.getOverlays().add(mRotationGestureOverlay);
        map.invalidate();
        Log.d("HomeFragment.java","in configureMap()");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        map.onResume();
        // when we click home button again after ordering
        try {
            Log.d(TAG, "onResume: "+viewModel.getRoad().getValue());

        } catch (Exception e) {
            e.printStackTrace();
        }
        if(viewModel.getRoad().getValue()!=null && viewModel.getDelivered().getValue()!=null){
            if(!viewModel.getDelivered().getValue()){
                Log.d(TAG, "onActivityCreated: handle home click between order");
                Drawable poiIcon = getResources().getDrawable(R.drawable.marker_default_focused_base);
                Marker poiMarker = new Marker(map);
                poiMarker.setTitle("Fuel");
                poiMarker.setSnippet(viewModel.getFuelLocation());
                poiMarker.setPosition(viewModel.getFuelCoordinates().getValue());
                poiMarker.setIcon(poiIcon);
                poiMarker.setId("fuelMarker");
                map.getOverlays().add(poiMarker);
                Polyline roadOverlay = RoadManager.buildRoadOverlay(viewModel.getRoad().getValue());
                roadOverlay.setId("road");
                roadOverlay.getOutlinePaint().setStrokeWidth(30.0f);
                map.getOverlays().add(roadOverlay);
                map.invalidate();
            }
        }

        if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            requestPermissionsIfNecessary(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    // WRITE_EXTERNAL_STORAGE is required in order to show the map
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            });
            return;
        }
        if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        }
        else{
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG,"onPause");
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        Configuration.getInstance().save(ctx, prefs);
        map.onPause();
        locationManager.removeUpdates(locationListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG,"onStop");
    }
}