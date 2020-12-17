package com.example.afinal;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class PlannerNote extends AppCompatActivity {

    final int GET_STRING = 1;
    final int GET_STRING2 = 2;
    ListView list;
    ArrayAdapter<String> adapter;
    ArrayList<String> listItem;
    Button addBtn, backBtn;
    String[] days;
    String data;
    String[] listItemId = new String[5];
    ProgressDialog progressDialog;
    AlertDialog alertDialog;
    AlertDialog.Builder builder;
    int count;
    private DatabaseReference mDatabase;


    //컨텍스트
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("수정 or 삭제");
        menu.add(0, 1, 0, "수정");
        menu.add(0, 2, 0, "삭제");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo itemId = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            //수정
            case 1:
                Intent goDetail = new Intent(PlannerNote.this, PlannerDetailActivity.class);
                goDetail.putExtra("id", listItemId[itemId.position]);
                goDetail.putExtra("month", days[1]);
                goDetail.putExtra("day", days[2]);
                goDetail.putExtra("MONTHANDDAY", data);
                startActivity(goDetail);

                return true;
            //삭제
            case 2:
                adapter.remove(adapter.getItem(itemId.position));
                removeTodo(days[1], days[2], listItemId[itemId.position]);
                adapter.notifyDataSetChanged();
                return true;
            case 3:
                TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                        Toast.makeText(PlannerNote.this, hour + "시" + minute + "분", Toast.LENGTH_SHORT).show();
                    }
                }, 0, 0, true); // 타임피커 객체 생성
                timePickerDialog.show();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        listItem = new ArrayList<String>();
        adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, listItem);

        list = findViewById(R.id.list);
        list.setAdapter(adapter);
        progressDialog = new ProgressDialog(this);

        //AlertDialog 추가
        builder = new AlertDialog.Builder(this);
        builder.setTitle("Todo 추가에러").setMessage("\n\n     Todo는 5개까지 만들 수 있습니다.\n     Todo를 삭제후 진행해 주세요\n");
        //컨텍스트 메뉴 등록
        registerForContextMenu(list);
        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d("listLongClicked", String.valueOf(i));
                return false;
            }
        });

        Intent intent = getIntent();
        data = intent.getStringExtra("ID"); // 선택한 년도 월 일임. - 이걸 파일처리를 이용할것
        days = data.split("-");
        setTitle(data);

        addBtn = findViewById(R.id.button);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (count < 5) {
                    Intent goDetail = new Intent(PlannerNote.this, PlannerDetailActivity.class);
                    goDetail.putExtra("MONTHANDDAY", data);
                    startActivity(goDetail);
                } else {
                    alertDialog = builder.create();
                    alertDialog.show();
                }
            }
        });
        backBtn = findViewById(R.id.back);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        //listItem 초기화
        int i = 0;
        for (String id : listItemId) {
            listItemId[i++] = "";
        }
        listItem.clear();

        progressDialog.setMessage("로딩중입니다...");
        progressDialog.show();
        //db 가져오기
        mDatabase.child("datas").child(days[1]).child(days[2]).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        int i = 0;
                        count = (int) dataSnapshot.getChildrenCount();
                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                            listItem.add("할일:" + child.getValue(Todo.class).name + ", 걸리는 시간:" + child.getValue(Todo.class).estimatedTime + "시간" + ", 중요도:" + child.getValue(Todo.class).importance);
                            listItemId[i++] = child.getKey();
                        }
                        adapter.notifyDataSetChanged();
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e("database", "Wrong Url");
                        progressDialog.dismiss();
                    }
                });
    }

    public void removeTodo(String month, String day, String id) {
        mDatabase.child("datas").child(month).child(day).child(id).removeValue();
        count = count - 1;
    }

}
