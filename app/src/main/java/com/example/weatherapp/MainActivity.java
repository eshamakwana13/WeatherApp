package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    EditText cityView;
    String city;
    String lat, lng;
    String temp, humid, wind, precip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cityView = findViewById(R.id.address);
    }

    public void getWeather(View v) {
        city = cityView.getText().toString().replaceAll(" ", "+");
        new URLRequest().execute();
    }

    private class URLRequest extends AsyncTask<Void,Void,Void> {

        Exception ex;

        @Override
        protected Void doInBackground(Void... voids) {

            String url = "https://maps.googleapis.com/maps/api/geocode/json?address=" + city + "&key=AIzaSyBALCpGFgQeiST5YCjuLTF_za0NkQFy01Y";

            String response = request(url);

            try {
                parseGoogleResult(response);
            } catch (Exception e) {
                System.out.println(e);
            }

            System.out.println(city);
            System.out.println(response);

            url = "https://api.darksky.net/forecast/e04408a5798706aa9a220ea44bdfb784/" + lat + "," + lng;

            response = request(url);

            try {
                parseDarkSkyResult(response);
            } catch (Exception e) {
                System.out.println(e);
            }
            System.out.println(response);

            return null;
        }

        protected String request(String url) {
            try {
                URL request = new URL(url);
                HttpURLConnection con = (HttpURLConnection) request.openConnection();

                con.setRequestMethod("GET");
                con.setRequestProperty("User-Agent", "Mozilla/5.0");
                int code = con.getResponseCode();

                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                StringBuffer response = new StringBuffer();
                String line;
                while ((line = in.readLine()) != null) {
                    response.append(line);
                }

                return response.toString();
            } catch (Exception e) {
                this.ex = e;
                System.out.println(e);
            }

            return null;
        }
    }

    public void parseGoogleResult(String response) throws Exception {
        JSONObject obj = new JSONObject(response);
        JSONArray results = obj.getJSONArray("results");
        JSONObject info = results.getJSONObject(0);
        JSONObject geometry = info.getJSONObject("geometry");
        JSONObject location = geometry.getJSONObject("location");
        lat = location.getString("lat");
        lng = location.getString("lng");

        System.out.println(lat + ", " + lng);
    }

    public void parseDarkSkyResult(String response) throws Exception {
        JSONObject obj = new JSONObject(response);
        JSONObject current = obj.getJSONObject("currently");
        temp = current.getString("temperature");
        humid = current.getString("humidity");
        wind = current.getString("windSpeed");
        precip = current.getString("precipProbability");

        System.out.println(temp + ", " + humid + ", " + wind + ", " + precip);
    }
}
