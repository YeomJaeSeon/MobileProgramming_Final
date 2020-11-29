package com.example.afinal;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class PlannerNote extends AppCompatActivity {

    final int GET_STRING = 1;
    ListView list;
    ArrayAdapter<String> adapter;
    ArrayList<String> listItem;
    Button addBtn;

    private DatabaseReference mDatabase;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);


        mDatabase = FirebaseDatabase.getInstance().getReference();
        list = findViewById(R.id.list);
        listItem = new ArrayList<String>();

        Intent intent = getIntent();

        final String data = intent.getStringExtra("ID"); // 선택한 년도 월 일임. - 이걸 파일처리를 이용할것
        setTitle(data);

        addBtn = findViewById(R.id.button);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goDetail = new Intent(PlannerNote.this, PlannerDetailActivity.class);
                goDetail.putExtra("MONTHANDDAY", data);
                startActivityForResult(goDetail, GET_STRING);
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        String month = data.getStringExtra("month");
        String day = data.getStringExtra("day");
        String id = data.getStringExtra("id");


        DatabaseReference todo = mDatabase.child("datas").child(month).child(day).child(id);

        if (requestCode == GET_STRING) {
            if (resultCode == RESULT_OK) {
                //값 받았으니 view에 값 설정

                Log.d("database", "" + todo.child("name").toString());
                todo.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {

                        listItem.add("할일:"+snapshot.getValue(Todo.class).name);
                        listItem.add("걸리는 시간:"+String.valueOf(snapshot.getValue(Todo.class).estimatedTime)+"시간");
                        listItem.add("난이도:"+snapshot.getValue(Todo.class).importance);

                        //어뎁터 세팅
                        adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, listItem);

                        list.setAdapter(adapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                listItem = new ArrayList<String>();
                adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, listItem);

                list = findViewById(R.id.list);
                list.setAdapter(adapter);
            } else if (resultCode == RESULT_CANCELED) {
                Log.e("Intent", "값을 받지 못함");
            }
        } else {
            Log.e("Intent", "Request코드 에러");
        }
    }
}
