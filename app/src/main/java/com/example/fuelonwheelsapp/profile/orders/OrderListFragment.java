package com.example.fuelonwheelsapp.profile.orders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.fuelonwheelsapp.viewModels.FOWViewModel;
import com.example.fuelonwheelsapp.R;
import com.example.fuelonwheelsapp.interfaces.LoadOrderListCallback;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Collections;

public class OrderListFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG ="OrderListFragment";

    private String mParam1;
    private String mParam2;

    public OrderListFragment() {
        // Required empty public constructor
    }
    public static OrderListFragment newInstance(String param1, String param2) {
        OrderListFragment fragment = new OrderListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    private FOWViewModel viewModel;
    private ShimmerFrameLayout mShimmerViewContainer;
    private ImageView back_button;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        viewModel = new ViewModelProvider(requireActivity()).get(FOWViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_order_list, container, false);
        mShimmerViewContainer = view.findViewById(R.id.shimmer_view_container);
        back_button = view.findViewById(R.id.order_list_back_btn);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.action_orderListFragment_to_profileFragment);
            }
        });


        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.order_list_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
        recyclerView.setHasFixedSize(true);
        viewModel.getResponseAfterFetchingOrderList(new LoadOrderListCallback() {
            @Override
            public void onResponse(ArrayList<Order> orders, String exception) {
                Collections.reverse(orders);
                mShimmerViewContainer.stopShimmerAnimation();
                mShimmerViewContainer.setVisibility(View.GONE);
                if(exception==null){
                    // stop shimmer effect
                    Log.d(TAG, "onResponse: Fetched order list successfully");
                    Log.d(TAG, "onResponse: "+orders.size());
                    recyclerView.setAdapter(new OrderListAdapter(orders, new OrderListAdapter.OnItemClickListener() {
                        @Override
                        public void onClick(View view, int position) {
                            Log.d(TAG, "onClick: clicked order no.->"+position);
                            viewModel.setOrder(orders.get(position));
                            Navigation.findNavController(view).navigate(R.id.action_orderListFragment_to_orderDetailFragment);
                        }
                    }));
                }
                // TODO: It was throwing an error while signOut
                else{
                    if(exception.equals("ListEmpty"))
                        Toast.makeText(view.getContext(), "You haven't placed an order yet", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        mShimmerViewContainer.startShimmerAnimation();
    }
    @Override
    public void onPause() {
        mShimmerViewContainer.stopShimmerAnimation();
        super.onPause();
    }
}