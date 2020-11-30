package com.example.afinal;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;


import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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
    //메인타이머버튼임
//    Button stopStartButton;
Button newStartButton;
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

    //커스텀 리스트뷰
    private ListView m_oListView = null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        quote = findViewById(R.id.quotes);
        author = findViewById(R.id.author);
        setQuote(quote, author);

        timerText = (TextView) findViewById(R.id.timeText);
        //메인타이머 버튼임
//        stopStartButton = (Button) findViewById(R.id.startstopbutton);
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

        final ArrayList<ItemData> oData = new ArrayList<>();// 염재선 수정

        mDatabase.child("datas").child(curTimeArr[1]).child(curTimeArr[2]).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
            Log.d("CURRENTLOC",dataSnapshot.toString());
                if (dataSnapshot.getChildren() != null) {
                    for (DataSnapshot child : dataSnapshot.getChildren()) {

                        //커스텀리스트뷰 동적으로 추가
                        ItemData oItem = new ItemData();
                        oItem.todo = child.getValue(Todo.class).name;
                        oItem.times = String.valueOf(child.getValue(Todo.class).estimatedTime);
                        oItem.importance = child.getValue(Todo.class).importance;
                        oData.add(oItem);

                        m_oListView = (ListView)findViewById(R.id.listViewTodo);
                        ListAdapter oAdapter = new ListAdapter(oData);
                        m_oListView.setAdapter(oAdapter);
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

        try{
            FileInputStream fis = openFileInput("time");
            byte[] buffer = new byte[fis.available()];
            fis.read(buffer);
            String timeString = new String(buffer, "UTF-8");
            time=Double.parseDouble(timeString);
            timerText.setText(getTimerText());

        } catch (IOException e){
            e.printStackTrace();
        }


}

//커스텀리스트뷰 시작
    public class ItemData
    {
        public String todo;
        public String times;
        public String importance;
    }
    public class ListAdapter extends BaseAdapter
    {
        LayoutInflater inflater = null;
        private ArrayList<ItemData> m_oData = null;
        private int nListCnt = 0;

        public ListAdapter(ArrayList<ItemData> _oData)
        {
            m_oData = _oData;
            nListCnt = m_oData.size();
        }

        @Override
        public int getCount()
        {
            Log.i("TAG", "getCount");
            return nListCnt;
        }

        @Override
        public Object getItem(int position)
        {
            return null;
        }

        @Override
        public long getItemId(int position)
        {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            if (convertView == null)
            {
                final Context context = parent.getContext();
                if (inflater == null)
                {
                    inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                }
                convertView = inflater.inflate(R.layout.main_listview, parent, false);
            }

            TextView todo = (TextView) convertView.findViewById(R.id.todo);
            TextView time = (TextView) convertView.findViewById(R.id.time);
            TextView importance = (TextView) convertView.findViewById(R.id.importance);

            final Button startButton = (Button)convertView.findViewById(R.id.startstopbutton);


            //리스트뷰의 타이머 클릭했을때 타이머시작 & stop 구현
            startButton.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    if (timerStarted == false) {
                        timerStarted = true;
                        startButton.setText("STOP");
                        startTimer(); // startTimer() 함수 호출
                    } else {
                        timerStarted = false;
                        startButton.setText("START");
                        timerTask.cancel(); // timerTask 중단
                    }
                }
            });

            todo.setText(m_oData.get(position).todo);
            time.setText(m_oData.get(position).times);
            importance.setText(m_oData.get(position).importance);
            return convertView;
        }
    }

    //커스텀 리스트뷰 끝



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

    private void startTimer() {
        timerTask = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() { // runOnUiThread함수를 이용하여 작업 스레드에서 타이머 텍스트를 변경한다.
                    @Override
                    public void run() {
                        time++;
                        timerText.setText(getTimerText());

                        // 파일에 시간 저장
                        try{
                            FileOutputStream fos = openFileOutput("time", Context.MODE_PRIVATE);
                            fos.write(time.toString().getBytes());
                            fos.close();
                        }catch (IOException e){
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