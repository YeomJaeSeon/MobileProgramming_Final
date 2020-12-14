package com.example.afinal;


import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Handler;

//커스텀리스트뷰 시작 재선

class ItemData {
    String todo;
    String times;
    String importance;
    String time;
    boolean flag;

    public ItemData() {

    }
}

public class CustomListAdapter extends BaseAdapter {

    LayoutInflater inflater = null;
    private ArrayList<ItemData> m_oData = new ArrayList<ItemData>();
    String[] curTimeArr;
    String curTime;
    String[] listItemId = new String[5];
    Double[] timeCount = new Double[5];
    boolean[] flagCount = new boolean[5];
    TextView time;
    Button startButton;
    TimerTask timerTask;
    private DatabaseReference mDatabase;

    public static class CustomViewHolder{
        public TextView todo ;
        public TextView estimatedTime;
        public TextView importance;
        public TextView time ;
        public Button startButton ;


    }
    public CustomListAdapter(ArrayList<ItemData> _oData) {
        m_oData = _oData;
    }

    @Override
    public int getCount() {
        return m_oData.size();
    }

    @Override
    public Object getItem(int position) {
        return m_oData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {

        final CustomViewHolder customViewHolder;

        if (convertView == null) {
            final Context context = parent.getContext();
            if (inflater == null) {
                inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            }
            convertView = inflater.inflate(R.layout.main_listview, parent, false);
            customViewHolder=new CustomViewHolder();
            customViewHolder.todo=(TextView) convertView.findViewById(R.id.todo);
            customViewHolder.estimatedTime = convertView.findViewById(R.id.estimatedTime);
            customViewHolder.importance = convertView.findViewById(R.id.importance);
            customViewHolder.time = convertView.findViewById(R.id.time);
            customViewHolder.startButton = convertView.findViewById(R.id.startstopbutton);
            convertView.setTag(customViewHolder);
        }else{
            customViewHolder=(CustomViewHolder)convertView.getTag();
        }

        //ㅠㅠ
        //Dates
        Date date = new Date(System.currentTimeMillis());
        final SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
        curTime = format1.format(date);
        curTimeArr = curTime.split("-");
        //현재시간 출력
        Log.d("MAINACTIVITY_TIME", curTimeArr[1] + "-" + curTimeArr[2]);

        //database
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("datas").child(curTimeArr[1]).child(curTimeArr[2]).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("CUSTOMLISTVIEW_FIREBASE", dataSnapshot.toString());
                if (dataSnapshot.getChildren() != null) {
                    int i = 0;
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        listItemId[i] = child.getValue(Todo.class).id;
                        timeCount[i] = child.getValue(Todo.class).time;
                        flagCount[i] = child.getValue(Todo.class).flag;
                        i++;
                    }
                } else {
                    Log.w("CUSTOMLISTVIEW_FIREBASE", "Value 없음");
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("CUSTOMLISTVIEW_FIREBASE", "Failed to read value.", error.toException());
            }
        });

        //안되면 지우자



        customViewHolder.todo.setText(m_oData.get(position).todo);
        customViewHolder.estimatedTime.setText(m_oData.get(position).times);
        customViewHolder.importance.setText(m_oData.get(position).importance);
        customViewHolder.time.setText(getTimerText(Double.parseDouble(m_oData.get(position).time)));

        //리스트뷰의 타이머 클릭했을때 타이머시작 & stop 구현
        customViewHolder.startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flagCount[position] == false) {
                    mDatabase.child("datas").child(curTimeArr[1]).child(curTimeArr[2]).child(listItemId[position]).child("flag").setValue(true);
                    customViewHolder.startButton.post(new Runnable() {
                        @Override
                        public void run() {
                            customViewHolder.startButton.setText("STOP");
                        }
                    });

                    timerTask = new TimerTask() {
                        @Override
                        public void run() {
                            timeCount[position]++;
                            //반드시 정지하고 다음액티비티로 가야댐
                            mDatabase.child("datas").child(curTimeArr[1]).child(curTimeArr[2]).child(listItemId[position]).child("time").setValue(timeCount[position]);
                        }
                    };
                    ((MainActivity) MainActivity.mContext).timer.schedule(timerTask, 0, 1000);
                } else if (flagCount[position] == true) {
                    mDatabase.child("datas").child(curTimeArr[1]).child(curTimeArr[2]).child(listItemId[position]).child("flag").setValue(false);
                    customViewHolder.startButton.setText("START");
                    timerTask.cancel(); // timerTask 중단
                }
            }
        });

        return convertView;
    }

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

    public void addItem(ItemData data) {
        m_oData.add(data);
    }
}


//커스텀 리스트뷰 끝
