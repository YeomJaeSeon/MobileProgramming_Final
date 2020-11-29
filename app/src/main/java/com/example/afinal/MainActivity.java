package com.example.afinal;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;


import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseError;
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
import java.util.Timer;
import java.util.TimerTask;

import static android.widget.Toast.LENGTH_LONG;


public class MainActivity extends AppCompatActivity {
    //https://firebase.google.com/docs/database/android/read-and-write?hl=ko

    TextView timerText;
    Button stopStartButton;
    Timer timer;
    TimerTask timerTask;
    Double time = 0.0;

    boolean timerStarted = false;

    //database 가져오기
    private DatabaseReference mDatabase;


    TextView quote, author;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        mDatabase= FirebaseDatabase.getInstance().getReference();
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                if (dataSnapshot.getValue() != null) {
                    String value = dataSnapshot.getValue().toString();
                    Log.d("Database", "Value is: " + value);
                }else{
                    Log.w("Database","Value 없음");
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("Database", "Failed to read value.", error.toException());
            }
        });
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        quote = findViewById(R.id.quotes);
        author = findViewById(R.id.author);
        setQuote(quote, author);
        timerText = (TextView) findViewById(R.id.timeText);
        stopStartButton = (Button) findViewById(R.id.startstopbutton);

        timer = new Timer();

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