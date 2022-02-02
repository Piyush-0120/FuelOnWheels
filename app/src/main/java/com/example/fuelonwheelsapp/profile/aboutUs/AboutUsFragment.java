package com.example.fuelonwheelsapp.profile.aboutUs;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.fuelonwheelsapp.R;
import com.example.fuelonwheelsapp.databinding.FragmentAboutUsBinding;

public class AboutUsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private FragmentAboutUsBinding binding;

    public AboutUsFragment() {
        // Required empty public constructor
    }
    public static AboutUsFragment newInstance(String param1, String param2) {
        AboutUsFragment fragment = new AboutUsFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentAboutUsBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.aboutUsBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.action_aboutUsFragment_to_profileFragment);
            }
        });
        binding.aboutUsLicenseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // send to license page
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/Piyush-0120/FuelOnWheels/blob/master/LICENSE")));
            }
        });
        binding.aboutUsReadMoreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // send to read more
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/Piyush-0120/FuelOnWheels/blob/master/README.md")));
            }
        });
    }
}