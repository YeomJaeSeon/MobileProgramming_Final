package com.example.afinal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    Intent intent_P = new Intent(MainActivity.this, PlannerActivity.class);
    Intent intent_S = new Intent(MainActivity.this, StatisticsActivity.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    public void click(View v){
        int btnId = v.getId();

        switch(btnId){
            case R.id.status:
                startActivity(intent_P);
                break;
            case R.id.home:
                break;
            case R.id.planner:
                startActivity(intent_S);
                break;
        }
    }
}