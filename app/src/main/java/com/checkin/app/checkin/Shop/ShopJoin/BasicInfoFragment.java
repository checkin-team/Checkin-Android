package com.checkin.app.checkin.Shop.ShopJoin;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.Maps.MapsActivity;
import com.checkin.app.checkin.Misc.DebouncedOnClickListener;
import com.checkin.app.checkin.Misc.GenericDetailModel;
import com.checkin.app.checkin.Misc.LocationModel;
import com.checkin.app.checkin.R;
import com.fasterxml.jackson.databind.JsonNode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnTextChanged;
import butterknife.Unbinder;

import static android.app.Activity.RESULT_OK;

public class BasicInfoFragment extends Fragment {
    @BindView(R.id.et_location)
    EditText etLocality;
    @BindView(R.id.et_name)
    EditText etName;
    @BindView(R.id.et_gstin)
    EditText etGstin;
    private Unbinder unbinder;
    private JoinViewModel mViewModel;
    private BasicInfoFragmentInteraction mInteractionListener;

    public static BasicInfoFragment newInstance(BasicInfoFragmentInteraction listener) {
        BasicInfoFragment fragment = new BasicInfoFragment();
        fragment.mInteractionListener = listener;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_shop_join_basic_info, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mViewModel = ViewModelProviders.of(requireActivity()).get(JoinViewModel.class);

        mViewModel.getShopJoinModel().observe(this, model -> {
            if (model == null)
                return;
            if (model.isValidName() && model.isValidGstin() && model.isValidLocality()) {
                mInteractionListener.onBasicDataValidStatus(true);
            } else {
                mInteractionListener.onBasicDataValidStatus(false);
            }
        });

        mViewModel.getJoinResults().observe(this, resource -> {
            if (resource == null)
                return;
            if (resource.getStatus() == Resource.Status.SUCCESS && resource.getData() != null) {
                mInteractionListener.onShopRegistered(resource.getData());
            } else if (resource.getStatus() == Resource.Status.ERROR_INVALID_REQUEST) {
                JsonNode error = resource.getErrorBody();
                if (error != null && error.has("gstin")) {
                    JsonNode gstinNode = error.get("gstin");
                    String msg = gstinNode.get(0).asText();
                    etGstin.setError(msg);
                } else if (error != null && error.has("location")) {
                    Toast.makeText(getContext(), "Locate your shop on Google Maps!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Error!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        ImageView imMaps = view.findViewById(R.id.im_maps);

        imMaps.setOnClickListener(new DebouncedOnClickListener(2500) {
            @Override
            public void onDebouncedClick(View v) {
                Intent intent = new Intent(getContext(), MapsActivity.class);
                startActivityForResult(intent, MapsActivity.REQUEST_MAP_CODE);
            }
        });
    }

    @OnTextChanged(value = {R.id.et_name, R.id.et_gstin, R.id.et_location}, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void onValueChanged(Editable editable) {
        String name = etName.getText().toString();
        String gstin = etGstin.getText().toString();
        String locality = etLocality.getText().toString();
        mViewModel.updateShopJoin(name, gstin, locality);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MapsActivity.REQUEST_MAP_CODE && resultCode == RESULT_OK) {
            Address address = data.getParcelableExtra(MapsActivity.KEY_MAPS_ADDRESS);
            LocationModel location = new LocationModel(address);
            mViewModel.setLocation(location);
            etLocality.setText(location.toString());
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            this.mInteractionListener = ((BasicInfoFragmentInteraction) context);
        } catch (ClassCastException ignored) {
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    interface BasicInfoFragmentInteraction {
        void onShopRegistered(GenericDetailModel details);

        void onBasicDataValidStatus(boolean isValid);
    }
}
