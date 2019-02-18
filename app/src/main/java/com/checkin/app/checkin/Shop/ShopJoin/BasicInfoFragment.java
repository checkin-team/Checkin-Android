package com.checkin.app.checkin.Shop.ShopJoin;

import androidx.lifecycle.ViewModelProviders;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.Maps.MapsActivity;
import com.checkin.app.checkin.Misc.GenericDetailModel;
import com.checkin.app.checkin.Misc.LocationModel;
import com.checkin.app.checkin.R;
import com.fasterxml.jackson.databind.JsonNode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import butterknife.Unbinder;

import static android.app.Activity.RESULT_OK;

public class BasicInfoFragment extends Fragment {
    private static final String TAG = BasicInfoFragment.class.getSimpleName();
    private Unbinder unbinder;

    @BindView(R.id.et_location) EditText etLocality;
    @BindView(R.id.et_name) EditText etName;
    @BindView(R.id.et_gstin) EditText etGstin;

    private JoinViewModel mViewModel;
    private BasicInfoFragmentInteraction mInteractionListener;
    private ProgressDialog mProgressDialog;

    public static BasicInfoFragment newInstance(BasicInfoFragmentInteraction listener) {
        BasicInfoFragment fragment = new BasicInfoFragment();
        fragment.mInteractionListener = listener;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view =inflater.inflate(R.layout.fragment_shop_join_basic_info,container,false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mProgressDialog = new ProgressDialog(getContext());
        mProgressDialog.setMessage("Please wait...");
        mProgressDialog.setCancelable(false);

        mViewModel = ViewModelProviders.of(getActivity()).get(JoinViewModel.class);

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
            if (resource.status == Resource.Status.SUCCESS && resource.data != null) {
                if (mProgressDialog != null && mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                mInteractionListener.onShopRegistered(resource.data);
            } else if (resource.status == Resource.Status.ERROR_INVALID_REQUEST) {
                if (mProgressDialog != null && mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
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
                Log.e(TAG, "Error: " + resource.message + ", data: " + resource.data);
            }else if(resource.status == Resource.Status.LOADING){
                if(mProgressDialog !=null)
                    mProgressDialog.show();
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


    @OnClick(R.id.im_maps)
    public void onMapsClick() {
        Intent intent = new Intent(getContext(), MapsActivity.class);
        startActivityForResult(intent, MapsActivity.REQUEST_MAP_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MapsActivity.REQUEST_MAP_CODE && resultCode == RESULT_OK) {
            double latitude = data.getDoubleExtra(MapsActivity.KEY_MAPS_LATITUDE,0);
            double longitude = data.getDoubleExtra(MapsActivity.KEY_MAPS_LONGITUDE, 0);
            mViewModel.setLocation(new LocationModel(latitude, longitude));
            String address = data.getStringExtra(MapsActivity.KEY_MAPS_ADDRESS);
            if (address != null && !address.isEmpty())
                etLocality.setText(address);
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
