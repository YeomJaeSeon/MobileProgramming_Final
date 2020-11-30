package com.example.afinal;

import android.content.res.AssetManager;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class Quote {


    //assets파일에서 json파일을 읽어오는 함수
    private String getJson(AssetManager assetManager) {

        String data = null;

        try {
            InputStream is = assetManager.open("quotes.json");
            int size = is.available();

            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            data = new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return data;
    }

    //읽어온 json 파일을 string파일로 변환시켜 textView에 setText하는 함수
    public void setQuote(TextView quote, TextView author,AssetManager assetManager) {
        JSONObject obj, content;
        JSONArray jsonArray;
        String quotes, title, subtitle;
        try {
            //json파일을 읽어와 JSONObject 파일로 변환
            obj = new JSONObject(getJson(assetManager));
            //변환한 JSONObject에서 quotes key를 가진 값들을  string으로 변환한 뒤 quotes에 저장
            quotes = obj.getString("quotes");
            //quotes를 JSONArray 타입으로 변환
            jsonArray = new JSONArray(quotes);

            //랜덤값을 rand에 저장
            int rand = (int) (Math.random() * jsonArray.length());
            //JSONArray에서 랜덤값이 위치한 index를 추출하여 JSONObject로 변환
            content = jsonArray.getJSONObject(rand);
            //JSONObject title에 값 저장
            title = content.getString("quote");  //title은 json파일의 quote임
            subtitle = content.getString("author"); //subtitle은 json파일의 author임
            //View에 텍스트 지정
            quote.setText(title);
            author.setText("- " + subtitle + " -");

        } catch (JSONException ex) {
            Log.e("Quote",ex.toString());
            ex.printStackTrace();
        }
    }

}
