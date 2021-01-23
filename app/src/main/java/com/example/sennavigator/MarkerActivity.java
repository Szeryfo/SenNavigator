package com.example.sennavigator;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.model.LatLng;

public class MarkerActivity  extends FragmentActivity {

    private TextView pozycja;
    private LatLng position;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marker);
        init();
    }

    private void init(){
        Bundle extras = getIntent().getExtras();

        pozycja = findViewById(R.id.pozycja);
        pozycja.setText((extras.get("pozycja")).toString());



        Button btnMap = findViewById(R.id.powrót);
        btnMap.setOnClickListener(v -> {
            Intent intent = new Intent(MarkerActivity.this, MapActivity.class);
            startActivity(intent);
        });

        Button btnSettings = findViewById(R.id.zapisz);
        btnSettings.setOnClickListener(v -> {
            Intent intent = new Intent(MarkerActivity.this, ListActivity.class);
            startActivity(intent);
        });
    }

}
