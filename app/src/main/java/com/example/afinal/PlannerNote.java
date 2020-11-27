package com.example.afinal;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;


public class PlannerNote extends AppCompatActivity {

    ListView list;
    ArrayAdapter<String> adapter;
    ArrayList<String> listItem;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        Intent intent = getIntent();
        String data = intent.getStringExtra("ID"); // 선택한 년도 월 일임. - 이걸 파일처리를 이용할것
        setTitle(data);

        listItem = new ArrayList<String>();
        listItem.add("홍길동");
        listItem.add("이순신");
        listItem.add("강감찬");
        listItem.add("조자룡");

        adapter = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_list_item_1,listItem);
        list = findViewById(R.id.list);
        list.setAdapter(adapter);


    }
}
