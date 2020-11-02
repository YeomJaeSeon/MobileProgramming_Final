package com.example.afinal;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class StatisticsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
    }
    public void click(View v){
        Intent intent_H = new Intent(StatisticsActivity.this, MainActivity.class);
        Intent intent_P = new Intent(StatisticsActivity.this, PlannerActivity.class);
        int btnId = v.getId();

        switch(btnId){
            case R.id.status:
                break;
            case R.id.home:
                startActivity(intent_H);
                break;
            case R.id.planner:
                startActivity(intent_P);
                break;
        }
    }
}
