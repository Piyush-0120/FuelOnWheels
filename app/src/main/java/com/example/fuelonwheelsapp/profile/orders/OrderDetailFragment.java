package com.example.fuelonwheelsapp.profile.orders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.fuelonwheelsapp.viewModels.FOWViewModel;
import com.example.fuelonwheelsapp.R;
import com.example.fuelonwheelsapp.databinding.FragmentOrderDetailBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class OrderDetailFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    private Order order;
    private FOWViewModel viewModel;
    private FirebaseUser firebaseUser;
    private FragmentOrderDetailBinding orderDetailBinding;


    public OrderDetailFragment() {
        // Required empty public constructor
    }
    public static OrderDetailFragment newInstance(String param1, String param2) {
        OrderDetailFragment fragment = new OrderDetailFragment();
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
        viewModel = new ViewModelProvider(requireActivity()).get(FOWViewModel.class);
        order = viewModel.getOrder().getValue();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        View view = inflater.inflate(R.layout.fragment_order_detail, container, false);
//        return view;
        orderDetailBinding = FragmentOrderDetailBinding.inflate(inflater,container,false);
        return orderDetailBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        orderDetailBinding.floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.action_orderDetailFragment_to_orderListFragment);
            }
        });

        orderDetailBinding.orderDetailOrderId.setText(order.getOrderId());
        orderDetailBinding.orderDetailFuelAddress.setText(order.getFuelLocation());
        orderDetailBinding.orderDetailUserAddress.setText(order.getUserLocation());
        // add Rs. as prefix
        String dieselQtyPrice = order.getnDiesel()+" X Rs. "+order.getDieselPrice();
        orderDetailBinding.orderDetailDieselQtyPrice.setText(dieselQtyPrice);
        String petrolQtyPrice = order.getnPetrol()+" X Rs. "+order.getPetrolPrice();
        orderDetailBinding.orderDetailPetrolQtyPrice.setText(petrolQtyPrice);
        orderDetailBinding.orderDetailDateTime.setText(order.getDateTime());
        float totalDieselPrice = Float.parseFloat(order.getnDiesel())*Float.parseFloat(order.getDieselPrice());
        orderDetailBinding.orderDetailDieselTotalPrice.setText("Rs. "+String.valueOf(totalDieselPrice));
        float totalPetrolPrice = Float.parseFloat(order.getnPetrol())*Float.parseFloat(order.getPetrolPrice());
        orderDetailBinding.orderDetailTotalpetrolPrice.setText("Rs. "+String.valueOf(totalPetrolPrice));
        orderDetailBinding.orderDetailTotalFuelCost.setText("Rs. "+String.valueOf(totalPetrolPrice+totalDieselPrice));
        orderDetailBinding.orderDetailTaxes.setText("Rs. 0");
        orderDetailBinding.orderDetailDeliveryCharge.setText("Rs. "+order.getDeliveryCharge());
        orderDetailBinding.orderDetailGrandTotal.setText("Rs. "+order.getTotalAmount());
        orderDetailBinding.orderDetailPhoneNo.setText(firebaseUser.getPhoneNumber());
    }
}