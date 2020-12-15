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

import androidx.core.content.ContextCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.TimerTask;

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

    String[] listItemId = new String[5];
    Double[] timeCount = new Double[5];
    TimerTask[] timerTasks = new TimerTask[5];

    DateAndTimer dateAndTimer = new DateAndTimer();

    private DatabaseReference mDatabase;

    public static class CustomViewHolder {
        public TextView todo;
        public TextView estimatedTime;
        public TextView importance;
        public TextView time;
        public Button startButton;
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

        //건들노노요 진짜중요 이거 ㅠ
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

        //현재시간 출력
        Log.d("MAINACTIVITY_TIME", dateAndTimer.curTimeArr[1] + "-" + dateAndTimer.curTimeArr[2]);
        //database
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("datas").child(dateAndTimer.curTimeArr[1]).child(dateAndTimer.curTimeArr[2]).addListenerForSingleValueEvent(new ValueEventListener() {
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
        customViewHolder.time.setText(dateAndTimer.getTimerText(Double.parseDouble(m_oData.get(position).time)));

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
                            btn.setBackgroundColor(ContextCompat.getColor(parent.getContext(), R.color.green));
                        }
                    }
                    //현재 선택한 버튼은 STOP으로 바꾸자
                    customViewHolder.startButton.setText("STOP");
                    customViewHolder.startButton.setBackgroundColor(ContextCompat.getColor(parent.getContext(), R.color.red));
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
                            mDatabase.child("datas").child(dateAndTimer.curTimeArr[1]).child(dateAndTimer.curTimeArr[2]).child(listItemId[position]).child("time").setValue(timeCount[position]);
                        }
                    };
                    ((MainActivity) MainActivity.mContext).timer.scheduleAtFixedRate(timerTasks[position], 1000, 1000);
                }
                //현재 선택한 버튼이 STOP일때 즉 현재 timerTask가 돌고있을때
                else {
                    Toast.makeText(((MainActivity) MainActivity.mContext), "스케쥴 스톱", Toast.LENGTH_SHORT).show();
                    customViewHolder.startButton.setText("START");
                    customViewHolder.startButton.setBackgroundColor(ContextCompat.getColor(parent.getContext(),R.color.green));
                    timerTasks[position].cancel();
                }
            }
        });
        return convertView;
    }

    public void addItem(ItemData data) {
        m_oData.add(data);
    }
}