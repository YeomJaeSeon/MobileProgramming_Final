package com.example.afinal;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class PlannerDetailActivity extends AppCompatActivity {
    EditText inputTodo;
    EditText inputTimes;

    RadioButton radioButton, radioButton1, radioButton2;

    private DatabaseReference mDatabase;
    int month;
    int day;
    String todo;
    String times;
    String importance;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        
        mDatabase=FirebaseDatabase.getInstance().getReference();
        radioButton = (RadioButton) findViewById(R.id.radioButton);
        radioButton1 = (RadioButton) findViewById(R.id.radioButton1);
        radioButton2 = (RadioButton) findViewById(R.id.radioButton2);
        radioButton.setOnClickListener(radioButtonClickListener);
        radioButton1.setOnClickListener(radioButtonClickListener);
        radioButton2.setOnClickListener(radioButtonClickListener);

        Intent intent = getIntent();
        final String data = intent.getStringExtra("MONTHANDDAY"); // 선택한 년도 월 일임. - 이걸 파일처리를 이용할것
        String[] div = data.split("-");
        month = Integer.parseInt(div[1]);
        day = Integer.parseInt(div[2]);

        inputTodo = findViewById(R.id.editTextTextPersonName);
        inputTimes = findViewById(R.id.editTextTextPersonName2);
    }
    RadioButton.OnClickListener radioButtonClickListener = new RadioButton.OnClickListener(){
        @Override public void onClick(View view) {
            if(radioButton.isChecked()){
                importance = "상";
            }
            else if(radioButton1.isChecked()) {
                importance = "중";
            }else{
                importance = "하";
            }
        }
    };
    public void click(View v){
        if(v.getId() == R.id.add){
            todo = inputTodo.getText().toString();
            times = inputTimes.getText().toString();

            Log.i("Tag",todo+month+day+times+importance);
            writeNewTodo(todo,month,day,times,importance,3);
        }
    }
    public void writeNewTodo(String _name, int _month, int _day, String _estimatedTime, String _importance, int _id) {

        HashMap<String, Object> childUpdates = new HashMap<>();
        Todo todo = new Todo(_name,_month,_day,_estimatedTime,_importance,_id);
        Map<String, Object> todoData = todo.toMap();
        //"User_info/ID"가 존재하지 않을 시 그대로 데이터가 삽입되고,
        //"User_info/ID"가 존재할 시 해당 데이터가 변경됩니다.
        childUpdates.put("/datas/" + _id, todoData);
        mDatabase.updateChildren(childUpdates);
    }

}
