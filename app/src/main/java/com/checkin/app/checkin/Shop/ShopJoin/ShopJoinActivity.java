package com.checkin.app.checkin.Shop.ShopJoin;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.Maps.MapsActivity;
import com.checkin.app.checkin.Misc.LocationModel;
import com.checkin.app.checkin.R;
import com.fasterxml.jackson.databind.JsonNode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;

public class ShopJoinActivity extends AppCompatActivity {
    private static final String TAG = ShopJoinActivity.class.getSimpleName();
    public static final String KEY_SHOP_EMAIL = "shop_email";
    public static final String KEY_SHOP_PHONE_TOKEN = "shop_phone";

    @BindView(R.id.btn_next) Button btnNext;
    @BindView(R.id.et_location) EditText etLocality;
    @BindView(R.id.et_name) EditText etName;
    @BindView(R.id.et_gstin) EditText etGstin;

    private JoinViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_join);
        ButterKnife.bind(this);

        mViewModel = ViewModelProviders.of(this).get(JoinViewModel.class);

        String email = getIntent().getStringExtra(KEY_SHOP_EMAIL);
        String phoneToken = getIntent().getStringExtra(KEY_SHOP_PHONE_TOKEN);

        mViewModel.newShop(email, phoneToken);

        mViewModel.getShopLiveModel().observe(this, model -> {
            if (model == null) {
                Log.e(TAG, "ShopJoinModel is NULL");
                return;
            }
            if (model.isValidName() && model.isValidGstin() && model.isValidLocality()) {
                btnNext.setActivated(true);
            } else {
                btnNext.setActivated(false);
            }
        });

        mViewModel.getObservableData().observe(this, resource -> {
            if (resource == null)
                return;
            if (resource.status == Resource.Status.SUCCESS && resource.data != null) {
                Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
            } else if (resource.status == Resource.Status.ERROR_INVALID_REQUEST) {
                JsonNode error = resource.getErrorBody();
                if (error != null && error.has("gstin")) {
                    JsonNode gstinNode = error.get("gstin");
                    String msg = gstinNode.get(0).asText();
                    etGstin.setError(msg);
                } else {
                    Toast.makeText(getApplicationContext(), "Error!", Toast.LENGTH_SHORT).show();
                }
                Log.e(TAG, "Error: " + resource.message + ", data: " + resource.data);
            }
        });
    }

    @OnTextChanged(value = {R.id.et_name, R.id.et_gstin, R.id.et_location}, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void onValueChanged(Editable editable) {
        String name = etName.getText().toString();
        String gstin = etGstin.getText().toString();
        String locality = etLocality.getText().toString();
        mViewModel.updateShop(name, gstin, locality);
    }


    @OnClick(R.id.im_maps)
    public void onMapsClick() {
        Intent intent = new Intent(this, MapsActivity.class);
        startActivityForResult(intent, MapsActivity.REQUEST_MAP_CODE);
    }

    @OnClick(R.id.btn_next)
    public void onNextClick(View view) {
        if (view.isActivated()) {
            mViewModel.registerNewBusiness();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MapsActivity.REQUEST_MAP_CODE && resultCode == RESULT_OK) {
            double latitude = data.getDoubleExtra(MapsActivity.KEY_MAPS_LATITUDE,0);
            double longitude = data.getDoubleExtra(MapsActivity.KEY_MAPS_LONGITUDE, 0);
            mViewModel.setLocation(new LocationModel(latitude, longitude));
            String address = data.getStringExtra(MapsActivity.KEY_MAPS_ADDRESS);
            etLocality.setText(address);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
