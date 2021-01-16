package com.example.sennavigator;

import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class MainActivity extends FragmentActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init(){
        Button btnMap = findViewById(R.id.btnMap);
        btnMap.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MapActivity.class);
            startActivity(intent);
        });

        Button btnSettings = findViewById(R.id.btnSettings);
        btnSettings.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
        });

        Button btnPlanningMap = findViewById(R.id.btnPlanowanie);
        btnPlanningMap.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, PlanningActivity.class);
            startActivity(intent);
        });
    }
}