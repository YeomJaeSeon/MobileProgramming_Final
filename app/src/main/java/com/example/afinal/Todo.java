package com.example.afinal;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class Todo {
    public String name;
    public String estimatedTime;
    public int importance;

    //생성자
    public Todo(String _name,String _estimatedTime, int _importance){
        this.name=_name;
        this.estimatedTime=_estimatedTime;
        this.importance=_importance;
    }
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("name", name);
        result.put("estimatedTime", estimatedTime);
        result.put("importance", importance);

        return result;
    }
}
