package com.example.sennavigator;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

public class ListActivity extends AppCompatActivity {

     ListView listView;

     @Override
     protected void onCreate(Bundle savedInstanceState) {
          super.onCreate(savedInstanceState);
          setContentView(R.layout.activity_list);

          listView = findViewById(R.id.listView);

          String[] values = new String[] {
                  "Dom","Rodzina","Sklep","Garaż",
                  "5","6","7","8",
                  "9","10","11","12",
                  "13","14","15","16"
          };

          ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this,
                  android.R.layout.simple_list_item_1,
                  android.R.id.text1, values);

          listView.setAdapter(arrayAdapter);

          listView.setOnItemClickListener((parent, view, position, id) -> {
           //    if (id == 0) {
                    Intent intent = new Intent(view.getContext(), MapActivity.class);
                    intent.putExtra("string",values[(int) id]);
                    startActivity(intent);

          });
     }
}
