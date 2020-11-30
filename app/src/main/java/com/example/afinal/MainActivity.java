package com.example.afinal;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity {

    Button addBtn;
    TextView timerText;
    //메인타이머버튼임
    //Button stopStartButton;
    Button newStartButton;
    Timer timer;
    TimerTask timerTask;
    Double time = 0.0;
    //현재시간
    String curTime;
    String[] curTimeArr;

    //명언
    TextView quote, author;
    AssetManager assetManager;
    Quote quoteObj = new Quote();

    //리스트뷰
    boolean timerStarted = false;
    ArrayAdapter<String> adapter;
    ArrayList<String> listItem;
    ListView listView;

    //database 가져오기
    private DatabaseReference mDatabase;

    //커스텀 리스트뷰
    private ListView m_oListView = null;


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //quote
        quote = findViewById(R.id.quotes);
        author = findViewById(R.id.author);
        assetManager = getResources().getAssets();
        quoteObj.setQuote(quote, author, assetManager);


        timerText = findViewById(R.id.timeText);
        //메인타이머 버튼임
        //stopStartButton = (Button) findViewById(R.id.startstopbutton);
        timer = new Timer();

        mDatabase = FirebaseDatabase.getInstance().getReference();


        listItem = new ArrayList<String>();
        adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, listItem);
        listView = findViewById(R.id.listViewTodo);
        listView.setAdapter(adapter);


        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
        curTime = format1.format(date);
        curTimeArr = curTime.split("-");

        Log.d("Time", curTimeArr[1] + "-" + curTimeArr[2]);
        mDatabase.child("datas").child(curTimeArr[1]).child(curTimeArr[2]).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("CURRENTLOC", dataSnapshot.toString());
                if (dataSnapshot.getChildren() != null) {
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        //커스텀리스트뷰 동적으로 추가
                        ItemData oItem = new ItemData();
                        oItem.todo = child.getValue(Todo.class).name;
                        oItem.times = String.valueOf(child.getValue(Todo.class).estimatedTime);
                        oItem.importance = child.getValue(Todo.class).importance;
                        oData.add(oItem);

                        m_oListView = findViewById(R.id.listViewTodo);
                        CustomListAdapter oAdapter = new CustomListAdapter(oData);
                        m_oListView.setAdapter(oAdapter);
                        oAdapter.notifyDataSetChanged();
                    }
                } else {
                    Log.w("Database", "Value 없음");
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("Database", "Failed to read value.", error.toException());
            }
        });

//유성
        File file = new File("time.txt");

        try {
            FileInputStream fis = openFileInput("time");
            byte[] buffer = new byte[fis.available()];
            fis.read(buffer);
            String timeString = new String(buffer, StandardCharsets.UTF_8);
            time = Double.parseDouble(timeString);
            timerText.setText(getTimerText());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    final ArrayList<ItemData> oData = new ArrayList<>();// 염재선 수정


    public void switchIntent(View v) {
        Intent intent_S = new Intent(MainActivity.this, StatisticsActivity.class);
        Intent intent_P = new Intent(MainActivity.this, PlannerActivity.class);
        int btnId = v.getId();

        switch (btnId) {
            case R.id.status:
                startActivity(intent_S);
                break;
            case R.id.home:
                break;
            case R.id.planner:
                startActivity(intent_P);
                break;
        }
    }
    //start버튼클릭 -메인 버튼잠깐삭제
//    public void startStopTapped(View view) {
//        if (timerStarted == false) {
//            timerStarted = true;
//            stopStartButton.setText("STOP");
//            stopStartButton.setTextColor(ContextCompat.getColor(this, R.color.red));
//            startTimer(); // startTimer() 함수 호출
//        } else {
//            timerStarted = false;
//            stopStartButton.setText("START");
//            stopStartButton.setTextColor(ContextCompat.getColor(this, R.color.green));
//            timerTask.cancel(); // timerTask 중단
//        }
//    }

    public void startTimer() {
        timerTask = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() { // runOnUiThread함수를 이용하여 작업 스레드에서 타이머 텍스트를 변경한다.
                    @Override
                    public void run() {
                        time++;
                        timerText.setText(getTimerText());

                        // 파일에 시간 저장
                        try {
                            FileOutputStream fos = openFileOutput("time", Context.MODE_PRIVATE);
                            fos.write(time.toString().getBytes());
                            fos.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                });
            }
        };
        timer.scheduleAtFixedRate(timerTask, 0, 1000); // 즉시 타이머를 구동하고 1000 밀리초 단위로 반복
    }

    private String getTimerText() {
        int rounded = (int) Math.round(time);
        int seconds = ((rounded % 86400) % 3600) % 60;
        int minutes = ((rounded % 86400) % 3600) / 60;
        int hours = ((rounded % 86400) / 3600);

        return formatTime(seconds, minutes, hours);
    }

    private String formatTime(int seconds, int minutes, int hours) {
        return String.format("%02d", hours) + " : " + String.format("%02d", minutes) + " : " + String.format("%02d", seconds);
    }

}