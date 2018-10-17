package com.checkin.app.checkin.Shop;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.checkin.app.checkin.Maps.MapsActivity;
import com.checkin.app.checkin.R;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.OnMapReadyCallback;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
public class ShopInfoActivity extends AppCompatActivity {
    @BindView(R.id.btn3_next)
    Button next;
    @BindView(R.id.et_address)
    EditText tvAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_info);
        ButterKnife.bind(this);
        tvAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ShopInfoActivity.this,MapsActivity.class);
                startActivityForResult(intent, MapsActivity.REQUEST_MAP_CODE);
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // check that it is the SecondActivity with an OK result
        if (requestCode == MapsActivity.REQUEST_MAP_CODE) {
            if (resultCode == RESULT_OK) {

                // get String data from Intent
                Double latitude = data.getDoubleExtra(MapsActivity.KEY_MAPS_LATITUDE,0);
                double longitude = data.getDoubleExtra(MapsActivity.KEY_MAPS_LONGITUDE, 0);
                String address = data.getStringExtra(MapsActivity.KEY_MAPS_ADDRESS);
                // set text view with string
               tvAddress.setText(address);
            }
        }
    }
}
