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

    private TextView pozycja;
    private EditText nazwa;
    private Bundle extras;

    private LatLng position;
    private ArrayList<DataList> values1 = new ArrayList<>();
    private DataList dataList;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marker);

        extras = getIntent().getExtras();
        position =(LatLng) extras.get("pozycja");

        init();
    }

    private void init(){
        pozycja = findViewById(R.id.pozycja);
        pozycja.setText(position.toString());

        nazwa = findViewById(R.id.nazwa);


        Button btnMap = findViewById(R.id.powrót);
        btnMap.setOnClickListener(v -> {
            Intent intent = new Intent(MarkerActivity.this, MapActivity.class);
            startActivity(intent);
        });

        Button btnSettings = findViewById(R.id.zapisz);
        btnSettings.setOnClickListener(v -> {
            dataList = new DataList(null,null);
            dataList.setNazwa(nazwa.getText().toString());
            dataList.setPozycja(position);
            //zapis do jsona
            addDataToList(dataList);


            Intent intent = new Intent(MarkerActivity.this, ListActivity.class);
            startActivity(intent);
        });
    }

    private void loadDataList() {
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("list", null);
        Type type = new TypeToken<ArrayList<DataList>>() {}.getType();
        values1 = gson.fromJson(json, type);
    }

    private void addDataToList(DataList dataList) {
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();

        loadDataList();
        values1.add(dataList);

        String json = gson.toJson(values1);
        editor.putString("list", json);
        editor.apply();
    }
}
