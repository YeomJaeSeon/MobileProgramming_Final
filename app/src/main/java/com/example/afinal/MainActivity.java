package com.example.afinal;

import android.app.ProgressDialog;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.AssetManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;


public class MainActivity extends AppCompatActivity {

    TextView timerText; // 홈 화면 총 공부시간

    //현재시간
    String curTime;
    String[] curTimeArr;


    //명언
    TextView quote, author;
    AssetManager assetManager;
    Quote quoteObj = new Quote();

    //리스트뷰
    CustomListAdapter oAdapter;
    ArrayList<ItemData> oData = new ArrayList<>();
    String[] listItemId = new String[5];

    //로딩창
    ProgressDialog progressDialog;
    //time
    Timer timer;
    Double time = 0.0;
    Double[] times = new Double[5];
    //Service
    Service mService;
    ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            TimeService.TimeBinder timeBinder = (TimeService.TimeBinder) iBinder;
            mService = timeBinder.getService();

        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mService = null;
        }
    };
    Intent intent;

    //database 가져오기
    private DatabaseReference mDatabase;
    //커스텀 리스트뷰
    private ListView m_oListView = null;

    public static Context mContext;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //액션바 표시 안하기
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Context
        mContext = this;
        timer = new Timer();

        //quote
        quote = findViewById(R.id.quotes);
        //삭제
        //quote.setText("When one door of happiness closes, another opens, but often we look so long at the closed door that we do not see the one that has been opened for us.");
        //author.setText("Helen Keller");
        author = findViewById(R.id.author);
        assetManager = getResources().getAssets();

        quoteObj.setQuote(quote, author, assetManager);

        //ProgressDialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("로딩중입니다...");
        //로딩창 시작
        progressDialog.show();


        //Dates
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
        curTime = format1.format(date);
        curTimeArr = curTime.split("-");
        //현재시간 출력
        Log.d("MAINACTIVITY_TIME", curTimeArr[1] + "-" + curTimeArr[2]);

        //arrayList, 리스트 아이템
        oData = new ArrayList<>();
        oAdapter = new CustomListAdapter(oData);
        m_oListView = findViewById(R.id.listViewTodo);
        m_oListView.setAdapter(oAdapter);

        //Database
        mDatabase = FirebaseDatabase.getInstance().getReference();
        //유성
        timerText = findViewById(R.id.timeText);
//        Double sum = 0.0;
//        for (int i = 0; i < times.length; i++) {
//            saum += times[i];
//        }
//        timerText.setText(getTimerText(sum)); // timertext에 총 공부 시간 display
    }

    @Override
    protected void onStart() {
        super.onStart();
        mDatabase.child("datas").child(curTimeArr[1]).child(curTimeArr[2]).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("MAINACTIVITY_FIREBASE", dataSnapshot.toString());
                if (dataSnapshot.getChildren() != null) {
                    int i = 0;
                    oData.clear();
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        //커스텀리스트뷰 동적으로 추가
                        ItemData oItem = new ItemData();
                        oItem.todo = child.getValue(Todo.class).name;
                        oItem.times = String.valueOf(child.getValue(Todo.class).estimatedTime);
                        oItem.importance = child.getValue(Todo.class).importance;
                        oItem.time = String.valueOf(child.getValue(Todo.class).time);
                        listItemId[i] = child.getValue(Todo.class).id;
                        times[i] = child.getValue(Todo.class).time;
                        i++;
                        oAdapter.addItem(oItem);
                        oAdapter.notifyDataSetChanged();
                    }

                } else {
                    Log.w("MAINACTIVITY_FIREBASE", "Value 없음");
                }
                //로딩종료
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("MAINACTIVITY_FIREBASE", "Failed to read value.", error.toException());
                progressDialog.dismiss();
            }
        });

        //Service
        intent = new Intent(this, TimeService.class);
        startService(intent);
        bindService(intent, mConnection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        //listView 클리어
        timer.cancel();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mConnection);
        stopService(intent);
    }

    //Timer____LEGACY
//    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
//    public boolean readTimeFile() {
//        try {
//            FileInputStream fis = openFileInput("time"); // 총 공부 시간을 저장할 "time" 내부 파일 열기
//            byte[] buffer = new byte[fis.available()]; // 파일에 값을 읽을 byte형 변수 buffer 생성
//            fis.read(buffer); // 파일이 끝날 때 까지 읽음
//            String timeString = new String(buffer, StandardCharsets.UTF_8); // 파일에 저장된 총 공부 시간을 byte형에서 String형으로 형변환
//            time = Double.parseDouble(timeString); // 총 공부 시간을 String형에서 Double형으로 형변환
//            //timerText.setText(getTimerText()); // timertext에 총 공부 시간 display
//            return true;
//        } catch (IOException e) {
//            e.printStackTrace();
//            return false;
//        }
//    }
//
//    public void saveTimeFile() {
//        // 파일에 시간 저장
//        try {
//            FileOutputStream fos = openFileOutput("time", Context.MODE_PRIVATE);
//
//            fos.write(time.toString().getBytes());
//            fos.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }


    // 초 단위로 되어있는 시간 값을 (시간 : 분 : 초) 형식으로 만드는 메소드
    private String getTimerText(double time) {
        int rounded = (int) Math.round(time); // double형인 변수 time을 int형으로 형변환
        int seconds = ((rounded % 86400) % 3600) % 60; // 초
        int minutes = ((rounded % 86400) % 3600) / 60; // 분
        int hours = ((rounded % 86400) / 3600); // 시간

        return formatTime(seconds, minutes, hours);
    }

    private String formatTime(int seconds, int minutes, int hours) {
        return String.format("%02d", hours) + " : " + String.format("%02d", minutes) + " : " + String.format("%02d", seconds);
    }


    // 하단 버튼(통계 / 홈 / 플래너) 클릭 시 각 액티비티로 이동하는 메소드
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

}