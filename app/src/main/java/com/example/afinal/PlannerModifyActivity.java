package com.example.afinal;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.RadioButton;

import androidx.appcompat.app.AppCompatActivity;

public class PlannerModifyActivity extends AppCompatActivity {
    EditText inputTodo;
    EditText inputTimes;


    RadioButton radioButton, radioButton1, radioButton2;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plannermodifier);

        Intent intent = getIntent();
        String result = intent.getStringExtra("id");

        inputTodo = findViewById(R.id.editTextTextPersonName);
        inputTimes = findViewById(R.id.editTextEstimatedTime);

        radioButton = findViewById(R.id.radioButton);
        radioButton1 = findViewById(R.id.radioButton1);
        radioButton2 = findViewById(R.id.radioButton2);

        Log.i("whereis you", result);

    }
}
