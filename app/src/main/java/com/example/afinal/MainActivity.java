package com.example.afinal;

import androidx.appcompat.app.AppCompatActivity;

        import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    public void click(View v){
        int btnId = v.getId();

        switch(btnId){
            case R.id.status:
                break;
            case R.id.home:
                break;
            case R.id.planner:
                break;
        }
    }
}