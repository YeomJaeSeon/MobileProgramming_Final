package com.example.afinal;

import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

import static android.widget.Toast.LENGTH_LONG;

public class MainActivity extends AppCompatActivity {

    TextView quote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        quote = findViewById(R.id.quotes);
        try {
            JSONObject obj = new JSONObject(getJson());
            String quotes = obj.getString("quotes");
            JSONArray jsonArray = new JSONArray(quotes);

            int rand = (int) (Math.random() * jsonArray.length());
            JSONObject content = jsonArray.getJSONObject(rand);
            String title = content.getString("quote");  //title은 json파일의 quote임
            quote.setText(title);


        } catch (JSONException ex) {
            ex.printStackTrace();
        }
    }

    private String getJson() {
        String data = null;
        AssetManager assetManager = getAssets();
        try {
            InputStream is = assetManager.open("quotes.json");
            int size = is.available();

            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            data = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return data;
    }


    public void click(View v) {
        Intent intent_S = new Intent(MainActivity.this, StatisticsActivity.class);
        Intent intent_P = new Intent(MainActivity.this, PlannerActivity.class);
        int btnId = v.getId();

        switch (btnId) {
            case R.id.status:
                startActivity(intent_S);
                break;
            case R.id.home:
                break;
            case R.id.planner:
                startActivity(intent_P);
                break;
        }
    }
}