package com.example.afinal;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;


import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity {

    Button addBtn;
    TextView timerText;
    Button stopStartButton;
    Timer timer;
    TimerTask timerTask;
    Double time = 0.0;
    String curTime;
    String[] curTimeArr;
    TextView quote, author;
    boolean timerStarted = false;

    ArrayAdapter<String> adapter;
    ArrayList<String> listItem;
    ListView listView;

    //database 가져오기
    private DatabaseReference mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        quote = findViewById(R.id.quotes);
        author = findViewById(R.id.author);
        setQuote(quote, author);

        timerText = (TextView) findViewById(R.id.timeText);
        stopStartButton = (Button) findViewById(R.id.startstopbutton);
        timer = new Timer();

        mDatabase = FirebaseDatabase.getInstance().getReference();

        listItem = new ArrayList<String>();
        adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, listItem);
        listView = findViewById(R.id.listViewTodo);
        listView.setAdapter(adapter);

        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
        curTime = format1.format(date);
        curTimeArr=curTime.split("-");

        mDatabase.child("datas").child(curTimeArr[1]).child(curTimeArr[2]).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
            Log.d("CURRENTLOC",dataSnapshot.toString());
                if (dataSnapshot.getChildren() != null) {
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        listItem.add("할일:" + child.getValue(Todo.class).name + ", 걸리는 시간:" + String.valueOf(child.getValue(Todo.class).estimatedTime) + "시간" + ", 난이도:" + child.getValue(Todo.class).importance);
                        adapter.notifyDataSetChanged();
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


    }


    //assets파일에서 json파일을 읽어오는 함수
    private String getJson() {

        String data = null;
        AssetManager assetManager = getAssets();


        try {
            InputStream is = assetManager.open("quotes.json");
            int size = is.available();

            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            data = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return data;
    }

    //읽어온 json 파일을 string파일로 변환시켜 textView에 setText하는 함수
    private void setQuote(TextView quote, TextView author) {
        JSONObject obj, content;
        JSONArray jsonArray;
        String quotes, title, subtitle;
        try {
            //json파일을 읽어와 JSONObject 파일로 변환
            obj = new JSONObject(getJson());
            //변환한 JSONObject에서 quotes key를 가진 값들을  string으로 변환한 뒤 quotes에 저장
            quotes = obj.getString("quotes");
            //quotes를 JSONArray 타입으로 변환
            jsonArray = new JSONArray(quotes);

            //랜덤값을 rand에 저장
            int rand = (int) (Math.random() * jsonArray.length());
            //JSONArray에서 랜덤값이 위치한 index를 추출하여 JSONObject로 변환
            content = jsonArray.getJSONObject(rand);
            //JSONObject title에 값 저장
            title = content.getString("quote");  //title은 json파일의 quote임
            subtitle = content.getString("author"); //subtitle은 json파일의 author임
            //View에 텍스트 지정
            quote.setText(title);
            author.setText("- " + subtitle + " -");

        } catch (JSONException ex) {
            ex.printStackTrace();
        }
    }

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

    public void startStopTapped(View view) {
        if (timerStarted == false) {
            timerStarted = true;
            stopStartButton.setText("STOP");
            stopStartButton.setTextColor(ContextCompat.getColor(this, R.color.red));
            startTimer();
        } else {
            timerStarted = false;
            stopStartButton.setText("START");
            stopStartButton.setTextColor(ContextCompat.getColor(this, R.color.green));
            timerTask.cancel();
        }
    }

    private void startTimer() {
        timerTask = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        time++;
                        timerText.setText(getTimerText());
                    }
                });
            }
        };
        timer.scheduleAtFixedRate(timerTask, 0, 1000);
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