package com.example.sennavigator;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class ListActivity extends AppCompatActivity {

     public ArrayList<DataList> placeList = new ArrayList<>();

     private boolean longClick;

     @Override
     protected void onCreate(Bundle savedInstanceState) {
          super.onCreate(savedInstanceState);
          setContentView(R.layout.activity_list);

          ListView listView = findViewById(R.id.listView);
          Button button = findViewById(R.id.powrót);

          loadDataList();

          ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this,
                  android.R.layout.simple_list_item_1,
                  android.R.id.text1);
          for (DataList dataList: placeList) {
               arrayAdapter.add(dataList.getNazwa());
          }
          listView.setAdapter(arrayAdapter);

          listView.setOnItemClickListener((parent, view, position, id) -> {
               if(!longClick) {
                    Intent intent = new Intent(view.getContext(), MapActivity.class);
                    intent.putExtra("DataList", placeList.get((int)id));
                    startActivity(intent);
               }
          });
          listView.setOnItemLongClickListener((parent, view, position, id) -> {
               deletePlace((int) id);
               Intent intent = new Intent(ListActivity.this, ListActivity.class);
               intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
               startActivity(intent);
               longClick = true;
               return false;
          });

          button.setOnClickListener(v -> {
               Intent intent = new Intent(ListActivity.this, MainActivity.class);
               intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
               startActivity(intent);
          });

     }
     private void loadDataList() {
          SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
          Gson gson = new Gson();
          String json = sharedPreferences.getString("list", null);
          Type type = new TypeToken<ArrayList<DataList>>() {}.getType();
          placeList = gson.fromJson(json, type);
     }

     private void deletePlace(int id) {
          SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
          SharedPreferences.Editor editor = sharedPreferences.edit();
          Gson gson = new Gson();

          loadDataList();
          placeList.remove(id);

          String json = gson.toJson(placeList);
          editor.putString("list", json);
          editor.apply();
     }
}
