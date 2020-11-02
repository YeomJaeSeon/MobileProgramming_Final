package com.example.afinal;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.CalendarView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.io.FileOutputStream;
import java.io.IOException;

public class PlannerActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_planner);

        CalendarView calendarView = (CalendarView) findViewById(R.id.calendarView);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {

            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
//                Toast.makeText(PlannerActivity.this, "토스트 메시지 띄우기 성공~!", Toast.LENGTH_SHORT).show();
                Intent in = new Intent(PlannerActivity.this, PlannerNote.class);
                int requestCode = year+month+dayOfMonth;

                in.putExtra("INPUT_TEXT", "염재선");
//                in.putExtra("INPUT_TEXT", "염재선");
//                in.putExtra("INPUT_TEXT", Integer.toString(requestCode));
                startActivity(in);
            }
        });
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
