package com.example.fuelonwheelsapp;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment implements GoogleMap.OnMapLongClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private FOWViewModel viewModel;

    private GoogleMap mMap;

    //private ActivityMapsBinding binding;
    private LocationManager lm;
    private LocationListener ll;
    private Geocoder gc;

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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==1)
        {
            if(grantResults.length>1 && grantResults[0]== PackageManager.PERMISSION_GRANTED)
            {
                if(ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                    lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,0,0,ll);
                }
            }
            else {
                Toast.makeText(getActivity(), "You need to allow Location to continue", Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }
    }




    void generate_location(Location location)
    {
        mMap.clear();
        LatLng user = new LatLng(location.getLatitude(),location.getLongitude());
        //System.out.println(location.toString());
        //Log.i("Info",location.toString());


        if(getActivity() != null && isAdded()){

            gc = new Geocoder(getActivity(), Locale.getDefault());
            try {
                List<Address> l = gc.getFromLocation(location.getLatitude(),location.getLongitude(),1);
                if (l!=null && l.size()>0){
                    Log.i("Location info",l.get(0).toString());
                    String add="";

                    if(l.get(0).getAddressLine(0)!=null)
                        add+=l.get(0).getAddressLine(0);
                    viewModel.setOrderLocation(add);
                    Toast.makeText(getActivity(), add, Toast.LENGTH_SHORT).show();
                    mMap.addMarker(new MarkerOptions().position(user).title(add).draggable(true));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(user,15));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        return view;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;
                viewModel = new ViewModelProvider(getActivity()).get(FOWViewModel.class);
                //mMap.setOnMapLongClickListener((GoogleMap.OnMapLongClickListener) getActivity());
                //mMap.setOnMarkerDragListener((GoogleMap.OnMarkerDragListener) getActivity());
                mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                    @Override
                    public void onMarkerDragStart(Marker marker) {
                        Log.i("Start","Mp");
                    }

                    @Override
                    public void onMarkerDrag(Marker marker) {
                        Log.i("ON","Mp");
                    }

                    @Override
                    public void onMarkerDragEnd(Marker marker) {
                        Log.i("End",marker.getPosition().toString());
                        LatLng dragLoc = marker.getPosition();
                        System.out.println("In Marker");
                        try {
                            List<Address> l = gc.getFromLocation(dragLoc.latitude,dragLoc.longitude,1);
                            if (l!=null && l.size()>0){
                                Log.i("Location end",l.get(0).toString());
                                String add="";

                                if(l.get(0).getAddressLine(0)!=null)
                                    add+=l.get(0).getAddressLine(0);
                                viewModel.setOrderLocation(add);
                                Toast.makeText(getActivity(), add, Toast.LENGTH_SHORT).show();
                                marker.setTitle(add);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

                lm = (LocationManager) requireActivity().getSystemService(Context.LOCATION_SERVICE);

                ll = new LocationListener() {
                    @Override
                    public void onLocationChanged(@NonNull Location location) {
                        generate_location(location);//live first location
//                        lm.removeUpdates(ll);
//                        lm=null;
                        lm.removeUpdates(this);
                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {

                    }

                    @Override
                    public void onProviderEnabled(@NonNull String provider) {

                    }

                    @Override
                    public void onProviderDisabled(@NonNull String provider) {

                    }
                };
                if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    //requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
                    ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                } else {
                    if( !lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER) ) {
                        new AlertDialog.Builder(requireContext())
                                .setTitle("GPS not found")  // GPS not found
                                .setMessage("Enable GPS") // Want to enable?
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        requireActivity().startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                                    }
                                })
                                .setNegativeButton("No", null)
                                .show();
                    }
                    lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, ll);
                    Location lKnown = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    if(lKnown != null){
                        generate_location(lKnown);
                    }
                }
            }
        });


    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        Log.i("Location Clicked",latLng.toString());
    }
}