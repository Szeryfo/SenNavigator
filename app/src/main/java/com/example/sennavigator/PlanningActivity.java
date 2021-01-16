package com.example.sennavigator;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PlanningActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "PlanningActivity";
    private static final float ZOOM = 15f;
    private static final LatLng latLng = new LatLng(54.19,16.18);

    private AutoCompleteTextView searchText;

    private Boolean mLocationPermissionGranted = false;
    private GoogleMap googleMap;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_planning);
        searchText = findViewById(R.id.input_search);
    }

    @Override
    public void onMapReady(GoogleMap gMap) {
        Toast.makeText(this, "Wyświetlenie mapy", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onMapReady: Wyświetlenie mapy");
        googleMap = gMap;
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, ZOOM));
        //do ogarnienia..
            init();

    }

    private void init() {
        Log.d(TAG, "init: initalizacja");
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, ZOOM));

        searchText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH
                    || actionId == EditorInfo.IME_ACTION_DONE
                    || event.getAction() == KeyEvent.ACTION_DOWN
                    || event.getAction() == KeyEvent.KEYCODE_ENTER) {
                geoLocate();
            }
            return false;
        });
        searchText.setOnFocusChangeListener((v, hasFocus) -> {
            Log.d(TAG, "setOnFocusChangeListener");
            this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        });
    }

    private void geoLocate() {
        Log.d(TAG, "geoLocate: Wyszukiwanie");
        closeKeyboard();
        String search = searchText.getText().toString();

        Geocoder geocoder = new Geocoder(PlanningActivity.this);
        List<Address> list = new ArrayList<>();

        try {
            list = geocoder.getFromLocationName(search, 1);

        } catch (IOException e) {
            Log.e(TAG, "geoLocate: IOException " + e.getMessage());
        }

        if (list.size() > 0) {
            Address address = list.get(0);

            Log.d(TAG, "geoLocate: Znaleziono adres: " + address.toString());
            // Toast.makeText(this, address.toString(), Toast.LENGTH_SHORT).show();

            moveCamera(new LatLng(address.getLatitude(), address.getLongitude()), ZOOM, address.getAddressLine(0));
        }
    }

    public void closeKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }

    private void moveCamera(LatLng latLng, float zoom, String title) {
        Log.d(TAG, "moveCamera: Przeniesienie kamery na: lat: " + latLng.latitude + ", lng: " + latLng.longitude);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

        if (!title.equals("Moja lokalizacja")) {
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(latLng)
                    .title(title);
            googleMap.addMarker(markerOptions);
        }
    }
}
