package com.example.sennavigator;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class ListActivity extends AppCompatActivity {

     ListView listView;
     ArrayList<DataList> values1 = new ArrayList<>();
     Button button;

     boolean longClick;

     @Override
     protected void onCreate(Bundle savedInstanceState) {
          super.onCreate(savedInstanceState);
          setContentView(R.layout.activity_list);

          listView = findViewById(R.id.listView);
          button = findViewById(R.id.powrót);

          loadDataList();

          ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this,
                  android.R.layout.simple_list_item_1,
                  android.R.id.text1);
          for (DataList dataList: values1) {
               arrayAdapter.add(dataList.getNazwa());
          }
          listView.setAdapter(arrayAdapter);

          listView.setOnItemClickListener((parent, view, position, id) -> {
               if(!longClick) {
                    Intent intent = new Intent(view.getContext(), MapActivity.class);
                    intent.putExtra("DataList", values1.get((int)id));
                    startActivity(intent);
               }
          });
          listView.setOnItemLongClickListener((parent, view, position, id) -> {
               saveList((int) id);
               Intent intent = new Intent(ListActivity.this, ListActivity.class);
               intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
               startActivity(intent);
               longClick = true;
               return false;
          });

          button.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                    Intent intent = new Intent(ListActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
               }
          });

     }
     private void loadDataList() {
          SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
          Gson gson = new Gson();
          String json = sharedPreferences.getString("list", null);
          Type type = new TypeToken<ArrayList<DataList>>() {}.getType();
          values1 = gson.fromJson(json, type);
     }

     private void saveList(int id) {
          SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
          SharedPreferences.Editor editor = sharedPreferences.edit();
          Gson gson = new Gson();

          loadDataList();
          values1.remove(id);

          String json = gson.toJson(values1);
          editor.putString("list", json);
          editor.apply();
     }
}
