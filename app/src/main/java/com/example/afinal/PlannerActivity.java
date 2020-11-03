package com.example.afinal;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CalendarView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class PlannerActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_planner);

        CalendarView calendarView = (CalendarView) findViewById(R.id.calendarView);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {

            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                Toast.makeText(PlannerActivity.this, year+":"+month+":"+dayOfMonth, Toast.LENGTH_SHORT).show();
                Intent i = new Intent(PlannerActivity.this, PlannerNote.class);
                String yearId = year+", "+month+", "+dayOfMonth;

                i.putExtra("ID", yearId);
                startActivity(i);
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
