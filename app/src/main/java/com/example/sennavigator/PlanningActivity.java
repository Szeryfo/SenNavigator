package com.example.sennavigator;

import android.content.Context;
import android.graphics.Color;
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

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PlanningActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "PlanningActivity";
    private static final float ZOOM = 12f;
    private static final LatLng latLng = new LatLng(54.19, 16.18);

    private AutoCompleteTextView searchText;

    private final ArrayList<LatLng> listPoints = new ArrayList<>();
    private MarkerOptions markerOptions;
    private Polyline polyline;

    private GoogleMap googleMap;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_planning);
        searchText = findViewById(R.id.input_search);
        initMap();
    }

    @Override
    public void onMapReady(GoogleMap gMap) {
        Toast.makeText(this, "Wyświetlenie mapy", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onMapReady: Wyświetlenie mapy");

        googleMap = gMap;
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, ZOOM));

        markerOptions = new MarkerOptions();

        // wybieranie lokacji
        googleMap.setOnMapLongClickListener(latLng -> {
            if (listPoints.size() == 2) {
                listPoints.clear();
                googleMap.clear();
                return;
            }

            listPoints.add(latLng);

            markerOptions.position(latLng);

            if (listPoints.size() == 1) {
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            } else {
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                drawLine(listPoints.get(0),listPoints.get(1));
            }
            googleMap.addMarker(markerOptions);
        });

        init();
    }

    private void init() {
        Log.d(TAG, "init: initalizacja");
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
    private void initMap() {
        Log.d(TAG, "initMap: Initializacja mapy");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.planning_map);
        mapFragment.getMapAsync(PlanningActivity.this);
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
            if (listPoints.size() == 2) {
                listPoints.clear();
                googleMap.clear();
            }

            listPoints.add(new LatLng(address.getLatitude(), address.getLongitude()));
            markerOptions.position(new LatLng(address.getLatitude(), address.getLongitude()));

            if (listPoints.size() == 1) {
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            } else {
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                drawLine(listPoints.get(0),listPoints.get(1));
            }
            googleMap.addMarker(markerOptions);

            moveCamera(new LatLng(address.getLatitude(), address.getLongitude()), address.getAddressLine(0));
        }
    }

    public void closeKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }

    private void moveCamera(LatLng latLng, String title) {
        Log.d(TAG, "moveCamera: Przeniesienie kamery na: lat: " + latLng.latitude + ", lng: " + latLng.longitude);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, ZOOM));

        if (!title.equals("Moja lokalizacja")) {
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(latLng)
                    .title(title);
            googleMap.addMarker(markerOptions);
        }
    }

    private void drawLine(LatLng latLng, LatLng latLng1) {

        if(polyline != null) {
            polyline.remove();
        }

        polyline = googleMap.addPolyline(new PolylineOptions()
                .add(latLng,latLng1)
                .width(7)
                .color(Color.BLUE));
    }
}
