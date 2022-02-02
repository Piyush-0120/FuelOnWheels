package com.example.fuelonwheelsapp.dashboard;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.fuelonwheelsapp.viewModels.FOWViewModel;
import com.example.fuelonwheelsapp.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PaymentFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PaymentFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private FOWViewModel viewModel;
    TextView petrolXQuant, dieselXQuant,petrolPrice,dieselPrice,totalPrice;
    TableRow petrolRow, dieselRow;

    public PaymentFragment() {
        // Required empty public constructor
        super(R.layout.fragment_payment);
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PaymentFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PaymentFragment newInstance(String param1, String param2) {
        PaymentFragment fragment = new PaymentFragment();
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
        TransitionInflater inflater = TransitionInflater.from(requireContext());
        setEnterTransition(inflater.inflateTransition(R.transition.slide_up));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_payment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {

        viewModel = new ViewModelProvider(requireActivity()).get(FOWViewModel.class);
        petrolXQuant = view.findViewById(R.id.petrol_x_quant);
        dieselXQuant = view.findViewById(R.id.diesel_x_quant);
        petrolRow = view.findViewById(R.id.petrol_row);
        dieselRow = view.findViewById(R.id.diesel_row);
        petrolPrice = view.findViewById(R.id.petrol_price);
        dieselPrice = view.findViewById(R.id.diesel_price);
        totalPrice = view.findViewById(R.id.total_price);

        petrolXQuant.setText("Petrol x " + viewModel.getPetrolQuantInt());
        petrolPrice.setText("Rs."+(viewModel.getPetrolQuantInt().intValue() * 100)+"/-");
        dieselXQuant.setText("Diesel x " + viewModel.getDieselQuantInt());
        dieselPrice.setText("Rs."+(viewModel.getDieselQuantInt().intValue() * 94)+"/-");
        totalPrice.setText("Rs."+(viewModel.getPetrolQuantInt().intValue()*100
                +viewModel.getDieselQuantInt().intValue()*94+50)+"/-");

        petrolRow.setVisibility(View.VISIBLE);
        dieselRow.setVisibility(View.VISIBLE);
        if(viewModel.getPetrolQuantInt().intValue() == 0)
            petrolRow.setVisibility(View.GONE);
        if(viewModel.getDieselQuantInt().intValue() == 0)
            dieselRow.setVisibility(View.GONE);
    }
}