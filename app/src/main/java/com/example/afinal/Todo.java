package com.example.afinal;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class Todo {
    private DatabaseReference mDatabase;
    public String name;
    public String estimatedTime;
    public String importance;
    public String month;
    public String day;
    public String id;
    public double time;
    public boolean flag;

    //생성자
    public Todo() {
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    public Todo(String _name, String _month, String _day, String _estimatedTime, String _importance, String _id, double _time) {
        this.name = _name;
        this.month = _month;
        this.day = _day;
        this.estimatedTime = _estimatedTime;
        this.importance = _importance;
        this.id = _id;
        this.time = _time;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("name", name);
        result.put("estimatedTime", estimatedTime);
        result.put("importance", importance);
        result.put("month", month);
        result.put("id", id);
        result.put("day", day);
        result.put("time", time);
        result.put("flag", flag);
        return result;
    }
}
