package com.example.afinal;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class PlannerNote extends AppCompatActivity {

    final int GET_STRING = 1;
    ListView list;
    ArrayAdapter<String> adapter;
    ArrayList<String> listItem;
    Button addBtn;
    Button backBtn;

    private DatabaseReference mDatabase;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        Intent intent = getIntent();

        mDatabase = FirebaseDatabase.getInstance().getReference();

        listItem = new ArrayList<String>();
        adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, listItem);
        list = findViewById(R.id.list);
        list.setAdapter(adapter);
        //list.setOnItemClickListener();    //listItem 클릭시 실행되는 함수 MoreInfo로 가야함

        addBtn = findViewById(R.id.button);

        final String data = intent.getStringExtra("ID"); // 선택한 년도 월 일임.
        String [] days=data.split("-");     // "-" 로 년 월 일 구분
        setTitle(data);

        Log.d("day",days[1]);
        Log.d("day",days[2]);

        //DB경로에 있는 모든 자식들 가져오기
        mDatabase.child("datas").child(days[1]).child(days[2]).addListenerForSingleValueEvent(
                new ValueEventListener () {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.getChildren()!=null){
                        for(DataSnapshot child:dataSnapshot.getChildren()) {
                            listItem.add("할일:" + child.getValue(Todo.class).name + ", 걸리는 시간:" + child.getValue(Todo.class).estimatedTime + "시간" + ", 난이도:" + child.getValue(Todo.class).importance);
                            adapter.notifyDataSetChanged();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e("database","Wring Url");
                    }
                });


        //날짜 클릭 이벤트
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goDetail = new Intent(PlannerNote.this, PlannerDetailActivity.class);
                goDetail.putExtra("MONTHANDDAY", data);
                startActivityForResult(goDetail, GET_STRING);
            }
        });

        backBtn = findViewById(R.id.back);
        backBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                finish();
            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //PlannerDetail Activity로 부터 id,month,day 값 가져옴
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
                        listItem.add("할일:"+snapshot.getValue(Todo.class).name+", 걸리는 시간:"+ snapshot.getValue(Todo.class).estimatedTime +"시간"+", 난이도:"+snapshot.getValue(Todo.class).importance);
                        adapter.notifyDataSetChanged();
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            } else if (resultCode == RESULT_CANCELED) {
                Log.e("Intent", "값을 받지 못함");
            }
        } else {
            Log.e("Intent", "Request코드 에러");
        }
    }
}
