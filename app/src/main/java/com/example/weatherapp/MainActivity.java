package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    TextView temp, city, description, date;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        date = (TextView) findViewById(R.id.textView);
        city = (TextView) findViewById(R.id.textView2);
        description = (TextView) findViewById(R.id.textView3);
        temp = (TextView) findViewById(R.id.textView4);
    }

    private void getWeather(){
        String url = "";
    }
}
