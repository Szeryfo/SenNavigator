package com.example.sennavigator;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class MarkerActivity  extends FragmentActivity {

    private LatLng position;
    private ArrayList<Data> placeList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marker);

        Bundle extras = getIntent().getExtras();
        position =(LatLng) extras.get("pozycja");

        init();
    }

    private void init(){
        TextView pozycja = findViewById(R.id.pozycja);
        pozycja.setText(position.toString());

        EditText name = findViewById(R.id.nazwa);


        Button btnMap = findViewById(R.id.powrót);
        btnMap.setOnClickListener(v -> {
            Intent intent = new Intent(MarkerActivity.this, MapActivity.class);
            startActivity(intent);
        });

        Button btnSettings = findViewById(R.id.zapisz);
        btnSettings.setOnClickListener(v -> {
            Data data = new Data(null,null);
            data.setName(name.getText().toString());
            data.setPosition(position);
            //zapis do jsona
            addDataToList(data);


            Intent intent = new Intent(MarkerActivity.this, ListActivity.class);
            startActivity(intent);
        });
    }

    private void loadDataList() {
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("list", null);
        Type type = new TypeToken<ArrayList<Data>>() {}.getType();
        placeList = gson.fromJson(json, type);
    }

    private void addDataToList(Data data) {
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();

        loadDataList();
        placeList.add(data);

        String json = gson.toJson(placeList);
        editor.putString("list", json);
        editor.apply();
    }
}
