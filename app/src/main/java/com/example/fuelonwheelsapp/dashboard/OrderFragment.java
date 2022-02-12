package com.example.fuelonwheelsapp.dashboard;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.transition.TransitionInflater;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.fuelonwheelsapp.viewModels.FOWViewModel;
import com.example.fuelonwheelsapp.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OrderFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OrderFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    Button petrolAdd, dieselAdd, petrolMinus, petrolPlus, dieselMinus, dieselPlus;
    TextView petrolQuant, dieselQuant;
    int petrolQuantInt, dieselQuantInt;
    private FOWViewModel viewModel;

    public OrderFragment() {
        // Required empty public constructor
        super(R.layout.fragment_order);
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment OrderFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static OrderFragment newInstance(String param1, String param2) {
        OrderFragment fragment = new OrderFragment();
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
        return inflater.inflate(R.layout.fragment_order, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        petrolAdd = view.findViewById(R.id.petrol_add);
        petrolMinus = view.findViewById(R.id.petrol_minus);
        petrolQuant = view.findViewById(R.id.petrol_quant);
        petrolPlus = view.findViewById(R.id.petrol_plus);
        dieselAdd = view.findViewById(R.id.diesel_add);
        dieselMinus = view.findViewById(R.id.diesel_minus);
        dieselQuant = view.findViewById(R.id.diesel_quant);
        dieselPlus = view.findViewById(R.id.diesel_plus);
        viewModel = new ViewModelProvider(requireActivity()).get(FOWViewModel.class);

        updatePetrolQuant(0);
        updateDieselQuant(0);

        petrolAdd.setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    petrolMinus.setVisibility(View.VISIBLE);
                    petrolQuant.setVisibility(View.VISIBLE);
                    petrolPlus.setVisibility(View.VISIBLE);
                    petrolAdd.setVisibility(View.GONE);

                    petrolQuantInt = Integer.parseInt(petrolQuant.getText().toString().substring(1));
                    updatePetrolQuant(1);
                }
            }
        );
        petrolMinus.setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    petrolQuantInt--;
                    updatePetrolQuant(petrolQuantInt);
                }
            }
        );
        petrolPlus.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        petrolQuantInt++;
                        updatePetrolQuant(petrolQuantInt);
                    }
                }
        );

        dieselAdd.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dieselMinus.setVisibility(View.VISIBLE);
                        dieselQuant.setVisibility(View.VISIBLE);
                        dieselPlus.setVisibility(View.VISIBLE);
                        dieselAdd.setVisibility(View.GONE);

                        dieselQuantInt = Integer.parseInt(dieselQuant.getText().toString().substring(1));
                        updateDieselQuant(1);
                    }
                }
        );
        dieselMinus.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dieselQuantInt--;
                        updateDieselQuant(dieselQuantInt);
                    }
                }
        );
        dieselPlus.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dieselQuantInt++;
                        updateDieselQuant(dieselQuantInt);
                    }
                }
        );
    }

    private void updatePetrolQuant(int quant) {
        if(quant == 0) {
            petrolMinus.setVisibility(View.GONE);
            petrolQuant.setVisibility(View.GONE);
            petrolPlus.setVisibility(View.GONE);
            petrolAdd.setVisibility(View.VISIBLE);
        } else {
            petrolQuant.setText("x"+petrolQuantInt);
        }
        viewModel.setPetrolQuantInt(Integer.valueOf(petrolQuantInt));
    }

    private void updateDieselQuant(int quant) {
        if(quant == 0) {
            dieselMinus.setVisibility(View.GONE);
            dieselQuant.setVisibility(View.GONE);
            dieselPlus.setVisibility(View.GONE);
            dieselAdd.setVisibility(View.VISIBLE);
        } else {
            dieselQuant.setText("x"+dieselQuantInt);
        }
        viewModel.setDieselQuantInt(Integer.valueOf(dieselQuantInt));
    }
}