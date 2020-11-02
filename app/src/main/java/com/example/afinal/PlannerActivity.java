package com.example.afinal;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class PlannerActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_planner);
    }
    public void click(View v){
        Intent intent_H = new Intent(PlannerActivity.this, MainActivity.class);
        Intent intent_S = new Intent(PlannerActivity.this, StatisticsActivity.class);
        int btnId = v.getId();

        switch(btnId){
            case R.id.status:
                startActivity(intent_S);
                break;
            case R.id.home:
                startActivity(intent_H);
                break;
            case R.id.planner:
                break;
        }
    }
}
