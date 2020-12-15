package com.example.afinal;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class PlannerDetailActivity extends AppCompatActivity {
    EditText inputTodo;
    EditText inputTimes;

    //입력데이터를 받을 intent
    Intent intent = new Intent();

    RadioButton radioButton, radioButton1, radioButton2;

    private DatabaseReference mDatabase;

    String month;
    String day;
    String todo;
    String estimatedTime;
    String importance = "";
    String id = null;
    String data;
    Button addBtn;
    ProgressDialog progressDialog;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        radioButton = findViewById(R.id.radioButton);
        radioButton1 = findViewById(R.id.radioButton1);
        radioButton2 = findViewById(R.id.radioButton2);
        radioButton.setOnClickListener(radioButtonClickListener);
        radioButton1.setOnClickListener(radioButtonClickListener);
        radioButton2.setOnClickListener(radioButtonClickListener);

        inputTodo = findViewById(R.id.editTextTextPersonName);
        inputTimes = findViewById(R.id.editTextEstimatedTime);
        progressDialog = new ProgressDialog(this);
        Intent intent = getIntent();

        progressDialog.setMessage("로딩중입니다...");
        progressDialog.show();
        //DB접근 될때
        if (intent.getStringExtra("id") != null) {
            id = intent.getStringExtra("id");
            month = intent.getStringExtra("month");
            day = intent.getStringExtra("day");
            addBtn = findViewById(R.id.add);
            addBtn.setText("수정");
        }

        //수정을 클릭하여 id가 있으면 id를 새로 만들지 않는다.
        if (id == null) {
            id = String.valueOf(new Date().getTime());
        }
        data = intent.getStringExtra("MONTHANDDAY"); // 선택한 년도 월 일임. - 이걸 파일처리를 이용할것
        String[] div = data.split("-");
        month = (div[1]);
        day = (div[2]);

        //intent에 Id정보가 담겨오면 수정버튼을 클릭한것임으로 id정보가 있으면 수정화면으로 변환
        if (intent.getStringExtra("id") != null) {
            id = intent.getStringExtra("id");
            //db 가져오기
            Log.d("DATE", month + " " + day);
            mDatabase.child("datas").child(month).child(day).addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            int i = 0;
                            Log.d("ACTID", id);
                            for (DataSnapshot child : dataSnapshot.getChildren()) {
                                if (id.equals(child.getKey())) {
                                    inputTodo.setText(child.getValue(Todo.class).name);
                                    inputTimes.setText(child.getValue(Todo.class).estimatedTime);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.e("database", "Wring Url");
                        }
                    });
        }
        progressDialog.dismiss();
    }

    RadioButton.OnClickListener radioButtonClickListener = new RadioButton.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (radioButton.isChecked()) {
                importance = "상";
            } else if (radioButton1.isChecked()) {
                importance = "중";
            } else {
                importance = "하";
            }
        }
    };

    public void click(View v) {
        if (v.getId() == R.id.add) {
            todo = inputTodo.getText().toString();
            String toTime = inputTimes.getText().toString();
            try {
                estimatedTime = (toTime);
            } catch (NumberFormatException ex) {
                System.out.println("not a number" + ex);
            }
            Log.i("Tag", todo + month + day + estimatedTime + importance);
            if (todo.getBytes().length <= 0) {
                Toast.makeText(this, "할일 입력해주세요..", Toast.LENGTH_SHORT).show();
                return;
            } else if (inputTimes.getText().toString().getBytes().length <= 0) {
                Toast.makeText(this, "예상 시간 입력해주세요..", Toast.LENGTH_SHORT).show();
                return;
            } else if (importance.getBytes().length <= 0) {
                Toast.makeText(this, "중요도 입력해주세요..", Toast.LENGTH_SHORT).show();
                return;
            }

            progressDialog.show();
            writeNewTodo(todo, month, day, estimatedTime, importance, id, 0);
            progressDialog.dismiss();
            //setResult(RESULT_OK, intent);
            this.finish();
        } else if (v.getId() == R.id.goback) {
            //setResult(RESULT_CANCELED, intent);
            this.finish();
        }
    }

    public void writeNewTodo(String _name, String _month, String _day, String _estimatedTime, String _importance, String _id, double _time) {
        HashMap<String, Object> childUpdates = new HashMap<>();
        Todo todo = new Todo(_name, _month, _day, _estimatedTime, _importance, _id, _time);
        Map<String, Object> todoData = todo.toMap();

        //Database에 들어갈 경로 설정정
        childUpdates.put("/datas/" + _month + "/" + _day + "/" + _id, todoData);
        mDatabase.updateChildren(childUpdates).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("Database", "데이터 저장 완료");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("Database", "데이터 저장 실패");
            }
        });
    }

}
