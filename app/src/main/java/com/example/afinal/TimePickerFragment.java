package com.example.afinal;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {
    private AlarmManager mAlarmManager;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mAlarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE); // 알람 매니저 객체에 알람 서비스 설정

        Calendar calendar = Calendar.getInstance(); // 캘린더 객체 생성
        int hour = calendar.get(Calendar.HOUR_OF_DAY); // 현재 시간을 가져온다.
        int minute = calendar.get(Calendar.MINUTE); // 현재 분을 가져온다.

        return new TimePickerDialog(getContext(), this, hour, minute, DateFormat.is24HourFormat(getContext())); // 기기에 설정된 시간을 가져온다.
    }

    @Override
    public void onTimeSet(TimePicker View, int hourOfDay, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay); // 사용자가 설정한 시간으로 덮어씀
        calendar.set(Calendar.MINUTE, minute);

        Intent intent = new Intent(getContext(), MainActivity.class);
        PendingIntent operation = PendingIntent.getActivity(getContext(), 0, intent, 0); // pending intent 선언
        mAlarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), operation); // pending intent로 동작
    }
}
