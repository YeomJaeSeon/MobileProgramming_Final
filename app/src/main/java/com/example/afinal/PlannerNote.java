package com.example.afinal;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class PlannerNote extends AppCompatActivity {

    static final int GET_STRING = 1;
    static final int GET_NUMBER = 2;

    EditText edit = findViewById(R.id.edit);

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        Intent intent = getIntent();
        String data = intent.getStringExtra("INPUT_TEXT");

        edit.setText(data);
    }

}
