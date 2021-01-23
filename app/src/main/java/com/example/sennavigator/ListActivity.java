package com.example.sennavigator;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class ListActivity extends AppCompatActivity {

     ListView listView;
     ArrayList<LatLng> values1 = new ArrayList<>();

     @Override
     protected void onCreate(Bundle savedInstanceState) {
          super.onCreate(savedInstanceState);
          setContentView(R.layout.activity_list);

          listView = findViewById(R.id.listView);

          loadDataList();

          ArrayAdapter<LatLng> arrayAdapter = new ArrayAdapter<>(this,
                  android.R.layout.simple_list_item_1,
                  android.R.id.text1, values1);

          listView.setAdapter(arrayAdapter);

          listView.setOnItemClickListener((parent, view, position, id) -> {
                    Intent intent = new Intent(view.getContext(), MapActivity.class);
                    intent.putExtra("string", values1.get((int) id).toString());
                    startActivity(intent);

          });
     }
     private void loadDataList() {
          SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
          Gson gson = new Gson();
          String json = sharedPreferences.getString("list", null);
          Type type = new TypeToken<ArrayList<LatLng>>() {}.getType();
          values1 = gson.fromJson(json, type);
     }
}
