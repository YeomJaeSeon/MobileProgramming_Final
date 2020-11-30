package com.example.afinal;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CalendarView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class PlannerActivity extends AppCompatActivity {
    String yearId;
    Intent i;
    CalendarView calendarView;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_planner);

        calendarView = findViewById(R.id.calendarView);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {




            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {

                month++;
                String formattedDay = String.format("%02d", dayOfMonth);
                String formattedMonth = String.format("%02d",month);

                Toast.makeText(PlannerActivity.this, year+":"+formattedMonth+":"+formattedDay, Toast.LENGTH_SHORT).show();
                i = new Intent(PlannerActivity.this, PlannerNote.class);
                yearId = year+"-"+formattedMonth+"-"+formattedDay;
                i.putExtra("ID", yearId);
                startActivity(i);
            }
        });
    }
    public void click(View v){
        Intent intentMain = new Intent(PlannerActivity.this, MainActivity.class);
        Intent intentStatistics = new Intent(PlannerActivity.this, StatisticsActivity.class);
        int btnId = v.getId();

        switch(btnId){
            case R.id.status:
                startActivity(intentStatistics);
                break;
            case R.id.home:
                startActivity(intentMain);
                break;
            case R.id.planner:
                break;
        }
    }
}
