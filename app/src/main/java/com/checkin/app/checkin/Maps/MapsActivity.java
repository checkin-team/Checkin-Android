package com.checkin.app.checkin.Maps;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.checkin.app.checkin.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MapsActivity extends AppCompatActivity implements
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    public static final int REQUEST_MAP_CODE = 2500;
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final int REQUEST_CODE_AUTOCOMPLETE = 5000;
    private static final int PERMISSION_REQUEST_FINE_LOCATION = 1000;
    private static String TAG = MapsActivity.class.getSimpleName();
    public static final String KEY_MAPS_LATITUDE = TAG + ".latitude";
    public static final String KEY_MAPS_LONGITUDE = TAG + ".longitude";
    public static final String KEY_MAPS_ADDRESS = TAG + ".address";
    /**
     * The formatted location address.
     */
    protected Address mAddress;
    @BindView(R.id.btn_done)
    ImageView btnDone;
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private FusedLocationProviderClient mLocationProviderClient;
    private LatLng mCenterLatLong;
    /**
     * Receiver registered with this activity to get the response from FetchAddressIntentService.
     */
    private AddressResultReceiver mResultReceiver;
    private boolean isSearchOpened = false;

    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            Location location = locationResult.getLastLocation();
            if (location != null)
                changeMap(location);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        ButterKnife.bind(this);

        Places.initialize(getApplicationContext(), this.getResources().getString(R.string.google_maps_key));

        mLocationProviderClient = new FusedLocationProviderClient(this);
        mapFragment.getMapAsync(this);
        mResultReceiver = new AddressResultReceiver(new Handler());
        if (checkPlayServices()) {
            // If this check succeeds, proceed with normal processing.
            // Otherwise, prompt user to get valid Play Services APK.
            if (!AppUtils.isLocationEnabled(this)) {
                // notify user
                new AlertDialog.Builder(this)
                        .setMessage("Location not enabled!")
                        .setPositiveButton("Open location settings", (paramDialogInterface, paramInt) -> {
                            Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(myIntent);

                        })
                        .setNegativeButton("Cancel", (paramDialogInterface, paramInt) -> {
                        })
                        .show();
            }
            buildGoogleApiClient();
        } else {
            Toast.makeText(this, "Location not supported in this device", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(false);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.setOnCameraIdleListener(() -> {
            mCenterLatLong = mMap.getCameraPosition().target;
            Location mLocation = new Location("");
            mLocation.setLatitude(mCenterLatLong.latitude);
            mLocation.setLongitude(mCenterLatLong.longitude);
            startIntentService(mLocation);
        });
    }

    @Override
    public void onConnected(Bundle bundle) {
        if (
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_FINE_LOCATION);
            return;
        }
        goToCurrentLocation();
    }

    private void goToCurrentLocation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }
        mMap.setMyLocationEnabled(true);
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setNumUpdates(1);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationProviderClient.requestLocationUpdates(locationRequest, mLocationCallback, null);
    }

    @Override
    public void onConnectionSuspended(int cause) {
        Log.i(TAG, "Google API Connection suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "Unable to connect", Toast.LENGTH_SHORT).show();
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
            mLocationProviderClient.removeLocationUpdates(mLocationCallback);
        }
    }

    private boolean checkPlayServices() {
        int resultCode = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GoogleApiAvailability.getInstance().isUserResolvableError(resultCode)) {
                GoogleApiAvailability.getInstance().getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.e(TAG, "Google Play services not usable: " + resultCode);
                finish();
            }
            return false;
        }
        return true;
    }

    private void changeMap(Location location) {
        Log.d(TAG, "Reaching map" + mMap);
        // check if map is created successfully or not
        if (mMap != null) {
            Double lat = location.getLatitude();
            Double lng = location.getLongitude();
            LatLng latLong = new LatLng(lat, lng);
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(latLong)
                    .zoom(19f)
                    .tilt(70)
                    .build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            startIntentService(location);
        } else {
            Toast.makeText(getApplicationContext(),
                    "Sorry! unable to create maps", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    @OnClick(R.id.btn_done)
    public void onDoneClick(View v) {
        LatLng latLng = mMap.getCameraPosition().target;
        Intent data = new Intent();
        data.putExtra(KEY_MAPS_LATITUDE, latLng.latitude);
        data.putExtra(KEY_MAPS_LONGITUDE, latLng.longitude);
        data.putExtra(KEY_MAPS_ADDRESS, mAddress);
        setResult(RESULT_OK, data);
        finish();
    }

    /**
     * Creates an intent, adds location data to it as an extra, and starts the intent service for
     * fetching an address.
     */
    protected void startIntentService(Location mLocation) {
        Intent intent = new Intent(this, FetchAddressIntentService.class);
        // Pass the result receiver as an extra to the service.
        intent.putExtra(AppUtils.LocationConstants.RECEIVER, mResultReceiver);
        // Pass the location data as an extra to the service.
        intent.putExtra(AppUtils.LocationConstants.LOCATION_DATA_EXTRA, mLocation);
        startService(intent);
    }

    @OnClick(R.id.tv_search_location)
    public void openAutocompleteActivity() {
        if (checkPlayServices() && !isSearchOpened) {
            try {
                List<Place.Field> fields = Arrays.asList(Place.Field.NAME, Place.Field.LAT_LNG);
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields).build(this);
                startActivityForResult(intent, REQUEST_CODE_AUTOCOMPLETE);
                isSearchOpened = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Called after the autocomplete activity has finished to return its result.
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Check that the result was from the autocomplete widget.
        if (requestCode == REQUEST_CODE_AUTOCOMPLETE) {
            if (resultCode == RESULT_OK) {
                // Get the user's selected place from the Intent.
                Place place = Autocomplete.getPlaceFromIntent(data);
                LatLng latLong = place.getLatLng();
                if (latLong != null) {
                    Location location = new Location("");
                    location.setLatitude(latLong.latitude);
                    location.setLongitude(latLong.longitude);
                    changeMap(location);
                }
            }
            isSearchOpened = false;
        } else {
            Log.e(TAG, Autocomplete.getStatusFromIntent(data).getStatusMessage());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_FINE_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                goToCurrentLocation();
        }
    }

    /**
     * Receiver for data sent from FetchAddressIntentService.
     */
    class AddressResultReceiver extends ResultReceiver {
        AddressResultReceiver(Handler handler) {
            super(handler);
        }

        /**
         * Receives data sent from FetchAddressIntentService and updates the UI in MainActivity.
         */
        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            if (resultCode == AppUtils.LocationConstants.SUCCESS_RESULT) {
                mAddress = resultData.getParcelable(AppUtils.LocationConstants.LOCATION_DATA_ADDRESS_BUNDLE);
            }
        }
    }
}
