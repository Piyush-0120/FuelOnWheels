package com.example.fuelonwheelsapp.profile.account;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.fuelonwheelsapp.viewModels.FOWViewModel;
import com.example.fuelonwheelsapp.R;
import com.example.fuelonwheelsapp.dashboard.User;
import com.example.fuelonwheelsapp.placeholder.PlaceholderContent;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

/**
 * A fragment representing a list of Items.
 */
public class AccountFragment extends Fragment {

    private FOWViewModel viewModel;
    private final static String TAG ="AccountFragment";
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public AccountFragment() {
    }

    public static AccountFragment newInstance(int columnCount) {
        AccountFragment fragment = new AccountFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(FOWViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account_list, container, false);
        ProgressBar progressBar = view.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        ImageView imageView = view.findViewById(R.id.accountList_btn_back);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.action_accountFragment_to_profileFragment);
            }
        });
        // TODO: call this function once and use the field of viewmodel
        viewModel.getResponseAsUserProfile().observe(getViewLifecycleOwner(), new Observer<User>() {
            @Override
            public void onChanged(User user) {
                progressBar.setVisibility(View.GONE);
                viewModel.setUser(user);
                Context context = view.getContext();
                RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.account_list);
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
                PlaceholderContent placeholderContent = new PlaceholderContent();
                placeholderContent.addPlaceholderItem(0, "Full name", user.getFullName());
                placeholderContent.addPlaceholderItem(0, "Phone Number", user.getPhoneNo());
                placeholderContent.addPlaceholderItem(0, "Email", user.getEmail());
                recyclerView.setAdapter(new AccountRecyclerViewAdapter(placeholderContent.ITEMS, new AccountRecyclerViewAdapter.OnItemClickListener() {
                    @Override
                    public void onClick(View view, int position) {
                        if (position != 1) {
                            Bundle bundle = new Bundle();
                            bundle.putString("ContentType", placeholderContent.ITEMS.get(position).contentType);
                            bundle.putString("ContentDesc", placeholderContent.ITEMS.get(position).contentDescription);
                            Navigation.findNavController(view).navigate(R.id.action_accountFragment_to_accountEditFragment, bundle);
                        } else {
                            Toast.makeText(context, "Phone number cannot be changed", Toast.LENGTH_SHORT).show();
                        }
                    }
                }));
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}