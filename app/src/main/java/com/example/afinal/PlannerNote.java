package com.example.afinal;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;


public class PlannerNote extends AppCompatActivity {

    ListView list;
    ArrayAdapter<String> adapter;
    ArrayList<String> listItem;

    Button addBtn;

    private DatabaseReference mDatabase;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        Intent intent = getIntent();
        final String data = intent.getStringExtra("ID"); // 선택한 년도 월 일임. - 이걸 파일처리를 이용할것
        setTitle(data);

        addBtn = findViewById(R.id.button);

        listItem = new ArrayList<String>();
        listItem.add("홍길동");
        listItem.add("이순신");
        listItem.add("강감찬");
        listItem.add("조자룡");

        adapter = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_list_item_1,listItem);
        list = findViewById(R.id.list);
        list.setAdapter(adapter);

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goDetail = new Intent(PlannerNote.this, PlannerDetailActivity.class);
                goDetail.putExtra("MONTHANDDAY", data);
                startActivity(goDetail);
            }
        });


    }
}
