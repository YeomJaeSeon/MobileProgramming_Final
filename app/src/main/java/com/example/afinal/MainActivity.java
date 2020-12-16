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

import java.util.ArrayList;
import java.util.Timer;


public class MainActivity extends AppCompatActivity {

    TextView timerText; // 홈 화면 총 공부시간

    //현재시간
    DateAndTimer dateAndTimer;
    public double sum;
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

        //현재시간 출력
        dateAndTimer = new DateAndTimer();

        //arrayList, 리스트 아이템
        oData = new ArrayList<>();
        oAdapter = new CustomListAdapter(oData);
        m_oListView = findViewById(R.id.listViewTodo);
        m_oListView.setAdapter(oAdapter);

        //Database
        mDatabase = FirebaseDatabase.getInstance().getReference();
        //유성
        timerText = findViewById(R.id.timeText);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mDatabase.child("datas").child(dateAndTimer.curTimeArr[1]).child(dateAndTimer.curTimeArr[2]).addValueEventListener(new ValueEventListener() {
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

                //초기화면 구성
                sum=0;
                for (int i = 0; i < times.length; i++) {
                    if (times[i] != null) {
                        sum += times[i];
                    }
                }
                mDatabase.child("times").child(dateAndTimer.curTimeArr[1]).child(dateAndTimer.curTimeArr[2]).child("wholeTime").setValue(sum);
                timerText.post(new Runnable() {
                    @Override
                    public void run() {
                        timerText.setText(dateAndTimer.getTimerText(sum));
                    }
                });

                for (int i = 0; i < times.length; i++) {

                }
                //로딩 종료
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

        //시간저장
//        timer.scheduleAtFixedRate(new TimerTask() {
//            @Override
//            public void run() {
//                sum = 0;
//                for (int i = 0; i < times.length; i++) {
//                    if (times[i] != null) {
//                        sum += times[i];
//                    }
//                }
//                timerText.setText(dateAndTimer.getTimerText(sum));
//            }
//        }, 1000, 100);
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