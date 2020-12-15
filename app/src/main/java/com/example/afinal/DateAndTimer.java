package com.example.afinal;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateAndTimer {
    //Dates
    public Date date = new Date(System.currentTimeMillis());
    public SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
    public String[] curTimeArr;
    public String curTime;

    public DateAndTimer() {
        curTime = format1.format(date);
        curTimeArr = curTime.split("-");
    }

    // 초 단위로 되어있는 시간 값을 (시간 : 분 : 초) 형식으로 만드는 메소드
    public String getTimerText(double time) {
        int rounded = (int) Math.round(time); // double형인 변수 time을 int형으로 형변환
        int seconds = ((rounded % 86400) % 3600) % 60; // 초
        int minutes = ((rounded % 86400) % 3600) / 60; // 분
        int hours = ((rounded % 86400) / 3600); // 시간

        return formatTime(seconds, minutes, hours);
    }

    public String formatTime(int seconds, int minutes, int hours) {
        return String.format("%02d", hours) + " : " + String.format("%02d", minutes) + " : " + String.format("%02d", seconds);
    }

}
