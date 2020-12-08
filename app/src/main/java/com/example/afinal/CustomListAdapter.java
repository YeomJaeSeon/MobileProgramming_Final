package com.example.afinal;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

//커스텀리스트뷰 시작 재선

class ItemData {
    String todo;
    String times;
    String importance;
}

public class CustomListAdapter extends BaseAdapter {


    LayoutInflater inflater = null;
    private ArrayList<ItemData> m_oData = null;
    private int nListCnt = 0;
    boolean timerStarted = false;

    public CustomListAdapter(ArrayList<ItemData> _oData) {
        m_oData = _oData;
        nListCnt = m_oData.size();
    }


    @Override
    public int getCount() {
        return nListCnt;
    }

    @Override
    public Object getItem(int position) {return null;}

    @Override
    public long getItemId(int position) { return 0;}

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            final Context context = parent.getContext();
            if (inflater == null) {
                inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            }
            convertView = inflater.inflate(R.layout.main_listview, parent, false);
        }

        TextView todo = convertView.findViewById(R.id.todo);
        TextView time = convertView.findViewById(R.id.time);
        TextView importance = convertView.findViewById(R.id.importance);

        final Button startButton = convertView.findViewById(R.id.startstopbutton);


        //리스트뷰의 타이머 클릭했을때 타이머시작 & stop 구현
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (timerStarted == false) {
                    timerStarted = true;
                    startButton.setText("STOP");
                    ((MainActivity)MainActivity.mContext).startTimer(); // startTimer() 함수 호출
                } else {
                    timerStarted = false;
                    startButton.setText("START");
                    ((MainActivity)MainActivity.mContext).timerTask.cancel();
                    //timerTask.cancel(); // timerTask 중단
                }
            }
        });

        todo.setText(m_oData.get(position).todo);
        time.setText(m_oData.get(position).times);
        importance.setText(m_oData.get(position).importance);
        return convertView;
    }}

//커스텀 리스트뷰 끝
