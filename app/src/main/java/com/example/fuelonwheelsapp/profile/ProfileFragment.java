package com.example.fuelonwheelsapp.profile;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.fuelonwheelsapp.R;

import java.util.ArrayList;

public class ProfileFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
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
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ArrayList<ProfileItem> arrayList = new ArrayList<>();
        arrayList.add(new ProfileItem(R.drawable.ic_baseline_account_circle_24,"Account"));
        arrayList.add(new ProfileItem(R.drawable.ic_baseline_local_gas_station_24,"Your Orders"));
        arrayList.add(new ProfileItem(R.drawable.ic_baseline_payment_24,"Payments"));
        arrayList.add(new ProfileItem(R.drawable.ic_baseline_contact_support_24,"About Us"));

        RecyclerView recyclerView = view.findViewById(R.id.profile_menu);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(new ProfileAdapter(arrayList, new ProfileAdapter.OnItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                switch (position) {
                    case 0: Navigation.findNavController(view).navigate(R.id.action_profileFragment_to_accountFragment);break;
                    case 1: Navigation.findNavController(view).navigate(R.id.action_profileFragment_to_orderListFragment);break;
                    case 2: Navigation.findNavController(view).navigate(R.id.action_profileFragment_to_paymentSetUpFragment);break;
                    case 3: Navigation.findNavController(view).navigate(R.id.action_profileFragment_to_aboutUsFragment);break;
                    default: Toast.makeText(view.getContext(), "(" + arrayList.get(position).getItem() + ") clicked", Toast.LENGTH_SHORT).show();
                }
            }
        }));
    }
}