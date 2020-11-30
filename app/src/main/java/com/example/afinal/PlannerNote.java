package com.example.afinal;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
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

        listItem = new ArrayList<String>();
        adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, listItem);

        list = findViewById(R.id.list);
        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                adapter.getItem(i);

            }
        });


        Intent intent = getIntent();
        final String data = intent.getStringExtra("ID"); // 선택한 년도 월 일임. - 이걸 파일처리를 이용할것

        String [] days=data.split("-");
        setTitle(data);

        Log.d("day",days[1]);
        Log.d("day",days[2]);

        //db 가져오기
        mDatabase.child("datas").child(days[1]).child(days[2]).addListenerForSingleValueEvent(
                new ValueEventListener () {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot child:dataSnapshot.getChildren()){
                            listItem.add("할일:"+child.getValue(Todo.class).name+", 걸리는 시간:"+ child.getValue(Todo.class).estimatedTime +"시간"+", 난이도:"+child.getValue(Todo.class).importance);
                            adapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e("database","Wring Url");
                    }
                });

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
    public void back(View v){
        if(v.getId() == R.id.back){
            this.finish();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        String month = data.getStringExtra("month");
        String day = data.getStringExtra("day");
        String id = data.getStringExtra("id");

        final DatabaseReference todo = mDatabase.child("datas").child(month).child(day).child(id);

        if (requestCode == GET_STRING) {
            if (resultCode == RESULT_OK) {
                //값 받았으니 view에 값 설정

                Log.d("database", "" + todo.child("name").toString());
                todo.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        listItem.add("할일:"+snapshot.getValue(Todo.class).name+", 걸리는 시간:"+ snapshot.getValue(Todo.class).estimatedTime +"시간"+", 중요도:"+snapshot.getValue(Todo.class).importance);
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
