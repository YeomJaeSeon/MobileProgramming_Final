package com.example.afinal;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

class ChartData {
    String date;
    int time;

    ChartData(String _date, int _time) {
        this.date = _date;
        this.time = _time;
    }
}

public class StatisticsActivity extends AppCompatActivity {

    DateAndTimer dateAndTimer;
    ProgressDialog progressDialog;

    //database 가져오기
    private DatabaseReference mDatabase;

    //chart
    String[] xVal;
    String[] yVal;
    String[] totalVal;
    BarChart barChart;
    ArrayList<BarEntry> dates;
    TextView totalStudy;
    TextView avergeStudy;
    int sum;
    int avg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        sum = 0;
        avg = 0;

        //views
        totalStudy = findViewById(R.id.totalStudy);
        avergeStudy = findViewById(R.id.avergeStudy);

        //Database
        mDatabase = FirebaseDatabase.getInstance().getReference();

        //dates
        dateAndTimer = new DateAndTimer();

        //ProgressDialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("로딩중입니다...");
        //로딩창 시작
        progressDialog.show();

        xVal = new String[]{"0", "0", "0", "0", "0",};
        yVal = new String[]{"0", "0", "0", "0", "0",};
        totalVal = new String[]{"0", "0", "0", "0", "0"};

        //chart
        barChart = findViewById(R.id.barchart);
        dates = new ArrayList<>();
        //getValues
        mDatabase.child("times").child(dateAndTimer.curTimeArr[1]).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("MAINACTIVITY_FIREBASE", dataSnapshot.toString());
                //오늘날짜
                //dateAndTimer.curTimeArr[2];
                if (dataSnapshot.getChildren() != null) {
                    double currentDate = Double.parseDouble(dateAndTimer.curTimeArr[2]);
                    for (int i = 0; i < 5; i++) {
                        if (currentDate - i < 1) {
                            break;
                        }
                        int loc = (int) currentDate - i;
                        String str = String.valueOf(dataSnapshot.child(String.valueOf(loc)).child("wholeTime").getValue());
                        if (str == "null") {
                            yVal[i] = "0";
                        } else {
                            totalVal[i] = str;
                            yVal[i] = dateAndTimer.formatToHour(str);
                        }
                        xVal[i] = (dateAndTimer.curTimeArr[1] + "-" + String.valueOf(loc));
                        dates.add(new BarEntry(i, Integer.parseInt(yVal[i])));
                        Log.d("TIMETIME", xVal[i] + "값:" + yVal[i]);
                    }
                } else {
                    Log.w("STATISTICS_FIREBASE", "Value 없음");
                }
                XAxis xAxis = barChart.getXAxis();
                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                xAxis.setValueFormatter(new IndexAxisValueFormatter(xVal));
                xAxis.setTextSize(16f);
                xAxis.setTextColor(R.color.colorPrimary);
                BarDataSet barDataSet = new BarDataSet(dates, "날짜들");
                barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
                barDataSet.setValueTextColor(Color.BLACK);
                barDataSet.setValueTextSize(12f);
                BarData barData = new BarData(barDataSet);

                barChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
                    @Override
                    public void onValueSelected(Entry e, Highlight h) {
                        float x = e.getX();
                        float y = e.getY();
                        String[] str = xVal[(int) x].split("-");
                        Toast.makeText(StatisticsActivity.this, str[0] + "월" + str[1] + "일" + " " + String.format("%.0f", y) + "시간 만큼 공부함", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNothingSelected() {

                    }
                });
                barChart.setFitBars(true);
                barChart.setData(barData);
                barChart.getDescription().setText("5일간 공부량");
                barChart.animateY(2000);
                barChart.invalidate();

                for (String str : totalVal) {
                    sum += Integer.parseInt(str);
                    Log.d("TIMETIME",str+":"+sum);
                }

                avg = sum / totalVal.length;
                //로딩 종료
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                totalStudy.setText("5일간 총 공부량: " + dateAndTimer.getTimerText((double)sum));
                avergeStudy.setText("5일간 하루 평균 공부량: " + dateAndTimer.getTimerText((double)avg));
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("STATISTICS_FIREBASE", "Failed to read value.", error.toException());
                progressDialog.dismiss();
            }

        });

    }

    public void click(View v) {
        Intent intent_H = new Intent(StatisticsActivity.this, MainActivity.class);
        Intent intent_P = new Intent(StatisticsActivity.this, PlannerActivity.class);
        int btnId = v.getId();

        switch (btnId) {
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
