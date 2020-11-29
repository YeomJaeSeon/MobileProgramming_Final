package com.example.afinal;

import android.util.Log;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class Todo {
    public String name;
    public int estimatedTime;
    public String importance;
    public int month;
    public int day;
    public int id;
    //생성자
    public Todo(String _name,int _month, int _day,int _estimatedTime, String _importance,int _id){
        this.name=_name;
        this.month = _month;
        this.day = _day;
        this.estimatedTime=_estimatedTime;
        this.importance=_importance;
        this.id=_id;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("name", name);
        result.put("estimatedTime", estimatedTime);
        result.put("importance", importance);
        result.put("month",month);
        result.put("day",day);
        return result;
    }
}