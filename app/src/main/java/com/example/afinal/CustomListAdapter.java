package com.example.afinal;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

//커스텀리스트뷰 시작 재선
//customViewHolder=>현재 선택한 view
//parent.getChildAt(int i).findViewById(R.id.~~~); 원하는 view 선택
class ItemData {
    String todo;
    String times;
    String importance;
    String time;

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
    TimerTask[] timerTasks = new TimerTask[5];
    private DatabaseReference mDatabase;

    public static class CustomViewHolder {
        public TextView todo;
        public TextView estimatedTime;
        public TextView importance;
        public TextView time;
        public Button startButton;
        public Timer timer;
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
            customViewHolder = new CustomViewHolder();
            customViewHolder.todo = (TextView) convertView.findViewById(R.id.todo);
            customViewHolder.estimatedTime = convertView.findViewById(R.id.estimatedTime);
            customViewHolder.importance = convertView.findViewById(R.id.importance);
            customViewHolder.time = convertView.findViewById(R.id.time);
            customViewHolder.startButton = convertView.findViewById(R.id.startstopbutton);
            convertView.setTag(customViewHolder);
        } else {
            customViewHolder = (CustomViewHolder) convertView.getTag();
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
        customViewHolder.todo.setText(m_oData.get(position).todo);
        customViewHolder.estimatedTime.setText(m_oData.get(position).times);
        customViewHolder.importance.setText(m_oData.get(position).importance);
        customViewHolder.time.setText(getTimerText(Double.parseDouble(m_oData.get(position).time)));

        //리스트뷰의 타이머 클릭했을때 타이머시작 & stop 구현

        customViewHolder.startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if 현재 선택한 view의 택스트라 start라 쓰여있을때 즉 현재 stop상태
                if (customViewHolder.startButton.getText().toString().equals("START")) {
                    //모든 view의 버튼 Start로 바꾸자
                    for (int i = 0; i < parent.getChildCount(); i++) {
                        if (parent.getChildAt(i) != null) {
                            Button btn = (Button) parent.getChildAt(i).findViewById(R.id.startstopbutton);
                            btn.setText("START");
                        }
                    }
                    //현재 선택한 버튼은 STOP으로 바꾸자
                    customViewHolder.startButton.setText("STOP");
                    //현재 실행되고 있는 모든 timerTask 종료 한 다음
                    for (TimerTask timerTask : timerTasks) {
                        if (timerTask != null) {
                            timerTask.cancel();
                        }
                    }
                    //현재 선택한 view를 위한 timerTask를 실행하자
                    timerTasks[position] = new TimerTask() {
                        @Override
                        public void run() {
                            timeCount[position]++;
                            mDatabase.child("datas").child(curTimeArr[1]).child(curTimeArr[2]).child(listItemId[position]).child("time").setValue(timeCount[position]);
                        }
                    };
                    ((MainActivity) MainActivity.mContext).timer.scheduleAtFixedRate(timerTasks[position], 1000, 1000);
                }
                //현재 선택한 버튼이 STOP일때 즉 현재 timerTask가 돌고있을때
                else {
                    Toast.makeText(((MainActivity) MainActivity.mContext), "스케쥴 스톱", Toast.LENGTH_SHORT).show();
                    customViewHolder.startButton.setText("START");
                    timerTasks[position].cancel();
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