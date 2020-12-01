package com.example.afinal;

import android.util.Log;

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

    //생성자
    public Todo() {
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    public Todo(String _name, String _month, String _day, String _estimatedTime, String _importance, String _id) {
        this.name = _name;
        this.month = _month;
        this.day = _day;
        this.estimatedTime = _estimatedTime;
        this.importance = _importance;
        this.id = _id;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("name", name);
        result.put("estimatedTime", estimatedTime);
        result.put("importance", importance);
        result.put("month", month);
        result.put("day", day);
        return result;
    }
}
