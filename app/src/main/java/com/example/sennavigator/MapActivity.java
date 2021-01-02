package com.example.sennavigator;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "MapActivity";
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float ZOOM = 15f;

    private EditText mSearchText;

    private Boolean mLocationPermissionGranted = false;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        mSearchText = (EditText) findViewById(R.id.input_search);
        getLocationPermission();
    }

    public void closeKeyboard(){
        InputMethodManager inputMethodManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }

    private void init() {
        Log.d(TAG, "init: initalizacja");
        mSearchText.setOnEditorActionListener((v, actionId, event) -> {
            if(actionId == EditorInfo.IME_ACTION_SEARCH
                    || actionId == EditorInfo.IME_ACTION_DONE
                    || event.getAction() == KeyEvent.ACTION_DOWN
                    || event.getAction() == KeyEvent.KEYCODE_ENTER) {
                geoLocate();
            }
            return false;
        });
        mSearchText.setOnFocusChangeListener((v, hasFocus) -> {
            Log.d(TAG, "setOnFocusChangeListener");
            this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        });
    }

    private void geoLocate() {
        Log.d(TAG, "geoLocate: Wyszukiwanie");
        closeKeyboard();
        String search = mSearchText.getText().toString();

        Geocoder geocoder = new Geocoder(MapActivity.this);
        List<Address> list = new ArrayList<>();

        try {
            list = geocoder.getFromLocationName(search, 1);

        } catch (IOException e) {
            Log.e(TAG, "geoLocate: IOException " + e.getMessage() );
        }

        if(list.size() > 0) {
            Address address = list.get(0);

            Log.d(TAG, "geoLocate: Znaleziono adres: " + address.toString());
           // Toast.makeText(this, address.toString(), Toast.LENGTH_SHORT).show();

            moveCamera(new LatLng(address.getLatitude(), address.getLongitude()), ZOOM, address.getAddressLine(0));
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(this, "Wyświetlenie mapy", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onMapReady: Wyświetlenie mapy");
        mMap = googleMap;

        if (mLocationPermissionGranted) {
            getDeviceLocation();

            if (ActivityCompat.checkSelfPermission(this, FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false); // znika przycisk

            init();
        }
    }

    private void initMap() {
        Log.d(TAG, "initMap: Initializacja mapy");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(MapActivity.this);
    }

    private void getDeviceLocation() {
        Log.d(TAG, "getDeviceLocation: Pobranie aktualnej lokalizacji urządzenia");
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try {
            if(mLocationPermissionGranted) {
                Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        Log.d(TAG, "onComplete: Znaleziono lokalizację");
                        Location currentLocation = (Location) task.getResult();
                        if(currentLocation != null) {
                            moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), ZOOM, "Moja lokalizacja");
                        }
                    } else {
                        Log.d(TAG, "onComplete: Nie znaleziono lokalizacji");
                        Toast.makeText(MapActivity.this, "Nie znaleziono lokalizacji urządzenia", Toast.LENGTH_SHORT).show();
                    }
                });
            }

        } catch (SecurityException e) {
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage());
        }
    }

    private void getLocationPermission() {
        Log.d(TAG, "getLocationPermission: Pobranie Uprawnień do lokalizacji");
        String[] permissions = {FINE_LOCATION, COARSE_LOCATION};

        if(ContextCompat.checkSelfPermission(this.getApplicationContext(), FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this.getApplicationContext(), COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                mLocationPermissionGranted = true;
                initMap();
            } else{
                ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else{
            ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    private void moveCamera(LatLng latLng, float zoom, String title) {
        Log.d(TAG, "moveCamera: Przeniesienie kamery na: lat: " + latLng.latitude + ", lng: " + latLng.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

        if(!title.equals("Moja lokalizacja")){
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(latLng)
                    .title(title);
            mMap.addMarker(markerOptions);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: Pobranie Uprawnień do lokalizacji");
        mLocationPermissionGranted = false;

        switch (requestCode){
            case LOCATION_PERMISSION_REQUEST_CODE:{
                if(grantResults.length > 0){
                    for (int i = 0; i < grantResults.length; i++){
                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                            Log.d(TAG, "onRequestPermissionsResult: nieudane podanie uprawnień");
                            mLocationPermissionGranted = false;
                            return;
                        }
                    }
                    Log.d(TAG, "onRequestPermissionsResult: udane podanie uprawnień");
                    mLocationPermissionGranted = true;
                    initMap();
                }
            }
        }
    }
}
